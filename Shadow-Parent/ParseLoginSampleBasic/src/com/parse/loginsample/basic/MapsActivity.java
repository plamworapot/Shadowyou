package com.parse.loginsample.basic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.ui.IconGenerator;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity  {

    private GoogleMap mMap;
    ArrayList<AreaOption> arealist = new ArrayList<AreaOption>();
    ArrayList<ChildOption> childlist = new ArrayList<ChildOption>();


    ArrayList<LatLng> markPosit = new ArrayList<LatLng>();
    ArrayList<ArrayList<LatLng>> polyPosit = new ArrayList<ArrayList<LatLng>>(); //เก็บ markposit
    ArrayList<LatLng> arrayPoints = null;
    PolygonOptions polygonOptions;
    String android_id;
    int chk = 1;
    int selectchk =0;
    int i=0;
    boolean first_load = true;
    Gson g = new Gson();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        Button btn_select = (Button)findViewById(R.id.button2);
        Button btn_delete = (Button)findViewById(R.id.button3);
        Button btn_add = (Button)findViewById(R.id.button_add);
        final Button btn_left = (Button)findViewById(R.id.button_left);
        final Button btn_right = (Button)findViewById(R.id.button_right);
//      Parse.enableLocalDatastore(this);
//      Parse.initialize(this, "gBRDBgkOHQmO5AwTB0TsByWqCHaAmjuLfd25xQWJ", "0cVOTgzS7ZKXewFHuJz4nYb5GSkYayU37OSFrRcv"); //ayzrl
        Parse.initialize(this, "BlDypkDXDLkGHQSor5PJBIY9y3LrI4U9F7W7Maxq", "fpyiTXf1PcFHofcGiDKOgHqc08RO2xiDdhCxxxgl"); //plam
        android_id = Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID);

        Type type = new TypeToken<List<String[]>>() {}.getType();
//        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
//        installation.put("deviceId", android_id);
//        installation.saveInBackground();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Area");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject row, ParseException e) {
                if (e == null) {

                    String jsonArea = row.getString("Area");
                    Type listType = new TypeToken<ArrayList<AreaOption>>(){}.getType();
                    ArrayList<AreaOption> mylist = new Gson().fromJson(jsonArea,listType);
                    arealist = mylist;
                    redraw();
                }else{
                    redraw();
                }
            }
        });


        arrayPoints = new ArrayList<LatLng>();

        //Add polygon
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chk = 0;
            }
        });

        //Click map and Add marker
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (chk == 0){
                    Log.i("", "Chk = 0");
                    Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(latLng.latitude,latLng.longitude))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_pole))

                    );
                    markPosit.add(marker.getPosition());
                    arrayPoints.add(latLng);
                    Log.i("",""+markPosit);
                }else{
                    Log.i("", "Chk != 0");
                }
            }
        });

        //create polygon
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.i("","chk============="+chk);
                if(chk==0){
                    if (arrayPoints.get(0).equals(marker.getPosition())) {
                        countPolygonPoints();
                        polyPosit.add(markPosit);
                        markPosit = new ArrayList<LatLng>();
                        System.out.println(""+polyPosit);
                    }
                }

                return true;
            }
        });


        //select polygon
        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectchk == 0){
                    selectchk =1;
                    polygonOptions = new PolygonOptions();
                    if(arealist.size() > 0){

                        btn_left.setVisibility(View.VISIBLE);
                        btn_right.setVisibility(View.VISIBLE);
                        i= arealist.size()-1;
                        selectArea(i);
                        btn_left.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(arealist.size()>0){
                                    i--;
                                    if(i < 0){
                                        i = arealist.size()-1;
                                    }
                                    selectArea(i);
                                }
                            }
                        });
                        btn_right.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(arealist.size()>0) {
                                    i++;
                                    if (i > arealist.size() - 1) {
                                        i = 0;
                                    }
                                    selectArea(i);
                                }
                            }
                        });
                    }
                }

            }
        });

        //delete selected polygon
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectchk == 1){
                    if(arealist.size() > 0){
                        arealist.remove(i);
                        redraw();
                        i= arealist.size()-1;
                    }
                    sendData();
                }
                btn_left.setVisibility(View.INVISIBLE);
                btn_right.setVisibility(View.INVISIBLE);
                selectchk =0;
            }
        });

    }
    private void sendData() {
        Log.i("","senddata");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Area");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> rows, ParseException e) {
                if (e == null) {
                    Log.d("score", "row deviceID " + rows.size());
                    String json = new Gson().toJson(arealist);

                    if(rows.size()==0){
                        ParseObject AreaObject = new ParseObject("Area");
                        AreaObject.put("user", ParseUser.getCurrentUser());
                        AreaObject.put("Area",json);
                        AreaObject.saveInBackground();
                    }else{
                        ParseObject row = rows.get(0);
//                        Log.i("",""+row.getObjectId());
                        row.put("Area",json);
                        row.saveInBackground();
                    }



                }
            }
        });
    }
    private void getChildPosition(){
        if(childlist.size()==0){
            final ParseQuery<ParseObject> query = ParseQuery.getQuery("Parent_relation_child");
            query.include("device");
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> rows, ParseException e) {
                    // results has the list of users with a hometown team with a winning record
                    if(e == null && rows.size() > 0){
                        int device_count = 0;
                        for(ParseObject row:rows){
                            device_count++;
                            ParseObject device = row.getParseObject("device");
                            ParseGeoPoint child_latlng = device.getParseGeoPoint("location");
                            int battery = device.getInt("level");
                            Boolean plugged = device.getBoolean("plugged");
                            String charge = plugged == true ? "(Charging)" : "" ;
                            LatLng childlatlng = new LatLng(child_latlng.getLatitude(),child_latlng.getLongitude());
                            if(device_count == 1){
                                panCamera(childlatlng);
                            }
                            Marker child = mMap.addMarker(new MarkerOptions()
                                     .position(new LatLng(child_latlng.getLatitude(),child_latlng.getLongitude()))
                                     .icon(BitmapDescriptorFactory.fromResource(R.drawable.child))
                                     .title("CHILD NAME")
                                     .snippet("BATTERY :"+battery+" "+charge)
                            );
                            child.showInfoWindow();
                            childlist.add(new ChildOption(battery,childlatlng,plugged));
                        }
                    }
                }
            });

        }else {
            for(ChildOption child :childlist){
                String charge = child.plugged == true ? "(Charging)" : "" ;

                mMap.addMarker(new MarkerOptions()
                                .position(child.childlatlng)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.child))
                                .title("CHILD NAME")
                                .snippet("BATTERY :"+child.battery+" "+charge)
                );
            }
        }

    }
    public void panCamera(LatLng latlng){
        CameraPosition now = new CameraPosition.Builder().target(latlng).zoom(14.5f).build();
        if(first_load){
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(now));
            first_load = false;
        }else{
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(now));
        }
    }
    public void redraw(){
        mMap.clear();
        getChildPosition();
//        Log.i("",""+g.toJson(arealist));
        if(childlist.size()==0 && arealist.size()>0){
            panCamera(arealist.get(0).getCentroid());
        }
        for(AreaOption area:arealist){
            //polygon
            PolygonOptions polyoption = new PolygonOptions();
            polyoption.addAll(area.latlnglist);
            polyoption.strokeColor(Color.BLUE);
            polyoption.strokeWidth(7);
            mMap.addPolygon(polyoption);
            //label
            IconGenerator factory = new IconGenerator(getApplicationContext());
            Bitmap icon = factory.makeIcon(area.label);
            MarkerOptions mko = new MarkerOptions().position(area.getCentroid()).icon(BitmapDescriptorFactory.fromBitmap(icon));
            mMap.addMarker(mko);
        }


    }
    private void selectArea(int i) {
        mMap.clear();
//        Log.i("", "" + g.toJson(arealist));
        int count = 0 ;
        for(AreaOption area:arealist){
            //polygon
            PolygonOptions polyoption = new PolygonOptions();
            polyoption.addAll(area.latlnglist);
            if(count!=i){
                polyoption.strokeColor(Color.BLUE);
            }else{
                polyoption.strokeColor(Color.RED);
            }
            polyoption.strokeWidth(7);
            mMap.addPolygon(polyoption);
            //label
            IconGenerator factory = new IconGenerator(getApplicationContext());
            Bitmap icon = factory.makeIcon(area.label);
            MarkerOptions mko = new MarkerOptions().position(area.getCentroid()).icon(BitmapDescriptorFactory.fromBitmap(icon));
            mMap.addMarker(mko);
            count++;
        }
        panCamera(arealist.get(i).getCentroid());
    }
    public void countPolygonPoints() {
        if (arrayPoints.size() >= 3) {
            //dialog

            AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
            Context context = MapsActivity.this;


            builder.setTitle("Add Area Name");
            builder.setMessage("Set your Area Name");

            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);

            final EditText textArea = new EditText(context);
            textArea.setHint("Insert Area Name");
            layout.addView(textArea);


            builder.setView(layout);
            builder.setPositiveButton("Save",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            String temp_label = textArea.getText().toString();
                            arealist.add(new AreaOption(temp_label, arrayPoints));
                            arrayPoints.clear();
                            redraw();
                            sendData();
                            chk = 1;

                        }
                    });
            builder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            // DO TASK
                            arrayPoints.clear();
                            redraw();
                            chk =1;
                        }
                    });


            final AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

            textArea.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before,
                                          int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // Check if edit text is empty
                    if (TextUtils.isEmpty(s)) {
                        // Disable ok button
                        (dialog).getButton(
                                AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    } else {
                        // Something into edit text. Enable the button.
                        (dialog).getButton(
                                AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    }

                }
            });
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        childlist.clear();
        first_load = false;
        redraw();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "SETTING");

        MenuItem item = menu.getItem(0);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS|MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case 0:
                Intent prefA = new Intent(this, SettingsActivity.class);
                startActivityForResult(prefA, 0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }


    private void setUpMap() {
//        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
//
    }



}
