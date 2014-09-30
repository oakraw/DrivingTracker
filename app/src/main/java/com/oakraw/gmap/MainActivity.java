package com.oakraw.gmap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.oakraw.gmap.model.RouteDetail;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.util.ArrayList;


public class MainActivity extends Activity {

    private String name = "Record";
    private GoogleMap googleMap;
    LatLng fromPosition;
    LatLng toPosition;
    private int polyLineColor = Color.GREEN;

    private int delay = 1000;
    private Handler handler = new Handler();

    private final int count = 20;
    private int c = count;
    private int from = 0;
    private int to = 1;
    private LatLng tmpPos;
    private LatLng currentPos;
    private ShakeDetector mShakeDetector;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private boolean isStart;
    private Button startBtn;
    private Button stopBtn;
    private ArrayList<RouteDetail>routeDetails = new ArrayList<RouteDetail>();
    private ArrayList<LatLng>routeLatLng = new ArrayList<LatLng>();
    JSONArray jsonArray = new JSONArray();


    private Runnable running = new Runnable() {
        @Override
        public void run() {
            currentPos = new LatLng(tmpPos.latitude+((toPosition.latitude-fromPosition.latitude)/count),
                    tmpPos.longitude+((toPosition.longitude-fromPosition.longitude)/count));

            //Log.d("myTAG", currentPos.latitude+" "+currentPos.longitude);
            drawPolyline(tmpPos,currentPos);
            tmpPos = currentPos;
            c--;
            if(c > 0)
                handler.postDelayed(running, delay);
            else {
                if(to == routeLatLng.size()-1) {
                    mSensorManager.unregisterListener(mShakeDetector);
                    startBtn.setVisibility(View.GONE);
                    stopBtn.setVisibility(View.GONE);
                    finishTracking();
                    save(jsonArray.toString());
                }else{
                    finishTracking();
                    from++;
                    to++;
                    fromPosition = routeLatLng.get(from);
                    toPosition = routeLatLng.get(to);
                    c = count;
                    handler.postDelayed(running, delay);
                }
            }
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = getIntent().getStringExtra("name");

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

        isStart = false;
        startBtn = (Button) findViewById(R.id.startBtn);
        stopBtn = (Button) findViewById(R.id.stopBtn);
        stopBtn.setVisibility(View.GONE);


        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isStart) {
                    if(routeLatLng.size() >= 2) {
                        fromPosition = routeLatLng.get(from);
                        toPosition = routeLatLng.get(to);
                        tmpPos = fromPosition;
                        startTracking();
                        isStart = true;
                        startBtn.setText("PAUSE");
                        stopBtn.setVisibility(View.VISIBLE);
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Please insert at least 2 markers",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    //Pause Tracking
                    if(mShakeDetector != null)
                        mSensorManager.unregisterListener(mShakeDetector);
                    handler.removeCallbacks(running);
                    isStart = false;
                    startBtn.setText("RESUME");

                }
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mShakeDetector != null)
                    mSensorManager.unregisterListener(mShakeDetector);
                handler.removeCallbacks(running);
                startBtn.setVisibility(View.GONE);
                stopBtn.setVisibility(View.GONE);
                finishTracking();
                save(jsonArray.toString());
            }
        });

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                routeLatLng.add(latLng);
            }
        });
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


            LatLng coordinates = new LatLng(13.685400079263206, 100.537133384495975);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 16));

            //googleMap.addMarker(new MarkerOptions().position(fromPosition).title("Start"));
            //googleMap.addMarker(new MarkerOptions().position(toPosition).title("End"));

        }
    }

    private void startTracking(){
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(float count) {
                Log.d("shake",count+"");
                if(count<1.9){
                    polyLineColor = Color.GREEN;
                    delay = 1000;
                }else if(count>=1.9 && count < 2.5){
                    polyLineColor = Color.YELLOW;
                    delay = 500;
                }else{
                    polyLineColor = Color.RED;
                    delay = 100;
                }
            }
        });
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,    SensorManager.SENSOR_DELAY_UI);
        handler.postDelayed(running,delay);
    }


    private void drawPolyline(LatLng start, LatLng end){
        PolylineOptions rectLine = new PolylineOptions().width(7).color(polyLineColor)
                .add(start)
                .add(end);

        googleMap.addPolyline(rectLine);

        routeDetails.add(new RouteDetail(start,end,polyLineColor));
    }

    private void finishTracking(){
        try {
            JSONObject json = new JSONObject();
            JSONObject detailJson = new JSONObject();
            JSONArray arrayRoute = new JSONArray();

            detailJson.put("startLat", fromPosition.latitude);
            detailJson.put("startLon", fromPosition.longitude);
            detailJson.put("endLat", toPosition.latitude);
            detailJson.put("endLon", toPosition.longitude);

            for(int i=0;i<routeDetails.size();i++){
                JSONObject routeJson = new JSONObject();

                routeJson.put("startLat", routeDetails.get(i).getStart().latitude);
                routeJson.put("startLon", routeDetails.get(i).getStart().longitude);
                routeJson.put("endLat", routeDetails.get(i).getEnd().latitude);
                routeJson.put("endLon", routeDetails.get(i).getEnd().longitude);
                routeJson.put("color",routeDetails.get(i).getColor());

                arrayRoute.put(routeJson);
            }

            json.put("detail",detailJson);
            json.put("route",arrayRoute);

            jsonArray.put(json);
            //Log.d("myTag",json.toString());
            //save(json.toString());
        }
        catch(Exception e){

        }
    }

    private void save(String route){
        Database db = new Database(this);
        db.addRecord(name,"Today",route);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //initilizeMap();
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
