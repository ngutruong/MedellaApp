package com.medella.android.list;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import com.medella.android.R;

public class ActivityTableAdapter extends ArrayAdapter<ActivityTable> {

    private static final String TAG = "ActivityTableAdapter";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    /**
     * Holds variables in a View
     */
    private static class ActivityViewHolder {
        /*
        TextView title;
        ImageView image;*/
        protected TextView tvActivityTitle;
        protected TextView tvActivityDescription;
        protected TextView tvActivityDetails;
        protected TextView tvBmiStatus;
        protected TextView tvLocation;
        protected TextView tvDateAdded;
        protected View cardView;
    }

    /*
    public ActivityTableAdapter(Context context, int resource, ArrayList<ActivityTable> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }*/
    public ActivityTableAdapter(Context context, int resource) {
        super(context, resource);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final ActivityTable currentItem = getItem(position);

        //get the persons information
        //String title = getItem(position).getTitle();
        //String imgUrl = getItem(position).getImgURL();


        try{


        //create the view result for showing the animation
        //final View result;

        //ViewHolder object
        ActivityViewHolder holder;

        /*
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.cardTitle);
            holder.image = (ImageView) convertView.findViewById(R.id.cardImage);

            //result = convertView;

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
            //result = convertView;
        }*/

        if(row == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            row = inflater.inflate(mResource, parent, false);
            holder = new ActivityViewHolder();
            /*
            holder.title = (TextView) convertView.findViewById(R.id.cardTitle);
            holder.image = (ImageView) convertView.findViewById(R.id.cardImage);
            */
            holder.tvActivityTitle = (TextView)row.findViewById(R.id.activity_title_text);
            holder.tvActivityDetails = (TextView)row.findViewById(R.id.activity_details_text);
            holder.tvActivityDescription = (TextView)row.findViewById(R.id.activity_description_text);
            holder.tvBmiStatus = (TextView)row.findViewById(R.id.bmi_status_text);
            holder.tvLocation = (TextView)row.findViewById(R.id.activity_location_text);
            holder.tvDateAdded = (TextView)row.findViewById(R.id.activity_date_added_text);
            holder.cardView = row.findViewById(R.id.activity_card_view);

            //result = convertView;

            row.setTag(holder);
        }
        else{
            holder = (ActivityViewHolder) row.getTag();
            //result = convertView;
        }

        //Animation animation = AnimationUtils.loadAnimation(mContext,
        //        (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
        //result.startAnimation(animation);
        lastPosition = position;

        //holder.title.setText(title);
            holder.tvActivityTitle.setText(currentItem.getActivityTitle());
            String acWeight = "Weight: "+String.valueOf(currentItem.getWeightLbs())+"lbs"+" ("+String.valueOf(currentItem.getWeightKg())+"kg"+")";
            String acPainInt = "Pain Intensity: "+String.valueOf(currentItem.getPainIntensity());
            String acMedication = "Medication: "+currentItem.getMedicationBrand()+" "+currentItem.getMedicationDosage();
            String acBodyTemp = "Body Temperature: "+String.valueOf(currentItem.getBodyTemperatureCelsius())+"C ("+String.valueOf(currentItem.getBodyTemperatureFahrenheit())+"F)";
            String acBloodPressure = "Body Pressure: "+String.valueOf(currentItem.getSystolic())+"/"+String.valueOf(currentItem.getDiastolic())+" mm Hg";
            String acHeartRate = "Heart Rate: "+String.valueOf(currentItem.getHeartRate())+"bpm";
            holder.tvActivityDetails.setText(
                    acWeight+" | "
                    +acPainInt+" | "
                    +acMedication+" | "
                    +acBodyTemp+" | "
                    +acBloodPressure+" | "
                    +acHeartRate
            );
            holder.tvActivityDescription.setText(currentItem.getDescription());
            holder.tvLocation.setText(currentItem.getLocation());
            String addedDate = currentItem.getCreatedAt().substring(0,10)+" ";
            String addedTime = currentItem.getCreatedAt().substring(11,19);
            holder.tvDateAdded.setText("Created: "+addedDate+addedTime);
            /*holder.tvDateAdded.setText(DateUtils.formatDateTime(
                    mContext,
                    currentItem.getCreatedAt(),
                    DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR
            ));*/
            holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ArrayList<String> entries = new ArrayList<String>();
                    entries.add("Share Activity");
                    entries.add("Edit Activity");
                    entries.add("Delete Activity");

                    final CharSequence[] items = entries.toArray(new CharSequence[entries.size()]);


                    // File delete confirm
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Choose one:");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            /*if (item == 0) {
                                //shareActivityDialog(holder.getPosition());

                            } if (item == 1) {
                                //renameFileDialog(holder.getPosition());
                            } else if (item == 2) {
                                if(mContext instanceof ListActivity){
                                    ListActivity activity = (ListActivity) mContext;
                                    activity.deleteHealthActivity(currentItem);
                                }
                            }*/
                        }
                    });
                    builder.setCancelable(true);
                    builder.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();

                    return false;
                }
            });

        //create the imageloader object
        //ImageLoader imageLoader = ImageLoader.getInstance();

        //int defaultImage = mContext.getResources().getIdentifier("@drawable/image_failed",null,mContext.getPackageName());

        //create display options
        //DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
          //      .cacheOnDisc(true).resetViewBeforeLoading(true)
            //    .showImageForEmptyUri(defaultImage)
              //  .showImageOnFail(defaultImage)
                //.showImageOnLoading(defaultImage).build();

        //download and display image from url
        //imageLoader.displayImage(imgUrl, holder.image, options);

        //return convertView;
            return row;
        }catch (IllegalArgumentException e){
            Log.e(TAG, "getView: IllegalArgumentException: " + e.getMessage() );
            //return convertView;
            return row;
        }

    }

}
