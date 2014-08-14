package com.example.samsungmdm;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.app.enterprise.EnterpriseDeviceManager;
import android.app.enterprise.RestrictionPolicy;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends Activity {

    public static final String TAG = "DEBUG";

    private Button registerDeviceButton;
    private Button getNextCommandButton;
    private Switch bluetoothSwitch;
    private Switch wifiSwitch;

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

    }

    private void setViewListeners(){
        registerDeviceButton.setOnClickListener(buttonClickListeners);
        getNextCommandButton.setOnClickListener(buttonClickListeners);
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
        getNextCommandButton = (Button) findViewById(R.id.getNextCommandButton);

    }

    private CompoundButton.OnCheckedChangeListener switchCheckedListeners = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            switch (buttonView.getId()){
                    case R.id.bluetoothSwitch:
                        if (isChecked && !restrictionPolicy.isBluetoothEnabled(false)){
                        restrictionPolicy.setBluetoothState(true);
                    }else if (!isChecked && restrictionPolicy.isBluetoothEnabled(false)){
                        restrictionPolicy.setBluetoothState(false);
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
            Communication sin = new Communication(getBaseContext());
            switch(v.getId()){
                case R.id.registerDeviceButton:
//                    ActivateLicense activateLicense = new ActivateLicense();
//                    activateLicense.applyInitialLicenses(MainActivity.this);
                    sin.registerDevice();
                    break;
                case R.id.getNextCommandButton:
                    sin.getNextCommand();
                    break;
            }
        }
    };

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


}
