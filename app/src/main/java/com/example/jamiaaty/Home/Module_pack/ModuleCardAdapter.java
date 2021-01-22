package com.example.jamiaaty.Home.Module_pack;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.jamiaaty.R;
import com.example.jamiaaty.Home.localdb.localdb;

import java.util.ArrayList;

//https://stackoverflow.com/questions/40584424/simple-android-recyclerview-example
public class ModuleCardAdapter extends RecyclerView.Adapter<ModuleCardAdapter.ViewHolder>/* implements Serializable*/ {
    Context contextAdap;
    ArrayList<com.example.jamiaaty.Home.Module_pack.Module> modules;
    localdb dbbookmark;
    boolean isFolder=false;
    private ItemClickListener mClickListener;
    public ModuleCardAdapter(@NonNull Context context, @NonNull ArrayList<com.example.jamiaaty.Home.Module_pack.Module> produits) {
        this.contextAdap=context;
        this.modules=produits;
        dbbookmark=new localdb(contextAdap);
    }   public ModuleCardAdapter(@NonNull Context context, @NonNull ArrayList<com.example.jamiaaty.Home.Module_pack.Module> produits, Boolean isFolder) {
        this.contextAdap=context;
        this.modules=produits;
        dbbookmark=new localdb(contextAdap);
        this.isFolder=isFolder;
    }
    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.module_card,parent,false);
        return new ViewHolder(view);
    }
    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.ModuleName.setText(modules.get(position).getName());
        holder.ModuleDesription.setText(modules.get(position).description );
        holder.ModuletImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        holder.ModuletImg.setAdjustViewBounds(true);
        holder.key=modules.get(position).getKey();
        try {//load module icon
            if(!modules.get(position).getimgLink().isEmpty())
                 Glide.with(contextAdap).load(modules.get(position).getimgLink()).into(holder.ModuletImg);
            else holder.ModuletImg.setImageResource(R.drawable.ic_folder);
        }catch(Exception e){
            Toast.makeText(contextAdap, "Glide Erreur"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        if(dbbookmark.getModule(modules.get(position).key)!=null){ //checked
            holder.bookmarkFlag=true;
            holder.BtnBookmark.setImageResource(R.drawable.ic_bookmark_checked);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isNetworkAvailable() && !isFolder){
                    Toast.makeText(contextAdap, "Pas de Connexion internet!", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                   Intent intent=new Intent(contextAdap, module_supports.class);
                     intent.putExtra("Cours_key",holder.key);
                     intent.putExtra("Cours_name",modules.get(position).getName());
                     intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                     contextAdap.startActivity(intent);
                }catch (Exception e){
                    Toast.makeText(contextAdap, "error adapter on item clicked: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        if(isFolder)  holder.BtnBookmark.setVisibility(View.INVISIBLE);
        holder.BtnBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(holder.bookmarkFlag){//if user unchecked module
                        holder.BtnBookmark.setImageResource(R.drawable.ic_bookmark_unchecked);
                        holder.bookmarkFlag=false;
                        Toast.makeText(view.getContext(),  modules.get(position).getName()+" Retiré du favoris", Toast.LENGTH_SHORT).show();
                        dbbookmark.deleteModule(modules.get(position));
                    }else{//if user checked module
                        holder.BtnBookmark.setImageResource(R.drawable.ic_bookmark_checked);
                        holder.bookmarkFlag=true;
                        Toast.makeText(view.getContext(),  modules.get(position).getName()+" Ajouté au favoris", Toast.LENGTH_SHORT).show();
                        dbbookmark.addModule(modules.get(position));
//                        Log.i("debuuug"," _\n_\n_\n"+dbbookmark.getAllModules().get(0).name+"\n_\n_\n_ ");
                    }
                }catch(Exception e){
                    Toast.makeText(contextAdap, "Erreur "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) contextAdap.getSystemService(contextAdap.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public com.example.jamiaaty.Home.Module_pack.Module getModule(int position) {
        return modules.get(position);
    }
    @Override
    public int getItemCount() {
        return this.modules.size();
    }

    public void updateList(ArrayList<com.example.jamiaaty.Home.Module_pack.Module> list){
        modules = list;
        notifyDataSetChanged();
    }
    public void search(String key,ArrayList<com.example.jamiaaty.Home.Module_pack.Module> AllModules){
        ArrayList<com.example.jamiaaty.Home.Module_pack.Module> modulesCherche=new ArrayList<>();
        if(AllModules.size()<1) return;
        for (com.example.jamiaaty.Home.Module_pack.Module module:AllModules ) {
            if(module.name.toLowerCase().contains(key.toLowerCase()))
                modulesCherche.add(module);
        }
        updateList(modulesCherche);
    }
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
    public class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener */  {
        TextView ModuleName;
        TextView ModuleDesription;
        ImageView ModuletImg;
        ImageButton BtnBookmark;
        String key;
        boolean bookmarkFlag;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ModuleName=itemView.findViewById(R.id.module_name);
            ModuleDesription=itemView.findViewById(R.id.module_size);
            ModuletImg=itemView.findViewById(R.id.module_img);
            BtnBookmark=itemView.findViewById(R.id.BtnBookmark);
            bookmarkFlag=false;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), "itemview position inner : " + getLayoutPosition(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    /*   @Override
        public void onClick(View view) {
          //  if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            Toast.makeText(view.getContext(), "implement cddc position : " + getLayoutPosition(), Toast.LENGTH_SHORT).show();
        } */
    }
}
