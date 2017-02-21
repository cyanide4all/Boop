package com.example.cya.boop;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.cya.boop.core.Boop;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_boop);
        
        final Button dia = (Button) findViewById(R.id.bdia);
        final Button hora = (Button) findViewById(R.id.bhora);
        final Button fdia = (Button) findViewById(R.id.bfdia);
        final Button fhora = (Button) findViewById(R.id.bfhora);

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
        

    }

    private void crearBoop() {
        //Primero pillamos las entradas del formulario
        EditText Bnombre = (EditText) findViewById(R.id.Bnombre);
        EditText Bdescripcion = (EditText) findViewById(R.id.bdescripcion);

        //Luego metemos los datos en el boop
        boop.setNombre(Bnombre.getText().toString());
        boop.setDescripcion(Bdescripcion.getText().toString());
        //TODO pillar las excepciones que pueda soltar el apartado anterior

        if(setDateAndTime(boop)){
            //Publicamos el boop
            double latitude = getIntent().getDoubleExtra("latitude",0.0);
            double longitude = getIntent().getDoubleExtra("longitude",0.0);
            boop.crear(longitude,latitude);
            Toast.makeText(this,"BOOPED!",Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private boolean setDateAndTime(Boop b){
        Calendar c = Calendar.getInstance();
        c.set(mYear,mMonth,mDay,mDay,mMinute);
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
}
