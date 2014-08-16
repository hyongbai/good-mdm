package com.example.samsungmdm;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.app.enterprise.EnterpriseDeviceManager;
import android.app.enterprise.RestrictionPolicy;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class MainActivity extends Activity implements ServerResponseInterface {

    public static final String TAG = "DEBUG";
    private static final int POLL_FREQUENCY = 10; // in seconds

    public int REQUEST_ENABLE;
    private Button mRegisterDeviceButton;
    private Button mGetNextCommandButton;
    private Button mActivateLicenseButton;
    private Switch mBluetoothSwitch;
    private Switch mWifiSwitch;
    private LinearLayout mServerResponseLinearLayout;
    private Communication mServerCommunication;
    private Runnable mPollServer;
    private EnterpriseDeviceManager mEnterpriseDeviceManager;
    private RestrictionPolicy mRestrictionPolicy;
    private DevicePolicyManager mDPM;
    private ComponentName mAdminName;
    private android.os.Handler mHandler; //Continuous Polling

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEnterpriseDeviceManager = (EnterpriseDeviceManager) getSystemService(EnterpriseDeviceManager.ENTERPRISE_POLICY_SERVICE);
        mRestrictionPolicy = mEnterpriseDeviceManager.getRestrictionPolicy();

        findViewsInActivity();
        grantAdminPrivileges();
        setViewListeners();
        updateSwitchesBasedOnStatus();

        //Init Server Communication
        mServerCommunication = new Communication(this, getBaseContext());
        mServerCommunication.registerDevice();
        mHandler = new android.os.Handler();

        mPollServer = new Runnable() {
            @Override
            public void run() {
                try {
                    mServerCommunication.getNextCommand();
                    mHandler.postDelayed(this, 1000 * POLL_FREQUENCY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        //Start Polling
        mHandler.postDelayed(mPollServer, 1000);

    }

    private void setViewListeners() {
        mRegisterDeviceButton.setOnClickListener(buttonClickListeners);
        mGetNextCommandButton.setOnClickListener(buttonClickListeners);
        mActivateLicenseButton.setOnClickListener(buttonClickListeners);
        mBluetoothSwitch.setOnCheckedChangeListener(switchCheckedListeners);
        mWifiSwitch.setOnCheckedChangeListener(switchCheckedListeners);
    }

    private void grantAdminPrivileges() {
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mAdminName = new ComponentName(this, MyDeviceAdminReceiver.class);

        if (!mDPM.isAdminActive(mAdminName)) {
            //Not yet device admin
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "This needs to be added");
            startActivityForResult(intent, REQUEST_ENABLE);
        }
    }

    private void findViewsInActivity() {

        //Switches
        mBluetoothSwitch = (Switch) findViewById(R.id.bluetoothSwitch);
        mWifiSwitch = (Switch) findViewById(R.id.wifiSwitch);

        //Buttons
        mRegisterDeviceButton = (Button) findViewById(R.id.registerDeviceButton);
        mActivateLicenseButton = (Button) findViewById(R.id.activateLicenseButton);
        mGetNextCommandButton = (Button) findViewById(R.id.getNextCommandButton);

        //Others
        mServerResponseLinearLayout = (LinearLayout) findViewById(R.id.serverResponseLinearLayout);

    }

    private CompoundButton.OnCheckedChangeListener switchCheckedListeners = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            switch (buttonView.getId()) {
                case R.id.bluetoothSwitch:
                    Bluetooth bluetooth = new Bluetooth(mRestrictionPolicy);
                    if (isChecked && !mRestrictionPolicy.isBluetoothEnabled(false)) {
                        bluetooth.enableBluetooth();
                    } else if (!isChecked && mRestrictionPolicy.isBluetoothEnabled(false)) {
                        bluetooth.disableBluetooth();
                    }
                    break;
                case R.id.wifiSwitch:

                    break;
            }
        }
    };

    private View.OnClickListener buttonClickListeners = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.activateLicenseButton:
                    ActivateLicense activateLicense = new ActivateLicense();
                    activateLicense.applyInitialLicenses(MainActivity.this);
                    break;
                case R.id.registerDeviceButton:
                    mServerCommunication.registerDevice();
                    break;
                case R.id.getNextCommandButton:
                    mServerCommunication.getNextCommand();
                    break;
            }
        }
    };

    private void parseServerResponse(JSONObject response) { // response should be a valid JSON

        //TODO:VALIDATE SERVER RESPONSE
        Log.d(TAG, "Parsing Response...");
        Log.d(TAG, response.toString());
        boolean command_exists; //Assume no command exists
        JSONObject test_case;
        JSONArray steps;

        try {
            command_exists = Boolean.valueOf(response.getBoolean("command_exists"));
            test_case = response.getJSONObject("test_case");
            steps = test_case.getJSONArray("steps");
            Log.d(TAG, "Successfully Parsed Response...");
        } catch (JSONException e) {
            command_exists = false;
            test_case = null;
            steps = null;
            Log.d(TAG, "JSON parse exception");
        }

        try {
            if (command_exists && test_case != null && steps != null) {
                if (steps.get(0).equals("Enable Bluetooth")) {
                    Bluetooth bluetooth = new Bluetooth(mRestrictionPolicy);
                    bluetooth.enableBluetooth();
                    updateSwitchesBasedOnStatus();
                } else if (steps.get(0).equals("Disable Bluetooth")) {
                    Bluetooth bluetooth = new Bluetooth(mRestrictionPolicy);
                    bluetooth.disableBluetooth();
                    updateSwitchesBasedOnStatus();
                } else {
                    Log.e(TAG, "Step: " + steps.get(0) + " is an unknown command");
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON parse exception");
            e.printStackTrace();
        }


    }

    private void updateSwitchesBasedOnStatus() {

        Bluetooth bluetooth = new Bluetooth(mRestrictionPolicy);
        if (!mBluetoothSwitch.isChecked() && bluetooth.isEnabled()) {
            mBluetoothSwitch.setChecked(true);
        } else if (mBluetoothSwitch.isChecked() && !bluetooth.isEnabled()) {
            mBluetoothSwitch.setChecked(false);
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (REQUEST_ENABLE == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                //Has become the admin
                Toast.makeText(getBaseContext(), "Admin Rights Granted", Toast.LENGTH_SHORT).show();
            } else {
                //failed to become the admin
                Toast.makeText(getBaseContext(), "Admin Rights Denied", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Request code is: " + requestCode + ", Result OK is: " + Activity.RESULT_OK);
            }
        }
    }

    public void onServerResponse(String response) {
        TextView serverResponseTextView = new TextView(this);
        serverResponseTextView.setHorizontallyScrolling(true);
        serverResponseTextView.setSingleLine();
        serverResponseTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        serverResponseTextView.setFocusableInTouchMode(true);
        serverResponseTextView.setMarqueeRepeatLimit(1);
        serverResponseTextView.setFocusable(true);
        String myTime = java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
        serverResponseTextView.setText(myTime + " - " + response);

        mServerResponseLinearLayout.addView(serverResponseTextView, 0);

        //Parsing server response
        try {
            parseServerResponse(new JSONObject(response));
        } catch (JSONException e) {
            Log.e(TAG, "Invalid JSON response from the server");
            e.printStackTrace();
        }
    }


}
