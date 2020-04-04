package com.inei.appcartoinei.activities;

import com.google.android.gms.maps.model.LatLng;
import com.inei.appcartoinei.modelo.pojos.FusionItem;
import com.inei.appcartoinei.modelo.pojos.ManzanaReplanteo;

import java.util.ArrayList;
import java.util.Collections;

public class pruebas {

    public static char castNumero(int numero){
        char valor = (char) numero;
        return valor;
    }

    public static int castCaracter(char caracter){
        int numero = (int) caracter;
        return numero;
    }



    public static ArrayList<String> replantear(int cantidad, String idmanzana){
        ArrayList<String> lista = new ArrayList<>();
        char valor='0';
        int a = 65;
        for (int i=0;i<cantidad;i++)
        {
           valor = (char) a;
           lista.add(idmanzana+String.valueOf(valor));
           a++;
        }
        return lista;
    }

    public static ManzanaReplanteo  pruebaReplanteo(){
        ArrayList<LatLng> listita = new ArrayList<>();
        listita.add(new LatLng(0,0));
        listita.add(new LatLng(1,1));
        listita.add(new LatLng(2,2));

        ManzanaReplanteo manzanaReplanteo = new ManzanaReplanteo();
        manzanaReplanteo.setIdManzana("041");
        manzanaReplanteo.setLista(listita);
        return  manzanaReplanteo;
    }

    public static ArrayList<ManzanaReplanteo>  pruebaReplanteo2(){
        ArrayList<ManzanaReplanteo> lista = new ArrayList<>();
        ArrayList<LatLng> listita1 = new ArrayList<>();
        listita1.add(new LatLng(0,0));
        listita1.add(new LatLng(1,1));
        listita1.add(new LatLng(2,2));

        ArrayList<LatLng> listita2 = new ArrayList<>();
        listita2.add(new LatLng(3,3));
        listita2.add(new LatLng(4,4));
        listita2.add(new LatLng(5,5));

        ManzanaReplanteo manzanaReplanteo1 = new ManzanaReplanteo();
        manzanaReplanteo1.setIdManzana("041");
        manzanaReplanteo1.setLista(listita1);

        ManzanaReplanteo manzanaReplanteo2 = new ManzanaReplanteo();
        manzanaReplanteo2.setIdManzana("042");
        manzanaReplanteo2.setLista(listita2);

        lista.add(manzanaReplanteo1);
        lista.add(manzanaReplanteo2);
        return  lista;
    }
    /*045*/

    public static void negocio(int cantidad,String idManzana){
        int contadorManzana=0;
        /*INICIO GENERAL*/
        ArrayList<LatLng> puntos = new ArrayList<>();
        puntos.add(new LatLng(0,0));
        puntos.add(new LatLng(1,1));
        puntos.add(new LatLng(2,2));
        /*FIN GENERAL*/
        ArrayList<ManzanaReplanteo> listaManzanasMemoria = new ArrayList<>();

        ArrayList<String> listaManzanasNuevas = replantear(cantidad,idManzana);
        String idManzanaNueva;
        final int CANTIDAD= cantidad;
        if(contadorManzana<CANTIDAD){
           idManzanaNueva = listaManzanasNuevas.get(contadorManzana);
           listaManzanasMemoria.add(new ManzanaReplanteo(idManzanaNueva,puntos));
           contadorManzana++;
           //limpiarpoligono
        }
        else{
            //Desaparecer botones de edicion
            //mostrar boton de guardado
            for (int i=0;i<listaManzanasMemoria.size();i++){
                System.out.println ("Insertar IdManzana:"+listaManzanasMemoria.get(i).getIdManzana());
                System.out.println ("Insertar Arraylist Puntos:"+listaManzanasMemoria.get(i).getLista());
            }
            //limpiar
        }

    }

    public static String getNewManzana(ArrayList<FusionItem> listaManzana, String idManzana) {

        int menor;
        int valorid;
        int newValorId = 0;
        if (listaManzana.size() > 0) {
            menor = Integer.parseInt(listaManzana.get(0).getIdManzana().trim());
            for (FusionItem objeto : listaManzana) {
                int numero = Integer.parseInt(objeto.getIdManzana().trim());
                if (numero < menor) {
                    menor = numero;
                }
            }
            valorid = Integer.parseInt(idManzana.trim());
            if (menor < valorid) {
                newValorId = menor;
            } else {
                newValorId = valorid;
            }
        }

        return "0" + newValorId + "A";
    }

    public static String setDigitos(int numero){
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

    public static boolean checkNumero(String numero){
        try{
            Integer.parseInt(numero);
            return true;
        }catch(NumberFormatException e){
            return false;
        }
    }

    public static ArrayList<String>  filtrarNumero(ArrayList<String> lista){
        ArrayList<String> newlista = new ArrayList<>();
         for(int i=0;i<lista.size();i++){
             if(checkNumero(lista.get(i)))
             {newlista.add(lista.get(i));}
         }
         return newlista;
    }

    public static String getDigito(String cadena){
        //String ultimo = cadena.substring(cadena.length() - 1);
        String ultimo = cadena.substring(0, cadena.length() - 1);
        return  ultimo;
    }

    public static ArrayList<String>  cleanLista(ArrayList<String> lista){
        ArrayList<String> newlista = new ArrayList<>();
        for(int i=0;i<lista.size();i++){
            if (lista.get(i).length()>3)
            {newlista.add(getDigito(lista.get(i)));}
            else{
                newlista.add(lista.get(i));
            }
        }
        return newlista;
    }

    public static String cleanCadena(String cadena){
        String resultado="";
            if (cadena.length()>3)
            {resultado=getDigito(cadena);}
            else{
                resultado=cadena;
            }

        return resultado;
    }

    public static String getNewManzanaAnadida(ArrayList<String> listaManzana1){

        ArrayList<String> listaManzana2 = new ArrayList<>();
        int mayor;
        listaManzana2 = cleanLista(listaManzana1);
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
        return setDigitos(mayor+1);
    }

    public static String getLetra(ArrayList<String> listaManzana,String idmanzana){
        String resultado1 ="";
        String resultado2 ="";
            for(int i=0;i<listaManzana.size();i++)
            {
                if(cleanCadena(listaManzana.get(i)).equals(idmanzana))
                {resultado1=listaManzana.get(i);}

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






    public static void main(String[] args) {
        System.out.println ("Empezamos la ejecución del programa");
        //countDigitos(0);
        System.out.println ("resultado casteo:"+castCaracter('C'));
        ArrayList<String> lista = new ArrayList<>();
        lista.add("041");
        lista.add("042");
//        lista.add("043");
//        lista.add("043");
        System.out.println ("resultado letra:"+getLetra(lista,"041"));
//        for(int i=0;i<cleanLista(lista).size();i++){
//            System.out.println ("resultadosss:"+cleanLista(lista).get(i));
//        }
//        for(int i=0;i<filtrarNumero(lista).size();i++){
//            System.out.println ("resultado:"+filtrarNumero(lista).get(i));
//        }

//        for (int i=0;i<pruebaReplanteo2().size();i++)
//        {
//            System.out.println("Valor1:" +pruebaReplanteo2().get(i).getIdManzana());
//            System.out.println("Valor2:" +pruebaReplanteo2().get(i).getLista());
//            for (int x=0;x<pruebaReplanteo2().get(i).getLista().size();x++)
//            {
//                System.out.println("Valor3:" +(pruebaReplanteo2().get(i).getLista().get(x)));
//            }
//        }

//        for (int i=0;i<replantear(2,"044").size();i++)
//        {
//            System.out.println("Valor:" +replantear(2, "044").get(i));
//        }
    }
}
