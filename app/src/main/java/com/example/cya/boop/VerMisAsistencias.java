package com.example.cya.boop;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.design.internal.BottomNavigationPresenter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.cya.boop.core.Boop;
import com.example.cya.boop.core.Usuario;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class VerMisAsistencias extends AppCompatActivity {

    private ListView lista;
    private ArrayAdapter<Boop> itemsAdapter;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseBoops;
    private Usuario user;
    private Boop boop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_mis_asistencias);
        lista = (ListView) findViewById(R.id.VMAlista);
    }

    private void inicializarDesdeFirebase(){
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_selectable_list_item);
        lista.setAdapter(itemsAdapter);

        mDatabase = FirebaseDatabase.getInstance().getReference("Usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(Usuario.class);
                if(user!=null){
                    inicializarLista(user.getBoopsQueAsisto());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void inicializarLista(ArrayList<String> boopsQueAsisto) {
        for(int i = 0; i < boopsQueAsisto.size(); i++){
            mDatabaseBoops = FirebaseDatabase.getInstance().getReference("BoopInfo").child(boopsQueAsisto.get(i));
            mDatabaseBoops.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boop = dataSnapshot.getValue(Boop.class);
                    itemsAdapter.add(boop);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intento = new Intent(VerMisAsistencias.this, VerBoop.class);
                    intento.putExtra("boopClave", user.getBoopsQueAsisto().get(position));
                    startActivity(intento);
                }
            });

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        inicializarDesdeFirebase();
    }
}
