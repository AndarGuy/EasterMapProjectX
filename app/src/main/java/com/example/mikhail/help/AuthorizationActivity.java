package com.example.mikhail.help;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mikhail.help.util.NameHelper;
import com.example.mikhail.help.util.Utilities;
import com.example.mikhail.help.web.RequestListener;
import com.example.mikhail.help.web.RetrofitRequest;

import java.util.HashMap;

import retrofit2.Call;

public class AuthorizationActivity extends AppCompatActivity {

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
    private final String TAG = "AuthorizationActivity";
    private final String
            PASSWORD = "password",
            EMAIL = "email",
            LOGIN = "login",
            REGISTER = "register",
            NAME = "name";
    private Button changeTypeButton, enterButton;
    private AutoCompleteTextView emailView, passwordView, repeatPassword;
    private TextView emailInfo, passwordInfo, title;

    private LinearLayout passwordRepLayout;

    private String state = LOGIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        enterButton = findViewById(R.id.enterButton);
        changeTypeButton = findViewById(R.id.changeTypeButton);
        emailView = findViewById(R.id.nicknameInput);
        passwordView = findViewById(R.id.passwordInput);
        emailInfo = findViewById(R.id.nicknameInfo);
        passwordInfo = findViewById(R.id.passwordInfo);

        passwordRepLayout = findViewById(R.id.passwordRepLayout);

        title = findViewById(R.id.title);

        repeatPassword = findViewById(R.id.passwordRepInput);

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

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = emailView.getText().toString();
                String password = passwordView.getText().toString();
                final String passwordRep = repeatPassword.getText().toString();

                if (state.equals(LOGIN)) {
                    if (checkData(email, password)) makeRequest(LOGIN, email, password);
                } else {

                    AuthorizationActivity.this.setTheme(R.style.AppTheme_Light);
                    AlertDialog.Builder builder = new AlertDialog.Builder(AuthorizationActivity.this);
                    builder.setTitle(getResources().getString(R.string.warning) + "!")
                            .setMessage(getResources().getString(R.string.no_provide))
                            .setCancelable(true)
                            .setPositiveButton(R.string.scontinue,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            String email = emailView.getText().toString();
                                            String password = passwordView.getText().toString();
                                            if (checkData(email, password))
                                                if (password.equals(passwordRep))
                                                    makeRequest(REGISTER, email, password);
                                                else passwordInfo.setText(R.string.passwords_different);

                                        }
                                    }).setNegativeButton(R.string.cancel, null);

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                }
            }
        });

        changeTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (state.equals(REGISTER)) {
                    state = LOGIN;
                    title.setText(R.string.sign_in);
                    changeTypeButton.setText(R.string.register);
                    passwordRepLayout.setAlpha(0);
                } else {
                    state = REGISTER;
                    title.setText(R.string.register);
                    changeTypeButton.setText(R.string.sign_in);
                    passwordRepLayout.setAlpha(1);
                }

            }
        });
    }

    private boolean checkData(String email, String password) {

        switch (Utilities.isEmailCorrect(email)) {
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

        switch (Utilities.isPasswordCorrect(password)) {
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

    private void makeRequest(final String action, final String email, final String password) {
        RetrofitRequest request = new RetrofitRequest(action, email, password);
        request.setListener(new RequestListener() {
            @Override
            public void onResponse(Call<Object> call, HashMap<String, String> response, Integer result) {
                switch (result) {
                    case OK:
                        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AuthorizationActivity.this);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(EMAIL, email);
                        editor.putString(PASSWORD, password);
                        editor.apply();
                        setResult(RESULT_OK);
                        startActivity(new Intent(AuthorizationActivity.this, MainActivity.class));
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
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }


}
