package com.medella.android.activities;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.medella.android.R;
import com.medella.android.database.ActivityTable;
import com.medella.android.database.ActivityTableAdapter;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncContext;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.ColumnDataType;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.MobileServiceLocalStoreException;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.SQLiteLocalStore;
import com.microsoft.windowsazure.mobileservices.table.sync.synchandler.SimpleSyncHandler;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ListActivity extends AppCompatActivity {
    MaterialSearchView searchView;
    //ListView testListview;
    private MobileServiceClient mClient;
    private MobileServiceTable<ActivityTable> mActivityTable;
    //private ActivityTableAdapter1 mAdapter;
    private ActivityTableAdapter mAdapter;
    private ProgressBar mProgressBar;

    /*String[] testingListSource = {
            "Glaucoma",
            "Fibromyalgia",
            "Joint pain",
            "Osteoporosis",
            "Tinnitus"
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mProgressBar = (ProgressBar) findViewById(R.id.loadListProgressBar);

        // Initialize the progress bar
        mProgressBar.setVisibility(ProgressBar.GONE);

        try {
            // Create the Mobile Service Client instance, using the provided

            // Mobile Service URL and key
            mClient = new MobileServiceClient(
                    "https://medellaapp.azurewebsites.net",
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

            mActivityTable = mClient.getTable(ActivityTable.class);

            // Offline Sync
            //mActivityTable = mClient.getSyncTable("ActivityTable", ActivityTable.class);

            //Init local storage
            initLocalStore().get();

            // Create an adapter to bind the items with the view
            /*
            mAdapter = new ActivityTableAdapter1(this, R.layout.row_list_to_do);
            ListView listViewToDo = (ListView) findViewById(R.id.listViewToDo);
            listViewToDo.setAdapter(mAdapter);
            */
            mAdapter = new ActivityTableAdapter(this, R.layout.activity_card_view);
            ListView listViewToDo = (ListView) findViewById(R.id.listViewToDo);
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

    private List<ActivityTable> refreshItemsFromMobileServiceTable() throws ExecutionException, InterruptedException, MobileServiceException {
        //return mActivityTable.where().field("complete").
        //eq(val(false)).execute().get();
        return mActivityTable.execute().get();
    }

    private AsyncTask<Void, Void, Void> initLocalStore() throws MobileServiceLocalStoreException, ExecutionException, InterruptedException {

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
                    tableDefinition.put("activity_title", ColumnDataType.String);
                    tableDefinition.put("pain_intensity", ColumnDataType.Real);
                    tableDefinition.put("bodytemperature_celsius", ColumnDataType.Real);
                    tableDefinition.put("bodytemperature_fahrenheit", ColumnDataType.Real);
                    tableDefinition.put("activity_description", ColumnDataType.String);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list,menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }
}
