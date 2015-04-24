package com.parse.loginsample.basic;

import android.webkit.GeolocationPermissions;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Ayzrl Skinwalker on 18/4/2558.
 */
public class ChildOption {
    public int battery;
    public LatLng childlatlng;
    public Boolean plugged;
    ChildOption(int battery ,LatLng childlatlng,boolean plugged){
        this.battery = battery;
        this.childlatlng = childlatlng;
        this.plugged = plugged;
    }
}
