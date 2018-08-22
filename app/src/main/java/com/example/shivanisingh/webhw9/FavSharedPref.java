package com.example.shivanisingh.webhw9;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FavSharedPref {

    public static final String PREFS_NAME = "SEARCH_APP";
    public static final String FAVORITES = "Place_Favorite";

    public FavSharedPref() {
        super();
    }


    public void saveFavorites(Context context, List<SearchResults> favorites) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);
        editor.putString(FAVORITES, jsonFavorites);
        editor.apply();
    }

    public void addFavorite(Context context, SearchResults place) {
        List<SearchResults> favorites = getFavorites(context);
        if (favorites == null)
            favorites = new ArrayList<SearchResults>();
        favorites.add(place);
        saveFavorites(context, favorites);
    }

    public void removeFavorite(Context context, SearchResults place) {
        ArrayList<SearchResults> favorites = getFavorites(context);
        if (favorites != null) {
            for(SearchResults sr : favorites) {
                if(sr.getPlaceId().equals(place.getPlaceId())){
                    favorites.remove(sr);
                    break;
                }
            }
            saveFavorites(context, favorites);
        }
    }

    public ArrayList<SearchResults> getFavorites(Context context) {
        SharedPreferences settings;
        List<SearchResults> favorites;
        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        if (settings.contains(FAVORITES)) {
            String jsonFavorites = settings.getString(FAVORITES, null);
            Gson gson = new Gson();
            SearchResults[] favoriteItems = gson.fromJson(jsonFavorites, SearchResults[].class);
            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<SearchResults>(favorites);
        } else
            return null;
        return (ArrayList<SearchResults>) favorites;
    }
}