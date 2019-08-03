package com.example.recorder.ui.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.recorder.R;

public class SettingActivity extends AppCompatActivity {
    private TextView tv_title;
    private ImageView img_back;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        setup();



    }

    private void setup() {
        tv_title = (TextView)findViewById(R.id.tv_title_settings);
        tv_title.setText(getString(R.string.settings));
        img_back = (ImageView)findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
