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
    private String uId;
    private String idCreador;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_boop);

        //Recogemos el boop a traves de su bundle

        this.boopClave = getIntent().getStringExtra("boopClave");
        uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        idCreador= "";
        
        final View view = inflater.inflate(R.layout.activity_ver_boop, container, false);
        //Transformamos el boop en cosas visibles
        mDatabase = FirebaseDatabase.getInstance().getReference("BoopInfo").child(boopClave);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
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

                    toggleAssist();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("VerBoop", "onCreateValueEventListener:onCancelled", databaseError.toException());

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

        //likeOdislike(); //Pinta los botones segun haya votado Todo descomentar esto cuando felipeman haga los botones

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
                    toggleAssist();
                    mDatabase.setValue(boop);
                }else{
                    boop.noAsistir(uId);
                    toggleAssist();
                    mDatabase.setValue(boop);
                }
            }
        });*/


        mDatabaseCreador = FirebaseDatabase.getInstance().getReference("Usuarios").child(idCreador); //Posible peligro troll
        mDatabaseCreador.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Aqui se meten en la vista las cosas que vienen de la BD
                Usuario user = dataSnapshot.getValue(Usuario.class);
                creador.setText(user.getNombre());
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