package com.example.recorder;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;

public class RecordingService extends Service {
    private static final String TAG = "RecordingService";
    private String fileName = null;
    private String filePath = null;
    private MediaRecorder recorder = null;
    private DatabaseHandler database;
    private long startTimeMillis = 0;
    private long elapseMillis = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        database = new DatabaseHandler(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startRecording();
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        if (recorder!=null){
            stopRecording();
        }
        super.onDestroy();
    }

    private void startRecording() {
        setFileNameAndPath();
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile(filePath);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setAudioChannels(1);

        try{
            recorder.prepare();
            recorder.start();
            startTimeMillis = System.currentTimeMillis();
        }catch (IOException e){
            Log.e(TAG, "prepare() failed");
        }
    }

    private void setFileNameAndPath() {
        int count = 0;
        File file;
        do {
            count++;
            fileName = "Record"+"_"+(database.getCount()+count)+".mp4";
            filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            filePath += "/Recorder/"+fileName;

            file = new File(filePath);
        }while (file.exists() && !file.isDirectory());
    }

    private void stopRecording() {
        recorder.stop();
        elapseMillis = (System.currentTimeMillis() - startTimeMillis);
        recorder.release();
        Toast.makeText(this,getString(R.string.toast_recording_finish)+" "+filePath,Toast.LENGTH_LONG).show();
        recorder = null;
        try{
            database.addRecording(fileName,filePath,elapseMillis);
        }catch (Exception e){

        }


    }
}
