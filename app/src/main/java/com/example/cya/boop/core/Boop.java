package com.example.cya.boop.core;

import android.location.Location;
import android.support.annotation.NonNull;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by cya on 2/9/17.
 */
//La clase Boop representa un Boop de hacer Boop. Guarda los datos del Boop
//Mantengamosla ordenada y mantenible.
public class Boop implements Serializable{
    //Valor por el que se multiplica el Karma
    public static final int KARMA_VALUE = 10;
    //Nombre del boop, a mostrar en el mapa. Algo descriptivo como "Quedada puchamongo equipo rojo"
    private String nombre;
    //Descripción del boop, a mostrar al seleccionar para ver los detalles del boop
    private String descripcion;
    //ID del usuario creador del boop
    private String idCreador;
    //Enlazar a foto de usuario o de Boop. //ToDo foto de usuario o sacar una al instante para mostrar Boop
    private String foto;
    //Numero maximo de admitidos, 0 para sin limite
    private int maxBoopers;
    //Numero de personas actualmente admitidas, nuca superará la cifra anterior
    private int boopers;
    //Fecha y hora de inicio del evento
    private Date fechaIni;
    //Fecha y hora de finalización
    private Date fechaFin;
    // si es un concierto, bar, museo, performance etc..
    private enum  clasificacion {General, Bar, Exposición, Concierto, Comida, Estudiar};
    //Lista de asistentes
    ArrayList<String> asistentes;
    //Mapa de likes y dislikes
    HashMap<String, Integer> votaron;

    private clasificacion tipo;
    //CON ESTO BASTARA POR AHORA, pero aun asi...

    //TODO foto, edad max/min, amigos/publico etc... Si se os ocurre más, aquí hay que ponerlo


    //Constructor vacío, montamos el boop a base de setters
    public Boop(){
        this.nombre = "";
        this.descripcion = "";
        this.idCreador = "";
        this.foto = "";
        this.tipo = clasificacion.General;
        this.maxBoopers = 0; //TODO
        this.boopers = 0;
        this.fechaIni = Calendar.getInstance().getTime(); //TODO
        this.fechaFin = Calendar.getInstance().getTime(); //TODO
        this.asistentes = new ArrayList<>();
        this.votaron = new HashMap<>();
    }


    //
    //GETTERS & SETTERS (Algunos retornan excepciones al intentar settear imposibles
    //
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripción) {
        this.descripcion = descripción;
    }

    public int getMaxBoopers() {
        return maxBoopers;
    }

    public void setMaxBoopers(int maxBoopers) {
        this.maxBoopers = maxBoopers;
    }

    public int getBoopers() {
        return boopers;
    }

    public String getTipo() {return tipo.name();}

    public void setBoopers(int boopers) throws Exception {
        if(maxBoopers == 0 || (this.boopers + boopers) <= maxBoopers) {
            this.boopers = boopers;
        }else{
            throw new Exception("Excedido el número de participantes");
        }
    }

    public Date getFechaIni() {
        return fechaIni;
    }

    public void setFechaIni(Date fechaIni) {
        this.fechaIni = fechaIni;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) throws Exception {
        if(fechaFin.after(this.fechaIni)) {
            this.fechaFin = fechaFin;
        }else{
            throw new Exception("La fecha fin no puede ser anterior a la de inicio");
        }
    }

    public ArrayList<String> getAsistentes(){return asistentes;}

    public boolean asistir (String idUsuario) {
        if (asistentes.size() < maxBoopers)
        {
            asistentes.add(idUsuario);
            return true;
        }
        else
        {
            return false;
        }
    }

    public void noAsistir (String idUsuario) {asistentes.remove(idUsuario);}

    public boolean saberSiAsisto (String idUsuario) {return asistentes.contains(idUsuario);}

    //RELACIONADOS CON EL USUARIO QUE CREA ESTE BOOP
    public String getidCreador() {return idCreador;}

    public void setidCreador(String n) {this.idCreador = n;}

    public int getPopularidad() {
        Collection<Integer> valores = votaron.values();
        Iterator<Integer> it = valores.iterator();
        int toret = 0;

        while (it.hasNext())
        {
            toret += it.next();
        }

        return toret * KARMA_VALUE; // Todo mirar de banear si muy baja puntuacion
    }

    public void incrementarPopularidad(String who) //1 like, -1 no like
    {
        this.votaron.put(who, 1);

    }

    public void decrementarPopularidad(String who)
    {
        //if (this.popularidad < 0 ) this.popularidad = 0;  //Todo mirar si controlar popularidad de Boop
        this.votaron.put(who, -1);
    }

    public int getMiVoto (String who)
    {
        return votaron.get(who); //Devuelve null si no voto, 1 si like, -1 si dislike
    }

    public String getFoto(){return foto;}

    public void setFoto(String foto) {
        this.foto = foto;
    }

    //FUNCIONES Y METODOS IMPORTANTES
    public void crear(double longitude, double latitude) {
        //Firebaseamientos para funcar
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("BoopInfo");

        DatabaseReference newPostRef = myRef.push();
        newPostRef.setValue(this);

        // /Location aqui
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("locations");
        GeoFire geofire = new GeoFire(ref);
        geofire.setLocation(newPostRef.getKey(), new GeoLocation(latitude, longitude));

        //TODO
    }

}
