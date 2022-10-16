package com.audy.ecomplaint.session;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.audy.ecomplaint.DashboardActivity;
import com.audy.ecomplaint.LoginActivity;

import java.util.HashMap;

public class UserManagement {
    Context context;
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;

    public static final String PREF_NAME = "User_Login";
    public static final String LOGIN = "is_user_login";
    public static final String ID = "id";
    public static final String NAME = "nama";
    public static final String NO_HP = "no_hp";
    public static final String NO_METER = "no_meter";
    public static final String DAYA = "daya";
    public static final String ALAMAT = "alamat";

    public UserManagement(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME,context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public boolean isUserLogin(){
        return sharedPreferences.getBoolean(LOGIN,false);
    }

    public void UserSessionManage(String id, String nama, String no_hp, String no_meter, String daya, String alamat){
        editor.putBoolean(LOGIN,true);
        editor.putString(ID, id);
        editor.putString(NAME, nama);
        editor.putString(NO_HP,no_hp);
        editor.putString(NO_METER, no_meter);
        editor.putString(DAYA,daya);
        editor.putString(ALAMAT,alamat);
        editor.apply();
    }

    public void checkLogin(){
        if(!this.isUserLogin()){
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
            ((DashboardActivity) context).finish();
        }
    }

    public HashMap<String,String> userDetails(){
        HashMap<String,String> user = new HashMap<>();
        user.put(ID,sharedPreferences.getString(ID, null));
        user.put(NAME,sharedPreferences.getString(NAME, null));
        user.put(NO_HP,sharedPreferences.getString(NO_HP, null));
        user.put(NO_METER,sharedPreferences.getString(NO_METER, null));
        user.put(DAYA,sharedPreferences.getString(DAYA, null));
        user.put(ALAMAT,sharedPreferences.getString(ALAMAT, null));
        return user;
    }

    public void logout(){
        editor.clear();
        editor.commit();

        Intent intent = new Intent(context,LoginActivity.class);
        context.startActivity(intent);
        ((DashboardActivity) context).finish();
    }
}
