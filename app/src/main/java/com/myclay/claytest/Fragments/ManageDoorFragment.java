package com.myclay.claytest.Fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.myclay.claytest.Adapters.DoorListAdapter;
import com.myclay.claytest.Connections.AsyncTaskRequest;
import com.myclay.claytest.Connections.SQLiteHelper;
import com.myclay.claytest.Interfaces.AsyncRequestCaller;
import com.myclay.claytest.MainActivity;
import com.myclay.claytest.Models.DataModel;
import com.myclay.claytest.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageDoorFragment extends Fragment implements AsyncRequestCaller {
    private View viewObject;
    private ManageDoorFragment currentObject;
    private static Integer selectedPropertyPosition;
    private ProgressDialog pd;

    public ManageDoorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        currentObject = this;
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.viewObject =  inflater.inflate(R.layout.fragment_manage, container, false);
        new AsyncTaskRequest(currentObject, "getPropertyList", viewObject.getContext()).execute(new JSONObject());

        if(this.pd == null){
            this.pd = new ProgressDialog(viewObject.getContext());
            pd.setCancelable(true);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }


        return viewObject;
    }

    public void unlockDoor(JSONObject selected){
        pd.setTitle("Unlocking Door");
        pd.setMessage("Please wait");
        pd.show();
        new AsyncTaskRequest(this,"unlockDoor",viewObject.getContext()).execute(selected);
    }

    public void lockDoor(JSONObject selected){
        pd.setTitle("Locking Door");
        pd.setMessage("Please wait");
        pd.show();
        new AsyncTaskRequest(this,"lockDoor",viewObject.getContext()).execute(selected);
    }

    public void getDetails(JSONObject selected){
        pd.setTitle("Loading Details");
        pd.setMessage("Please wait");
        pd.show();
        new AsyncTaskRequest(this,"getDoorDetails",viewObject.getContext()).execute(selected);
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
            case "getPropertyList":
                populateSpinner(data);
                break;
            case "getDoorListAtProperty":
                populateListview(data);
                break;
            case "lockDoor":
                updateListview(data);
                break;
            case "unlockDoor":
                updateListview(data);
                break;
            case "getDoorDetails":
                showDetails(data);
                break;
        }
    }
    private void populateSpinner(JSONObject data){
        final Spinner spinner = (Spinner) this.viewObject.findViewById(R.id.spinner);
        ArrayList<String> list = new ArrayList<>();
        try {
            JSONArray arrays = data.getJSONArray("properties");
            for (int i = 0; i < arrays.length(); i++) {
                JSONObject row = null;

                row = arrays.getJSONObject(i);

                list.add(row.getString("property_name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this.viewObject.getContext(),android.R.layout.simple_spinner_item, list);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner.setAdapter(spinnerArrayAdapter);

        if(this.selectedPropertyPosition != null){
            spinner.setSelection(selectedPropertyPosition);
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedProperty = spinner.getSelectedItem().toString();
                selectedPropertyPosition = spinner.getSelectedItemPosition();
                JSONObject temp = new JSONObject();
                try {
                    temp.put("selectedProperty",selectedProperty);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new AsyncTaskRequest(currentObject, "getDoorListAtProperty", viewObject.getContext()).execute(temp);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //do nothing
            }

        });


    }

    private void populateListview(JSONObject data){
        JSONArray doorlist = new JSONArray();
        ListView listView = (ListView) this.viewObject.findViewById(R.id.listView);

        try {
            doorlist = data.getJSONArray("doorlist");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Construct the data source
        ArrayList<DataModel> arrayOfDoors = DataModel.getDoorModels(doorlist);
        // Create the adapter to convert the array to views
        DoorListAdapter adapter = new DoorListAdapter(this, this.viewObject.findViewById(R.id.listView).getContext(), arrayOfDoors);
        // Attach the adapter to a ListView
        listView.setAdapter(adapter);
    }

    private void updateListview(JSONObject data){
        pd.dismiss();
        if(data.has("ERROR")){
            Toast toast = Toast.makeText(this.viewObject.getContext(), "Access Denied", Toast.LENGTH_SHORT);
            toast.show();
        }
        else{
            try {
                int position = data.getInt("position");
                JSONObject selected = data.getJSONObject("data");

                ListView listView = (ListView) this.viewObject.findViewById(R.id.listView);
                View v = listView.getChildAt(position-listView.getFirstVisiblePosition());

                if(v == null)
                    return;

                TextView someText = (TextView) v.findViewById(R.id.textView_doorStatus);
                someText.setText("status: " + selected.optString("door_status",""));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void showDetails(JSONObject data){
        pd.dismiss();
        if(data.has("ERROR")){
            Toast toast = Toast.makeText(this.viewObject.getContext(), "Access Denied", Toast.LENGTH_SHORT);
            toast.show();
        }
        else{
            MainActivity main = (MainActivity) this.getActivity();
            main.showDetailsFragment(data);
        }

    }
}
