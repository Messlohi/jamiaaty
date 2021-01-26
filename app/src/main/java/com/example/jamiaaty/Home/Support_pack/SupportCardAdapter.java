package com.example.jamiaaty.Home.Support_pack;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jamiaaty.MainActivity;
import com.example.jamiaaty.R;
import java.io.File;
import java.util.ArrayList;

public class SupportCardAdapter extends RecyclerView.Adapter<SupportCardAdapter.ViewHolder>/* implements Serializable*/ {
    Context contextAdap;
    ArrayList<Support> supports;
    String ModuleName="All";
    Boolean isOpenDownloads=false;
    String supportType;
    public SupportCardAdapter(@NonNull Context context, @NonNull ArrayList<Support> supports) {
        this.contextAdap=context;
        this.supports=supports;
    }
    public SupportCardAdapter(@NonNull Context context, @NonNull ArrayList<Support> supports,String ModuleName) {
        this.contextAdap=context;
        this.supports=supports;
        this.ModuleName=ModuleName;
    }    public SupportCardAdapter(@NonNull Context context, @NonNull ArrayList<Support> supports,String ModuleName,Boolean isOpenDownloads,String supportType) {
        this.contextAdap=context;
        this.supports=supports;
        this.ModuleName=ModuleName;
        this.isOpenDownloads=isOpenDownloads;
        this.supportType=supportType;
    }
    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.support_card,parent,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.SupportName.setText(supports.get(position).Name);
        holder.SupportSize.setText(supports.get(position).FileSize+" MB" );
        holder.SupportImg.setImageResource(R.drawable.ic_baseline_picture_as_pdf_24);
        holder.SupportImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        holder.SupportImg.setAdjustViewBounds(true);

        holder.key=supports.get(position).Key;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 try {
                   Intent intent=new Intent(contextAdap, view_pdf_support.class);
                     intent.putExtra("support_key",holder.key);
                     intent.putExtra("support_name",supports.get(position).Name);
                     intent.putExtra("support_link",supports.get(position).FileLink);
                     Log.i("test","adap isOpenDownloads "+isOpenDownloads);
                     intent.putExtra("isFromDownload",isOpenDownloads.toString());

                     contextAdap.startActivity(intent);
                }catch (Exception e){
                    Toast.makeText(contextAdap, "error adapter on item clicked: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.btnViewPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 try {
                   Intent intent=new Intent(contextAdap, view_pdf_support.class);
                     intent.putExtra("support_key",holder.key);
                     intent.putExtra("support_name",supports.get(position).Name);
                     intent.putExtra("support_link",supports.get(position).FileLink);
                     intent.putExtra("isFromDownload",isOpenDownloads.toString());
                     contextAdap.startActivity(intent);
                }catch (Exception e){
                    Toast.makeText(contextAdap, "error adapter on item clicked: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        if(isOpenDownloads){
            holder.BtnDownload.setEnabled(!isOpenDownloads);
            holder.BtnDownload.setImageResource(R.drawable.ic_download_icon_gray);
        }
       // holder.BtnDownload.setBackground();
        holder.BtnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ActivityCompat.requestPermissions((Activity) contextAdap,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
                    ActivityCompat.requestPermissions((Activity) contextAdap,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
                    Toast.makeText(view.getContext(),  "Téléchargement...", Toast.LENGTH_LONG).show();
                    file_download(supports.get(position).FileLink,supports.get(position).Name);
                }catch (Exception e){
                    Toast.makeText(contextAdap, "Erreur de telechargement"+e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void file_download(String uRl,String fileName) {
        //https://www.codota.com/code/java/methods/android.app.DownloadManager/enqueue
        File isAlreadyDownloaded = new File(Environment.getExternalStorageDirectory() + "/Download/jami3aty/"+ModuleName+"/"+supportType+"/"+fileName+".pdf");
        if(isAlreadyDownloaded.canRead() && !isAlreadyDownloaded.isDirectory() ) {
            Toast.makeText(contextAdap, "Support déja Téléchargé (Voir Download/jami3aty/" + supportType + "/" + ModuleName + "/" + fileName + ".pdf )", Toast.LENGTH_LONG).show();
            return;
        }
        File direct = new File(Environment.DIRECTORY_DOWNLOADS + "/jami3aty/"+ModuleName+"/"+supportType);
        if (!direct.exists()) {
//            Toast.makeText(contextAdap, "direct not exist", Toast.LENGTH_SHORT).show();
            direct.mkdirs();
        }
        DownloadManager mgr = (DownloadManager) contextAdap.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri = Uri.parse(uRl);
        DownloadManager.Request request = new DownloadManager.Request(
                downloadUri);
        request.setAllowedNetworkTypes( DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(true).setTitle(fileName+".pdf")
                .setDescription("Téléchargement du support")
                .setDestinationInExternalPublicDir("/Download/jami3aty/"+ModuleName+"/"+supportType, fileName+".pdf");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        mgr.enqueue(request);
        try {
            Thread.sleep(1500);
        }catch(Exception e){
            Log.e("debuuug","error "+e.getMessage());
        }
        Toast.makeText(contextAdap,  "Support Téléchargé ( Download/jami3aty/"+supportType+"/"+ModuleName+"/"+fileName+".pdf )", Toast.LENGTH_LONG).show();
    }
    public Support getModule(int position) {
        return supports.get(position);
    }
    @Override
    public int getItemCount() {
        return this.supports.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener */  {
        TextView SupportName;
        TextView SupportSize;
        ImageView SupportImg;
        ImageButton BtnDownload;
        ImageButton btnViewPdf;
        String key;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            SupportName=itemView.findViewById(R.id.support_name);
            SupportSize=itemView.findViewById(R.id.support_size);
            SupportImg=itemView.findViewById(R.id.support_img);
            BtnDownload=itemView.findViewById(R.id.BtnDownload);
            btnViewPdf=itemView.findViewById(R.id.btnViewPdf);
        }
    }
}
