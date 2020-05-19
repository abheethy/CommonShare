package com.example.commonshare;

import android.content.Context;
import android.content.SharedPreferences;

public class UserLocalStore {
    public  static final String SP_NAME = "userDetails";
    SharedPreferences userLocalDataBase;
    public UserLocalStore(Context context){
        userLocalDataBase = context.getSharedPreferences(SP_NAME, 0);
    }

    public  void storeUserData(UserDetails userDetails){
        SharedPreferences.Editor spEditor = userLocalDataBase.edit();
        spEditor.putString("name", userDetails.getName());
        spEditor.putString("password", userDetails.getPassword());
        spEditor.putString("group", userDetails.getGroup());
        spEditor.commit();
    }
    public UserDetails getLoggdInUser(){
        String name = userLocalDataBase.getString("name", "");
        String password = userLocalDataBase.getString("password", "");
        String group = userLocalDataBase.getString("group", "");
        UserDetails userDetails = new UserDetails();
        userDetails.setName(name);
        userDetails.setPassword(password);
        userDetails.setGroup(group);
        return userDetails;
    }
    public  void setUserLoggedIn(boolean loggedIn){
        SharedPreferences.Editor spEditor = userLocalDataBase.edit();
        spEditor.putBoolean("loggedIn",loggedIn);
        spEditor.commit();
    }
    public void clearUserData(){
        SharedPreferences.Editor spEditor = userLocalDataBase.edit();
        spEditor.clear();
        spEditor.commit();
    }

    public boolean isUserLoggedIn(){
        if(userLocalDataBase.getBoolean("loggedIn", false) == true){
            return true;
        }else{
          return false;
        }
    }
}
