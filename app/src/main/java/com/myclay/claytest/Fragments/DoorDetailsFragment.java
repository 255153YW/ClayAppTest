package com.myclay.claytest.Fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.myclay.claytest.Adapters.DoorDetailsAdapter;
import com.myclay.claytest.Adapters.DoorListAdapter;
import com.myclay.claytest.Adapters.UserListAdapter;
import com.myclay.claytest.Connections.AsyncTaskRequest;
import com.myclay.claytest.Interfaces.AsyncRequestCaller;
import com.myclay.claytest.Interfaces.UserManager;
import com.myclay.claytest.Models.DataModel;
import com.myclay.claytest.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class DoorDetailsFragment extends Fragment implements AsyncRequestCaller,UserManager {
    private View viewObject;

    public DoorDetailsFragment() {
        // Required empty public constructor
    }

    public static DoorDetailsFragment newInstance(JSONObject data) {
        DoorDetailsFragment f = new DoorDetailsFragment();
        Bundle args = new Bundle();
        args.putString("selected",data.toString());
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.viewObject =  inflater.inflate(R.layout.fragment_door_details, container, false);
        Bundle args = this.getArguments();
        if(args != null){
            try {
                populateDoorDetailsListview(new JSONObject(args.getString("selected")));
                new AsyncTaskRequest(this, "getAuthorizedUserAt", viewObject.getContext()).execute(new JSONObject(args.getString("selected")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return viewObject;
    }

    private void populateDoorDetailsListview(JSONObject data){
        JSONArray doorDetails = new JSONArray();
        ListView listView = (ListView) this.viewObject.findViewById(R.id.listView_doorInfo);

        try {
            doorDetails = data.getJSONArray("doorDetails");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Construct the data source
        ArrayList<DataModel> arrayOfDoors = DataModel.getDoorModels(doorDetails);
        // Create the adapter to convert the array to views
        DoorDetailsAdapter adapter = new DoorDetailsAdapter(this.viewObject.findViewById(R.id.listView_doorInfo).getContext(), arrayOfDoors);
        // Attach the adapter to a ListView
        listView.setAdapter(adapter);
    }

    private void populateAuthorizedUserListview(JSONObject data){
        JSONArray userlist = new JSONArray();
        ListView listView = (ListView) this.viewObject.findViewById(R.id.listView_userInfo);
        TextView msg = (TextView)this.viewObject.findViewById(R.id.textView_message);

        try {
            userlist = data.getJSONArray("userList");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(userlist.length() <= 0){
            msg.setText("You are the only user that are authorized to open this door");
            msg.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
        else{
            msg.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            // Construct the data source
            ArrayList<DataModel> arrayOfUsers = DataModel.getDoorModels(userlist);
            // Create the adapter to convert the array to views
            UserListAdapter adapter = new UserListAdapter(this,this.viewObject.findViewById(R.id.listView_userInfo).getContext(), arrayOfUsers);
            // Attach the adapter to a ListView
            listView.setAdapter(adapter);
        }


    }

    @Override
    public void requestProcessFinish(Boolean success, String message, JSONObject data) {
        String reqType = null;
        try {
            reqType = data.getString("reqType");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        switch(reqType){
            case "getAuthorizedUserAt":
                populateAuthorizedUserListview(data);
                break;
            case "revokeDoorFromUser":
                if(data.has("ERROR")){
                    //other handler
                }
                else{
                    Toast toast = Toast.makeText(this.viewObject.getContext(), "User Revoked", Toast.LENGTH_SHORT);
                    toast.show();
                    Bundle args = this.getArguments();
                    try {
                        new AsyncTaskRequest(this, "getAuthorizedUserAt", viewObject.getContext()).execute(new JSONObject(args.getString("selected")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }

    }

    @Override
    public void assignDoorToUser(JSONObject data) {

    }

    @Override
    public void revokeDoorFromUser(JSONObject data) {
        new AsyncTaskRequest(this, "revokeDoorFromUser", viewObject.getContext()).execute(data);
    }
}
