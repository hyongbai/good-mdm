package com.example.samsungmdm;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Switch wifiSwitch;
	private Switch bluetoothSwitch;
    int REQUEST_ENABLE;

    DevicePolicyManager mDPM;
    ComponentName mAdminName;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		wifiSwitch = (Switch) findViewById(R.id.wifiSwitch);
		bluetoothSwitch = (Switch) findViewById(R.id.bluetoothSwitch);

        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mAdminName = new ComponentName(this, MyDeviceAdminReceiver.class);

        if (!mDPM.isAdminActive(mAdminName)){
            //Not yet device admin
            Intent intent = new Intent (DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"This needs to be added");
            startActivityForResult(intent,REQUEST_ENABLE);
        }else{
            //Already a device admin
            mDPM.lockNow();
        }

		wifiSwitch.setOnCheckedChangeListener(switchListeners);
		bluetoothSwitch.setOnCheckedChangeListener(switchListeners);

	}

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(REQUEST_ENABLE == requestCode){
            if(resultCode==Activity.RESULT_OK){
                //Has become the admin
                Toast.makeText(getBaseContext(),"BECAME THE ADMIN",Toast.LENGTH_SHORT).show();
            }else{
                //failed to become the admin
                Toast.makeText(getBaseContext(),"FAILED TO BECOME THE ADMIN",Toast.LENGTH_SHORT).show();
                Log.e("ADMINISTRATION", "Request code is: " + requestCode + ", Result OK is: " + Activity.RESULT_OK);
            }
        }
    }

    private OnCheckedChangeListener switchListeners = new OnCheckedChangeListener(){

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {



        }
    };
}
