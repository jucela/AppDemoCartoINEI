package com.inei.appcartoinei.modelo.DAO;

public class SQLConstantes {
    public static String DB_PATH = "/data/data/com.inei.appcartoinei/databases/";
    public static String DB_NAME = "cartoinei.sqlite";
    public static int DATABASE_VERSION = 5;
    public static String tb_manzana ="manzana";
    public static String tb_vivienda ="vivienda";
    public static String tb_eje_vial ="eje_vial";

    public static final String manzana_cp_id         = "id";
    public static final String manzana_cp_iduser     = "iduser";
    public static final String manzana_cp_idmanzana  = "idmanzana";
    public static final String manzana_cp_nommanzana = "nommanzana";
    public static final String manzana_cp_idzona     = "idzona";
    public static final String manzana_cp_zona       = "zona";
    public static final String manzana_cp_ubigeo     = "ubigeo";
    public static final String manzana_cp_shape      = "shape";

    public static String vivienda_cp_id          = "id";
    public static String vivienda_cp_iduser      = "iduser";
    public static String vivienda_cp_idviv       = "idviv";
    public static String vivienda_cp_idmanzana   = "idmanzana";
    public static String vivienda_cp_nommanzana  = "nommanzana";
    public static String vivienda_cp_idzona      = "idzona";
    public static String vivienda_cp_zona        = "zona";
    public static String vivienda_cp_ubigeo      = "ubigeo";
    public static String vivienda_cp_nrofrente   = "nrofrente";
    public static String vivienda_cp_nropuerta   = "nropuerta";
    public static String vivienda_cp_descripcion = "descripcion";
    public static String vivienda_cp_shape       = "shape";

    public static String ejevial_cp_id          = "id";
    public static String ejevial_cp_iduser      = "iduser";
    public static String ejevial_cp_idcategoria = "idcategoria";
    public static String ejevial_cp_nombrevia   = "nombrevia";
    public static String ejevial_cp_nombrealt   = "nombrealt";
    public static String ejevial_cp_ubigeo      = "ubigeo";
    public static String ejevial_cp_shape       = "shape";


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

    public static final String SQL_CREATE_TABLA_VIVIENDA =
            "CREATE TABLE " + tb_vivienda + "(" +
                    vivienda_cp_id          + " INTEGER," +
                    vivienda_cp_iduser      + " INTEGER," +
                    vivienda_cp_idviv       + " TEXT," +
                    vivienda_cp_idmanzana   + " TEXT," +
                    vivienda_cp_nommanzana  + " TEXT," +
                    vivienda_cp_idzona      + " TEXT," +
                    vivienda_cp_zona        + " TEXT," +
                    vivienda_cp_ubigeo      + " TEXT," +
                    vivienda_cp_nrofrente   + " INTEGER," +
                    vivienda_cp_nropuerta   + " INTEGER," +
                    vivienda_cp_descripcion + " TEXT," +
                    vivienda_cp_shape      + " POINT"+");"
            ;

    public static final String SQL_CREATE_TABLA_EJE_VIAL =
            "CREATE TABLE " + tb_eje_vial + "(" +
                    ejevial_cp_id          + " INTEGER," +
                    ejevial_cp_iduser      + " INTEGER," +
                    ejevial_cp_idcategoria + " INTEGER," +
                    ejevial_cp_nombrevia   + " TEXT," +
                    ejevial_cp_nombrealt   + " TEXT," +
                    ejevial_cp_ubigeo      + " TEXT," +
                    ejevial_cp_shape       + " POLYGON"+");"
            ;


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
