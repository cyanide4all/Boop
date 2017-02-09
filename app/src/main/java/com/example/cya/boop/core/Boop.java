package com.example.cya.boop.core;

import java.util.Date;

/**
 * Created by cya on 2/9/17.
 */
//La clase Boop representa un Boop de hacer Boop. Guarda los datos del Boop
//Mantengamosla ordenada y mantenible.
public class Boop {
    //Nombre del boop, a mostrar en el mapa. Algo descriptivo como "Quedada puchamongo equipo rojo"
    private String nombre;
    //Descripción del boop, a mostrar al seleccionar para ver los detalles del boop
    private String descripción;
    //Localización. Será para usar con la api hermosa de geo-firebase
    private String localización;
    //Numero maximo de admitidos, 0 para sin limite
    private int maxBoopers;
    //Numero de personas actualmente admitidas, nuca superará la cifra anterior
    private int boopers;
    //Fecha de inicio del evento
    private Date fechaIni;
    //Fecha de finalización
    private Date fechaFin;
    //TODO horas y lo que surja

    //GETTERS & SETTERS
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripción() {
        return descripción;
    }

    public void setDescripción(String descripción) {
        this.descripción = descripción;
    }

    public String getLocalización() {
        return localización;
    }

    public void setLocalización(String localización) {
        this.localización = localización;
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

//FUNCIONES Y METODOS IMPORTANTES
}
