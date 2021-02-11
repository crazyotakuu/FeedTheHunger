package com.example.feedthehunger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MapInitiator extends AppCompatActivity {
    private ViewPager viewpager;
    private TabLayout tablayout;
    private DonationlistMap donationlistMap;
    private status status;
    private history history;
    private Logout logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_initiator);
        viewpager = findViewById(R.id.viewpager);
        tablayout = findViewById(R.id.tablayout);
        donationlistMap = new DonationlistMap();
        status = new status();
        history = new history();
        logout=new Logout();
        tablayout.setupWithViewPager(viewpager);
        ViewPagerAdapter viewpageadapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);

        viewpageadapter.addFragment(donationlistMap, " Maps ");
        viewpageadapter.addFragment(status, " Status ");
        viewpageadapter.addFragment(history, " History ");
        viewpageadapter.addFragment(logout,"");
        viewpager.setAdapter(viewpageadapter);
        tablayout.getTabAt(0).setIcon(R.drawable.ic_location);
        tablayout.getTabAt(1).setIcon(R.drawable.ic_status);
        tablayout.getTabAt(2).setIcon(R.drawable.ic_clock);
        tablayout.getTabAt(3).setIcon(R.drawable.ic_logout);
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