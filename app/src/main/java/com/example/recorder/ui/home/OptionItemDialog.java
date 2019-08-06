package com.example.recorder.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recorder.DatabaseHandler;
import com.example.recorder.R;
import com.example.recorder.listeners.OnDatabaseChangedListeners;

import java.io.File;

@SuppressLint("ValidFragment")
public class OptionItemDialog extends DialogFragment {
    private static final String TAG = "OptionItemDialog";
    private TextView title;
    private TextView rename;
    private TextView delete;
    private TextView cancel;
    private int id;
    private Context mContext;
    private DatabaseHandler databaseHandler;
    public OptionItemDialog(Context context,int position){
        id =  position;
        mContext = context;
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

        View view = inflater.inflate(R.layout.dialog_change_item,container,false);
        setup(view);

        return view;
    }

    public void setup(View v) {
        title = (TextView)v.findViewById(R.id.tv_title_option);
        rename = (TextView)v.findViewById(R.id.tv_rename);
        delete = (TextView)v.findViewById(R.id.tv_delete);
        cancel = (TextView)v.findViewById(R.id.tv_cancel);
        title.setText(R.string.dialog_title_options);
        rename.setText(R.string.dialog_title_rename);
        delete.setText(R.string.dialog_title_delete);
        cancel.setText(R.string.dialog_button_cancel);

        rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
                remaneFile(mContext, id);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFile(mContext,id);
                getDialog().dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

    }

    public void remaneFile(Context context, int position) {
        RemaneDialog remaneDialog = new RemaneDialog(context, position);
        remaneDialog.show(getFragmentManager(), "RemaneDialog");
    }


    public void deleteFile(Context context, int position) {
        File file = new File(databaseHandler.getItemAt(position).getFilePath());
        file.delete();
        Toast.makeText(context,String.format(context.getString(R.string.toast_file_delete),
                databaseHandler.getItemAt(position).getName()),Toast.LENGTH_SHORT).show();
        databaseHandler.removeItemWithId(databaseHandler.getItemAt(position).getId());

    }

}
