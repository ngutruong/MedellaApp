package com.medella.android;

import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.regex.Pattern;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        CheckBox feetAndInches = (CheckBox)findViewById(R.id.chkFeetInches);
        final EditText editHeight = (EditText)findViewById(R.id.txtHeight);
        final Spinner spinHeight = (Spinner)findViewById(R.id.heightSpinner);
        final EditText editFeet = (EditText)findViewById(R.id.txtFeet);
        final EditText editInches = (EditText)findViewById(R.id.txtInches);
        final TextView feetLabel = (TextView)findViewById(R.id.feetText);
        final TextView inchesLabel = (TextView)findViewById(R.id.inchesText);

        editFeet.setVisibility(View.GONE);
        editInches.setVisibility(View.GONE);
        feetLabel.setVisibility(View.GONE);
        inchesLabel.setVisibility(View.GONE);

        feetAndInches.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    editHeight.setVisibility(View.GONE);
                    spinHeight.setVisibility(View.GONE);

                    editFeet.setVisibility(View.VISIBLE);
                    editInches.setVisibility(View.VISIBLE);
                    feetLabel.setVisibility(View.VISIBLE);
                    inchesLabel.setVisibility(View.VISIBLE);

                    editHeight.setText("");
                }
                else{
                    editHeight.setVisibility(View.VISIBLE);
                    spinHeight.setVisibility(View.VISIBLE);

                    editFeet.setVisibility(View.GONE);
                    editInches.setVisibility(View.GONE);
                    feetLabel.setVisibility(View.GONE);
                    inchesLabel.setVisibility(View.GONE);

                    editFeet.setText("");
                    editInches.setText("");
                }
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_profile);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    ); //This is a regular expression to validate user's e-mail address

    private static String collectErrors = "";

    public void nextClick(View view) {
        EditText nameInput = (EditText)findViewById(R.id.txtNameEdit);
        EditText emailInput = (EditText)findViewById(R.id.txtEmailEdit);
        EditText passwordInput = (EditText)findViewById(R.id.txtCreatePassword);
        EditText confirmPassword = (EditText)findViewById(R.id.txtConfirmEdit);
        EditText securityAnswer1 = (EditText)findViewById(R.id.txtSq1Answer);
        EditText securityAnswer2 = (EditText)findViewById(R.id.txtSq2Answer);
        EditText securityAnswer3 = (EditText)findViewById(R.id.txtSq3Answer);
        EditText birthdateInput = (EditText)findViewById(R.id.txtBirthdate);
        EditText heightInput = (EditText)findViewById(R.id.txtHeight);
        EditText feetInput = (EditText)findViewById(R.id.txtFeet);
        EditText inchesInput = (EditText)findViewById(R.id.txtInches);
        Spinner heightSpin = (Spinner)findViewById(R.id.heightSpinner);

        String checkNameInput = nameInput.getText().toString();
        String checkEmailInput = emailInput.getText().toString();
        String checkPasswordInput = passwordInput.getText().toString();
        String checkConfirmInput = confirmPassword.getText().toString();
        String checkAnswer1Input = securityAnswer1.getText().toString();
        String checkAnswer2Input = securityAnswer2.getText().toString();
        String checkAnswer3Input = securityAnswer3.getText().toString();
        String checkBirthdateInput; //MUST FIND BIRTHDATE VALIDATION
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
            //NAME VALIDATION IN PROFILE PAGE
            if(checkNameInput.trim().length() == 0){
                collectErrors+="- Name is empty.\n";
            }

            //EMAIL VALIDATION IN PROFILE PAGE -- incomplete
            if(checkEmailInput.trim().length() == 0){
                collectErrors+="- E-mail address is empty.\n";
            }
            else if(!EMAIL_PATTERN.matcher(checkEmailInput).matches()){
                collectErrors+="- E-mail address is not valid.\n";
            }

            //PASSWORD VALIDATION IN PROFILE PAGE
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

            //SECURITY ANSWER VALIDATION IN PROFILE PAGE
            if(checkAnswer1Input.trim().isEmpty()){
                collectErrors+="- Answer for Security Question 1 is empty.\n";
            }
            else if (checkAnswer1Input.trim().length() < 5 && checkAnswer1Input.trim().length() > 0) {
                collectErrors+="- Answer for Security Question 1 is less than 5 characters.\n";
            }
            if(checkAnswer2Input.trim().isEmpty()){
                collectErrors+="- Answer for Security Question 2 is empty.\n";
            }
            else if (checkAnswer2Input.trim().length() < 5 && checkAnswer2Input.trim().length() > 0) {
                collectErrors+="- Answer for Security Question 2 is less than 5 characters.\n";
            }
            if(checkAnswer3Input.trim().isEmpty()){
                collectErrors+="- Answer for Security Question 3 is empty.\n";
            }
            else if (checkAnswer3Input.trim().length() < 5 && checkAnswer3Input.trim().length() > 0) {
                collectErrors+="- Answer for Security Question 3 is less than 5 characters.\n";
            }

            //HEIGHT VALIDATION IN PROFILE PAGE
            if(heightSpin.getSelectedItem().toString().equals("cm.") && Double.parseDouble(checkHeightInput) < 50){
                collectErrors+="- Height cannot be less than 50 cm.";
            }
            else if(heightSpin.getSelectedItem().toString().equals("m.") && Double.parseDouble(checkHeightInput) < 0.5){
                collectErrors+="- Height cannot be less than 0.5 m.";
            }
            else if(heightSpin.getSelectedItem().toString().equals("in.") && Double.parseDouble(checkHeightInput) < 19.685){
                collectErrors+="- Height cannot be less than 20 in.";
            }
            else if(Double.parseDouble(checkInchesInput) > 12){ //HAVING ERRORS
                collectErrors+="- Inches are not valid.";
            }
        }
        catch (Exception ex){
            errorDialog.setMessage("Please enter a valid input.");
        }

        if(collectErrors.trim().length() > 0) {
            errorDialog.setTitle("Error")
                    .setMessage("Registration is not complete due to the following errors:\n\n" + collectErrors)
                    .show();
            collectErrors = ""; //Reset errors
        }
        else{
            errorDialog.setTitle("Registration Success")
                    .setMessage("Thank you.")
                    .show();
        }
    }

    @Override
    public void onBackPressed() {
        EditText nameInput = (EditText)findViewById(R.id.txtNameEdit);
        EditText emailInput = (EditText)findViewById(R.id.txtEmailEdit);
        EditText passwordInput = (EditText)findViewById(R.id.txtCreatePassword);
        EditText confirmPassword = (EditText)findViewById(R.id.txtConfirmEdit);
        EditText securityAnswer1 = (EditText)findViewById(R.id.txtSq1Answer);
        EditText securityAnswer2 = (EditText)findViewById(R.id.txtSq2Answer);
        EditText securityAnswer3 = (EditText)findViewById(R.id.txtSq3Answer);
        EditText birthdateInput = (EditText)findViewById(R.id.txtBirthdate);
        EditText heightInput = (EditText)findViewById(R.id.txtHeight);
        EditText feetInput = (EditText)findViewById(R.id.txtFeet);
        EditText inchesInput = (EditText)findViewById(R.id.txtInches);

        boolean emptyName = nameInput.getText().toString().trim().isEmpty();
        boolean emptyEmail = emailInput.getText().toString().trim().isEmpty();
        boolean emptyPassword = passwordInput.getText().toString().trim().isEmpty();
        boolean emptyConfirm = confirmPassword.getText().toString().trim().isEmpty();
        boolean emptyFirstAnswer = securityAnswer1.getText().toString().trim().isEmpty();
        boolean emptySecondAnswer = securityAnswer2.getText().toString().trim().isEmpty();
        boolean emptyThirdAnswer = securityAnswer3.getText().toString().trim().isEmpty();
        boolean emptyBirthdate; //MUST FIND BIRTHDATE VALIDATION--need to be boolean
        boolean emptyHeight = heightInput.getText().toString().trim().isEmpty();
        boolean emptyFeet = feetInput.getText().toString().trim().isEmpty();
        boolean emptyInches = inchesInput.getText().toString().trim().isEmpty();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

            //Warning message must show when one or more fields are not empty
            //Validations for birthdate, feet and inches are not available yet
            if(!emptyName || !emptyEmail || !emptyPassword || !emptyConfirm || !emptyFirstAnswer || !emptySecondAnswer || !emptyThirdAnswer || !emptyHeight) {
                warningDialog.show(); //Warning message will show when user clicks the Back button
            }
            else{
                super.onBackPressed(); //Will redirect to previous page when all fields are empty
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_profile, menu); //Cancel out 3-dot menu in Health Activity page
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        EditText nameInput = (EditText)findViewById(R.id.txtNameEdit);
        EditText emailInput = (EditText)findViewById(R.id.txtEmailEdit);
        EditText passwordInput = (EditText)findViewById(R.id.txtCreatePassword);
        EditText confirmPassword = (EditText)findViewById(R.id.txtConfirmEdit);
        EditText securityAnswer1 = (EditText)findViewById(R.id.txtSq1Answer);
        EditText securityAnswer2 = (EditText)findViewById(R.id.txtSq2Answer);
        EditText securityAnswer3 = (EditText)findViewById(R.id.txtSq3Answer);
        EditText birthdateInput = (EditText)findViewById(R.id.txtBirthdate);
        EditText heightInput = (EditText)findViewById(R.id.txtHeight);
        EditText feetInput = (EditText)findViewById(R.id.txtFeet);
        EditText inchesInput = (EditText)findViewById(R.id.txtInches);

        boolean emptyName = nameInput.getText().toString().trim().isEmpty();
        boolean emptyEmail = emailInput.getText().toString().trim().isEmpty();
        boolean emptyPassword = passwordInput.getText().toString().trim().isEmpty();
        boolean emptyConfirm = confirmPassword.getText().toString().trim().isEmpty();
        boolean emptyFirstAnswer = securityAnswer1.getText().toString().trim().isEmpty();
        boolean emptySecondAnswer = securityAnswer2.getText().toString().trim().isEmpty();
        boolean emptyThirdAnswer = securityAnswer3.getText().toString().trim().isEmpty();
        boolean emptyBirthdate; //MUST FIND BIRTHDATE VALIDATION--need to be boolean
        boolean emptyHeight = heightInput.getText().toString().trim().isEmpty();
        boolean emptyFeet = feetInput.getText().toString().trim().isEmpty();
        boolean emptyInches = inchesInput.getText().toString().trim().isEmpty();

        final Intent iHome = new Intent(ProfileActivity.this, HomeActivity.class);
        final Intent iHealth = new Intent(ProfileActivity.this, HealthActivity.class);
        final Intent iResults = new Intent(ProfileActivity.this, ResultsActivity.class);
        //final Intent iList = new Intent(ProfileActivity.this, ListActivity.class); //ListActivity isn't available yet; don't know why it's available
        //final Intent iTrash = new Intent(ProfileActivity.this, TrashActivity.class);

        //Warning message when user selects an option from navigation drawer
        AlertDialog.Builder warningDialog = new AlertDialog.Builder(this);
        //Registration page will stay when user clicks No
        warningDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        warningDialog.setTitle("Registration Incomplete")
                .setMessage("Are you sure you want to exit?");

        //DRAWER NAVIGATION IN HOME PAGE
        if (id == R.id.nav_amHome) {
            //Will redirect to Home page when user selects Yes
            warningDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(iHome);
                }
            });

            //Warning message must show when one or more fields are not empty
            //Validations for birthdate, feet and inches are not available yet
            if(!emptyName || !emptyEmail || !emptyPassword || !emptyConfirm || !emptyFirstAnswer || !emptySecondAnswer || !emptyThirdAnswer || !emptyHeight) {
                warningDialog.show(); //Warning message will show when user selects the Home option
            }
            else{
                startActivity(iHome);  //Will redirect to Home page when all fields are empty
            }
        } else if (id == R.id.nav_amActivity) {
            //Will redirect to Activity page when user clicks Yes
            warningDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(iHealth);
                }
            });

            //Warning message must show when one or more fields are not empty
            //Validations for birthdate, feet and inches are not available yet
            if(!emptyName || !emptyEmail || !emptyPassword || !emptyConfirm || !emptyFirstAnswer || !emptySecondAnswer || !emptyThirdAnswer || !emptyHeight) {
                warningDialog.show(); //Warning message will show when user selects the Activity option
            }
            else{
                startActivity(iHealth);  //Will redirect to Activity page when all fields are empty
            }
        } else if (id == R.id.nav_amList) {
            //ListActivity.class not available yet
        } else if (id == R.id.nav_amResults) {
            //Will redirect to Results page when user clicks Yes
            //MAY NOT SHOW RESULTS OPTION DUE TO NOT BEING LOGIN
            warningDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(iResults);
                }
            });

            //Warning message must show when one or more fields are not empty
            //Validations for birthdate, feet and inches are not available yet
            if(!emptyName || !emptyEmail || !emptyPassword || !emptyConfirm || !emptyFirstAnswer || !emptySecondAnswer || !emptyThirdAnswer || !emptyHeight) {
                warningDialog.show(); //Warning message will show when user selects the Results option
            }
            else{
                startActivity(iResults);  //Will redirect to Results page when all fields are empty
            }
        } else if (id == R.id.nav_amTrash) {
            //TrashActivity.class not available yet
        } else if (id == R.id.nav_amLogout) {
            //Logout is not available at this moment
            //Logout must not appear while registering account
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}