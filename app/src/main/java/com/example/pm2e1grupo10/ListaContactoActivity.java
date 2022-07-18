package com.example.pm2e1grupo10;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ListaContactoActivity extends AppCompatActivity {
    private String idCont, nombre, telefono, longitud, latitud;
    ListView lstContactos;
    ArrayList<String> arrayListContactos;
    ArrayList<FotografiaContacto> listadoContactos;
    ArrayList<Contacto> lista;
    EditText txtBuscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_contacto);
        lstContactos = findViewById(R.id.lstContactos);
        arrayListContactos = new ArrayList<String>();
        listadoContactos = new ArrayList<>();;
        txtBuscar = (EditText) findViewById(R.id.txtBuscar);
        Button btnVolver = (Button) findViewById(R.id.btnVolver);
        Button btnActualizar = (Button) findViewById(R.id.btnEditar);
        Button btnUbicacion = (Button) findViewById(R.id.btnUbicacion);

        //LISTA DE CONTACTOS
        SendRequest();

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pantallaVolver = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(pantallaVolver);
            }
        });

        btnUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(idCont != null){
                    //ubicacionContacto();

                    Uri gmmIntentUri = Uri.parse("google.navigation:q="+latitud+","+longitud);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");

                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }

                }else{
                    mostrarDialogoSeleccion();
                }
            }
        });

        lstContactos.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                idCont = lista.get(position).getId();
                nombre = lista.get(position).getNombre();
                telefono = lista.get(position).getTelefono();
                longitud = lista.get(position).getLongitud();
                latitud = lista.get(position).getLatitud();
                Toast.makeText(getApplicationContext(), "Ha seleccionado a: "+nombre, Toast.LENGTH_LONG).show();
            }
        });

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(idCont != null){
                    Intent pantallaActualizar = new Intent(getApplicationContext(), EditarContactoActivity.class);
                    pantallaActualizar.putExtra("idCont", String.valueOf(idCont));
                    startActivityForResult(pantallaActualizar,2552);
                }else{
                    mostrarDialogoSeleccion();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2552) {
            finish();
            Intent in = new Intent(getApplicationContext(),ListaContactoActivity.class);
            startActivity(in);
        }
    }

        private void SendRequest(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = RestApiMethods.ApiGetUrl;
        lista = new ArrayList<Contacto>();
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject contactoObject = new JSONObject();
                    for (int i = 0; i < response.length(); i++) {
                         contactoObject = response.getJSONObject(i);

                        Contacto contact = new Contacto(String.valueOf(contactoObject.getInt("id_contacto")),
                                contactoObject.getString("nombre"),
                                String.valueOf(contactoObject.getInt("telefono")),
                                String.valueOf(contactoObject.getDouble("latitud")),
                                String.valueOf(contactoObject.getDouble("longitud")),
                                contactoObject.getString("foto"));
                        lista.add(contact);
                        arrayListContactos.add(contact.getNombre());
                        byte[] foto = Base64.decode(contact.getFoto().getBytes(), Base64.DEFAULT);
                        FotografiaContacto fotografia = new FotografiaContacto(BitmapFactory.decodeByteArray(foto, 0, foto.length), contact.getNombre());
                        fotografia.setId(contact.getId());
                        fotografia.setLongitud(contact.getLongitud());
                        fotografia.setLatitud(contact.getLatitud());
                        listadoContactos.add(fotografia);
                    }
                    Adaptador adp = new Adaptador(getApplicationContext(), R.layout.activity_imagen, listadoContactos );
                    lstContactos.setAdapter(adp);
                    //BUSCADOR
                    txtBuscar.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            adp.filtrar(txtBuscar.getText().toString());
                            lista = adp.filtrarListado(txtBuscar.getText().toString());
                        }
                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });
                } catch (JSONException ex) {
                    System.out.println("Error" + ex.getMessage());
                    Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Error en Response", "onResponse: " +  error.getMessage().toString() );
            }
        });
        // Add the request to the RequestQueue.
        queue.add(arrayRequest);
    }
    private void mostrarDialogoSeleccion() {
        new AlertDialog.Builder(this)
                .setTitle("Alerta de Selección")
                .setMessage("Seleccione un contacto de la lista")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).show();
    }
    private void ubicacionContacto() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmación de ver Ubicación")
                .setMessage("¿Desea ver la ubicación del contacto de " + nombre + "?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent pantallaUbicacion = new Intent(getApplicationContext(), MapasActivity.class);
                        pantallaUbicacion.putExtra("idCont", String.valueOf(idCont));
                        pantallaUbicacion.putExtra("nombre", String.valueOf(nombre));
                        pantallaUbicacion.putExtra("longitud", String.valueOf(longitud));
                        pantallaUbicacion.putExtra("latitud", String.valueOf(latitud));
                        startActivity(pantallaUbicacion);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), "Se canceló ver ubicación", Toast.LENGTH_LONG).show();
                    }
                }).show();
    }
}
