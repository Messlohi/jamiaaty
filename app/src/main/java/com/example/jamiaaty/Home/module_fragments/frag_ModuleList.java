package com.example.jamiaaty.Home.module_fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;
import com.example.jamiaaty.Home.Module_pack.Module;
import com.example.jamiaaty.Home.Module_pack.ModuleCardAdapter;
import com.example.jamiaaty.Home.Module_pack.bookmarks_Modules;
import com.example.jamiaaty.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
public class frag_ModuleList extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static ModuleCardAdapter adapter;
    View root=null;
    public static ArrayList<Module> modules;
    FirebaseDatabase database;
    DatabaseReference myRef;
    SwipeRefreshLayout swipeContainer;
    ImageView errormsg;
    RecyclerView rv;
    ProgressDialog loading;
    ImageButton btnBookmarks;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public frag_ModuleList() {     }
    public static frag_ModuleList newInstance(String param1, String param2) {
        frag_ModuleList fragment = new frag_ModuleList();
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
        root=inflater.inflate(R.layout.frag_module_list, container, false);
        modules=new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        errormsg=root.findViewById(R.id.errormsg);
        btnBookmarks=root.findViewById(R.id.btnBookmarks);
        rv=root.findViewById(R.id.coursRV);
        swipeContainer = root.findViewById(R.id.swipe_container);
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
        loading = ProgressDialog.show(getContext(), "Chargement ...", null,true,true);
        //get data for the first time
        refrechData();
        //open bookmarks btn listener
        btnBookmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBookmarks();
            }
        });
        return root;
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void openBookmarks() {
        Intent intent=new Intent(getActivity(), bookmarks_Modules.class);
        startActivity(intent);
    }
    public void refrechData() {
        if (!isNetworkAvailable() ) {
            errormsg.setVisibility(View.VISIBLE);
            // rv.setBackgroundColor(Color.rgb(247, 247, 247));
            rv.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
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
                    }
                    rv.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
                    rv.setHasFixedSize(true);
                    rv.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), DividerItemDecoration.VERTICAL));
                    adapter = new ModuleCardAdapter(getActivity(),modules );
                    rv.setAdapter(adapter);
                    loading.dismiss();
                    SearchView Search=(SearchView) getActivity().findViewById(R.id.SearchView);
                    Search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            callSearch(query);
                            return true;
                        }
                        @Override
                        public boolean onQueryTextChange(String newText) {
                            callSearch(newText);
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
            Toast.makeText(getActivity(), "error "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        //stop refreching icon
        swipeContainer.setRefreshing(false);
    }

}