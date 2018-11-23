package com.example.mikhail.help;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mikhail.help.util.IconRendered;
import com.example.mikhail.help.util.Utilities;

import java.util.List;
import java.util.Locale;

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
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        setupToolbar();

        name = findViewById(R.id.nameText);
        description = findViewById(R.id.descriptionText);
        image = findViewById(R.id.myImage);
        address = findViewById(R.id.addressText);
        icon = findViewById(R.id.iconImage);

        name.setText(getIntent().getExtras().getString(NAME));
        description.setText(getIntent().getExtras().getString(DESCRIPTION));
        image.setImageBitmap(Utilities.decodeBase64(getIntent().getExtras().getString(IMAGE)));
        icon.setImageBitmap(Utilities.decodeBase64(getIntent().getExtras().getString(ICON)));

        try {
            Geocoder geo = new Geocoder(this.getBaseContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(getIntent().getExtras().getDouble(LATITUDE), getIntent().getExtras().getDouble(LONGITUDE), 1);
            if (!addresses.isEmpty()) {
                address.setText(addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
