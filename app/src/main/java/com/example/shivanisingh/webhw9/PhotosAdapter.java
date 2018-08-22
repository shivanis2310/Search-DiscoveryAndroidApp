package com.example.shivanisingh.webhw9;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.MyViewHolder> {

    private List<Bitmap> photos;
    private Context context;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView img;
        public View vm;

        public MyViewHolder(View view) {
            super(view);
            vm = view;
            img = (ImageView) view.findViewById(R.id.icon);

        }
    }


    public PhotosAdapter(List<Bitmap> photos, Context context) {
        this.photos = photos;
        this.context = context;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Bitmap photo = photos.get(position);
        holder.img.setImageBitmap(photo);
        holder.vm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(v.getContext(), "You Clicked photo", Toast.LENGTH_SHORT).show();

            }

        });






    }




    @Override
    public int getItemCount() {
        return photos.size();
    }
}

