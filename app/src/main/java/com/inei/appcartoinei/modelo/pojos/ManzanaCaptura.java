package com.inei.appcartoinei.modelo.pojos;

public class ManzanaCaptura {
    private int id;
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
    private String shape;

    public ManzanaCaptura() {
    }

    public ManzanaCaptura(int id, int iduser, String ccdd, String ccpp, String ccdi, String codzona, String sufzona, String codmzna, String sufmzna, String mznabelong, int estado, int frentes, String shape) {
        this.id = id;
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
        this.shape = shape;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }
}
