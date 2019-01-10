package com.medella.android.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.medella.android.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

public class ProfileActivity extends AppCompatActivity {

    private EditText nameInput, emailInput, passwordInput, confirmPassword, securityAnswer1,
    securityAnswer2, securityAnswer3, birthdateInput, heightInput, feetInput, inchesInput;
    private Spinner heightSpin, spSecurityQuestion1, spSecurityQuestion2, spSecurityQuestion3;
    private TextView feetLabel, inchesLabel;
    private View registerProgress;

    private boolean isHeightChecked;
    
    private Connection con;
    private RegisterTask mRegisterTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nameInput = findViewById(R.id.txtNameEdit);
        emailInput = findViewById(R.id.txtEmailEdit);
        passwordInput = findViewById(R.id.txtCreatePassword);
        confirmPassword = findViewById(R.id.txtConfirmEdit);
        spSecurityQuestion1 = findViewById(R.id.sq1Spinner);
        spSecurityQuestion2 = findViewById(R.id.sq2Spinner);
        spSecurityQuestion3 = findViewById(R.id.sq3Spinner);
        securityAnswer1 = findViewById(R.id.txtSq1Answer);
        securityAnswer2 = findViewById(R.id.txtSq2Answer);
        securityAnswer3 = findViewById(R.id.txtSq3Answer);
        birthdateInput = findViewById(R.id.txtBirthdate);
        heightInput = findViewById(R.id.txtHeight);
        feetInput = findViewById(R.id.txtFeet);
        inchesInput = findViewById(R.id.txtInches);
        heightSpin = findViewById(R.id.heightSpinner);
        feetLabel = findViewById(R.id.feetText);
        inchesLabel = findViewById(R.id.inchesText);
        CheckBox feetAndInches = findViewById(R.id.chkFeetInches);

        isHeightChecked = false;

        feetInput.setVisibility(View.GONE);
        inchesInput.setVisibility(View.GONE);
        feetLabel.setVisibility(View.GONE);
        inchesLabel.setVisibility(View.GONE);

        registerProgress = findViewById(R.id.registerProgressBar);

        feetAndInches.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    heightInput.setVisibility(View.GONE);
                    heightSpin.setVisibility(View.GONE);

                    feetInput.setVisibility(View.VISIBLE);
                    inchesInput.setVisibility(View.VISIBLE);
                    feetLabel.setVisibility(View.VISIBLE);
                    inchesLabel.setVisibility(View.VISIBLE);

                    heightInput.setText("");

                    isHeightChecked = true;
                }
                else{
                    heightInput.setVisibility(View.VISIBLE);
                    heightSpin.setVisibility(View.VISIBLE);

                    feetInput.setVisibility(View.GONE);
                    inchesInput.setVisibility(View.GONE);
                    feetLabel.setVisibility(View.GONE);
                    inchesLabel.setVisibility(View.GONE);

                    feetInput.setText("");
                    inchesInput.setText("");

                    isHeightChecked = false;
                }
            }
        });
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

    private static String collectErrors = "";

    public void nextClick(View view) {
        String checkNameInput = nameInput.getText().toString();
        String checkEmailInput = emailInput.getText().toString();
        String checkPasswordInput = passwordInput.getText().toString();
        String checkConfirmInput = confirmPassword.getText().toString();
        String checkAnswer1Input = securityAnswer1.getText().toString();
        String checkAnswer2Input = securityAnswer2.getText().toString();
        String checkAnswer3Input = securityAnswer3.getText().toString();
        String checkBirthdateInput = birthdateInput.getText().toString();
        String checkHeightInput = heightInput.getText().toString();
        String checkFeetInput = feetInput.getText().toString();
        String checkInchesInput = inchesInput.getText().toString();

        AlertDialog.Builder errorDialog = new AlertDialog.Builder(this);
        errorDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        try {
            /** NAME VALIDATION IN PROFILE PAGE */
            if(checkNameInput.trim().length() == 0){
                collectErrors+="- Name is empty.\n";
            }

            /**  EMAIL VALIDATION IN PROFILE PAGE -- incomplete */
            if(checkEmailInput.trim().length() == 0){
                collectErrors+="- E-mail address is empty.\n";
            }
            else if(!EMAIL_PATTERN.matcher(checkEmailInput).matches()){
                collectErrors+="- E-mail address is not valid.\n";
            }

            /** PASSWORD VALIDATION IN PROFILE PAGE */
            if(checkPasswordInput.trim().length() == 0){
                collectErrors+="- Password is empty.\n";
            }
            else if (checkPasswordInput.trim().length() < 8 && checkPasswordInput.trim().length() > 0) {
                collectErrors+="- Password is less than 8 characters.\n";
            }

            if(checkConfirmInput.trim().length() == 0){
                collectErrors+="- Confirm Password is empty.\n";
            }
            else if (checkConfirmInput.trim().length() < 8 && checkConfirmInput.trim().length() > 0) {
                collectErrors+="- Confirm Password is less than 8 characters.\n";
            }

            if(!checkPasswordInput.equals(checkConfirmInput) || !checkConfirmInput.equals(checkPasswordInput)) {
                collectErrors+="- Passwords do not match.\n";
            }

            /** SECURITY ANSWER VALIDATION IN PROFILE PAGE */
            if(checkAnswer1Input.trim().isEmpty()){
                collectErrors+="- Answer for Security Question 1 is empty.\n";
            }
            else if (checkAnswer1Input.trim().length() < 4 && checkAnswer1Input.trim().length() > 0) {
                collectErrors+="- Answer for Security Question 1 is less than 4 characters.\n";
            }
            if(checkAnswer2Input.trim().isEmpty()){
                collectErrors+="- Answer for Security Question 2 is empty.\n";
            }
            else if (checkAnswer2Input.trim().length() < 4 && checkAnswer2Input.trim().length() > 0) {
                collectErrors+="- Answer for Security Question 2 is less than 4 characters.\n";
            }
            if(checkAnswer3Input.trim().isEmpty()){
                collectErrors+="- Answer for Security Question 3 is empty.\n";
            }
            else if (checkAnswer3Input.trim().length() < 4 && checkAnswer3Input.trim().length() > 0) {
                collectErrors+="- Answer for Security Question 4 is less than 4 characters.\n";
            }

            /** BIRTH DATE VALIDATION IN PROFILE PAGE */
            if(checkBirthdateInput.isEmpty()){
                collectErrors+="- Birth date is empty.\n";
            }
            else if(!BIRTHDATE_PATTERN.matcher(checkBirthdateInput).matches()){
                collectErrors+="- Birth date format does not match.\n";
            }
            else {
                String[] birthDateArray = checkBirthdateInput.split("-");
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                int birthYear = Integer.parseInt(birthDateArray[0]);
                if(currentYear - birthYear < 13) {
                    collectErrors+="- You must be 13 years of age or older to register.\n";
                }
                else if(currentYear - birthYear > 111){
                    collectErrors+="- Birth date is invalid.\n";
                }
                else{
                    if(!isDateValid(checkBirthdateInput)){
                        collectErrors+="- Birth date is invalid.\n";
                    }
                }
            }

            /** HEIGHT VALIDATION IN PROFILE PAGE */
            if(!isHeightChecked) {
                if(checkHeightInput.isEmpty()){
                    collectErrors += "- Height cannot be empty.";
                }
                else {
                    if (heightSpin.getSelectedItem().toString().equals("cm.") && Double.parseDouble(checkHeightInput) < 50) {
                        collectErrors += "- " + checkHeightInput + " cm. is not a valid height.";
                    } else if (heightSpin.getSelectedItem().toString().equals("m.") && Double.parseDouble(checkHeightInput) < 0.5) {
                        collectErrors += "- " + checkHeightInput + " m. is not a valid height.";
                    } else if (heightSpin.getSelectedItem().toString().equals("in.") && Double.parseDouble(checkHeightInput) < 19.685) {
                        collectErrors += "- " + checkHeightInput + " in. is not a valid height.";
                    }
                }
            }
            else {
                if(checkFeetInput.isEmpty()){
                    collectErrors += "- Feet cannot be empty.\n";
                }
                else if(Double.parseDouble(checkFeetInput) < 2){
                    collectErrors += "- Feet is invalid.\n";
                }
                if(checkInchesInput.isEmpty()){
                    collectErrors += "- Inches cannot be empty. If inches are 0, please enter 0.";
                }
                else if(Double.parseDouble(checkInchesInput) >= 12){
                    collectErrors += "- Inches cannot be 12 or higher.";
                }
            }
        }
        catch (Exception ex){
            errorDialog.setMessage("Please enter a valid input.");
        }

        if(collectErrors.trim().length() > 0) {
            //isSuccess = false;

            errorDialog.setTitle("Error")
                    .setMessage("Registration is not complete due to the following errors:\n\n" + collectErrors)
                    .show();
            collectErrors = ""; //Reset errors
        }
        else{
            Double getHeightCm = 0.0;
            Double getHeightM = 0.0;
            Double getHeightIn = 0.0;
            Double ftinFeet = 0.0;
            Double ftinInches = 0.0;

            if(!isHeightChecked){
                Double heightFromInput = Double.parseDouble(heightInput.getText().toString());
                if(heightSpin.getSelectedItemId() == 0){
                    getHeightCm = heightFromInput;
                    getHeightM = new ConvertCentimeters().convertCmToM(heightFromInput);
                    getHeightIn = new ConvertCentimeters().convertCmToIn(heightFromInput);
                    ftinFeet = new ConvertFeetAndInches().ftinCentimeters_ft(heightFromInput);
                    ftinInches = new ConvertFeetAndInches().ftinCentimeters_in(heightFromInput);
                }
                else if(heightSpin.getSelectedItemId() == 1){
                    getHeightM = heightFromInput;
                    getHeightCm = new ConvertMeters().convertMToCm(heightFromInput);
                    getHeightIn = new ConvertMeters().convertMToIn(heightFromInput);
                    ftinFeet = new ConvertFeetAndInches().ftinMeters_ft(heightFromInput);
                    ftinInches = new ConvertFeetAndInches().ftinMeters_in(heightFromInput);
                }
                else if(heightSpin.getSelectedItemId() == 2){
                    getHeightIn = heightFromInput;
                    getHeightCm = new ConvertInches().convertInToCm(heightFromInput);
                    getHeightM = new ConvertInches().convertInToM(heightFromInput);
                    ftinFeet = new ConvertFeetAndInches().ftinInches_ft(heightFromInput);
                    ftinInches = new ConvertFeetAndInches().ftinInches_in(heightFromInput);
                }
            }
            else{
                Double feetFromInput = Double.parseDouble(feetInput.getText().toString());
                Double inchesFromInput = Double.parseDouble(inchesInput.getText().toString());
                ftinFeet = feetFromInput;
                ftinInches = inchesFromInput;
                getHeightCm = new ConvertFeetAndInches().convertFtInToCm(feetFromInput, inchesFromInput);
                getHeightM = new ConvertFeetAndInches().convertFtInToM(feetFromInput, inchesFromInput);
                getHeightIn = new ConvertFeetAndInches().convertFtInToIn(feetFromInput, inchesFromInput);
            }

            String getSecurityQuestion1 = spSecurityQuestion1.getSelectedItem().toString();
            String getSecurityQuestion2 = spSecurityQuestion2.getSelectedItem().toString();
            String getSecurityQuestion3 = spSecurityQuestion3.getSelectedItem().toString();
            RegisterTask registerTask = new RegisterTask(checkNameInput, checkEmailInput, checkPasswordInput,
                    getSecurityQuestion1, getSecurityQuestion2, getSecurityQuestion3, checkAnswer1Input,
                    checkAnswer2Input, checkAnswer3Input, getHeightCm, getHeightM, getHeightIn, ftinFeet,
                    ftinInches, checkBirthdateInput);
            registerTask.execute("");
        }
    }

    private class ConvertCentimeters {
        Double convertCmToM(Double cm){
            return cm / 100;
        }
        Double convertCmToIn(Double cm){
            return cm * 0.3937;
        }
    }
    private class ConvertInches {
        Double convertInToM(Double in){
            return (in/0.3937)/100;
        }
        Double convertInToCm(Double in){
            return in / 0.3937;
        }
    }
    private class ConvertMeters {
        Double convertMToCm(Double m){
            return m * 100;
        }
        Double convertMToIn(Double m) {
            return (m*100)*0.3937;
        }
    }
    private class ConvertFeetAndInches {
        Double convertFtInToCm(Double ft, Double in){
            Double ftCm = ft * 30.48;
            Double inCm = new ConvertInches().convertInToCm(in);
            return ftCm + inCm;
        }
        Double convertFtInToM(Double ft, Double in){
            Double ftM = ft * 0.3048;
            Double inM = new ConvertInches().convertInToM(in);
            return ftM + inM;
        }
        Double convertFtInToIn(Double ft, Double in){
            Double ftIn = ft * 12;
            return ftIn + in;
        }
        // Reverse
        Double ftinCentimeters_ft(Double cm){
            String feetDecimal = String.valueOf(cm/30.48);
            return Double.valueOf(feetDecimal.substring(0, feetDecimal.indexOf(".")));
        }
        Double ftinCentimeters_in(Double cm){
            String feetDecimal = String.valueOf(cm/30.48);
            Double feetAfterDecimalPoint = Double.valueOf(feetDecimal.substring(feetDecimal.indexOf(".")));
            return feetAfterDecimalPoint * 12;
        }
        Double ftinMeters_ft(Double m){
            String feetDecimal = String.valueOf(m/0.3048);
            return Double.valueOf(feetDecimal.substring(0, feetDecimal.indexOf(".")));
        }
        Double ftinMeters_in(Double m){
            String feetDecimal = String.valueOf(m/0.3048);
            Double feetAfterDecimalPoint = Double.valueOf(feetDecimal.substring(feetDecimal.indexOf(".")));
            return feetAfterDecimalPoint * 12;
        }
        Double ftinInches_ft(Double in){
            String feetDecimal = String.valueOf(in/12);
            return Double.valueOf(feetDecimal.substring(0, feetDecimal.indexOf(".")));
        }
        Double ftinInches_in(Double in){
            Double wholeInches = ftinInches_ft(in) * 12;
            return in - wholeInches;
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

    @Override
    public void onBackPressed() {
        boolean emptyName = nameInput.getText().toString().trim().isEmpty();
        boolean emptyEmail = emailInput.getText().toString().trim().isEmpty();
        boolean emptyPassword = passwordInput.getText().toString().trim().isEmpty();
        boolean emptyConfirm = confirmPassword.getText().toString().trim().isEmpty();
        boolean emptyFirstAnswer = securityAnswer1.getText().toString().trim().isEmpty();
        boolean emptySecondAnswer = securityAnswer2.getText().toString().trim().isEmpty();
        boolean emptyThirdAnswer = securityAnswer3.getText().toString().trim().isEmpty();
        boolean emptyHeight = heightInput.getText().toString().trim().isEmpty();
        boolean emptyFeet = feetInput.getText().toString().trim().isEmpty();
        boolean emptyInches = inchesInput.getText().toString().trim().isEmpty();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //Warning message when user clicks the Back button
            AlertDialog.Builder warningDialog = new AlertDialog.Builder(this);
            //Will redirect to Home page when user clicks Yes
            warningDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ProfileActivity.super.onBackPressed();
                }
            });
            //Registration page will stay when user clicks No
            warningDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            warningDialog.setTitle("Registration Incomplete")
                    .setMessage("Are you sure you want to exit?");

            /**
             * Warning message must show when one or more fields are not empty
             * Validations for birthdate, feet and inches are not available yet
             */
            if(!emptyName || !emptyEmail || !emptyPassword || !emptyConfirm || !emptyFirstAnswer || !emptySecondAnswer || !emptyThirdAnswer || !emptyHeight) {
                warningDialog.show(); //Warning message will show when user clicks the Back button
            }
            else{
                super.onBackPressed(); //Will redirect to previous page when all fields are empty
            }
        }
    }

    public class RegisterTask extends AsyncTask<String, String, String> {

        private final String mName, mEmail, mPassword, mSecQues1, mSecQues2, mSecQues3, mSecAns1,
                mSecAns2, mSecAns3, mBirthdate;
        private Double mHeightCm, mHeightM, mHeightIn, mHeight2Feet, mHeight2Inches;
        String registerMsg = "";
        Boolean isSuccess = false;

        RegisterTask(String name, String email, String password, String sq1, String sq2, String sq3,
                     String sa1, String sa2, String sa3, Double cm, Double m, Double in, Double ftin_ft,
                     Double ftin_in, String birthdate){
            mName = name;
            mEmail = email;
            mPassword = password;
            mSecQues1 = sq1;
            mSecQues2 = sq2;
            mSecQues3 = sq3;
            mSecAns1 = sa1;
            mSecAns2 = sa2;
            mSecAns3 = sa3;
            mHeightCm = cm;
            mHeightM = m;
            mHeightIn = in;
            mHeight2Feet = ftin_ft;
            mHeight2Inches = ftin_in;
            mBirthdate = birthdate;
        }

        protected void onPreExecute(){
            registerProgress.setVisibility(View.VISIBLE);
        }
        @Override
        protected void onPostExecute(String r){
            registerProgress.setVisibility(View.GONE);
            if(isSuccess){
                AlertDialog.Builder successDialog = new AlertDialog.Builder(ProfileActivity.this);
                successDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent iLogin = new Intent(ProfileActivity.this, LoginActivity.class);
                        startActivity(iLogin);
                    }
                });
                successDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Intent iLogin = new Intent(ProfileActivity.this, LoginActivity.class);
                        startActivity(iLogin);
                    }
                });
                successDialog.setTitle("Registration Successful").setMessage(registerMsg);
                successDialog.show();
            }
            else{
                AlertDialog.Builder errorDialog = new AlertDialog.Builder(ProfileActivity.this);
                errorDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                errorDialog.setTitle("Registration Error")
                        .setMessage(registerMsg)
                        .show();
            }
        }

        @Override
        protected String doInBackground(String... params){
            try{
                con = connectionClass();
                if(con==null){
                    registerMsg = "Please check your internet connection";
                    isSuccess = false;
                }
                else{
                    String queryInsertProfile = "insert into profiletable(profile_name, email, profile_pw, security_question1, security_question2, security_question3, security_answer1, security_answer2, security_answer3, height_cm, height_m, height_in, height2_ft, height2_in, birthdate) values";

                    String querySelectProfile = "select * from profiletable where ";
                    String queryWhereEmail = "email = \'" + mEmail + "\'";
                    String checkEmailQuery = querySelectProfile + queryWhereEmail;
                    Statement checkEmailStatement = con.createStatement();
                    ResultSet rsCheckEmail = checkEmailStatement.executeQuery(checkEmailQuery);
                    if(rsCheckEmail.next()){
                        registerMsg = "E-mail address already exists. Please login with the existing e-mail address, or register with a different address.";
                        isSuccess = false;
                        con.close();
                    }
                    else {
                        String queryInsertName = "\'"+mName+"\',";
                        String queryInsertEmail = "\'"+mEmail+"\',";
                        byte[] encodedString = Base64.encode(mPassword.getBytes(), Base64.NO_WRAP);
                        String queryInsertPassword = "\'" + new String(encodedString) + "\',";
                        String querySecQues1 = "\'"+mSecQues1+"\',";
                        String querySecQues2 = "\'"+mSecQues2+"\',";
                        String querySecQues3 = "\'"+mSecQues3+"\',";
                        String querySecAns1 = "\'"+mSecAns1.toLowerCase()+"\',";
                        String querySecAns2 = "\'"+mSecAns2.toLowerCase()+"\',";
                        String querySecAns3 = "\'"+mSecAns3.toLowerCase()+"\',";
                        String queryCm = "\'"+mHeightCm+"\',";
                        String queryM = "\'"+mHeightM+"\',";
                        String queryIn = "\'"+mHeightIn+"\',";
                        String queryFtin_ft = "\'"+mHeight2Feet+"\',";
                        String queryFtin_in = "\'"+mHeight2Inches+"\',";
                        String queryBirthdate = "\'"+mBirthdate+"\'"; // Last value for INSERT statement

                        String queryInsertValues = "(" + queryInsertName + queryInsertEmail +
                                queryInsertPassword + querySecQues1 + querySecQues2 + querySecQues3 +
                                querySecAns1 + querySecAns2 + querySecAns3 + queryCm + queryM + queryIn + queryFtin_ft + queryFtin_in + queryBirthdate + ")";

                        Statement insertAccount = con.createStatement();
                        insertAccount.executeUpdate(queryInsertProfile + queryInsertValues);
                        registerMsg = "You have successfully registered your account, " + mName + ". Please login.";
                        isSuccess = true;
                        con.close();
                    }
                }
            } catch (SQLException e) {
                registerMsg = "We hit a snag. Please check back later.";
                isSuccess = false;
                e.printStackTrace();
            }
            return registerMsg;
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
}