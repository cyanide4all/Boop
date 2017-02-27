package com.example.cya.boop;


import android.app.DialogFragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.cya.boop.core.Boop;
import com.example.cya.boop.core.Usuario;

import java.util.Locale;

public class VerBoop extends DialogFragment {

    private int margin;
    private Boop boop;
    private Usuario usuario;
    private TextView nombre;
    private TextView descripcion;
    private TextView creador;
    private TextView popularidadUsuario;
    private TextView empieza;
    private TextView termina;
    private TextView tipoEvento;
    private TextView aforo;
    private TextView asisten;
    private RatingBar estrellas;

    private Button botonSalir;
    private ToggleButton botonAsisitir;
    private Button botonChat;

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
        this.usuario = (Usuario) bundle.getSerializable("usuario"); //Todo Martin revisa esto

        final View view = inflater.inflate(R.layout.activity_ver_boop, container, false);
        //Transformamos el boop en cosas visibles
        nombre = (TextView) view.findViewById(R.id.VBnombre);
        nombre.setText(boop.getNombre());

        descripcion = (TextView) view.findViewById(R.id.VBdescripcion);
        descripcion.setText(boop.getDescripcion());

        creador = (TextView) view.findViewById(R.id.VBCreador);
        creador.setText(boop.getNombreCreador());

        popularidadUsuario = (TextView) view.findViewById(R.id.VBPopularidad);
        popularidadUsuario.setText(String.format(Locale.ENGLISH, " %d" ,boop.getPopularidad()));

        empieza = (TextView) view.findViewById(R.id.VBhoraIni);
        termina.setText(boop.getFechaIni().toString());

        termina = (TextView) view.findViewById(R.id.VBhoraFin);
        termina.setText(boop.getFechaFin().toString());

        tipoEvento = (TextView) view.findViewById(R.id.VBclasificacion);
        tipoEvento.setText(boop.getTipo());

        aforo = (TextView) view.findViewById(R.id.VBmaxAsistentes);
        aforo.setText(String.format(Locale.ENGLISH, " %d" ,boop.getMaxBoopers()));

        asisten = (TextView) view.findViewById(R.id.VBasistentes);
        asisten.setText(String.format(Locale.ENGLISH, " %d" ,boop.getBoopers()));


        estrellas = (RatingBar) view.findViewById(R.id.VBratingBar);
        estrellas.setNumStars(5);
        estrellas.setRating((float)2.5);

        botonChat = (Button) view.findViewById(R.id.VBsalaChat);
        botonChat.setFocusable(false); //ToDo para la sig iteraccion. Ver como hacer sala chat

        botonSalir = (Button) view.findViewById(R.id.VBdejarDeVer);
        botonSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                puntuarBoop();
                getActivity().onBackPressed();  //Mirar si esto funca correctamente
            }
        });

        botonAsisitir = (ToggleButton) view.findViewById(R.id.VBasistir);
        saberSiAsisto();

        //Todo Alex se qedo haciendo listener de este boton

        return view;
    }

    @Override
    public void onResume() {
        int dialogHeight = BoopMap.displayMetrics.heightPixels - (margin * 2);
        int dialogWidth = BoopMap.displayMetrics.widthPixels - (margin * 2);
        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
        super.onResume();
    }

    //@Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            puntuarBoop();

            return true;
        }

        return getActivity().onKeyDown(keyCode, event);
    }

    //Funcion de puntuacion de estrellas:
    public void puntuarBoop() {
        int rating = (int) this.estrellas.getRating();

        switch (rating) {
            case (1):
                boop.decrementarPopularidad(20);
                break;
            case (2):
                boop.decrementarPopularidad(10);
                break;
            case (4):
                boop.incrementarPopularidad(10);
                break;
            case (5):
                boop.incrementarPopularidad(20);
                break;
        }
    }

    public void saberSiAsisto() {

        if (boop.saberSiAsisto(this.usuario.getIdUsuario()))
        {
            botonAsisitir.setChecked(true);
        }
        else {
            botonAsisitir.setChecked(false);
        }
    }

}