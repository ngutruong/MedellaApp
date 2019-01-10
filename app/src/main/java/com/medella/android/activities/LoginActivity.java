package com.medella.android.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.medella.android.AccountStatus;
import com.medella.android.R;
import com.medella.android.forgotpassword.ForgotPasswordEmail;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A login screen that offers login via email/password.
 */
//public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    //For Connection
    public Connection con;
    private String loginEmail, loginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.txtEmailLogin);

        mPasswordView = (EditText) findViewById(R.id.txtPasswordLogin);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        if(AccountStatus.isLoggedIn(getApplicationContext())){
            Intent iList = new Intent(this, ListActivity.class);
            startActivity(iList);
        }
    }

    /**
     * Run login process when user taps Login button
     *
     * @param view
     */
    public void runLogin(View view) {
        loginEmail = mEmailView.getText().toString();
        loginPassword = mPasswordView.getText().toString();
        UserLoginTask loginTest = new UserLoginTask(loginEmail, loginPassword);
        loginTest.execute("");
    }

    public void goToRegister(View view) {
        Intent iRegister = new Intent(this, ProfileActivity.class);
        startActivity(iRegister);
    }

    @Override
    public void onBackPressed() { }

    public void goToForgotPassword(View view) {
        Intent iForgotPassword = new Intent(this, ForgotPasswordEmail.class);
        startActivity(iForgotPassword);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    //public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
    public class UserLoginTask extends AsyncTask<String, String, String> {

        private final String mEmail;
        private final String mPassword;
        String loginMsg = "";
        String profileId = "";
        String profileName = "";
        String profileEmail = "";
        String profileBirthdate = "";
        float profileHeight, profileHeightFt, profileHeightIn;
        Boolean isSuccess = false;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        protected void onPreExecute(){
            mProgressView.setVisibility(View.VISIBLE);
        }
        @Override
        protected void onPostExecute(String r){
            mProgressView.setVisibility(View.GONE);
            if(isSuccess){
                Intent iList = new Intent(LoginActivity.this, ListActivity.class);
                startActivity(iList);
                Toast.makeText(getApplicationContext(), loginMsg, Toast.LENGTH_LONG).show();
            }
            else{
                AlertDialog.Builder failDialog = new AlertDialog.Builder(LoginActivity.this);
                failDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                failDialog.setTitle("Login Error").setMessage(loginMsg);
                failDialog.show();
            }
        }

        @Override
        protected String doInBackground(String... params){
            try{
                con = connectionClass();
                if(con==null){
                    loginMsg = "Please check your internet connection";
                }
                else{
                    String querySelectProfile = "select * from profiletable where ";
                    String queryEmail = "email = \'" + mEmail + "\'";
                    byte[] encodedString = Base64.encode(mPassword.getBytes(), Base64.NO_WRAP);
                    String queryPassword = "profile_pw = \'" + new String(encodedString) + "\'";
                    String query = querySelectProfile + queryEmail + " and " + queryPassword;
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if(rs.next()){
                        profileId = rs.getString("id");
                        profileName = rs.getString("profile_name");
                        profileEmail = rs.getString("email");
                        profileHeight = rs.getFloat("height_m");
                        profileHeightFt = rs.getFloat("height2_ft");
                        profileHeightIn = rs.getFloat("height2_in");
                        profileBirthdate = rs.getString("birthdate"); // Will it work?
                        AccountStatus.setProfileName(getApplicationContext(), profileName);
                        AccountStatus.setEmail(getApplicationContext(), profileEmail);
                        AccountStatus.setHeightM(getApplicationContext(), profileHeight);
                        AccountStatus.setHeight2Ft(getApplicationContext(), profileHeightFt);
                        AccountStatus.setHeight2In(getApplicationContext(), profileHeightIn);
                        AccountStatus.setProfileId(getApplicationContext(), profileId);
                        AccountStatus.setProfileBirthdate(getApplicationContext(), profileBirthdate);
                        AccountStatus.setLogin(getApplicationContext(), true);
                        loginMsg = "Welcome to Medella, " + profileName + "!";
                        isSuccess = true;
                        con.close();
                    }
                    else{
                        loginMsg = "Incorrect e-mail address or password. Please enter the valid e-mail address and password, and try again.";
                        isSuccess = false;
                    }
                }
            } catch (SQLException e) {
                loginMsg = "We hit a snag. Please check back later.";
                isSuccess = false;
                e.printStackTrace();
            }
            return loginMsg;
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

