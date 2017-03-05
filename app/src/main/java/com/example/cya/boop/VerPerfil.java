package com.example.cya.boop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.cya.boop.core.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VerPerfil extends AppCompatActivity {

    private Button botonLogout;
    private TextView bio;
    private TextView nombre;
    private TextView edad;
    private DatabaseReference mDatabase;
    private Usuario user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_perfil);

        //Getteamos id del usuario actual
        String idUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Firebasin'
        mDatabase = FirebaseDatabase.getInstance().getReference("Usuarios").child(idUser);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Aqui se meten en la vista las cosas que vienen de la BD
                user = dataSnapshot.getValue(Usuario.class);

                bio = (TextView) findViewById(R.id.VPbio);
                bio.setText(user.getBio());

                nombre = (TextView) findViewById(R.id.VPnombre);
                nombre.setText(user.getNombre());

                edad = (TextView) findViewById(R.id.VPedad);
                //TODO Tenemos fecha de nacimiento, conseguimos edad
                //edad.setText();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("VerPerfil", "onCreateValueEventListener:onCancelled", databaseError.toException());

            }
        });

        botonLogout = (Button) findViewById(R.id.VPlogout);
        botonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });



        TextView nombre = (TextView) findViewById(R.id.VPnombre);

    }

    private void logout(){
        FirebaseAuth.getInstance().signOut();
        finish();
    }
}
