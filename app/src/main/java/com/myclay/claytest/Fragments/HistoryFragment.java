package com.myclay.claytest.Fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.myclay.claytest.Adapters.HistoryListAdapter;
import com.myclay.claytest.Connections.AsyncTaskRequest;
import com.myclay.claytest.Interfaces.AsyncRequestCaller;
import com.myclay.claytest.Models.DataModel;
import com.myclay.claytest.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment implements AsyncRequestCaller {
    private View viewObject;

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.viewObject =  inflater.inflate(R.layout.fragment_history, container, false);

        final Spinner sort = (Spinner) this.viewObject.findViewById(R.id.spinner_sort);
        final Spinner period = (Spinner) this.viewObject.findViewById(R.id.spinner_period);

        ArrayList<String> sortlist = new ArrayList<>();
        sortlist.add("Newest first");
        sortlist.add("Oldest first");

        ArrayList<String> periodlist = new ArrayList<>();
        periodlist.add("24 hours");
        periodlist.add("7 days");
        periodlist.add("30 days");

        ArrayAdapter<String> spinnerArrayAdapterSort = new ArrayAdapter<String>(this.viewObject.getContext(),android.R.layout.simple_spinner_item, sortlist);
        ArrayAdapter<String> spinnerArrayAdapterPeriod = new ArrayAdapter<String>(this.viewObject.getContext(),android.R.layout.simple_spinner_item, periodlist);

        spinnerArrayAdapterSort.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinnerArrayAdapterPeriod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view

        sort.setAdapter(spinnerArrayAdapterSort);
        period.setAdapter(spinnerArrayAdapterPeriod);

        new AsyncTaskRequest(this, "getHistoryList", viewObject.getContext()).execute(new JSONObject());

        return viewObject;
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
            case "getHistoryList":
                populateListview(data);
                break;
        }
    }

    private void populateListview(JSONObject data){
        JSONArray historyList = new JSONArray();
        ListView listView = (ListView) this.viewObject.findViewById(R.id.listView);

        try {
            historyList = data.getJSONArray("historylist");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Construct the data source
        ArrayList<DataModel> arrayOfHistory = DataModel.getDoorModels(historyList);
        // Create the adapter to convert the array to views
        HistoryListAdapter adapter = new HistoryListAdapter(this.viewObject.findViewById(R.id.listView).getContext(), arrayOfHistory);
        // Attach the adapter to a ListView
        listView.setAdapter(adapter);
    }
}
