package com.example.supletorio30;

import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, Asynchtask {

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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void processFinish(String result) throws JSONException {
        ArrayList<ListaBanderas> listapaises = new ArrayList<ListaBanderas>();
        JSONObject response = new JSONObject(result);

        JSONObject resultsObject = response.getJSONObject("Results");
        listapaises = ListaBanderas.JsonObjectsBuild(resultsObject);

        AdaptadorListaBandera adaptadorPais = new AdaptadorListaBandera(this, listapaises);
        ListView lstOpciones = (ListView) findViewById(R.id.lstBanderas);
        lstOpciones.setAdapter(adaptadorPais);
    }
}
