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
import com.medella.android.activities.ListActivity;

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

        try {

            ActivityViewHolder holder;

            if (row == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                row = inflater.inflate(mResource, parent, false);
                holder = new ActivityViewHolder();

                // Initialize the views for ListActivity
                holder.tvActivityTitle = (TextView) row.findViewById(R.id.activity_title_text);
                holder.tvActivityDetails = (TextView) row.findViewById(R.id.activity_details_text);
                holder.tvActivityDescription = (TextView) row.findViewById(R.id.activity_description_text);
                holder.tvBmiStatus = (TextView) row.findViewById(R.id.bmi_status_text);
                holder.tvLocation = (TextView) row.findViewById(R.id.activity_location_text);
                holder.tvDateAdded = (TextView) row.findViewById(R.id.activity_date_added_text);
                holder.cardView = row.findViewById(R.id.activity_card_view);

                row.setTag(holder);
            } else {
                holder = (ActivityViewHolder) row.getTag();
            }

            lastPosition = position;

            final String currentTitle = currentItem.getActivityTitle();
            final StringBuilder activityDetails = new StringBuilder();
            String acMedication;
            String currentMedBrand = currentItem.getMedicationBrand();
            String currentMedDosage = currentItem.getMedicationDosage();
            String acBodyTemp;
            float currentTempCels = currentItem.getBodyTemperatureCelsius();
            float currentTempFahr = currentItem.getBodyTemperatureFahrenheit();
            String acBloodPressure;
            float currentSystolic = currentItem.getSystolic();
            float currentDiastolic = currentItem.getDiastolic();
            String acHeartRate;
            float currentHeartRate = currentItem.getHeartRate();
            float currentBmi = currentItem.getBmi();
            final String currentLocation = currentItem.getLocation();
            final String currentDescription = currentItem.getDescription();

            /** Create a card view for ListActivity */
            holder.tvActivityTitle.setText(currentTitle);
            String acWeight = "Weight: " + String.valueOf(currentItem.getWeightLbs()) + "lbs" + " (" + String.valueOf(currentItem.getWeightKg()) + "kg" + ")\n";
            activityDetails.append(acWeight);
            String acPainInt = "Pain Intensity: " + String.valueOf(currentItem.getPainIntensity()) + "\n";
            activityDetails.append(acPainInt);
            if (currentMedBrand != null && currentMedDosage != null) {
                acMedication = "Medication: " + currentItem.getMedicationBrand() + " " + currentItem.getMedicationDosage() + "\n";
                activityDetails.append(acMedication);
            }
            if (currentTempCels > 0 || currentTempFahr > 0) {
                acBodyTemp = "Body Temperature: " + String.valueOf(currentTempCels) + "C (" + String.valueOf(currentTempFahr) + "F)\n";
                activityDetails.append(acBodyTemp);
            }
            if (currentSystolic > 0 || currentDiastolic > 0) {
                acBloodPressure = "Body Pressure: " + String.valueOf(currentSystolic) + "/" + String.valueOf(currentDiastolic) + " mmHg\n";
                activityDetails.append(acBloodPressure);
            }
            if (currentHeartRate > 0) {
                acHeartRate = "Heart Rate: " + String.valueOf(currentHeartRate) + "bpm\n";
                activityDetails.append(acHeartRate);
            }
            holder.tvActivityDetails.setText(activityDetails);
            if (currentBmi > 0 && currentBmi < 18.5) {
                holder.tvBmiStatus.setText("BMI: " + String.valueOf(currentBmi) + " (Underweight)");
                holder.tvBmiStatus.setTextColor(Color.parseColor("#22A7F2"));
            } else if (currentBmi >= 18.5 && currentBmi < 25) {
                holder.tvBmiStatus.setText("BMI: " + String.valueOf(currentBmi) + " (Normal)");
                holder.tvBmiStatus.setTextColor(Color.GREEN);
                holder.tvBmiStatus.setTypeface(null, Typeface.BOLD);
            } else if (currentBmi >= 25 && currentBmi < 30) {
                holder.tvBmiStatus.setText("BMI: " + String.valueOf(currentBmi) + " (Overweight)");
                holder.tvBmiStatus.setTextColor(Color.parseColor("#F28622"));
            } else if (currentBmi >= 30) {
                holder.tvBmiStatus.setText("BMI: " + String.valueOf(currentBmi) + " (Obese)");
                holder.tvBmiStatus.setTextColor(Color.RED);
                holder.tvBmiStatus.setTypeface(null, Typeface.BOLD);
            } else {
                holder.tvBmiStatus.setText("0");
                holder.tvBmiStatus.setTextColor(Color.GRAY);
                holder.tvBmiStatus.setTypeface(null, Typeface.NORMAL);
            }
            holder.tvActivityDescription.setText(currentDescription + "\n");
            if (currentLocation != null) {
                holder.tvLocation.setText(currentLocation);
                activityDetails.append("Location: " + currentLocation + "\n");
            } else {
                holder.tvLocation.setText("No location");
            }
            String addedDate = currentItem.getCreatedAt().substring(0, 10) + " ";
            String addedTime = currentItem.getCreatedAt().substring(11, 19);
            holder.tvDateAdded.setText("Created: " + addedDate + addedTime);

            /** When the user taps and holds a card view on ListActivity*/
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
                                Intent iShare = new Intent(Intent.ACTION_SEND);
                                iShare.setType("text/plain");

                                String shareBody = currentTitle + "\n" + activityDetails.toString() + "\n" + currentDescription;

                                iShare.putExtra(Intent.EXTRA_SUBJECT, currentTitle);
                                iShare.putExtra(Intent.EXTRA_TEXT, shareBody);
                                mContext.startActivity(Intent.createChooser(iShare, "Share your health activity via"));
                            }
                            if (item == 1) {
                                Toast.makeText(mContext, "Edit/update not available", Toast.LENGTH_SHORT).show();
                            } else if (item == 2) {
                                if(mContext instanceof ListActivity) {
                                    final ListActivity activity = (ListActivity) mContext;
                                    AlertDialog.Builder confirmDeleteDialog = new AlertDialog.Builder(mContext); //Create AlertDialog to confirm deletion

                                    // Will delete Health Activity if user taps "Yes"
                                    confirmDeleteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            activity.deleteHealthActivity(currentItem);
                                            Toast.makeText(mContext, currentTitle + " has been deleted", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    // Will not delete Health Activity if user taps "No"
                                    confirmDeleteDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                    confirmDeleteDialog.setTitle("Confirm Delete")
                                            .setMessage("Are you sure you want to delete this? It will not be retrieved.")
                                            .show();
                                }
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
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "getView: IllegalArgumentException: " + e.getMessage());
            return row;
        }

    }
}
