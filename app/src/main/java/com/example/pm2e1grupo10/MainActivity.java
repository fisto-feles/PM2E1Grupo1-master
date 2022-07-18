package com.example.pm2e1grupo10;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
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
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int PETICION_ACCESO_PERMISOS = 100;

    ImageView imgFoto;
    EditText txtNombreContacto, txtTelefonoContacto;
    TextView txtLatitud, txtLongitud;

    Contacto contacto;
    byte[] byteArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgFoto = (ImageView) findViewById(R.id.imgFoto);
        txtNombreContacto = (EditText) findViewById(R.id.txtNombreContacto);
        txtTelefonoContacto = (EditText) findViewById(R.id.txtTelefonoContacto);
        txtLatitud = (TextView) findViewById(R.id.txtLatitud);
        txtLongitud = (TextView) findViewById(R.id.txtLongitud);
        Button btnTomarFoto = (Button) findViewById(R.id.btnTomarFoto);
        Button btnSalvar = (Button) findViewById(R.id.btnGuardar);
        Button btnListarContactos = (Button) findViewById(R.id.btnListarContactos);
        contacto = new Contacto("","","","","","");
        checkGPS();

        //BOTON TOMAR FOTO
        btnTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permisos();
            }
        });

        //BOTON SALVAR CONTACTO
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarContacto();
            }
        });

        btnListarContactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ListaContactoActivity.class);
                startActivity(intent);
            }
        });

    }

    //FUNCIONES REALCIONADAS A LA TOMA DE LA FOTOGRAFIA
    private void permisos() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PETICION_ACCESO_PERMISOS);
        } else {
            tomarFoto();
        }
    }

    private void checkGPS(){
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(!provider.contains("gps")){
            mostrarDialogoGPSInactivo();
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

    private void getBytes(Intent data) {
        Bitmap photo = (Bitmap) data.getExtras().get("data");
        imgFoto.setImageBitmap(photo);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byteArray = stream.toByteArray();

        //SETEO DE DATOS EN EL OBJETO (FOTO BASE64 Y NOMBRE DEL ARCHIVO)
        String encode = Base64.encodeToString(byteArray, Base64.DEFAULT);
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        contacto.setFoto(encode);
       //contacto.setArchivo(currentDateTimeString);
        obtenerLocalizacion();
    }

    private void tomarFoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void obtenerLocalizacion() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        encontrarUbicacion(getApplicationContext(), lm);
    }

    public void encontrarUbicacion(Context contexto, LocationManager locationManager) {
        String location_context = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) contexto.getSystemService(location_context);
        List<String> providers = locationManager.getProviders(true);
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(provider, 1000, 0,
                    new LocationListener() {

                        public void onLocationChanged(Location location) {
                            String longitud = String.valueOf(location.getLongitude());
                            String latitud = String.valueOf(location.getLatitude());
                            contacto.setLongitud(longitud);
                            contacto.setLatitud(latitud);
                            txtLongitud.setText(longitud);
                            txtLatitud.setText(latitud);
                        }

                        public void onProviderDisabled(String provider) {
                        }

                        public void onProviderEnabled(String provider) {
                        }

                        public void onStatusChanged(String provider, int status,
                                                    Bundle extras) {
                        }
                    });
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                String longitud = String.valueOf(location.getLongitude());
                String latitud = String.valueOf(location.getLatitude());
                contacto.setLongitud(longitud);
                contacto.setLatitud(latitud);
                txtLongitud.setText(longitud);
                txtLatitud.setText(latitud);
            }
        }
    }

    //FUNCIONES RELACIONADAS AL GUARDADO DEL CONTACTO Y MENSAJES DE ERROR
    private void agregarContacto() {
        int comprobaciones = 0;
        int numeros = 0;
        if(txtNombreContacto.getText().toString().isEmpty() || txtTelefonoContacto.getText().toString().isEmpty()) {
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
            for (int i = 0; i < txtNombreContacto.getText().toString().length(); i++) {
                if (Character.isDigit(txtNombreContacto.getText().toString().charAt(i))) {
                    mostrarDialogoNumeros();
                    numeros = 1;
                    break;
                }
            }

            if (numeros == 0) {
                contacto.setNombre(txtNombreContacto.getText().toString());
                contacto.setTelefono(txtTelefonoContacto.getText().toString());
                JSONObject object = new JSONObject();
                String url = RestApiMethods.ApiCreateUrl;
                try
                {
                    object.put("nombre",contacto.getNombre());
                    object.putOpt("telefono",contacto.getTelefono());
                    object.putOpt("latitud",contacto.getLatitud());
                    object.putOpt("longitud",contacto.getLongitud());
                    object.putOpt("foto", contacto.getFoto());
                 //   object.putOpt("archivo",contacto.getArchivo());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.d("JSON", response.toString());
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
                        System.out.println("Error" + error.getMessage());
                        Toast.makeText(MainActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(jsonObjectRequest);
                ClearScreen();
            }
        }
    }

    private void ClearScreen() {
        byteArray = new byte[0];
        imgFoto.setImageResource(R.mipmap.ic_launcher_round);
        contacto.setNombre("");
        contacto.setTelefono("");
        contacto.setLatitud("");
        contacto.setLongitud("");
        contacto.setFoto("");
       // contacto.setArchivo("");
        txtNombreContacto.setText("");
        txtTelefonoContacto.setText("");
        txtLatitud.setText("");
        txtLongitud.setText("");
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

}