package com.example.shivanisingh.webhw9;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.MyViewHolder> {

    private List<ReviewsResults> reviewList;
    private Context context;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, time, text;
        RatingBar ratingBar;
        public ImageView img;
        public View vm;


        public MyViewHolder(View view) {
            super(view);
            vm = view;
            RatingBar rating = (RatingBar) view.findViewById(R.id.rating);

            name = (TextView) view.findViewById(R.id.name);
            ratingBar = (RatingBar) view.findViewById(R.id.rating);
            time = (TextView) view.findViewById(R.id.time);
            text  = (TextView) view.findViewById(R.id.reviewtext);
            img = (ImageView) view.findViewById(R.id.icon);

        }
    }


    public ReviewsAdapter(List<ReviewsResults> reviews, Context context) {
        this.reviewList = reviews;
        this.context = context;

    }

    public void updateList(List<ReviewsResults> favourites) {
        //  searchList.clear();
        this.reviewList = favourites;
        //  this.searchList = favourites;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.reviews_row, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ReviewsResults rs = reviewList.get(position);
        holder.name.setText(rs.getReviewName());

        Timestamp stamp = new Timestamp(Integer.parseInt(rs.getReviewTime()));
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = new Date(stamp.getTime());
        String dm = dt.format(date).toString();

        holder.time.setText(dm);
        holder.text.setText(rs.getReviewText());
        holder.ratingBar.setRating(Float.parseFloat(rs.getReviewRating()));
        Picasso.get()
                .load(rs.getReviewImage())
                .resize(50, 50)
                .centerCrop()
                .into(holder.img);

        holder.vm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri builtUri = null;
                try {
                    builtUri = Uri.parse(rs.getAuthorurl())
                            .buildUpon()
                            .build();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, builtUri);
                    context.startActivity(browserIntent);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }




    @Override
    public int getItemCount() {
        return reviewList.size();
    }
}

