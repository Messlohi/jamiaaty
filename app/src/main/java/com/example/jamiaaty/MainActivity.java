package com.example.jamiaaty;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.example.jamiaaty.Home.Module_pack.frag_downloads;
import com.example.jamiaaty.Home.module_fragments.frag_ModuleList;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentUser = "";
    DatabaseReference chatRef;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    List<String> convKeys = new ArrayList<>();


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

        if(user != null){
            currentUser = user.getUid();
        }

        database.getReference("All Users").child(currentUser).child("notification").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                NotificationModel model = snapshot.getValue(NotificationModel.class);
                try {

                    if(model!= null && model.getFromName()!= null){
                        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
                            NotificationChannel channel = new NotificationChannel("My Notif","MessageNotif", NotificationManager.IMPORTANCE_DEFAULT);
                            NotificationManager manager = getSystemService(NotificationManager.class);
                            manager.createNotificationChannel(channel);
                        }
                        String message = model.message.trim().length()>60?model.message.trim().substring(0,60):model.message.trim();
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this,"My Notif")
                                .setSmallIcon(R.drawable.ic_baseline_email_24)
                                .setContentTitle(model.getFromName() +" :")
                                .setContentText(message+"...")
                                .setAutoCancel(true);

                        Intent intent1 = new Intent(MainActivity.this,MainActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this,0,intent1,PendingIntent.FLAG_UPDATE_CURRENT);
                        builder.setContentIntent(pendingIntent);

                        NotificationManager notificationManager =  (NotificationManager)getSystemService(
                                Context.NOTIFICATION_SERVICE
                        );
                        if(!ChatActivity.activityRuning){
                            notificationManager.notify(0,builder.build());
                        }
                        database.getReference("All Users").child(currentUser).child("notification").removeValue();
                    }

                }catch (Exception e){

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        startService(new Intent(MainActivity.this, AndroidService.class));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(MainActivity.this, AndroidService.class));

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