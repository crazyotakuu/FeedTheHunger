package com.example.feedthehunger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;

import static com.example.feedthehunger.Constants.MAPVIEW_BUNDLE_KEY;

public class DonationlistMap extends Fragment {

    private static final String TAG = "UserListFragment";

    //widgets
    private MapView mMapView;

    private LatLng mUserPosition;

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
            LatLng sydney = new LatLng(-34, 151);
            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            mGoogleMap = googleMap;
            addMapMarkers(mGoogleMap);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
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
            /*Toast.makeText(getActivity(),"Donors:size " + mDonorList.size(),Toast.LENGTH_LONG).show();
            for (Donation userLocation : mDonorList) {

                Log.d("addMapMarkers",  + userLocation.latitude+":::"+userLocation.longitude);
                Toast.makeText(getActivity(),"Donors:List " + userLocation.userid,Toast.LENGTH_LONG).show();
            }
            for (Donation userLocation : mDonorList) {

                Log.d("addMapMarkers",  + userLocation.latitude+":::"+userLocation.longitude);
                try {
                    String snippet = userLocation.address;

                    ClusterMarker newClusterMarker = new ClusterMarker(userLocation.description,snippet,userLocation,new LatLng(userLocation.latitude,userLocation.longitude));
                    mClusterManager.addItem(newClusterMarker);
                    mClusterMarkers.add(newClusterMarker);
                    Toast.makeText(getActivity(),"Donors:point " + userLocation.userid,Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Donors " + userLocation.userid);
                } catch (NullPointerException e) {
                    Log.e(TAG, "addMapMarkers: NullPointerException: " + e.getMessage());
                }

            }
            mClusterManager.cluster();*/

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

                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 0));
                    mUserPosition=new LatLng(location.getLatitude(),location.getLongitude());
                    FirebaseDatabase.getInstance().getReference().child("donations")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        Donation Don = snapshot.getValue(Donation.class);
                                        Toast.makeText(getActivity(),"Donors:checking" + Don.userid,Toast.LENGTH_LONG).show();
                                        if(distance(location.getLatitude(),location.getLongitude(),Don.latitude,Don.longitude)<50.00){
                                            mDonorList.add(Don);
                                            Toast.makeText(getActivity(),"Donors:Success" + Don.userid,Toast.LENGTH_LONG).show();
                                            Marker markerPerth = googleMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(Don.latitude,Don.longitude))
                                                    .title(Don.description));
                                            markerPerth.setTag(0);

                                        }

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

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    /**
     * Determines the view boundary then sets the camera
     * Sets the view
     */
}