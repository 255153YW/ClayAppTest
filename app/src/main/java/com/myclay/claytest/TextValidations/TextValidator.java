package com.myclay.claytest.TextValidations;

import android.widget.TextView;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Yohandi on 13/10/2015.
 * encapsulate a TextView object to check its value and set error message when needed
 */
public class TextValidator {
    private  TextView textView;
    private  String message;
    private  Boolean isRequired;

    public TextValidator(TextView textView, String message, Boolean required) {
        this.textView = textView;
        this.message = message;
        this.isRequired = required;
    }
    public TextView getTextView(){
        return textView;
    }

    public Boolean isEmpty(){
        if(isRequired == false){
            return false;
        }
        else if(textView.getText().toString().length()<=0){
            textView.setError("this field is required");
            return true;
        }
        else{
            return false;
        }
    }

    public Boolean isValidEntry(){
        String location_pattern = "/^[A-Z0-9_-]{3,32}$/";
        Boolean result = null;

        Pattern pattern = Pattern.compile(location_pattern);
        try {
            Matcher matcher = pattern.matcher(textView.getText());
            result = matcher.matches();

            if(result == false){
                textView.setError(message);
            }
        }
        catch (NullPointerException e) {
            result = false;
            e.printStackTrace();
            System.out.println(e.getMessage() + " textValidator");
        }

        return result;
    }

}
