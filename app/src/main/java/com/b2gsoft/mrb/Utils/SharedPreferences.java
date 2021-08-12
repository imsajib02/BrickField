package com.b2gsoft.mrb.Utils;

import android.content.Context;

public class SharedPreferences {

    public static final String IS_LOGGED_IN = "is_logged_in";

    public static final String USER_ID = "user_id";
    public static final String USER_TYPE_ID = "user_type_id";

    public static final String USER_NAME = "user_name";

    public static final String USER_PHONE = "user_phone";

    public static final String PREF_NAME = "b2g_dev";

    public static final String USER_TYPE = "";

    public static final String ROLE = "role";
    public static final String ADMIN_ID = "admin_id";
    public static final String TOTAL_BALANCE_PERMISSION = "total_balance_permission";
    public static final String CURRENT_BALANCE_PERMISSION = "current_balance_permission";
    public static final String EDIT_PERMISSION = "edit_permission";

    public Context context;
    public static android.content.SharedPreferences prefs;
    private String SESSION_TOKEN = "session_token";

    public SharedPreferences(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

    }

    public String getUserPhone() {
        return prefs.getString(USER_PHONE, "");
    }

    public int getUserId() {return prefs.getInt(USER_ID, 0);}


    public void setUserId(int id) {
        prefs.edit().putInt(USER_ID, id).commit();
    }

    public int getUserTypeId() {return prefs.getInt(USER_TYPE_ID, 0);}


    public void setUserTypeId(int id) {
        prefs.edit().putInt(USER_TYPE_ID, id).commit();
    }

    public void setUserName(String name) {
        prefs.edit().putString(USER_NAME, name).commit();
    }
    public String getUserName() {
        return prefs.getString(USER_NAME, "");
    }


    public void setUserPhone(String phone) {
        prefs.edit().putString(USER_PHONE, phone).commit();
    }


    public void setIsLoggedIn(boolean value) {
        prefs.edit().putBoolean(IS_LOGGED_IN, value).commit();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(IS_LOGGED_IN, false);
    }

    public void setSessionToken(String sessionToken){
        prefs.edit().putString(SESSION_TOKEN,sessionToken).commit();
    }
    public String getSessionToken(){
        return prefs.getString(SESSION_TOKEN,"");
    }

    public void setUserType(String userType){
        prefs.edit().putString(USER_TYPE, userType).commit();
    }
    public String getUserType(){
        return prefs.getString(USER_TYPE, "");
    }

    public void setRole(String role){
        prefs.edit().putString(ROLE, role).commit();
    }
    public String getRole(){
        return prefs.getString(ROLE, "");
    }

    public void setAdminId(int id){
        prefs.edit().putInt(ADMIN_ID, id).commit();
    }
    public int getAdminId(){
        return prefs.getInt(ADMIN_ID, 0);
    }

    public void setTotalBalancePermission(String permission){
        prefs.edit().putString(TOTAL_BALANCE_PERMISSION, permission).commit();
    }
    public String getTotalBalancePermission(){
        return prefs.getString(TOTAL_BALANCE_PERMISSION, "0");
    }

    public void setCurrentBalancePermission(String permission){
        prefs.edit().putString(CURRENT_BALANCE_PERMISSION, permission).commit();
    }
    public String getCurrentBalancePermission(){
        return prefs.getString(CURRENT_BALANCE_PERMISSION, "0");
    }

    public void setEditPermission(String permission){
        prefs.edit().putString(EDIT_PERMISSION, permission).commit();
    }
    public String getEditPermission(){
        return prefs.getString(EDIT_PERMISSION, "0");
    }
}
