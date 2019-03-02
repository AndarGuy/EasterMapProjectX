package com.example.mikhail.help;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mikhail.help.util.Utilities;

import java.util.Map;

public class InfoActivity extends AppCompatActivity {

    private final String
            LONGITUDE = "longitude",
            LATITUDE = "latitude",
            ICON = "icon",
            ADDRESS = "address",
            DESCRIPTION = "description",
            NAME = "name",
            IMAGE = "image";

    private TextView description, name, address;
    private ImageView image, icon;

    private void setupToolbar() {
        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitle(null);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MapHandler.isInfoActivityOpen = false;
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MapHandler.isInfoActivityOpen = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        setupToolbar();

        name = findViewById(R.id.nameText);
        description = findViewById(R.id.descriptionText);
        image = findViewById(R.id.myImage);
        icon = findViewById(R.id.iconImage);

        name.setText(getIntent().getExtras().getString(NAME));
        description.setText(getIntent().getExtras().getString(DESCRIPTION));
        image.setImageBitmap(Utilities.decodeBase64(getIntent().getExtras().getString(IMAGE)));
        icon.setImageBitmap(Utilities.getBitmapFromVectorDrawable(this, getIntent().getExtras().getInt(ICON)));
    }

}
