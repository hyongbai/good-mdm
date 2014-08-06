package com.example.samsungmdm;

import android.app.Activity;
import android.app.enterprise.EnterpriseDeviceManager;
import android.app.enterprise.RestrictionPolicy;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public class MainActivity extends Activity {

	private Switch wifiSwitch;
	private Switch bluetoothSwitch;
	
	private EnterpriseDeviceManager edm;
	private RestrictionPolicy restrictionPolicy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		wifiSwitch = (Switch) findViewById(R.id.wifiSwitch);
		bluetoothSwitch = (Switch) findViewById(R.id.bluetoothSwitch);

		wifiSwitch.setOnCheckedChangeListener(switchListeners);
		bluetoothSwitch.setOnCheckedChangeListener(switchListeners);

		edm = (EnterpriseDeviceManager) getSystemService(EnterpriseDeviceManager.ENTERPRISE_POLICY_SERVICE);
		restrictionPolicy = edm.getRestrictionPolicy();
		
	}

	private OnCheckedChangeListener switchListeners = new OnCheckedChangeListener(){


		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			switch (buttonView.getId()){
			case R.id.wifiSwitch:
				if (!restrictionPolicy.isWiFiEnabled(false) && isChecked){
					restrictionPolicy.setWiFiState(true);
				}else if (restrictionPolicy.isWiFiEnabled(false) && !isChecked){
					restrictionPolicy.setWiFiState(false);
				}
				break;
			case R.id.bluetoothSwitch:
				if (!restrictionPolicy.isBluetoothEnabled(false) && isChecked){
					restrictionPolicy.setBluetoothState(true);
				}else if (restrictionPolicy.isBluetoothEnabled(false) && !isChecked){
					restrictionPolicy.setBluetoothState(false);
				}
				break;
			}

		}

	};



}
