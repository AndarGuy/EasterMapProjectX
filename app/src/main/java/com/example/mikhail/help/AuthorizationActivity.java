package com.example.mikhail.help;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.example.mikhail.help.util.Utilities;

public class AuthorizationActivity extends AppCompatActivity {

    private static final String TAG = "AuthorizationActivity";

    private static final String
            LOGIN = "log",
            REGISTER = "reg";

    private Button registerButton, loginButton;

    private AutoCompleteTextView loginView, passwordView;

    private TextView loginInfo, passwordInfo;

    public static final int
            MAX_LOGIN_LENGTH = 16,
            MIN_LOGIN_LENGTH = 4,
            MAX_PASSWORD_LENGTH = 32,
            MIN_PASSWORD_LENGTH = 6;

    public static final int
            ENCRYPT_ERROR = 3,
            CHARACTERS_ERROR = 2,
            LENGTH_ERROR = 1,
            OK = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        loginButton = findViewById(R.id.enterButton);
        registerButton = findViewById(R.id.registerButton);
        loginView = findViewById(R.id.nicknameInput);
        passwordView = findViewById(R.id.passwordInput);
        loginInfo = findViewById(R.id.nicknameInfo);
        passwordInfo = findViewById(R.id.passwordInfo);

        loginView.setOnEditorActionListener(null);

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
                String login = loginView.getText().toString();
                String password = passwordView.getText().toString();
                if (checkData(login, password)) makeRequest(LOGIN, login, password);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String login = loginView.getText().toString();
                String password = passwordView.getText().toString();
                if (checkData(login, password)) makeRequest(REGISTER, login, password);
            }
        });
    }

    private boolean checkData(String login, String password) {

        switch (isLoginCorrect(login)) {
            case OK:
                loginView.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                loginInfo.setText("");
                break;
            case LENGTH_ERROR:
                loginView.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                loginInfo.setText(getResources().getString(R.string.need_from_to_symbols).replace("%name%", getResources().getString(R.string.login)).replace("%from%", String.valueOf(MIN_LOGIN_LENGTH)).replace("%to%", String.valueOf(MAX_LOGIN_LENGTH)));
                return false;
            case CHARACTERS_ERROR:
                loginView.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                loginInfo.setText(getResources().getString(R.string.unknown_symbols));
                return false;
            default:
                loginView.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                loginInfo.setText(getResources().getString(R.string.something_wrong));
                return false;
        }

        switch (isPasswordCorrect(password)) {
            case OK:
                passwordView.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                passwordInfo.setText("");
                break;
            case LENGTH_ERROR:
                passwordView.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                passwordInfo.setText(getResources().getString(R.string.need_from_to_symbols).replace("%name%", getResources().getString(R.string.login)).replace("%from%", String.valueOf(MIN_LOGIN_LENGTH)).replace("%to%", String.valueOf(MAX_LOGIN_LENGTH)));
                return false;
            case CHARACTERS_ERROR:
                passwordView.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                passwordInfo.setText(getResources().getString(R.string.unknown_symbols));
                return false;
            default:
                passwordView.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                passwordInfo.setText(getResources().getString(R.string.something_wrong));
                return false;
        }

        return true;
    }

    private int isLoginCorrect(String login) {
        if (login.length() > MAX_LOGIN_LENGTH || login.length() < MIN_LOGIN_LENGTH) {
            return LENGTH_ERROR;
        } else if (Utilities.isAvailableCharacters(login, Utilities.LOGIN_CHARACTERS)) {
            return CHARACTERS_ERROR;
        } else {
            return OK;
        }
    }

    private int isPasswordCorrect(String password) {
        if (password.length() > MAX_PASSWORD_LENGTH || password.length() < MIN_PASSWORD_LENGTH) {
            return LENGTH_ERROR;
        } else if (Utilities.isAvailableCharacters(password, Utilities.PASSWORD_CHARACTERS)) {
            return CHARACTERS_ERROR;
        } else {
            return OK;
        }
    }

    private int makeRequest(String type, String login, String password) {
        String encryptedLogin, encryptedPassword;
        try {
            encryptedLogin = Utilities.getSHA3(login);
            encryptedPassword = Utilities.getSHA3(password);
        } catch (Exception e) {
            return ENCRYPT_ERROR;
        }

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
