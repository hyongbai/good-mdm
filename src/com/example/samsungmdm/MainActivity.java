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

public class MainActivity extends Activity implements ServerResponseInterface{

    public static final String TAG = "DEBUG";

    private static final int POLL_FREQUENCY = 10; // in seconds
    private Button registerDeviceButton;
    private Button getNextCommandButton;
    private Button activateLicenseButton;
    private Switch bluetoothSwitch;
    private Switch wifiSwitch;
    private LinearLayout serverResponseLinearLayout;

    private Communication mServerCommunication;

    //Continuous Polling
    final android.os.Handler handler = new android.os.Handler();
    Runnable pollServer;

    private EnterpriseDeviceManager enterpriseDeviceManager;
    private RestrictionPolicy restrictionPolicy;

	int REQUEST_ENABLE;

	DevicePolicyManager mDPM;
	ComponentName mAdminName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enterpriseDeviceManager = (EnterpriseDeviceManager) getSystemService(EnterpriseDeviceManager.ENTERPRISE_POLICY_SERVICE);
        restrictionPolicy = enterpriseDeviceManager.getRestrictionPolicy();

        findViewsInActivity();
        grantAdminPrivileges();
        setViewListeners();
        updateSwitchesBasedOnStatus();

        //Init Server Communication
        mServerCommunication = new Communication(this, getBaseContext());
        mServerCommunication.registerDevice();

        pollServer = new Runnable() {
            @Override
            public void run() {
                try{
                    mServerCommunication.getNextCommand();
                    handler.postDelayed(this, 1000 * POLL_FREQUENCY);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };

        //Start Polling
        handler.postDelayed(pollServer, 1000);

    }

    private void setViewListeners(){
        registerDeviceButton.setOnClickListener(buttonClickListeners);
        getNextCommandButton.setOnClickListener(buttonClickListeners);
        activateLicenseButton.setOnClickListener(buttonClickListeners);
        bluetoothSwitch.setOnCheckedChangeListener(switchCheckedListeners);
        wifiSwitch.setOnCheckedChangeListener(switchCheckedListeners);
    }

            private void grantAdminPrivileges(){
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

        private void findViewsInActivity(){

            //Switches
            bluetoothSwitch = (Switch) findViewById(R.id.bluetoothSwitch);
            wifiSwitch = (Switch) findViewById(R.id.wifiSwitch);

            //Buttons
            registerDeviceButton = (Button) findViewById(R.id.registerDeviceButton);
            activateLicenseButton = (Button) findViewById(R.id.activateLicenseButton);
            getNextCommandButton = (Button) findViewById(R.id.getNextCommandButton);

            //Others
            serverResponseLinearLayout = (LinearLayout) findViewById(R.id.serverResponseLinearLayout);

        }

        private CompoundButton.OnCheckedChangeListener switchCheckedListeners = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                switch (buttonView.getId()){
                    case R.id.bluetoothSwitch:
                        Bluetooth bluetooth = new Bluetooth(restrictionPolicy);
                        if (isChecked && !restrictionPolicy.isBluetoothEnabled(false)){
                            bluetooth.enableBluetooth();
                        }else if (!isChecked && restrictionPolicy.isBluetoothEnabled(false)){
                            bluetooth.disableBluetooth();
                        }
                        break;
                    case R.id.wifiSwitch:

                        break;
                }
            }
        };

        private View.OnClickListener buttonClickListeners = new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            switch(v.getId()){
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

        try{
            command_exists = Boolean.valueOf(response.getBoolean("command_exists"));
            test_case = response.getJSONObject("test_case");
            steps = test_case.getJSONArray("steps");
            Log.d(TAG, "Successfully Parsed Response...");
        }catch(JSONException e){
            command_exists = false;
            test_case = null;
            steps = null;
            Log.d(TAG, "JSON parse exception");
        }

        try{
            if (command_exists && test_case != null && steps != null){
                if (steps.get(0).equals("Enable Bluetooth")){
                    Bluetooth bluetooth = new Bluetooth(restrictionPolicy);
                    bluetooth.enableBluetooth();
                    updateSwitchesBasedOnStatus();
                }else if (steps.get(0).equals("Disable Bluetooth")){
                    Bluetooth bluetooth = new Bluetooth(restrictionPolicy);
                    bluetooth.disableBluetooth();
                    updateSwitchesBasedOnStatus();
                }else{
                    Log.e(TAG,"Step: " + steps.get(0) + " is an unknown command");
                }
            }
        }catch (JSONException e){
            Log.e(TAG, "JSON parse exception");
            e.printStackTrace();
        }



    }

    private void updateSwitchesBasedOnStatus(){

        Bluetooth bluetooth = new Bluetooth(restrictionPolicy);
        if (!bluetoothSwitch.isChecked() && bluetooth.isEnabled()){
            bluetoothSwitch.setChecked(true);
        }else if (bluetoothSwitch.isChecked() && !bluetooth.isEnabled()){
            bluetoothSwitch.setChecked(false);
        }

    }

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if(REQUEST_ENABLE == requestCode){
			if(resultCode==Activity.RESULT_OK){
				//Has become the admin
				Toast.makeText(getBaseContext(),"Admin Rights Granted",Toast.LENGTH_SHORT).show();
			}else{
				//failed to become the admin
				Toast.makeText(getBaseContext(),"Admin Rights Denied",Toast.LENGTH_SHORT).show();
				Log.d(TAG, "Request code is: " + requestCode + ", Result OK is: " + Activity.RESULT_OK);
			}
		}
	}

    public void onServerResponse(String response){
        TextView serverResponseTextView = new TextView(this);
        serverResponseTextView.setHorizontallyScrolling(true);
        serverResponseTextView.setSingleLine();
        serverResponseTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        serverResponseTextView.setFocusableInTouchMode(true);
        serverResponseTextView.setMarqueeRepeatLimit(1);
        serverResponseTextView.setFocusable(true);
        String myTime = java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
        serverResponseTextView.setText(myTime + " - " + response);

        serverResponseLinearLayout.addView(serverResponseTextView, 0);

        //Parsing server response
        try{
            parseServerResponse(new JSONObject(response));
        }catch (JSONException e){
            Log.e(TAG, "Invalid JSON response from the server");
            e.printStackTrace();
        }
    }


}
