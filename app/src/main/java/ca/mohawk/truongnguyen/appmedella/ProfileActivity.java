package ca.mohawk.truongnguyen.appmedella;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Button goNext = (Button)findViewById(R.id.btnNext);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void nextClick(View view) {
        EditText nameInfo = (EditText)findViewById(R.id.txtNameEdit);
        EditText emailInfo = (EditText)findViewById(R.id.txtEmailEdit);
        EditText createPass = (EditText)findViewById(R.id.txtCreatePassword);
        EditText confPass = (EditText)findViewById(R.id.txtConfirmEdit);
        EditText sq1Info = (EditText)findViewById(R.id.txtSq1Answer);
        EditText sq2Info = (EditText)findViewById(R.id.txtSq2Answer);
        EditText sq3Info = (EditText)findViewById(R.id.txtSq3Answer);
        EditText bdInfo = (EditText)findViewById(R.id.txtBirthdate);
        EditText heightInfo = (EditText)findViewById(R.id.txtHeight);

        AlertDialog.Builder buildErrorMessage = new AlertDialog.Builder(ProfileActivity.this);
        buildErrorMessage.setTitle("Registration cannot be completed because of the following errors:");
        ArrayAdapter<String> errorArray = new ArrayAdapter<String>(ProfileActivity.this, android.R.layout.select_dialog_singlechoice);

        try {
            //Must not use Toast -- need MESSAGE BOX

            //NAME VALIDATION IN PROFILE PAGE
            if(nameInfo.getText().toString().trim().length() == 0){
                Toast.makeText(this, "Name must not be empty", Toast.LENGTH_SHORT).show();
                errorArray.add("Name is empty");
            }

            //PASSWORD VALIDATION IN PROFILE PAGE
            else if(createPass.getText().toString().trim().length() == 0){
                Toast.makeText(this, "Your password must not be empty", Toast.LENGTH_SHORT).show();
                errorArray.add("Password is empty");
            }
            else if(confPass.getText().toString().trim().length() == 0){
                Toast.makeText(this, "Confirm password must not be empty", Toast.LENGTH_SHORT).show();
                errorArray.add("Confirm Password is empty");
            }
            else if(createPass.getText().toString().trim().length() == 0 && confPass.getText().toString().trim().length() == 0){
                Toast.makeText(this, "Password and Confirm Password must not be empty", Toast.LENGTH_SHORT).show();
            }
            else if (createPass.getText().toString().trim().length() < 8 && createPass.getText().toString().trim().length() > 0) {
                Toast.makeText(this, "Your password must be at least 8 characters", Toast.LENGTH_SHORT).show();
            }
            else if (confPass.getText().toString().trim().length() < 8 && confPass.getText().toString().trim().length() > 0) {
                Toast.makeText(this, "Confirm password must be at least 8 characters", Toast.LENGTH_SHORT).show();
            }
            else if(!createPass.getText().toString().equals(confPass.getText().toString()) || !confPass.getText().toString().equals(createPass.getText().toString())) {
                Toast.makeText(this, "Passwords do not match. Try again.", Toast.LENGTH_SHORT).show();
            }

            //SECURITY ANSWER VALIDATION IN PROFILE PAGE
            else if(sq1Info.getText().toString().trim().length() == 0){
                Toast.makeText(this, "Security Answer 1 must not be empty", Toast.LENGTH_SHORT).show();
            }
            else if(sq2Info.getText().toString().trim().length() == 0){
                Toast.makeText(this, "Security Answer 2 must not be empty", Toast.LENGTH_SHORT).show();
            }
            else if(sq3Info.getText().toString().trim().length() == 0){
                Toast.makeText(this, "Security Answer 3 must not be empty", Toast.LENGTH_SHORT).show();
            }
            else if (sq1Info.getText().toString().trim().length() < 6 && sq1Info.getText().toString().trim().length() > 0) {
                Toast.makeText(this, "Security Answer 1 must be at least 6 characters", Toast.LENGTH_SHORT).show();
            }
            else if (sq2Info.getText().toString().trim().length() < 6 && sq2Info.getText().toString().trim().length() > 0) {
                Toast.makeText(this, "Security Answer 2 must be at least 6 characters", Toast.LENGTH_SHORT).show();
            }
            else if (sq3Info.getText().toString().trim().length() < 6 && sq3Info.getText().toString().trim().length() > 0) {
                Toast.makeText(this, "Security Answer 3 must be at least 6 characters", Toast.LENGTH_SHORT).show();
            }
            else if(sq1Info.getText().toString().trim().length() == 0 && sq2Info.getText().toString().trim().length() == 0 && sq3Info.getText().toString().trim().length() == 0){
                Toast.makeText(this, "Security answers must not be empty", Toast.LENGTH_SHORT).show();
            }
            else if((sq1Info.getText().toString().trim().length() < 6 && sq1Info.getText().toString().trim().length() > 0) && (sq2Info.getText().toString().trim().length() < 6 && sq2Info.getText().toString().trim().length() > 0) && (sq3Info.getText().toString().trim().length() < 6 && sq3Info.getText().toString().trim().length() > 0)){
                Toast.makeText(this, "Security answers must be at least 6 characters", Toast.LENGTH_SHORT).show();
            }

            //Correct validation
            else {
                //NOT THE OFFICIAL CODE -- FOR MINOR TESTING ONLY
                Toast.makeText(this, "Thank you", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception ex){
            Toast.makeText(this, "Please enter a valid input", Toast.LENGTH_SHORT).show();
        }
    }
}
