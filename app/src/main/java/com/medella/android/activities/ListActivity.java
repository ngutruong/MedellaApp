package com.medella.android.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.medella.android.R;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {
    MaterialSearchView searchView;
    ListView testListview;

    String[] testingListSource = {
            "Glaucoma",
            "Fibromyalgia",
            "Joint pain",
            "Osteoporosis",
            "Tinnitus"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search Health Activity");

        testListview = (ListView)findViewById(R.id.testingList);
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, testingListSource);
        testListview.setAdapter(adapter);



        searchView = (MaterialSearchView)findViewById(R.id.search_view);

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                //If search view is closed, list view will return default
                testListview = (ListView)findViewById(R.id.testingList);
                ArrayAdapter adapter = new ArrayAdapter(ListActivity.this,android.R.layout.simple_list_item_1,testingListSource);
                testListview.setAdapter(adapter);
            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText != null & !newText.isEmpty()){
                    List<String> lstFound = new ArrayList<String>();
                    for(String item:testingListSource){
                        if(item.contains(newText)){
                            lstFound.add(item);
                        }
                    }

                    ArrayAdapter adapter = new ArrayAdapter(ListActivity.this,android.R.layout.simple_list_item_1,lstFound);
                    testListview.setAdapter(adapter);

                }
                else{
                    //If search text is null, return default
                    ArrayAdapter adapter = new ArrayAdapter(ListActivity.this, android.R.layout.simple_list_item_1,testingListSource);
                    testListview.setAdapter(adapter);
                }
                return true;
            }
        });



        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list,menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }
}
