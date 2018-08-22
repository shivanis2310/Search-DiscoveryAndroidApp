package com.example.shivanisingh.webhw9;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.MyViewHolder> {

    private List<SearchResults> searchList;
    private Context context;
    String results;
    FavSharedPref sharedPreference = new FavSharedPref();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, address;
        public ImageView img;
        public View vm;
        public Button heart;
        public MyViewHolder(View view) {
            super(view);
            vm = view;
            name = (TextView) view.findViewById(R.id.name);
            address = (TextView) view.findViewById(R.id.address);
            img = (ImageView) view.findViewById(R.id.icon);
            heart = (Button) view.findViewById(R.id.heart);
        }
    }


    public FavouritesAdapter(List<SearchResults> searchList, Context context) {
        this.searchList = searchList;
        this.context = context;

    }

    public void updateList(List<SearchResults> favourites) { this.searchList = favourites;}

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_table_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final  SearchResults sr = searchList.get(position);
        holder.name.setText(sr.getName());
        holder.address.setText(sr.getAddress());
        holder.heart.setBackgroundResource(R.drawable.heart_fill_red);
        holder.heart.setTag(R.drawable.heart_fill_red);

        final ProgressDialog progress;
        progress = new ProgressDialog(context);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setMessage("Fetching results");


        Picasso.get()
                .load(sr.getImage())
                .resize(50, 50)
                .centerCrop()
                .into(holder.img);

        holder.vm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue queue = Volley.newRequestQueue(context);
                String HTTP_JSON_URL = "http://csci571shivani.us-east-2.elasticbeanstalk.com";
                HTTP_JSON_URL = HTTP_JSON_URL + "?placeId=" + sr.getPlaceId();
                progress.show();
                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, HTTP_JSON_URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            results = response.getString("result").toString();
                            Intent myIntent = new Intent(context, DetailsTabActivity.class);
                            Log.v("beforeintent", "Value: " + results);
                            myIntent.putExtra("details", results);
                            myIntent.putExtra("placeName", sr.getName());
                            myIntent.putExtra("placeAddress", sr.getAddress());
                            myIntent.putExtra("placeImg", sr.getImage());
                            myIntent.putExtra("placeId", sr.getPlaceId());
                            progress.hide();
                            context.startActivity(myIntent);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v(ResultsTableActivity.class.getSimpleName(), "Error: " + error.getMessage());

                    }
                });
                queue.add(jsonRequest);
            }

        });

        holder.heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreference.removeFavorite(context, sr);
                searchList =sharedPreference.getFavorites(context);
                updateList(searchList);
                notifyDataSetChanged();
                v.setBackgroundResource(R.drawable.heart_outline_black);
                v.setTag(R.drawable.heart_outline_black);
                Toast.makeText(context,sr.getName()+" was removed from favorites", Toast.LENGTH_SHORT).show();
                notifyItemRemoved(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(searchList == null) {
            return 0;
        }

        return searchList.size();
    }
}
