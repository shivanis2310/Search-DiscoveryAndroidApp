package com.example.shivanisingh.webhw9;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okio.Utf8;

public class DetailsTabActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public PlaceDetails detailResult;
    String jsonDetails;
    String location;
    String address;
    String phone;
    String rating;
    String pageURL;
    String website;
    String place_id;
    String latitude;
    String longitude;
    String price;
    String name;
    String placeName;
    String placeAddress;
    String placeImg;
    String placeId;
    Menu menu;
    SearchResults fav;
    FavSharedPref sharedPreference;
    private int[] tabIcons = {
            R.drawable.info,
            R.drawable.photos,
            R.drawable.maps,
            R.drawable.reviews

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_tab);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new InfoFragment(), "INFO");
        adapter.addFragment(new PhotosFragment(), "PHOTOS");
        adapter.addFragment(new MapFragment(), "MAP");
        adapter.addFragment(new ReviewsFragment(), "REVIEWS");

        viewPager.setAdapter(adapter);


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        jsonDetails = getIntent().getStringExtra("details");
        if(jsonDetails==null){
            jsonDetails = "";
        }
        placeId = getIntent().getStringExtra("placeName");
        placeAddress = getIntent().getStringExtra("placeAddress");
        placeName = getIntent().getStringExtra("placeName");
        placeImg = getIntent().getStringExtra("placeImg");
        sharedPreference = new FavSharedPref();
        fav = new SearchResults(placeName,placeAddress,placeImg,placeId);
        try {

            JSONObject jsonObj = new JSONObject(jsonDetails);
            name = jsonObj.getString("name");

            if(name==null){
                name = "";
            }
            else {
                getSupportActionBar().setTitle(name);

            }
            String geometry = jsonObj.getString("geometry");
            if(geometry==null){
                geometry = "";
            }
            JSONObject jsonGeom = new JSONObject(geometry);
            location = jsonGeom.getString("location");

            JSONObject jsonLoc = new JSONObject(location);
            latitude = jsonLoc.getString("lat");
            longitude = jsonLoc.getString("lng");
            place_id = jsonObj.getString("place_id");

            if(place_id==null){
                place_id = "";
            }
            address = jsonObj.getString("formatted_address");
            if(address==null){
                address = "";
            }
            phone = jsonObj.getString("international_phone_number");
            if(phone==null){
                phone = "";
            }
            rating = jsonObj.getString("rating");
            if(rating==null){
                rating = "";
            }
            pageURL = jsonObj.getString("url");
            if(pageURL==null){
                pageURL = "";
            }
            website = jsonObj.getString("website");
            if(website==null){
                website = "";
            }
            price = jsonObj.getString("price_level");
            if(price==null){
                price = "";
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public String getJsonDetails()
    {
        return jsonDetails;
    }
    public String getName() { return name;}
    public String getDetailsLatitude(){
        return latitude;
    }

    public String getDetailsLongitude(){
        return longitude;
    }

    public String getDetailsPlaceID(){
        return place_id;
    }

    public String getAddress(){
        return address;
    }

    public String getPhone(){
        return phone;
    }

    public String getRating(){
        return rating;
    }

    public String getURL(){
        return pageURL;
    }

    public String getWebsite(){
        return website;
    }
    public String getPrice() { return  price;}


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            Drawable image = getDrawable(tabIcons[position]);
            image.setBounds(0, 0, image.getIntrinsicWidth()-10, image.getIntrinsicHeight()-10);
            // Replace blank spaces with image icon
            SpannableString sb = new SpannableString("   " + mFragmentTitleList.get(position));
            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
            sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if(checkFavoriteItem(fav))
            menu.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.heart_fill_white));
        else
            menu.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.heart_outline_white));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            // action with ID action_refresh was selected
            case R.id.action_share:
                Toast.makeText(this, "Share selected", Toast.LENGTH_SHORT)
                        .show();
                final String TwitterURL =
                        "https://twitter.com/intent/tweet";

                String web=  getWebsite() != null ? getWebsite() : getURL();
                final String QUERY_TEXT = "Check out " + getName() + " located at " + getAddress() + "\n" + "Website: " + web;

                Uri builtUri = null;

                try {
                    builtUri = Uri.parse(TwitterURL)
                            .buildUpon()
                            .appendQueryParameter("text", QUERY_TEXT)
                            .build();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, builtUri);
                startActivity(browserIntent);
                break;
            // action with ID action_settings was selected
            case R.id.action_favorite:
                if (item.getIcon().getConstantState().equals(
                        ResourcesCompat.getDrawable(getResources(),R.drawable.heart_outline_white, null).getConstantState()
                )) {
                    menu.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.heart_fill_white));
                    sharedPreference.addFavorite(this, fav);
                    Toast.makeText(this, fav.getName()+" was added to favorites",
                            Toast.LENGTH_SHORT).show();
                }

                else
                {
                    menu.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.heart_outline_white));
                    sharedPreference.removeFavorite(this, fav);
                    Toast.makeText(this,fav.getName()+" was removed from favorites",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }

        return true;
    }

    public boolean checkFavoriteItem(SearchResults checkProduct) {
        boolean check = false;
        List<SearchResults> favorites = sharedPreference.getFavorites(this);

        if (favorites != null) {
            for (SearchResults sr : favorites) {
                if (sr.getPlaceId().equals(checkProduct.getPlaceId())) {
                    check = true;
                    break;
                }
            }
        }
        return check;

    }

}
