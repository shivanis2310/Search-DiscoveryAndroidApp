package com.example.shivanisingh.webhw9;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import org.w3c.dom.Text;


public class InfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    public DetailsTabActivity des;

    public InfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_info, container, false);
        des = (DetailsTabActivity) getActivity();
        TextView address = v.findViewById(R.id.address);
        address.setText(des.getAddress());
        if(des.getAddress() == null) {
            LinearLayout addresslayout = (LinearLayout) v.findViewById(R.id.address_layout);
            addresslayout.setVisibility(View.GONE);
        }
        TextView phone = v.findViewById(R.id.phone);
        phone.setText(des.getPhone());
        if(des.getPhone() == null) {
            LinearLayout phonelayout = (LinearLayout) v.findViewById(R.id.phone_layout);
            phonelayout.setVisibility(View.GONE);
        }

        String price = des.getPrice();
        StringBuilder dollar = new StringBuilder();
        if(price != null){
            int p = Integer.parseInt(price);
            for( int i=0; i<p; i++){
                dollar.append("$");

            }

        }

        TextView page = v.findViewById(R.id.page);
        page.setText(des.getURL());
        if(des.getURL() == null) {
            LinearLayout pagelayout = (LinearLayout) v.findViewById(R.id.page_layout);
            pagelayout.setVisibility(View.GONE);
        }

        TextView web = v.findViewById(R.id.website);
        web.setText(des.getWebsite());
        if(des.getWebsite() == null) {
            LinearLayout weblayout = (LinearLayout) v.findViewById(R.id.web_layout);
            weblayout.setVisibility(View.GONE);
        }

        RatingBar rating = (RatingBar) v.findViewById(R.id.rating);
        if(des.getRating() == null) {
            LinearLayout ratinglayout = (LinearLayout) v.findViewById(R.id.rating_layout);
            ratinglayout.setVisibility(View.GONE);
        }

        else
            rating.setRating(Float.parseFloat(des.getRating()));

        TextView pricel = v.findViewById(R.id.price);
        pricel.setText(dollar.toString());
        if(des.getPrice() == null) {
            LinearLayout pricelayout = (LinearLayout) v.findViewById(R.id.price_layout);
            pricelayout.setVisibility(View.GONE);

        }
        // Inflate the layout for this fragment
        return v;
    }

}
