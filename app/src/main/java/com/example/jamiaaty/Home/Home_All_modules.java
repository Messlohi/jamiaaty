package com.example.jamiaaty.Home;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;
import com.example.jamiaaty.Home.Module_pack.Module;
import com.example.jamiaaty.Home.Module_pack.ModuleCardAdapter;
import com.example.jamiaaty.Home.Module_pack.ac_downloaded_modules;
import com.example.jamiaaty.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class Home_All_modules extends AppCompatActivity /*implements SwipeRefreshLayout.OnRefreshListener*/{

    public static ModuleCardAdapter adapter;
    public static ArrayList<Module> modules;
    FirebaseDatabase database;
    DatabaseReference myRef;
    SwipeRefreshLayout swipeContainer;
    ImageView errormsg;
    RecyclerView rv;
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Matières");
        modules=new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        errormsg=findViewById(R.id.errormsg);
        rv=findViewById(R.id.coursRV);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refrechData();
            }
        });//https://guides.codepath.com/android/implementing-pull-to-refresh-guide
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        loading = ProgressDialog.show(this, "Chargement ...", null,true,true);
        //get data for the first time
        refrechData();

    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void openBookmarks(View view) {
        //  Intent intent=new Intent(this, bookmarks_Modules.class);
            Intent intent=new Intent(this, ac_downloaded_modules.class);
        startActivity(intent);
    }
    public void refrechData() {
        if (!isNetworkAvailable() ) {
            errormsg.setVisibility(View.VISIBLE);
            // rv.setBackgroundColor(Color.rgb(247, 247, 247));
            rv.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            swipeContainer.setRefreshing(false);
            loading.dismiss();
            return;
        }else{
            errormsg.setVisibility(View.INVISIBLE);
            // rv.setBackgroundColor(Color.WHITE);
            rv.setVisibility(View.VISIBLE);
        }
        try {
            myRef = database.getReference("Modules");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) { //Log.i("moduleeee","size:  "+ dataSnapshot.child("0").child("name").getValue());
                    modules.clear();
                    for (DataSnapshot elem: dataSnapshot.getChildren()) {
                        Module module=elem.getValue(Module.class);
                        modules.add(module);
                         Log.i("moduleeee","\nsize:  "+ module.name+"\n "+module.description+"\n "+module.key+"\n "+module.imgLink);
                    }loading.dismiss();
                    RecyclerView recyclerView = findViewById(R.id.coursRV);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclerView.setHasFixedSize(true);
                    recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
                    adapter = new ModuleCardAdapter(getApplicationContext(),modules );
                    recyclerView.setAdapter(adapter);
                    loading.dismiss();
                    SearchView Search=(SearchView)findViewById(R.id.SearchView);
                    Search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            callSearch(query);
                            return true;
                        }
                        @Override
                        public boolean onQueryTextChange(String newText) {
//              if (searchView.isExpanded() && TextUtils.isEmpty(newText)) {
                            callSearch(newText);
//              }
                            return true;
                        }

                        public void callSearch(String query) {
                            //Do searching
                            if(modules.size()<1) return;
                            adapter.search(query,modules);
                        }

                    });
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    Log.i("debuuug", "Failed to read value.", error.toException());
                }
            });
        }catch (Exception e){
            Toast.makeText(this, "error "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        //stop refreching icon
        swipeContainer.setRefreshing(false);
    }

}
