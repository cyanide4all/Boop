package com.example.cya.boop;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.cya.boop.core.Usuario;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Calendar;

public class CrearPerfil extends AppCompatActivity {

    private EditText nombre;
    private EditText bio;
    private Button fechaNac;
    private Button botonCrear;
    private String idUser;
    private Usuario user;
    private ImageButton botonAvatar;
    private int mYear;
    private int mMonth;
    private int mDay;
    private static final int READ_REQUEST_CODE = 324;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_perfil);

        idUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        nombre = (EditText) findViewById(R.id.CPnombre);
        bio = (EditText) findViewById(R.id.CPbio);
        botonCrear = (Button) findViewById(R.id.CPconfirm);
        botonAvatar = (ImageButton) findViewById(R.id.user_image_set);
        botonAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirImagen();
            }
        });

        fechaNac = (Button) findViewById(R.id.CPfechaNac);

        fechaNac.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DatePickerDialog d = new DatePickerDialog(CrearPerfil.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        fechaNac.setText(i+"/"+i1+"/"+i2);
                        mYear = i;
                        mMonth = i1;
                        mDay = i2;
                    }
                }, mYear, mMonth, mDay);
                d.show();
            }
        });

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

        user.setNombre(nombre.getText().toString());
        user.setBio(bio.getText().toString());
        Calendar c = Calendar.getInstance();
        c.set(mYear,mMonth,mDay,0,0);
        user.setFechaNac(c.getTime());
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

    private void pedirImagen(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                UploadTask up = user.uploadPhoto(uri,idUser);
                handleUploadTask(up);
            }
        }
    }

    private void handleUploadTask(UploadTask task){
        //run iconito de pensar
        task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") Uri u = taskSnapshot.getMetadata().getDownloadUrl();
                Picasso.with(CrearPerfil.this).load(u)
                        .memoryPolicy(MemoryPolicy.NO_CACHE )
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                // stop iconito de pensar
                                botonAvatar.setBackground(new BitmapDrawable(getResources(),bitmap));
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
            }
        });
    }

}
