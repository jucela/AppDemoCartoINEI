package com.inei.appcartoinei.modelo.pojos;

public class PoligonoManzana {
    private String idzona;
    private String idmanzana;
    private String shape;


    public PoligonoManzana() {
    }

    public PoligonoManzana(String idzona, String idmanzana, String shape) {
        this.idzona = idzona;
        this.idmanzana = idmanzana;
        this.shape = shape;
    }

    public String getIdzona() {
        return idzona;
    }

    public void setIdzona(String idzona) {
        this.idzona = idzona;
    }

    public String getIdmanzana() {
        return idmanzana;
    }

    public void setIdmanzana(String idmanzana) {
        this.idmanzana = idmanzana;
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }
}
