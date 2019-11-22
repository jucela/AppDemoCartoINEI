package com.inei.appcartoinei.modelo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ConexionSqlLiteHelper extends SQLiteOpenHelper {

    final String CREAR_TABLA = "CREATE TABLE ";
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "DB_EJEMPLO";
    private static final String TABLE_NAME = "TABLA_EJEMPLO";
    private static final String KEY_ID = "id";
    private static final String KEY_ID_TYPE = "INTEGER";


    public ConexionSqlLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATION_TABLE ="CREATE TABLE "+TABLE_NAME+"("+ KEY_ID+" "+KEY_ID_TYPE+")";
        db.execSQL(CREATION_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }


}
