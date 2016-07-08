package com.ianmyrfield.things;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Ian on 6/9/2016.
 */
public class Utility {

    private static final String TAG = "Utility";
    /**
     * Chooses color for notes
     * @param color
     * @return color
     */
    public int choseColor(int color){
        int mColor;

        switch (color){
            case 1:
                mColor = Color.BLUE;
                return mColor;
            default:
                return Color.GREEN;
        }
    }

    /**
     * Hides Keyboard
     * @param context
     * @param view
     */
    public static void hideKeyboardFrom ( Context context, View view ) {

        Log.d( TAG, "hideKeyboardFrom: " + view);
        InputMethodManager imm = (InputMethodManager) context.getSystemService( Activity.INPUT_METHOD_SERVICE );
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }



}
