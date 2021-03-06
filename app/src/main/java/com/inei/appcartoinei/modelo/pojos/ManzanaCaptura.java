package com.inei.appcartoinei.modelo.pojos;

public class ManzanaCaptura {
    private String idmanzana;
    private int iduser;
    private String ccdd;
    private String ccpp;
    private String ccdi;
    private String codzona;
    private String sufzona;
    private String codmzna;
    private String sufmzna;
    private String mznabelong;
    private int estado;
    private int frentes;
    private int cargado;
    private String shape;

    public ManzanaCaptura() {
    }

    public ManzanaCaptura(String idmanzana, int iduser, String ccdd, String ccpp, String ccdi, String codzona, String sufzona, String codmzna, String sufmzna, String mznabelong, int estado, int frentes, int cargado, String shape) {
        this.idmanzana = idmanzana;
        this.iduser = iduser;
        this.ccdd = ccdd;
        this.ccpp = ccpp;
        this.ccdi = ccdi;
        this.codzona = codzona;
        this.sufzona = sufzona;
        this.codmzna = codmzna;
        this.sufmzna = sufmzna;
        this.mznabelong = mznabelong;
        this.estado = estado;
        this.frentes = frentes;
        this.cargado = cargado;
        this.shape = shape;
    }

    public String getIdmanzana() {
        return idmanzana;
    }

    public void setIdmanzana(String idmanzana) {
        this.idmanzana = idmanzana;
    }

    public int getIduser() {
        return iduser;
    }

    public void setIduser(int iduser) {
        this.iduser = iduser;
    }

    public String getCcdd() {
        return ccdd;
    }

    public void setCcdd(String ccdd) {
        this.ccdd = ccdd;
    }

    public String getCcpp() {
        return ccpp;
    }

    public void setCcpp(String ccpp) {
        this.ccpp = ccpp;
    }

    public String getCcdi() {
        return ccdi;
    }

    public void setCcdi(String ccdi) {
        this.ccdi = ccdi;
    }

    public String getCodzona() {
        return codzona;
    }

    public void setCodzona(String codzona) {
        this.codzona = codzona;
    }

    public String getSufzona() {
        return sufzona;
    }

    public void setSufzona(String sufzona) {
        this.sufzona = sufzona;
    }

    public String getCodmzna() {
        return codmzna;
    }

    public void setCodmzna(String codmzna) {
        this.codmzna = codmzna;
    }

    public String getSufmzna() {
        return sufmzna;
    }

    public void setSufmzna(String sufmzna) {
        this.sufmzna = sufmzna;
    }

    public String getMznabelong() {
        return mznabelong;
    }

    public void setMznabelong(String mznabelong) {
        this.mznabelong = mznabelong;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public int getFrentes() {
        return frentes;
    }

    public void setFrentes(int frentes) {
        this.frentes = frentes;
    }

    public int getCargado() {
        return cargado;
    }

    public void setCargado(int cargado) {
        this.cargado = cargado;
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }
}
