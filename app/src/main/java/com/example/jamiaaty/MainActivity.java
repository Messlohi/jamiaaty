package com.example.jamiaaty;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.example.jamiaaty.Home.Module_pack.frag_downloads;
import com.example.jamiaaty.Home.module_fragments.frag_ModuleList;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentUser = "";
    DatabaseReference chatRef;
    FirebaseDatabase database = FirebaseDatabase.getInstance();


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_container);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNav);
        //request storage permissions
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new Fragment4()).commit();


        if(user != null) {
            currentUser = user.getUid();
        }



    }
    private BottomNavigationView.OnNavigationItemSelectedListener onNav = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selected = null;
            switch (item.getItemId()){
                case R.id.profile_bottom:
                    selected = new Fragment1();
                    break;
                //Fragment For donwloads
                case R.id.downloads_bottom:
                    selected = new frag_downloads();
                    break;
                //-------------
                case R.id.queue_bottom:
                    selected = new Fragment3();
                    break;
                case R.id.home_bottom:
                    selected = new Fragment4();
                    break;
                case  R.id.support_bottom :
                    selected = new frag_ModuleList();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, selected).commit();
            return true;
        }
    };
}