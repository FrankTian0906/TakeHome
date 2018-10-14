package com.tianfei.takehometest;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tianfei.takehometest.model.Airports;

import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String GOOGLE_SERVICE= "GOOGLE SERVICE";
    private  static final List<PatternItem> PATTERN = Arrays.asList(new Dash(30), new Gap(15));
    //MODE 0: 0 transfer station, 1: 1 transfer station, 2: 2 transfer stations
    //!!should use global final static variant
    private int mode = 0;
    private Airports origin, transfer_1, transfer_2, destination;
    private LatLng originMarker,transfer_1_marker,transfer_2_marker,destinationMarker;

    /**
    *get the Airports objects from intent
    */
    public  void getInfo(){
        origin = (Airports)getIntent().getSerializableExtra("origin");
        destination = (Airports)getIntent().getSerializableExtra("destination");

        switch (getIntent().getIntExtra("mode",0)){
            case 0:
                mode = 0;

                break;
            case 1:
                mode = 1;
                transfer_1 =  (Airports)getIntent().getSerializableExtra("transfer_1");

                break;
            case 2:
                mode  = 2;
                transfer_1 =  (Airports)getIntent().getSerializableExtra("transfer_1");
                transfer_2 =  (Airports)getIntent().getSerializableExtra("transfer_2");
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getInfo();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
       //MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(this,R.raw.style_json);
       //googleMap.setMapStyle(mapStyleOptions);
       mMap = googleMap;
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style));

            if (!success) {
                Log.e("MAP style", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MAP style", "Can't find style. Error: ", e);
        }
        setMap();
    }
    /**
    * set these airports with their name and IATA on the map, and draw the routers
    * include METHOD: drawLine()
    */
    public void setMap(){
        //add origin, destination
        originMarker = new LatLng(origin.getLatitude(), origin.getLongitude());
        mMap.addMarker(new MarkerOptions().position(originMarker)
                .title(origin.getName())
                .snippet(origin.getIATA())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
        destinationMarker = new LatLng(destination.getLatitude(),destination.getLongitude());
        mMap.addMarker(new MarkerOptions().position(destinationMarker)
                .title(destination.getName())
                .snippet(destination.getIATA())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));

        switch (mode){
            case 0:
                drawLine(originMarker,destinationMarker);
                break;
            case 1:
                //add transfer station 1
                transfer_1_marker = new LatLng(transfer_1.getLatitude(),transfer_1.getLongitude());
                mMap.addMarker(new MarkerOptions().position(transfer_1_marker)
                        .title(transfer_1.getName())
                        .snippet(transfer_1.getIATA())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                drawLine(originMarker,transfer_1_marker);
                drawLine(transfer_1_marker,destinationMarker);
                break;
            case 2:
                //add transfer station 1, 2
                transfer_1_marker = new LatLng(transfer_1.getLatitude(),transfer_1.getLongitude());
                mMap.addMarker(new MarkerOptions().position(transfer_1_marker)
                        .title(transfer_1.getName())
                        .snippet(transfer_1.getIATA())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                transfer_2_marker = new LatLng(transfer_2.getLatitude(),transfer_2.getLongitude());
                mMap.addMarker(new MarkerOptions().position(transfer_2_marker)
                        .title(transfer_2.getName())
                        .snippet(transfer_2.getIATA())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                drawLine(originMarker,transfer_1_marker);
                drawLine(transfer_1_marker,transfer_2_marker);
                drawLine(transfer_2_marker,destinationMarker);
                break;
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(originMarker));
    }

    /**
     *  create a line between 2 airports
     *  parameters: LatLng @origin, LatLng @destination
     */
    public void drawLine(LatLng origin, LatLng destination){
        mMap.addPolyline(new PolylineOptions().add(origin,destination)
                .width(10).color(Color.WHITE).pattern(PATTERN).geodesic(true));

    }

    //check service status
    public boolean checkServiceOK(){
        int avaliable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapsActivity.this);
        if(avaliable == ConnectionResult.SUCCESS){
            Log.d(GOOGLE_SERVICE, "google service is ok");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(avaliable)){
            Log.d(GOOGLE_SERVICE,"an error occurred, but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this,avaliable, 9001);
            dialog.show();
        }
        else
            Log.d(GOOGLE_SERVICE,"an fetal error, we can't do!");

        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        MapsActivity.this.finish();
    }
}
