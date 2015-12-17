package com.myclay.claytest.Connections;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.myclay.claytest.Interfaces.AsyncRequestCaller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Yohandi on 15/12/2015.
 */
public class SQLiteHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "ClayTestDB";

    private Context ctx;

    private static JSONArray dummy;

    private static JSONArray dummyHistory;

    private static JSONArray dummyUserPermission;

    public SQLiteHelper(AsyncTaskRequest atr, Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.ctx = context;
    }

    private Boolean isDBExist(){
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(ctx.getDatabasePath(SQLiteHelper.DATABASE_NAME).toString(), null,
                    SQLiteDatabase.OPEN_READONLY);
            checkDB.close();
        } catch (SQLiteException e) {
            // database doesn't exist yet.
        }
        return checkDB != null;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        if(!isDBExist()){
            String CREATE_USER_TABLE = "CREATE TABLE users ( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT )";

            String CREATE_USER_AUTH = "CREATE TABLE users_auth ( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER, "+
                    "door_id INTEGER )";

            String CREATE_DOORS_TABLE = "CREATE TABLE doors ( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "door_name TEXT, "+
                    "door_location TEXT,"+
                    "door_status TEXT )";

            String CREATE_PROPERTIES_TABLE = "CREATE TABLE properties ( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "property_name TEXT, "+
                    "country_name TEXT,"+
                    "postcode TEXT,"+
                    "street_name TEXT,"+
                    "number TEXT )";

            String CREATE_DOORLIST_TABLE = "CREATE TABLE properties_doorlist ( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "property_id INTEGER, "+
                    "door_id INTEGER )";

            String[] statements = new String[]{CREATE_USER_TABLE, CREATE_USER_AUTH, CREATE_DOORS_TABLE, CREATE_PROPERTIES_TABLE, CREATE_DOORLIST_TABLE};
            // create books table

            for(String sql :statements){
                db.execSQL(sql);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS users_auth");
        db.execSQL("DROP TABLE IF EXISTS doors");
        db.execSQL("DROP TABLE IF EXISTS properties");
        db.execSQL("DROP TABLE IF EXISTS properties_doorlist");

        // create fresh books table
        this.onCreate(db);
    }


    public JSONObject assignDoorToUser(JSONObject data){
        System.out.println("assign door to user");
        System.out.println(data);
        return new JSONObject();
    }

    public JSONObject revokeDoorFromUser(JSONObject data){
        try {
            JSONObject selected = data.getJSONObject("selected");

            for(int i = 0; i<dummyUserPermission.length();i++){
                JSONObject temp = dummyUserPermission.getJSONObject(i);
                if(temp.optString("door_id", "").equals(selected.optString("door_id","")) && temp.optString("user_name", "").equals(selected.optString("user_name",""))){
                    dummyUserPermission.remove(i);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }


    public JSONObject unlockDoor(JSONObject data){
        JSONObject newData = new JSONObject();
        JSONObject historyData = new JSONObject();
        if(dummyHistory == null){
            dummyHistory = new JSONArray();
        }
        try {
            int position = data.getInt("position");
            JSONObject selected = data.getJSONObject("selected");
            for(int i = 0; i<dummy.length();i++){
                JSONObject temp = dummy.getJSONObject(i);
                if(temp.optString("door_id","").equals(selected.optString("door_id", ""))){
                    if(isAuthorized(temp.optString("door_id",""))){
                        dummy.getJSONObject(i).put("door_status", String.valueOf("Unlocked"));
                        newData.put("position", position);
                        newData.put("data", dummy.getJSONObject(i));

                        SharedPreferences settings = ctx.getSharedPreferences("SharedPref", Context.MODE_PRIVATE);
                        String username = settings.getString("user", "user1");


                        historyData.put("action",String.valueOf("unlock door"));
                        historyData.put("actor",username);
                        historyData.put("door_name",dummy.getJSONObject(i).optString("door_name",""));
                        historyData.put("door_location",dummy.getJSONObject(i).optString("door_location",""));
                        historyData.put("property_name", dummy.getJSONObject(i).optString("property_name", ""));
                        historyData.put("timestamp", Calendar.getInstance().getTime().toString());

                        dummyHistory.put(historyData);
                    }
                    else{
                        System.out.println("auth_error");
                        newData.put("ERROR",String.valueOf("AuthError"));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return newData;
    }

    public JSONObject lockDoor(JSONObject data){
        JSONObject newData = new JSONObject();
        if(dummyHistory == null){
            dummyHistory = new JSONArray();
        }
        try {
            int position = data.getInt("position");
            JSONObject selected = data.getJSONObject("selected");
            for(int i = 0; i<dummy.length();i++){
                JSONObject temp = dummy.getJSONObject(i);

                if(temp.optString("door_id","").equals(selected.optString("door_id",""))){
                    if(isAuthorized(temp.optString("door_id",""))){
                        dummy.getJSONObject(i).put("door_status", String.valueOf("Locked"));
                        newData.put("position", position);
                        newData.put("data",dummy.getJSONObject(i));
                    }
                    else{
                        SharedPreferences settings = ctx.getSharedPreferences("SharedPref", Context.MODE_PRIVATE);
                        String username = settings.getString("user", "user1");

                        JSONObject historyData = new JSONObject();
                        historyData.put("action", String.valueOf("lock door"));
                        historyData.put("actor",username);
                        historyData.put("door_name",data.getJSONObject("selected").optString("door_name",""));
                        historyData.put("door_location",data.getJSONObject("selected").optString("door_location",""));
                        historyData.put("property_name", data.getJSONObject("selected").optString("property_name", ""));
                        historyData.put("timestamp", Calendar.getInstance().getTime().toString());

                        dummyHistory.put(historyData);

                        System.out.println("auth_error");
                        newData.put("ERROR", String.valueOf("AuthError"));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return newData;
    }

    public JSONObject getDetails(JSONObject data){
        JSONArray temp = new JSONArray();
        JSONObject holder = new JSONObject();
        if(dummyHistory == null){
            dummyHistory = new JSONArray();
        }
        try {
            if(isAuthorized(data.getJSONObject("selected").optString("door_id",""))){
                temp.put(data.getJSONObject("selected"));
                holder.put("doorDetails", temp);
            }
            else{
                SharedPreferences settings = ctx.getSharedPreferences("SharedPref", Context.MODE_PRIVATE);
                String username = settings.getString("user", "user1");

                JSONObject historyData = new JSONObject();
                historyData.put("action", String.valueOf("get door details"));
                historyData.put("actor",username);
                historyData.put("door_name",data.getJSONObject("selected").optString("door_name",""));
                historyData.put("door_location",data.getJSONObject("selected").optString("door_location",""));
                historyData.put("property_name", data.getJSONObject("selected").optString("property_name", ""));
                historyData.put("timestamp", Calendar.getInstance().getTime().toString());

                dummyHistory.put(historyData);
                System.out.println("auth_error");
                holder.put("ERROR", String.valueOf("AuthError"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return holder;
    }

    public JSONObject getDoorListAtProperty(JSONObject data){
        //create dummy
        if(dummyUserPermission == null){
            //dummy data
            String test = "{\"door_id\":\"d1\",\"user_name\":\"user1\"}";
            String test2 = "{\"door_id\":\"d2\",\"user_name\":\"user1\"}";
            String test3 = "{\"door_id\":\"d3\",\"user_name\":\"user1\"}";
            String test4 = "{\"door_id\":\"d4\",\"user_name\":\"user1\"}";
            String test5 = "{\"door_id\":\"d5\",\"user_name\":\"user1\"}";
            String test6 = "{\"door_id\":\"d5\",\"user_name\":\"user2\"}";
            String test7 = "{\"door_id\":\"d2\",\"user_name\":\"user2\"}";
            String test8 = "{\"door_id\":\"d2\",\"user_name\":\"user2\"}";
            //dummy data
            dummyUserPermission = new JSONArray();
            try {
                dummyUserPermission.put(new JSONObject(test));
                dummyUserPermission.put(new JSONObject(test2));
                dummyUserPermission.put(new JSONObject(test3));
                dummyUserPermission.put(new JSONObject(test4));
                dummyUserPermission.put(new JSONObject(test5));
                dummyUserPermission.put(new JSONObject(test6));
                dummyUserPermission.put(new JSONObject(test7));
                dummyUserPermission.put(new JSONObject(test8));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //create dummy
        if(dummy == null){
            //dummy data
            String test = "{\"door_id\":\"d1\",\"door_name\":\"Front door\",\"door_location\":\"1st floor entrance\",\"property_name\":\"Home\",\"door_status\":\"Locked\"}";
            String test2 = "{\"door_id\":\"d2\",\"door_name\":\"Back door\",\"door_location\":\"1st floor kitchen\",\"property_name\":\"Home\",\"door_status\":\"Locked\"}";
            String test3 = "{\"door_id\":\"d3\",\"door_name\":\"Tunnel entrance\",\"door_location\":\"tunnel\",\"property_name\":\"Office\",\"door_status\":\"Unlocked\"}";
            String test4 = "{\"door_id\":\"d4\",\"door_name\":\"Main entrance\",\"door_location\":\"main entrance\",\"property_name\":\"Office\",\"door_status\":\"Unlocked\"}";
            String test5 = "{\"door_id\":\"d5\",\"door_name\":\"garage door\",\"door_location\":\"1st floor garage\",\"property_name\":\"Home\",\"door_status\":\"Locked\"}";
            //dummy data
            dummy = new JSONArray();
            try {
                dummy.put(new JSONObject(test));
                dummy.put(new JSONObject(test2));
                dummy.put(new JSONObject(test3));
                dummy.put(new JSONObject(test4));
                dummy.put(new JSONObject(test5));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONObject holder = new JSONObject();
        JSONArray holder2 = new JSONArray();
        try {
            for(int i = 0; i<dummy.length();i++){
                JSONObject temp = dummy.getJSONObject(i);

                if(temp.optString("property_name","").equals(data.optString("selectedProperty",""))){
                    holder2.put(temp);
                }
            }
            holder.put("doorlist",holder2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return holder;
    }

    public JSONObject getPropertyList(JSONObject data){
        JSONArray temp = new JSONArray();
        String test = "{\"property_name\":\"Home\"}";
        String test2 = "{\"property_name\":\"Office\"}";
        try {
            temp.put(new JSONObject(test));
            temp.put(new JSONObject(test2));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject temp2 = new JSONObject();
        try {
            temp2.put("properties",temp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temp2;
    }

    public JSONObject getHistoryList(JSONObject data){
        JSONObject temp = new JSONObject();
        if(dummyHistory == null){
            dummyHistory = new JSONArray();
        }

        try {
            temp.put("historylist",dummyHistory);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return temp;
    }

    public JSONObject getAuthorizedUserAt(JSONObject data){

        JSONArray holder = new JSONArray();
        JSONObject holder2 = new JSONObject();

        try {
            JSONArray doorDetails = data.getJSONArray("doorDetails");

            for(int i = 0; i<dummyUserPermission.length();i++){
                JSONObject temp = dummyUserPermission.getJSONObject(i);

                SharedPreferences settings = ctx.getSharedPreferences("SharedPref", Context.MODE_PRIVATE);
                String username = settings.getString("user", "user1");

                if(temp.optString("door_id", "").equals(doorDetails.getJSONObject(0).optString("door_id", "")) && !temp.optString("user_name", "").equals(username)){
                   holder.put(temp);
                }
            }
            holder2.put("userList",holder);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return holder2;
    }

    private Boolean isAuthorized(String doorID){
        try {
            for(int i = 0; i<dummyUserPermission.length();i++){
                JSONObject temp = dummyUserPermission.getJSONObject(i);

                SharedPreferences settings = ctx.getSharedPreferences("SharedPref", Context.MODE_PRIVATE);
                String username = settings.getString("user", "user1");

                if(temp.optString("door_id", "").equals(doorID) && temp.optString("user_name", "").equals(username)){
                    return true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
