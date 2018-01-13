package com.medella.android.fragments;


import android.os.Bundle;
import android.os.FileObserver;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.medella.android.R;
import com.medella.android.adapters.PhotoAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoFragment extends Fragment {
    private static final String ARG_POSITION = "position";
    private static final String LOG_TAG = "PhotoFragment";

    private int position;

    // PhotoAdapter HAS NOT BEEN CREATED YET
    private PhotoAdapter mPhotoAdapter;

    public static PhotoFragment newInstance(int position) {
        // This method is used to SHOW File Viewer fragment
        // Same lines of codes from RecordFragment
        PhotoFragment vm = new PhotoFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        vm.setArguments(b);

        return vm;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
        observer.startWatching();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo, container, false);

        RecyclerView mRecyclerView = (RecyclerView) v.findViewById(R.id.photoRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        //newest to oldest order (database stores from oldest to newest)
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);

        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mPhotoAdapter = new PhotoAdapter(getActivity(), llm);
        mRecyclerView.setAdapter(mPhotoAdapter);

        return v;
    }

    FileObserver observer =
            new FileObserver(android.os.Environment.getExternalStorageDirectory().toString()
                    + "/Photos") {
                // set up a file observer to watch this directory on sd card
                @Override
                public void onEvent(int event, String file) {
                    if(event == FileObserver.DELETE){
                        // user deletes a recording file out of the app

                        String filePath = android.os.Environment.getExternalStorageDirectory().toString()
                                + "/Photos" + file + "]";

                        Log.d(LOG_TAG, "File deleted ["
                                + android.os.Environment.getExternalStorageDirectory().toString()
                                + "/Photos" + file + "]");

                        // remove file from database and recyclerview
                        mPhotoAdapter.removeOutOfApp(filePath);
                    }
                }
            };

    // NOT SHOWN IN FileViewerFragment - remove it?
    public PhotoFragment() {
        // Required empty public constructor
    }

/*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photo, container, false);
    }*/

}
