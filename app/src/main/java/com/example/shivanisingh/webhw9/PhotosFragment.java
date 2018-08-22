package com.example.shivanisingh.webhw9;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class PhotosFragment extends Fragment {
    GeoDataClient mGeoDataClient;
    RecyclerView recyclerView;
    PhotosAdapter mAdapter;
    PlaceDetails pd;
    SearchResults r;
    DetailsTabActivity dt;
    TextView norecords;


    public PhotosFragment() {
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
        View v =  inflater.inflate(R.layout.fragment_photos, container, false);
        recyclerView = v.findViewById(R.id.photos_recycler);
        dt = (DetailsTabActivity) getActivity();
        mGeoDataClient = Places.getGeoDataClient(getActivity());
        norecords = (TextView)v.findViewById(R.id.norecords);
        getPhotos();
        return v;
    }



    private void getPhotos() {

        final String placeId = dt.getDetailsPlaceID();

        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                // Get the list of photos.
                PlacePhotoMetadataResponse photos = task.getResult();
                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                if(photoMetadataBuffer.getCount()!=0) {
                    final ArrayList<Bitmap> pics = new ArrayList<Bitmap>();
                    for (PlacePhotoMetadata photoMetadata : photoMetadataBuffer) {
                        //   PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(i);
                        CharSequence attribution = photoMetadata.getAttributions();
                        // Get a full-size bitmap for the photo.
                        Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                            @Override
                            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {

                                PlacePhotoResponse photo = task.getResult();
                                if (photo != null) {
                                    Bitmap bitmap = photo.getBitmap();
                                    pics.add(bitmap);
                                    String size = Integer.toString(pics.size());
                                    Log.v("photooooolistttttttttt", size);
                                    mAdapter = new PhotosAdapter(pics, getActivity());
                                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                                    recyclerView.setLayoutManager(mLayoutManager);
                                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                                    recyclerView.setAdapter(mAdapter);
                                    mAdapter.notifyDataSetChanged();
                                }


                            }
                        });
                    }
                    // Get the first photo in the list.
                }
                // Get the attribution text.

                else {

                    norecords.setVisibility(View.VISIBLE);

                }


            }
        });
    }



}

