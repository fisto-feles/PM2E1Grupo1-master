package com.example.pm2e1grupo10;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class EditarContactoActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int PETICION_ACCESO_PERMISOS = 100;

    String id;
    Contacto contacto;
    byte[] byteArray;

    EditText txtNombreContacto2, txtTelefonoContacto2, txtLongitud2, txtLatitud2;
    Button btnGuardar2, btnEliminar, btnVolver, btnTomarFoto2;
    ImageView imgFoto2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_contacto);

        Intent intent = getIntent();
        id = intent.getStringExtra("idCont");
        contacto = new Contacto("","","","","","");

        txtNombreContacto2 = (EditText) findViewById(R.id.txtNombreContacto2);
        txtTelefonoContacto2 = (EditText) findViewById(R.id.txtTelefonoContacto2);
        txtLongitud2 = (EditText) findViewById(R.id.txtLongitud2);
        txtLatitud2 = (EditText) findViewById(R.id.txtLatitud2);
        imgFoto2 = (ImageView) findViewById(R.id.imgFoto2);

        String url = RestApiMethods.ApiGetID+id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    txtNombreContacto2.setText(response.getString("nombre"));
                    txtTelefonoContacto2.setText(response.getString("telefono"));
                    txtLatitud2.setText(String.valueOf(response.getDouble("latitud")));
                    txtLongitud2.setText(String.valueOf(response.getDouble("longitud")));
                    if (response.getString("foto") != null) {
                        setFoto(response.getString("foto"));
                    }
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), "ERROR AL LLENAR LOS CAMPOS!!!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
                Toast.makeText(getApplicationContext(), "ERROR AL OBTENER LOS DATOS!!!", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);

        btnTomarFoto2 = (Button) findViewById(R.id.btnTomarFoto2);
        btnGuardar2 = (Button) findViewById(R.id.btnGuardar2);
        btnEliminar = (Button) findViewById(R.id.btnEliminar);
        btnVolver = (Button) findViewById(R.id.btnListarContactos2);

        btnTomarFoto2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permisos();
            }
        });

        btnGuardar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarContacto();
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarContacto();
                finish();
            }
        });

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void eliminarContacto() {
        String url = RestApiMethods.ApiDeleteUrl+id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String Error = response.getString("httpStatus");
                            if (Error.equals("")||Error.equals(null)){
                            }
                            else if(Error.equals("OK")){
                                JSONObject body = response.getJSONObject("body");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(),"Contacto Eliminado",Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error", "Error: " + error.getMessage());
                System.out.println("---------------------------Error" + error.getMessage());
                Toast.makeText(getApplicationContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }

    private void actualizarContacto() {
        int comprobaciones = 0;
        int numeros = 0;

        contacto.setId(this.id);
        contacto.setNombre(txtNombreContacto2.getText().toString());
        contacto.setTelefono(txtTelefonoContacto2.getText().toString());
        contacto.setLongitud(txtLongitud2.getText().toString());
        contacto.setLatitud(txtLatitud2.getText().toString());

        if(imgFoto2.getDrawable() != null) {
            BitmapDrawable drawable = (BitmapDrawable) imgFoto2.getDrawable();
            Bitmap photo = drawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray = stream.toByteArray();
            String encode = Base64.encodeToString(byteArray, Base64.DEFAULT);
            contacto.setFoto(encode);
        }

        if(txtNombreContacto2.getText().toString().isEmpty() || txtTelefonoContacto2.getText().toString().isEmpty()) {
            mostrarDialogoVacios();
            comprobaciones = 1;
        }

        if(contacto.getFoto() == "" && comprobaciones == 0) {
            mostrarDialogoImagenNoTomada();
            comprobaciones = 1;
        }

        if((contacto.getLatitud() == "" || contacto.getLongitud() == "") && comprobaciones == 0) {
            mostrarDialogoLocalizacionNoEncontrada();
            comprobaciones = 1;
        }

        if(comprobaciones == 0) {
            for (int i = 0; i < txtNombreContacto2.getText().toString().length(); i++) {
                if (Character.isDigit(txtNombreContacto2.getText().toString().charAt(i))) {
                    mostrarDialogoNumeros();
                    numeros = 1;
                    break;
                }
            }

            if (numeros == 0) {

                JSONObject object = new JSONObject();
                String url = RestApiMethods.ApiUpdateUrl;
                try
                {
                    object.put("id",contacto.getId());
                    object.put("nombre",contacto.getNombre());
                    object.put("telefono",contacto.getTelefono());
                    object.put("latitud",contacto.getLatitud());
                    object.put("longitud",contacto.getLongitud());
                    object.put("foto", contacto.getFoto());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String Error = response.getString("httpStatus");
                                    if (Error.equals("")||Error.equals(null)){
                                    }
                                    else if(Error.equals("OK")){
                                        JSONObject body = response.getJSONObject("body");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(getApplicationContext(),"Contacto Guardado",Toast.LENGTH_LONG).show();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("Error", "Error: " + error.getMessage());
                        System.out.println("---------------------------Error" + error.getMessage());
                        Toast.makeText(EditarContactoActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(jsonObjectRequest);
                finish();
            }
        }
    }


    private void mostrarDialogoVacios() {
        new AlertDialog.Builder(this)
                .setTitle("Alerta de Vacíos")
                .setMessage("No puede dejar ningún campo vacío")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }


    private void mostrarDialogoImagenNoTomada() {
        new AlertDialog.Builder(this)
                .setTitle("Alerta de Fotografía")
                .setMessage("No se ha tomado ninguna fotografía")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

    private void mostrarDialogoLocalizacionNoEncontrada() {
        new AlertDialog.Builder(this)
                .setTitle("Alerta de Localización")
                .setMessage("No se ha encontrado su localización")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

    private void mostrarDialogoGPSInactivo() {
        new AlertDialog.Builder(this)
                .setTitle("Activación de GPS")
                .setMessage("Debe activar la ubicación de su dispositivo para acceder a todas las funciones")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

    private void mostrarDialogoNumeros() {
        new AlertDialog.Builder(this)
                .setTitle("Alerta de Números")
                .setMessage("No puede ingresar números en el campo de Nombre")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

    private void permisos() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(EditarContactoActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PETICION_ACCESO_PERMISOS);
        } else {
            tomarFoto();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PETICION_ACCESO_PERMISOS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                tomarFoto();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Se necesitan permisos de acceso", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            getBytes(data);
        }
    }

    private void setFoto(String foto) {
        byte[] ba = Base64.decode(foto, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(ba, 0, ba.length);
        imgFoto2.setImageBitmap(bitmap);
    }

    private void getBytes(Intent data) {
        Bitmap photo = (Bitmap) data.getExtras().get("data");
        imgFoto2.setImageBitmap(photo);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byteArray = stream.toByteArray();

        //SETEO DE DATOS EN EL OBJETO (FOTO BASE64 Y NOMBRE DEL ARCHIVO)
        String encode = Base64.encodeToString(byteArray, Base64.DEFAULT);
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        contacto.setFoto(encode);
    }

    private void tomarFoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

}