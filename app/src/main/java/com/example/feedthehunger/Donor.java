package com.example.feedthehunger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toolbar;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class Donor extends AppCompatActivity {
    private ViewPager viewpager;
    private TabLayout tablayout;
    private addDonation addDonation;
    private checkStatus checkStatus;
    private checkHistory checkHistory;
    private Logout logout;

//    changes
LocationManager locationManager;
    double latitude,longitude;
    private FusedLocationProviderClient mFusedLocationClient;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor);

        viewpager = findViewById(R.id.viewpager);
        tablayout = findViewById(R.id.tablayout);
        addDonation = new addDonation();
        checkStatus = new checkStatus();
        checkHistory = new checkHistory();
        logout=new Logout();
        tablayout.setupWithViewPager(viewpager);
        ViewPagerAdapter viewpageadapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);

        viewpageadapter.addFragment(addDonation, "Donation");
        viewpageadapter.addFragment(checkStatus, "Status");
        viewpageadapter.addFragment(checkHistory, "History");
        viewpageadapter.addFragment(logout,"");
        viewpager.setAdapter(viewpageadapter);
        tablayout.getTabAt(0).setIcon(R.drawable.ic_donation);
        tablayout.getTabAt(1).setIcon(R.drawable.ic_status);
        tablayout.getTabAt(2).setIcon(R.drawable.ic_clock);
        tablayout.getTabAt(3).setIcon(R.drawable.ic_logout);
        //location code starts from here


    }



    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments=new ArrayList<>();
        private List<String> fragmenttitle=new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }
        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            fragmenttitle.add(title);
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmenttitle.get(position);
        }
    }

}