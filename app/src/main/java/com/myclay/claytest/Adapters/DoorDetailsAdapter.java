package com.myclay.claytest.Adapters;

import android.app.Fragment;
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
import android.widget.ListView;
import android.widget.TextView;

import com.myclay.claytest.Fragments.DoorDetailsFragment;
import com.myclay.claytest.Fragments.ManageDoorFragment;
import com.myclay.claytest.Models.DataModel;
import com.myclay.claytest.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Yohandi on 16/12/2015.
 */
public class DoorDetailsAdapter extends ArrayAdapter<DataModel> {
    public DoorDetailsAdapter (Context context, ArrayList<DataModel> record){
        super(context, R.layout.listview_door_details,record);
    }

    // View lookup cache
    private static class ViewHolder {
        TextView textView_doorName;
        TextView textView_doorLocation;
        TextView textView_doorStatus;
        ImageView imageView_thumb;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataModel dm = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_door_details, parent, false);

            // Lookup view for data population
            viewHolder.textView_doorName = (TextView) convertView.findViewById(R.id.textView_doorName);
            viewHolder.textView_doorLocation = (TextView) convertView.findViewById(R.id.textView_doorLocation);
            viewHolder.textView_doorStatus = (TextView) convertView.findViewById(R.id.textView_doorStatus);
            viewHolder.imageView_thumb = (ImageView) convertView.findViewById(R.id.imageView_thumb);


            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        JSONObject data = dm.getData();


        // check and populate the data into the template view using the data object
        String temp = safeGet(data, "door_name");
        if (isVisible(temp, viewHolder.textView_doorName)){
            viewHolder.textView_doorName.setText(temp);
        }

        temp = safeGet(data, "door_location");
        if (isVisible(temp, viewHolder.textView_doorLocation)){
            viewHolder.textView_doorLocation.setText(temp);
        }

        temp = safeGet(data, "door_status");
        if (isVisible(temp, viewHolder.textView_doorStatus)){
            viewHolder.textView_doorStatus.setText("status: "+ temp);
        }

        temp = safeGet(data, "door_thumb");
        if(temp != null){
            byte[] decodedByte = Base64.decode(temp, 0);
            Bitmap bm = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
            viewHolder.imageView_thumb.setImageBitmap(bm);
        }
        else{
            viewHolder.imageView_thumb.setImageResource(android.R.drawable.ic_dialog_email);
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
