package com.medella.android.forgotpassword;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.medella.android.ForgotPWAccount;
import com.medella.android.R;
import com.medella.android.activities.LoginActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ForgotPasswordNewPassword extends AppCompatActivity {

    private EditText tvNewPw, tvConfirmNewPw;

    public Connection con;
    private String fpNewPw, fpConfirmNewPw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_new_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvNewPw = findViewById(R.id.etFPNewPassword);
        tvConfirmNewPw = findViewById(R.id.etFPConfirmNewPassword);
    }

    @Override
    public void onBackPressed() {
        ForgotPWAccount.setEmail(getApplicationContext(), null);
        ForgotPWAccount.setSecQuesWhich(getApplicationContext(), null);
        ForgotPWAccount.setSecQues(getApplicationContext(), null);
        ForgotPWAccount.setSecAnsWhich(getApplicationContext(), null);

        Intent iLogin = new Intent(this, LoginActivity.class);
        startActivity(iLogin);
    }

    public void finishForgotPassword(View view) {
        fpNewPw = tvNewPw.getText().toString();
        fpConfirmNewPw = tvConfirmNewPw.getText().toString();

        if(fpNewPw.equals(fpConfirmNewPw)){
            Toast.makeText(getApplicationContext(), "Please wait...", Toast.LENGTH_SHORT).show();
            UpdatePasswordTask updatePasswordTask = new UpdatePasswordTask(fpNewPw);
            updatePasswordTask.execute("");
        }
        else{
            AlertDialog.Builder failDialog = new AlertDialog.Builder(ForgotPasswordNewPassword.this);
            failDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            failDialog.setTitle("Error").setMessage("Passwords do not match.");
            failDialog.show();
        }
    }

    public class UpdatePasswordTask extends AsyncTask<String, String, String> {

        private final String mNewPassword;
        String forgotPasswordMsg = "";
        Boolean isSuccess = false;

        UpdatePasswordTask(String newPw) {
            mNewPassword = newPw;
        }

        protected void onPreExecute(){ }
        @Override
        protected void onPostExecute(String r){
            if(isSuccess){
                ForgotPWAccount.setEmail(getApplicationContext(), null);
                ForgotPWAccount.setSecQuesWhich(getApplicationContext(), null);
                ForgotPWAccount.setSecQues(getApplicationContext(), null);
                ForgotPWAccount.setSecAnsWhich(getApplicationContext(), null);

                Intent iLogin = new Intent(ForgotPasswordNewPassword.this, LoginActivity.class);
                startActivity(iLogin);
                Toast.makeText(getApplicationContext(), forgotPasswordMsg, Toast.LENGTH_LONG).show();
            }
            else{
                AlertDialog.Builder failDialog = new AlertDialog.Builder(ForgotPasswordNewPassword.this);
                failDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                failDialog.setTitle("Error").setMessage(forgotPasswordMsg);
                failDialog.show();
            }
        }

        @Override
        protected String doInBackground(String... params){
            try{
                con = connectionClass();
                if(con==null){
                    forgotPasswordMsg = "Please check your internet connection";
                }
                else{
                    String queryUpdateProfile = "update profiletable set ";

                    String fpEmail = ForgotPWAccount.getEmail(getApplicationContext());
                    String queryEmail = "email = \'" + fpEmail + "\'";

                    byte[] encodedString = Base64.encode(mNewPassword.getBytes(), Base64.NO_WRAP);
                    String queryPw = "profile_pw = \'" + new String(encodedString) + "\'";

                    String query = queryUpdateProfile + queryPw + " where " + queryEmail;
                    Statement stmt = con.createStatement();
                    stmt.executeUpdate(query);
                    forgotPasswordMsg = "Password has been updated.";
                    isSuccess = true;
                    con.close();
                }
            } catch (SQLException e) {
                forgotPasswordMsg = "We hit a snag. Please check back later.";
                isSuccess = false;
                e.printStackTrace();
            }
            return forgotPasswordMsg;
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
