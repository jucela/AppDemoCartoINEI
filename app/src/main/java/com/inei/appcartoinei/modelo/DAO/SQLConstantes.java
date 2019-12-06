package com.inei.appcartoinei.modelo.DAO;

public class SQLConstantes {
    public static String DB_PATH = "/data/data/com.inei.appcartoinei/databases/";
    public static String DB_NAME = "cartoinei.sqlite";
    public static int DATABASE_VERSION = 5;
    public static String tb_capa = "capa";
    public static String tb_poligono ="poligonos";
    public static String tb_manzana ="manzana";

    public static String capa_cp_id           = "id";
    public static String capa_cp_nombre       = "nombre";
    public static String capa_cp_descripcion  = "descripcion";
    public static String capa_cp_tipo         = "tipo";
    public static String capa_cp_srid         = "srid";
    public static String capa_cp_escalamin    = "escalamin";
    public static String capa_cp_escalamax    = "escalamax";
    public static String capa_cp_escalamineti = "escalamineti";
    public static String capa_cp_escalamaxeti = "escalamaxeti";

    public static String manzana_cp_id         = "id";
    public static String manzana_cp_iduser     = "iduser";
    public static String manzana_cp_idmanzana  = "idmanzana";
    public static String manzana_cp_nommanzana = "nommanzana";
    public static String manzana_cp_idzona     = "idzona";
    public static String manzana_cp_zona       = "zona";
    public static String manzana_cp_ubigeo     = "ubigeo";
    public static String manzana_cp_shape      = "shape";

    private static final String ID = "id";
    private static final String KEY_ID = "geometry_column";
    private static final String FLAG_EXPORT = "export";
    private static final String KEY_ID_TYPE = "POLYGON";
    private static final String FLAG_EXPORT_TYPE = "INT";
    //private static final String KEY_ID = "geometry_column";
    //private static final String KEY_ID_TYPE = "POLYGON";
    private static final String UBIGEO  = "ubigeo";
    private static final String ZONA    = "zona";
    private static final String MANZANA = "manzana";

    public static final String SQL_CREATE_TABLA_CAPA =
            "CREATE TABLE " + tb_capa + "(" +
                    capa_cp_id + " INTEGER," +
                    capa_cp_nombre + " TEXT," +
                    capa_cp_descripcion + " TEXT," +
                    capa_cp_tipo + " TEXT," +
                    capa_cp_srid + " INTEGER," +
                    capa_cp_escalamin + " INTEGER," +
                    capa_cp_escalamax + " INTEGER," +
                    capa_cp_escalamineti + " INTEGER," +
                    capa_cp_escalamaxeti + " INTEGER"+");"
            ;

    public static final String SQL_CREATE_TABLA_MANZANA =
            "CREATE TABLE " + tb_manzana + "(" +
                    manzana_cp_id         + " INTEGER," +
                    manzana_cp_iduser     + " INTEGER," +
                    manzana_cp_idmanzana  + " TEXT," +
                    manzana_cp_nommanzana + " TEXT," +
                    manzana_cp_idzona     + " TEXT," +
                    manzana_cp_zona       + " TEXT," +
                    manzana_cp_ubigeo     + " TEXT," +
                    manzana_cp_shape      + " POLYGON"+");"
            ;

    public static final String SQL_CREATE_TABLA_POLIGONO =
            "CREATE TABLE "+tb_poligono+"("+
            ID    +" INT,"+
            KEY_ID+" "+KEY_ID_TYPE+","+
            FLAG_EXPORT+" "+FLAG_EXPORT_TYPE+","+
            UBIGEO+" TEXT,"+
            ZONA+" TEXT,"+
            MANZANA+" TEXT"
            +" )";

    public static final String[] COLUMNAS_TB_CAPA = {
            capa_cp_id,
            capa_cp_nombre,
            capa_cp_descripcion,
            capa_cp_tipo,
            capa_cp_srid,
            capa_cp_escalamin,
            capa_cp_escalamax,
            capa_cp_escalamineti,
            capa_cp_escalamaxeti
    };

    public static final String[] COLUMNAS_TB_MANZANA = {
            manzana_cp_id,
            manzana_cp_iduser,
            manzana_cp_idmanzana,
            manzana_cp_nommanzana,
            manzana_cp_idzona,
            manzana_cp_zona,
            manzana_cp_ubigeo,
            manzana_cp_shape
    };
}
