package com.example.feedthehunger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
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
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;
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
                    Location l=null;
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

                    }

                        Log.d("latitude", String.valueOf(location.getLatitude()));
                        Log.d("longitude", String.valueOf(location.getLongitude()));
                        double bottomBoundary = location.getLatitude() - .001;
                        double leftBoundary = location.getLongitude() - .001;
                        double topBoundary = location.getLatitude() + .001;
                        double rightBoundary = location.getLongitude() + .001;
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(location.getLatitude()+0.0001,location.getLongitude()+0.00001))
                            .title("Me"));

                    mMapBoundary = new LatLngBounds(
                            new LatLng(bottomBoundary, leftBoundary),
                            new LatLng(topBoundary, rightBoundary)
                    );
                    Location startPoint=new Location("locationA");
                    startPoint.setLatitude(location.getLatitude());
                    startPoint.setLongitude(location.getLongitude());

                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 0));
                    mUserPosition=new LatLng(location.getLatitude(),location.getLongitude());
                    FirebaseDatabase.getInstance().getReference().child("donations")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        Donation Don = snapshot.getValue(Donation.class);
                                        //Toast.makeText(getActivity(),"Donors:checking" + Don.userid,Toast.LENGTH_LONG).show();
                                        Location endPoint=new Location("locationA");
                                        endPoint.setLatitude(Don.latitude);
                                        endPoint.setLongitude(Don.longitude);
                                        if(startPoint.distanceTo(endPoint)<15050.00){
                                            mDonorList.add(Don);
                                            //Toast.makeText(getActivity(),"Donors:Success" + Don.userid,Toast.LENGTH_LONG).show();
                                             googleMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(Don.latitude,Don.longitude))
                                                    .title(Don.description));

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
}