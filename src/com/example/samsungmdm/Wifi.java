package com.example.samsungmdm;

import android.app.enterprise.RestrictionPolicy;

/**
 * Created by wasiur on 2014-08-18.
 */
public class Wifi {

    private RestrictionPolicy mRestrictionPolicy;

    public Wifi(RestrictionPolicy restrictionPolicy){
        this.mRestrictionPolicy = restrictionPolicy;
    }

    public void enableWifi(){
        mRestrictionPolicy.setWiFiState(true);
    }

    public void disableWifi(){
        mRestrictionPolicy.setWiFiState(false);
    }
}
