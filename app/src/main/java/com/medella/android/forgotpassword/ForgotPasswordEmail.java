package com.medella.android.forgotpassword;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.medella.android.R;
import com.medella.android.activities.LoginActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ForgotPasswordEmail extends AppCompatActivity {

    private EditText etFpEmail;

    public Connection con;
    private String fpEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_email);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etFpEmail = findViewById(R.id.etFPEmail);
    }

    public void nextSecurityQuestion(View view) {
        Toast.makeText(getApplicationContext(), "Please wait...", Toast.LENGTH_SHORT).show();
        fpEmail = etFpEmail.getText().toString();
        CheckEmailTask checkEmailTask = new CheckEmailTask(fpEmail);
        checkEmailTask.execute("");
    }

    @Override
    public void onBackPressed() {
        Intent iLogin = new Intent(this, LoginActivity.class);
        startActivity(iLogin);
    }

    public class CheckEmailTask extends AsyncTask<String, String, String> {

        private final String mEmail;
        String forgotPasswordMsg = "";
        String profileEmail = "";
        String profileSecQues = "";
        Boolean isSuccess = false;

        CheckEmailTask(String email) {
            mEmail = email;
        }

        protected void onPreExecute(){  }
        @Override
        protected void onPostExecute(String r){
            if(isSuccess){
                Intent iSecurityQuestion = new Intent(ForgotPasswordEmail.this, ForgotPasswordSecurityQuestion.class);
                startActivity(iSecurityQuestion);
            }
            else{
                AlertDialog.Builder failDialog = new AlertDialog.Builder(ForgotPasswordEmail.this);
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
                    String querySelectProfile = "select * from profiletable where ";
                    String queryEmail = "email = \'" + mEmail + "\'";
                    String query = querySelectProfile + queryEmail;
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if(rs.next()){
                        profileEmail = rs.getString("email");
                        ForgotPWAccount.setEmail(getApplicationContext(), profileEmail);
                        double secQuesNum = getSecQuesNumber(1,3);
                        if(secQuesNum == 1){
                            profileSecQues = rs.getString("security_question1");
                            ForgotPWAccount.setSecQuesWhich(getApplicationContext(), "security_question1");
                            ForgotPWAccount.setSecAnsWhich(getApplicationContext(), "security_answer1");
                            ForgotPWAccount.setSecQues(getApplicationContext(), profileSecQues);
                        }
                        else if(secQuesNum == 2){
                            profileSecQues = rs.getString("security_question2");
                            ForgotPWAccount.setSecQuesWhich(getApplicationContext(), "security_question2");
                            ForgotPWAccount.setSecAnsWhich(getApplicationContext(), "security_answer2");
                            ForgotPWAccount.setSecQues(getApplicationContext(), profileSecQues);
                        }
                        else if(secQuesNum == 3){
                            profileSecQues = rs.getString("security_question3");
                            ForgotPWAccount.setSecQuesWhich(getApplicationContext(), "security_question3");
                            ForgotPWAccount.setSecAnsWhich(getApplicationContext(), "security_answer3");
                            ForgotPWAccount.setSecQues(getApplicationContext(), profileSecQues);
                        }
                        isSuccess = true;
                        con.close();
                    }
                    else{
                        forgotPasswordMsg = "Cannot find e-mail. Please try again.";
                        isSuccess = false;
                    }
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

    public static double getSecQuesNumber(double min, double max){
        double secQuesNum = (int)(Math.random()*((max-min)+1))+min;
        return secQuesNum;
    }
}
