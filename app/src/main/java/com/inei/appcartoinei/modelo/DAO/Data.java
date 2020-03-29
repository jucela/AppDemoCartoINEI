package com.inei.appcartoinei.modelo.DAO;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

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
    /*METODOS MANZANA CAPTURA*/

    public ArrayList<ManzanaCaptura> getAllManzanaCaptura(){
        ArrayList<ManzanaCaptura> manzanas = new ArrayList<>();
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT id,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,estado,frentes,AsGeoJSON(shape) FROM manzana_captura",null);
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
                manzana.setShape(cursor.getString(11));
                manzanas.add(manzana);
            }
        }finally{
            if(cursor != null) cursor.close();
        }
        return manzanas;

    }

    public ManzanaCaptura getManzanaCapturaXIdEstado(String idmanzana,int estado){
        ManzanaCaptura manzana = null;
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT id,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,frentes,AsGeoJSON(shape) geom FROM manzana_captura where codmzna = '"+idmanzana.trim()+"' and estado="+estado+" ",null);
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
                        cursor.getString(12)
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
            cursor = sqLiteDatabase.rawQuery("SELECT id,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,frentes,AsGeoJSON(shape) geom FROM manzana_captura where codmzna = '"+idmanzana.trim()+"' ",null);
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
                        cursor.getString(12)
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
            cursor = sqLiteDatabase.rawQuery("SELECT id,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,frentes,AsGeoJSON(shape) geom FROM manzana_captura where mznabelong = '"+mznabelong+"' ",null);
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
                        cursor.getString(12)
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
            cursor = sqLiteDatabase.rawQuery("SELECT id,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,frentes,AsGeoJSON(shape) geom FROM manzana_captura where mznabelong='"+mznabelong+"'",null);
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
                manzana.setShape(cursor.getString(12));
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

    public ArrayList<String> getListaManzanas(String idzona){
        ArrayList<String> manzanas = new ArrayList<>();
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT id,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,frentes,shape FROM manzana_captura where codzona = '"+idzona+"' ",null);
            while (cursor.moveToNext()){
                manzanas.add(cursor.getString(7));
            }
        }finally{
            if(cursor != null) cursor.close();
        }
        return manzanas;
    }

    public void insertManzanaCaptura(int id,int iduser,String ccdd,String ccpp,String ccdi,String codzona,String sufzona,String codmzna,String sufmzna,String mznabelong,int estado,int frentes,String shape){
        sqLiteDatabase.execSQL("INSERT INTO manzana_captura(id,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,frentes,shape) VALUES ("+id+","+iduser+",'"+ccdd+"','"+ccpp+"','"+ccdi+"','"+codzona+"','"+sufzona+"','"+codmzna+"','"+sufmzna+"','"+mznabelong+"','"+estado+"','"+frentes+"',"+shape+");");
    }

    public void updateManzanaCaptura(String codmzna,String mznabelong,int estado){
        sqLiteDatabase.execSQL("UPDATE manzana_captura SET estado="+estado+",mznabelong='"+mznabelong+"' WHERE codmzna='"+codmzna+"';");
    }

    public void deleteTblManzanaCaptura(){
        sqLiteDatabase.execSQL("delete from "+ SQLConstantes.tb_manzana_captura);
    }

    public void deleteManzanaCaptura(String idmanzana){
        sqLiteDatabase.execSQL("delete from "+ SQLConstantes.tb_manzana_captura +"  where codmzna='"+idmanzana+"' ");
    }

}
