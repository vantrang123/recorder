package com.example.recorder.ui.home;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recorder.DatabaseHandler;
import com.example.recorder.R;
import com.example.recorder.data.RecordingItem;

import java.io.File;

public class RemaneDialog extends DialogFragment {
    private static final String TAG = "RemaneDialog";
    private TextView title;
    private EditText input;
    private TextView cancle;
    private TextView save;
    private Context mContext;
    private int id;
    private DatabaseHandler databaseHandler;

    public RemaneDialog(Context context, int position) {
        mContext = context;
        id = position;
        databaseHandler = new DatabaseHandler(context);
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
            WindowManager.LayoutParams params = getDialog().getWindow().getAttributes(); // change this to your dialog.
            params.y = -100; // Here is the param to set your dialog position. Same with params.x
            getDialog().getWindow().setAttributes(params);
        }
        View view = inflater.inflate(R.layout.dialog_remane, container, false);
        setup(view);
        return view;
    }

    public void setup(View v) {
        title = (TextView)v.findViewById(R.id.tv_title_remane);
        title.setText(R.string.dialog_title_rename);

        input = (EditText)v.findViewById(R.id.edt_input);

        cancle = (TextView)v.findViewById(R.id.tv_cancel);
        cancle.setText(R.string.dialog_button_cancel);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });

        save = (TextView)v.findViewById(R.id.tv_save);
        save.setText(R.string.dialog_button_choice);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remane(id,input.getText().toString().trim()+ ".mp4");
                getDialog().cancel();
            }
        });
    }

    public void remane(int position, String name){
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        filePath += "/Recorder/"+name;

        File f = new File(filePath);

        if (f.exists() && !f.isDirectory()) {
            //file name is not unique, cannot rename file.
            Toast.makeText(mContext,
                    String.format(mContext.getString(R.string.toast_file_exists), name),
                    Toast.LENGTH_SHORT).show();

        }
        else {
            File oldFilePath = new File(databaseHandler.getItemAt(position).getFilePath());
            oldFilePath.renameTo(f);
            databaseHandler.renameItem(databaseHandler.getItemAt(position), name, filePath);
        }
    }



}
