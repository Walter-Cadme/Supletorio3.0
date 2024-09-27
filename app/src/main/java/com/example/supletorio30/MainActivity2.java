package com.example.supletorio30;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity2 extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MainActivity2"; // Para los logs
    private TextView txtResultName;
    private TextView txtinformacion;
    private ImageView imgPais;
    private String nombreNacionalidad;
    private GoogleMap mapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        txtResultName = findViewById(R.id.txtResultado);
        txtinformacion = findViewById(R.id.txtinfo); // Asegúrate de tener este ID en tu XML
        imgPais = findViewById(R.id.ImagenPais);

        // Obtener el código del país desde el Intent
        nombreNacionalidad = getIntent().getStringExtra("nombreNacionalidad");

        // Validar que el código no sea nulo o vacío
        if (nombreNacionalidad == null || nombreNacionalidad.isEmpty()) {
            Log.e(TAG, "El nombre del país es nulo o vacío");
            return;
        }

        // Mostrar el código del país en el TextView (opcional, solo para depuración)
        txtResultName.setText(nombreNacionalidad);

        // Obtener la información del país usando el código del país
        cambiarNombreEnURL(nombreNacionalidad);

        // Obtener el fragmento de mapa y establecer el callback
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void cambiarNombreEnURL(String nombreNacionalidad) {
        String infoUrl = "http://www.geognos.com/api/en/countries/info/" + nombreNacionalidad + ".json";
        Log.d(TAG, "URL de información del país: " + infoUrl); // Log de la URL
        new GetCountryInfoTask().execute(infoUrl);
    }

    // Este método se llama cuando el mapa está listo
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mapa = googleMap;
        mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mapa.getUiSettings().setZoomControlsEnabled(true);
        // Configuración inicial del mapa, se puede mover luego con la información del país
        CameraUpdate camUpd1 = CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), 2);
        mapa.moveCamera(camUpd1);
    }

    // Clase interna para obtener la información del país
    private class GetCountryInfoTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                // Validar la respuesta del servidor
                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Error en la conexión. Código de respuesta: " + responseCode);
                    return null;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                result = stringBuilder.toString();
                reader.close();
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Error en la conexión: " + e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s == null || s.isEmpty()) {
                Log.e(TAG, "La respuesta del servidor está vacía o es nula");
                return;
            }

            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONObject countryInfo = jsonObject.getJSONObject("Results");

                // Validar si el objeto tiene el nombre del país
                if (countryInfo.has("Name")) {
                    String countryName = countryInfo.getString("Name");
                    txtResultName.setText(countryName);
                }

                // Obtener las coordenadas geográficas
                if (countryInfo.has("GeoPt")) {
                    JSONArray geoPtArray = countryInfo.getJSONArray("GeoPt");
                    double latitude = geoPtArray.getDouble(0);
                    double longitude = geoPtArray.getDouble(1);
                    LatLng countryLocation = new LatLng(latitude, longitude);
                    CameraUpdate countryLocationUpdate = CameraUpdateFactory.newLatLngZoom(countryLocation, 4);
                    if (mapa != null) {
                        mapa.moveCamera(countryLocationUpdate);

                        // Dibujar el rectángulo que representa el área geográfica
                        if (countryInfo.has("GeoRectangle")) {
                            JSONObject geoRectangle = countryInfo.getJSONObject("GeoRectangle");
                            double west = geoRectangle.getDouble("West");
                            double east = geoRectangle.getDouble("East");
                            double north = geoRectangle.getDouble("North");
                            double south = geoRectangle.getDouble("South");
                            PolylineOptions lineas = new PolylineOptions()
                                    .add(new LatLng(north, west))
                                    .add(new LatLng(north, east))
                                    .add(new LatLng(south, east))
                                    .add(new LatLng(south, west))
                                    .add(new LatLng(north, west));
                            lineas.width(8).color(Color.RED);
                            mapa.addPolyline(lineas);
                        }
                    }
                }

                // Obtener la URL de la bandera y descargarla
                String flagUrl = "http://www.geognos.com/api/en/countries/flag/" + nombreNacionalidad + ".png";
                new GetFlagImageTask().execute(flagUrl);

                // Obtener información adicional (capital, códigos ISO, etc.)
                if (countryInfo.has("Capital")) {
                    JSONObject capitalInfo = countryInfo.getJSONObject("Capital");
                    String capitalName = capitalInfo.getString("Name");
                    JSONObject countryCodesInfo = countryInfo.getJSONObject("CountryCodes");
                    String iso2Code = countryCodesInfo.getString("iso2");
                    String iso3Code = countryCodesInfo.getString("iso3");
                    String fipsCode = countryCodesInfo.getString("fips");
                    int isoNValue = countryCodesInfo.getInt("isoN");

                    txtinformacion.setText("Capital: " + capitalName + "\n" +
                            "CODE ISO 2: " + iso2Code + "\n" +
                            "CODE ISO NUM: " + isoNValue + "\n" +
                            "CODE ISO 3: " + iso3Code + "\n" +
                            "CODE FIPS: " + fipsCode + "\n");
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "Error al procesar la respuesta JSON: " + e.getMessage());
            }
        }
    }

    // Clase interna para descargar la imagen de la bandera
    private class GetFlagImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Error al descargar la imagen: " + e.getMessage());
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                imgPais.setImageBitmap(bitmap);
            } else {
                Log.e(TAG, "El bitmap descargado es nulo");
            }
        }
    }
}

