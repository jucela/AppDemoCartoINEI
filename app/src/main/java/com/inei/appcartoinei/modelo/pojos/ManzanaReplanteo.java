package com.inei.appcartoinei.modelo.pojos;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class ManzanaReplanteo {
    private String idManzana;
    private ArrayList<LatLng> lista;

    public ManzanaReplanteo() {
    }

    public ManzanaReplanteo(String idManzana, ArrayList<LatLng> lista) {
        this.idManzana = idManzana;
        this.lista = lista;
    }

    public String getIdManzana() {
        return idManzana;
    }

    public void setIdManzana(String idManzana) {
        this.idManzana = idManzana;
    }

    public ArrayList<LatLng> getLista() {
        return lista;
    }

    public void setLista(ArrayList<LatLng> lista) {
        this.lista = lista;
    }
}
