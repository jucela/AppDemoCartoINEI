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
import com.inei.appcartoinei.modelo.DAO.DataBaseHelper;
import com.inei.appcartoinei.util.FileChooser;
import com.inei.appcartoinei.util.Importar_data;

import org.spatialite.database.SQLiteDatabase;

import java.io.File;

public class ImportarDataFragment extends Fragment  {


    View view;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private TextView ruta;
    private SQLiteDatabase db ;
    private DataBaseHelper op;
    private RequestQueue mQueue;
    String filename;

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
                Importar_data importar_encuesta = new Importar_data(getContext(),filename);
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

}
