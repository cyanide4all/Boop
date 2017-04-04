package com.example.cya.boop;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cya.boop.core.Boop;

import java.util.List;

/**
 * Created by nemboru on 4/04/17.
 */
public class BoopCardsManager extends RecyclerView.Adapter<BoopCardsManager.ImageHolder> {

    List<String> f;

    public BoopCardsManager(List<String> list) {

        f = list;

    }

    @Override
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.boopcard, parent, false);
        return new ImageHolder(v);
    }

    @Override
    public void onBindViewHolder(final ImageHolder holder, final int position) {
        holder.text_info.setText(f.get(position));
    }

    @Override
    public int getItemCount() {
        return f.size();
    }

    public static class ImageHolder extends RecyclerView.ViewHolder {


        TextView text_info;

        public ImageHolder(View itemView) {
            super(itemView);
            text_info = (TextView) itemView.findViewById(R.id.info_text);
        }

    }
}