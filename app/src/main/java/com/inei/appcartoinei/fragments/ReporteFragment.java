package com.inei.appcartoinei.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.inei.appcartoinei.R;
import com.inei.appcartoinei.adapters.ItemReporteAdapter;
import com.inei.appcartoinei.modelo.DAO.Data;
import com.inei.appcartoinei.modelo.pojos.ManzanaCaptura;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ReporteFragment extends Fragment {

    View view;
    RecyclerView recyclerView;
    ItemReporteAdapter itemReporteAdapter;
    Context context;
    FloatingActionButton btn_cargar;
    private RequestQueue mQueue;
    Data data;
    final String ubigeo;
    final String codigoZona;
    final String sufijoZona;
    TextView txt_zona;
    TextView txt_manzana;
    TextView txt_estado;
    TextView txt_estadoEnvio;
    int estadoZona=0;
    int estadoManzana=0;
    int estadoEstado=0;
    int estadoEstadoEnvio=0;



    private OnFragmentInteractionListener mListener;

    public ReporteFragment(String ubigeo,String codigoZona,String sufijoZona, Context context) {
        this.ubigeo = ubigeo;
        this.codigoZona = codigoZona;
        this.sufijoZona = sufijoZona;
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view            = inflater.inflate(R.layout.fragment_reporte, container, false);
        recyclerView    = (RecyclerView) view.findViewById(R.id.rv_lista_reporte);
        btn_cargar      = (FloatingActionButton) view.findViewById(R.id.fab_subir);
        txt_zona        = (TextView) view.findViewById(R.id.txt_reporte_idZona);
        txt_manzana     = (TextView) view.findViewById(R.id.txt_reporte_idManzana);
        txt_estado      = (TextView) view.findViewById(R.id.txt_reporte_idEstado);
        txt_estadoEnvio = (TextView) view.findViewById(R.id.txt_reporte_idEstadoEnvio);
        return view;
    }

    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        itemReporteAdapter = new ItemReporteAdapter(getAllManzanaCapturaTrabajadas(),new ItemReporteAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getContext(),"posiciòn:"+position,Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(itemReporteAdapter);
        mQueue = Volley.newRequestQueue(getContext());

        btn_cargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadDatos();
            }
        });

        txt_zona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(estadoZona==0){
                    updateLista("codzona",0);
                    txt_zona.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up_24_white, 0);
                    estadoZona=1;
                }
                else{
                    updateLista("codzona",1);
                    txt_zona.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_24_white, 0);
                    estadoZona=0;
                }

            }
        });
        txt_manzana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(estadoManzana==0){
                    updateLista("codmzna",0);
                    txt_manzana.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up_24_white, 0);
                    estadoManzana=1;
                }
                else{
                    updateLista("codmzna",1);
                    txt_manzana.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_24_white, 0);
                    estadoManzana=0;
                }

            }
        });
        txt_estado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(estadoEstado==0){
                    updateLista("estado",0);
                    txt_estado.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up_24_white, 0);
                    estadoEstado=1;
                }
                else{
                    updateLista("estado",1);
                    txt_estado.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_24_white, 0);
                    estadoEstado=0;
                }
            }
        });
        txt_estadoEnvio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(estadoEstadoEnvio==0){
                    updateLista("cargado",0);
                    txt_estadoEnvio.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up_24_white, 0);
                    estadoEstadoEnvio=1;
                }
                else{
                    updateLista("cargado",1);
                    txt_estadoEnvio.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_24_white, 0);
                    estadoEstadoEnvio=0;
                }
            }
        });


        Toast.makeText(getContext(),""+getAllManzanaCapturaXCargado().size()+" Manzanas Trabajadas",Toast.LENGTH_SHORT).show();
    }

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

    public void uploadDatos(){
        ArrayList<ManzanaCaptura> lista = new ArrayList<>();
        String stringJsonFinal = "";
        lista = getAllManzanaCapturaXCargado();

        if(lista.size()>0){
            Toast.makeText(getContext(),"Conectando con el servidor...",Toast.LENGTH_SHORT).show();
            for (int i=0;i<lista.size();i++){

                try {
                    JSONObject geom = new JSONObject(lista.get(i).getShape());
                    String rings = geom.get("coordinates").toString();
                    stringJsonFinal = "{\"geometry\":{\"rings\":" + rings + ", \"spatialReference\" : {\"wkid\" : 4326}},\"attributes\":{\"UBIGEO\":" + lista.get(i).getCcdd()+""+lista.get(i).getCcpp()+""+lista.get(i).getCcdi()+ ",\"ZONA\":" + lista.get(i).getCodzona() + ",\"MANZANA\":"+ lista.get(i).getCodmzna()+"}}";
                    Log.e("My App", "xxx: \"" + stringJsonFinal + "\"");
                    JSONArray arrayGeom = new JSONArray();
                    JSONObject obj = new JSONObject(stringJsonFinal);
                    arrayGeom.put(obj);
                    insertarServicio(arrayGeom,lista.get(i).getCodzona(),lista.get(i).getSufzona(),lista.get(i).getCodmzna(),lista.get(i).getSufmzna(),lista.size(),i);
                } catch (Throwable tx) {
                    Log.e("My App", "Could not parse malformed JSON: \"" + stringJsonFinal + "\"");
                }
            }
        }
        else{
            Toast.makeText(getContext(),"No Existe Registros para Subir",Toast.LENGTH_SHORT).show();
        }
    }

    public void insertarServicio( final JSONArray arrayGeom,final String codZona,final String sufZona,final String codMzna,final String sufMzna,final int listaSize,final int position){
        String url = "http://arcgis4.inei.gob.pe:6080/arcgis/rest/services/DESARROLLO/servicio_prueba_captura/FeatureServer/0/addFeatures";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("mensaje1:", response);
                setUpdateManzanaCaptura(codZona,sufZona,codMzna,sufMzna,3);
                Log.i("mensaje2",""+listaSize+""+position);
                if(listaSize==position+1)
                {
                    itemReporteAdapter.notifyDataSetChanged();
                    itemReporteAdapter = new ItemReporteAdapter(getAllManzanaCapturaTrabajadas(),null);
                    recyclerView.setAdapter(itemReporteAdapter);
                    Toast.makeText(getContext(),"Se Inserto "+listaSize+" registros en el servidor",Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("ERROR", error.toString());
                Toast.makeText(getContext(),"Error al Insertar en el Servidor",Toast.LENGTH_SHORT).show();
            }
        }){

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=utf-8";
            }
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("features", arrayGeom.toString());
                params.put("f", "json");
                return params;
            }
        };
        mQueue.add(request);

    }

    public ArrayList<ManzanaCaptura> getAllManzanaCapturaTrabajadas(){
        ArrayList<ManzanaCaptura> manzanas = new ArrayList<>();
        try {
            Data data = new Data(context);
            data.open();
            manzanas = data.getAllManzanaCapturaTrabajadas(codigoZona,sufijoZona);
            data.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return manzanas;
    }

    public ArrayList<ManzanaCaptura> getAllManzanaCapturaXCargado(){
        ArrayList<ManzanaCaptura> manzanas = new ArrayList<>();
        try {
            Data data = new Data(context);
            data.open();
            manzanas = data.getAllManzanaCapturaXZonaCargado(codigoZona,sufijoZona);
            data.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return manzanas;
    }

    public void setUpdateManzanaCaptura(String codZona,String sufZona,String codMzna,String sufMzna,int cargado){
        try {
        data = new Data(context);
        data.open();
        data.updateManzanaCapturaXCargado(codZona,sufZona,codMzna,sufMzna,cargado);
        }catch (IOException e) {
        e.printStackTrace();
        }
    }

    public ArrayList<ManzanaCaptura> getAllManzanaCapturaTrabajadasXCampo(String campo,int tipo){
        ArrayList<ManzanaCaptura> manzanas = new ArrayList<>();
        try {
            Data data = new Data(context);
            data.open();
            manzanas = data.getAllManzanaCapturaTrabajadasXCampo(codigoZona,sufijoZona,campo,tipo);
            data.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return manzanas;
    }

    public void updateLista(String campo,int tipo){
        itemReporteAdapter.notifyDataSetChanged();
        itemReporteAdapter = new ItemReporteAdapter(getAllManzanaCapturaTrabajadasXCampo(campo,tipo),null);
        recyclerView.setAdapter(itemReporteAdapter);
    }


}
