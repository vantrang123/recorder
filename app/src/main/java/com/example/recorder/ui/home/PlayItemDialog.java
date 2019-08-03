package com.example.recorder.ui.home;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.recorder.DatabaseHandler;
import com.example.recorder.R;
import com.example.recorder.data.RecordingItem;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class PlayItemDialog extends DialogFragment {

    private static final String ARG_ITEM = "recording_item";
    private MediaPlayer mediaPlayer = null;
    private FloatingActionButton actionButton = null;
    private SeekBar seekBar = null;
    private TextView fileNameTextView = null;
    private TextView currentProgressTextView = null;
    private TextView fileLengthTextView = null;
    private boolean isPlaying = false;
    private Handler handler = new Handler();
    private RecordingItem item;


    private Context mContext;
    //private DatabaseHandler databaseHandler;
    long minutes = 0;
    long seconds = 0;

    public PlayItemDialog(Context context, RecordingItem item) {
        this.item = item;
        mContext = context;
        //databaseHandler = new DatabaseHandler(context);
        long itemDuration = item.getLength();
        minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration);
        seconds = TimeUnit.MILLISECONDS.toSeconds(itemDuration)
                - TimeUnit.MINUTES.toSeconds(minutes);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            this.getDialog().setCanceledOnTouchOutside(true);
            //WindowManager.LayoutParams params = getDialog().getWindow().getAttributes(); // change this to your dialog.
            //params.y = -100; // Here is the param to set your dialog position. Same with params.x
            //getDialog().getWindow().setAttributes(params);
        }
        View view = inflater.inflate(R.layout.dialog_play,container,false);
        setup(view);
        return view;
    }

    private void setup(View v) {
        fileNameTextView = (TextView)v.findViewById(R.id.tv_rcName);
        fileNameTextView.setText(item.getName());

        fileLengthTextView = (TextView)v.findViewById(R.id.file_length_text_view);
        fileLengthTextView.setText(String.format("%02d:%02d", minutes,seconds));

        currentProgressTextView = (TextView)v.findViewById(R.id.current_progress_text_view);

        actionButton = (FloatingActionButton)v.findViewById(R.id.fab_play);
        actionButton.setImageResource(R.mipmap.ic_play);
        actionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
        actionButton.setRippleColor(getResources().getColor(R.color.colorPrimaryDark));
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(isPlaying);
                isPlaying = !isPlaying;
            }
        });

        seekBar = (SeekBar) v.findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer!=null && fromUser){
                    mediaPlayer.seekTo(progress);
                    handler.removeCallbacks(runnable);

                    int currentPosition = mediaPlayer.getCurrentPosition();
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(currentPosition);
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(currentPosition)
                            - TimeUnit.MINUTES.toSeconds(minutes);

                    currentProgressTextView.setText(String.format("%02d:%02d",minutes,seconds));
                    updateSeekBar();
                }
                else if (mediaPlayer == null && fromUser){
                    prepareMediaPlayerFromPoint(progress);
                    updateSeekBar();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null){
                    handler.removeCallbacks(runnable);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null){
                    handler.removeCallbacks(runnable);
                    mediaPlayer.seekTo(seekBar.getProgress());

                    int currentPosition = mediaPlayer.getCurrentPosition();
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(currentPosition);
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(currentPosition)
                            - TimeUnit.MINUTES.toSeconds(minutes);

                    currentProgressTextView.setText(String.format("%02d:%02d",minutes,seconds));
                    updateSeekBar();
                }
            }
        });
    }

    private void prepareMediaPlayerFromPoint(int progress) {
        mediaPlayer = new MediaPlayer();
        try{
            mediaPlayer.setDataSource(item.getFilePath());
            mediaPlayer.prepare();
            seekBar.setMax(mediaPlayer.getDuration());
            mediaPlayer.seekTo(progress);

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlaying();
                }
            });
        }catch (IOException e){
            Log.e(ARG_ITEM, "prepare() failed");
        }
    }

    private void onPlay(boolean isPlaying) {
        if (!isPlaying){
            if (mediaPlayer==null){
                startPlaying();
            }
            else{
                resumePlaying();
            }
        }
        else{
            pausePlaying();
        }
    }

    private void pausePlaying() {
        actionButton.setImageResource(R.mipmap.ic_play);
        handler.removeCallbacks(runnable);
        mediaPlayer.pause();
    }

    private void resumePlaying() {
        actionButton.setImageResource(R.mipmap.ic_pause);
        handler.removeCallbacks(runnable);
        mediaPlayer.start();

        updateSeekBar();
    }

    private void startPlaying() {
        actionButton.setImageResource(R.mipmap.ic_pause);
        mediaPlayer = new MediaPlayer();
        try{
            mediaPlayer.setDataSource(item.getFilePath());
            mediaPlayer.prepare();
            seekBar.setMax(mediaPlayer.getDuration());
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });
        }catch (IOException e){
            Log.e(ARG_ITEM, "prepare() failed");
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlaying();
            }
        });

        updateSeekBar();
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void stopPlaying() {
        actionButton.setImageResource(R.mipmap.ic_play);
        handler.removeCallbacks(runnable);
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;

        isPlaying = !isPlaying;

        currentProgressTextView.setText(fileLengthTextView.getText());
        seekBar.setProgress(seekBar.getMax());

        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null){
                int currentPosition = mediaPlayer.getCurrentPosition();
                seekBar.setProgress(currentPosition);

                long minutes = TimeUnit.MILLISECONDS.toMinutes(currentPosition);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(currentPosition)
                        - TimeUnit.MINUTES.toSeconds(minutes);

                currentProgressTextView.setText(String.format("%02d:%02d",minutes,seconds));

                updateSeekBar();
            }
        }
    };

    private void updateSeekBar(){
        handler.postDelayed(runnable, 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null){
            stopPlaying();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null){
            stopPlaying();
        }
    }
}
