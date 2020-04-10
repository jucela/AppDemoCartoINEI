package com.inei.appcartoinei.utils;
import com.inei.appcartoinei.modelo.pojos.FusionItem;
import java.util.ArrayList;

public class CreateNewManzana {

    /*GENERAR ID DE MANZANA AÑADIDA*/
    public static String getNewManzanaAnadida(ArrayList<String> listaManzana){
        ArrayList<String> listaManzana1 = listaManzana;
        ArrayList<String> listaManzana2 = new ArrayList<>();
        int mayor;
        listaManzana2 = getListaManzana(listaManzana1);
        if(listaManzana2.size()>0){
            mayor = Integer.parseInt(listaManzana2.get(0));
            for(int i=0;i<listaManzana2.size();i++)
            {
                int numero = Integer.parseInt(listaManzana2.get(i));
                if (numero>mayor)
                {mayor =numero;}
            }
        }
        else {
            mayor=0;
        }
        return setDigitosCero(mayor+1);
    }

    /*GENERAR ID DE MANZANA FUSIONADA*/
    public static String getNewManzanaFusionada(ArrayList<FusionItem> listaManzana, String idManzana) {
        int menor;
        int valorid;
        int newValorId = 0;
        if (listaManzana.size() > 0) {
            if(listaManzana.get(0).getIdManzana().trim().length()==4){

                menor = Integer.parseInt(getTresDigitos(listaManzana.get(0).getIdManzana().trim()));
            }
            else {
                menor = Integer.parseInt(listaManzana.get(0).getIdManzana().trim());
            }
            for (FusionItem objeto : listaManzana) {
                int numero = Integer.parseInt(getCodManzana(objeto.getIdManzana().trim()));
                if (numero < menor) {
                    menor = numero;
                }
            }
            valorid = Integer.parseInt(getCodManzana(idManzana.trim()));
            if (menor < valorid) {
                newValorId = menor;
            } else {
                newValorId = valorid;
            }
        }
        return setDigitosCero(newValorId) + ""+getSufManzana(listaManzana,setDigitosCero(newValorId));
    }

    /*GENERAR ID'S DE MANZANA FRACCIONADA*/
    public static ArrayList<String> getNewIdManzanaFraccionada(int cantidad, String idmanzana){
        ArrayList<String> lista = new ArrayList<>();
        char valor='0';
        char letraObtenida='0';
        int  letraCastInt;
        int a = 65;
        String codmzna;

        if(idmanzana.trim().length()==4){
            letraObtenida = getUltimoDigito(idmanzana).charAt(0);
            letraCastInt = (int) letraObtenida;
            a = letraCastInt+1;
            codmzna = getTresDigitos(idmanzana).trim();
        }
        else{
            a = 65;
            codmzna=idmanzana.trim();
        }


        for (int i=0;i<cantidad;i++)
        {

            valor = (char) a;
            lista.add(codmzna+String.valueOf(valor));
            a++;
        }
        return lista;
    }

    /*GENERAR ID'S DE MANZANA FRACCIONADA*/
    public static String getNewIdManzanaReplanteada(String idmanzana){
        String newManzana="";
        char valor='0';
        char letraObtenida='0';
        int  letraCastInt;
        int a = 65;
        String codmzna;

        if(idmanzana.trim().length()==4){
            letraObtenida = getUltimoDigito(idmanzana).charAt(0);
            letraCastInt = (int) letraObtenida;
            a = letraCastInt+1;
            codmzna = getTresDigitos(idmanzana).trim();
        }
        else{
            a = 65;
            codmzna=idmanzana.trim();
        }
            valor = (char) a;
            newManzana = codmzna+String.valueOf(valor);

        return newManzana;
    }

    /*OBTENER LISTA DE MANZANAS (CODZONA)*/
    public static ArrayList<String>  getListaManzana(ArrayList<String> listaManzana){
        ArrayList<String> newlista = new ArrayList<>();
        for(int i=0;i<listaManzana.size();i++){
            if (listaManzana.get(i).length()>3)
            {newlista.add(getTresDigitos(listaManzana.get(i)));}
            else{
                newlista.add(listaManzana.get(i));
            }
        }
        return newlista;
    }

    /*OBTENER STRING DE MANZANA (CODZONA)*/
    public static String getCodManzana(String cadena){
        String resultado="";
        if (cadena.length()>3)
        {resultado=getTresDigitos(cadena);}
        else{
            resultado=cadena;
        }
        return resultado;
    }

    /*OBTENER LETRA DE SUFIJO ZONA*/
    public static String getSufManzana(ArrayList<FusionItem> listaManzana,String idmanzana){
        String resultado1 ="";
        String resultado2 ="";
        for(FusionItem objeto :listaManzana)
        {
            if(getCodManzana(objeto.getIdManzana().trim()).equals(idmanzana))
            {resultado1=objeto.getIdManzana().trim();}

        }
        if(resultado1.length()>3)
        {
            char ultimocaracter = resultado1.charAt(resultado1.length()-1);
            int dato1 = castCaracter(ultimocaracter);
            char dato2 = castNumero(dato1+1);

            resultado2 = ""+dato2;
        }
        else{
            resultado2="A";
        }
        return resultado2;
    }

    /*CASTEAR NUMERO A CHAR*/
    public static char castNumero(int numero){
        char valor = (char) numero;
        return valor;
    }

    /*CASTEAR CHAR A NUMERO*/
    public static int castCaracter(char caracter){
        int numero = (int) caracter;
        return numero;
    }

    /*ASIGNAR CEROS A CODMANZANA(INT)*/
    public static String setDigitosCero(int numero){
        int digitos =Integer.toString(numero).length();
        String newIdNumero="";
        if(digitos==1){
            newIdNumero = "00"+numero;
        }
        if(digitos==2){
            newIdNumero = "0"+numero;
        }
        if(digitos>2){
            newIdNumero = String.valueOf(numero);
        }
        return newIdNumero;
    }

    /*OBTENER 3 PRIMEROS DIGITOS DE CADENA */
    public static String getTresDigitos(String cadena){
        String ultimo = cadena.substring(0, cadena.length() - 1);
        return  ultimo;
    }

    /*OBTENER ULTIMO DIGITO DE CADENA*/
    public static String getUltimoDigito(String cadena){
        String ultimo = cadena.substring(cadena.length() - 1);
        return  ultimo;
    }
}
