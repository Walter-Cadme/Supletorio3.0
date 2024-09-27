package com.example.supletorio30;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import WebServices.Asynchtask;
import WebServices.WebService;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, Asynchtask {

    private ArrayList<ListaBanderas> listapaises = new ArrayList<ListaBanderas>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Map<String, String> datos = new HashMap<String, String>();
        WebService ws= new WebService("http://www.geognos.com/api/en/countries/info/all.json",
                datos, MainActivity.this, MainActivity.this);
        ws.execute("GET");
    }


    @Override
    public void processFinish(String result) throws JSONException {
        JSONObject response = new JSONObject(result);
        JSONObject resultsObject = response.getJSONObject("Results");
        listapaises = ListaBanderas.JsonObjectsBuild(resultsObject);

        AdaptadorListaBandera adaptadorPais = new AdaptadorListaBandera(this, listapaises);
        ListView lstOpciones = (ListView) findViewById(R.id.lstBanderas);
        lstOpciones.setAdapter(adaptadorPais);

        // Establecer el OnItemClickListener para el ListView
        lstOpciones.setOnItemClickListener(this);
    }

    // Este método se invoca cuando se hace clic en un elemento del ListView
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Obtener el objeto seleccionado de la lista
        ListaBanderas paisSeleccionado = listapaises.get(position);

        // Crear un Intent para abrir la segunda actividad (MainActivity2)
        Intent intent = new Intent(MainActivity.this, MainActivity2.class);

        // Pasar el código del país al Intent
        String countryCode = paisSeleccionado.getCountryCode();
        Log.d("MainActivity", "Código del país seleccionado: " + countryCode);
        intent.putExtra("nombreNacionalidad", countryCode);

        // Iniciar la actividad
        startActivity(intent);
    }
}

