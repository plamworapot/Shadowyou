package com.parse.loginsample.basic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MapsHistoryActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    int j;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_history);
        setUpMapIfNeeded();
        long start = getIntent().getExtras().getLong("start");
        long stop = getIntent().getExtras().getLong("finish");
        String child_name = getIntent().getExtras().getString("child_name");
        String start_date = MillsToDate(start);
        String stop_date = MillsToDate(stop);
        Log.i("starts",start_date);
        Log.i("startf",stop_date);
        Log.i("start_child",child_name);
        ArrayList<LatLng> line = new ArrayList<LatLng>();
        line.add(new LatLng(13.639199, 100.524985));
        line.add(new LatLng(13.638657, 100.526884));
        line.add(new LatLng(13.640972, 100.525972));
        line.add(new LatLng(13.641837, 100.528997));
        line.add(new LatLng(13.639429, 100.525049));
        Log.i("array_list",""+line.size());
        DrawPolyline(line);

    }
    public String MillsToDate(long mills){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(mills);
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
        return formatter.format(cal.getTime());

    }
    public void DrawPolyline(ArrayList<LatLng> latlng){
        for(int i=0;i<latlng.size()-1;i++){
            int temp = i+1;
            for(int j=temp;;){

                mMap.addPolyline(new PolylineOptions()
                        .add(latlng.get(i), latlng.get(j))
                        .width(5)
                        .color(Color.RED));
//                Log.i("line","i = "+i+",j = "+j+",latlng size = "+latlng.size());
                break;
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

    }
}
