package ca.mohawk.truongnguyen.appmedella;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.regex.Pattern;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

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
            //else if(checkPasswordInput.trim().length() == 0){
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

            /*else if(checkPasswordInput.trim().length() == 0 && checkConfirmInput.trim().length() == 0){
                errorDialog.setMessage("Password and Confirm Password must not be empty.");
            }*/

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

            /*else if(checkAnswer1Input.trim().length() == 0 && checkAnswer2Input.trim().length() == 0 && checkAnswer3Input.trim().length() == 0){
                errorDialog.setMessage("Security answers must not be empty.");
            }
            else if((checkAnswer1Input.trim().length() < 6 && checkAnswer1Input.trim().length() > 0) && (checkAnswer2Input.trim().length() < 6 && checkAnswer2Input.trim().length() > 0) && (checkAnswer3Input.trim().length() < 6 && checkAnswer3Input.trim().length() > 0)){
                errorDialog.setMessage("Security answers must be at least 6 characters.");
            }

            //Correct validation
            else {
                //NOT THE OFFICIAL CODE -- FOR MINOR TESTING ONLY
                errorDialog.setMessage("Thank you.");
            }*/

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
}
