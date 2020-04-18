package com.inei.appcartoinei.modelo.DAO;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import com.inei.appcartoinei.modelo.pojos.ManzanaCaptura;

import org.spatialite.database.SQLiteDatabase;
import org.spatialite.database.SQLiteOpenHelper;

import java.io.IOException;
import java.sql.Blob;
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
        sqLiteDatabase.execSQL("insert into manzana_captura(idmanzana,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,shape) \n" +
                "select idmanzana,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,shape\n" +
                "from manzana_marco;");
    }

    /*METODOS MANZANA CAPTURA*/

    public ManzanaCaptura getManzanaCaptura(String codZona,String sufZona,String codMzna,String sufMzna){
        ManzanaCaptura manzana = null;
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT idmanzana,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,frentes,cargado,AsGeoJSON(shape) FROM manzana_captura where codzona = '"+codZona+"' and sufzona = '"+sufZona+"' and codmzna = '"+codMzna+"' and sufmzna = '"+sufMzna+"';",null);
            while(cursor.moveToNext()) {
                manzana = new ManzanaCaptura(
                        cursor.getString(0),
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

    public ManzanaCaptura getManzanaCapturaXZonaMzna(String codZona,String sufZona,String codMzna,String sufMzna){
        ManzanaCaptura manzana = null;
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT idmanzana,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,frentes,cargado,AsGeoJSON(shape) FROM manzana_captura where codzona = '"+codZona+"' and sufzona = '"+sufZona+"' and codmzna = '"+codMzna+"' and sufmzna = '"+sufMzna+"';",null);
            while(cursor.moveToNext()) {
                manzana = new ManzanaCaptura(
                        cursor.getString(0),
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

    public ManzanaCaptura getManzanaCapturaXMzna(String codZona,String sufZona,String codMzna,String sufMzna){
        ManzanaCaptura manzana = null;
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT idmanzana,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,frentes,cargado,AsGeoJSON(shape) geom FROM manzana_captura where codzona = '"+codZona+"' and sufzona = '"+sufZona+"' and codmzna = '"+codMzna+"' and sufmzna = '"+sufMzna+"'",null);
            while(cursor.moveToNext()) {
                manzana = new ManzanaCaptura(
                        cursor.getString(0),
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
            cursor = sqLiteDatabase.rawQuery("SELECT idmanzana,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,cargado,frentes,AsGeoJSON(shape) geom FROM manzana_captura where mznabelong = '"+mznabelong+"' ",null);
            while(cursor.moveToNext()) {
                manzana = new ManzanaCaptura(
                        cursor.getString(0),
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

    public ArrayList<ManzanaCaptura> getAllManzanaCaptura(){
        ArrayList<ManzanaCaptura> manzanas = new ArrayList<>();
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT idmanzana,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,frentes,cargado,AsGeoJSON(shape) FROM manzana_captura",null);
            while(cursor.moveToNext()){
                ManzanaCaptura manzana = new ManzanaCaptura();
                manzana.setIdmanzana(cursor.getString(0));
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

    public ArrayList<ManzanaCaptura> getAllManzanaCapturaXMznabelong(String codZona,String sufZona,String mznabelong){
        ArrayList<ManzanaCaptura> manzanas = new ArrayList<>();
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT idmanzana,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,frentes,cargado,AsGeoJSON(shape) geom FROM manzana_captura where codzona = '"+codZona+"' and sufzona = '"+sufZona+"' and mznabelong='"+mznabelong+"'",null);
            while(cursor.moveToNext()){
                ManzanaCaptura manzana = new ManzanaCaptura();
                manzana.setIdmanzana(cursor.getString(0));
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

    public ArrayList<ManzanaCaptura> getAllManzanaCapturaXZonaCargado(String codZona,String sufZona){
        ArrayList<ManzanaCaptura> manzanas = new ArrayList<>();
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT idmanzana,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,frentes,cargado,AsGeoJSON(shape) geom FROM manzana_captura WHERE codzona='"+codZona+"' and sufzona='"+sufZona+"' and cargado Between 1 And 2",null);
            while(cursor.moveToNext()){
                ManzanaCaptura manzana = new ManzanaCaptura();
                manzana.setIdmanzana(cursor.getString(0));
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

    public ArrayList<ManzanaCaptura> getAllManzanaCapturaTrabajadas(String codZona,String sufZona){
        ArrayList<ManzanaCaptura> manzanas = new ArrayList<>();
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT idmanzana,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,frentes,cargado,AsGeoJSON(shape) geom FROM manzana_captura WHERE codzona='"+codZona+"' and sufzona='"+sufZona+"' and cargado Between 1 And 3",null);
            while(cursor.moveToNext()){
                ManzanaCaptura manzana = new ManzanaCaptura();
                manzana.setIdmanzana(cursor.getString(0));
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

    public ArrayList<ManzanaCaptura> getAllManzanaCapturaTrabajadasXCampo(String codZona,String sufZona,String campo,int tipo){
        ArrayList<ManzanaCaptura> manzanas = new ArrayList<>();
        String estado="";
        if(tipo==0){
            estado="ASC";
        }
        else{
            estado="DESC";
        }
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT idmanzana,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,frentes,cargado,AsGeoJSON(shape) geom FROM manzana_captura WHERE codzona='"+codZona+"' and sufzona='"+sufZona+"' and cargado Between 1 And 3 ORDER BY "+campo+" "+estado+"",null);
            while(cursor.moveToNext()){
                ManzanaCaptura manzana = new ManzanaCaptura();
                manzana.setIdmanzana(cursor.getString(0));
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

    public ArrayList<ManzanaCaptura> getAllManzanaCapturaXZona(String codZona){
        ArrayList<ManzanaCaptura> manzanas = new ArrayList<>();
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.rawQuery("SELECT idmanzana,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,frentes,cargado,AsGeoJSON(shape) geom FROM manzana_captura WHERE codzona='"+codZona+"'",null);
            while(cursor.moveToNext()){
                ManzanaCaptura manzana = new ManzanaCaptura();
                manzana.setIdmanzana(cursor.getString(0));
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
            cursor = sqLiteDatabase.rawQuery("SELECT idmanzana,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,frentes,cargado,shape FROM manzana_captura where codzona = '"+idzona+"' ",null);
            while (cursor.moveToNext()){
                manzanas.add(cursor.getString(7));
            }
        }finally{
            if(cursor != null) cursor.close();
        }
        return manzanas;
    }

    public void insertManzanaCaptura(String idmanzana,int iduser,String ccdd,String ccpp,String ccdi,String codzona,String sufzona,String codmzna,String sufmzna,String mznabelong,int estado,int frentes,int cargado,String shape){
        sqLiteDatabase.execSQL("INSERT INTO manzana_captura(idmanzana,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,frentes,cargado,shape) VALUES ('"+idmanzana+"',"+iduser+",'"+ccdd+"','"+ccpp+"','"+ccdi+"','"+codzona+"','"+sufzona+"','"+codmzna+"','"+sufmzna+"','"+mznabelong+"','"+estado+"','"+frentes+"','"+cargado+"',"+shape+");");
    }

    public void updateManzanaCaptura(String codZona,String sufZona,String codMzna,String sufMzna,String mznaBelong,int estado,int cargado){
        sqLiteDatabase.execSQL("UPDATE manzana_captura SET mznabelong='"+mznaBelong+"',estado="+estado+",cargado="+cargado+" WHERE codzona='"+codZona+"' and sufzona='"+sufZona+"' and codmzna='"+codMzna+"' and sufmzna='"+sufMzna+"';");
    }

    public void updateManzanaCapturaXCargado(String codZona,String sufZona,String codMzna,String sufMzna,int cargado){
        sqLiteDatabase.execSQL("UPDATE manzana_captura SET cargado="+cargado+" WHERE codzona='"+codZona+"' and sufzona='"+sufZona+"' and codmzna='"+codMzna+"' and sufmzna='"+sufMzna+"';");
    }

    public void deleteTblManzanaCaptura(){
        sqLiteDatabase.execSQL("delete from "+ SQLConstantes.tb_manzana_captura);
    }

    public void deleteManzanaCaptura(String codZona,String sufZona,String codMzna,String sufMzna){
        sqLiteDatabase.execSQL("delete from "+ SQLConstantes.tb_manzana_captura +"  where codzona='"+codZona+"' and sufzona='"+sufZona+"' and codmzna='"+codMzna+"' and sufmzna='"+sufMzna+"';");
    }

    /*METODOS MANZANA MARCO*/
    public void insertManzanaMarco(String idmanzana,int iduser,String ccdd,String ccpp,String ccdi,String codzona,String sufzona,String codmzna,String sufmzna,String shape){
        sqLiteDatabase.execSQL("INSERT INTO manzana_marco(idmanzana,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,shape) VALUES ('"+idmanzana+"',"+iduser+",'"+ccdd+"','"+ccpp+"','"+ccdi+"','"+codzona+"','"+sufzona+"','"+codmzna+"','"+sufmzna+"',"+shape+");");
    }

    public void deleteTblManzanaMarco(){
        sqLiteDatabase.execSQL("delete from "+ SQLConstantes.tb_manzana_marco);
    }

    /*PRUEBA*/
    public String getPrueba(){
        String manzanas = "";
        Cursor cursor = null;
        try{
            //cursor = sqLiteDatabase.rawQuery("SELECT idmanzana,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,frentes,cargado,AsText(ST_Centroid(shape)) FROM manzana_captura where codzona='001' and codmzna='001' ",null);
            cursor = sqLiteDatabase.rawQuery("SELECT idmanzana,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,frentes,cargado,AsText(shape) FROM manzana_captura where codzona='001' and codmzna='001' ",null);
            while(cursor.moveToNext()){
                manzanas=cursor.getString(13);
            }
        }finally{
            if(cursor != null) cursor.close();
        }
        return manzanas;
    }
    public String getPruebaBlob(){
        String manzanas = "";
        Cursor cursor = null;
        try{
            //cursor = sqLiteDatabase.rawQuery("SELECT idmanzana,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,frentes,cargado,AsText(ST_Centroid(shape)) FROM manzana_captura where codzona='001' and codmzna='001' ",null);
            cursor = sqLiteDatabase.rawQuery("SELECT idmanzana,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,frentes,cargado,AsText(ST_Union(shape)) FROM manzana_captura where codzona='001' and codmzna='002' ",null);
            while(cursor.moveToNext()){
                manzanas= cursor.getString(13);
            }
        }finally{
            if(cursor != null) cursor.close();
        }
        return manzanas;
    }

    public ArrayList<ManzanaCaptura> getPolygonGeometria(String codmzna){
        ArrayList<ManzanaCaptura> manzanas = new ArrayList<>();
        Cursor cursor = null;
        try{
            //cursor = sqLiteDatabase.rawQuery("SELECT idmanzana,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,frentes,cargado,ST_Union(AsText("+getGeometria("048")+"),AsText("+getGeometria("051")+")) FROM manzana_captura where codzona='001' and codmzna='"+codmzna+"' ",null);
            cursor = sqLiteDatabase.rawQuery("SELECT idmanzana,iduser,ccdd,ccpp,ccdi,codzona,sufzona,codmzna,sufmzna,mznabelong,estado,frentes,cargado,AsText(shape) FROM manzana_captura where codzona='001' and codmzna='"+codmzna+"' ",null);
            while(cursor.moveToNext()){
                ManzanaCaptura manzana = new ManzanaCaptura();
                manzana.setIdmanzana(cursor.getString(0));
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

    public String getDato(){
        String manzana = null;
        Cursor cursor = null;
        try{
            //"GeomFromText('POLYGON((" + formatGeom(puntos) + "))',4326)"
            //cursor = sqLiteDatabase.rawQuery("SELECT ST_AsText(ST_Union(ST_GeomFromText('POINT(1 2)'),ST_GeomFromText('POINT(1 2)') ) );",null);
            String query1 = "SELECT AsText(GUnion(CastToLinestring(GeomFromText('"+getPolygonGeometria("048").get(0).getShape()+"',4326)),CastToLinestring(GeomFromText('"+getPolygonGeometria("051").get(0).getShape()+"',4326))));";
            String query11 ="SELECT AsText(GUnion(GeomFromText('POLYGON((-12.069939 -77.047833, -12.069079 -77.047143, -12.068399 -77.048017, -12.069252 -77.048717, -12.069939 -77.047833))',4326),GeomFromText('POLYGON((-12.070917 -77.048675, -12.070056 -77.047948, -12.069369 -77.048816, -12.07025 -77.049553, -12.070917 -77.048675))',4326)));";
            String query12 ="SELECT ST_AsText(ST_Union(ST_GeomFromText('POLYGON((-12.069939 -77.047833, -12.069079 -77.047143, -12.068399 -77.048017, -12.069252 -77.048717, -12.069939 -77.047833))'),ST_GeomFromText('POLYGON((-12.070917 -77.048675, -12.070056 -77.047948, -12.069369 -77.048816, -12.07025 -77.049553, -12.070917 -77.048675))')));";
            String query2 = "SELECT AsText(GUnion(GeomFromText('POLYGON((-12.069939 -77.047833, -12.069079 -77.047143, -12.068399 -77.048017, -12.069252 -77.048717, -12.069939 -77.047833, -12.070917 -77.048675, -12.070056 -77.047948, -12.069369 -77.048816, -12.07025 -77.049553, -12.070917 -77.048675))',4326)));";
            String query3 = "SELECT AsText(GUnion(GeomFromText('MULTIPOINT(-12.069939 -77.047833, -12.069079 -77.047143, -12.068399 -77.048017, -12.069252 -77.048717, -12.069939 -77.047833, -12.070917 -77.048675, -12.070056 -77.047948, -12.069369 -77.048816, -12.07025 -77.049553, -12.070917 -77.048675)',4326)));";
            String query4 = "SELECT ST_AsText(ST_Union(ST_GeomFromText( 'POLYGON (( 320 320, 320 400, 380 400, 380 320, 320 320 ))'),ST_GeomFromText(' POLYGON (( 340 280, 340 360, 400 360, 400 280, 340 280 ))')))";
            //String query3 = "SELECT AsText(GUnion(POLYGON((-12.069939 -77.047833, -12.069079 -77.047143, -12.068399 -77.048017, -12.069252 -77.048717, -12.069939 -77.047833, -12.070917 -77.048675, -12.070056 -77.047948, -12.069369 -77.048816, -12.07025 -77.049553, -12.070917 -77.048675))));";
            Log.e("prueba_query:",""+query4);
            cursor = sqLiteDatabase.rawQuery(query4,null);
            //cursor = sqLiteDatabase.rawQuery("SELECT AsText(ST_Union(GeomFromText('"+getPolygonGeometria("048").get(0).getShape()+"',4326)));",null);
            Log.e("prueba_data:",""+(String) cursor.toString());
            while(cursor.moveToNext()) {
                 manzana =  cursor.getString(0);
            }
        }finally{
            if(cursor != null) cursor.close();
        }
        return manzana;
    }


}
