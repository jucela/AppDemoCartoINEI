package com.inei.appcartoinei.modelo.pojos;

import android.content.ContentValues;

import com.inei.appcartoinei.modelo.DAO.SQLConstantes;

public class Capa {
    private int id;
    private String nombre;
    private String descripcion;
    private String tipo;
    private int srid;
    private int escalamin;
    private int escalamax;
    private int escalamineti;
    private int escalamaxeti;

    public Capa() {
    }

    public Capa(int id, String nombre, String descripcion, String tipo, int srid, int escalamin, int escalamax, int escalamineti, int escalamaxeti) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.srid = srid;
        this.escalamin = escalamin;
        this.escalamax = escalamax;
        this.escalamineti = escalamineti;
        this.escalamaxeti = escalamaxeti;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getSrid() {
        return srid;
    }

    public void setSrid(int srid) {
        this.srid = srid;
    }

    public int getEscalamin() {
        return escalamin;
    }

    public void setEscalamin(int escalamin) {
        this.escalamin = escalamin;
    }

    public int getEscalamax() {
        return escalamax;
    }

    public void setEscalamax(int escalamax) {
        this.escalamax = escalamax;
    }

    public int getEscalamineti() {
        return escalamineti;
    }

    public void setEscalamineti(int escalamineti) {
        this.escalamineti = escalamineti;
    }

    public int getEscalamaxeti() {
        return escalamaxeti;
    }

    public void setEscalamaxeti(int escalamaxeti) {
        this.escalamaxeti = escalamaxeti;
    }

    public ContentValues toValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLConstantes.capa_cp_id,id);
        contentValues.put(SQLConstantes.capa_cp_nombre,nombre);
        contentValues.put(SQLConstantes.capa_cp_descripcion,descripcion);
        contentValues.put(SQLConstantes.capa_cp_tipo,tipo);
        contentValues.put(SQLConstantes.capa_cp_srid,srid);
        contentValues.put(SQLConstantes.capa_cp_escalamin,escalamin);
        contentValues.put(SQLConstantes.capa_cp_escalamax,escalamax);
        contentValues.put(SQLConstantes.capa_cp_escalamineti,escalamineti);
        contentValues.put(SQLConstantes.capa_cp_escalamaxeti,escalamaxeti);
        return contentValues;
    }
}
