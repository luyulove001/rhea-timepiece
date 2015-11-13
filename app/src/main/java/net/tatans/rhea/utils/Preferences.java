package net.tatans.rhea.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2015/10/28.
 */
public class Preferences {
    private static final String SHARED_PATH = "Coeus_CountDown";
    private SharedPreferences sharedPreferences;
    public  Preferences(Context context) {
        sharedPreferences=context.getSharedPreferences(SHARED_PATH, Context.MODE_PRIVATE);
    }

    public  void putInt(String key, int value) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt(key, value);
        edit.commit();
    }

    public  int getInt(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    public  void putString(String key, String value) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key,null);
    }

    public  void putBoolean(String key, boolean value) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(key, value);
        edit.commit();
    }

    public  boolean getBoolean(String key,boolean defValue) {
        return sharedPreferences.getBoolean(key,defValue);
    }

    public boolean contains(String key){
        return sharedPreferences.contains(key);
    }

    public void putLong(String key, Long value){
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putLong(key, value);
        edit.commit();
    }

    public Long getLong(String key, Long defValue){
        return sharedPreferences.getLong(key, defValue);
    }
}
