package com.myclay.claytest.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Yohandi on 12/12/2015.
 */
public class DataModel {
    private JSONObject JSONHolder;
    public DataModel(JSONObject data){
        this.JSONHolder = data;
    }

    public static ArrayList<DataModel> getDoorModels(JSONArray data){
        ArrayList<DataModel> records = new ArrayList<DataModel>();
        try {
            for (int i = 0; i < data.length(); i++) {
                records.add(new DataModel(data.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return records;
    }

    public JSONObject getData(){
        return this.JSONHolder;
    }
}
