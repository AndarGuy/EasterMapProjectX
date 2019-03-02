package com.example.mikhail.help.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mikhail.help.R;
import com.example.mikhail.help.util.Utilities;
import com.example.mikhail.help.web.RequestListener;
import com.example.mikhail.help.web.RetrofitRequest;

import java.util.HashMap;

import retrofit2.Call;

public class PasswordChangeDialog extends DialogPreference {

    private static final String TAG = "PasswordChangeDialog";

    private static final int OK = 0;
    private static final String MANAGE = "manage", PASSWORD = "password", EMAIL = "email", NEW_PASSWORD = "new_password";
    private final int
            MAX_PASSWORD_LENGTH = 32,
            MIN_PASSWORD_LENGTH = 6;

    private EditText curPassword, newPassword, newPasswordRepeat;
    private TextView curPasswordInfo, newPasswordInfo, newPasswordRepeatInfo;

    public PasswordChangeDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_password_change);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        curPasswordInfo = view.findViewById(R.id.pastPasswordInfo);
        newPasswordInfo = view.findViewById(R.id.newPasswordInfo);
        newPasswordRepeatInfo = view.findViewById(R.id.newPasswordRepInfo);
        curPassword = view.findViewById(R.id.pastPasswordEdit);
        curPassword.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        newPassword = view.findViewById(R.id.newPasswordEdit);
        newPassword.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        newPasswordRepeat = view.findViewById(R.id.newPasswordRepEdit);
        newPasswordRepeat.setImeOptions(EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);

        final Button button = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: clicked!");
                if (Utilities.isPasswordCorrect(newPassword.getText().toString()) == OK) {
                    newPasswordInfo.setText(null);
                    if (newPassword.getText().toString().equals(newPasswordRepeat.getText().toString())) {
                        newPasswordRepeatInfo.setText(null);
                        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(PasswordChangeDialog.this.getContext());
                        button.setEnabled(false);
                        RetrofitRequest request = new RetrofitRequest(MANAGE, PASSWORD, preferences.getString(EMAIL, null), curPassword.getText().toString());
                        request.putParam(NEW_PASSWORD, newPassword.getText().toString());
                        request.setListener(new RequestListener() {
                            @Override
                            public void onResponse(Call<Object> call, HashMap<String, String> response, Integer result) {

                                if (result == OK) {
                                    preferences.edit().putString(PASSWORD, newPassword.getText().toString()).apply();
                                    getDialog().cancel();
                                }
                            }

                            @Override
                            public void onFailure(Call<Object> call, Throwable t) {
                                curPasswordInfo.setText(R.string.wrong_password);
                                 button.setEnabled(true);
                            }
                        });
                        request.makeRequest();
                    } else newPasswordRepeatInfo.setText(R.string.passwords_different);
                } else
                    newPasswordInfo.setText(getContext().getString(R.string.need_from_to_symbols).replace("%name%", getContext().getString(R.string.password)).replace("%from%", String.valueOf(MIN_PASSWORD_LENGTH)).replace("%to%", String.valueOf(MAX_PASSWORD_LENGTH)));
            }
        });
    }

}
