package com.example.supletorio30;


import android.content.Context;
import android.content.Intent; // Importar Intent para iniciar una nueva actividad
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AdaptadorListaBandera extends ArrayAdapter<ListaBanderas> {

    public AdaptadorListaBandera(Context context, ArrayList<ListaBanderas> datos) {
        super(context, R.layout.lyitem, datos);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View item = inflater.inflate(R.layout.lyitem, null);

        TextView lblNombre = (TextView) item.findViewById(R.id.lblNombre);
        lblNombre.setText(getItem(position).getNombre());

        ImageView imageView = (ImageView) item.findViewById(R.id.imgLogo);
        Glide.with(this.getContext())
                .load(getItem(position).getUrlLogo())
                .into(imageView);

        // Agregar el evento de clic al elemento de la lista
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent para abrir la nueva actividad
                Intent intent = new Intent(getContext(), CountryDetailActivity.class);

                // Pasar los datos necesarios a la nueva actividad
                intent.putExtra("nombrePais", getItem(position).getNombre());
                intent.putExtra("urlLogo", getItem(position).getUrlLogo());

                // Iniciar la nueva actividad
                getContext().startActivity(intent);
            }
        });

        return item;
    }
}


