package com.example.mikhail.help;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

public class AuthorizationActivity extends AppCompatActivity {

    Button registerButton, loginButton;

    AutoCompleteTextView nickname, password;

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
                //login
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //register
            }
        });
    }

    @Override
    public void onBackPressed() {

        Context context = this;
        context.setTheme(R.style.AppTheme_Light);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getResources().getString(R.string.warning) + "!")
                .setMessage(getResources().getString(R.string.no_sign_in_continue) + "!")
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
