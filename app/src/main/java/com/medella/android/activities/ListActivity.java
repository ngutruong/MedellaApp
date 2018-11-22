package com.medella.android.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;


import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.medella.android.R;
import com.medella.android.list.ActivityTable;
import com.medella.android.list.ListActivityAdapter;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncContext;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.ColumnDataType;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.MobileServiceLocalStoreException;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.SQLiteLocalStore;
import com.microsoft.windowsazure.mobileservices.table.sync.synchandler.SimpleSyncHandler;
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;

public class ListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private MobileServiceClient mClient;
    private MobileServiceTable<ActivityTable> mActivityTable;
    //Offline Sync
    //private MobileServiceSyncTable<ActivityTable> mActivityTable;
    private ListActivityAdapter mAdapter;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view_list);
        navigationView.setNavigationItemSelectedListener(this);

        mProgressBar = findViewById(R.id.loadListProgressBar);

        // Initialize the progress bar
        mProgressBar.setVisibility(ProgressBar.GONE);

        try {
            // Create the Mobile Service Client instance, using the provided

            // Mobile Service URL and key
            mClient = new MobileServiceClient(
                    "https://medellapp.azurewebsites.net",
                    this).withFilter(new ProgressFilter());

            // Extend timeout from default of 10s to 20s
            mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                @Override
                public OkHttpClient createOkHttpClient() {
                    OkHttpClient client = new OkHttpClient();
                    client.setReadTimeout(20, TimeUnit.SECONDS);
                    client.setWriteTimeout(20, TimeUnit.SECONDS);
                    return client;
                }
            });

            // Get the Mobile Service Table instance to use
            mActivityTable = mClient.getTable("ActivityTable", ActivityTable.class);

            // Offline Sync
            //mActivityTable = mClient.getSyncTable("ActivityTable", ActivityTable.class);

            //Init local storage
            initLocalStore().get();

            mAdapter = new ListActivityAdapter(this, R.layout.activity_card_view);
            ListView listViewToDo = findViewById(R.id.listViewToDo);
            listViewToDo.setAdapter(mAdapter);

            // Load the items from the Mobile Service
            refreshItemsFromTable();

        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        } catch (Exception e){
            createAndShowDialog(e, "Error");
        }
    }

    private void refreshItemsFromTable() {

        // Get the items that weren't marked as completed and add them in the
        // adapter

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    final List<ActivityTable> results = refreshItemsFromMobileServiceTable();

                    //Offline Sync
                    //final List<ActivityTable> results = refreshItemsFromMobileServiceTableSyncTable();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.clear();

                            for (ActivityTable item : results) {
                                mAdapter.add(item);
                            }
                        }
                    });
                } catch (final Exception e){
                    createAndShowDialogFromTask(e, "Error");
                }

                return null;
            }
        };

        runAsyncTask(task);
    }

    private List<ActivityTable> refreshItemsFromMobileServiceTable() throws ExecutionException, InterruptedException {
        //return mActivityTable.where().field("deleted").eq(val(false)).orderBy("createdAt", QueryOrder.Descending).execute().get();
        return mActivityTable.orderBy("createdAt", QueryOrder.Descending).execute().get();
    }

    //Offline Sync
    /**
     * Refresh the list with the items in the Mobile Service Sync Table
     */
    /*private List<ActivityTable> refreshItemsFromMobileServiceTableSyncTable() throws ExecutionException, InterruptedException {
        //sync the data
        sync().get();
        //Query query = QueryOperations.field("complete").
        //        eq(val(false));
        Query query = QueryOperations.field("deleted").eq(val(false)).orderBy("createdAt",QueryOrder.Descending);
        return mActivityTable.read(query).get();
    }*/

    private AsyncTask<Void, Void, Void> initLocalStore() {

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    MobileServiceSyncContext syncContext = mClient.getSyncContext();

                    if (syncContext.isInitialized())
                        return null;

                    SQLiteLocalStore localStore = new SQLiteLocalStore(mClient.getContext(), "OfflineStore", null, 1);

                    Map<String, ColumnDataType> tableDefinition = new HashMap<String, ColumnDataType>();
                    tableDefinition.put("id", ColumnDataType.String);
                    tableDefinition.put("deleted", ColumnDataType.Boolean);
                    tableDefinition.put("activity_title", ColumnDataType.String);
                    tableDefinition.put("pain_intensity", ColumnDataType.Real);
                    tableDefinition.put("bodytemperature_celsius", ColumnDataType.Real);
                    tableDefinition.put("bodytemperature_fahrenheit", ColumnDataType.Real);
                    tableDefinition.put("activity_description", ColumnDataType.String);
                    tableDefinition.put("activity_location", ColumnDataType.String);
                    tableDefinition.put("weight_lbs", ColumnDataType.Real);
                    tableDefinition.put("weight_kg", ColumnDataType.Real);
                    tableDefinition.put("medication_brand", ColumnDataType.String);
                    tableDefinition.put("medication_dosage", ColumnDataType.String);
                    tableDefinition.put("systolic", ColumnDataType.Real);
                    tableDefinition.put("diastolic", ColumnDataType.Real);
                    tableDefinition.put("heart_rate", ColumnDataType.Real);
                    tableDefinition.put("bmi", ColumnDataType.Real);

                    localStore.defineTable("ActivityTable", tableDefinition);

                    SimpleSyncHandler handler = new SimpleSyncHandler();

                    syncContext.initialize(localStore, handler).get();

                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }

                return null;
            }
        };

        return runAsyncTask(task);
    }

    //Offline Sync
    /**
     * Sync the current context and the Mobile Service Sync Table
     * @return
     */
    /*private AsyncTask<Void, Void, Void> sync() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    MobileServiceSyncContext syncContext = mClient.getSyncContext();
                    syncContext.push().get();
                    mActivityTable.pull(null).get();
                } catch (final Exception e) {
                    //createAndShowDialogFromTask(e, "Error"); //Hide error when running offline
                    e.printStackTrace();
                }
                return null;
            }
        };
        return runAsyncTask(task);
    }*/

    private void createAndShowDialogFromTask(final Exception exception, String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception, "Error");
            }
        });
    }

    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }

    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    public void refreshList(View view) {
        refreshItemsFromTable();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list,menu);
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

        //DRAWER NAVIGATION IN HOME PAGE
        if (id == R.id.nav_amHome) {
            Intent iHome = new Intent(this, HomeActivity.class);
            startActivity(iHome);
        } else if (id == R.id.nav_amActivity) {
            Intent iHealth = new Intent(this, HealthActivity.class);
            startActivity(iHealth);
        }else if (id == R.id.nav_amList) {
            //Intent is not needed as the List button leads to this page

        } else if (id == R.id.nav_amResults) {
            Intent iResults = new Intent(this, ResultsActivity.class);
            startActivity(iResults);

        }
        else if (id == R.id.nav_amSettings) {
            Intent iSettings = new Intent(this, SettingsActivity.class);
            startActivity(iSettings);
        }
        else if (id == R.id.nav_amLogout) {
            //Logout is not available at this moment
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void goToHealth(View view) {
        Intent iHealth = new Intent(this, HealthActivity.class);
        startActivity(iHealth);
    }

    public void deleteHealthActivity(final ActivityTable item) {
        if (mClient == null) {
            return;
        }

        // Set the item as completed and update it in the table
        // FOR deleteHealthActivity, DON'T NEED EXTRA LINE OF CODE; use it for reference
        //item.setDelete(true);

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    deleteItemInTable(item);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.remove(item);
                        }
                    });
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }

                return null;
            }
        };

        runAsyncTask(task);

    }

    public void updateItemInTable(ActivityTable item) throws ExecutionException, InterruptedException {
        mActivityTable.update(item).get();
    }

    public void deleteItemInTable(ActivityTable item) {
        mActivityTable.delete(item);
    }

    private class ProgressFilter implements ServiceFilter {

        @Override
        public ListenableFuture<ServiceFilterResponse> handleRequest(ServiceFilterRequest request, NextServiceFilterCallback nextServiceFilterCallback) {

            final SettableFuture<ServiceFilterResponse> resultFuture = SettableFuture.create();


            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.VISIBLE);
                }
            });

            ListenableFuture<ServiceFilterResponse> future = nextServiceFilterCallback.onNext(request);

            Futures.addCallback(future, new FutureCallback<ServiceFilterResponse>() {
                @Override
                public void onFailure(Throwable e) {
                    resultFuture.setException(e);
                }

                @Override
                public void onSuccess(ServiceFilterResponse response) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.GONE);
                        }
                    });

                    resultFuture.set(response);
                }
            });

            return resultFuture;
        }
    }
}
