package com.example.cya.boop;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import static android.R.attr.password;
import static android.R.attr.state_above_anchor;

public class Login extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener
{
    //  Esta es la main activity. Si firebase detecta que el usuario ya esta dado de alta se le pasa a la siguiente
    //  actividad
    //Esta actividad empieza con el login por email y pass, pero luego puede pasar a otros logins
    // con botones valeoc?

    private static final int REGISTER_REQUEST = 1;

    private static final String TAG = "Login";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private static final String TAGGoogle = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    //Almacena configuracion del cliente de Google
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        //Firebase auth aqui para quitarnoslo del medio
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    //TODO he comentado esto porque no tenemos boton de logut. Cuando lo tengamos, descomentar
                    startActivity(new Intent(Login.this, BoopMap.class));
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        //Botón de pasar a google login
        Button botonGoogle = (Button) findViewById(R.id.botonGoogle);
        botonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInGoogle();
            }
        });

        //Creación de cuenta
        Button botonCrear = (Button) findViewById(R.id.botonCrearCuenta);
        botonCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toRegister = new Intent(Login.this, Register.class);
                startActivityForResult(toRegister, REGISTER_REQUEST);
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

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.credencialesOAuth20))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
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
    public void crearCuenta(String email, String pass){
        //EditText inputEmail = (EditText) findViewById(R.id.inputEmail);
        //EditText inputPass = (EditText) findViewById(R.id.inputPass);
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        //Aqui va lo que pasa si la creación de user no vale
                        if (!task.isSuccessful()) {
                            Toast.makeText(Login.this, "EL email utilizado está en uso o es inválido.",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            //Aqui lo que pasa cuando sale bien
                            Toast.makeText(Login.this, "Cuenta creada correctamente",
                                    Toast.LENGTH_SHORT).show();

                            //Saltamos a la creación de perfil
                            Intent intento = new Intent(Login.this, CrearPerfil.class);
                            intento.putExtra("UserID", mAuth.getCurrentUser().getUid());
                            startActivity(intento);
                        }
                    }
                });
    }
    //Realiza login con una cuenta de usuario ya creada
    public void singIn(){
        final EditText inputEmail = (EditText) findViewById(R.id.inputEmail);
        final EditText inputPass = (EditText) findViewById(R.id.inputPass);
        mAuth.signInWithEmailAndPassword(inputEmail.getText().toString(), inputPass.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        //Aqui va lo que pasa si la creación de user no vale
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail", task.getException());
                            Toast.makeText(Login.this, "Fallo en la autenticación.",
                                    Toast.LENGTH_SHORT).show();
                            inputPass.setText("");

                        }else{
                            //Aqui lo que pasa cuando sale bien
                            Toast.makeText(Login.this, "Prepárate para hacer Boop!.",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this, BoopMap.class));
                        }

                    }
                });
    }

    /***A partir de Aqui Inicio de Google y onActivityResult***/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Inicio de sesion fallido, si no está registrado cree unh cuenta", Toast.LENGTH_SHORT);
            }
        }
        if (requestCode == REGISTER_REQUEST)
        {
            if (resultCode == RESULT_OK) {

                String correo = data.getExtras().getString("correo");
                String pass = data.getExtras().getString("pass");
                crearCuenta(correo, pass);
            }else
            {
                //No hacer nada. Boton cancelar registro viene aqui
            }
        }
    }

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAGGoogle, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]

        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAGGoogle, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAGGoogle, "signInWithCredential", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }else
                        // [START_EXCLUDE]
                        {
                            Toast.makeText(Login.this, "Authentication successful.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void signInGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        //updateUI(null);
                    }
                });
    }

    //Si se desea cancelar el acceso al usuario por algun motivo
    private void revokeAccess() { //Opcional
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        //updateUI(null);
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAGGoogle, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}


