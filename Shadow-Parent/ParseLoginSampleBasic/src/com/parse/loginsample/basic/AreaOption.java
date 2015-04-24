package com.parse.loginsample.basic;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class AreaOption {
    public String label;
    public ArrayList<LatLng> latlnglist;
    AreaOption(String label,ArrayList<LatLng> latlnglist){
        ArrayList<LatLng> temp_latlnglist = new ArrayList<LatLng>();
        for (LatLng latlag : latlnglist ){
            temp_latlnglist.add(latlag);
        }
        this.label  =   label;
        this.latlnglist =   temp_latlnglist;

    }
    public LatLng getCentroid() {
        double[] centroid = { 0.0, 0.0 };
        for (int i = 0; i < latlnglist.size(); i++) {
            centroid[0] += latlnglist.get(i).latitude;
            centroid[1] += latlnglist.get(i).longitude;
        }
        int totalPoints = latlnglist.size();
        centroid[0] = centroid[0] / totalPoints;
        centroid[1] = centroid[1] / totalPoints;

        LatLng cent = new LatLng(centroid[0],centroid[1]);

        return cent;
    }
}
