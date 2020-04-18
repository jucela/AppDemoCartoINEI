package com.inei.appcartoinei.utils;

public class UtilsImportData {

    public static String getUltimoTresDigito(String cadena){
        String ultimo = cadena.substring(cadena.length() - 5);
        return  ultimo;
    }
}
