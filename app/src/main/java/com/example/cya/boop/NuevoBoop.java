package com.example.cya.boop;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cya.boop.core.Boop;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//Esta actividad pilla datos que le metes + localización cuando le das al boton de boopear
//Por último usa la propia clase boop para mandar a firebase el tema
public class NuevoBoop extends AppCompatActivity {

    //objeto de la clase maestra
    private Boop boop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_boop);

        //Creamos el boop sin info
        boop = new Boop();

        //Evento para el boton
        Button botonBoopear = (Button) findViewById(R.id.botonBoopear);
        botonBoopear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearBoop();
            }
        });


    }

    private void crearBoop() {
        //Primero pillamos las entradas del formulario
        EditText Bnombre = (EditText) findViewById(R.id.Bnombre);
        EditText Bdescripcion = (EditText) findViewById(R.id.Bdescripcion);

        //Luego metemos los datos en el boop
        boop.setNombre(Bnombre.getText().toString());
        boop.setDescripcion(Bdescripcion.getText().toString());
        //TODO pillar las excepciones que pueda soltar el apartado anterior

        //Publicamos el boop
        double latitude = getIntent().getDoubleExtra("latitude",0.0);
        double longitude = getIntent().getDoubleExtra("longitude",0.0);
        boop.crear(longitude,latitude);
        Toast.makeText(this,"BOOPED!",Toast.LENGTH_LONG).show();
        finish();
    }
}
