package com.oakraw.gmap;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.oakraw.gmap.model.RouteDetail;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class StatisticActivity extends Activity {

    private GoogleMap googleMap;
    private String route;
    private String name;
    private LatLng fromPosition;
    private LatLng toPosition;
    private ArrayList<RouteDetail> routeDetails = new ArrayList<RouteDetail>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);


        name = getIntent().getStringExtra("name");
        route = getIntent().getStringExtra("route");

        getActionBar().setIcon(
                new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        getActionBar().setTitle(name);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            // Loading map
            initilizeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }

        decodeJson();

    }


    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();

            googleMap.getUiSettings().setZoomControlsEnabled(false);

            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }




            //googleMap.addMarker(new MarkerOptions().position(fromPosition).title("Start"));
            //googleMap.addMarker(new MarkerOptions().position(toPosition).title("End"));

        }
    }

    private void decodeJson(){
        if(route != null){
            try {
                LatLng sPos = new LatLng(13.685400079263206, 100.537133384495975);;
                JSONArray jsonArray = new JSONArray(route);
                for(int j=0; j<jsonArray.length();j++) {
                    JSONObject json = jsonArray.getJSONObject(j);
                    JSONObject detailJson = json.getJSONObject("detail");

                    double latitudeStart = detailJson.getDouble("startLat");
                    double longitudeStart = detailJson.getDouble("startLon");
                    fromPosition = new LatLng(latitudeStart, longitudeStart);
                    if(j==0)
                        sPos = fromPosition;
                    double latitudeEnd = detailJson.getDouble("endLat");
                    double longitudeEnd = detailJson.getDouble("endLon");
                    toPosition = new LatLng(latitudeEnd, longitudeEnd);

                    JSONArray arrayRoute = json.getJSONArray("route");
                    for (int i = 0; i < arrayRoute.length(); i++) {
                        JSONObject location = (JSONObject) arrayRoute.get(i);
                        LatLng startPosition = new LatLng(location.getDouble("startLat"), location.getDouble("startLon"));
                        LatLng endPosition = new LatLng(location.getDouble("endLat"), location.getDouble("endLon"));
                        int color = location.getInt("color");
                        routeDetails.add(new RouteDetail(startPosition, endPosition, color));
                        Log.d("myTag", location.toString());

                    }

                    Log.d("myTag", routeDetails.toString());
                    googleMap.addMarker(new MarkerOptions().position(fromPosition).title("Start"));
                    googleMap.addMarker(new MarkerOptions().position(toPosition).title("End"));
                    for (int i = 0; i < routeDetails.size(); i++) {
                        drawPolyline(routeDetails.get(i));
                    }


                }

                /*double lat = (jsonArray.getJSONObject(0).getJSONObject("detail").getDouble("startLat") + jsonArray.getJSONObject(0).getJSONObject("detail").getDouble("endLat"))/2;
                double lon = (jsonArray.getJSONObject(jsonArray.length()).getJSONObject("detail").getDouble("startLon") + jsonArray.getJSONObject(jsonArray.length()).getJSONObject("detail").getDouble("endLon"))/2;

                LatLng coordinates = new LatLng(lat,lon);*/
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sPos, 16));

            }catch (Exception e){

            }
        }
    }

    private void drawPolyline(RouteDetail det){
        PolylineOptions rectLine = new PolylineOptions().width(7).color(det.getColor())
                .add(det.getStart())
                .add(det.getEnd());

        googleMap.addPolyline(rectLine);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
