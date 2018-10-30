package com.example.mikhail.help;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

public class AuthorizationActivity extends AppCompatActivity {

    Button registerButton, loginButton;

    AutoCompleteTextView nickname, password;

    public static final int
            MAX_LOGIN_LENGTH = 16,
            MIN_LOGIN_LENGTH = 6,
            MAX_PASSWORD_LENGTH = 16,
            MIN_PASSWORD_LENGTH = 6;

    public static final int
            LENGTH_ERROR = 1,
            OK = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        loginButton = findViewById(R.id.enterButton);
        registerButton = findViewById(R.id.registerButton);
        nickname = findViewById(R.id.nicknameInput);
        password = findViewById(R.id.passwordInput);

        nickname.setOnEditorActionListener(null);

        password.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Check is data correct
                //TODO: Login request
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Check is data correct
                //TODO: Register request
            }
        });
    }

    public int isLoginCorrect(String login) {
        if (login.length() > MAX_LOGIN_LENGTH || login.length() < MIN_LOGIN_LENGTH) {
            return LENGTH_ERROR;
        } else if (true) {

        } else {
            return OK;
        }
        return OK;
    }

    public boolean login(String login, String password) {

        return false;
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
