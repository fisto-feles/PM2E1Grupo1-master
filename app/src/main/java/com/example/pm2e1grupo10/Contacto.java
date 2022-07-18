package com.example.pm2e1grupo10;

public class Contacto {
    private String id_contacto;
    private String nombre;
    private String telefono;
    private String latitud;
    private String longitud;
    private String foto;

    public Contacto() { }

    public Contacto(String nombre, String telefono, String latitud, String longitud, String foto) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.latitud = latitud;
        this.longitud = longitud;
        this.foto = foto;

    }

    public Contacto(String id_contacto, String nombre, String telefono, String latitud, String longitud, String foto) {
        this.id_contacto = id_contacto;
        this.nombre = nombre;
        this.telefono = telefono;
        this.latitud = latitud;
        this.longitud = longitud;
        this.foto = foto;

    }

    public String getId() {
        return id_contacto;
    }

    public void setId(String id_contacto) {
        this.id_contacto = id_contacto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
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

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    }

