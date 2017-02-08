package com.example.cya.boop;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.R.attr.password;

public class Login extends AppCompatActivity {
    //TODO esto es la main activity y creo que no debería ser así
    //Esta actividad empieza con el login por email y pass, pero luego puede pasar a otros logins
    // con botones valeoc?

    private static final String TAG = "Login";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Firebase auth aqui para quitarnoslo del medio
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        //Botón de pasar a google login
        //TODO de esta nueva actividad
        Button botonGoogle = (Button) findViewById(R.id.botonGoogle);
        botonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, LoginGoogle.class));
            }
        });

        //Creación de cuenta
        Button botonCrear = (Button) findViewById(R.id.botonCrearCuenta);
        botonCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearCuenta();
            }
        });

        //Login
        Button botonLogin = (Button) findViewById(R.id.botonLogin);
        botonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singIn();
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    //Crea una cuenta de usuario y hace login instantaneamente
    public void crearCuenta(){
        EditText inputEmail = (EditText) findViewById(R.id.inputEmail);
        EditText inputPass = (EditText) findViewById(R.id.inputPass);
        mAuth.createUserWithEmailAndPassword(inputEmail.getText().toString(), inputPass.getText().toString());
        //TODO redirect a actividad principal
        //TODO blindar ante la posibilidad de que el email esté usado
    }
    //Realiza login con una cuenta de usuario ya creada
    public void singIn(){
        EditText inputEmail = (EditText) findViewById(R.id.inputEmail);
        EditText inputPass = (EditText) findViewById(R.id.inputPass);
        mAuth.signInWithEmailAndPassword(inputEmail.getText().toString(), inputPass.getText().toString());
        //TODO redirect a actividad principal
        //TODO avisar de los fallos que se puedan producir aqui
    }
}
