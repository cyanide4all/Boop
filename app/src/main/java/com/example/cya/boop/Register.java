package com.example.cya.boop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    private static final String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private EditText correo;
    private EditText pass;
    private EditText pass2;
    private Button aceptar;
    private Button cancelar;
    private TextView condiciones;

    private TextView t, tt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_register);

        fontChanger.change(this,findViewById(R.id.activity_register),"fonts/Neris-Thin.otf");

        correo = (EditText) findViewById(R.id.mail);
        pass = (EditText) findViewById(R.id.pass);
        pass2 = (EditText) findViewById(R.id.pass2);

        aceptar = (Button) findViewById(R.id.accept);
        cancelar = (Button) findViewById(R.id.cancel);

        condiciones = (TextView) findViewById(R.id.useConditions);

        correo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!v.hasFocus())
                    if (!comprobarCorreo(correo.getText().toString())) {
                        /*Toast.makeText(Register.this, R.string.CorreoNoValido, Toast.LENGTH_SHORT).show();
                        pass.requestFocus();*/
                        correo.setError("Este correo no parece válido");
                    }
            }
        });

        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(pass.getText().length()==0)
                    pass.setError(getResources().getString(R.string.ContrasenaVacia));
                else {
                    if(pass.getText().length()<6)
                        pass.setError(getResources().getString(R.string.ContrasenaCorta));
                    else {
                        if(!comprobarContrasenas())
                            pass2.setError(getResources().getString(R.string.ContrasenaFallida));
                        else
                            pass2.setError(null);
                    }
                }
            }
        });

        pass2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!comprobarContrasenas())
                    pass2.setError(getResources().getString(R.string.ContrasenaFallida));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((comprobarCorreo(correo.getText().toString())) && (comprobarContrasenas())) {
                    //String n = nombre.getText().toString();
                    String c = correo.getText().toString();
                    String p = pass.getText().toString();

                    Intent i = new Intent();

                    //i.putExtra("nombre", n);
                    i.putExtra("correo", c);
                    i.putExtra("pass", p);

                    setResult(RESULT_OK, i);

                    finish();
                }else {
                    Toast.makeText(getApplicationContext(), R.string.ComprobarDatos, Toast.LENGTH_LONG).show();
                }

            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setResult(RESULT_CANCELED);

                finish();
            }
        });

        condiciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerCondicionesUso condiciones = new VerCondicionesUso();
                condiciones.show(getFragmentManager(), "");
            }
        });
    }

    public boolean comprobarCorreo(String c)
    {
        // Compiles the given regular expression into a pattern.
        Pattern pattern = Pattern.compile(PATTERN_EMAIL);

        // Match the given input against this pattern
        Matcher matcher = pattern.matcher(c);
        return matcher.matches();
    }

    public boolean comprobarContrasenas()
    {
        if (pass.getText().length()<6)
            return false;
        else
        {
            if(pass2.getText().toString().equals(pass.getText().toString()))
                return true;
            else
                return false;
        }
    }
}
