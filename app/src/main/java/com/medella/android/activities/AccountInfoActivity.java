package com.medella.android.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.medella.android.AccountStatus;
import com.medella.android.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

public class AccountInfoActivity extends AppCompatActivity {

    private EditText etEditName, etEditEmail, etEditBirthdate,
            etEditHeight, etEditFt, etEditIn;
    private TextView ftInDisplay, mCmInDisplay,
            tvEditFt, tvEditIn;
    private Button btnEditProfile, btnApplyChanges, btnCancelEdit;
    private CheckBox chkEditFtIn;
    private Spinner heightSpinner;
    private LinearLayout linEditHeight, linFtHeight, linInHeight;

    private float height2Ft, height2In;
    private boolean isEditHeightChecked;

    private String newProfileName, newEmail, newBirthdate, newHeightUnit;
    private float newWholeHeight, newHeightFt, newHeightIn;

    public Connection con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /**
         * Views before profile edit
         */
        etEditName = findViewById(R.id.etEditName); // Disable edit beforehand
        etEditEmail = findViewById(R.id.etEditEmail); // Disable edit beforehand
        etEditBirthdate = findViewById(R.id.etEditBirthdate); // Disable edit beforehand
        ftInDisplay = findViewById(R.id.tvEditFtIn); // Hidden during edit
        mCmInDisplay = findViewById(R.id.tvEditMCmIn); // Hidden during edit
        btnEditProfile = findViewById(R.id.btnEditProfile); //Hidden during edit

        /**
         * Views during profile edit
         * They will be hidden unless if user taps 'Edit Profile' button
         */
        btnApplyChanges = findViewById(R.id.btnApplyProfileChanges);
        btnCancelEdit = findViewById(R.id.btnCancelEditProfile);
        etEditHeight = findViewById(R.id.etEditHeight);
        etEditFt = findViewById(R.id.etEditFtHeight);
        etEditIn = findViewById(R.id.etEditInHeight);
        tvEditFt = findViewById(R.id.editFtText);
        tvEditIn = findViewById(R.id.editInText);
        chkEditFtIn = findViewById(R.id.chkEditFtIn);
        heightSpinner = findViewById(R.id.spEditHeight);

        linEditHeight = findViewById(R.id.linEditHeight);
        linFtHeight = findViewById(R.id.linEditFtHeight);
        linInHeight = findViewById(R.id.linEditInHeight);

        // Disable EditText views initially
        etEditName.setEnabled(false);
        etEditEmail.setEnabled(false);
        etEditBirthdate.setEnabled(false);

        // Set text to EditText and TextView views
        etEditName.setText(AccountStatus.getProfileName(getApplicationContext()));
        etEditEmail.setText(AccountStatus.getEmail(getApplicationContext()));
        String birthdateString = AccountStatus.getProfileBirthdate(getApplicationContext()).split(" ")[0];
        etEditBirthdate.setText(birthdateString);

        DecimalFormat df2 = new DecimalFormat("#.##");
        DecimalFormat df3 = new DecimalFormat("#.###");
        // Height (m, cm, in) display
        float heightMeters = AccountStatus.getHeightM(getApplicationContext());
        float heightCentimeters = AccountStatus.getHeightM(getApplicationContext()) * 100;
        float heightInches = (float)(39.37) * AccountStatus.getHeightM(getApplicationContext());
        String mCmInInfo = String.valueOf(df3.format(heightMeters)) + " m/" +
                String.valueOf(df2.format(heightCentimeters)) + " cm/" +
                String.valueOf(df2.format(heightInches)) + " in";
        mCmInDisplay.setText(mCmInInfo);
        // Height (ft. in.) display
        height2Ft = AccountStatus.getHeight2Ft(getApplicationContext());
        height2In = AccountStatus.getHeight2In(getApplicationContext());
        String ftInInfo = String.valueOf((int) height2Ft) + " ft " +
                String.valueOf(df2.format(height2In)) + " in";
        ftInDisplay.setText(ftInInfo);

        isEditHeightChecked = false;

        chkEditFtIn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    DecimalFormat df2 = new DecimalFormat("#.##");
                    DecimalFormat df3 = new DecimalFormat("#.###");
                    if(isChecked){
                        etEditHeight.setText(null);
                        linEditHeight.setVisibility(View.GONE);
                        linFtHeight.setVisibility(View.VISIBLE);
                        linInHeight.setVisibility(View.VISIBLE);
                        etEditFt.setText(String.valueOf((int) height2Ft));
                        etEditIn.setText(String.valueOf(df2.format(height2In)));

                        isEditHeightChecked = true;
                    }
                    else{
                        linEditHeight.setVisibility(View.VISIBLE);
                        float heightMeters = AccountStatus.getHeightM(getApplicationContext());
                        etEditHeight.setText(String.valueOf(df3.format(heightMeters)));
                        heightSpinner.setSelection(1);

                        etEditFt.setText(null);
                        etEditIn.setText(null);
                        linFtHeight.setVisibility(View.GONE);
                        linInHeight.setVisibility(View.GONE);

                        isEditHeightChecked = false;
                    }
                }
        });
    }

    public void goEditProfile(View view) {
        etEditName.setEnabled(true);
        etEditEmail.setEnabled(true);
        etEditBirthdate.setEnabled(true);
        ftInDisplay.setVisibility(View.GONE);
        mCmInDisplay.setVisibility(View.GONE);
        btnEditProfile.setVisibility(View.GONE);
        btnApplyChanges.setVisibility(View.VISIBLE);
        btnCancelEdit.setVisibility(View.VISIBLE);

        chkEditFtIn.setVisibility(View.VISIBLE);
        chkEditFtIn.setChecked(false);
        linEditHeight.setVisibility(View.VISIBLE);
        chkEditFtIn.setVisibility(View.VISIBLE);

        DecimalFormat df3 = new DecimalFormat("#.###");
        float heightMeters = AccountStatus.getHeightM(getApplicationContext());
        etEditHeight.setText(String.valueOf(df3.format(heightMeters)));
        heightSpinner.setSelection(1);

        isEditHeightChecked = false;
    }

    public void cancelEditProfile(View view) {
        etEditName.setEnabled(false);
        etEditEmail.setEnabled(false);
        etEditBirthdate.setEnabled(false);
        ftInDisplay.setVisibility(View.VISIBLE);
        mCmInDisplay.setVisibility(View.VISIBLE);
        btnEditProfile.setVisibility(View.VISIBLE);
        btnApplyChanges.setVisibility(View.GONE);
        btnCancelEdit.setVisibility(View.GONE);

        etEditName.setText(AccountStatus.getProfileName(getApplicationContext()));
        etEditEmail.setText(AccountStatus.getEmail(getApplicationContext()));
        String birthdateString = AccountStatus.getProfileBirthdate(getApplicationContext()).split(" ")[0];
        etEditBirthdate.setText(birthdateString);

        chkEditFtIn.setChecked(false);
        chkEditFtIn.setVisibility(View.GONE);
        linEditHeight.setVisibility(View.GONE);
        linFtHeight.setVisibility(View.GONE);
        linInHeight.setVisibility(View.GONE);
        etEditHeight.setText(null);
        etEditFt.setText(null);
        etEditIn.setText(null);

        isEditHeightChecked = false;
    }

    //This is a regular expression to validate user's e-mail address
    public static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );
    //This is a regular expression to validate birth date format
    public static final Pattern BIRTHDATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");

    public void applyProfileChanges(View view) {
        // Profile name
        String profileNameInput = etEditName.getText().toString();
        String previousProfileName = AccountStatus.getProfileName(getApplicationContext());
        // E-mail
        String profileEmailInput = etEditEmail.getText().toString();
        String previousEmail = AccountStatus.getEmail(getApplicationContext());
        // Birthdate
        String birthdateInput = etEditBirthdate.getText().toString();
        String previousBirthdate = AccountStatus.getProfileBirthdate(getApplicationContext()).split(" ")[0];
        // Height (cm, m, in)
        String wholeHeightInput = etEditHeight.getText().toString();
        float previousWholeHeightCm = AccountStatus.getHeightCm(getApplicationContext());
        float previousWholeHeightM = AccountStatus.getHeightM(getApplicationContext());
        float previousWholeHeightIn = AccountStatus.getHeightIn(getApplicationContext());
        // Height 2 (ft. in.)
        String heightFtInput = etEditFt.getText().toString();
        String heightInInput = etEditIn.getText().toString();

        StringBuilder errorMessage = new StringBuilder();

        /**
         * Profile name input
         */
        if(profileNameInput.isEmpty() || profileNameInput.equals(previousProfileName)){
            newProfileName = "N/A";
        }
        else{
            newProfileName = etEditName.getText().toString();
        }

        /**
         * Profile e-mail input
         */
        if(!EMAIL_PATTERN.matcher(profileEmailInput).matches()){
            errorMessage.append("- Please enter a valid e-mail.\n");
        }
        else if(profileEmailInput.isEmpty() || profileEmailInput.equals(previousEmail)){
            newEmail = "N/A";
        }
        else{
            newEmail = etEditEmail.getText().toString();
        }

        /**
         * Profile birth date input
         */
        String[] birthDateArray = birthdateInput.split("-");
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int birthYear = Integer.parseInt(birthDateArray[0]);
        int accountAge = currentYear - birthYear;
        if(BIRTHDATE_PATTERN.matcher(birthdateInput).matches() &&
                accountAge > 13 && accountAge < 111 && isDateValid(birthdateInput)){
            newBirthdate = etEditBirthdate.getText().toString();
        }
        else if(birthdateInput.isEmpty() || birthdateInput.equals(previousBirthdate)){
            newBirthdate = "N/A";
        }
        else{
            if(!BIRTHDATE_PATTERN.matcher(birthdateInput).matches()){
                errorMessage.append("- Birth date format does not match.\n");
            }
            else if(accountAge < 13){
                errorMessage.append("- You must be 13 years of age or older.\n");
            }
            else if(accountAge > 111){
                errorMessage.append("- Birth date is invalid.\n");
            }
            else if(!isDateValid(birthdateInput)){
                errorMessage.append("- Birth date is invalid.\n");
            }
        }

        /**
         * Profile height input
         */
        if(!chkEditFtIn.isChecked()){
            if(wholeHeightInput.isEmpty()){
                errorMessage.append("- Height should not be empty.");
            }
            else {
                if (heightSpinner.getSelectedItemId() == 0) {
                    if (Float.parseFloat(wholeHeightInput) < 50) {
                        errorMessage.append("- Height in cm. is not a valid input.\n");
                    }
                    else if(Float.parseFloat(wholeHeightInput) == previousWholeHeightCm){
                        newWholeHeight = 0;
                        newHeightUnit = "N/A";
                    }
                    else{
                        newWholeHeight = Float.parseFloat(wholeHeightInput);
                        newHeightUnit = "cm";
                    }
                }
                else if (heightSpinner.getSelectedItemId() == 1) {
                    if (Float.parseFloat(wholeHeightInput) < 0.5) {
                        errorMessage.append("- Height in m. is not a valid input.\n");
                    }
                    else if(Float.parseFloat(wholeHeightInput) == previousWholeHeightM){
                        newWholeHeight = 0;
                        newHeightUnit = "N/A";
                    }
                    else{
                        newWholeHeight = Float.parseFloat(wholeHeightInput);
                        newHeightUnit = "m";
                    }
                }
                else if (heightSpinner.getSelectedItemId() == 2) {
                    if (Float.parseFloat(wholeHeightInput) < 19.685) {
                        errorMessage.append("- Height in in. is not a valid input.\n");
                    }
                    else if(Float.parseFloat(wholeHeightInput) == previousWholeHeightIn){
                        newWholeHeight = 0;
                        newHeightUnit = "N/A";
                    }
                    else{
                        newWholeHeight = Float.parseFloat(wholeHeightInput);
                        newHeightUnit = "in";
                    }
                }
                newHeightFt = 0;
                newHeightIn = 0;
            }
        }
        else{
            if(heightFtInput.isEmpty() || heightInInput.isEmpty()){
                errorMessage.append("- Do not leave blank field for ft. or in.\n");
            }
            else{
                if(Float.parseFloat(heightFtInput) < 2){
                    errorMessage.append("- Feet input is invalid.\n");
                }
                else{
                    newHeightFt = Float.parseFloat(heightFtInput);
                }
                if(Float.parseFloat(heightInInput) >= 12){
                    errorMessage.append("- Inches input is invalid.\n");
                }
                else{
                    newHeightIn = Float.parseFloat(heightInInput);
                }
            }
            newWholeHeight = 0;
            newHeightUnit = "N/A";
        }

        /**
         * Execute profile update if all inputs are valid
         * Will not execute if inputs are invalid
         */
        if(errorMessage.toString().trim().length() > 0){
            AlertDialog.Builder errorDialog = new AlertDialog.Builder(this);
            errorDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            errorDialog.setTitle("Error")
                    .setMessage(errorMessage.toString())
                    .show();
            //errorMessage.setLength(0); // Reset errors?
        }
        else{
            UpdateAccountTask updateAccountTask = new UpdateAccountTask(newProfileName, newEmail,
                    newBirthdate, newWholeHeight, newHeightUnit, newHeightFt, newHeightIn);
            updateAccountTask.execute("");
        }
    }

    private static boolean isDateValid(String date)
    {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            df.setLenient(false);
            df.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public class UpdateAccountTask extends AsyncTask<String, String, String> {

        private final String mNameEdit;
        private final String mEmailEdit;
        private final String mBirthdateEdit;
        private final String mHeightUnit;
        private final float mHeightEdit;
        private final float mFtEdit;
        private final float mInEdit;
        String updateAccountMsg = "";
        Boolean isSuccess = false;

        UpdateAccountTask(String newName, String newEmail, String newBirthdate,
                          float heightEdit, String heightUnit, float ftEdit, float inEdit) {
            mNameEdit = newName;
            mEmailEdit = newEmail;
            mBirthdateEdit = newBirthdate;
            mHeightEdit = heightEdit;
            mHeightUnit = heightUnit;
            mFtEdit = ftEdit;
            mInEdit = inEdit;
        }

        protected void onPreExecute(){ }
        @Override
        protected void onPostExecute(String r){
            if(isSuccess){
                if(!mNameEdit.equals("N/A")){
                    AccountStatus.setProfileName(getApplicationContext(), mNameEdit);
                }

                if(!mEmailEdit.equals("N/A")){
                    AccountStatus.setEmail(getApplicationContext(), mEmailEdit);
                }

                if(!mBirthdateEdit.equals("N/A")){
                    AccountStatus.setProfileBirthdate(getApplicationContext(), mBirthdateEdit);
                }

                if(mHeightUnit.equals("cm")){
                    AccountStatus.setHeightCm(getApplicationContext(), mHeightEdit);
                    float cmToM = ConvertCentimeters.convertCmToM(mHeightEdit);
                    float cmToIn = ConvertCentimeters.convertCmToIn(mHeightEdit);
                    float cmToFt = ConvertFeetAndInches.ftinCentimeters_ft(mHeightEdit);
                    float cmToIn2 = ConvertFeetAndInches.ftinCentimeters_in(mHeightEdit);
                    AccountStatus.setHeightM(getApplicationContext(), cmToM);
                    AccountStatus.setHeightIn(getApplicationContext(), cmToIn);
                    AccountStatus.setHeight2Ft(getApplicationContext(), cmToFt);
                    AccountStatus.setHeight2In(getApplicationContext(), cmToIn2);
                }
                else if(mHeightUnit.equals("m")){
                    AccountStatus.setHeightM(getApplicationContext(), mHeightEdit);
                    float mToCm = ConvertMeters.convertMToCm(mHeightEdit);
                    float mToIn = ConvertMeters.convertMToIn(mHeightEdit);
                    float mToFt = ConvertFeetAndInches.ftinMeters_ft(mHeightEdit);
                    float mToIn2 = ConvertFeetAndInches.ftinMeters_in(mHeightEdit);
                    AccountStatus.setHeightCm(getApplicationContext(), mToCm);
                    AccountStatus.setHeightIn(getApplicationContext(), mToIn);
                    AccountStatus.setHeight2Ft(getApplicationContext(), mToFt);
                    AccountStatus.setHeight2In(getApplicationContext(), mToIn2);
                }
                else if(mHeightUnit.equals("in")){
                    AccountStatus.setHeightIn(getApplicationContext(), mHeightEdit);
                    float inToCm = ConvertInches.convertInToCm(mHeightEdit);
                    float inToM = ConvertInches.convertInToM(mHeightEdit);
                    float mToFt = ConvertFeetAndInches.ftinInches_ft(mHeightEdit);
                    float mToIn2 = ConvertFeetAndInches.ftinInches_in(mHeightEdit);
                    AccountStatus.setHeightCm(getApplicationContext(), inToCm);
                    AccountStatus.setHeightM(getApplicationContext(), inToM);
                    AccountStatus.setHeight2Ft(getApplicationContext(), mToFt);
                    AccountStatus.setHeight2In(getApplicationContext(), mToIn2);
                }
                else if(mHeightUnit.equals("N/A")){
                    AccountStatus.setHeight2Ft(getApplicationContext(), mFtEdit);
                    AccountStatus.setHeight2In(getApplicationContext(), mInEdit);
                    float ftInToCm = ConvertFeetAndInches.convertFtInToCm(mFtEdit, mInEdit);
                    float ftInToM = ConvertFeetAndInches.convertFtInToM(mFtEdit, mInEdit);
                    float ftInToIn = ConvertFeetAndInches.convertFtInToIn(mFtEdit, mInEdit);
                    AccountStatus.setHeightCm(getApplicationContext(), ftInToCm);
                    AccountStatus.setHeightM(getApplicationContext(), ftInToM);
                    AccountStatus.setHeightIn(getApplicationContext(), ftInToIn);
                }

                Intent iAccountInfo = new Intent(AccountInfoActivity.this, AccountInfoActivity.class);
                startActivity(iAccountInfo);
                Toast.makeText(getApplicationContext(), updateAccountMsg, Toast.LENGTH_LONG).show();
            }
            else{
                AlertDialog.Builder failDialog = new AlertDialog.Builder(AccountInfoActivity.this);
                failDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                failDialog.setTitle("Error").setMessage(updateAccountMsg);
                failDialog.show();
            }
        }

        @Override
        protected String doInBackground(String... params){
            try{
                con = connectionClass();
                if(con==null){
                    updateAccountMsg = "Please check your internet connection";
                }
                else{
                    String queryUpdateProfile = "update profiletable set ";

                    String pId = AccountStatus.getProfileId(getApplicationContext());
                    String queryId = "id = \'" + pId + "\'";

                    StringBuilder queryBuilder = new StringBuilder();

                    if(!mNameEdit.equals("N/A")) {
                        String queryName = "profile_name = \'" + mNameEdit + "\'";
                        if(queryBuilder.toString().trim().length() > 0)
                            queryBuilder.append("," + queryName);
                        else
                            queryBuilder.append(queryName);
                    }

                    if(!mEmailEdit.equals("N/A")){
                        String queryEmail = "email = \'" + mEmailEdit + "\'";
                        if(queryBuilder.toString().trim().length() > 0)
                            queryBuilder.append("," + queryEmail);
                        else
                            queryBuilder.append(queryEmail);
                    }

                    if(!mBirthdateEdit.equals("N/A")){
                        String queryBirthdate = "birthdate = \'" + mBirthdateEdit + "\'";
                        if(queryBuilder.toString().trim().length() > 0)
                            queryBuilder.append("," + queryBirthdate);
                        else
                            queryBuilder.append(queryBirthdate);
                    }

                    if(mHeightUnit.equals("N/A")){
                        String queryFt = "height2_ft = \'" + mFtEdit + "\'";
                        String queryIn2 = "height2_in = \'" + mInEdit + "\'";
                        String queryCm = "height_cm = \'" + ConvertFeetAndInches.convertFtInToCm(mFtEdit, mInEdit) + "\'";
                        String queryM = "height_m = \'" + ConvertFeetAndInches.convertFtInToM(mFtEdit, mInEdit) + "\'";
                        String queryIn = "height_in = \'" + ConvertFeetAndInches.convertFtInToIn(mFtEdit, mInEdit) + "\'";

                        if(queryBuilder.toString().trim().length() > 0) {
                            queryBuilder.append("," + queryFt);
                            queryBuilder.append("," + queryIn2);
                            queryBuilder.append("," + queryCm);
                            queryBuilder.append("," + queryM);
                            queryBuilder.append("," + queryIn);
                        }
                        else {
                            queryBuilder.append(queryFt);
                            queryBuilder.append("," + queryIn2);
                            queryBuilder.append("," + queryCm);
                            queryBuilder.append("," + queryM);
                            queryBuilder.append("," + queryIn);
                        }
                    }
                    else{
                        if(mHeightUnit.equals("cm")){
                            String queryCm = "height_cm = \'" + mHeightEdit + "\'";
                            String queryM = "height_m = \'" + ConvertCentimeters.convertCmToM(mHeightEdit) + "\'";
                            String queryIn = "height_in = \'" + ConvertCentimeters.convertCmToIn(mHeightEdit) + "\'";
                            String queryFt = "height2_ft = \'" + ConvertFeetAndInches.ftinCentimeters_ft(mHeightEdit) + "\'";
                            String queryIn2 = "height2_in = \'" + ConvertFeetAndInches.ftinCentimeters_in(mHeightEdit) + "\'";

                            if(queryBuilder.toString().trim().length() > 0){
                                queryBuilder.append("," + queryCm);
                                queryBuilder.append("," + queryM);
                                queryBuilder.append("," + queryIn);
                                queryBuilder.append("," + queryFt);
                                queryBuilder.append("," + queryIn2);
                            }
                            else{
                                queryBuilder.append(queryCm);
                                queryBuilder.append("," + queryM);
                                queryBuilder.append("," + queryIn);
                                queryBuilder.append("," + queryFt);
                                queryBuilder.append("," + queryIn2);
                            }
                        }
                        else if(mHeightUnit.equals("m")){
                            String queryM = "height_m = \'" + mHeightEdit + "\'";
                            String queryCm = "height_cm = \'" + ConvertMeters.convertMToCm(mHeightEdit) + "\'";
                            String queryIn = "height_in = \'" + ConvertMeters.convertMToIn(mHeightEdit) + "\'";
                            String queryFt = "height2_ft = \'" + ConvertFeetAndInches.ftinMeters_ft(mHeightEdit) + "\'";
                            String queryIn2 = "height2_in = \'" + ConvertFeetAndInches.ftinMeters_in(mHeightEdit) + "\'";

                            if(queryBuilder.toString().trim().length() > 0) {
                                queryBuilder.append("," + queryM);
                                queryBuilder.append("," + queryCm);
                                queryBuilder.append("," + queryIn);
                                queryBuilder.append("," + queryFt);
                                queryBuilder.append("," + queryIn2);
                            }
                            else {
                                queryBuilder.append(queryM);
                                queryBuilder.append("," + queryCm);
                                queryBuilder.append("," + queryIn);
                                queryBuilder.append("," + queryFt);
                                queryBuilder.append("," + queryIn2);
                            }
                        }
                        else if(mHeightUnit.equals("in")){
                            String queryIn = "height_in = \'" + mHeightEdit + "\'";
                            String queryCm = "height_cm = \'" + ConvertInches.convertInToCm(mHeightEdit) + "\'";
                            String queryM = "height_m = \'" + ConvertInches.convertInToM(mHeightEdit) + "\'";
                            String queryFt = "height2_ft = \'" + ConvertFeetAndInches.ftinInches_ft(mHeightEdit) + "\'";
                            String queryIn2 = "height2_in = \'" + ConvertFeetAndInches.ftinInches_in(mHeightEdit) + "\'";

                            if(queryBuilder.toString().trim().length() > 0) {
                                queryBuilder.append("," + queryIn);
                                queryBuilder.append("," + queryCm);
                                queryBuilder.append("," + queryM);
                                queryBuilder.append("," + queryFt);
                                queryBuilder.append("," + queryIn2);
                            }
                            else {
                                queryBuilder.append(queryIn);
                                queryBuilder.append("," + queryCm);
                                queryBuilder.append("," + queryM);
                                queryBuilder.append("," + queryFt);
                                queryBuilder.append("," + queryIn2);
                            }
                        }
                    }

                    String query = queryUpdateProfile + queryBuilder.toString() + " where " + queryId;
                    Statement stmt = con.createStatement();
                    stmt.executeUpdate(query);
                    updateAccountMsg = "Account info has been updated.";
                    isSuccess = true;
                    con.close();
                }
            } catch (SQLException e) {
                updateAccountMsg = "We hit a snag. Please check back later.";
                isSuccess = false;
                e.printStackTrace();
            }
            return updateAccountMsg;
        }
    }

    public Connection connectionClass(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String connectionURL = null;
        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connectionURL = "jdbc:jtds:sqlserver://medellapp.database.windows.net:1433;DatabaseName=MedellaData;user=MedellaAdmin@medellapp;password=C0ntrolHe@lth;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
            connection = DriverManager.getConnection(connectionURL);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    private static class ConvertCentimeters {
        static Float convertCmToM(Float cm){
            return (float)(cm / 100);
        }
        static Float convertCmToIn(Float cm){
            return (float)(cm * 0.3937);
        }
    }
    private static class ConvertInches {
        static Float convertInToM(Float in){
            return (float)((in/0.3937)/100);
        }
        static Float convertInToCm(Float in){
            return (float)(in / 0.3937);
        }
    }
    private static class ConvertMeters {
        static Float convertMToCm(Float m){
            return m * 100;
        }
        static Float convertMToIn(Float m) {
            return (float)((m*100)*0.3937);
        }
    }
    private static class ConvertFeetAndInches {
        static Float convertFtInToCm(Float ft, Float in){
            Float ftCm = (float)(ft * 30.48);
            Float inCm = new ConvertInches().convertInToCm(in);
            return ftCm + inCm;
        }
        static Float convertFtInToM(Float ft, Float in){
            Float ftM = (float)(ft * 0.3048);
            Float inM = new ConvertInches().convertInToM(in);
            return ftM + inM;
        }
        static Float convertFtInToIn(Float ft, Float in){
            Float ftIn = ft * 12;
            return ftIn + in;
        }
        // Reverse
        static Float ftinCentimeters_ft(Float cm){
            String feetDecimal = String.valueOf(cm/30.48);
            return Float.valueOf(feetDecimal.substring(0, feetDecimal.indexOf(".")));
        }
        static Float ftinCentimeters_in(Float cm){
            String feetDecimal = String.valueOf(cm/30.48);
            Float feetAfterDecimalPoint = Float.valueOf(feetDecimal.substring(feetDecimal.indexOf(".")));
            return feetAfterDecimalPoint * 12;
        }
        static Float ftinMeters_ft(Float m){
            String feetDecimal = String.valueOf(m/0.3048);
            return Float.valueOf(feetDecimal.substring(0, feetDecimal.indexOf(".")));
        }
        static Float ftinMeters_in(Float m){
            String feetDecimal = String.valueOf(m/0.3048);
            Float feetAfterDecimalPoint = Float.valueOf(feetDecimal.substring(feetDecimal.indexOf(".")));
            return feetAfterDecimalPoint * 12;
        }
        static Float ftinInches_ft(Float in){
            String feetDecimal = String.valueOf(in/12);
            return Float.valueOf(feetDecimal.substring(0, feetDecimal.indexOf(".")));
        }
        static Float ftinInches_in(Float in){
            Float wholeInches = ftinInches_ft(in) * 12;
            return in - wholeInches;
        }
    }
}
