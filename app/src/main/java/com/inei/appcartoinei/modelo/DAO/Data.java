package com.inei.appcartoinei.modelo.DAO;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import com.inei.appcartoinei.modelo.pojos.Manzana;
import com.inei.appcartoinei.modelo.pojos.ManzanaCaptura;
import com.inei.appcartoinei.modelo.pojos.PoligonoManzana;

import org.spatialite.database.SQLiteDatabase;
import org.spatialite.database.SQLiteOpenHelper;

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

    public void open() throws SQLException {
        String myPath = SQLConstantes.DB_PATH + SQLConstantes.DB_NAME;
        sqLiteDatabase = SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READWRITE);
    }

    public synchronized void close(){
        if(sqLiteDatabase != null){
            sqLiteDatabase.close();
        }
    }


    /*METODOS MANZANA*/
    public ArrayList<Manzana> getAllManzana(){
        ArrayList<Manzana> manzanas = new ArrayList<>();
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT id,iduser,idmanzana,nommanzana,idzona,zona,ubigeo,ST_Area(shape)*10000000000 FROM manzana ",null);
            while(cursor.moveToNext()){
                Manzana manzana = new Manzana();
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

    public void deleteTblManzana(){
        sqLiteDatabase.execSQL("delete from "+ SQLConstantes.tb_manzana);
    }

    /*METODOS VIVIENDA*/

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

    public void insertVivienda(int id,int iduser,String idviv,String idmanzana,String nommanzana,String idzona,String zona,String ubigeo,int nrofrente,int nropuerta,String descripcion,String shape){
        sqLiteDatabase.execSQL("INSERT INTO vivienda(id,iduser,idviv,idmanzana,nommanzana,idzona,zona,ubigeo,nrofrente,nropuerta,descripcion,shape) VALUES ("+id+","+iduser+",'"+idviv+"','"+idmanzana+"','"+nommanzana+"','"+idzona+"','"+zona+"','"+ubigeo+"','"+nrofrente+"','"+nropuerta+"','"+descripcion+"',"+shape+");");
    }

    public void deleteTblVivienda(){
        sqLiteDatabase.execSQL("delete from "+ SQLConstantes.tb_vivienda);
    }

    /*METODOS MANZANA CAPTURA*/
    public ArrayList<ManzanaCaptura> getAllManzanaCapturaEstado(){
        ArrayList<ManzanaCaptura> manzanas = new ArrayList<>();
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT id,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,estado,frentes,shape FROM manzana_captura where estado=0",null);
            while(cursor.moveToNext()){
                ManzanaCaptura manzana = new ManzanaCaptura();
                manzana.setId(cursor.getInt(0));
                manzana.setIduser(cursor.getInt(1));
                manzana.setCcdd(cursor.getString(2));
                manzana.setCcpp(cursor.getString(3));
                manzana.setCcdi(cursor.getString(4));
                manzana.setCodzona(cursor.getString(5));
                manzana.setSufzona(cursor.getString(6));
                manzana.setCodmzna(cursor.getString(7));
                manzana.setSufzona(cursor.getString(8));
                manzana.setEstado(cursor.getInt(9));
                manzana.setFrentes(cursor.getInt(10));
//                manzana.setShape(cursor.getString(11));
                manzanas.add(manzana);
            }
        }finally{
            if(cursor != null) cursor.close();
        }
        return manzanas;

    }

    public ArrayList<ManzanaCaptura> getAllManzanaCaptura(){
        ArrayList<ManzanaCaptura> manzanas = new ArrayList<>();
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT id,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,estado,frentes,shape FROM manzana_captura",null);
            while(cursor.moveToNext()){
                ManzanaCaptura manzana = new ManzanaCaptura();
                manzana.setId(cursor.getInt(0));
                manzana.setIduser(cursor.getInt(1));
                manzana.setCcdd(cursor.getString(2));
                manzana.setCcpp(cursor.getString(3));
                manzana.setCcdi(cursor.getString(4));
                manzana.setCodzona(cursor.getString(5));
                manzana.setSufzona(cursor.getString(6));
                manzana.setCodmzna(cursor.getString(7));
                manzana.setSufzona(cursor.getString(8));
                manzana.setEstado(cursor.getInt(9));
                manzana.setFrentes(cursor.getInt(10));
                //manzana.setShape(cursor.getString(11));
                manzanas.add(manzana);
            }
        }finally{
            if(cursor != null) cursor.close();
        }
        return manzanas;

    }

    public ArrayList<String> getAllShapeManzanaCaptura(){
        ArrayList<String> listashape = new ArrayList<>();
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT id,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,estado,frentes,AsGeoJSON(shape) geom FROM manzana_captura ",null);
            while(cursor.moveToNext()){
                String shape = cursor.getString(11);
                listashape.add(shape);
            }
        }finally{
            if(cursor != null) cursor.close();
        }
        return listashape;
    }

    public boolean getEstateManzana(String idzona,String idmanzana){
        boolean estado = false;
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT * FROM manzana_captura where codzona = '"+idzona+"' and codmzna = '"+idmanzana.trim()+"' and estado > 0  ",null);
            if(cursor.getCount()>0){
                estado=true;
            }
        }finally{
            if(cursor != null) cursor.close();
        }
        return estado;

    }

    public void insertManzanaCaptura(int id,int iduser,String ccdd,String ccpp,String ccdi,String codzona,String sufzona,String codmzna,String sufmzna,int estado,int frentes,String shape){
        sqLiteDatabase.execSQL("INSERT INTO manzana_captura(id,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,estado,frentes,shape) VALUES ("+id+","+iduser+",'"+ccdd+"','"+ccpp+"','"+ccdi+"','"+codzona+"','"+sufzona+"','"+codmzna+"','"+sufmzna+"','"+estado+"','"+frentes+"',"+shape+");");
    }

    public void updateManzanaCaptura(String codmzna,int estado){
        sqLiteDatabase.execSQL("UPDATE manzana_captura SET estado="+estado+" WHERE codmzna='"+codmzna+"';");
    }

    public void deleteTblManzanaCaptura(){
        sqLiteDatabase.execSQL("delete from "+ SQLConstantes.tb_manzana_captura);
    }



    /*PRUEBAS*/
    public ArrayList<PoligonoManzana> getAllObjectShapeManzanaCaptura(){
        ArrayList<PoligonoManzana> listashape = new ArrayList<>();
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT id,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,estado,frentes,AsGeoJSON(shape) geom FROM manzana_captura ",null);
            while(cursor.moveToNext()){
                PoligonoManzana manzana = new PoligonoManzana();
                manzana.setIdzona(cursor.getString(5));
                manzana.setIdmanzana(cursor.getString(7));
                manzana.setShape(cursor.getString(11));
                listashape.add(manzana);
            }
        }finally{
            if(cursor != null) cursor.close();
        }
        return listashape;
    }



}
