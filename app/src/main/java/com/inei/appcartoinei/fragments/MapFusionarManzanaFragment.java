package com.inei.appcartoinei.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;
import com.inei.appcartoinei.R;
import com.inei.appcartoinei.dialogs.DialogFusion;
import com.inei.appcartoinei.dialogs.DialogFusionManzana;
import com.inei.appcartoinei.modelo.DAO.Data;
import com.inei.appcartoinei.modelo.DAO.DataBaseHelper;
import com.inei.appcartoinei.modelo.pojos.FusionItem;

import org.json.JSONException;
import org.json.JSONObject;
import org.spatialite.database.SQLiteDatabase;

import java.io.IOException;
import java.util.ArrayList;


public class MapFusionarManzanaFragment extends Fragment implements OnMapReadyCallback,GoogleMap.OnMapClickListener, DialogFusionManzana.SendDialogListener{

    GoogleMap mgoogleMap;
    MapView mapView;
    View view;
    private LocationManager mLocationManager;
    private Location location;
    private Polygon poligon;
    private Marker vertice;
    private ArrayList<Marker> listaMarker = new ArrayList<Marker>();
    private ArrayList<LatLng> listPoints = new ArrayList<LatLng>() ;
    private ArrayList<FusionItem> manzanaSeleccionadaEnvio = new ArrayList<FusionItem>();

    private int idAccionManzana = 0;
    private String newIdManzana = "";
    private String selectIdManzana = "";
    private FloatingActionButton fab2;
    private FloatingActionButton fab4;
    private FloatingActionButton fab5;
    private SQLiteDatabase db ;
    private DataBaseHelper op;
    Data    data;
    Context context;

    GeoJsonLayer layer;
    GeoJsonPolygonStyle polygonStyle;

    private OnFragmentInteractionListener mListener;

    public MapFusionarManzanaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_mapa_principal, container, false);
        return view;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        mapView = (MapView) view.findViewById(R.id.map);
        fab2 =  (FloatingActionButton) view.findViewById(R.id.fab2);
        fab4 =  (FloatingActionButton) view.findViewById(R.id.fab4);
        fab5 =  (FloatingActionButton) view.findViewById(R.id.fab5);

        op = new DataBaseHelper(getContext());
        db = op.getWritableDatabase();

        if(mapView!=null){
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mgoogleMap =googleMap;
        mgoogleMap.getUiSettings().setCompassEnabled(false);//Brujula
        mgoogleMap.getUiSettings().setZoomControlsEnabled(true);//Zoom
        mgoogleMap.getUiSettings().setMyLocationButtonEnabled(true);//GPS
        mgoogleMap.setOnMapClickListener(this);
        final LatLng peru = new LatLng(-9, -74);

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        CameraPosition Liberty = CameraPosition.builder().target(peru).zoom(16).bearing(0).tilt(45).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(Liberty));

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            mgoogleMap.setMyLocationEnabled(true);
            location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location!=null){
                LatLng gps=new LatLng(location.getLatitude(),location.getLongitude());
                mgoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gps,12));
            }
            else{
                mgoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(peru,5));
            }
        }
        else {
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)){
            }
            else{
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION} ,1);
            }
        }


        /*INSERTAR GEOMETRIA + PARAMETROS*/
        fab2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                insertarManzanaCaptura(listPoints);
            }
        });

        /*DESHACER ULTIMO PUNTO DE POLIGONO*/
        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listPoints.isEmpty())
                {
                    Toast.makeText(getContext(),"No ha Dibujado una manzana",Toast.LENGTH_SHORT).show();
                }
                else{
                    listaMarker.get(listaMarker.size()-1).remove();
                    listaMarker.remove(listaMarker.size()-1);
                    listPoints.remove(listPoints.size()-1);
                    poligon.remove();
                    if(listPoints.isEmpty())
                    {
                        poligon = mgoogleMap.addPolygon(new PolygonOptions()
                                .add(new LatLng(0, 0), new LatLng(0, 0), new LatLng(0, 0))
                                .fillColor(Color.GREEN)
                                .zIndex(3f)
                                .strokeWidth(5));
                    }
                    else{poligon = mgoogleMap.addPolygon(new PolygonOptions()
                            .addAll(listPoints)
                            .fillColor(Color.GREEN)
                            .zIndex(3f)
                            .strokeWidth(5));}
                }
            }
        });

        /*ANULAR ACCION*/
        fab5.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                cleanPolygon();
                addLayerGeojson(1);
            }
        });
        /*MOSTRAR CARGA DE TRABAJO*/
        addLayerGeojson(1);
        /*MOSTRAR MANZANAS INGRESADAS*/
        loadManzana();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if(manzanaSeleccionadaEnvio.size()<=1)
        {
            Toast.makeText(getContext(),"Seleccione una Manzana para Actualizar",Toast.LENGTH_SHORT).show();
        }
        else {
                listPoints.add(latLng);
            Log.e("mensaje:","Se agrego a punto->"+latLng);
                poligon.setPoints(listPoints);
            Log.e("mensaje:","Se agrego poligono con puntos->"+listPoints.size());
                vertice = mgoogleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_edit_location)));
            Log.e("mensaje","Se agrego vertice"+vertice.getPosition());
                listaMarker.add(vertice);
            Log.e("mensaje","Se grego vertices a Arraylist"+listaMarker.size());
        }
    }

    /*METODO INSERTAR MANZANA_CAPTURA A SQLITE INTERNO*/
    public  void insertarManzanaCaptura(ArrayList<LatLng> poligono){
        if(manzanaSeleccionadaEnvio.size()>0)
        { if(listPoints.size()>2) {
            try {
                data = new Data(context);
                data.open();
                data.insertManzanaCaptura(1,2,"15","01","13","001","00",newIdManzana,"","",idAccionManzana,0,"GeomFromText('POLYGON(("+formatGeom(poligono)+"))',4326)");
                //data.updateManzanaCaptura(selectIdManzana,1);
                for(int i=0;i<manzanaSeleccionadaEnvio.size();i++)
                {   Log.e("mensaje_insertar:",""+manzanaSeleccionadaEnvio.get(i).getIdzona()+"--"+manzanaSeleccionadaEnvio.get(i).getIdManzana());
                    data.updateManzanaCapturaXEstado(manzanaSeleccionadaEnvio.get(i).getIdManzana().trim(),1);}
            } catch (IOException e) {
                e.printStackTrace();
            }
            addLayerGeojson(1);
            cleanPolygon();
            loadManzana();
            Toast.makeText(getContext(),"Se registro Manzana correctamente!",Toast.LENGTH_SHORT).show();
        }
        else
        {Toast.makeText(getContext(),"Dibuje una Manzana",Toast.LENGTH_SHORT).show();}
        }
        else{Toast.makeText(getContext(),"Ingrese Manzanas a Unir",Toast.LENGTH_SHORT).show();}
    }

    /*METODO OBTENER LISTA(STRING) DE SHAPE MANZANA*/
    public ArrayList<String> obtenerListaShapeManzana(){
        ArrayList<String> listashape = new ArrayList<>();
        try {
            Data data = new Data(context);
            data.open();
            ArrayList<String> query = data.getAllShapeManzanaCaptura();
            for(int i=0;i<query.size();i++){
                String shape = query.get(i);
                listashape.add(shape);
            }
            data.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return listashape;
    }

    /*METODO CONVERTIR LISTA(STRING) A LISTA(LATLNG)*/
    public ArrayList<LatLng> obtenerLatLngShapeManzana(String shape){
        ArrayList<LatLng> listapintado = new ArrayList<LatLng>();
        String campoGeom = shape;
        try {
            JSONObject jsonObject = new JSONObject(campoGeom);
            String dato = jsonObject.getString("coordinates");
            String ncadena1= dato.substring(1,dato.length()-1);
            String ncadena2= ncadena1.substring(1,ncadena1.length()-1);
            String ncadena3 = ncadena2.replace("],[", "];[");
            String[] parts = ncadena3.split(";");
            for(int i =0;i<parts.length;i++)
            {
                String part1 = parts[i];
                String cadena4= part1.substring(1,part1.length()-1);
                String[] latlog = cadena4.split(",");
                for(int x=0;x<1;x++){
                    listapintado.add(new LatLng(Double.parseDouble(latlog[0]),Double.parseDouble(latlog[1])));
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return listapintado;
    }

    /*METODO DE FORMATO A POLYGONO*/
    public String formatGeom(ArrayList<LatLng> poligono){
        String format ="";
        for (int i = 0; i <poligono.size() ; i++) {
            if (i >0){
                format = format +"," + poligono.get(i).latitude+ " "+poligono.get(i).longitude;
            }
            else{
                format = poligono.get(i).latitude+ " "+poligono.get(i).longitude;
            }
        }
        return format;
    }


    /*1. METODO CARGAR LAYER MARCO DE TRABAJO (/*0=SIN MARCO,1=CAPA MARCO,2=CAPA EDICION)*/
    @SuppressLint("RestrictedApi")
    public  void addLayerGeojson(int estado)
    {
        try {
                layer = new GeoJsonLayer(mgoogleMap, R.raw.marco_jmaria,getContext());
                layer.addLayerToMap();
                polygonStyle = layer.getDefaultPolygonStyle();
                polygonStyle.setStrokeWidth(3);
                polygonStyle.setZIndex(1f);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        switch (estado){
            case 0:
                Log.e("mensajes:","0->sin capa, dibujar poligono");
                fab2.setVisibility(View.VISIBLE);
                fab4.setVisibility(View.VISIBLE);
                fab5.setVisibility(View.VISIBLE);
                layer.removeLayerFromMap();
                break;
            case 1:
                Log.e("mensajes:","1->capa roja,marco de trabajo");
                polygonStyle.setStrokeColor(Color.RED);
                layer.setOnFeatureClickListener(new GeoJsonLayer.GeoJsonOnFeatureClickListener() {
                    @Override
                    public void onFeatureClick(Feature feature) {
                        if(validarPolygon(feature.getProperty("CODZONA"),feature.getProperty("CODMZNA")+""+feature.getProperty("SUFMZNA"))){
                            Toast.makeText(getContext(),"Poligono ya trabajado",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {formAccionManzana(feature.getProperty("CODZONA"),feature.getProperty("CODMZNA")+""+feature.getProperty("SUFMZNA"));}
                    }
                });
                break;
            case 2:
                Log.e("mensajes:","2->capa amarilla,Seleccion de poligonos");
                polygonStyle.setStrokeColor(Color.YELLOW);
                layer.setOnFeatureClickListener(new GeoJsonLayer.GeoJsonOnFeatureClickListener() {
                    @Override
                    public void onFeatureClick(Feature feature) {
                        if(listPoints.size()>0){
                            Log.e("mensajes:","se esta pintado poligono");
                        }
                        else {
                            if (filterManzana(manzanaSeleccionadaEnvio, feature.getProperty("CODMZNA") + "" + feature.getProperty("SUFMZNA"))) {
                                formSeleccionManzana(feature.getProperty("CODMZNA") + "" + feature.getProperty("SUFMZNA"), feature.getProperty("CODZONA"), 1);
                            } else {
                                String idmanzana = feature.getProperty("CODMZNA") + "" + feature.getProperty("SUFMZNA");
                                OpenDialogFusion(idmanzana.trim(), manzanaSeleccionadaEnvio);
                                Toast.makeText(getContext(), "Ya Selecciono Manzana", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                break;
            default:
        }
    }

    /*2. METODO DE CREACION DE DIALOGO DE ACCIONES DE MANZANAS (FUSION,REPLANTEAR,ETC)*/
    public  void formAccionManzana(final String codzona,final String idmanzana){
        ArrayList<String> acciones= new ArrayList<>();
        acciones.add("Fusionar (Misma Zona)");
        acciones.add("Fusionar (Diferente Zona)");

        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(),R.style.ThemeOverlay_MaterialComponents_Dialog);
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.layout_form_accion, null);
        final Spinner accion     =  (Spinner) dialogView.findViewById(R.id.id_accion_sp_accion);
        ArrayAdapter<String> adapterZona         = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,acciones);
        adapterZona.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accion.setAdapter(adapterZona);
        alert.setTitle("Nro Manzana: "+idmanzana);
        alert.setIcon(R.drawable.ic_view_module_26);
        alert.setView(dialogView);
        alert.setPositiveButton("OK",null);
        alert.setNegativeButton("Cancelar",null);
        final AlertDialog alertDialog = alert.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        if(accion.getSelectedItemPosition()==0)
                        {
                            idAccionManzana = 2;
                            manzanaSeleccionadaEnvio.clear();
                            manzanaSeleccionadaEnvio.add(new FusionItem(1,codzona,idmanzana));
                            OpenDialogFusion(idmanzana,manzanaSeleccionadaEnvio);
                        }
                        if(accion.getSelectedItemPosition()==1)
                        {Toast.makeText(getActivity().getApplicationContext(), "Opcion no desarrollada", Toast.LENGTH_SHORT).show();}
                    }
                });
            }
        });
        alertDialog.show();
    }

    /*3. ABRIR DIALOGO DE ACCION (FUSION) Y ENVIAR PARAMETROS*/
    public void OpenDialogFusion(String idManzanna,ArrayList<FusionItem> manzanas){
        DialogFusionManzana dialogo = DialogFusionManzana.newInstance(idManzanna,manzanas);
        dialogo.setTargetFragment(MapFusionarManzanaFragment.this,1);
        dialogo.show(getFragmentManager(),DialogFusion.TAG);
    }

    /*4. METODO DE CREACION DIALOGO PARA APROBAR SELECCION DE MANZANA*/
    public  void formSeleccionManzana(final String idmanzana,final String zona, final int estado){
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(),R.style.ThemeOverlay_MaterialComponents_Dialog);
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.layout_form_selectmanzana, null);
        final TextView text_selection     =  (TextView) dialogView.findViewById(R.id.id_txt_select_manzana);
        text_selection.setText(idmanzana);
        alert.setTitle("Desea Seleccionar Manzana?");
        alert.setIcon(R.drawable.ic_info_outline);
        alert.setView(dialogView);
        alert.setPositiveButton("OK",null);
        alert.setNegativeButton("Cancelar",null);
        final AlertDialog alertDialog = alert.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        manzanaSeleccionadaEnvio.add(new FusionItem(estado,zona,idmanzana));
                        OpenDialogFusion(idmanzana,manzanaSeleccionadaEnvio);
                        alertDialog.dismiss();
                    }
                });
            }
        });
        alertDialog.show();
    }

    /*5. RECIBE PARAMETROS DE DIALOGO DialogFusionManzana*/
    @Override
    public void receiveFusion(final int estadoLayer,final ArrayList<FusionItem> listaManzana,String idManzana) {
        removeLayer();
        addLayerGeojson(estadoLayer);
        newIdManzana = getNewManzana(listaManzana,listaManzana.get(0).getIdManzana());
        selectIdManzana = idManzana;
        manzanaSeleccionadaEnvio = listaManzana;

        if(estadoLayer==0){
            createPolygon();
        }
        if(estadoLayer==1){
            cleanPolygon();
        }
        if(estadoLayer==2){
        }
    }

    /*CREACION DE NUEVO ID DE MANZANA*/
    public String getNewManzana(ArrayList<FusionItem> listaManzana, String idManzana){
        int menor;
        int valorid;
        int newValorId=0;
        if(listaManzana.size()>0){
            menor = Integer.parseInt(listaManzana.get(0).getIdManzana().trim());
            for(FusionItem objeto: listaManzana)
            {
                int numero = Integer.parseInt(objeto.getIdManzana().trim());
                if (numero<menor)
                {menor =numero;}
            }
            valorid = Integer.parseInt(idManzana.trim());
            if(menor<valorid){
                newValorId=menor;
            }
            else{
                newValorId=valorid;
            }
        }
        else {
            menor=0;
        }
        return "0"+newValorId+"A";
    }

    /*FILTRAR MANZANAS*/
    private boolean filterManzana(ArrayList<FusionItem> manzanas, String manzana){
        boolean respuesta = true;
        manzana = manzana.toLowerCase().trim();
        for(FusionItem valor1: manzanas){
            String valor2= valor1.getIdManzana().toLowerCase();
            if(valor2.contains(manzana)){
                respuesta = false;
            }
        }
        return respuesta;
    }

    /*CARGAR MANZANAS DIBUJADAS*/
    private void loadManzana(){
        if(obtenerListaShapeManzana().isEmpty())
        {Toast.makeText(getContext(),"No se encontraron Manzanas",Toast.LENGTH_SHORT).show();}
        else{
            for(int i=0;i<obtenerListaShapeManzana().size();i++)
            {
                ArrayList<LatLng> listados = new ArrayList<LatLng>();
                listados = obtenerLatLngShapeManzana(obtenerListaShapeManzana().get(i));
                if(listados.size()>0)
                {
                    Polygon polygono = mgoogleMap.addPolygon(new PolygonOptions()
                            .addAll(listados)
                            .fillColor(Color.YELLOW)
                            .strokeColor(Color.GRAY)
                            .strokeWidth(3)
                            .strokeJointType(JointType.ROUND)
                            .zIndex(2f)
                            .visible(true));
                    polygono.setClickable(true);
                    polygono.setStrokeJointType(JointType.ROUND);
                }
            }
//            mgoogleMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
//                @Override
//                public void onPolygonClick(Polygon polygon) {
//                    Toast.makeText(getContext(),"x:"+polygon.,Toast.LENGTH_SHORT).show();
//                }
//            });

        }
    }

    /*LIMPIAR POLIGONO*/
    @SuppressLint("RestrictedApi")
    public void cleanPolygon(){
        manzanaSeleccionadaEnvio.clear();
        if(poligon!=null)
        {poligon.remove();}
        fab2.setVisibility(View.GONE);
        fab4.setVisibility(View.GONE);
        fab5.setVisibility(View.GONE);
        if(listaMarker!=null) {
            for (int i = 0; i < listaMarker.size(); i++) {
                listaMarker.get(i).remove();
            }
        }
            listaMarker.clear();
            listPoints.clear();

    }

    /*CREAR POLIGONO*/
    public void createPolygon(){
        poligon = mgoogleMap.addPolygon(new PolygonOptions()
                .add(new LatLng(0, 0), new LatLng(0, 0), new LatLng(0, 0))
                .fillColor(Color.GREEN)
                .clickable(false)
                .zIndex(3f)
                .strokeWidth(5));
        Log.e("Mensaje:","poligono creado");
    }

    /*VALIDAR POLIGONO SELECCIONADO CON POLIGONO EN SQLITE*/
    public boolean validarPolygon(String idzona,String idmzna){
        boolean estado = false;
        try {
            Data data = new Data(context);
            data.open();
            estado = data.getEstateManzana(idzona,idmzna);
            data.close();
            }
        catch (IOException e){
            e.printStackTrace();
        }
        return estado;

    }

    /*QUITAR CAPA*/
    public void removeLayer(){
        layer.removeLayerFromMap();
        Log.e("Mensaje:","Capa removida");
    }

}
