package com.inei.appcartoinei.modelo.pojos;

import android.content.ContentValues;

import com.inei.appcartoinei.modelo.DAO.SQLConstantes;
public class Manzana {
    private int id;
    private int iduser;
    private String idmanzana;
    private String nommanzana;
    private String idzona;
    private String zona;
    private String ubigeo;
    private String shape;

    public Manzana() {

    }

    public Manzana(int id, int iduser, String idmanzana, String nommanzana, String idzona, String zona, String ubigeo, String shape) {
        this.id = id;
        this.iduser = iduser;
        this.idmanzana = idmanzana;
        this.nommanzana = nommanzana;
        this.idzona = idzona;
        this.zona = zona;
        this.ubigeo = ubigeo;
        this.shape = shape;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserid() {
        return iduser;
    }

    public void setUserid(int userid) {
        this.iduser = iduser;
    }

    public String getIdmanzana() {
        return idmanzana;
    }

    public void setIdmanzana(String idmanzana) {
        this.idmanzana = idmanzana;
    }

    public String getNommanzana() {
        return nommanzana;
    }

    public void setNommanzana(String nommanzana) {
        this.nommanzana = nommanzana;
    }

    public String getIdzona() {
        return idzona;
    }

    public void setIdzona(String idzona) {
        this.idzona = idzona;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public String getUbigeo() {
        return ubigeo;
    }

    public void setUbigeo(String ubigeo) {
        this.ubigeo = ubigeo;
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public void setVariable(String variable,String valor){
        switch (variable){
            case SQLConstantes.manzana_cp_id:         setId(Integer.parseInt(valor)); break;
            case SQLConstantes.manzana_cp_iduser:     setUserid(Integer.parseInt(valor)); break;
            case SQLConstantes.manzana_cp_idmanzana:  setIdmanzana(valor); break;
            case SQLConstantes.manzana_cp_nommanzana: setNommanzana(valor); break;
            case SQLConstantes.manzana_cp_idzona:     setIdzona(valor); break;
            case SQLConstantes.manzana_cp_zona:       setZona(valor); break;
            case SQLConstantes.manzana_cp_ubigeo:     setUbigeo(valor); break;
            case SQLConstantes.manzana_cp_shape:      setShape(valor); break;
            default: break;
        }
    }

    public ContentValues toValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLConstantes.manzana_cp_id,id);
        contentValues.put(SQLConstantes.manzana_cp_iduser,iduser);
        contentValues.put(SQLConstantes.manzana_cp_idmanzana,idmanzana);
        contentValues.put(SQLConstantes.manzana_cp_nommanzana,nommanzana);
        contentValues.put(SQLConstantes.manzana_cp_idzona,idzona);
        contentValues.put(SQLConstantes.manzana_cp_zona,zona);
        contentValues.put(SQLConstantes.manzana_cp_ubigeo,ubigeo);
        contentValues.put(SQLConstantes.manzana_cp_shape,shape);
        return contentValues;
    }
}
