package com.example.shivanisingh.webhw9;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.android.volley.ExecutorDelivery;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;


public class MapFragment extends Fragment {


    Spinner travelmodes;
    String selectedMode;
    String dirResults;
    String points;
    GoogleMap map;
    String origin;
    AutoCompleteTextView ac;
    int currentItem = 0;
    String encodedorigin;
    String  encodedselected;
    DetailsTabActivity detailsTab;



    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        String[] values = {"Driving", "Bicycling", "Walking", "Transit"};
        travelmodes = (Spinner) v.findViewById(R.id.spinner_travel);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        travelmodes.setAdapter(adapter);

        SupportMapFragment mf;
        mf = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mf == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mf = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, mf).commit();
        }

        if (mf != null) {
            mf.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    if (googleMap != null) {
                        map = googleMap;
                        map.getUiSettings().setAllGesturesEnabled(true);
                        Double lat = Double.parseDouble(detailsTab.getDetailsLatitude());
                        Double lon = Double.parseDouble(detailsTab.getDetailsLongitude());
                        LatLng destPlace = new LatLng(lat, lon);


                        CameraPosition cameraPosition = new CameraPosition.Builder().target(destPlace).zoom(15.0f).build();
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                        map.moveCamera(cameraUpdate);
                        map.addMarker(new MarkerOptions().position(destPlace).title(detailsTab.getName()));

                    }

                }
            });

        }

        ac = (AutoCompleteTextView) v.findViewById(R.id.autocomplete);
        ac.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.autocomplete_list_item));
        ac.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get data associated with the specified position
                // in the list (AdapterView)
                origin = (String) parent.getItemAtPosition(position);
                Toast.makeText(getActivity(), origin, Toast.LENGTH_SHORT).show();
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                try {
                    encodedorigin = URLEncoder.encode(origin,"UTF-8");
                    encodedselected = URLEncoder.encode(selectedMode.replaceAll(" ","_").toLowerCase(),"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                String dirURL = "https://maps.googleapis.com/maps/api/directions/json?origin=" + encodedorigin + "&destination=" + detailsTab.getDetailsLatitude()
                        + "," + detailsTab.getDetailsLongitude() + "&mode=" + encodedselected + "&key=AIzaSyDpldXJN2As4TpLrGxSFcoy0Za76V-JA2Q";

                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, dirURL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            dirResults = response.getString("routes").toString();
                            JSONArray pL = new JSONArray(dirResults);
                            JSONObject firstRoute = pL.getJSONObject(0);
                            String poly = firstRoute.getString("overview_polyline").toString();
                            JSONObject pointObject = new JSONObject(poly);
                            JSONArray legs = firstRoute.getJSONArray("legs");
                            JSONObject startObject = legs.getJSONObject(0);
                            JSONObject start = startObject.getJSONObject("start_location");
                            String lati = start.getString("lat").toString();
                            String lngi = start.getString("lng").toString();
                            Double lat1 = Double.parseDouble(lati);
                            Double lon1 = Double.parseDouble(lngi);
                            LatLng ori = new LatLng(lat1, lon1);
                            points = pointObject.getString("points").toString();

                            Polyline polylineFinal;
                            map.clear();
                            PolylineOptions polylineOptions = new PolylineOptions();
                            List<LatLng> decodedPath = PolyUtil.decode(points);
                            polylineOptions.color(Color.BLUE);
                            polylineFinal = map.addPolyline(polylineOptions.addAll(decodedPath));
                            Double lat = Double.parseDouble(detailsTab.getDetailsLatitude());
                            Double lon = Double.parseDouble(detailsTab.getDetailsLongitude());
                            LatLng destPlace = new LatLng(lat, lon);
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(destPlace, 12));
                            map.addMarker(new MarkerOptions().position(destPlace));
                            map.addMarker(new MarkerOptions().position(ori));


                            //   Log.v("points", points);

                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v(MainActivity.class.getSimpleName(), "Error: " + error.getMessage());

                    }
                });
                queue.add(jsonRequest);
            }
        });

        detailsTab = (DetailsTabActivity) getActivity();
        travelmodes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                selectedMode = travelmodes.getSelectedItem().toString();
                if(currentItem == position){
                    return; //do nothing
                }
                else
                {
                    if(origin!=null) {
                        RequestQueue queue = Volley.newRequestQueue(getActivity());

                        String dirURL = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin + "&destination=" + detailsTab.getDetailsLatitude()
                                + "," + detailsTab.getDetailsLongitude() + "&mode=" + selectedMode.toLowerCase() + "&key=AIzaSyDpldXJN2As4TpLrGxSFcoy0Za76V-JA2Q";

                        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, dirURL, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                try {

                                    dirResults = response.getString("routes").toString();
                                    JSONArray pL = new JSONArray(dirResults);
                                    JSONObject firstRoute = pL.getJSONObject(0);
                                    String poly = firstRoute.getString("overview_polyline").toString();
                                    JSONObject pointObject = new JSONObject(poly);
                                    JSONArray legs = firstRoute.getJSONArray("legs");
                                    JSONObject startObject = legs.getJSONObject(0);
                                    JSONObject start = startObject.getJSONObject("start_location");
                                    String lati = start.getString("lat").toString();
                                    String lngi = start.getString("lng").toString();
                                    Double lat1 = Double.parseDouble(lati);
                                    Double lon1 = Double.parseDouble(lngi);
                                    LatLng ori = new LatLng(lat1, lon1);
                                    points = pointObject.getString("points").toString();
                                    Polyline polylineFinal;
                                    map.clear();
                                    PolylineOptions polylineOptions = new PolylineOptions();
                                    List<LatLng> decodedPath = PolyUtil.decode(points);
                                    polylineOptions.color(Color.BLUE);
                                    polylineFinal = map.addPolyline(polylineOptions.addAll(decodedPath));
                                    Double lat = Double.parseDouble(detailsTab.getDetailsLatitude());
                                    Double lon = Double.parseDouble(detailsTab.getDetailsLongitude());
                                    LatLng destPlace = new LatLng(lat, lon);
                                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(destPlace, 12));
                                    map.addMarker(new MarkerOptions().position(destPlace));
                                    map.addMarker(new MarkerOptions().position(ori).title("Your position"));

                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.v(MainActivity.class.getSimpleName(), "Error: " + error.getMessage());

                            }
                        });
                        queue.add(jsonRequest);
                    }
                }
                currentItem = position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Inflate the layout for this fragment
        return v;
    }


}


