package com.medella.android.list;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.Html;
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

import org.w3c.dom.Text;

public class HealthActivityAdapter extends ArrayAdapter<ActivityTable> {

    private static final String TAG = "HealthActivityAdapter";

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

    /*private static class ResultsViewHolder {
        protected TextView tvBmiResults;
        protected TextView tvPintResults;
        protected TextView tvWeightResults;
        protected TextView tvBodyTempResults;
        protected TextView tvSystolicResults;
        protected TextView tvDiastolicResults;
        protected TextView tvHrateResults;
    }*/

    public HealthActivityAdapter(Context context, int resource) {
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
            ActivityViewHolder activityViewHolder;
            //ResultsViewHolder resultsViewHolder;

            if (row == null) {
                //LayoutInflater inflater = LayoutInflater.from(mContext);
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                row = inflater.inflate(mResource, parent, false);
                activityViewHolder = new ActivityViewHolder();
                //resultsViewHolder = new ResultsViewHolder();

                // Initialize the views for ListActivity
                activityViewHolder.tvActivityTitle = (TextView) row.findViewById(R.id.activity_title_text);
                activityViewHolder.tvActivityDetails = (TextView) row.findViewById(R.id.activity_details_text);
                activityViewHolder.tvActivityDescription = (TextView) row.findViewById(R.id.activity_description_text);
                activityViewHolder.tvBmiStatus = (TextView) row.findViewById(R.id.bmi_status_text);
                activityViewHolder.tvLocation = (TextView) row.findViewById(R.id.activity_location_text);
                activityViewHolder.tvDateAdded = (TextView) row.findViewById(R.id.activity_date_added_text);
                activityViewHolder.cardView = row.findViewById(R.id.activity_card_view);

                // Initialize the views for ResultsActivity
                /*resultsViewHolder.tvBmiResults = (TextView)row.findViewById(R.id.txtBmiResult);
                resultsViewHolder.tvPintResults = (TextView)row.findViewById(R.id.txtPintResult);
                resultsViewHolder.tvWeightResults = (TextView)row.findViewById(R.id.txtWeightResult);
                resultsViewHolder.tvBodyTempResults = (TextView)row.findViewById(R.id.txtBtempResult);
                resultsViewHolder.tvSystolicResults = (TextView)row.findViewById(R.id.txtSystolicResult);
                resultsViewHolder.tvDiastolicResults = (TextView)row.findViewById(R.id.txtDiastolicResult);
                resultsViewHolder.tvHrateResults = (TextView)row.findViewById(R.id.txtHrateResult);*/

                row.setTag(activityViewHolder);
                //row.setTag(resultsViewHolder); //For ResultsActivity
            } else {
                activityViewHolder = (ActivityViewHolder) row.getTag();
                //resultsViewHolder = (ResultsViewHolder) row.getTag(); //For ResultsActivity
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
            activityViewHolder.tvActivityTitle.setText(currentTitle);
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
            activityViewHolder.tvActivityDetails.setText(activityDetails);
            if (currentBmi > 0 && currentBmi < 18.5) {
                activityViewHolder.tvBmiStatus.setText("BMI: " + String.valueOf(currentBmi) + " (Underweight)");
                activityViewHolder.tvBmiStatus.setTextColor(Color.parseColor("#22A7F2"));
            } else if (currentBmi >= 18.5 && currentBmi < 25) {
                activityViewHolder.tvBmiStatus.setText("BMI: " + String.valueOf(currentBmi) + " (Normal)");
                activityViewHolder.tvBmiStatus.setTextColor(Color.parseColor("#3ebc70"));
                activityViewHolder.tvBmiStatus.setTypeface(null, Typeface.BOLD);
            } else if (currentBmi >= 25 && currentBmi < 30) {
                activityViewHolder.tvBmiStatus.setText("BMI: " + String.valueOf(currentBmi) + " (Overweight)");
                activityViewHolder.tvBmiStatus.setTextColor(Color.parseColor("#F28622"));
            } else if (currentBmi >= 30) {
                activityViewHolder.tvBmiStatus.setText("BMI: " + String.valueOf(currentBmi) + " (Obese)");
                activityViewHolder.tvBmiStatus.setTextColor(Color.RED);
            } else {
                activityViewHolder.tvBmiStatus.setText("0");
                activityViewHolder.tvBmiStatus.setTextColor(Color.GRAY);
            }
            activityViewHolder.tvActivityDescription.setText(currentDescription + "\n");
            if (currentLocation != null) {
                activityViewHolder.tvLocation.setText(currentLocation);
                activityDetails.append("Location: " + currentLocation + "\n");
            } else {
                activityViewHolder.tvLocation.setText("No location");
            }
            String addedDate = currentItem.getCreatedAt().substring(0, 10) + " ";
            String addedTime = currentItem.getCreatedAt().substring(11, 19);
            activityViewHolder.tvDateAdded.setText("Created: " + addedDate + addedTime);

            /** When the user taps and holds a card view on ListActivity*/
            activityViewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
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
                                            .setMessage(Html.fromHtml("Are you sure you want to delete <b>" + currentTitle + "</b>? This activity will not be restored."))
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
