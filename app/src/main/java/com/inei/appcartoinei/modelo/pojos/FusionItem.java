package com.inei.appcartoinei.modelo.pojos;

import android.os.Parcel;
import android.os.Parcelable;

public class FusionItem implements Parcelable {

    private int estado;
    private String idzona;
    private String idManzana;

    public FusionItem(int estado, String idzona, String idManzana) {
        this.estado = estado;
        this.idzona = idzona;
        this.idManzana = idManzana;
    }

    public FusionItem() {
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getIdzona() {
        return idzona;
    }

    public void setIdzona(String idzona) {
        this.idzona = idzona;
    }

    public String getIdManzana() {
        return idManzana;
    }

    public void setIdManzana(String idManzana) {
        this.idManzana = idManzana;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
