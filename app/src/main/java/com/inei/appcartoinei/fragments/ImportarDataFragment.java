package com.inei.appcartoinei.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.inei.appcartoinei.R;
import com.inei.appcartoinei.modelo.DAO.Data;
import com.inei.appcartoinei.modelo.DAO.DataBaseHelper;
import com.inei.appcartoinei.modelo.DAO.SQLConstantes;
import com.inei.appcartoinei.modelo.pojos.Manzana;
import com.inei.appcartoinei.util.FileChooser;
import com.inei.appcartoinei.util.Importar_data;

import org.spatialite.database.SQLiteDatabase;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.os.Environment.getExternalStorageDirectory;

public class ImportarDataFragment extends Fragment  {


    View view;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private TextView ruta;
    private SQLiteDatabase db ;
    private DataBaseHelper op;
    private RequestQueue mQueue;
    String filename;
    private Manzana manzana;
    private String currentTag = null;
    private String currentVariable = null;

    private OnFragmentInteractionListener mListener;

    public ImportarDataFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_importar, container, false);
        //idCapa = getArguments().getString("idUsuario","0");
        return view;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        fab1 =  (FloatingActionButton) view.findViewById(R.id.fab1_importar);
        fab2 =  (FloatingActionButton) view.findViewById(R.id.fab2_importar);
        ruta =  (EditText) view.findViewById(R.id.edt_ruta);


        op = new DataBaseHelper(getContext());
        db = op.getWritableDatabase();
        mQueue = Volley.newRequestQueue(getContext());

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(),"Abrir Carpeta",Toast.LENGTH_SHORT).show();
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }else{
                    FileChooser fileChooser = new FileChooser(getActivity());
                    fileChooser.setFileListener(new FileChooser.FileSelectedListener() {
                        @Override
                        public void fileSelected(File file) {
                            String filename = file.getAbsolutePath();
                            if(filename.substring(filename.length()-4,filename.length()).toLowerCase().equals(".xml")){
                                ruta.setText(filename);
                            }else{
                                Toast.makeText(getContext(), "archivo de tipo incorrecto", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    fileChooser.showDialog();
                }
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Importar",Toast.LENGTH_SHORT).show();
                String nombreArchivo = ruta.getText().toString();
                filename = nombreArchivo;
                parseXMLImportar(nombreArchivo);
                //Importar_data importar_encuesta = new Importar_data(getContext(),filename);
            }
        });

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /*IMPORTACION*/
    public void parseXMLImportar(String nombreArchivo){
        manzana = new Manzana();

        XmlPullParserFactory factory;
        FileInputStream fis = null;
        try {
            StringBuilder sb = new StringBuilder();
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            File nuevaCarpeta = new File(getExternalStorageDirectory(), "datos");
            File file = new File(nuevaCarpeta, nombreArchivo);
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
                Data data = new Data(getContext());
                data.open();
                data.insertarApple(manzana);
                data.close();

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "No existe el archivo", Toast.LENGTH_SHORT).show();
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

    private void handleText(String text) {
        String xmlText = text;
        switch (currentTag){
            case "manzana": agregarVariableManzana(currentVariable,text);break;
        }
    }

    private void handleStarTag(String name) {
        switch (name){
            case "manzana": currentTag = "manzana";break;
            default: currentVariable = name;break;
        }
    }
    public void handleEndTag(String name){
//        switch (name){
//            case "VISITA": visitas.add(currentVisita);break;
//            case "MODULO5_II":modulo5Dinamicos.add(currentModulo5Dinamico);break;
//        }
    }

    public void agregarVariableManzana(String campo, String valor){
        switch(campo){
            case SQLConstantes.manzana_cp_id:manzana.setId(Integer.parseInt(valor));break;
            case SQLConstantes.manzana_cp_iduser:manzana.setUserid(Integer.parseInt(valor));break;
            case SQLConstantes.manzana_cp_idmanzana:manzana.setIdmanzana(valor);break;
            case SQLConstantes.manzana_cp_nommanzana:manzana.setNommanzana(valor);break;
            case SQLConstantes.manzana_cp_idzona:manzana.setIdzona(valor);break;
            case SQLConstantes.manzana_cp_zona:manzana.setZona(valor);break;
            case SQLConstantes.manzana_cp_ubigeo:manzana.setUbigeo(valor);break;
            case SQLConstantes.manzana_cp_shape:manzana.setShape(valor);break;
        }

    }

}
