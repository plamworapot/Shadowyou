package com.shadowu.parent;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
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
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.parsers.ParserConfigurationException;

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
    boolean firstload;
    Gson g = new Gson();
    DatePicker finish_date;
    DatePicker start_date;
    AlertDialog Date_dialog;
    int chk_t = 0;
    Button listview;
    List<ParseObject> child_history = new ArrayList<ParseObject>();
    ProgressDialog progress;
    ImageButton btn_add;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        firstload = true;
        ImageButton btn_select  = (ImageButton)findViewById(R.id.imageButton_select);
        btn_add     = (ImageButton)findViewById(R.id.imageButton_add);
        ImageButton btn_delete  = (ImageButton)findViewById(R.id.imageButton_delete);
        ImageButton btn_child_management     = (ImageButton)findViewById(R.id.imageButton_child_management);
        ImageButton btn_history = (ImageButton)findViewById(R.id.imageButton_history);
        ImageButton btn_cancel = (ImageButton)findViewById(R.id.imageButton_cancel);
        final TableRow main = (TableRow)findViewById(R.id.main_table);
        final TableRow slave = (TableRow)findViewById(R.id.second_table);
        progress = ProgressDialog.show(this, "ShadowU",
                "Loading...", true);

        final ImageButton btn_left = (ImageButton)findViewById(R.id.imageButton_left);
        final ImageButton btn_right = (ImageButton)findViewById(R.id.imageButton_right);
        android_id = Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID);

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
                Toast.makeText(
                        MapsActivity.this,
                        "Area Mode",
                        Toast.LENGTH_LONG
                ).show();
                if(chk == 1){
                    btn_add.setImageResource(R.drawable.xmark);
                    chk = 0;
                }else{
                    btn_add.setImageResource(R.drawable.addarea);
                    arrayPoints.clear();
                    redraw();
                    chk =1;
                }
            }
        });

        btn_child_management.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AddChildActivity.class);
                startActivity(intent);
            }
        });

        /* arrayPoints.clear();
                            redraw();
                            chk =1;*/


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
                if(chk==0){
                    if (arrayPoints.get(0).equals(marker.getPosition())) {
                        countPolygonPoints();
                        polyPosit.add(markPosit);
                        markPosit = new ArrayList<LatLng>();
                        System.out.println(""+polyPosit);
                    }
                }else{
                    marker.showInfoWindow();
                }

                return true;
            }
        });


        //select polygon
        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(
                        MapsActivity.this,
                        "Select Area Mode",
                        Toast.LENGTH_LONG
                ).show();
                if(selectchk == 0){
                    selectchk =1;
                    polygonOptions = new PolygonOptions();
                    if(arealist.size() > 0){
                        slave.setVisibility(View.VISIBLE);
                        main.setVisibility(View.INVISIBLE);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                    Context context = MapsActivity.this;


                    builder.setTitle("Delete?");
                    builder.setMessage("Do you sure for delete this area? ");

                    LinearLayout layout = new LinearLayout(context);
                    layout.setOrientation(LinearLayout.VERTICAL);

                    builder.setView(layout);
                    builder.setPositiveButton("Save",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    if(arealist.size() > 0){
                                        arealist.remove(i);
                                        redraw();
                                        i= arealist.size()-1;
                                    }
                                    sendData();
                                    slave.setVisibility(View.INVISIBLE);
                                    main.setVisibility(View.VISIBLE);
                                    btn_left.setVisibility(View.INVISIBLE);
                                    btn_right.setVisibility(View.INVISIBLE);
                                    selectchk =0;
                                }
                            });
                    builder.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    // DO TASK

                                }
                            });


                    final AlertDialog dialog = builder.create();
                    dialog.show();


                }

            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(
                        MapsActivity.this,
                        "Cancel",
                        Toast.LENGTH_LONG
                ).show();
                redraw();
                selectchk =0;
                slave.setVisibility(View.INVISIBLE);
                main.setVisibility(View.VISIBLE);
            }
        });
        btn_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(
                        MapsActivity.this,
                        "History Mode",
                        Toast.LENGTH_LONG
                ).show();
                final ArrayList<String> deviceId = new ArrayList<String>();


                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                Context context = MapsActivity.this;


                builder.setTitle("Select Date ");

                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                final TextView start_text = new TextView(context);
                start_text.setText("Start Date");
                layout.addView(start_text);

                start_date = new DatePicker(context);
                start_date.setMaxDate(System.currentTimeMillis());
                layout.addView(start_date);

                final TextView to_text = new TextView(context);
                to_text.setText("to");
                layout.addView(to_text);

                finish_date = new DatePicker(context);
                finish_date.setMaxDate(System.currentTimeMillis());
                layout.addView(finish_date);

                listview = new Button(context);
                listview.setHint("Choose child to show");
                listview.setText(null);
                layout.addView(listview);
                    listview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final PopupMenu popup = new PopupMenu(MapsActivity.this,listview);
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Parent_relation_child");
                            query.whereEqualTo("user", ParseUser.getCurrentUser());
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> rows, ParseException e) {
                                    if (e == null && rows.size() > 0) {
                                        child_history = rows;
                                        int i =0;
                                        for (ParseObject row : rows) {
                                            popup.getMenu().add(0,i,i,row.getString("name").toString());
                                            i++;
                                        }
                                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                            public boolean onMenuItemClick(MenuItem item) {
                                                Toast.makeText(
                                                        MapsActivity.this,
                                                        "You Clicked : " + item.getTitle(),
                                                        Toast.LENGTH_LONG
                                                ).show();
                                                if(item.getTitle() == "Please select child"){
                                                    listview.setText("");
                                                }else{
                                                    listview.setText(item.getTitle().toString());
                                                    Log.i("item", "" + item.getItemId());
                                                    Globals.select_device = child_history.get(item.getItemId());
                                                }
                                                Log.i("temp",listview.getText().toString());
                                                return true;
                                            }
                                        });

                                        popup.show();
                                    }
                                }
                            });


                        }
                    });
                builder.setView(layout);
                builder.setPositiveButton("Open History",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                long c = new GregorianCalendar(
                                        start_date.getYear(),start_date.getMonth(),start_date.getDayOfMonth()
                                ).getTimeInMillis();
                                long d = new GregorianCalendar(
                                        finish_date.getYear(),finish_date.getMonth(),finish_date.getDayOfMonth()
                                ).getTimeInMillis();
                                String childname = listview.getText().toString();

                                Intent intent = new Intent(MapsActivity.this,MapsHistoryActivity.class);
                                intent.putExtra("start",c);
                                intent.putExtra("finish",d);
                                intent.putExtra("child_name",childname);

                                startActivity(intent);

                            }
                        });
                builder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                // DO TASK

                            }
                        });


                Date_dialog = builder.create();
                Date_dialog.show();
                Calendar ca = Calendar.getInstance();
                int mYear = ca.get(Calendar.YEAR);
                int mMonth = ca.get(Calendar.MONTH);
                int mDay = ca.get(Calendar.DAY_OF_MONTH);
                start_date.init(mYear,mMonth,mDay,dateSetListener);
                finish_date.init(mYear,mMonth,mDay,dateSetListener);

//                start_date.init();
                Date_dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        listview.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {


                    Date_dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    chk_t = 1;

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Check if edit text is empty
//                if (TextUtils.isEmpty(s)) {
//                    // Disable ok button
//                    (Date_dialog).getButton(
//                            AlertDialog.BUTTON_POSITIVE).setEnabled(false);
//                } else {
//                    // Something into edit text. Enable the button.
//                    (Date_dialog).getButton(
//                            AlertDialog.BUTTON_POSITIVE).setEnabled(true);
//                }

            }
        });
            }
        });

    }

    private DatePicker.OnDateChangedListener dateSetListener = new DatePicker.OnDateChangedListener() {

        public void onDateChanged(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

            long c = new GregorianCalendar(
                    start_date.getYear(),start_date.getMonth(),start_date.getDayOfMonth()
            ).getTimeInMillis();
            long d = new GregorianCalendar(
                    finish_date.getYear(),finish_date.getMonth(),finish_date.getDayOfMonth()
            ).getTimeInMillis();
            if(d>=c && listview.getText().toString() != ""){
                Date_dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
            }else{
                Date_dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            }

        }
    };




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
            query.whereEqualTo("user", ParseUser.getCurrentUser());
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
                            String child_name = row.getString("name");
                            Boolean plugged = device.getBoolean("plugged");
                            String charge = plugged == true ? "(Charging)" : "" ;
                            Calendar calendar = new GregorianCalendar();

//                            Log.i("timeago a",(calendar.getTimeInMillis() - row.getUpdatedAt().getTime() - 3600) +"");
                            Log.i("time_parse",device.getUpdatedAt().toString());
                            Log.i("time_andriod", calendar.getTime().toString());
                            String last_update = TimeAgo.toDuration(calendar.getTimeInMillis() - device.getUpdatedAt().getTime());
                            LatLng childlatlng = new LatLng(child_latlng.getLatitude(),child_latlng.getLongitude());
                            if(device_count == 1){
                                panCamera(childlatlng);
                            }
                            Marker child = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(child_latlng.getLatitude(),child_latlng.getLongitude()))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.child))
                                    .title(child_name+"  "+battery+"% "+charge+" ")
                                    .snippet("Last Update :"+last_update)

                            );
//                            child.showInfoWindow();
                            childlist.add(new ChildOption(child_name,battery,childlatlng,plugged,last_update));
                        }
                    }
                }
            });

        }else {
            for(ChildOption child :childlist){
                String charge = child.plugged == true ? "(Charging)" : "" ;

                Marker childMarker =mMap.addMarker(new MarkerOptions()
                                .position(child.childlatlng)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.child))
                                .title(child.name + " " + child.battery + "% " + charge + " ")
                                .snippet("Last Update :" +child.time)
                );
//                childMarker.showInfoWindow();
            }
        }

    }


    public void panCamera(LatLng latlng){
        CameraPosition now = new CameraPosition.Builder().target(latlng).zoom(14.5f).build();
        if(firstload){
            Log.i("firstload","true");
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(now));
            firstload = false;
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
        progress.dismiss();
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
                            btn_add.setImageResource(R.drawable.addarea);

                        }
                    });
            builder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            // DO TASK
                            arrayPoints.clear();
                            redraw();
                            chk =1;
                            btn_add.setImageResource(R.drawable.addarea);
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
//        firstload  = false;
        redraw();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "SETTING").setIcon(R.drawable.setting);

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