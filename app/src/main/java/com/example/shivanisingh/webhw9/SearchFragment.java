package com.example.shivanisingh.webhw9;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SearchFragment extends Fragment {

    EditText keyword;
    Spinner categories;
    EditText distance;
    AutoCompleteTextView autocompleteView;
    String dist;
    String selectedCategory;
    String HTTP_JSON_URL = "http://csci571shivani.us-east-2.elasticbeanstalk.com";
    static JSONArray resp;
    String results;
    String nextpagetoken;
    String otherlocation;
    boolean flag;
    String key1;
    String cat1;
    String dist1;
    String otherl;
    TextView norecords;
    ProgressDialog progress;

    RequestQueue requestQueue ;
    public SearchFragment() {
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
        flag = true;
        final  View v = inflater.inflate(R.layout.searchtab, container, false);

        String [] values =
                {"Default","Airport","Amusement Park","Aquarium","Art Gallery","Bakery","Bar","Beauty Salom","Bowling Alley", "Bus Station","Cafe","Campground","Car Rental","Casino","Lodging","Movie Theater","Museum","Night Club","Park","Parking","Restaurant","Shopping Mall","Stadium","Subway Station","Taxi Stand","Train Station","Transit Station","Travel Agency","Zoo"};
        categories = (Spinner) v.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        categories.setAdapter(adapter);

        progress = new ProgressDialog(getActivity());
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setMessage("Fetching results");


        categories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categories.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        Button button = (Button) v.findViewById(R.id.button2);
        Button clear = (Button) v.findViewById(R.id.clearbutton);
        keyword = (EditText) v.findViewById(R.id.editText);
        distance = (EditText) v.findViewById(R.id.editText2);
        final RadioGroup rg = (RadioGroup) v.findViewById(R.id.radioSearch);
        final  TextView ol = (TextView) v.findViewById(R.id.errorOtherloc);
        final  TextView tk = (TextView) v.findViewById(R.id.errorKeyword);
        final RadioButton selectedRadioButton = (RadioButton) v.findViewById(R.id.radiocurrent);
        final RadioButton selectedRadioButton2 = (RadioButton) v.findViewById(R.id.radioother);
        final AutoCompleteTextView autocompleteView = (AutoCompleteTextView) v.findViewById(R.id.autocomplete);
        autocompleteView.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.autocomplete_list_item));

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ol.setVisibility(View.GONE);
                tk.setVisibility(View.GONE);
                keyword.setText("");
                selectedRadioButton.setChecked(true);
                selectedRadioButton2.setChecked(false);
                distance.setText("");
                categories.setSelection(0);
                autocompleteView.setText("");



            }
        });

        autocompleteView.setEnabled(false);

        final KeyListener variable;
        variable = autocompleteView.getKeyListener();


        View.OnClickListener otherLocRadioListener = new View.OnClickListener(){
            public void onClick(View v) {
                autocompleteView.setEnabled(true);
                autocompleteView.setKeyListener(variable);
            }
        };


        View.OnClickListener currentLocRadioListener = new View.OnClickListener(){
            public void onClick(View v) {
                autocompleteView.setText("");
                autocompleteView.setKeyListener(null);

            }
        };

        selectedRadioButton2.setOnClickListener(otherLocRadioListener);
        selectedRadioButton.setOnClickListener(currentLocRadioListener);




        autocompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get data associated with the specified position
                // in the list (AdapterView)
                otherlocation = (String) parent.getItemAtPosition(position);

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dist = distance.getText().toString();
                if (dist.equals("")) {
                    Double i = 10 * 1609.34;
                    String s = i.toString();
                    dist = s;
                } else {
                    Double d = Double.parseDouble(dist);
                    d = d * 1609.34;
                    String s = d.toString();
                    dist = s;
                }

                if(keyword.getText()==null || keyword.getText().toString().equals("")){

                    tk.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(),"Please fix all fields with errors",Toast.LENGTH_SHORT).show();
                    return;
                }


                int selectedRadioButtonID = rg.getCheckedRadioButtonId();

                if (selectedRadioButtonID== R.id.radioother && (otherlocation==null || otherlocation.equals(""))) {
                    ol.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(),"Please fix all fields with errors",Toast.LENGTH_SHORT).show();
                    return;
                }
                if((keyword.getText()!=null && !keyword.getText().equals(""))||(rg.getCheckedRadioButtonId()== R.id.radioother && (otherlocation!=null || !otherlocation.equals("")))) {
                    RequestQueue queue = Volley.newRequestQueue(getActivity());
                    try {
                        key1 = URLEncoder.encode(keyword.getText().toString(),"UTF-8");
                        cat1 = URLEncoder.encode(selectedCategory.replaceAll(" ","_").toLowerCase(),"UTF-8");
                        dist1 = URLEncoder.encode(dist,"UTF-8");
                        if(otherlocation!=null && !otherlocation.equals(""))
                            otherl = URLEncoder.encode(otherlocation,"UTF-8");

                        else
                            otherl = "";

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    HTTP_JSON_URL = "http://csci571shivani.us-east-2.elasticbeanstalk.com?keyword=" + key1 + "&category=" + cat1 + "&distance=" + dist1 +
                            "&currentlat=34.0205&currentlon=-118.2856&otherlocation=" + otherl + "&pagetoken=";

                    progress.show();

                    JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, HTTP_JSON_URL, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            try {


                                results = response.getString("results").toString();
                                if (response.has("next_page_token"))
                                    nextpagetoken = response.getString("next_page_token");
                                Intent myIntent = new Intent(getActivity(), ResultsTableActivity.class);
                                myIntent.putExtra("keyword", keyword.getText().toString());
                                myIntent.putExtra("distance", dist);
                                myIntent.putExtra("category", selectedCategory);
                                myIntent.putExtra("currentLat", "34.0224");
                                myIntent.putExtra("currentLong", "-118.2851");
                                myIntent.putExtra("otherlocation", otherlocation);
                                myIntent.putExtra("jsonArray", results);
                                myIntent.putExtra("nextpagetoken", nextpagetoken);
                                progress.hide();
                                startActivity(myIntent);

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
        });
        return v;
    }

}

