package com.example.jamiaaty.Home.Module_pack;

public class Module {
    public String key;
    public String name;
    public String imgLink;
    public String description;
    public Module(){}
    public Module(String key, String name, String imgLink,String description){
        this.key = key;
        this.name = name;
        this.imgLink = imgLink;
        this.description = description;
    }
    public String getKey(){ return this.key; }
    public void setKey(String key){ this.key = key; }
    public String getName(){ return this.name; }
    public void setName(String name){ this.name = name; }
    public String getimgLink(){return this.imgLink;}
    public void setimgLink(String imgLink){ this.imgLink = imgLink; }
    public void setdescription(String description){ this.description = description; }
    public String getdescription(){return this.description; }
}
