package com.example.cya.boop;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by noboru on 1/03/17.
 */

public class fontChanger {
    public static void change(final Context context, final View v, String font) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    change(context, child, font);
                }
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), font));
            }
        } catch (Exception e) {
            Log.d("error","fonts not working "+e);
        }
    }
}
