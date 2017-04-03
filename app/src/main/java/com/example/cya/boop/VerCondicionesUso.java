package com.example.cya.boop;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Alex on 13/03/2017.
 */

public class VerCondicionesUso extends DialogFragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view;

        view = inflater.inflate(R.layout.activity_condiciones_uso, container);

        final Button botonSalir = (Button) view.findViewById(R.id.CUSalir);

        botonSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }
}
