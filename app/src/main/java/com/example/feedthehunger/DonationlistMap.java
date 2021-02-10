package com.example.feedthehunger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.feedthehunger.Constants.MAPVIEW_BUNDLE_KEY;

public class DonationlistMap extends Fragment {

    private static final String TAG = "UserListFragment";

    //widgets
    private MapView mMapView;

    private LatLng mUserPosition;

    Location startPoint;

    //vars
    private ArrayList<Donation> mDonorList = new ArrayList<>();
    private GoogleMap mGoogleMap;
    private LatLngBounds mMapBoundary;
    private FusedLocationProviderClient mFusedLocationClient;
    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {

            mGoogleMap = googleMap;
            getDonationList(mGoogleMap);

            mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    if((int)marker.getTag()!=-1) {
                        Toast.makeText(getContext(), mDonorList.get((int) marker.getTag()).address + " ", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });

        }
    };

    @Override
    public void onStart() {
        super.onStart();


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        //getDonationList(mGoogleMap);
        //initGoogleMap(savedInstanceState);
        return inflater.inflate(R.layout.fragment_donationlist_map, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private void addMapMarkers(GoogleMap googleMap) {

        if (mGoogleMap != null) {


            getDonationList(googleMap);

        }
    }

    private void getDonationList(GoogleMap googleMap) {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "Permissions", Toast.LENGTH_LONG).show();
        }

        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    /*Location l=null;
                    LocationManager mLocationManager = (LocationManager)getContext().getSystemService(LOCATION_SERVICE);
                    List<String> providers = mLocationManager.getProviders(true);
                    Location bestLocation = null;
                    if(location==null){

                        for (String provider : providers) {
                            if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
                                l = mLocationManager.getLastKnownLocation(provider);
                            }
                            if (l == null) {
                                continue;
                            }
                            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                                bestLocation = l;
                            }
                        }
                        location = bestLocation;

                    }*/
                    Toast.makeText(getActivity(), location.toString(), Toast.LENGTH_SHORT).show();

                    Log.d("latitude", String.valueOf(location.getLatitude()));
                    Log.d("longitude", String.valueOf(location.getLongitude()));
                    double bottomBoundary = location.getLatitude() - .001;
                    double leftBoundary = location.getLongitude() - .001;
                    double topBoundary = location.getLatitude() + .001;
                    double rightBoundary = location.getLongitude() + .001;

                    mMapBoundary = new LatLngBounds(
                            new LatLng(bottomBoundary, leftBoundary),
                            new LatLng(topBoundary, rightBoundary)
                    );

                    Toast.makeText(getActivity(), mMapBoundary.toString(), Toast.LENGTH_SHORT).show();

                    startPoint = new Location("locationA");
                    startPoint.setLatitude(location.getLatitude());
                    startPoint.setLongitude(location.getLongitude());


                    mUserPosition = new LatLng(location.getLatitude(), location.getLongitude());
                    FirebaseDatabase.getInstance().getReference().child("donations")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int t = 0;
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                        Donation Don = snapshot.getValue(Donation.class);

                                        //Toast.makeText(getActivity(),"Donors:checking" + Don.userid,Toast.LENGTH_LONG).show();
                                        Location endPoint = new Location("locationA");
                                        endPoint.setLatitude(Don.latitude);
                                        endPoint.setLongitude(Don.longitude);
                                        if (startPoint.distanceTo(endPoint) < 15000.00) {


                                            //Toast.makeText(getActivity(),t++ +"",Toast.LENGTH_SHORT).show();
                                            mDonorList.add(Don);
                                            //Toast.makeText(getActivity(), mDonorList.size() + " ", Toast.LENGTH_LONG).show();
                                            //Toast.makeText(getActivity(),"Donors:Success" + Don.userid,Toast.LENGTH_LONG).show();
                                            //googleMap.addMarker(new MarkerOptions().position(new LatLng(Don.latitude,Don.longitude)).title(Don.description));

                                        }

                                    }


                                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 0));
                                    //Toast.makeText(getActivity(),mDonorList.size()+"",Toast.LENGTH_SHORT).show();
                                    Marker me = googleMap.addMarker(new MarkerOptions()
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                                            .position(mUserPosition)
                                            .title("You"));
                                    me.setTag(-1);
                                    int k=0;
                                    for (Donation i : mDonorList) {
                                        //Toast.makeText(getActivity(), i.latitude + " " + i.longitude, Toast.LENGTH_SHORT).show();
                                        Marker mperth = googleMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(i.latitude, i.longitude))
                                                .title(i.description));
                                        mperth.setTag(k++);

                                    }
                                }


                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });

                }
            }
        });

    }


}