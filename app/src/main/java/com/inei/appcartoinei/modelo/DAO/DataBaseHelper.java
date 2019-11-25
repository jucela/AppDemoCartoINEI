package com.inei.appcartoinei.modelo.DAO;
import android.content.Context;

import org.spatialite.database.SQLiteDatabase;
import org.spatialite.database.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
    private final Context myContext;
    private SQLiteDatabase myDatabase;

    public DataBaseHelper(Context context) {
        super(context,SQLConstantes.DB_NAME,null,5);
        this.myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQLConstantes.SQL_CREATE_TABLA_CAPA);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+SQLConstantes.tb_capa);
        onCreate(sqLiteDatabase);
    }
}
