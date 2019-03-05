package com.example.mikhail.help;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mikhail.help.util.Message;
import com.example.mikhail.help.util.Utilities;
import com.example.mikhail.help.web.RequestListener;
import com.example.mikhail.help.web.RetrofitRequest;
import com.google.gson.Gson;
import com.rw.keyboardlistener.KeyboardUtils;

import java.util.HashMap;

import retrofit2.Call;

public class InfoPlaceActivity extends AppCompatActivity {

    private static final String TAG = "InfoPlaceActivity";

    private final String
            USER = "user",
            DATE = "date",
            ID = "id",
            TEXT = "text",
            GET = "get",
            SEND = "send",
            MSGS = "msgs",
            EMAIL = "email",
            PASSWORD = "password",
            LONGITUDE = "longitude",
            LATITUDE = "latitude",
            ICON = "icon",
            ADDRESS = "address",
            DESCRIPTION = "description",
            NAME = "name",
            IMAGE = "image";
    private final int OK = 0;
    private EditText commentEdit;
    private LinearLayout buttonLayout, commentContainer;
    private TextView description, name, address;
    private ImageView image, icon;
    private String imagePath;
    private Button sendButton, cancelButton;

    private void setupToolbar() {
        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitle(null);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void updateComments() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        RetrofitRequest request = new RetrofitRequest(MSGS, GET, preferences.getString(EMAIL, null), preferences.getString(PASSWORD, null));
        request.putParam(ID, getIntent().getExtras().getString(ID));
        request.setListener(new RequestListener() {
            @Override
            public void onResponse(Call<Object> call, HashMap<String, String> response, Integer result) {
                Gson gson = new Gson();
                if (result == OK) {
                    Log.d(TAG, "onResponse: " + response);
                    commentContainer.removeAllViewsInLayout();
                    for (int i = 0; i < response.keySet().size() - 1; i++) {
                        HashMap<String, String> tempMsg = gson.fromJson(gson.toJson(response.get(String.valueOf(i))), HashMap.class);
                        Message msg = new Message(tempMsg.get(USER), tempMsg.get(TEXT), Utilities.parseDateFromString(tempMsg.get(DATE)));
                        addComment(msg, commentContainer);
                        Log.d(TAG, "onResponse: " + msg.getName() + " " + msg.getText() + " " + msg.getDate().getTime());
                    }
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

            }
        });
        request.makeRequest();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MapHandler.isInfoActivityOpen = false;
        finish();
        return super.onOptionsItemSelected(item);
    }

    private void sendMsg() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        RetrofitRequest request = new RetrofitRequest(MSGS, SEND, preferences.getString(EMAIL, null), preferences.getString(PASSWORD, null));
        if (commentEdit.getText().toString().isEmpty()) return;
        request.putParam(ID, getIntent().getExtras().getString(ID));
        request.putParam(TEXT, commentEdit.getText().toString());
        request.setListener(new RequestListener() {
            @Override
            public void onResponse(Call<Object> call, HashMap<String, String> response, Integer result) {
                updateComments();
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

            }
        });
        request.makeRequest();
    }

    public void addComment(Message msg, LinearLayout layout) {
        LinearLayout commentSample = (LinearLayout) getLayoutInflater().inflate(R.layout.comment_sample, null);
        TextView msgName = commentSample.findViewById(R.id.msgNickname), msgText = commentSample.findViewById(R.id.msgText);
        msgName.setText(msg.getName());
        msgText.setText(msg.getText());
        layout.addView(commentSample);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MapHandler.isInfoActivityOpen = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_place);

        setupToolbar();

        name = findViewById(R.id.nameText);
        description = findViewById(R.id.descriptionText);
        image = findViewById(R.id.myImage);
        icon = findViewById(R.id.iconImage);
        commentEdit = findViewById(R.id.comment);
        buttonLayout = findViewById(R.id.buttonLayout);
        sendButton = findViewById(R.id.sendButton);
        cancelButton = findViewById(R.id.cancelButton);
        commentContainer = findViewById(R.id.commentContainer);

        commentEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    sendMsg();
                    InputMethodManager imm = (InputMethodManager) InfoPlaceActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    getCurrentFocus().clearFocus();
                    commentEdit.setText("");
                    return true;
                }
                return false;
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsg();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) InfoPlaceActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                getCurrentFocus().clearFocus();
                commentEdit.setText("");
            }
        });

        KeyboardUtils.addKeyboardToggleListener(InfoPlaceActivity.this, new KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean b) {
                Log.d(TAG, "onToggleSoftKeyboard: eseges");
                if (!b) {
                    try {
                        getCurrentFocus().clearFocus();
                    } catch (NullPointerException ignore) {
                    }
                }
            }
        });

        commentEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) buttonLayout.setVisibility(View.VISIBLE);
                else buttonLayout.setVisibility(View.INVISIBLE);
            }
        });

        name.setText(getIntent().getExtras().getString(NAME));
        description.setText(getIntent().getExtras().getString(DESCRIPTION));
        imagePath = getIntent().getExtras().getString(IMAGE);
        image.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        icon.setImageBitmap(Utilities.getBitmapFromVectorDrawable(this, getIntent().getExtras().getInt(ICON)));

        updateComments();
    }

}
