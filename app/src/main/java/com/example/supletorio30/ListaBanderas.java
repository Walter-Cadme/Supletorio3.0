package com.example.supletorio30;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class ListaBanderas {
    String Nombre;

    public String getNombre() {
        return Nombre;
    }
    public void setNombre(String nombre) {
        Nombre = nombre;
    }
    public String getUrlLogo() {
        return UrlLogo;
    }
    public void setUrlLogo(String urlLogo) {
        UrlLogo = urlLogo;
    }
    String UrlLogo;
    public ListaBanderas(String countryCode, JSONObject countryData) throws JSONException {
        Nombre = countryData.getString("Name");
        UrlLogo = "http://www.geognos.com/api/en/countries/flag/" + countryCode + ".png";
    }
    public static ArrayList<ListaBanderas> JsonObjectsBuild(JSONObject datos) throws JSONException {
        ArrayList<ListaBanderas> lstpaises = new ArrayList<>();
        Iterator<String> keys = datos.keys();
        while (keys.hasNext()) {
            String countryCode = keys.next();
            JSONObject countryData = datos.getJSONObject(countryCode);
            lstpaises.add(new ListaBanderas(countryCode, countryData));
        }
        return lstpaises;
    }
}

