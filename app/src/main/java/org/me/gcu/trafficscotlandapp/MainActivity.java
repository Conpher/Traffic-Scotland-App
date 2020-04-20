package org.me.gcu.trafficscotlandapp;

/**
 * Christopher Conlan
 * Created On: 14/04/2020
 * Student No: S1512271
 * Mobile Platform Development Coursework
 */


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.me.gcu.trafficscotlandapp.Enum.SourceUrl;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Define references and create OnClickListeners.
    private static final String TAG = "MainActivity";

    private RecyclerView itemRecycler;
    private Button btnCurrentIncidents;
    private Button btnRoadworks;
    private Button btnPlannedRoadworks;
    private TextView txtAboutApp;
    private TextView txtErrorOccured;
    private SwipeRefreshLayout refreshLayout;
    private SearchView searchView;


    private List<ItemModel> itemList;
    private ItemRecyclerAdapter initialAdaptor;

    private String urlParcel;

    //Keep placeholder text if there is no data or internet connection drops.
    private void  showPlaceHolderText() {
        if (itemList != null && itemList.size() > 0) {
            txtAboutApp.setVisibility(View.GONE);
        } else {
            txtAboutApp.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itemRecycler = (RecyclerView) findViewById(R.id.recyclerView);
        btnCurrentIncidents = (Button) findViewById(R.id.getCurrentIncidentsBtn);
        btnRoadworks = (Button) findViewById(R.id.getRoadworksBtn);
        btnPlannedRoadworks = (Button) findViewById(R.id.getPlannedRoadworksBtn);
        searchView = (SearchView) findViewById(R.id.searchView);
        txtAboutApp = (TextView) findViewById(R.id.aboutAppTxtView);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);


        //Set LayoutManager.
        itemRecycler.setLayoutManager(new LinearLayoutManager(this));


        //OnClickListener for Current Incidents.
        btnCurrentIncidents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                urlParcel = SourceUrl.CURRENT_INCIDENTS.toString();
                new FetchFeedTask().execute((Void) null);
            }
        });

        //OnClickListener for Roadworks.
        btnRoadworks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                urlParcel = SourceUrl.ROADWORKS.toString();
                new FetchFeedTask().execute((Void) null);
            }
        });

        //OnClickListener for Planned Roadworks.
        btnPlannedRoadworks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                urlParcel = SourceUrl.PLANNED_ROADWORKS.toString();
                new FetchFeedTask().execute((Void) null);
            }
        });

        //Create OnQueryListener for SearchView.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            //Stop Inception of SearchView, filter results if something is found and return true.
            @Override
            public boolean onQueryTextChange(String newText) {
                if (itemList == null || itemList.size() == 0) return false;
                if (initialAdaptor == null){
                    initialAdaptor = new ItemRecyclerAdapter(itemList);
                }
                ItemRecyclerAdapter itemRecyclerAdapter = initialAdaptor;
                initialAdaptor.getFilter().filter(newText);
                itemRecycler.setAdapter(itemRecyclerAdapter);

                return true;
            }
        });


        //set OnRefreshListener. If refresh takes place reload RecyclerView and execute FetchFeedTask.
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new FetchFeedTask().execute((Void) null);
            }
        });
    }

    //Fetch feed task is an AsyncTask, so processes wont be done on the main thread.
    private class FetchFeedTask extends AsyncTask<Void, Void, Boolean> {

        private String urlLink;

        //Executed before task start. Executed on the thread that is called.
        @Override
        protected void onPreExecute() {

            refreshLayout.setRefreshing(true);
            urlLink = urlParcel;
        }

        //Executes on a new thread. Stops heavy processes being carried out on main thread.
        @Override
        protected Boolean doInBackground(Void... voids) {

            //Fetch RSS feed.
            try {
                URL url = new URL(urlLink);
                InputStream inputStream = url.openConnection().getInputStream();
                itemList = new GetRssDataOperation().parseFeed(inputStream);
                return true;
            } catch (IOException e) {
                Log.e(TAG, "Error.", e);
            } catch (XmlPullParserException e) {
                Log.e(TAG, "Error, XmlPullParser issue.", e);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {

            refreshLayout.setRefreshing(false);

            //If successful, fill the recycler view with items.
            if (success) {
                initialAdaptor = new ItemRecyclerAdapter(itemList);
                itemRecycler.setAdapter(initialAdaptor);
                showPlaceHolderText();
            } else {
                Toast.makeText(MainActivity.this,
                        "Error, no items found...",
                        Toast.LENGTH_LONG).show();
            }

        }
    }
}
