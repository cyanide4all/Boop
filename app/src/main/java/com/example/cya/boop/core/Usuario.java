package com.example.cya.boop.core;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

/**
 * Created by cya on 2/22/17.
 */

public class Usuario implements Serializable {
    //Id para ver tu perfil y esas cosas
    private String idUsuario;
    //Nombre completo
    private String nombre;
    //Pequeña presentación personal de rellenar perfil
    private String bio;
    //Fecha de nacimiento en principio para restringción de edad. Puede que luego se permita
    // ocultarlo para por si mujer subnormal
    private String fechaNac;

    //Constructor vacío por tocarle los huevos a oskaru
    public Usuario (){}

    //
    //GETTERS & SETTERS (Algunos retornan excepciones al intentar settear imposibles
    //
    public String getBio() {
        return bio;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public String getFechaNac() {
        return fechaNac;
    }

    public String getNombre() {
        return nombre;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setFechaNac(String fechaNac) {
        this.fechaNac = fechaNac;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void crear() {
        //Firebaseamientos para funcar
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Usuarios");

        DatabaseReference newPostRef = myRef.push();
        newPostRef.setValue(this);
    }
}