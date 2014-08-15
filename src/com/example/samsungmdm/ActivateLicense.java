package com.example.samsungmdm;

import android.app.enterprise.license.EnterpriseLicenseManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.sec.enterprise.knox.license.KnoxEnterpriseLicenseManager;

/**
 * Created by wasiur on 2014-08-12.
 */
public class ActivateLicense extends BroadcastReceiver {

    public static final String ELM_KEY = "92D4017683D1EE2644AF68C484DE6A1205DD41D435DBF5EEF7139CA1D8072BC56E7AD1D46855C034027624BC9AD04F82561E7F03A3F42D1723C301E57314933E";
    public static final String KLM_KEY = "KLM03-NMS7W-6PZZ0-ZV35D-1PSIN-2LZNW";

    public static final String PREFS_NAME = "MyPrefsFile";

    private static final String SUCCESS = "success";
    private static final String FAILURE = "fail";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (EnterpriseLicenseManager.ACTION_LICENSE_STATUS.equals(intent.getAction())) {
            final String status = intent.getExtras().getString(EnterpriseLicenseManager.EXTRA_LICENSE_STATUS);
            final String errorCode = String.valueOf(intent.getExtras().getInt(EnterpriseLicenseManager.EXTRA_LICENSE_ERROR_CODE, Integer.MIN_VALUE));

            Log.d(MainActivity.TAG, status);
            Log.d(MainActivity.TAG, errorCode);

            if (SUCCESS.equals(status)) {
                Log.d(MainActivity.TAG, "ELM success");

                setPreferences(context.getApplicationContext(), "ELM_ENROLLED", true);
            } else if (FAILURE.equals(status)) {
                final String errorMsg = "ELM failure: " + errorCode;
                final String userMsg = "ELM Activation failed: " + errorCode;

                Log.w(MainActivity.TAG, errorMsg);

                Toast.makeText(context.getApplicationContext(), userMsg, Toast.LENGTH_LONG).show();
            } else {
                Log.e(MainActivity.TAG, "unknown ELM state ignored: " + status + ", " + errorCode);
            }
        } else if (KnoxEnterpriseLicenseManager.ACTION_LICENSE_STATUS.equals(intent.getAction())) {
            final String status = intent.getExtras().getString(KnoxEnterpriseLicenseManager.EXTRA_LICENSE_STATUS);
            final int rc0 = intent.getExtras().getInt(KnoxEnterpriseLicenseManager.EXTRA_LICENSE_ERROR_CODE, Integer.MIN_VALUE);
            final int rc1 = intent.getExtras().getInt(KnoxEnterpriseLicenseManager.EXTRA_LICENSE_RESULT_TYPE, Integer.MIN_VALUE);
            final int rc2 = intent.getExtras().getInt(KnoxEnterpriseLicenseManager.EXTRA_LICENSE_ACTIVATION_INITIATOR, Integer.MIN_VALUE);

            Log.d(MainActivity.TAG, status);
            Log.d(MainActivity.TAG, String.valueOf(rc0));
            Log.d(MainActivity.TAG, String.valueOf(rc1));
            Log.d(MainActivity.TAG, String.valueOf(rc2));

            if (SUCCESS.equals(status)) {
                Log.d(MainActivity.TAG, String.format("KLMS success: " + rc0 + " " + rc1 + " " + rc2));

                setPreferences(context.getApplicationContext(), "KLM_ENROLLED", true);
            } else if (FAILURE.equals(status)) {
                final String errorMsg = String.format("KLMS failure: " + rc0 + " " + rc1 + " " + rc2);
                final String userMsg = String.format("KLM Activation failed: " + rc0);
                Log.w(MainActivity.TAG, errorMsg);

                Toast.makeText(context.getApplicationContext(), userMsg, Toast.LENGTH_LONG).show();
            } else {
                Log.e(MainActivity.TAG, "unknown KLMS state ignored: " + status + " " + rc0 + " " + rc1 + " " + rc2);
            }
        } else {
            Log.e(MainActivity.TAG, "unknown action intent ignored");
        }
    }

    public static synchronized void applyInitialLicenses(Context context) {
        if(!(getPreferences(context, "KLM_ENROLLED") && getPreferences(context, "ELM_ENROLLED"))) {
            Log.d(MainActivity.TAG, "Activating keys...");
            Log.d(MainActivity.TAG, String.format("ELM: %s", ELM_KEY));
            Log.d(MainActivity.TAG, String.format("KLM: %s", KLM_KEY));
            KnoxEnterpriseLicenseManager.getInstance(context).activateLicense(KLM_KEY);
            EnterpriseLicenseManager.getInstance(context).activateLicense(ELM_KEY);
        } else {
            Log.d(MainActivity.TAG, "Already enrolled ELM and KLM keys");
            Toast.makeText(context.getApplicationContext(), "Already enrolled ELM and KLM keys", Toast.LENGTH_LONG).show();
        }
    }

    private static boolean getPreferences(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
        return sharedPreferences.getBoolean(key, false);
    }

    private void setPreferences(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
}
