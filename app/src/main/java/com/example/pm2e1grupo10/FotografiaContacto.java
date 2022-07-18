package com.example.pm2e1grupo10;

import android.graphics.Bitmap;

public class FotografiaContacto {
    String id_contacto;
    Bitmap foto;
    String nombre;
    String latitud;
    String longitud;

    public FotografiaContacto(Bitmap foto, String nombre)
    {
        this.foto= foto;
        this.nombre = nombre;
        this.latitud = "";
        this.longitud = "";
        this.id_contacto = "";
    }

    public String getId() {
        return id_contacto;
    }

    public void setId(String id_contacto) {
        this.id_contacto = id_contacto;
    }

    public void setFoto(Bitmap foto) {
        this.foto = foto;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Bitmap getFoto()
    {
        return foto;
    }



    public String getNombre()
    {
        return nombre;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }
}