package com.example.recorder.ui.home;

import android.content.Context;
import android.graphics.Path;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.recorder.R;
import com.example.recorder.data.RecordingItem;

public class FileViewerFragment extends Fragment implements FileViewerAdapter.ItemListener {
    private static final String TAG = "FileViewerFragment";
    private FileViewerAdapter viewerAdapter;
    private RecyclerView recyclerView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_viewer, container, false);
        setup(view);

        return view;
    }

    private void setup(View v) {
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        viewerAdapter = new FileViewerAdapter(getActivity(), layoutManager,this);
        recyclerView.setAdapter(viewerAdapter);
    }

    @Override
    public void onOption(Context context ,int id) {
        OptionItemDialog dialog = new OptionItemDialog(context, id);
        dialog.show(getFragmentManager(),"OptionItemDialog");
    }

    @Override
    public void onPlayBack(Context context, RecordingItem item) {
        PlayItemDialog dialog = new PlayItemDialog(context, item);
        dialog.show(getFragmentManager(), "FileViewerAdapter");
    }

}
