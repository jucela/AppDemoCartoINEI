package com.inei.appcartoinei.utils;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UtilsPoligonos {

    /*METODO CONVERTIR LISTA(STRING) A LISTA(LATLNG)*/
    public static ArrayList<LatLng> getLatLngShapeManzana(String shape) {
        ArrayList<LatLng> listapintado = new ArrayList<LatLng>();
        String campoGeom = shape;
        try {
            JSONObject jsonObject = new JSONObject(campoGeom);
            String dato = jsonObject.getString("coordinates");
            //Log.e("mensaje:","String de datos->[]:"+dato);
            String ncadena1 = dato.substring(1, dato.length() - 1);
            String ncadena2 = ncadena1.substring(1, ncadena1.length() - 1);
            String ncadena3 = ncadena2.replace("],[", "];[");
            String[] parts = ncadena3.split(";");
            for (int i = 0; i < parts.length; i++) {
                String part1 = parts[i];
                String cadena4 = part1.substring(1, part1.length() - 1);
                String[] latlog = cadena4.split(",");
                for (int x = 0; x < 1; x++) {
                    listapintado.add(new LatLng(Double.parseDouble(latlog[0]), Double.parseDouble(latlog[1])));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listapintado;
    }

    /*METODO DE FORMATO A POLYGONO*/
    public static String formatGeom(ArrayList<LatLng> poligono) {
        String format = "";
        for (int i = 0; i < poligono.size(); i++) {
            if (i > 0) {
                format = format + "," + poligono.get(i).latitude + " " + poligono.get(i).longitude;
            } else {
                format = poligono.get(i).latitude + " " + poligono.get(i).longitude;
            }
        }
        return format;
    }

    /*OBTENER CENTRO DE POLIGONO*/
    public static LatLng getCenterOfPolygon(ArrayList<LatLng> latLngList) {
        double[] centroid = {0.0, 0.0};
        for (int i = 0; i < latLngList.size(); i++) {
            centroid[0] += latLngList.get(i).latitude;
            centroid[1] += latLngList.get(i).longitude;
        }
        int totalPoints = latLngList.size();

        return new LatLng(centroid[0] / totalPoints, centroid[1] / totalPoints);
    }
}
