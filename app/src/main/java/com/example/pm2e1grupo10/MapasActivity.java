package com.example.pm2e1grupo10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.pm2e1grupo10.databinding.ActivityMainBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapasActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ActivityMainBinding binding;
    String nombre, longitud, latitud;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapas);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        nombre = intent.getStringExtra("nombre");
        longitud = intent.getStringExtra("longitud");
        latitud = intent.getStringExtra("latitud");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        GoogleMap map = googleMap;
        // ... get a map.
        // Add a thin red line from London to New York.
        LatLng sydney = new LatLng(Double.parseDouble(latitud), Double.parseDouble(longitud));
        mMap.addMarker(new MarkerOptions().position(sydney).title("Ubicación de: "+nombre));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(Double.parseDouble(latitud), Double.parseDouble(longitud)))
                .zoom(14)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}