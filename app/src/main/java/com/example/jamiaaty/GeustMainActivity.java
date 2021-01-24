package com.example.jamiaaty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.jamiaaty.Home.Module_pack.frag_downloads;
import com.example.jamiaaty.Home.module_fragments.frag_ModuleList;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class GeustMainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geust_main);


         bottomNavigationView = findViewById(R.id.bottom_nav_geust);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNav);
        //request storage permissions
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_geust, new frag_ModuleList()).commit();

    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNav = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selected = null;
            switch (item.getItemId()){
                case R.id.profile_bottom:
                case R.id.home_bottom:
                case R.id.queue_bottom:
                    if(isNetworkAvailable()){
                        selected = new FragmentRequireLogin();

                    }else {
                        selected = new NoInternetFragment();
                    }
                    break;

                case R.id.downloads_bottom:
                    selected = new frag_downloads();
                    break;
                case  R.id.support_bottom :
                    selected = new frag_ModuleList();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_geust, selected).commit();
            return true;
        }
    };


}