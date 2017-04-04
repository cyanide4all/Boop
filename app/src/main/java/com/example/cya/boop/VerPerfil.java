package com.example.cya.boop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cya.boop.core.Usuario;
import com.example.cya.boop.util.LetterAvatar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class VerPerfil extends AppCompatActivity {

    private Button botonLogout;
    private TextView bio;
    private TextView nombre;
    private TextView edad;
    private DatabaseReference mDatabase;
    private Usuario user;
    private String idUser;
    private boolean esMiPerfil;
    private Button botonEditarPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ver_perfil);
        getSupportActionBar().hide();

        //Si le viene un userID en los extras del intent, muestra los datos de ese senior, si no los del propio usuario actual
        if(getIntent().getExtras()==null) {
            //Hemos entrado desde nuestro propio boton de ver perfil
            //Getteamos id del usuario actual
            idUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
            //Obviamente estamos en nuestro perfil
            esMiPerfil = true;
        }else{
            //Hemos entrado desde un boop u otro sitio ajeno...
            idUser = getIntent().getExtras().getString("userID");
            //Debemos chekear si estamos en nuestro propio perfil
            if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(idUser)) {
                esMiPerfil = true;
            }else{
                esMiPerfil = false;
            }
        }

        //Firebasin'
        mDatabase = FirebaseDatabase.getInstance().getReference("Usuarios").child(idUser);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Aqui se meten en la vista las cosas que vienen de la BD
                user = dataSnapshot.getValue(Usuario.class);
                if(user != null){
                    bio = (TextView) findViewById(R.id.VPbio);
                    bio.setText(user.getBio());

                    nombre = (TextView) findViewById(R.id.VPnombre);
                    nombre.setText(user.getNombre());

                    ImageView avatar = (ImageView) findViewById(R.id.VPavatar);
                    avatar.setImageDrawable(new LetterAvatar(VerPerfil.this, 0,"S",90));

                    edad = (TextView) findViewById(R.id.VPedad);
                    //Si eso, usar cosas no deprecated
                    edad.setText(getAge(user.getFechaNac().getYear()+1900, user.getFechaNac().getMonth(),user.getFechaNac().getDay()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("VerPerfil", "onCreateValueEventListener:onCancelled", databaseError.toException());
            }
        });

        ImageButton botonSalir = (ImageButton) findViewById(R.id.VPsalir);
        botonSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        /*
        botonLogout = (Button) findViewById(R.id.VPlogout);
        botonEditarPerfil = (Button) findViewById(R.id.VPmodificarPerfil);


        if(esMiPerfil) {
            botonLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logout();
                }
            });
            botonEditarPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intento = new Intent(VerPerfil.this, CrearPerfil.class);
                    intento.putExtra("Editando", true);
                    startActivity(intento);
                }
            });
        }else{
            botonLogout.setVisibility(View.INVISIBLE);
            botonEditarPerfil.setVisibility(View.INVISIBLE);
        }
        */
        TextView nombre = (TextView) findViewById(R.id.VPnombre);


    }

    private void logout(){
        FirebaseAuth.getInstance().signOut();
        finish();
    }

    //Devuelve edad como string a partir de nacimiento. (src StackOverflow)
    private String getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }

}
