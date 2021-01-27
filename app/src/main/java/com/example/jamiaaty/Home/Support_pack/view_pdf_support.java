package com.example.jamiaaty.Home.Support_pack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.example.jamiaaty.R;
import com.github.barteksc.pdfviewer.PDFView;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class view_pdf_support extends AppCompatActivity {
    TextView msg;
    PDFView pdfviewer;
    String SupportName;
    String SupportKey;
    String support_link="";
    String isFromDownload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pdf_support);
        pdfviewer=findViewById(R.id.pdfviewer);
        msg=findViewById(R.id.textmsg);
        Intent intent=getIntent();
        support_link= intent.getStringExtra("support_link") ;
        SupportKey= intent.getStringExtra("support_key") ;
        isFromDownload=intent.getStringExtra("isFromDownload");
        Log.i("test","isFromdownload string "+intent.getStringExtra("isFromDownload"));
       // SupportName= intent.getStringExtra("support_name") ;
        //setTitle(SupportName);
        try {
            if(isFromDownload.equals("false"))
                new displayPdf().execute(); //after getting pdf from firebase
            else{
                ActivityCompat.requestPermissions(view_pdf_support.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
                ActivityCompat.requestPermissions((Activity) view_pdf_support.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
                File f = new File(support_link);
                if(f.exists()){
                    //Toast.makeText(this, "opening file from sd", Toast.LENGTH_LONG).show();
                    pdfviewer.fromFile(f).load();
                }
                else
                Toast.makeText(this, "file does not exist", Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            msg.setText("Erreur de chargement de fichier : "+e.getMessage());
            Toast.makeText(this, "Erreur de chargement de fichier : "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    class displayPdf extends AsyncTask<String,Void, InputStream> {
        ProgressDialog loading;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(view_pdf_support.this, "Chargement ...", null,true,true);
        }
        @Override
        protected void onPostExecute(InputStream inputStream) {
            //super.onPostExecute(b);
            loading.dismiss();
            try {
                if(inputStream!=null){
                    pdfviewer.fromStream(inputStream).load();
                    Log.i("debuuug","pdf is loading");
                }else{
                    Log.i("debuuug","pdf is null");
                    msg.setText("Erreur de chargement de fichier");
                    return;
                }
            }catch(Exception e){
                Toast.makeText(view_pdf_support.this, "error reading pdf "+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        @Override
        protected InputStream doInBackground(String... string) {
            InputStream inputStream=null;
            try {
                //URL pdflink=new URL("https://firebasestorage.googleapis.com/v0/b/jami3aty-dfe94.appspot.com/o/Modules%2Fmodule1%2Fsupports%2Ftp%2Ftp3.pdf?alt=media&token=c905e3a5-b3b8-475b-a762-2c2679ea397d");
                try {
                    URL pdflink=new URL(support_link);
                    HttpURLConnection httpURLConnection=(HttpURLConnection)pdflink.openConnection();
                    if (httpURLConnection.getResponseCode()==200 && httpURLConnection.getInputStream()!=null){
                        inputStream=new BufferedInputStream(httpURLConnection.getInputStream());
                    }else{
                        Toast.makeText(view_pdf_support.this, "Error fichier introuvable ", Toast.LENGTH_LONG).show();
                        return null;
                    }
                }catch(Exception e){
                Toast.makeText(view_pdf_support.this, "error reading pdf "+e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }catch(Exception e){
                Toast.makeText(view_pdf_support.this, "error reading pdf "+e.getMessage(), Toast.LENGTH_LONG).show();
                msg.setText("Erreur de chargement de fichier "+e.getMessage());
            }
            return inputStream;
        }
    }

}