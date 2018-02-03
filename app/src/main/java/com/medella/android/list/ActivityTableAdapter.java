package com.medella.android.list;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
        protected TextView tvActivityTitle;
        protected TextView tvActivityDescription;
        protected TextView tvActivityDetails;
        protected TextView tvBmiStatus;
        protected TextView tvLocation;
        protected TextView tvDateAdded;
        protected View cardView;
    }

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


        try{

            ActivityViewHolder holder;

            if(row == null){
                LayoutInflater inflater = LayoutInflater.from(mContext);
                row = inflater.inflate(mResource, parent, false);
                holder = new ActivityViewHolder();

                holder.tvActivityTitle = (TextView)row.findViewById(R.id.activity_title_text);
                holder.tvActivityDetails = (TextView)row.findViewById(R.id.activity_details_text);
                holder.tvActivityDescription = (TextView)row.findViewById(R.id.activity_description_text);
                holder.tvBmiStatus = (TextView)row.findViewById(R.id.bmi_status_text);
                holder.tvLocation = (TextView)row.findViewById(R.id.activity_location_text);
                holder.tvDateAdded = (TextView)row.findViewById(R.id.activity_date_added_text);
                holder.cardView = row.findViewById(R.id.activity_card_view);

                row.setTag(holder);
            }
            else{
                holder = (ActivityViewHolder) row.getTag();
            }

            lastPosition = position;

                holder.tvActivityTitle.setText(currentItem.getActivityTitle());
                String acWeight = "Weight: "+String.valueOf(currentItem.getWeightLbs())+"lbs"+" ("+String.valueOf(currentItem.getWeightKg())+"kg"+")";
                String acPainInt = "Pain Intensity: "+String.valueOf(currentItem.getPainIntensity());
                String acMedication = "Medication: "+currentItem.getMedicationBrand()+" "+currentItem.getMedicationDosage();
                String acBodyTemp = "Body Temperature: "+String.valueOf(currentItem.getBodyTemperatureCelsius())+"C ("+String.valueOf(currentItem.getBodyTemperatureFahrenheit())+"F)";
                String acBloodPressure = "Body Pressure: "+String.valueOf(currentItem.getSystolic())+"/"+String.valueOf(currentItem.getDiastolic())+" mmHg";
                String acHeartRate = "Heart Rate: "+String.valueOf(currentItem.getHeartRate())+"bpm";
                holder.tvActivityDetails.setText(
                        acWeight+" | "
                        +acPainInt+" | "
                        +acMedication+" | "
                        +acBodyTemp+" | "
                        +acBloodPressure+" | "
                        +acHeartRate
                );
                if(currentItem.getBmi() > 0 && currentItem.getBmi() < 18.5){
                    holder.tvBmiStatus.setText("BMI: "+String.valueOf(currentItem.getBmi())+" (Underweight)");
                    holder.tvBmiStatus.setTextColor(Color.parseColor("#22A7F2"));
                }
                else if(currentItem.getBmi() >= 18.5 && currentItem.getBmi() < 25){
                    holder.tvBmiStatus.setText("BMI: "+String.valueOf(currentItem.getBmi())+" (Normal)");
                    holder.tvBmiStatus.setTextColor(Color.GREEN);
                    holder.tvBmiStatus.setTypeface(null, Typeface.BOLD);
                }
                else if(currentItem.getBmi() >= 25 && currentItem.getBmi() < 30){
                    holder.tvBmiStatus.setText("BMI: "+String.valueOf(currentItem.getBmi())+" (Overweight)");
                    holder.tvBmiStatus.setTextColor(Color.parseColor("#F28622"));
                }
                else if(currentItem.getBmi() >= 30){
                    holder.tvBmiStatus.setText("BMI: "+String.valueOf(currentItem.getBmi())+" (Obese)");
                    holder.tvBmiStatus.setTextColor(Color.RED);
                    holder.tvBmiStatus.setTypeface(null, Typeface.BOLD);
                }
                else{
                    holder.tvBmiStatus.setText("0");
                    holder.tvBmiStatus.setTextColor(Color.GRAY);
                    holder.tvBmiStatus.setTypeface(null, Typeface.NORMAL);
                }
                holder.tvActivityDescription.setText(currentItem.getDescription());
                holder.tvLocation.setText(currentItem.getLocation());
                String addedDate = currentItem.getCreatedAt().substring(0,10)+" ";
                String addedTime = currentItem.getCreatedAt().substring(11,19);
                holder.tvDateAdded.setText("Created: "+addedDate+addedTime);

                holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        ArrayList<String> entries = new ArrayList<String>();
                        entries.add("Share Activity");
                        entries.add("Edit Activity");
                        entries.add("Delete Activity");

                        final CharSequence[] items = entries.toArray(new CharSequence[entries.size()]);

                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("Choose one:");
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                if (item == 0) {
                                    String acTitle = currentItem.getActivityTitle()+"\n\n";
                                    String acWeight = "Weight: "+String.valueOf(currentItem.getWeightLbs())+"lbs"+" ("+String.valueOf(currentItem.getWeightKg())+"kg"+")"+"\n";
                                    String acMedication = "Medication: "+currentItem.getMedicationBrand()+" "+currentItem.getMedicationDosage()+"\n";
                                    String acBodyTemp = "Body Temperature: "+String.valueOf(currentItem.getBodyTemperatureCelsius())+"C ("+String.valueOf(currentItem.getBodyTemperatureFahrenheit())+"F)"+"\n";
                                    String acBloodPressure = "Body Pressure: "+String.valueOf(currentItem.getSystolic())+"/"+String.valueOf(currentItem.getDiastolic())+" mmHg"+"\n";
                                    String acHeartRate = "Heart Rate: "+String.valueOf(currentItem.getHeartRate())+"bpm"+"\n\n";
                                    String acDescription = currentItem.getDescription();

                                    Intent iShare = new Intent(Intent.ACTION_SEND);
                                    iShare.setType("text/plain");

                                    String shareBody = "Your body here"; //shareBody does not show up - possibly due to update?
                                    String shareSub = acTitle+acWeight+acMedication+acBodyTemp+acBloodPressure+acHeartRate+acDescription;

                                    iShare.putExtra(Intent.EXTRA_TEXT, shareBody);
                                    iShare.putExtra(Intent.EXTRA_TEXT, shareSub);
                                    mContext.startActivity(Intent.createChooser(iShare, "Share your health activity via"));
                                } if (item == 1) {
                                    Toast.makeText(mContext, "Edit/update not available", Toast.LENGTH_SHORT).show();
                                } else if (item == 2) {
                                    Toast.makeText(mContext, "Delete not available", Toast.LENGTH_SHORT).show();
                                }
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

                return row;
        }catch (IllegalArgumentException e){
            Log.e(TAG, "getView: IllegalArgumentException: " + e.getMessage() );
            return row;
        }

    }
}
