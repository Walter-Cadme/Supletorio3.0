package com.example.supletorio30;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class CountryDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_detail); // Asegúrate de tener el layout activity_country_detail

        // Recuperar los datos enviados
        String nombrePais = getIntent().getStringExtra("nombrePais");
        String urlLogo = getIntent().getStringExtra("urlLogo");

        // Referencias de los elementos UI
        TextView txtNombrePais = findViewById(R.id.txtNombrePaisDetalle);
        ImageView imgLogoPais = findViewById(R.id.imgLogoDetalle);

        // Asignar los datos a los elementos UI
        txtNombrePais.setText(nombrePais);
        Glide.with(this).load(urlLogo).into(imgLogoPais);
    }
}
