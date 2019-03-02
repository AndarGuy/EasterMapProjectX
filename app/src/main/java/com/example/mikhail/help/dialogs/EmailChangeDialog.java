package com.example.mikhail.help.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.DialogPreference;
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

public class EmailChangeDialog extends DialogPreference {

    private EditText emailEdit;
    private TextView emailEditInfo;
    private static final int OK = 0;

    public EmailChangeDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_email_change);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        emailEditInfo = view.findViewById(R.id.newEmailInfo);

        emailEdit = view.findViewById(R.id.newEmailEdit);
        emailEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);

        final Button button = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isEmailCorrect(emailEdit.getText().toString()) == OK) {
                    emailEditInfo.setText(null);

                } else emailEditInfo.setText(R.string.invalid_email);
            }
        });
    }
}
