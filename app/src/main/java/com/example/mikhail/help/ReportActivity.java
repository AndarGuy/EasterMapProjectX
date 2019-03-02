package com.example.mikhail.help;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mikhail.help.web.RequestListener;
import com.example.mikhail.help.web.RetrofitRequest;

import java.util.HashMap;

import retrofit2.Call;

public class ReportActivity extends AppCompatActivity {

    Button sendButton;
    TextInputLayout reportBugTextLayout;
    TextView textView;
    ProgressBar loading;

    private final static String
            PASSWORD = "password",
            EMAIL = "email",
            REPORT = "report",
            BUG = "bug",
            TEXT = "text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        sendButton = findViewById(R.id.sendButton);
        reportBugTextLayout = findViewById(R.id.bugTextEditContainer);
        loading = findViewById(R.id.loadingIndicator);
        textView = findViewById(R.id.infoUnderButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int msgLen = reportBugTextLayout.getEditText().getText().length();
                if (msgLen <= reportBugTextLayout.getCounterMaxLength()) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ReportActivity.this);
                    final RetrofitRequest request = new RetrofitRequest(BUG, REPORT, preferences.getString(EMAIL, null), preferences.getString(PASSWORD, null));
                    request.putParam(TEXT, reportBugTextLayout.getEditText().getText().toString());
                    sendButton.setEnabled(false);
                    loading.setVisibility(View.VISIBLE);
                    request.setListener(new RequestListener() {
                        @Override
                        public void onResponse(Call<Object> call, HashMap<String, String> response, Integer result) {
                            Toast.makeText(getApplicationContext(), getString(R.string.thank_for_feedback), Toast.LENGTH_SHORT).show();
                            sendButton.setEnabled(true);
                            loading.setVisibility(View.INVISIBLE);
                            finish();
                        }

                        @Override
                        public void onFailure(Call<Object> call, Throwable t) {
                            textView.setText(R.string.error_occurred);
                            sendButton.setEnabled(true);
                            loading.setVisibility(View.INVISIBLE);
                        }
                    });
                    request.makeRequest();
                }
            }
        });

        setupToolbar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar() {
        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitle(R.string.bug_report);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
