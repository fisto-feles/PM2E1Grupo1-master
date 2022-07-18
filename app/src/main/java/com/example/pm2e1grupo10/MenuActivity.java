package com.example.pm2e1grupo10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

      //  Button agregar = findViewById(R.id.button);
      //  Button agrega = findViewById(R.id.button2);
    }
    public void Agregar(View view){
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }

    public void Lista(View view){
        Intent i = new Intent(this,ListaContactoActivity.class);
        startActivity(i);
    }
}