package com.inei.appcartoinei.modelo.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.inei.appcartoinei.modelo.pojos.Manzana;

import org.json.JSONException;
import org.json.JSONObject;
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
    }

//    public void createDataBase() throws  IOException {
//        boolean dbExist = checkDataBase();
//        Log.i("Datos",""+dbExist);
//        if(!dbExist){
//
//        }
//        else{
//            sqLiteDatabase.execSQL(SQLConstantes.SQL_CREATE_TABLA_CAPA);
//            sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
//            sqLiteDatabase.close();
//        }
//    }

    public void open() throws SQLException {
        String myPath = SQLConstantes.DB_PATH + SQLConstantes.DB_NAME;
        sqLiteDatabase = SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READWRITE);
    }

    public synchronized void close(){
        if(sqLiteDatabase != null){
            sqLiteDatabase.close();
        }
    }

//    public boolean checkDataBase(){
//        //sqLiteDatabase =null;
//        try{
//            String myPath = SQLConstantes.DB_PATH + SQLConstantes.DB_NAME;
//            sqLiteDatabase = SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READWRITE);
//            //sqLiteDatabase.close();
//        }catch (Exception e){
//            File dbFile = new File(SQLConstantes.DB_PATH + SQLConstantes.DB_NAME);
//            return dbFile.exists();
//        }
//        if (sqLiteDatabase != null) sqLiteDatabase.close();
//        //Si sqlLiteDatabase es diferente de nulo,Devuelve true : Devuelve false.
//        return sqLiteDatabase != null ? true : false;
//    }

    /*METODOS MANZANA*/
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

    public ArrayList<String> getAllShapeManzana(){
        ArrayList<String> listashape = new ArrayList<>();
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT id,iduser,idmanzana,nommanzana,idzona,zona,ubigeo,AsGeoJSON(shape) geom FROM manzana ",null);
            while(cursor.moveToNext()){
                String shape = cursor.getString(7);
                listashape.add(shape);
            }
        }finally{
            if(cursor != null) cursor.close();
        }
        return listashape;
    }

    public void insertManzana(int id,int iduser,String idmanzana,String nommanzana,String idzona,String zona,String ubigeo,String shape){
        sqLiteDatabase.execSQL("INSERT INTO manzana(id,iduser,idmanzana,nommanzana,idzona,zona,ubigeo,shape) VALUES ("+id+","+iduser+",'"+idmanzana+"','"+nommanzana+"','"+idzona+"','"+zona+"','"+ubigeo+"',"+shape+");");
    }

    public String getArea(){
        String dato = new String();
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

    public void deleteTblManzana(){
        sqLiteDatabase.execSQL("delete from "+ SQLConstantes.tb_manzana);
    }

    /*METODOS VIVIENDA*/
    public void insertVivienda(int id,int iduser,String idviv,String idmanzana,String nommanzana,String idzona,String zona,String ubigeo,int nrofrente,int nropuerta,String descripcion,String shape){
        sqLiteDatabase.execSQL("INSERT INTO vivienda(id,iduser,idviv,idmanzana,nommanzana,idzona,zona,ubigeo,nrofrente,nropuerta,descripcion,shape) VALUES ("+id+","+iduser+",'"+idviv+"','"+idmanzana+"','"+nommanzana+"','"+idzona+"','"+zona+"','"+ubigeo+"','"+nrofrente+"','"+nropuerta+"','"+descripcion+"',"+shape+");");
    }

    public ArrayList<String> getAllShapeVivienda(){
        ArrayList<String> listashape = new ArrayList<>();
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT id,iduser,idviv,idmanzana,nommanzana,idzona,zona,ubigeo,nrofrente,nropuerta,descripcion,AsGeoJSON(shape) geom FROM vivienda ",null);
            while(cursor.moveToNext()){
                String shape = cursor.getString(11);
                listashape.add(shape);
            }
        }finally{
            if(cursor != null) cursor.close();
        }
        return listashape;
    }





}
