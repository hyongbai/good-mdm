package com.example.samsungmdm;

import android.app.enterprise.license.EnterpriseLicenseManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sec.enterprise.knox.license.KnoxEnterpriseLicenseManager;

/**
 * Created by wasiur on 2014-08-12.
 */
public class ActivateLicense extends BroadcastReceiver {

    public static final String ELM_KEY = "92D4017683D1EE2644AF68C484DE6A1205DD41D435DBF5EEF7139CA1D8072BC56E7AD1D46855C034027624BC9AD04F82561E7F03A3F42D1723C301E57314933E";
    public static final String KLM_KEY = "KLM03-NMS7W-6PZZ0-ZV35D-1PSIN-2LZNW";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (EnterpriseLicenseManager.ACTION_LICENSE_STATUS.equals(intent.getAction())) {

            Log.d(MainActivity.TAG, intent.getExtras().getString(EnterpriseLicenseManager.EXTRA_LICENSE_STATUS));
            Log.d(MainActivity.TAG, String.valueOf(intent.getExtras().getInt(EnterpriseLicenseManager.EXTRA_LICENSE_ERROR_CODE, Integer.MIN_VALUE)));

            // KLMS result
        } else if (KnoxEnterpriseLicenseManager.ACTION_LICENSE_STATUS.equals(intent.getAction())) {
            final String status = intent.getExtras().getString(KnoxEnterpriseLicenseManager.EXTRA_LICENSE_STATUS);
            final int rc0 = intent.getExtras().getInt(KnoxEnterpriseLicenseManager.EXTRA_LICENSE_ERROR_CODE, Integer.MIN_VALUE);
            final int rc1 = intent.getExtras().getInt(KnoxEnterpriseLicenseManager.EXTRA_LICENSE_RESULT_TYPE, Integer.MIN_VALUE);
            final int rc2 = intent.getExtras().getInt(KnoxEnterpriseLicenseManager.EXTRA_LICENSE_ACTIVATION_INITIATOR, Integer.MIN_VALUE);

            Log.d(MainActivity.TAG, status);
            Log.d(MainActivity.TAG, String.valueOf(rc0));
            Log.d(MainActivity.TAG, String.valueOf(rc1));
            Log.d(MainActivity.TAG, String.valueOf(rc2));

        }

    }

    public static synchronized void applyInitialLicenses(Context context) {

        Log.d(MainActivity.TAG, "Activating keys...");
        Log.d(MainActivity.TAG, String.format("ELM: %s", ELM_KEY));
        Log.d(MainActivity.TAG, String.format("KLM: %s", KLM_KEY));
        KnoxEnterpriseLicenseManager.getInstance(context).activateLicense(KLM_KEY);
        EnterpriseLicenseManager.getInstance(context).activateLicense(ELM_KEY);

    }
}
