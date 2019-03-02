package com.example.mikhail.help;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

public class AccountSettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final static String EMAIL = "email", LOGOUT = "logout", PASSWORD = "password";
    private static final String TAG = "AccountSettingsFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_account);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        getPreferenceManager().findPreference(EMAIL).setSummary(preferences.getString(EMAIL, null));
        getPreferenceManager().findPreference(LOGOUT).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(final Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.important)
                        .setMessage(R.string.do_you_want_to_log_out)
                        .setCancelable(true)
                        .setNegativeButton(R.string.no,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = preference.getEditor();
                                editor.putString(PASSWORD, null);
                                editor.putString(EMAIL, null);
                                editor.apply();
                                startActivity(new Intent(getActivity(), AuthorizationActivity.class));
                                getActivity().finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                return false;
            }
        });

        Context context = getActivity().getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "onSharedPreferenceChanged: " + key);
    }
}
