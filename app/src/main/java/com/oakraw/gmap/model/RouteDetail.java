package com.oakraw.gmap.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Rawipol on 9/26/14 AD.
 */
public class RouteDetail {
    private LatLng start;
    private LatLng end;
    private int color;

    public RouteDetail(LatLng start, LatLng end, int color) {
        this.start = start;
        this.end = end;
        this.color = color;
    }

    public LatLng getStart() {
        return start;
    }

    public LatLng getEnd() {
        return end;
    }

    public int getColor() {
        return color;
    }
}
