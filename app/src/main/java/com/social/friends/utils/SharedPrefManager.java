package com.social.friends.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static String SHARED_PREF_NAME = "makeFriends";
    private SharedPreferences sharedPreferences;
    private Context context;
    private SharedPreferences.Editor editor;

    public SharedPrefManager(Context context) {
        this.context = context;
    }

    public void saveWallpaper(String wallpaper){
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("WALLPAPER",wallpaper);
        editor.apply();
    }

    public String getBackgroundWallpaper(){
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString("WALLPAPER",null);
    }


}
