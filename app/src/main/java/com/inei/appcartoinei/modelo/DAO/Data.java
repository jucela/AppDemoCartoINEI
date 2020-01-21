package com.inei.appcartoinei.modelo.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import com.inei.appcartoinei.modelo.pojos.Capa;
import com.inei.appcartoinei.modelo.pojos.Manzana;
import com.inei.appcartoinei.modelo.pojos.Poligono;

import org.spatialite.database.SQLiteDatabase;
import org.spatialite.database.SQLiteOpenHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Data {
    Context          contexto;
    SQLiteOpenHelper sqLiteOpenHelper;
    SQLiteDatabase   sqLiteDatabase;

    public Data (Context context) throws IOException{
        this.contexto = context;
        sqLiteOpenHelper = new DataBaseHelper(contexto);
        //sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
        //createDataBase();
    }

    public void createDataBase() throws  IOException {
        boolean dbExist = checkDataBase();
        Log.i("Datos",""+dbExist);
        if(!dbExist){

        }
        else{

            sqLiteDatabase.execSQL(SQLConstantes.SQL_CREATE_TABLA_CAPA);
            sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
            sqLiteDatabase.close();
        }
    }

    public void open() throws SQLException {
        String myPath = SQLConstantes.DB_PATH + SQLConstantes.DB_NAME;
        sqLiteDatabase = SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READWRITE);
    }

    public synchronized void close(){
        if(sqLiteDatabase != null){
            sqLiteDatabase.close();
        }
    }

    public boolean checkDataBase(){
        //sqLiteDatabase =null;
        try{
            String myPath = SQLConstantes.DB_PATH + SQLConstantes.DB_NAME;
            sqLiteDatabase = SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READWRITE);
            //sqLiteDatabase.close();
        }catch (Exception e){
            File dbFile = new File(SQLConstantes.DB_PATH + SQLConstantes.DB_NAME);
            return dbFile.exists();
        }
        if (sqLiteDatabase != null) sqLiteDatabase.close();
        //Si sqlLiteDatabase es diferente de nulo,Devuelve true : Devuelve false.
        return sqLiteDatabase != null ? true : false;
    }

    /*METODOS*/
    public ArrayList<Capa> getAllCapa(){
        ArrayList<Capa> capa = new ArrayList<>();
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT id,nombre,descripcion,tipo,srid,escalamin,escalamax,escalamineti,escalamaxeti FROM capa ",null);
            while(cursor.moveToNext()){
                Capa capas = new Capa();
                capas.setId(cursor.getInt(cursor.getColumnIndex("id")));
                capas.setNombre(cursor.getString(cursor.getColumnIndex("nombre")));
                capas.setDescripcion(cursor.getString(cursor.getColumnIndex("descripcion")));
                capas.setTipo(cursor.getString(cursor.getColumnIndex("tipo")));
                capas.setSrid(cursor.getInt(cursor.getColumnIndex("srid")));
                capas.setEscalamin(cursor.getInt(cursor.getColumnIndex("escalamin")));
                capas.setEscalamax(cursor.getInt(cursor.getColumnIndex("escalamax")));
                capas.setEscalamineti(cursor.getInt(cursor.getColumnIndex("escalamineti")));
                capas.setEscalamaxeti(cursor.getInt(cursor.getColumnIndex("escalamaxeti")));
                capa.add(capas);
            }
        }finally{
            if(cursor != null) cursor.close();
        }
        return capa;

    }

    public ArrayList<Manzana> getAllManzana(){
        ArrayList<Manzana> manzanas = new ArrayList<>();
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT id,iduser,idmanzana,nommanzana,idzona,zona,ubigeo,ST_Area(shape)*10000000000 FROM manzana ",null);
            while(cursor.moveToNext()){
                Manzana manzana = new Manzana();
//                manzana.setId(cursor.getInt(cursor.getColumnIndex("id")));
//                manzana.setUserid(cursor.getInt(cursor.getColumnIndex("iduser")));
//                manzana.setUbigeo(cursor.getString(cursor.getColumnIndex("idmanzana")));
//                manzana.setIdmanzana(cursor.getString(cursor.getColumnIndex("nommanzana")));
//                manzana.setIdzona(cursor.getString(cursor.getColumnIndex("idzona")));
//                manzana.setZona(cursor.getString(cursor.getColumnIndex("zona")));
//                manzana.setUbigeo(cursor.getString(cursor.getColumnIndex("ubigeo")));
//                manzana.setShape(cursor.getString(cursor.getColumnIndex("area")));
                manzana.setId(cursor.getInt(0));
                manzana.setUserid(cursor.getInt(1));
                manzana.setIdmanzana(cursor.getString(2));
                manzana.setNommanzana(cursor.getString(3));
                manzana.setIdzona(cursor.getString(4));
                manzana.setZona(cursor.getString(5));
                manzana.setUbigeo(cursor.getString(6));
                manzana.setShape(cursor.getString(7));
                manzanas.add(manzana);
            }
        }finally{
            if(cursor != null) cursor.close();
        }
        return manzanas;

    }

    public String getManzana(){
        String dato = new String();
        ArrayList<Poligono> poligonos = new ArrayList<>();
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT nommanzana FROM manzana ",null);
            while(cursor.moveToNext()){
                Poligono poligono = new Poligono();
                poligono.setManzana(cursor.getString(cursor.getColumnIndex("nommanzana")));
                poligonos.add(poligono);
            }
        }finally{
            if(cursor != null) cursor.close();
        }
        dato = String.valueOf(poligonos.get(0).getManzana());
        return dato;

    }

    public String getVersion(){
        String dato = new String();
        ArrayList<Poligono> poligonos = new ArrayList<>();
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT spatialite_version()",null);
            while(cursor.moveToNext()){
                dato = cursor.getString(0);
            }
        }finally{
            if(cursor != null) cursor.close();
        }

        return dato;

    }

    public String getArea(){
        String dato = new String();
        ArrayList<Poligono> poligonos = new ArrayList<>();
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT ST_Area(shape)*10000000000 FROM manzana WHERE id=1",null);
            while(cursor.moveToNext()){
                dato = cursor.getString(0);
            }
        }finally{
            if(cursor != null) cursor.close();
        }

        return dato;

    }

    public String getDato(){
        String dato = new String();
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT AsGeoJSON(shape,0,0) FROM manzana WHERE id=1",null);
            while(cursor.moveToNext()){
                dato = cursor.getString(0);
            }
        }finally{
            if(cursor != null) cursor.close();
        }

        return dato;

    }






    public void insertarCapa(Capa capa){
        ContentValues contentValues = capa.toValues();
        sqLiteDatabase.insert(SQLConstantes.tb_capa,null,contentValues);
    }

    public void insertarPoligono(Capa capa){
        ContentValues contentValues = capa.toValues();
        sqLiteDatabase.insert(SQLConstantes.tb_capa,null,contentValues);
    }

    public void insertManzana(Manzana manzana){
        ContentValues contentValues = manzana.toValues();
        sqLiteDatabase.insert(SQLConstantes.tb_manzana,null,contentValues);
    }

    public void deleteManzana(){
        sqLiteDatabase.execSQL("delete from "+ SQLConstantes.tb_manzana);
    }

//    public boolean isTableExists(String nombreTabla) {
//        sqLiteDatabase = new DataBaseHelper(contexto);
//        boolean isExist = false;
//        Cursor cursor = sqLiteOpenHelper.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + nombreTabla + "'", null);
//        if (cursor != null) {
//            if (cursor.getCount() > 0) {
//                isExist = true;
//            }
//            cursor.close();
//        }
//        return isExist;
//    }




}
