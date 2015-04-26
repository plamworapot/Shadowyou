package com.shadowu.parent;

import android.webkit.GeolocationPermissions;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Ayzrl Skinwalker on 18/4/2558.
 */
public class ChildOption {
    public String name;
    public int battery;
    public LatLng childlatlng;
    public Boolean plugged;
    public String time;
    ChildOption(String name,int battery ,LatLng childlatlng,boolean plugged,String time){
        this.name = name;
        this.battery = battery;
        this.childlatlng = childlatlng;
        this.plugged = plugged;
        this.time = time;
    }
}
