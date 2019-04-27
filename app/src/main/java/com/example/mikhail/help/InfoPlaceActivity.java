package com.example.mikhail.help;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mikhail.help.util.Message;
import com.example.mikhail.help.util.Place;
import com.example.mikhail.help.util.Utilities;
import com.example.mikhail.help.web.RequestListener;
import com.example.mikhail.help.web.RetrofitRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.rw.keyboardlistener.KeyboardUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
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
    private LinearLayout commentContainer;
    private TextView description, name;
    private ImageView image, icon;
    private String imagePath, id;
    private CircleImageView avatar;
    private FloatingActionButton showOnMap;

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
                    ArrayList<Message> messages = new ArrayList<>();
                    commentContainer.removeAllViewsInLayout();
                    for (int i = 0; i < response.keySet().size() - 1; i++) {
                        HashMap<String, String> tempMsg = gson.fromJson(gson.toJson(response.get(String.valueOf(i))), HashMap.class);
                        Message msg = new Message(tempMsg.get(USER), tempMsg.get(TEXT), Utilities.parseDateFromString(tempMsg.get(DATE)));
                        messages.add(msg);
                    }

                    Collections.sort(messages, new Comparator<Message>() {
                        @Override
                        public int compare(Message o1, Message o2) {
                            return o2.getDate().compareTo(o1.getDate());
                        }
                    });
                    for (Message message : messages) addComment(message, commentContainer);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_menu, menu);

        int dp8 = Utilities.getPxFromDp(8, this), dp16 = Utilities.getPxFromDp(16, this);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        LinearLayout linearLayout = (LinearLayout) searchItem.getActionView();
        final ImageView imageView = new ImageView(this);

        LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        imageLayoutParams.gravity = Gravity.CENTER;
        imageLayoutParams.setMarginEnd(dp16);
        imageLayoutParams.setMarginStart(dp16);

        imageView.setLayoutParams(imageLayoutParams);
        imageView.setImageDrawable(getDrawable(R.drawable.ic_share));
        imageView.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.interesting_place) + " " + name.getText());
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, description.getText() + "\n" + getString(R.string.download_app));
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_via)));
            }
        });
        linearLayout.addView(imageView);

        return super.onCreateOptionsMenu(menu);
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
        CircleImageView msgAvatar = commentSample.findViewById(R.id.avatar);
        msgAvatar.setColorFilter(Utilities.getColorOfString(Utilities.formatName(msg.getName())));
        msgName.setText(Utilities.formatName(msg.getName()));
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

        showOnMap = findViewById(R.id.showOnMapButton);

        commentEdit = findViewById(R.id.comment);
        commentContainer = findViewById(R.id.commentContainer);
        avatar = findViewById(R.id.avatar);

        avatar.setColorFilter(Utilities.getColorOfString(MainActivity.username));

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

        KeyboardUtils.addKeyboardToggleListener(InfoPlaceActivity.this, new KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean b) {
                if (!b) {
                    try {
                        getCurrentFocus().clearFocus();
                    } catch (NullPointerException ignore) {
                    }
                }
            }
        });

        id = getIntent().getExtras().getString(Place.ID);
        imagePath = getIntent().getExtras().getString(Place.IMAGE);
        image.setImageBitmap(BitmapFactory.decodeFile(imagePath));

        String[] params = {Place.NAME, Place.DESCRIPTION, Place.TYPE, Place.LONGITUDE, Place.LATITUDE};

        RetrofitRequest infoRequest = new RetrofitRequest(Place.PLACE, Place.GET_INFO);

        infoRequest.putParam(Place.ID, id);
        infoRequest.putParam(Place.PARAMS, TextUtils.join("|", params));

        infoRequest.setListener(new RequestListener() {
            @Override
            public void onResponse(Call<Object> call, final HashMap<String, String> response, Integer result) {
                name.setText(response.get(Place.NAME));
                description.setText(response.get(Place.DESCRIPTION));
                icon.setImageBitmap(Utilities.drawableToBitmap(getResources().getDrawable(Utilities.getIconId(response.get(Place.TYPE)), null)));

                showOnMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MapHandler.moveCameraToPosition(new LatLng(Double.valueOf(response.get(Place.LATITUDE)), Double.valueOf(response.get(Place.LONGITUDE))), 15f);
                        setResult(RESULT_OK, null);
                        finish();
                    }
                });
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

            }
        });

        infoRequest.makeRequest();

        RetrofitRequest imageRequest = new RetrofitRequest(Place.PLACE, Place.GET_IMAGE);

        imageRequest.putParam(Place.ID, id);
        imageRequest.putParam(Place.IMAGE_SIZE, Place.M_SIZE);

        imageRequest.setListener(new RequestListener() {
            @Override
            public void onResponse(Call<Object> call, HashMap<String, String> response, Integer result) {
                image.setImageBitmap(Utilities.decodeBase64(response.get(Place.IMAGE)));
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

            }
        });

        imageRequest.makeRequest();

        updateComments();
    }

}
