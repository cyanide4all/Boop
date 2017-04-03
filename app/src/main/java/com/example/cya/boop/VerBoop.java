package com.example.cya.boop;


import android.app.DialogFragment;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
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

import java.text.SimpleDateFormat;

import com.example.cya.boop.core.Boop;
import com.example.cya.boop.core.Usuario;

import com.example.cya.boop.util.LetterAvatar;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private DatabaseReference mDatabase;
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


        ImageButton avatar = (ImageButton) view.findViewById(R.id.boopavatar);
        avatar.setImageDrawable(new LetterAvatar(view.getContext(), 0,"F",40));
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