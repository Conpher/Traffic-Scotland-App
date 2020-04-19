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
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.me.gcu.trafficscotlandapp.Enum.SourceUrl;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private RecyclerView itemRecycler;
    private Button btnCurrentIncidents;
    private Button btnRoadworks;
    private Button btnPlannedRoadworks;
    private TextView txtAboutApp;
    private SwipeRefreshLayout refreshLayout;
    private SearchView searchView;


    private List<ItemModel> itemList;
    private ItemRecyclerAdapter initialAdaptor;

    private String urlParcel;

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


        itemRecycler.setLayoutManager(new LinearLayoutManager(this));


        btnCurrentIncidents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtAboutApp.setVisibility(View.GONE);
                urlParcel = SourceUrl.CURRENT_INCIDENTS.toString();
                new FetchFeedTask().execute((Void) null);
            }
        });

        btnRoadworks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtAboutApp.setVisibility(View.GONE);
                urlParcel = SourceUrl.ROADWORKS.toString();
                new FetchFeedTask().execute((Void) null);
            }
        });

        btnPlannedRoadworks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtAboutApp.setVisibility(View.GONE);
                urlParcel = SourceUrl.PLANNED_ROADWORKS.toString();
                new FetchFeedTask().execute((Void) null);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (initialAdaptor == null){
                    initialAdaptor = new ItemRecyclerAdapter(itemList);
                }
                ItemRecyclerAdapter itemRecyclerAdapter = initialAdaptor;
                initialAdaptor.getFilter().filter(newText);
                itemRecycler.setAdapter(itemRecyclerAdapter);

                return true;
            }
        });


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new FetchFeedTask().execute((Void) null);
            }
        });
    }

    public List<ItemModel> parseFeed(InputStream inputStream) throws XmlPullParserException, IOException {
        String title = null;
        String link = null;
        String description = null;
        boolean isItem = false;
        List<ItemModel> items = new ArrayList<>();

        try {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, null);

            xmlPullParser.nextTag();
            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                int eventType = xmlPullParser.getEventType();

                String name = xmlPullParser.getName();
                if(name == null)
                    continue;

                if(eventType == XmlPullParser.END_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = false;
                    }
                    continue;
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = true;
                        continue;
                    }
                }

                Log.d("MainActivity", "Parsing name ==> " + name);
                String result = "";
                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }

                if (name.equalsIgnoreCase("title")) {
                    title = result;
                } else if (name.equalsIgnoreCase("description")) {
                    description = result;
                } else if (name.equalsIgnoreCase("link")) {
                    link = result;
                }

                if (title != null && link != null && description != null) {
                    if(isItem) {
                        ItemModel item = new ItemModel(title, link, description);
                        items.add(item);
                    }
                    else {

                        Toast.makeText(MainActivity.this,"Error! No information available, please try again later.",
                                Toast.LENGTH_SHORT).show();
                    }

                    title = null;
                    link = null;
                    description = null;
                    isItem = false;
                }
            }
            return items;
        } finally {
            inputStream.close();
        }
    }

    private class FetchFeedTask extends AsyncTask<Void, Void, Boolean> {

        private String urlLink;



        @Override
        protected void onPreExecute() {

            refreshLayout.setRefreshing(true);
            urlLink = urlParcel;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            try {
                URL url = new URL(urlLink);
                InputStream inputStream = url.openConnection().getInputStream();
                itemList = parseFeed(inputStream);
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

            if (success) {
                // Fill RecyclerView
                initialAdaptor = new ItemRecyclerAdapter(itemList);
                itemRecycler.setAdapter(initialAdaptor);
            } else {
                Toast.makeText(MainActivity.this,
                        "Error, no items found...",
                        Toast.LENGTH_LONG).show();
            }

        }
    }
}
