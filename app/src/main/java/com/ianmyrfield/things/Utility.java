package com.ianmyrfield.things;

import android.graphics.Color;

/**
 * Created by Ian on 6/9/2016.
 */
public class Utility {

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

}
