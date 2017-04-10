package com.example.cya.boop;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import com.example.cya.boop.core.Boop;
import com.example.cya.boop.core.Usuario;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class VerBoop extends AppCompatActivity {

    private int margin;
    private String boopClave;
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

    private ImageButton likeBoop;
    private ImageButton dislikeBoop;
    private Button botonSalir;
    private Button botonAsisitir;
    private Button botonChat;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseCreador;
    private DatabaseReference mDatabaseUser;
    private String uId;
    private String idCreador;
    private Usuario user;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_boop);

        this.boopClave = getIntent().getStringExtra("boopClave");
        uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //idCreador= "";

        mDatabase = FirebaseDatabase.getInstance().getReference("BoopInfo").child(boopClave);
        mDatabaseUser = FirebaseDatabase.getInstance().getReference("Usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Aqui se meten en la vista las cosas que vienen de la BD
                boop = dataSnapshot.getValue(Boop.class);
                if(boop != null){
                    nombre = (TextView) findViewById(R.id.VBnombre);
                    nombre.setText(boop.getNombre());

                    descripcion = (TextView) findViewById(R.id.VBdescripcion);
                    descripcion.setText(boop.getDescripcion());
                    creador = (TextView) findViewById(R.id.VBCreador);

                    popularidadUsuario = (TextView) findViewById(R.id.VBPopularidad);
                    popularidadUsuario.setText(String.format(Locale.ENGLISH, " %d" ,boop.getPopularidad()));

                    empieza = (TextView) findViewById(R.id.VBhoraIni);
                    empieza.setText(boop.getFechaFin().toString());                      //ToDo "Controlar formato fecha para ingles"

                    termina = (TextView) findViewById(R.id.VBhoraFin);
                    termina.setText(boop.getFechaFin().toString());

                    tipoEvento = (TextView) findViewById(R.id.VBclasificacion);
                    tipoEvento.setText(boop.getTipo());

                    aforo = (TextView) findViewById(R.id.VBmaxAsistentes);
                    aforo.setText(String.format(Locale.ENGLISH, " %d" ,boop.getMaxBoopers()));

                    asisten = (TextView) findViewById(R.id.VBasistentes);
                    asisten.setText(String.format(Locale.ENGLISH, " %d" ,boop.getBoopers()));

                    idCreador = boop.getidCreador();

                    mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            user = dataSnapshot.getValue(Usuario.class);
                            if(user!=null){
                                toggleAssist();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("VerBoop", "onCreateValueEventListener:onCancelled", databaseError.toException());
                        }
                    });

                    mDatabaseCreador = FirebaseDatabase.getInstance().getReference("Usuarios").child(idCreador); //Posible peligro troll
                    mDatabaseCreador.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Aqui se meten en la vista las cosas que vienen de la BD
                            Usuario userCreador = dataSnapshot.getValue(Usuario.class);
                            creador.setText(userCreador.getNombre());
                            creador.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intento = new Intent(VerBoop.super.getApplicationContext(), VerPerfil.class);
                                    intento.putExtra("userID", boop.getidCreador());
                                    startActivity(intento);
                                }
                            });
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("VerBoop", "onCreateValueEventListener:onCancelled", databaseError.toException());
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("VerBoop", "onCreateValueEventListener:onCancelled", databaseError.toException());

            }
        });

        botonChat = (Button) findViewById(R.id.VBsalaChat);
        botonChat.setFocusable(false); //ToDo para la sig iteraccion. Ver como hacer sala chat

        botonSalir = (Button) findViewById(R.id.VBdejarDeVer);
        botonSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        botonAsisitir = (Button) findViewById(R.id.VBasistir);
        botonAsisitir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!boop.saberSiAsisto(uId)){
                    boop.asistir(uId);
                    user.asistir(boopClave);
                }else{
                    boop.noAsistir(uId);
                    user.noAsistir(boopClave);
                }
                mDatabaseUser.setValue(user);
                mDatabase.setValue(boop);
            }
        });

        dislikeBoop = (ImageButton) findViewById(R.id.noMeGusta);
        dislikeBoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boop.incrementarPopularidad(uId);
                dislikeBoop.setClickable(false);
                likeBoop.setClickable(true);
                //Todo Pintar o no pintar
            }
        });

        likeBoop = (ImageButton) findViewById(R.id.meGusta);
        likeBoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boop.decrementarPopularidad(uId);
                dislikeBoop.setClickable(true);
                likeBoop.setClickable(false);
                //Todo Pintar o no pintar
            }
        });
    }

    @Override
    public void onResume() {
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
        if (boop.quedanPlazas()) {
            botonAsisitir.setClickable(true);
            if (boop.saberSiAsisto(uId)) {
                botonAsisitir.setText("Cancelar asistencia");
            } else {
                botonAsisitir.setText("Asistir");
            }
        } else {
            if (boop.saberSiAsisto(uId)) {
                botonAsisitir.setClickable(true);
                botonAsisitir.setText("Cancelar asistencia");
            } else {
                botonAsisitir.setText("No quedan plazas");
                botonAsisitir.setClickable(false);
            }
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
    */
}