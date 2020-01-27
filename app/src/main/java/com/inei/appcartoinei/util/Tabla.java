package com.inei.appcartoinei.util;

import android.content.ContentValues;

import java.util.ArrayList;

public class Tabla {
    public String nombre_tabla="";
    public ArrayList<ColumnaTabla> columnas_tabla;

    public Tabla(){
        nombre_tabla="";
        columnas_tabla = new ArrayList<>();
    }

    public String getNombre_tabla(){
        String tabla= "" + nombre_tabla;
        return tabla.replace("tabla_","");
    }

    public ContentValues toValues(){
        ContentValues contentValues = new ContentValues();

        for(ColumnaTabla columnaTabla:columnas_tabla){
            contentValues.put(columnaTabla.nombre_columna,columnaTabla.valor_columna);
        }
        return contentValues;
    }
}
