package com.example.samsungmdm;

import android.app.enterprise.RestrictionPolicy;

/**
 * Created by wasiur on 2014-08-15.
 */
public class Bluetooth {

    private RestrictionPolicy restrictionPolicy;

    public Bluetooth (RestrictionPolicy restrictionPolicy){
        this.restrictionPolicy = restrictionPolicy;
    }

    public void enableBluetooth(){
        restrictionPolicy.setBluetoothState(true);
    }

    public void disableBluetooth(){
        restrictionPolicy.setBluetoothState(false);
    }

    public boolean isEnabled(){
        return restrictionPolicy.isBluetoothEnabled(false);
    }


}
