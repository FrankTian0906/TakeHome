package com.tianfei.takehometest;

import android.app.Dialog;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String GOOGLE_SERVICE= "GOOGLE SERVICE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng goroka = new LatLng(-6.081689835, 145.3919983);
        mMap.addMarker(new MarkerOptions().position(goroka).title("Goroka Airport"));
        LatLng madang = new LatLng(-5.207079887,145.7890015);
        mMap.addMarker(new MarkerOptions().position(madang).title("Madang Airport"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(goroka));
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
}
