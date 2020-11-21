package ru.larin.mykidsposition.mykidspositionclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity {
    private EditText personEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        TrackerConfig config = TrackerApplication.instance().getConfig(getApplicationContext());
        personEditText = (EditText)findViewById(R.id.editTextPerson);

        personEditText.setText(String.valueOf(config.getPerson()));

        TrackerApplication.instance().start(getApplicationContext());
    }

    public void onSave(View view) {
        TrackerConfig config = new TrackerConfig();
        config.setPerson(Long.valueOf(personEditText.getText().toString()));
        TrackerApplication.instance().setConfig(getApplicationContext(), config);
        System.exit(0);
    }

    public void onCancal(View view) {
        System.exit(0);
    }
}
