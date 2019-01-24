package com.jts.root.sinage_10;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.jts.root.sinage_10.Gridview_list.gridboolen;
import static com.jts.root.sinage_10.Schedule.latSJ;
import static com.jts.root.sinage_10.Schedule.longSJ;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    TextView locationSearch, et1, et2;

    List<Address> addresses;
    static String latS,langS;
    Button cancle;

    String getplace;
    SupportMapFragment mapFragment;
    LatLng position;
    double latitude,longitude;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        // mMap = mapFragment.getMap();
        //mMap= ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync((OnMapReadyCallback);this);
        //mMap=((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        //locationSearch = (EditText) findViewById(R.id.editText);
        et1 = (TextView) findViewById(R.id.e1);
        et2 = (TextView) findViewById(R.id.e2);
        cancle = (Button) findViewById(R.id.canclemap);
        gridboolen=true;

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, Schedule.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        try{
        if (latS!=null || langS!=null) {
             latitude = Double.parseDouble(latSJ);
             longitude = Double.parseDouble(longSJ);
            Log.d("...latitude....", "---" + latitude);

            position = new LatLng(latitude, longitude);
            et1.setText(String.valueOf(latitude));
            et2.setText(String.valueOf(longitude));
        }
        }catch (Exception e){
            e.printStackTrace();
        }
        PlaceAutocompleteFragment places = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        places.getView().setBackgroundColor(Color.WHITE);

        places.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                 getplace= String.valueOf(place.getName());
                Log.d("...getplace....", "---" + getplace);

                Toast.makeText(getApplicationContext(), place.getName(), Toast.LENGTH_SHORT).show();

                List<Address> addressList = null;
                double lan;
                double lat;

                try {
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    addressList = geocoder.getFromLocationName(getplace, 1);
                    Address match = addressList.isEmpty() ? null : addressList.get(0);
                    if (match != null) {
                        Address address = addressList.get(0);

                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        //CameraUpdate center=CameraUpdateFactory.newLatLng(new LatLng(address.getLatitude(),address.getLongitude()));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,(float) 15.6));

                        //Toast.makeText(MapsActivity.this,"Lattitude:"+ address.getLatitude(),Toast.LENGTH_LONG).show();
                        lan = address.getLongitude();
                        lat = address.getLatitude();
                        langS= String.valueOf(address.getLongitude());
                        latS= String.valueOf(address.getLatitude());
                        System.out.println(latS+ "---" + langS);

                        et1.setText(String.valueOf(lan));
                        et2.setText(String.valueOf(lat));
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                        mMap.getUiSettings().setZoomGesturesEnabled(true);
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,(float) 15.6));
                        // mMap.animateCamera( CameraUpdateFactory.zoomTo( 4.0f ) );
                    } else {
                        Toast.makeText(MapsActivity.this, "No result found", Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception e) {
                    Toast.makeText(MapsActivity.this, "Error search :- Wrong input", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onError(Status status) {

                Toast.makeText(getApplicationContext(), status.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onMapSearch(View view) throws IOException {
        //String location = locationSearch.getText().toString();
        //String location = place.getName();

        List<Address> addressList = null;
        double lan;
        double lat;

        try {
                Geocoder geocoder = new Geocoder(this);
                addressList = geocoder.getFromLocationName(getplace, 1);
                Address match = addressList.isEmpty() ? null : addressList.get(0);
                if (match != null) {
                    Address address = addressList.get(0);

                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    //CameraUpdate center=CameraUpdateFactory.newLatLng(new LatLng(address.getLatitude(),address.getLongitude()));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,(float) 15.6));

                    //Toast.makeText(MapsActivity.this,"Lattitude:"+ address.getLatitude(),Toast.LENGTH_LONG).show();
                    lan = address.getLongitude();
                    lat = address.getLatitude();
                    langS= String.valueOf(address.getLongitude());
                    latS= String.valueOf(address.getLatitude());
                    System.out.println(latS+ "---" + langS);

                    et1.setText(String.valueOf(lan));
                    et2.setText(String.valueOf(lat));
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                    mMap.getUiSettings().setZoomGesturesEnabled(true);
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,(float) 15.6));
                   // mMap.animateCamera( CameraUpdateFactory.zoomTo( 4.0f ) );
                } else {
                    Toast.makeText(this, "No result found", Toast.LENGTH_LONG).show();
                }
            }
        catch (Exception e) {
            Toast.makeText(this, "Error search :- Wrong input", Toast.LENGTH_LONG).show();
        }
        }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            if (latS != null || langS != null) {
                googleMap.addMarker(new MarkerOptions().position(position)
                        .title("Marker in Sydney"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
       /* googleMap.moveCamera(CameraUpdateFactory.newLatLng(position));

        CameraUpdate updatePosition = CameraUpdateFactory.newLatLng(position);

        // Creating CameraUpdate object for zoom
        CameraUpdate updateZoom = CameraUpdateFactory.zoomBy(4);

        // Updating the camera position to the user input latitude and longitude
        googleMap.moveCamera(updatePosition);

        // Applying zoom to the marker position
        googleMap.animateCamera(updateZoom);*/

        mMap.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {

                String address;
                String city;
                mMap.clear();
                Geocoder geocoder;
                geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                mMap.clear();
                try {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point,(float) 16.6));
                    MarkerOptions marker = new MarkerOptions().position(new LatLng(point.latitude, point.longitude)).title("marker");
                    mMap.addMarker(marker);
                    addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point,(float) 16.6));

                    mMap.getUiSettings().setZoomGesturesEnabled(true);
                    System.out.println(point.latitude + "---" + point.longitude);

                    langS= String.valueOf(point.longitude);
                    latS= String.valueOf(point.latitude);
                    System.out.println(latS+ "---" + langS);

                    et1.setText(String.valueOf(point.longitude));
                    et2.setText(String.valueOf(point.latitude));
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
               /* MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(position);
                markerOptions.title(position.latitude + ":"+ position.longitude);
                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(position));
                mMap.addMarker(markerOptions);*/
            }
        });
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(17.385044, 78.486671);
        try {
            if (latS != null || langS != null) {
                mMap.addMarker(new MarkerOptions().position(position).title("hyderabad, India"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(1.0f));
                // mMap.clear();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    protected void zoomMapInitial(LatLng finalPlace, LatLng currenLoc) {
        try {
            int padding = 200; // Space (in px) between bounding box edges and view edges (applied to all four sides of the bounding box)
            LatLngBounds.Builder bc = new LatLngBounds.Builder();
            bc.include(finalPlace);
            bc.include(currenLoc);
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), padding));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        mMap.animateCamera(cameraUpdate);
        //locationManager.removeUpdates(this);
    }

    public void ReturnHome(View view){
        super.onBackPressed();
    }
}
