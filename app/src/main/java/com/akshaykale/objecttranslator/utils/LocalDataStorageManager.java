package com.akshaykale.objecttranslator.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.akshaykale.objecttranslator.OTApplication;
import com.akshaykale.objecttranslator.utils.retrofit.RetroApiController;

public class LocalDataStorageManager {
    private final String TAG = getClass().getSimpleName();
    private static LocalDataStorageManager sInstance;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private LocalDataStorageManager(){
        sharedPreferences = OTApplication.getAppContext().
                getSharedPreferences(TAG, Context.MODE_PRIVATE);

        editor = sharedPreferences.edit();
    }

    public static LocalDataStorageManager getInstance(){
        if(sInstance == null){
            sInstance = new LocalDataStorageManager();
        }
        return sInstance;
    }

    public void clear(){
        editor.clear();
        editor.apply();
    }

    public void translationFrom(String key) {
        editor.putString("translationFrom", key);
        editor.apply();
    }
    public String translationFrom(){
        return sharedPreferences.getString("translationFrom","English");
    }

    public void translationTo(String key) {
        editor.putString("translationTo", key);
        editor.apply();
    }
    public String translationTo(){
        return sharedPreferences.getString("translationTo","Japanese");
    }

    public void swapLanguage(){
        String to = translationTo();
        translationTo(translationFrom());
        translationFrom(to);
    }

    public void key(String key) {
        editor.putString("key", key);
        editor.apply();
    }
    public String key(){
        final String r = sharedPreferences.getString("key", "SOHIU4COJZGRKWM4HGMG6I7Y3HM5BSYRRM3RO5XGWO5T6MSTZNBWIE6E7DO2S3OK3LCCD6YSMDFYVXPGR2FZCDSPXQ6GZZ5YIQENUVQ");
        return r;
    }
}
