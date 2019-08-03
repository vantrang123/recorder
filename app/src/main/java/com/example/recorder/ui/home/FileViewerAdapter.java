package com.example.recorder.ui.home;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recorder.DatabaseHandler;
import com.example.recorder.R;
import com.example.recorder.data.RecordingItem;
import com.example.recorder.listeners.OnDatabaseChangedListeners;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
public class FileViewerAdapter extends RecyclerView.Adapter<FileViewerAdapter.RecordingsViewHolder> implements OnDatabaseChangedListeners {
    private static final String TAG = "FileViewerAdapter";
    Context context;
    RecordingItem recordingItem;
    LinearLayoutManager layoutManager;
    private int iD;
    private DatabaseHandler databaseHandler;
    private ItemListener listener;

    public FileViewerAdapter(Context context, LinearLayoutManager layoutManager,ItemListener itemListener){
        super();
        this.context = context;
        this.layoutManager = layoutManager;
        listener = itemListener;
        databaseHandler = new DatabaseHandler(context);
        databaseHandler.setChangedListeners(this);
    }

    @NonNull
    @Override
    public RecordingsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.item_file_viewer, viewGroup, false);

        context = viewGroup.getContext();

        return new RecordingsViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull final RecordingsViewHolder holder, int position) {
        recordingItem = getItem(position);
        long itemDuration = recordingItem.getLength();

        long minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(itemDuration) - TimeUnit.MINUTES.toSeconds(minutes);

        holder.vName.setText(recordingItem.getName());
        holder.vLength.setText(String.format("%02d:%02d", minutes, seconds));
        holder.vDateAdded.setText(
                DateUtils.formatDateTime(
                        context,
                        recordingItem.getTime(),
                        DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR
                )
        );

    }

    @Override
    public int getItemCount() {
        return databaseHandler.getCount();
    }

    private RecordingItem getItem(int position) {
        return databaseHandler.getItemAt(position);
    }

    @Override
    public void onNewDatabaseEntryAdded() {
        layoutManager.scrollToPosition(getItemCount()-1);
    }

    @Override
    public void onNewDatabaseEntryRemoved() {
        notifyItemRemoved(iD);
    }



    class RecordingsViewHolder extends RecyclerView.ViewHolder{
        protected TextView vName;
        protected TextView vLength;
        protected TextView vDateAdded;
        protected View cardView;
        public RecordingsViewHolder(@NonNull View itemView) {
            super(itemView);
            vName = (TextView)itemView.findViewById(R.id.file_name_text);
            vLength = (TextView)itemView.findViewById(R.id.file_length_text);
            vDateAdded = (TextView)itemView.findViewById(R.id.file_date_added_text);
            cardView = itemView.findViewById(R.id.card_view);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onOption(context,getLayoutPosition());
                    iD = getLayoutPosition();
                    return false;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*PlayItemDialog playItemDialog = new PlayItemDialog();
                    FragmentTransaction transaction =((FragmentActivity) context)
                            .getSupportFragmentManager()
                            .beginTransaction();
                    playItemDialog.show(transaction,"dialog_playback");*/
                    listener.onPlayBack(context, getItem(getLayoutPosition()));
                    iD = getLayoutPosition();
                }
            });
        }
    }
    interface ItemListener{
        void onOption(Context context,int id);
        void onPlayBack(Context context, RecordingItem item);
    }

}
