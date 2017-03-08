package com.example.cya.boop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.cya.boop.core.Usuario;
import com.google.firebase.auth.FirebaseAuth;

public class CrearPerfil extends AppCompatActivity {

    private EditText nombre;
    private EditText bio;
    private EditText fechaNac;
    private Button botonCrear;
    private Usuario user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_perfil);

        nombre = (EditText) findViewById(R.id.CPnombre);
        bio = (EditText) findViewById(R.id.CPbio);
        //TODO que fecha no sea un editText y que sea un calendario
        fechaNac = (EditText) findViewById(R.id.CPfechaNac);
        botonCrear = (Button) findViewById(R.id.CPconfirm);

        user = new Usuario();

        botonCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearPerfil();
            }
        });
    }

    //Mete datos al usuario user creado arriba y luego ejecuta su crear. Luego va a boopmap
    public void crearPerfil(){
        String idUser = getIntent().getExtras().getString("UserID");
        user.setNombre(nombre.getText().toString());
        user.setBio(bio.getText().toString());
        user.setFechaNac(fechaNac.getText().toString());

        user.crear(idUser);
        startActivity(new Intent(this, BoopMap.class));
    }

    @Override
    public void onResume(){
        super.onResume();
        //TODO usar aqui un AuthStateListener en vez de esta solucion cutre
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            finish();
        }

    }
}
