package com.example.shivanisingh.webhw9;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.Spinner;
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
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ReviewsFragment extends Fragment {

    ArrayList<ReviewsResults> reviewslist = new ArrayList<>();
    ArrayList<ReviewsResults> reviewlist2 = new ArrayList<>();
    RecyclerView recyclerView;
    ReviewsAdapter mAdapter;
    Spinner review, reviewsort;
    public DetailsTabActivity des;
    String address_Components;
    String State;
    String Country;
    String City;
    JSONObject jsonObject;
    TextView norecords;

    public ReviewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_reviews, container, false);

        String [] values = {"Google Reviews","Yelp Reviews"};
        review = (Spinner) v.findViewById(R.id.spinner_reviews);
        norecords = (TextView)v.findViewById(R.id.norecords);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        review.setAdapter(adapter);
        des = (DetailsTabActivity)getActivity();

        final String jsonDetails = des.getJsonDetails();
        try {
            jsonObject = new JSONObject(jsonDetails);
            address_Components = jsonObject.getString("address_components");
            JSONArray AddressArr= new JSONArray(address_Components);
            String[] array_types;
            for (int i = 0; i < AddressArr.length(); i++) {
                JSONObject jsonObj = AddressArr.getJSONObject(i);
                String types = jsonObj.getString("types");
                types = types.replace ("[", "").replace ("]", "");
                array_types = types.split(",");

                for(int j=0; j< array_types.length; j++) {
                    String type = (array_types[j]).replaceAll("^\"|\"$", "");

                    if (type.equals("administrative_area_level_1")) {
                        State = jsonObj.getString("short_name");
                        break;
                    }


                    if (type.equals("country")) {
                        Country = jsonObj.getString("short_name");
                        break;
                    }


                    if (type.equals("administrative_area_level_2")) {
                        City = jsonObj.getString("short_name");
                        break;
                    }

                }
            }

            String reviews = jsonObject.getString("reviews");

            JSONArray jsonArr = new JSONArray(reviews);
            reviewslist.clear();

            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jsonObj = jsonArr.getJSONObject(i);
                ReviewsResults result = new ReviewsResults(jsonObj.getString("author_name"), jsonObj.getString("rating"), jsonObj.getString("profile_photo_url"), jsonObj.getString("time"), jsonObj.getString("text"), jsonObj.getString("author_url"));
                // Log.v("message",jsonObj.getString("icon"));
                reviewslist.add(result);
                reviewlist2.add(result);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        recyclerView = (RecyclerView)v.findViewById(R.id.recycler_view);
        mAdapter = new ReviewsAdapter(reviewslist, getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        review.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                String selectedReviews = review.getSelectedItem().toString();
                if (selectedReviews.equals("Yelp Reviews")) {
                    recyclerView.setVisibility(View.GONE);
                    norecords.setVisibility(View.VISIBLE);
                }

                else {
                    recyclerView.setVisibility(View.VISIBLE);
                    norecords.setVisibility(View.GONE);

                    mAdapter.updateList(reviewslist);
                    mAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });


        String [] sortvalues = {"Default order","Most recent", "Least recent", "Highest rating", "Lowest rating"};
        reviewsort = (Spinner) v.findViewById(R.id.spinner_sort);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, sortvalues);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        reviewsort.setAdapter(adapter2);

        reviewsort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                String selected = reviewsort.getSelectedItem().toString();
                if(selected.equals("Default order")){
                    mAdapter.updateList(reviewlist2);
                    mAdapter.notifyDataSetChanged();
                }

                else if(selected.equals("Most recent")){
                    ArrayList<ReviewsResults> mr = new ArrayList<>();
                    for(int i=0; i<mostRecent(reviewslist).size(); i++) {
                        mr.add(mostRecent(reviewslist).get(i));
                    }

                    mAdapter.updateList(mr);
                    mAdapter.notifyDataSetChanged();
                }

                else if(selected.equals("Least recent")){
                    ArrayList<ReviewsResults> lr = new ArrayList<>();
                    for(int i=0; i<leastRecent(reviewslist).size(); i++) {
                        lr.add(leastRecent(reviewslist).get(i));
                    }
                    mAdapter.updateList(lr);
                    mAdapter.notifyDataSetChanged();
                }

                else if(selected.equals("Highest rating")) {
                    ArrayList<ReviewsResults> hr = new ArrayList<>();
                    for(int i=0; i<highestRating(reviewslist).size(); i++) {
                        hr.add(highestRating(reviewslist).get(i));
                    }
                    mAdapter.updateList(hr);
                    mAdapter.notifyDataSetChanged();
                }

                else if(selected.equals("Lowest rating")) {
                    ArrayList<ReviewsResults> lor = new ArrayList<>();
                    for(int i=0; i<lowestRating(reviewslist).size(); i++) {
                        lor.add(lowestRating(reviewslist).get(i));
                    }
                    mAdapter.updateList(lor);
                    mAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });






        return v;
    }


    ArrayList<ReviewsResults> mostRecent(ArrayList<ReviewsResults> reviews){
        //  ArrayList<ReviewsResults> res = new ArrayList<>();
        Collections.sort(reviews, new Comparator<ReviewsResults>() {
            @Override
            public int compare(ReviewsResults o1, ReviewsResults o2) {
                return Integer.parseInt(o2.getReviewTime()) - Integer.parseInt(o1.getReviewTime()); // Ascending
            }



        });

        return reviews;

    }

    ArrayList<ReviewsResults> leastRecent(ArrayList<ReviewsResults> reviews){
        //  ArrayList<ReviewsResults> res = new ArrayList<>();
        Collections.sort(reviews, new Comparator<ReviewsResults>() {
            @Override
            public int compare(ReviewsResults o1, ReviewsResults o2) {
                return Integer.parseInt(o1.getReviewTime()) - Integer.parseInt(o2.getReviewTime());

            }

        });

        return reviews;

    }

    ArrayList<ReviewsResults> highestRating(ArrayList<ReviewsResults> reviews){
        //  ArrayList<ReviewsResults> res = new ArrayList<>();
        Collections.sort(reviews, new Comparator<ReviewsResults>() {
            @Override
            public int compare(ReviewsResults o1, ReviewsResults o2) {
                return Integer.parseInt(o2.getReviewRating()) - Integer.parseInt(o1.getReviewRating());

            }

        });

        return reviews;

    }

    ArrayList<ReviewsResults> lowestRating(ArrayList<ReviewsResults> reviews){
        //  ArrayList<ReviewsResults> res = new ArrayList<>();
        Collections.sort(reviews, new Comparator<ReviewsResults>() {
            @Override
            public int compare(ReviewsResults o1, ReviewsResults o2) {
                return Integer.parseInt(o1.getReviewRating()) - Integer.parseInt(o2.getReviewRating());

            }

        });

        return reviews;

    }



}
