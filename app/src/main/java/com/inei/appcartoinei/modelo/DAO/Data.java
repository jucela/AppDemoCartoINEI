package com.inei.appcartoinei.modelo.DAO;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import com.inei.appcartoinei.modelo.pojos.ManzanaCaptura;
import com.inei.appcartoinei.modelo.pojos.PoligonoManzana;

import org.spatialite.database.SQLiteDatabase;
import org.spatialite.database.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    public void copyTableMarco(){
        sqLiteDatabase.execSQL("insert into manzana_captura(id,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,shape) \n" +
                "select id,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,shape\n" +
                "from manzana_marco;");
    }



    /*METODOS MANZANA CAPTURA*/

    public ArrayList<ManzanaCaptura> getAllManzanaCaptura(){
        ArrayList<ManzanaCaptura> manzanas = new ArrayList<>();
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT id,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,frentes,cargado,AsGeoJSON(shape) FROM manzana_captura",null);
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
                manzana.setMznabelong(cursor.getString(9));
                manzana.setEstado(cursor.getInt(10));
                manzana.setFrentes(cursor.getInt(11));
                manzana.setCargado(cursor.getInt(12));
                manzana.setShape(cursor.getString(13));
                manzanas.add(manzana);
            }
        }finally{
            if(cursor != null) cursor.close();
        }
        return manzanas;
    }

    public ManzanaCaptura getManzanaCapturaXZonaMznaEstado(String codZona,String codMzna,String sufMzna,int estado){
        ManzanaCaptura manzana = null;
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT id,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,frentes,cargado,AsGeoJSON(shape) FROM manzana_captura where codzona = '"+codZona.trim()+"' and codmzna = '"+codMzna.trim()+"' and sufmzna = '"+sufMzna+"' and estado="+estado+";",null);
            while(cursor.moveToNext()) {
                manzana = new ManzanaCaptura(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getInt(10),
                        cursor.getInt(11),
                        cursor.getInt(12),
                        cursor.getString(13)
                );
            }
        }finally{
            if(cursor != null) cursor.close();
        }
        return manzana;
    }

    public ManzanaCaptura getManzanaCapturaXZonaMzna(String codZona,String codMzna,String sufMzna){
        ManzanaCaptura manzana = null;
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT id,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,frentes,cargado,AsGeoJSON(shape) FROM manzana_captura where codzona = '"+codZona.trim()+"' and codmzna = '"+codMzna.trim()+"' and sufmzna = '"+sufMzna+"';",null);
            while(cursor.moveToNext()) {
                manzana = new ManzanaCaptura(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getInt(10),
                        cursor.getInt(11),
                        cursor.getInt(12),
                        cursor.getString(13)
                );
            }
        }finally{
            if(cursor != null) cursor.close();
        }
        return manzana;
    }

    public ManzanaCaptura getManzanaCapturaXIdEstado(String idmanzana,int estado){
        ManzanaCaptura manzana = null;
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT id,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,frentes,cargado,AsGeoJSON(shape) geom FROM manzana_captura where codmzna = '"+idmanzana.trim()+"' and estado="+estado+" ",null);
            while(cursor.moveToNext()) {
                manzana = new ManzanaCaptura(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getInt(10),
                        cursor.getInt(11),
                        cursor.getInt(12),
                        cursor.getString(13)
                );
            }
        }finally{
            if(cursor != null) cursor.close();
        }
        return manzana;
    }

    public ManzanaCaptura getManzanaCapturaXId(String idmanzana){
        ManzanaCaptura manzana = null;
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT id,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,frentes,cargado,AsGeoJSON(shape) geom FROM manzana_captura where codmzna = '"+idmanzana.trim()+"' ",null);
            while(cursor.moveToNext()) {
                manzana = new ManzanaCaptura(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getInt(10),
                        cursor.getInt(11),
                        cursor.getInt(12),
                        cursor.getString(13)
                );
            }
        }finally{
            if(cursor != null) cursor.close();
        }
        return manzana;
    }

    public ManzanaCaptura getManzanaCapturaXZonaIdSuf(String codzona,String codmzna,String sufmzna){
        ManzanaCaptura manzana = null;
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT id,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,frentes,cargado,AsGeoJSON(shape) geom FROM manzana_captura where codzona = '"+codzona+"' and codmzna = '"+codmzna+"' and sufmzna = '"+sufmzna+"'",null);
            while(cursor.moveToNext()) {
                manzana = new ManzanaCaptura(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getInt(10),
                        cursor.getInt(11),
                        cursor.getInt(12),
                        cursor.getString(13)
                );
            }
        }finally{
            if(cursor != null) cursor.close();
        }
        return manzana;
    }

    public ManzanaCaptura getManzanaCapturaXMznabelong(String mznabelong){
        ManzanaCaptura manzana = null;
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT id,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,cargado,frentes,AsGeoJSON(shape) geom FROM manzana_captura where mznabelong = '"+mznabelong+"' ",null);
            while(cursor.moveToNext()) {
                manzana = new ManzanaCaptura(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getInt(10),
                        cursor.getInt(11),
                        cursor.getInt(12),
                        cursor.getString(13)
                );
            }
        }finally{
            if(cursor != null) cursor.close();
        }
        return manzana;
    }

    public ArrayList<ManzanaCaptura> getAllManzanaCapturaXMznabelong(String mznabelong){
        ArrayList<ManzanaCaptura> manzanas = new ArrayList<>();
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT id,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,frentes,cargado,AsGeoJSON(shape) geom FROM manzana_captura where mznabelong='"+mznabelong+"'",null);
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
                manzana.setMznabelong(cursor.getString(9));
                manzana.setEstado(cursor.getInt(10));
                manzana.setFrentes(cursor.getInt(11));
                manzana.setCargado(cursor.getInt(12));
                manzana.setShape(cursor.getString(13));
                manzanas.add(manzana);
            }
        }finally{
            if(cursor != null) cursor.close();
        }
        return manzanas;
    }

    public ArrayList<ManzanaCaptura> getAllManzanaCapturaXZonaCargado(String codZona){
        ArrayList<ManzanaCaptura> manzanas = new ArrayList<>();
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT id,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,frentes,cargado,AsGeoJSON(shape) geom FROM manzana_captura WHERE codzona='"+codZona+"' and cargado Between 1 And 2",null);
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
                manzana.setMznabelong(cursor.getString(9));
                manzana.setEstado(cursor.getInt(10));
                manzana.setFrentes(cursor.getInt(11));
                manzana.setCargado(cursor.getInt(12));
                manzana.setShape(cursor.getString(13));
                manzanas.add(manzana);
            }
        }finally{
            if(cursor != null) cursor.close();
        }
        return manzanas;
    }

    public ArrayList<ManzanaCaptura> getAllManzanaCapturaXZona(String codZona){
        ArrayList<ManzanaCaptura> manzanas = new ArrayList<>();
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT id,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,frentes,cargado,AsGeoJSON(shape) geom FROM manzana_captura WHERE codzona='"+codZona+"'",null);
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
                manzana.setSufmzna(cursor.getString(8));
                manzana.setMznabelong(cursor.getString(9));
                manzana.setEstado(cursor.getInt(10));
                manzana.setFrentes(cursor.getInt(11));
                manzana.setCargado(cursor.getInt(12));
                manzana.setShape(cursor.getString(13));
                manzanas.add(manzana);
            }
        }finally{
            if(cursor != null) cursor.close();
        }
        return manzanas;
    }

    public boolean getValidacionTabla(String tabla){
        int count = 0;
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT count(*) FROM  '"+tabla+"' ",null);
            if(cursor != null)
                if(cursor.getCount() > 0){
                    cursor.moveToFirst();
                    count = cursor.getInt(0);
                }
        }finally{
            if(cursor != null) cursor.close();
        }
        if(count>0)
            return false;
        else
            return true;
    }

    public ArrayList<String> getListaManzanasXZona(String idzona){
        ArrayList<String> manzanas = new ArrayList<>();
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT id,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,frentes,cargado,shape FROM manzana_captura where codzona = '"+idzona+"' ",null);
            while (cursor.moveToNext()){
                manzanas.add(cursor.getString(7));
            }
        }finally{
            if(cursor != null) cursor.close();
        }
        return manzanas;
    }

    public void insertManzanaCaptura(int id,int iduser,String ccdd,String ccpp,String ccdi,String codzona,String sufzona,String codmzna,String sufmzna,String mznabelong,int estado,int frentes,int cargado,String shape){
        sqLiteDatabase.execSQL("INSERT INTO manzana_captura(id,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,frentes,cargado,shape) VALUES ("+id+","+iduser+",'"+ccdd+"','"+ccpp+"','"+ccdi+"','"+codzona+"','"+sufzona+"','"+codmzna+"','"+sufmzna+"','"+mznabelong+"','"+estado+"','"+frentes+"','"+cargado+"',"+shape+");");
    }

    public void updateManzanaCaptura(String codzona,String codmzna,String sufmzna,String mznabelong,int estado,int cargado){
        sqLiteDatabase.execSQL("UPDATE manzana_captura SET mznabelong='"+mznabelong+"',estado="+estado+",cargado="+cargado+" WHERE codzona='"+codzona+"' and codmzna='"+codmzna+"' and sufmzna='"+sufmzna+"';");
    }

    public void updateManzanaCapturaXCargado(String codzona,String codmzna,String sufmzna,int cargado){
        sqLiteDatabase.execSQL("UPDATE manzana_captura SET cargado="+cargado+" WHERE codzona='"+codzona+"' and codmzna='"+codmzna+"' and sufmzna='"+sufmzna+"';");
    }

    public void deleteTblManzanaCaptura(){
        sqLiteDatabase.execSQL("delete from "+ SQLConstantes.tb_manzana_captura);
    }

    public void deleteManzanaCaptura(String codzona,String codmzna,String sufmzna){
        sqLiteDatabase.execSQL("delete from "+ SQLConstantes.tb_manzana_captura +"  where codzona='"+codzona+"' and codmzna='"+codmzna+"' and sufmzna='"+sufmzna+"';");
    }

    /*MARCO*/
    public void insertManzanaMarco(int id,int iduser,String ccdd,String ccpp,String ccdi,String codzona,String sufzona,String codmzna,String sufmzna,String shape){
        sqLiteDatabase.execSQL("INSERT INTO manzana_marco(id,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,shape) VALUES ("+id+","+iduser+",'"+ccdd+"','"+ccpp+"','"+ccdi+"','"+codzona+"','"+sufzona+"','"+codmzna+"','"+sufmzna+"',"+shape+");");
    }

    public void deleteTblManzanaMarco(){
        sqLiteDatabase.execSQL("delete from "+ SQLConstantes.tb_manzana_marco);
    }


}
