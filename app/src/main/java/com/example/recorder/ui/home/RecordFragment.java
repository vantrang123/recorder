package com.example.recorder.ui.home;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recorder.R;
import com.example.recorder.RecordingService;

import java.io.File;


public class RecordFragment extends Fragment {
    private FloatingActionButton actionButton;


    private Chronometer chronometer;
    private int status_count = 0;
    private TextView status;
    private Boolean startRecord = true;
    private Boolean stopRecord = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record,container,false);
        setupFloatingActionButton(view);

        setup(view);
        return view;

    }

    private void setupFloatingActionButton(View v) {
        actionButton = (FloatingActionButton) v.findViewById(R.id.btnFloating);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecord(startRecord);
                startRecord = !startRecord;
            }
        });

    }

    private void setup(View v) {
        status = (TextView)v.findViewById(R.id.tv_status);
        chronometer = (Chronometer) v.findViewById(R.id.timer);
    }

    private void onRecord(final Boolean start) {
        Intent intent = new Intent(getActivity(), RecordingService.class);

        if (start){
            Toast.makeText(getActivity(),R.string.toast_recording_start,Toast.LENGTH_SHORT).show();
            File folder = new File(Environment.getExternalStorageDirectory()+"/Recorder");
            if (!folder.exists()){
                folder.mkdir();
            }

            actionButton.setImageResource(R.mipmap.ic_media_stop);
            actionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorRed)));
            actionButton.setRippleColor(getResources().getColor(R.color.colorRed_Black));

            //start Chronometer
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
            chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    switch (status_count){
                        case 0:
                            status.setText(getString(R.string.tv_status_stop) + ".");
                            break;
                        case 1:
                            status.setText(getString(R.string.tv_status_stop) + "..");
                            break;
                        case 2:
                            status.setText(getString(R.string.tv_status_stop) + "...");
                            status_count-=3;
                            break;
                    }
                    status_count++;
                }
            });

            //start RecordingService
            getActivity().startService(intent);
            //keep screen on while recording
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        }

        else {
            actionButton.setImageResource(R.mipmap.ic_mic_white_36dp);
            actionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            actionButton.setRippleColor(getResources().getColor(R.color.colorPrimaryDark));
            chronometer.stop();
            chronometer.setBase(SystemClock.elapsedRealtime());
            status.setText(R.string.tv_status_start);

            getActivity().stopService(intent);
            //allow the screen to turn off again once recording is finished
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        }
    }



}
