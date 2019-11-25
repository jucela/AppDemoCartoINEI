package com.inei.appcartoinei.modelo.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import com.inei.appcartoinei.modelo.pojos.Capa;

import org.spatialite.database.SQLiteDatabase;
import org.spatialite.database.SQLiteOpenHelper;

import java.io.File;
import java.io.IOException;

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
    public void insertarDatos(Capa capa){
        ContentValues contentValues = capa.toValues();
        sqLiteDatabase.insert(SQLConstantes.tb_capa,null,contentValues);
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
