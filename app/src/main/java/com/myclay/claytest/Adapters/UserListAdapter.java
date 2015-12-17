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
import android.widget.ListView;
import android.widget.TextView;

import com.myclay.claytest.Interfaces.UserManager;
import com.myclay.claytest.Models.DataModel;
import com.myclay.claytest.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Yohandi on 16/12/2015.
 */
public class UserListAdapter extends ArrayAdapter<DataModel> {
    private UserManager um;

    public UserListAdapter (UserManager um,Context context, ArrayList<DataModel> record){
        super(context, R.layout.listview_user_entry,record);
        this.um = um;
    }

    // View lookup cache
    private static class ViewHolder {
        TextView textView_username;
        ImageView imageView_thumb;
        Button button_revoke;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataModel dm = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_user_entry, parent, false);

            // Lookup view for data population
            viewHolder.textView_username = (TextView) convertView.findViewById(R.id.textView_username);
            viewHolder.imageView_thumb = (ImageView) convertView.findViewById(R.id.imageView_thumb);
            viewHolder.button_revoke = (Button) convertView.findViewById(R.id.button_revoke);
            viewHolder.button_revoke.setOnClickListener(new ItemClickListener());

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        JSONObject data = dm.getData();


        // check and populate the data into the template view using the data object
        String temp = safeGet(data, "user_name");
        if (isVisible(temp, viewHolder.textView_username)){
            viewHolder.textView_username.setText(temp);
        }

        temp = safeGet(data, "user_thumb");
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

    // Handles clicks on the menu board buttons
    private class ItemClickListener implements View.OnClickListener {
        public void onClick(View v) {
            View parentRow1 = (View) v.getParent();
            View parentRow2 = (View)parentRow1.getParent();
            ListView listView = (ListView) parentRow2.getParent();
            int position = listView.getPositionForView(parentRow2);
            ArrayList<DataModel> selected = new ArrayList<DataModel>();
            selected.add((DataModel) listView.getAdapter().getItem(position));

            JSONObject holder = new JSONObject();
            try {
                holder.put("position",Integer.valueOf(position));
                holder.put("selected",selected.get(0).getData());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            switch (v.getId()){
                case R.id.button_revoke:
                    um.revokeDoorFromUser(holder);
                    break;
                default:
                    System.out.println("button clicked: " + String.valueOf(v.getId()));
                    break;
            }

        }
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
