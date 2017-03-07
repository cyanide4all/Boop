package com.example.cya.boop.util;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by noboru on 7/03/17.
 */

public class Validator {
    private static final String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    protected boolean state = true;

    public void basicValidation(View v){
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    basicValidation(child);
                }
            } else {
                if (v instanceof EditText) {
                    if ( ( (EditText) v ).getText().toString() == null || (((EditText) v).getText().toString().length() == 0) ) {
                        ((EditText) v).setError("cannot be empty");
                        state = false;
                    }
                }
            }
        } catch (Exception e) {
            Log.d("error","basicValidation not working "+e);
        }
    }

    public void shouldHaveMoreThan(int letters, EditText v){
        if(v.getText().toString().length() < letters){
            v.setError("should have more than "+letters+" letters");
            state = false;
        }
    }

    public void shouldBeEmail(EditText v){
        if(!v.getText().toString().matches(PATTERN_EMAIL)){
            state = false;
        }
    }

    public boolean isAllOk(){
        boolean tmp = state;
        state = true;
        return tmp;
    }
}
