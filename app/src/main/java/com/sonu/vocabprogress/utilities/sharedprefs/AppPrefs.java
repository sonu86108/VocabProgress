package com.sonu.vocabprogress.utilities.sharedprefs;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPrefs {
    private SharedPreferences sharedPreferences;
    private static AppPrefs instance=null;
    private AppPrefs(Context context){
        sharedPreferences=context.getSharedPreferences(Prefs.SharedPrefs.APP_SETTINGS.toString(),Context.MODE_PRIVATE);
    }
    public static AppPrefs getInstance(Context context){
        if(instance == null){
            return instance=new AppPrefs(context);
        }
        return instance;
    }

    public boolean isServiceRunning(){
        return sharedPreferences.getBoolean(Prefs.AppSettings.SERVICE_RUNNING_STATUS.toString(),false);
    }

    public void setServiceRunningStatus(boolean status){
        sharedPreferences.edit().putBoolean(Prefs.AppSettings.SERVICE_RUNNING_STATUS.toString(),status).apply();
    }


}
