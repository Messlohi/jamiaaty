package com.example.jamiaaty.Home.localdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
import com.example.jamiaaty.Home.Module_pack.Module;
import java.util.ArrayList;

public class localdb extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 5;
    private static final String db_Name = "Modules.db";
    private static final String tab_Modules = "Modules";
    private static final String Module_Key = "id";
    private static final String Module_Name = "name";
    private static final String Module_imgLink = "imgLink";
    private static final String Module_description = "description";
    public localdb(@Nullable Context context) {
        super(context, db_Name, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MODULES_TABLE ="CREATE TABLE "+tab_Modules+"("+ Module_Key + " TEXT PRIMARY KEY," +
                Module_Name + " TEXT, "+ Module_imgLink + " TEXT, " + Module_description + " TEXT" +")";
        db.execSQL(CREATE_MODULES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + tab_Modules);
        onCreate(db);
    }
    public void addModule(Module module) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Module_Key, module.key);
        values.put(Module_Name, module.name);
        values.put(Module_imgLink, module.imgLink);
        values.put(Module_description, module.description);
        db.insert(tab_Modules, null, values);
        db.close();
    }

    public Module getModule(String id) {
        SQLiteDatabase db = this.getReadableDatabase(); ;
        Module module=null;
        Cursor cursor = db.query(tab_Modules, new String[]{Module_Key,
                        Module_Name, Module_imgLink,Module_description}, Module_Key + "=?",
                new String[]{id}, null, null, null, null);

        if (cursor != null && cursor.getCount() >0 && cursor.moveToFirst()){
            module = new Module( cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)
            );
        }
        else return null;
        return module;
    }
    public ArrayList<Module> getAllModules() {
        ArrayList<Module> moduleList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + tab_Modules;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Module module = new Module();
                module.key= cursor.getString(0);
                module.name=cursor.getString(1);
                module.imgLink=cursor.getString(2);
                module.description=cursor.getString(3);
                moduleList.add(module);
            } while (cursor.moveToNext());
        }
        return moduleList;
    }
    public int updateModule(Module module) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Module_Key, module.key);
        values.put(Module_Name, module.getName());
        values.put(Module_imgLink, module.imgLink);
        values.put(Module_description, module.description);
        return db.update(tab_Modules, values, Module_Key + " = ?",
                new String[] { String.valueOf(module.key) });

    }
    public void deleteModule(Module module) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete( tab_Modules, Module_Key + " = ?",
                new String[] {module.key}
        );
        db.close();
    }

}
