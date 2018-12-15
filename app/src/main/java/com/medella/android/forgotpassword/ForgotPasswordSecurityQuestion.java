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
import android.widget.TextView;
import android.widget.Toast;

import com.medella.android.R;
import com.medella.android.activities.LoginActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ForgotPasswordSecurityQuestion extends AppCompatActivity {

    private TextView tvFpSecQues;
    private EditText etFpSecAns;

    public Connection con;
    private String fpSecAns, fpSecQues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_security_question);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fpSecQues = ForgotPWAccount.getSecQues(getApplicationContext());
        tvFpSecQues = findViewById(R.id.tvFPSecQues);
        tvFpSecQues.setText(fpSecQues);

        etFpSecAns = findViewById(R.id.etFPSecAns);
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

    public void nextNewPassword(View view) {
        Toast.makeText(getApplicationContext(), "Please wait...", Toast.LENGTH_SHORT).show();

        fpSecAns = etFpSecAns.getText().toString().toLowerCase();

        CheckSecAnsTask checkSecAnsTask = new CheckSecAnsTask(fpSecAns);
        checkSecAnsTask.execute("");
    }

    public class CheckSecAnsTask extends AsyncTask<String, String, String> {

        private final String mSecAns;
        String forgotPasswordMsg = "";
        Boolean isSuccess = false;

        CheckSecAnsTask(String secAns) {
            mSecAns = secAns;
        }

        protected void onPreExecute(){ }
        @Override
        protected void onPostExecute(String r){
            if(isSuccess){
                Intent iNewPassword = new Intent(ForgotPasswordSecurityQuestion.this, ForgotPasswordNewPassword.class);
                startActivity(iNewPassword);
            }
            else{
                AlertDialog.Builder failDialog = new AlertDialog.Builder(ForgotPasswordSecurityQuestion.this);
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

                    String fpEmail = ForgotPWAccount.getEmail(getApplicationContext());
                    String queryEmail = "email = \'" + fpEmail + "\'";

                    String whichSecAns = ForgotPWAccount.getSecAnsWhich(getApplicationContext());
                    String querySecAns = whichSecAns + " = \'" + mSecAns + "\'";

                    String query = querySelectProfile + queryEmail + " and " + querySecAns;
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if(rs.next()){
                        isSuccess = true;
                        con.close();
                    }
                    else{
                        forgotPasswordMsg = "Security answer is incorrect. Please try again.";
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
}
