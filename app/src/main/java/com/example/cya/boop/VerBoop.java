package com.example.cya.boop;


import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cya.boop.core.Boop;

public class VerBoop extends DialogFragment {

    private int margin;
    private Boop boop;
    private TextView nombre;
    private TextView descripcion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        margin = 10;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Recogemos el boop a traves de su bundle
        Bundle bundle = getArguments();
        this.boop = (Boop) bundle.getSerializable("boop");


        View view = inflater.inflate(R.layout.activity_ver_boop, container, false);
        //Transformamos el boop en cosas visibles
        nombre = (TextView) view.findViewById(R.id.VBnombre);
        nombre.setText(boop.getNombre());
        descripcion = (TextView) view.findViewById(R.id.VBdescripcion);
        descripcion.setText(boop.getDescripcion());
        return view;
    }

    @Override
    public void onResume() {
        int dialogHeight = BoopMap.displayMetrics.heightPixels - (margin * 2);
        int dialogWidth = BoopMap.displayMetrics.widthPixels - (margin * 2);
        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
        super.onResume();
    }

}