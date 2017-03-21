package com.example.cya.boop;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.cya.boop.core.Boop;
import com.example.cya.boop.util.Validator;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Calendar;
import java.util.Date;

//Esta actividad pilla datos que le metes + localización cuando le das al boton de boopear
//Por último usa la propia clase boop para mandar a firebase el tema
public class NuevoBoop extends AppCompatActivity {

    //objeto de la clase maestra
    private Boop boop;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour, mMinute;

    private int fmYear;
    private int fmMonth;
    private int fmDay;
    private int fmHour, fmMinute;
    private static final int READ_REQUEST_CODE = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_nuevo_boop);

        fontChanger.change(this,findViewById(R.id.activity_nuevo_boop),"fonts/Neris-Thin.otf");

        
        final Button dia = (Button) findViewById(R.id.bdia);
        final Button hora = (Button) findViewById(R.id.bhora);
        final Button fdia = (Button) findViewById(R.id.bfdia);
        final Button fhora = (Button) findViewById(R.id.bfhora);

        fontChanger.change(this,findViewById(R.id.Bnombre),"fonts/Neris-Bold.otf");

        fontChanger.change(this,findViewById(R.id.botonBoopear),"fonts/Neris-SemiBold.otf");
        fontChanger.change(this,findViewById(R.id.bcancelar),"fonts/Neris-SemiBold.otf");

        Button cancel = (Button) findViewById(R.id.bcancelar);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR);
        mMinute = c.get(Calendar.MINUTE);

        fmYear = mYear;
        fmDay = mDay;
        fmHour = mHour;
        fmMinute = mMinute;
        fmMonth = mMonth;

        dia.setText(mYear+"/"+mMonth+"/"+mDay);
        hora.setText(mHour+":"+mMinute);
        fdia.setText(mYear+"/"+mMonth+"/"+mDay);
        fhora.setText(mHour+":"+mMinute);

        // TODO refactor

        dia.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DatePickerDialog d = new DatePickerDialog(NuevoBoop.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        dia.setText(i+"/"+i1+"/"+i2);
                        mYear = i;
                        mMonth = i1;
                        mDay = i2;
                    }
                }, mYear, mMonth, mDay);
                d.show();
            }
        });

        fhora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog t = new TimePickerDialog(NuevoBoop.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        fhora.setText(i+":"+i1);
                        fmHour = i;
                        fmMinute = i1;
                    }
                },fmHour,fmMinute,false);
                t.show();
            }
        });

        fdia.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DatePickerDialog d = new DatePickerDialog(NuevoBoop.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        fdia.setText(i+"/"+i1+"/"+i2);
                        fmYear = i;
                        fmMonth = i1;
                        fmDay = i2;
                    }
                }, fmYear, fmMonth, fmDay);
                d.show();
            }
        });

        hora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog t = new TimePickerDialog(NuevoBoop.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        hora.setText(i+":"+i1);
                        mHour = i;
                        mMinute = i1;
                    }
                },mHour,mMinute,false);
                t.show();
            }
        });
        
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

        ImageButton nueva_imagen = (ImageButton) findViewById(R.id.nuevo_boop_imagen);
        nueva_imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirImagen();
            }
        });
        

    }

    private void crearBoop() {
        Validator v = new Validator();
        v.basicValidation(findViewById(R.id.activity_nuevo_boop));
        if(v.isAllOk()){
            //Primero pillamos las entradas del formulario
            EditText Bnombre = (EditText) findViewById(R.id.Bnombre);
            EditText Bdescripcion = (EditText) findViewById(R.id.bdescripcion);

            //Luego metemos los datos en el boop
            boop.setNombre(Bnombre.getText().toString());
            boop.setDescripcion(Bdescripcion.getText().toString());

            //Metemos la id del creador en el boop
            String idUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
            boop.setidCreador(idUser);

            if(setDateAndTime(boop)){
                //Publicamos el boop
                double latitude = getIntent().getDoubleExtra("latitude",0.0);
                double longitude = getIntent().getDoubleExtra("longitude",0.0);
                boop.crear(longitude,latitude);
                Toast.makeText(this,"BOOPED!",Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private boolean setDateAndTime(Boop b){
        Calendar c = Calendar.getInstance();
        c.set(mYear,mMonth,mDay,mHour,mMinute);
        boop.setFechaIni(c.getTime());

        c.set(fmYear,fmMonth,fmDay,fmDay,fmMinute);
        try {
            boop.setFechaFin(c.getTime());
        } catch (Exception e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Problemilla con las fechas")
                    .setMessage("La fecha de final no puede ser antes que la de principio")
                    .setPositiveButton("Aceptar",null).show();
            return false;

        }

        return true;
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
                UploadTask up = boop.uploadPhoto(uri);
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
                Picasso.with(NuevoBoop.this).load(u)
                        .memoryPolicy(MemoryPolicy.NO_CACHE )
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        // stop iconito de pensar
                        findViewById(R.id.nuevo_boop_imagen_fondo).setBackground(new BitmapDrawable(bitmap));
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
