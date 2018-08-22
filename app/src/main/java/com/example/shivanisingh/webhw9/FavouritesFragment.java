package com.example.shivanisingh.webhw9;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FavouritesFragment extends Fragment {

    List<SearchResults> favorites = new ArrayList<>();
    private FavSharedPref sharedPreference;
    RecyclerView recyclerView;
    FavouritesAdapter mAdapter;
    public MainActivity m;
    TextView norecords;

    public FavouritesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favtab, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        norecords = (TextView) view.findViewById(R.id.norecords);
        mAdapter = new FavouritesAdapter(favorites, getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        sharedPreference = new FavSharedPref();
        favorites = sharedPreference.getFavorites(getActivity());
        if(favorites.size()==0){
            norecords.setVisibility(View.VISIBLE);
        }

        else {
            norecords.setVisibility(View.GONE);
        }

        mAdapter.updateList(favorites);
        mAdapter.notifyDataSetChanged();

        return view;
    }


    @Override
    public void onResume(){
        super.onResume();
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }



}