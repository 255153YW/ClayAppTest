package com.myclay.claytest.Fragments;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;

import com.myclay.claytest.MainActivity;
import com.myclay.claytest.R;
import com.myclay.claytest.TextValidations.TextValidator;

import java.util.HashMap;

public class LoginFragment extends Fragment {
    private View viewObject = null;


    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.viewObject =  inflater.inflate(R.layout.fragment_login, container, false);
        View btn_login = viewObject.findViewById(R.id.button_login);

        EditText userName = (EditText) viewObject.findViewById(R.id.editText_username);
        EditText password = (EditText) viewObject.findViewById(R.id.editText_password);

        TextValidator val_userName = new TextValidator(userName,"invalid entry", true);
        TextValidator val_password = new TextValidator(password,"invalid entry", true);

        final TextValidator [] validationGroup = new TextValidator[]{val_userName,val_password};
        btn_login.setOnClickListener(new ItemClickListener(R.id.button_login,validationGroup));

        password.setOnKeyListener(new OnKeyListener(validationGroup));
        return viewObject;
    }

    //handles submit on password field
    private class OnKeyListener implements  View.OnKeyListener{
        private TextValidator [] validationGroup;
        public OnKeyListener(TextValidator [] validationGroup){
            this.validationGroup = validationGroup;
        }

        @Override
        public boolean onKey(View view, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                new ItemClickListener(R.id.button_login,validationGroup).onClick(view);
                return true;
            } else {
                return false;
            }
        }
    }

    // Handles clicks on the menu board buttons
    private class ItemClickListener implements View.OnClickListener {
        int itemID;
        TextValidator [] validationGroup;

        public ItemClickListener(int id, TextValidator [] validationGroup) {
            this.itemID = id;
            this.validationGroup = validationGroup;
        }

        public void onClick(View v) {
            if(validate(validationGroup) == true) {
                HashMap<Integer, String> holder = new HashMap<Integer, String>();
                for(int i=0; i<validationGroup.length; i++){
                    holder.put(validationGroup[i].getTextView().getId(), validationGroup[i].getTextView().getText().toString());
                }

                SharedPreferences settings = getActivity().getSharedPreferences("SharedPref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                CheckBox cb = (CheckBox)viewObject.findViewById(R.id.remember_login);
                EditText userName = (EditText) viewObject.findViewById(R.id.editText_username);
                editor.putBoolean("remember", cb.isChecked());
                editor.putString("user",userName.getText().toString());
                editor.putString("authToken", "123");

                ((MainActivity) getActivity()).showHomeFragment();

                // Commit the edits!
                editor.commit();
                hideKeyboard(getView().getContext());
            }
        }
    }

    private boolean validate(TextValidator[] fields){
        Boolean status = true;
        for(int i=0; i<fields.length; i++){
            TextValidator currentField=fields[i];
            if(currentField.isEmpty() == true){
                status = false;
            }
        }
        return status;
    }

    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


}
