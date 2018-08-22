package com.example.shivanisingh.webhw9;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class ResultsTableActivity extends AppCompatActivity {

    private ArrayList<SearchResults> placeList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SearchResultAdapter mAdapter;
    private FavSharedPref sharedPreference;
    String nextpagetoken;
    String newUrl;
    String keyword;
    String category;
    String distance;
    String currentlat;
    String currentlon;
    String otherloc;
    String results;
    ArrayList<ArrayList<SearchResults>> pages;
    ArrayList<SearchResults> temp ;
    int pageCount = -1;
    String key1;
    String cat1;
    String dist1;
    String otherl;
    TextView norecords;
    ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_table);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Button previous = (Button) findViewById(R.id.prev);
        final Button next = (Button) findViewById(R.id.next);
        previous.setEnabled(false);
        next.setEnabled(false);
        pages = new ArrayList<ArrayList<SearchResults>>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setMessage("Fetching results");


        sharedPreference = new FavSharedPref();
        String jsonArray = getIntent().getStringExtra("jsonArray");
        nextpagetoken = getIntent().getStringExtra("nextpagetoken");
        keyword = getIntent().getStringExtra("keyword");
        category = getIntent().getStringExtra("category");
        distance = getIntent().getStringExtra("distance");
        currentlat = getIntent().getStringExtra("currentLat");
        currentlon = getIntent().getStringExtra("currentLon");
        otherloc = getIntent().getStringExtra("otherloc");
        norecords = (TextView) findViewById(R.id.norecords);
        pages.clear();
        temp = new ArrayList<>();
        if(jsonArray.equals("[]")){
            norecords.setVisibility(View.VISIBLE);
        }
        try {
            JSONArray jsonArr = new JSONArray(jsonArray);
            placeList.clear();
            String placename = "";
            String placevic = "";
            String placeicon = "";
            String place_id = "";

            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jsonObj = jsonArr.getJSONObject(i);

                SearchResults result = new SearchResults(jsonObj.getString("name"),jsonObj.getString("vicinity"), jsonObj.getString("icon"), jsonObj.getString("place_id"));
                // Log.v("message",jsonObj.getString("icon"));
                placeList.add(result);

            }

            for(int i=0; i<placeList.size(); i++){
                temp.add(placeList.get(i));
            }

            pages.add(temp);

            Log.v("size initiallyyyy", "" + pages.size());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(nextpagetoken!=null) {
            next.setEnabled(true);
        }

        else {
            next.setEnabled(false);
        }


        mAdapter = new SearchResultAdapter(placeList, this, sharedPreference);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        pageCount++;
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageCount++;



                if (pageCount == 1 && pages.size() ==3) {
                    previous.setEnabled(true);
                    mAdapter.updateList(pages.get(pageCount));
                    mAdapter.notifyDataSetChanged();
                    if(pages.get(2)!=null){
                        next.setEnabled(true);
                    }

                } else if (pageCount == 2 && pages.size()== 3) {
                    previous.setEnabled(true);
                    mAdapter.updateList(pages.get(pageCount));
                    mAdapter.notifyDataSetChanged();
                    next.setEnabled(false);


                } else {

                    if (nextpagetoken != null) {
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        newUrl = "http://csci571shivani.us-east-2.elasticbeanstalk.com";
                        try {
                            key1 = URLEncoder.encode(keyword,"UTF-8");
                            cat1 = URLEncoder.encode(category,"UTF-8");
                            dist1 = URLEncoder.encode(distance,"UTF-8");
                            if(otherloc!=null)
                                otherl = URLEncoder.encode(otherloc,"UTF-8");

                            else
                                otherl="";
                        }
                        catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        newUrl = newUrl + "?keyword=" + key1 + "&category=" + cat1+ "&distance=" + dist1 +
                                "&currentlat=34.0224&currentlon=-118.2851&otherlocation=" + otherl+ "&pagetoken=" + nextpagetoken;
                        progress.show();

                        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, newUrl, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {


                                try {


                                    results = response.getString("results").toString();
                                    previous.setEnabled(true);
                                    JSONArray jsonArr = new JSONArray(results);
                                    temp = new ArrayList<>();
                                    placeList.clear();

                                    for (int i = 0; i < jsonArr.length(); i++) {
                                        JSONObject jsonObj = jsonArr.getJSONObject(i);
                                        SearchResults result = new SearchResults(jsonObj.getString("name"), jsonObj.getString("vicinity"), jsonObj.getString("icon"), jsonObj.getString("place_id"));
                                        placeList.add(result);
                                    }
                                    for (int i = 0; i < placeList.size(); i++) {
                                        temp.add(placeList.get(i));
                                    }

                                    pages.add(temp);
                                    progress.hide();

                                    Log.v("size on next......", "" + pages.size());
                                    mAdapter = new SearchResultAdapter(placeList, getApplicationContext(), sharedPreference);
                                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                    recyclerView.setLayoutManager(mLayoutManager);
                                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                                    recyclerView.setAdapter(mAdapter);
                                    mAdapter.notifyDataSetChanged();

                                    if (response.has("next_page_token")) {
                                        nextpagetoken = response.getString("next_page_token");


                                    } else {
                                        next.setEnabled(false);
                                    }


                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }


                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                norecords.setVisibility(View.VISIBLE);
                                Log.v(MainActivity.class.getSimpleName(), "Error: " + error.getMessage());

                            }
                        });
                        queue.add(jsonRequest);
                    }
                }
            }

        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageCount --;

                if(pageCount==0 || pageCount==1){
                    next.setEnabled(true);
                }

                mAdapter.updateList(pages.get(pageCount));
                mAdapter.notifyDataSetChanged();
                previous.setEnabled(true);
                if(pageCount==0){
                    previous.setEnabled(false);
                }
                else{
                    previous.setEnabled(true);
                }

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }



}
