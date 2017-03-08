package com.example.cya.boop;


import android.app.DialogFragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;

import com.example.cya.boop.core.Boop;
import com.example.cya.boop.core.Usuario;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class VerBoop extends DialogFragment {

    private int margin;
    private Boop boop;
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

    private ImageButton likeBoop;
    private ImageButton dislikeBoop;
    private Button botonSalir;
    private ToggleButton botonAsisitir;
    private Button botonChat;

    private String uId;

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
        uId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy 'a las' HH:mm"); //Formato de fecha

        final View view = inflater.inflate(R.layout.activity_ver_boop, container, false);
        //Transformamos el boop en cosas visibles
        nombre = (TextView) view.findViewById(R.id.VBnombre);
        nombre.setText(boop.getNombre());

        descripcion = (TextView) view.findViewById(R.id.VBdescripcion);
        descripcion.setText(boop.getDescripcion());

        /*
        creador = (TextView) view.findViewById(R.id.VBCreador);
        creador.setText(boop.getNombreCreador());
        */

        popularidadUsuario = (TextView) view.findViewById(R.id.VBPopularidad);
        popularidadUsuario.setText(String.format(Locale.ENGLISH, " %d" ,boop.getPopularidad()));

        empieza = (TextView) view.findViewById(R.id.VBhoraIni);
        empieza.setText(boop.getFechaIni().toString());

        termina = (TextView) view.findViewById(R.id.VBhoraFin);
        termina.setText(dateFormat.format(boop.getFechaFin()));

        tipoEvento = (TextView) view.findViewById(R.id.VBclasificacion);
        tipoEvento.setText(boop.getTipo());

        aforo = (TextView) view.findViewById(R.id.VBmaxAsistentes);
        aforo.setText(String.format(Locale.ENGLISH, " %d" ,boop.getMaxBoopers()));

        asisten = (TextView) view.findViewById(R.id.VBasistentes);
        asisten.setText(String.format(Locale.ENGLISH, " %d" ,boop.getBoopers()));


        dislikeBoop = (ImageButton) view.findViewById(R.id.noMeGusta);
        dislikeBoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boop.incrementarPopularidad(uId);
                dislikeBoop.setClickable(false);
                likeBoop.setClickable(true);
                //Todo Pintar o no pintar
            }
        });

        likeBoop = (ImageButton) view.findViewById(R.id.meGusta);
        likeBoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boop.decrementarPopularidad(uId);
                dislikeBoop.setClickable(true);
                likeBoop.setClickable(false);
                //Todo Pintar o no pintar
            }
        });

        //likeOdislike(); //Pinta los botones segun haya votado Todo descomentar esto cuando felipeman haga los botones

        botonChat = (Button) view.findViewById(R.id.VBsalaChat);
        botonChat.setFocusable(false); //ToDo para la sig iteraccion. Ver como hacer sala chat

        botonSalir = (Button) view.findViewById(R.id.VBdejarDeVer);
        botonSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();  //Mirar si esto funca correctamente
            }
        });

        botonAsisitir = (ToggleButton) view.findViewById(R.id.VBasistir);
        toggleAssist();
        botonAsisitir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!botonAsisitir.isChecked()) {
                    if(!boop.asistir(uId))  //Devuelve false si se ha alcanzado el máximo de boopers
                    {
                        Toast.makeText(view.getContext(), R.string.alcanzadoMaximo, Toast.LENGTH_SHORT ).show();
                    }
                    else
                        botonAsisitir.setChecked(true);
                }else
                {
                    boop.noAsistir(uId);
                    botonAsisitir.setChecked(false);
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        int dialogHeight = BoopMap.displayMetrics.heightPixels - (margin * 2);
        int dialogWidth = BoopMap.displayMetrics.widthPixels - (margin * 2);
        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
        super.onResume();
    }

    //Funcion de puntuacion de votar:
    public void puntuarBoopPositivamente()
    {
        boop.incrementarPopularidad(uId);
    }

    public void puntuarBoopNegativamente()
    {
        boop.decrementarPopularidad(uId);
    }

    public void toggleAssist() {

        if (boop.saberSiAsisto(uId))
            {
            botonAsisitir.setChecked(true);
        }
        else {
            botonAsisitir.setChecked(false);
        }
    }

    //Todo descomentar esto cuando felipe ponga las imagenes
    /*public void likeOdislike ()
    {
        if(boop.getMiVoto(uId) == 1) {
            likeBoop.setImageDrawable();        //Pinto like
            dislikeBoop.setImageDrawable();
        }
        else{
            likeBoop.setImageDrawable();
            dislikeBoop.setImageDrawable();     //Pinto dislike
        }
    }
    }*/

}