package com.example.mikhail.help;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.example.mikhail.help.util.Utilities;
import com.example.mikhail.help.web.RequestListener;
import com.example.mikhail.help.web.RetrofitRequest;

import java.util.HashMap;

import retrofit2.Call;

public class AuthorizationActivity extends AppCompatActivity {

    private final String TAG = "AuthorizationActivity";

    private final String
            PASSWORD = "password",
            EMAIL = "email",
            LOGIN = "login",
            REGISTER = "register";

    public static final String APP_PREFERENCES = "config";

    private Button registerButton, loginButton;

    private AutoCompleteTextView emailView, passwordView;

    private TextView emailInfo, passwordInfo;

    public final int
            MAX_PASSWORD_LENGTH = 32,
            MIN_PASSWORD_LENGTH = 6;

    public final int
            WRONG_EMAIL = 1,
            WRONG_PASSWORD = 2,
            ALREADY_REGISTERED = 3,
            SQL_ERROR = 4;

    public final int
            INVALID_EMAIL = 2,
            LENGTH_ERROR = 1;

    public final int OK = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        loginButton = findViewById(R.id.enterButton);
        registerButton = findViewById(R.id.registerButton);
        emailView = findViewById(R.id.nicknameInput);
        passwordView = findViewById(R.id.passwordInput);
        emailInfo = findViewById(R.id.nicknameInfo);
        passwordInfo = findViewById(R.id.passwordInfo);

        emailView.setOnEditorActionListener(null);

        passwordView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(passwordView, 0);
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailView.getText().toString();
                String password = passwordView.getText().toString();
                if (checkData(email, password)) makeRequest(LOGIN, email, password);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailView.getText().toString();
                String password = passwordView.getText().toString();
                if (checkData(email, password)) makeRequest(REGISTER, email, password);
            }
        });
    }

    private boolean checkData(String email, String password) {

        switch (isEmailCorrect(email)) {
            case OK:
                emailView.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                emailInfo.setText("");
                break;
            case INVALID_EMAIL:
                sendError(getString(R.string.invalid_email), emailView, emailInfo);
                return false;
            default:
                sendError(getResources().getString(R.string.something_wrong), emailView, emailInfo);
                return false;
        }

        switch (isPasswordCorrect(password)) {
            case OK:
                passwordView.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                passwordInfo.setText("");
                break;
            case LENGTH_ERROR:
                sendError(getString(R.string.need_from_to_symbols).replace("%name%", getString(R.string.password)).replace("%from%", String.valueOf(MIN_PASSWORD_LENGTH)).replace("%to%", String.valueOf(MAX_PASSWORD_LENGTH)), passwordView, passwordInfo);
                return false;
            default:
                sendError(getString(R.string.something_wrong), passwordView, passwordInfo);
                return false;
        }

        return true;
    }

    private void sendError(String message, AutoCompleteTextView textView, TextView info) {
        textView.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        info.setText(message);
    }

    private int isEmailCorrect(String email) {
        if (!Utilities.isValidEmail(email)) {
            return INVALID_EMAIL;
        } else {
            return OK;
        }
    }

    private int isPasswordCorrect(String password) {
        if (password.length() > MAX_PASSWORD_LENGTH || password.length() < MIN_PASSWORD_LENGTH) {
            return LENGTH_ERROR;
        } else {
            return OK;
        }
    }

    private int makeRequest(final String action, final String email, final String password) {
        final RetrofitRequest request = new RetrofitRequest(action);

        request.putParam(EMAIL, email);
        request.putParam(PASSWORD, password);
        request.setListener(new RequestListener() {
            @Override
            public void onResponse(Call<Object> call, HashMap<String, Double> response, Integer result) {
                switch (result) {
                    case OK:
                        SharedPreferences preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(EMAIL, email);
                        editor.putString(PASSWORD, password);
                        editor.commit();
                        finish();
                        break;
                    case WRONG_EMAIL:
                        sendError(getString(R.string.wrong_email), emailView, emailInfo);
                        break;
                    case WRONG_PASSWORD:
                        sendError(getString(R.string.wrong_password), passwordView, passwordInfo);
                        break;
                    case ALREADY_REGISTERED:
                        sendError(getString(R.string.already_registered), emailView, emailInfo);
                        break;
                    case SQL_ERROR:
                        sendError(getString(R.string.sql_error), emailView, emailInfo);
                        break;
                    default:
                        sendError(getString(R.string.unknown_error).replace("%num%", String.valueOf(result)), emailView, emailInfo);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.toString());
                sendError(getString(R.string.no_connection), emailView, emailInfo);
            }
        });
        request.makeRequest();
        return OK;
    }

    @Override
    public void onBackPressed() {

        Context context = this;
        context.setTheme(R.style.AppTheme_Light);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getResources().getString(R.string.warning) + "!")
                .setMessage(getResources().getString(R.string.no_sign_in_continue))
                .setCancelable(true)
                .setNegativeButton(getResources().getString(R.string.scontinue),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                finish();
                            }
                        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


}
