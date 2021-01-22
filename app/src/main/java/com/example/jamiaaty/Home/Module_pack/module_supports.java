package com.example.jamiaaty.Home.Module_pack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.jamiaaty.R;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jamiaaty.Home.module_fragments.SectionsPagerAdapter;

public class module_supports extends AppCompatActivity {
    String ModuleName;
    String ModuleKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        Intent intent=getIntent();
        ModuleName= intent.getStringExtra("Cours_name") ;
        ModuleKey= intent.getStringExtra("Cours_key") ;
        TextView Moduletitle=findViewById(R.id.Moduletitle);
        Moduletitle.setText(ModuleName);
    }
}