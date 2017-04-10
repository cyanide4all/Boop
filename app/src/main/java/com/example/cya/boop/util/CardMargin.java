package com.example.cya.boop.util;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by nemboru on 5/04/17.
 */

public class CardMargin extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int dataSize = state.getItemCount()-1;
        int position = parent.getChildLayoutPosition(view);
        if(position == 0 ) {
            outRect.set(300,0,0,0);
        }
        if(position == dataSize){
            outRect.set(0,0,300,0);
        }
    }
}
