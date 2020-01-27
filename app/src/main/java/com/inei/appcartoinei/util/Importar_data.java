package com.inei.appcartoinei.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.inei.appcartoinei.fragments.ImportarDataFragment;
import com.inei.appcartoinei.modelo.DAO.Data;
import com.inei.appcartoinei.modelo.pojos.Manzana;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;


public class Importar_data {
    private ArrayList<Manzana> manzanas;
    private Manzana manzana;
    private Context context;

    private ArrayList<Tabla> tablas;
    private Tabla current_tabla;
    String nombre_flujo="";
    Data    data;
    Context contexto;

    private String currentTag = null;
    private String currentVariable = null;
    public Importar_data(Context context, String filename) {
        this.context = context;

    }

    public void parseXML(String nombreArchivo){
        manzanas = new ArrayList<>();
        tablas = new ArrayList<>();

        XmlPullParserFactory factory;
        FileInputStream fis = null;

        try {
            StringBuilder sb = new StringBuilder();

            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            XmlPullParser xpp = factory.newPullParser();

            File file = new File(nombreArchivo);
            FileInputStream fileInputStream = new FileInputStream(file);

            fis = new FileInputStream(file);

            xpp.setInput(fis, null);

            int eventType = xpp.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT){
                if(eventType == XmlPullParser.START_TAG){
                    handleStarTag(xpp.getName());
                }else if(eventType == XmlPullParser.END_TAG){
                    handleEndTag(xpp.getName());
                }else if(eventType == XmlPullParser.TEXT){
                    handleText(xpp.getText());
                }
                eventType = xpp.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "No existe el archivo", Toast.LENGTH_SHORT).show();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    private void handleStarTag(String name) {
        if(Herramientas.texto(name).length()>6 && Herramientas.texto(name).substring(0,6).equals("tabla_")){
            nombre_flujo = name;
        }
        switch (name){
            case "manzana":
                currentTag = "manzana";
                manzana = new Manzana();
                break;
            default:
                if(name.equals(nombre_flujo)){
                    currentTag = nombre_flujo; current_tabla = new Tabla(); current_tabla.nombre_tabla = nombre_flujo;
                }else{
                    currentVariable = name;
                }
                break;
        }
    }

    public void handleEndTag(String name){
        switch (name){
            case "manzana": manzanas.add(manzana); break;
            default:
                if(name.equals(nombre_flujo)) {
                    tablas.add(current_tabla);
                }
        }
    }

    private void handleText(String text) {
            switch (currentTag) {
                case "manzana":
                    manzana.setVariable(currentVariable, text);
                    break;
                default:
                    if(text.equals("vacio")) text = "";
                    ColumnaTabla columnaTabla = new ColumnaTabla();
                    columnaTabla.nombre_columna = currentVariable;
                    columnaTabla.valor_columna = text;
                    current_tabla.columnas_tabla.add(columnaTabla);
                    break;
            }
    }

    public void llenarTablasBD(){

        for (Tabla tabla: tablas)
        {data.insertarDatos("manzana",tabla.toValues());}




    }

}
