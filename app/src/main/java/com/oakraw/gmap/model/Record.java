package com.oakraw.gmap.model;

import org.json.JSONObject;

/**
 * Created by Rawipol on 9/26/14 AD.
 */
public class Record {
    private int mId;
    private String mName;
    private String mDate;
    private String mRoute;

    public Record(int id, String name, String date,String route){
        mId = id;
        mName = name;
        mDate = date;
        mRoute = route;
    }

    public int getmId() {
        return mId;
    }

    public String getmName() {
        return mName;
    }

    public String getmDate() {
        return mDate;
    }

    public String getmRoute() {
        return mRoute;
    }
}
