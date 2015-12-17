package com.myclay.claytest.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.myclay.claytest.Models.DataModel;
import com.myclay.claytest.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Yohandi on 12/12/2015.
 */
public class HistoryListAdapter extends ArrayAdapter<DataModel> {

    public HistoryListAdapter (Context context, ArrayList<DataModel> record){
        super(context, R.layout.listview_history_entry,record);
    }
    // View lookup cache
    private static class ViewHolder {
        TextView textView_doorName;
        TextView textView_doorLocation;
        TextView textView_action;
        TextView textView_actor;
        TextView textView_timestamp;
        TextView textView_propertyname;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataModel dm = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_history_entry, parent, false);

            // Lookup view for data population
            viewHolder.textView_doorName = (TextView) convertView.findViewById(R.id.textView_doorName);
            viewHolder.textView_doorLocation = (TextView) convertView.findViewById(R.id.textView_doorLocation);
            viewHolder.textView_action = (TextView) convertView.findViewById(R.id.textView_action);
            viewHolder.textView_actor = (TextView) convertView.findViewById(R.id.textView_actor);
            viewHolder.textView_timestamp = (TextView) convertView.findViewById(R.id.textView_timestamp);
            viewHolder.textView_propertyname = (TextView) convertView.findViewById(R.id.textView_propertyname);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        JSONObject data = dm.getData();


        // check and populate the data into the template view using the data object
        String temp = safeGet(data, "door_name");
        if (isVisible(temp, viewHolder.textView_doorName)){
            viewHolder.textView_doorName.setText("Door name: " + temp);
        }

        temp = safeGet(data, "door_location");
        if (isVisible(temp, viewHolder.textView_doorLocation)){
            viewHolder.textView_doorLocation.setText("Door location: "+temp);
        }

        temp = safeGet(data, "action");
        if (isVisible(temp, viewHolder.textView_action)){
            viewHolder.textView_action.setText("Door action: "+temp);
        }

        temp = safeGet(data, "actor");
        if (isVisible(temp, viewHolder.textView_actor)){
            viewHolder.textView_actor.setText("Actor: "+temp);
        }

        temp = safeGet(data, "timestamp");
        if (isVisible(temp, viewHolder.textView_timestamp)){
            viewHolder.textView_timestamp.setText("Timestamp: "+temp);
        }

        temp = safeGet(data, "property_name");
        if (isVisible(temp, viewHolder.textView_propertyname)){
            viewHolder.textView_propertyname.setText("Property name: "+temp);
        }


        // Return the completed view to render on screen
        return convertView;
    }

    private String safeGet(JSONObject data, String key){
        if(data.has(key)){
            // It exists, return value
            try {
                return data.getString(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            // It doesn't exist, give null
            return null;
        }
        return null;
    }

    private Boolean isVisible(String val, View v){
        if(val == null){
            v.setVisibility(View.GONE);
            return  false;
        }
        else if(val.isEmpty()){
            v.setVisibility(View.GONE);
            return false;
        }
        else{
            v.setVisibility(View.VISIBLE);
            return true;
        }
    }
}
