package com.inei.appcartoinei.modelo.DAO;

public class SQLConstantes {
    public static String DB_PATH = "/data/data/com.inei.appcartoinei/databases/";
    public static String DB_NAME = "cartoinei.sqlite";
    public static int DATABASE_VERSION = 5;
    public static String tb_permiso        ="permiso";
    public static String tb_rol_permiso    ="rol_permiso";
    public static String tb_rol             ="rol";
    public static String tb_usuario_rol     ="usuario_rol";
    public static String tb_usuario         ="usuario";
    public static String tb_manzana_captura ="manzana_captura";
    public static String tb_manzana_marco   ="manzana_marco";

    public static final String permiso_cp_idpermiso   = "idpermiso";
    public static final String permiso_cp_descripcion = "descripcion";
    public static final String permiso_cp_creado      = "creado ";
    public static final String permiso_cp_modificado  = "modificado";

    public static final String rol_permiso_cp_idrol     = "idrol";
    public static final String rol_permiso_cp_idpermiso = "idpermiso";

    public static final String rol_cp_idrol       = "idrol";
    public static final String rol_cp_descripcion = "descripcion";
    public static final String rol_cp_creado      = "creado ";
    public static final String rol_cp_modificado  = "modificado";

    public static final String usuario_rol_cp_idusuario = "idusuario";
    public static final String usuario_rol_cp_idrol     = "idrol";

    public static final String usuario_cp_idusuario  = "idusuario";
    public static final String usuario_cp_usuario    = "usuario";
    public static final String usuario_cp_clave      = "clave";
    public static final String usuario_cp_creado     = "creado ";
    public static final String usuario_cp_modificado = "modificado";


    public static final String manzana_cp_idmanzana  = "idmanzana";
    public static final String manzana_cp_iduser     = "iduser";
    public static final String manzana_cp_CCDD       = "ccdd";
    public static final String manzana_cp_CCPP       = "ccpp";
    public static final String manzana_cp_CCDI       = "ccdi";
    public static final String manzana_cp_CODZONA    = "codzona";
    public static final String manzana_cp_SUFZONA    = "sufzona";
    public static final String manzana_cp_CODMZNA    = "codmzna";
    public static final String manzana_cp_SUFMZNA    = "sufmzna";
    public static final String manzana_cp_MZNABELONG = "mznabelong";
    public static final String manzana_cp_ESTADO     = "estado";
    public static final String manzana_cp_FRENTES    = "frentes";
    public static final String manzana_cp_CARGADO    = "cargado";
    public static final String manzana_cp_shape      = "shape";

    public static final String SQL_CREATE_TABLA_MANZANA_CAPTURA =
            "CREATE TABLE " + tb_manzana_captura + "(" +
                    manzana_cp_idmanzana         + " TEXT," +
                    manzana_cp_iduser     + " INTEGER," +
                    manzana_cp_CCDD       + " TEXT," +
                    manzana_cp_CCPP       + " TEXT," +
                    manzana_cp_CCDI       + " TEXT," +
                    manzana_cp_CODZONA    + " TEXT," +
                    manzana_cp_SUFZONA    + " TEXT," +
                    manzana_cp_CODMZNA    + " TEXT," +
                    manzana_cp_SUFMZNA    + " TEXT," +
                    manzana_cp_MZNABELONG + " TEXT," +
                    manzana_cp_ESTADO     + " INTEGER," +
                    manzana_cp_FRENTES    + " INTEGER," +
                    manzana_cp_CARGADO    + " INTEGER," +
                    manzana_cp_shape      + " POLYGON"+");"
            ;

    public static final String SQL_CREATE_TABLA_MANZANA_MARCO =
            "CREATE TABLE " + tb_manzana_marco + "(" +
                    manzana_cp_idmanzana         + " TEXT," +
                    manzana_cp_iduser     + " INTEGER," +
                    manzana_cp_CCDD       + " TEXT," +
                    manzana_cp_CCPP       + " TEXT," +
                    manzana_cp_CCDI       + " TEXT," +
                    manzana_cp_CODZONA    + " TEXT," +
                    manzana_cp_SUFZONA    + " TEXT," +
                    manzana_cp_CODMZNA    + " TEXT," +
                    manzana_cp_SUFMZNA    + " TEXT," +
                    manzana_cp_shape      + " POLYGON"+");"
            ;

}
