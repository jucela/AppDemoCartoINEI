package com.inei.appcartoinei.modelo.pojos;

import com.google.android.gms.maps.model.Polygon;

public class Poligono {

    private int id;
    private int export;
    private String ubigeo;
    private String zona;
    private String manzana;

    public Poligono() {
    }

    public Poligono(int id, int export, String ubigeo, String zona, String manzana) {
        this.id = id;
        this.export = export;
        this.ubigeo = ubigeo;
        this.zona = zona;
        this.manzana = manzana;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getExport() {
        return export;
    }

    public void setExport(int export) {
        this.export = export;
    }

    public String getUbigeo() {
        return ubigeo;
    }

    public void setUbigeo(String ubigeo) {
        this.ubigeo = ubigeo;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public String getManzana() {
        return manzana;
    }

    public void setManzana(String manzana) {
        this.manzana = manzana;
    }
}
