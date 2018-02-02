package com.medella.android.database;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.medella.android.R;
import com.medella.android.activities.HealthActivity;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ActivityTableAdapter1 extends ArrayAdapter<ActivityTable> {

    /**
     * Adapter context
     */
    Context mContext;

    /**
     * Adapter View layout
     */
    int mLayoutResourceId;

    public ActivityTableAdapter1(Context context, int layoutResourceId) {
        super(context, layoutResourceId);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
    }

    /**
     * Returns the view for a specific item on the list
     * NEEDS SOME CHANGE - APPLY TO LIST
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        final ActivityTable currentItem = getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);
        }

        row.setTag(currentItem);
        //final CheckBox checkBox = (CheckBox) row.findViewById(R.id.checkToDoItem);
        //checkBox.setText(currentItem.getActivityTitle()+"\n"+currentItem.getDescription()+"\n"+currentItem.getWeightLbs());
        //checkBox.setChecked(false);
        //checkBox.setEnabled(true);

        /*checkBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                /*if (checkBox.isChecked()) {
                    checkBox.setEnabled(false);
                    if (mContext instanceof HealthActivity) {
                        HealthActivity activity = (HealthActivity) mContext;
                        //activity.checkItem(currentItem);
                    }
                }*/
            //}
        //});

        return row;
    }

}