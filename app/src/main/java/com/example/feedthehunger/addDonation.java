package com.example.feedthehunger;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static androidx.core.content.ContextCompat.getSystemService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link addDonation#newInstance} factory method to
 * create an instance of this fragment.
 */
public class addDonation extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    LocationManager locationManager;
    double latitude,longitude;
    private EditText foodtype,quanity,des,address,expdate;
    private Button adddon;
    private FirebaseAuth mAuth;
    double lat,longi;
    private FusedLocationProviderClient mFusedLocationClient;
    public addDonation() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment addDonation.
     */
    // TODO: Rename and change types and number of parameters
    public static addDonation newInstance(String param1, String param2) {
        addDonation fragment = new addDonation();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_add_donation, container, false);
        foodtype=v.findViewById(R.id.foodtype);
        quanity=v.findViewById(R.id.quantity);
        des=v.findViewById(R.id.des);
        address=v.findViewById(R.id.address);
        expdate=v.findViewById(R.id.expdate);
        adddon=v.findViewById(R.id.buttonadddon);
        adddon.setOnClickListener(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        return v;
    }

    private void getLastKnownLocation() {
        Log.d(getTag(), "getLastKnownLocation: called.");


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(),"Permissions",Toast.LENGTH_LONG).show();
        }
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<android.location.Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();

                    Log.d("latitude", String.valueOf(location.getLatitude()));
                    Log.d("longitude", String.valueOf(location.getLongitude()));
                    mAuth = FirebaseAuth.getInstance();
                    FirebaseUser user = mAuth.getCurrentUser();
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("donations");
                    String donId = mDatabase.push().getKey();
                    mDatabase.child(donId).setValue(new Donation(user.getUid(),foodtype.getText().toString(),quanity.getText().toString()
                            ,des.getText().toString(),address.getText().toString(),expdate.getText().toString(),location.getLatitude(),location.getLongitude()));
                    Intent i=new Intent(getContext(),MainActivity.class);
                    startActivity(i);
                }
            }
        });


    }


    @Override
    public void onClick(View view) {
        if(foodtype.getText().toString().equals("")||quanity.getText().toString().equals("")||des.getText().toString().equals("")||
                address.getText().toString().equals("")||expdate.getText().toString().equals("")){
            Toast.makeText(getContext(),"Enter all the fields",Toast.LENGTH_LONG).show();
            return;
        }
        getLastKnownLocation();

    }

}