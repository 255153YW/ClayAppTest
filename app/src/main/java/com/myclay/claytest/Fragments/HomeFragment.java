package com.myclay.claytest.Fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.myclay.claytest.MainActivity;
import com.myclay.claytest.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private View viewObject;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.viewObject =  inflater.inflate(R.layout.fragment_home, container, false);
        Button btn_history = (Button) viewObject.findViewById(R.id.button_menu_history);
        Button btn_doors = (Button) viewObject.findViewById(R.id.button_menu_doors);
        Button btn_properties = (Button) viewObject.findViewById(R.id.button_menu_properties);
        Button btn_lock = (Button) viewObject.findViewById(R.id.button_menu_lock);
        Button btn_user = (Button) viewObject.findViewById(R.id.button_menu_user);
        Button btn_alert = (Button) viewObject.findViewById(R.id.button_menu_alert);

        //set event listener
        btn_history.setOnClickListener(new ItemClickListener());
        btn_doors.setOnClickListener(new ItemClickListener());
        btn_properties.setOnClickListener(new ItemClickListener());
        btn_lock.setOnClickListener(new ItemClickListener());
        btn_user.setOnClickListener(new ItemClickListener());
        btn_alert.setOnClickListener(new ItemClickListener());

        return viewObject;
    }

    // Handles clicks on the menu board buttons
    private class ItemClickListener implements View.OnClickListener {
        public void onClick(View view) {
            int itemID = view.getId();
            switch (itemID) {
                case R.id.button_menu_history:
                    ((MainActivity)getActivity()).showHistoryFragment();
                    break;
                case R.id.button_menu_doors:
                    ((MainActivity)getActivity()).showManageDoorFragment();
                    break;
                case R.id.button_menu_properties:
                    break;
                case R.id.button_menu_lock:
                    break;
                case R.id.button_menu_user:
                    break;
                case R.id.button_menu_alert:
                    break;
                default: System.out.println("id not handled: "+itemID);
                    break;
            }
        }
    }
}
