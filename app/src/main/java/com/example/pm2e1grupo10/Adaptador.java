package com.example.pm2e1grupo10;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class Adaptador extends ArrayAdapter<FotografiaContacto>{

    ArrayList<FotografiaContacto> listado = new ArrayList<>();
    ArrayList<FotografiaContacto> copyContactos = new ArrayList<>();

    public Adaptador(Context context, int textViewResourceId, ArrayList<FotografiaContacto> objects) {
        super(context, textViewResourceId, objects);
        listado = objects;
        copyContactos.addAll(listado);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.activity_imagen, null);
        ImageView imageView = (ImageView) v.findViewById(R.id.img);
        imageView.setImageBitmap(listado.get(position).getFoto());
        TextView textView = (TextView) v.findViewById(R.id.txt);
        textView.setText(listado.get(position).getNombre());
        return v;
    }

    public void filtrar(String texto) {
        listado.clear();

        if (texto.length() == 0) {
            listado.addAll(copyContactos);
        } else {
            for (FotografiaContacto contacto : copyContactos) {
                if (contacto.getNombre().toLowerCase().contains(texto.toLowerCase())) {
                    listado.add(contacto);
                }
            }
        }
        notifyDataSetChanged();
    }

    public ArrayList<Contacto> filtrarListado(String texto) {
        ArrayList<Contacto> listadoContacto = new ArrayList<>();
        listadoContacto.clear();

        if (texto.length() == 0) {
            for (FotografiaContacto contacto : copyContactos) {
                if (contacto.getNombre().toLowerCase().contains(texto.toLowerCase())) {
                    Contacto contactoLista = new Contacto();
                    contactoLista.setId(String.valueOf(contacto.getId()));
                    contactoLista.setNombre(contacto.getNombre());
                    contactoLista.setFoto(contacto.getFoto().toString());
                    contactoLista.setLatitud(contacto.getLatitud());
                    contactoLista.setLongitud(contacto.getLongitud());
                    listadoContacto.add(contactoLista);
                }
            }
        } else {
            for (FotografiaContacto contacto : copyContactos) {
                if (contacto.getNombre().toLowerCase().contains(texto.toLowerCase())) {
                    Contacto contactoLista = new Contacto();
                    contactoLista.setId(String.valueOf(contacto.getId()));
                    contactoLista.setNombre(contacto.getNombre());
                    contactoLista.setFoto(contacto.getFoto().toString());
                    contactoLista.setLatitud(contacto.getLatitud());
                    contactoLista.setLongitud(contacto.getLongitud());
                    listadoContacto.add(contactoLista);
                }
            }
        }
        notifyDataSetChanged();
        return listadoContacto;
    }
}