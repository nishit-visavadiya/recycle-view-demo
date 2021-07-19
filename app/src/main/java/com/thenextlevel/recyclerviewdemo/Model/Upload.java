package com.thenextlevel.recyclerviewdemo.Model;

import android.provider.ContactsContract;

import com.google.firebase.database.Exclude;

public class Upload {

    private String mName;
    private String mImageUrl;
    private String mKey;

    public Upload() {

    }

    public Upload(String name, String imageUrl) {

        if (name.trim().equals("")) {
            name = "No name";
        }

        this.mName = name;
        this.mImageUrl = imageUrl;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.mImageUrl = imageUrl;
    }

    @Exclude
    public String getKey() {
        return mKey;
    }

    @Exclude
    public void setKey(String key) {
        this.mKey = key;
    }
}
