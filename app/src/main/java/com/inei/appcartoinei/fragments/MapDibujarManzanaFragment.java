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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
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
import com.inei.appcartoinei.modelo.pojos.Manzana;
import com.inei.appcartoinei.modelo.pojos.ManzanaCaptura;

import org.json.JSONException;
import org.json.JSONObject;
import org.spatialite.database.SQLiteDatabase;

import java.io.IOException;
import java.util.ArrayList;


public class MapDibujarManzanaFragment extends Fragment implements OnMapReadyCallback,GoogleMap.OnMapClickListener, DialogFusionManzana.SendDialogListener{

    GoogleMap mgoogleMap;
    MapView mapView;
    View view;
    private LocationManager mLocationManager;
    private Location location;
    private Polygon poligon;
    private Marker vertice;
    private ArrayList<Marker> listaMarker = new ArrayList<Marker>();
    private ArrayList<LatLng> listPoints = new ArrayList<LatLng>() ;
    private ArrayList<LatLng> newListPoints = new ArrayList<LatLng>();
    private ArrayList<LatLng> listalatlog= new ArrayList<LatLng>();
    private ArrayList<String> datosManzana = new ArrayList<String>() ;
    private ArrayList<FusionItem> datosManzanaCaptura = new ArrayList<FusionItem>();
    private ArrayList<FusionItem> datosManzanaEnvio = new ArrayList<FusionItem>();
    private String newIdManzana = "";
    private String selectIdManzana = "";
    private String selectIdManzanaTitulo = "";
    private int selectIdAccion = 0;
    private FloatingActionButton fab2;
    private FloatingActionButton fab4;
    private FloatingActionButton fab5;
    private SQLiteDatabase db ;
    private DataBaseHelper op;
    Data    data;
    Context context;

    private GeoJsonLayer layer;

    private OnFragmentInteractionListener mListener;

    public MapDibujarManzanaFragment() {
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
        //idCapa = getArguments().getString("idUsuario","0");
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


        /*INSERTAR GEOMETRIA + 3 CAMPOS*/
        fab2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                //insertarManzana(listPoints);
                insertarManzanaCaptura(listPoints);
                fab2.setVisibility(View.GONE);
                fab4.setVisibility(View.GONE);
                fab5.setVisibility(View.GONE);

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
                                .strokeWidth(5));
                    }
                    else{poligon = mgoogleMap.addPolygon(new PolygonOptions()
                            .addAll(listPoints)
                            .fillColor(Color.GREEN)
                            .strokeWidth(5));}
                }
            }
        });

        /*ANULAR ACCION*/
        fab5.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                poligon.remove();
                for(int i=0;i<listaMarker.size();i++)
                {
                    listaMarker.get(i).remove();
                }
                listaMarker.clear();
                listPoints.clear();
                datosManzanaCaptura.clear();
                addLayerGeojson(true,false);
                fab2.setVisibility(View.GONE);
                fab4.setVisibility(View.GONE);
                fab5.setVisibility(View.GONE);
            }
        });
        /*MOSTRAR CARA DE TRABAJO*/
        addLayerGeojson(true,false);


        /*MOSTRAR MANZANAS*/
        if(obtenerListaShapeManzana().isEmpty())
        {Toast.makeText(getContext(),"No se encontraron Manzanas",Toast.LENGTH_SHORT).show();}
        else{
            for(int i=0;i<obtenerListaShapeManzana().size();i++)
            {
                ArrayList<LatLng> listados = new ArrayList<LatLng>();
                listados = obtenerLatLngShapeManzana(obtenerListaShapeManzana().get(i));
                if(listados.size()>0)
                {
                    Polygon polygono = googleMap.addPolygon(new PolygonOptions()
                            .addAll(listados)
                            .fillColor(Color.YELLOW)
                            .strokeColor(Color.BLUE)
                            .strokeWidth(3)
                            .strokeJointType(JointType.ROUND)
                            .visible(true));
                    polygono.setClickable(true);
                    polygono.setStrokeJointType(JointType.ROUND);
                }
            }
        }

        /*CREAR POLIGONO*/
        poligon = googleMap.addPolygon(new PolygonOptions()
                .add(new LatLng(0, 0), new LatLng(0, 0), new LatLng(0, 0))
                .fillColor(Color.GREEN)
                .strokeWidth(5));
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if(datosManzanaCaptura.size()==0)
        {
            Toast.makeText(getContext(),"Seleccione una Manzana para Actualizar",Toast.LENGTH_SHORT).show();
        }
        else {
            listPoints.add(latLng);
            newListPoints = new ArrayList<LatLng>(listPoints);
            newListPoints.add(listPoints.get(0));
            poligon.setPoints(newListPoints);
            vertice = mgoogleMap.addMarker(new
                    MarkerOptions().
                    position(latLng).
                    icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_edit_location)));
            listaMarker.add(vertice);
        }
    }

    /*METODO INSERTAR MANZANA_CAPTURA A SQLITE INTERNO*/
    public  void insertarManzanaCaptura(ArrayList<LatLng> poligono){
        Polygon poligonAdd = mgoogleMap.addPolygon(new PolygonOptions().add(new LatLng(0, 0), new LatLng(0, 0), new LatLng(0, 0)).strokeColor(Color.BLUE).strokeWidth(3).strokeJointType(JointType.ROUND).visible(true));
        if(datosManzanaCaptura.size()>0)
        { if(listPoints.size()>2) {
            try {
                data = new Data(context);
                data.open();
                data.insertManzanaCaptura(1,2,"15","01","13","003","00",newIdManzana,"",selectIdAccion,0,"GeomFromText('POLYGON(("+formatGeom(poligono)+"))',4326)");
                data.updateManzanaCaptura(selectIdManzana,1);
                for(int i=0;i<datosManzanaCaptura.size();i++)
                {data.updateManzanaCaptura(datosManzanaCaptura.get(i).getIdManzana(),1);}
                //data.updateManzanaCaptura(selectIdManzana,1);
                data.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(getContext(),"Se registro Manzana correctamente!",Toast.LENGTH_SHORT).show();
            poligon.remove();
            poligon = mgoogleMap.addPolygon(new PolygonOptions()
                    .add(new LatLng(0, 0), new LatLng(0, 0), new LatLng(0, 0))
                    .fillColor(Color.YELLOW)
                    .strokeColor(Color.BLUE)
                    .strokeWidth(5)
                    .visible(true));
            poligonAdd.setPoints(poligono);
            for(int i=0;i<listaMarker.size();i++)
            {
                listaMarker.get(i).remove();
            }
            listaMarker.clear();
            listPoints.clear();
            datosManzanaCaptura.clear();
            addLayerGeojson(true,false);
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

    /*METODO DE CREACION DE DIALOGO DE ACCIONES DE MANZANAS*/
    public  void formAccionManzana(final String idmanzana){
        ArrayList<String> acciones= new ArrayList<>();
        acciones.add("Fusionar (Misma Zona)");
        acciones.add("Fusionar (Diferente Zona)");
        acciones.add("Fraccionar");

        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(),R.style.ThemeOverlay_MaterialComponents_Dialog);
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.layout_form_accion, null);
        final Spinner accion     =  (Spinner) dialogView.findViewById(R.id.id_spnAccionNombre);
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
                        //addLayerGeojson(false);
                        alertDialog.dismiss();
                        if(accion.getSelectedItemPosition()==0)
                        {OpenDialogFusion(idmanzana,2,datosManzanaEnvio);
                        }
                        if(accion.getSelectedItemPosition()==1)
                        {Toast.makeText(getActivity().getApplicationContext(), "Opcion no desarrollada", Toast.LENGTH_SHORT).show();}
                    }
                });
            }
        });
        alertDialog.show();
    }

    /*METODO DE CREACION DE DIALOGO DE SELECCIONES DE MANZANAS*/
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
                        datosManzanaEnvio.add(new FusionItem(estado,zona,idmanzana));
                        OpenDialogFusion(idmanzana,2,datosManzanaEnvio);
                        alertDialog.dismiss();
                    }
                });
            }
        });
        alertDialog.show();
    }

    /*METODO CARGAR LAYER MARCO DE TRABAJO*/
    @SuppressLint("RestrictedApi")
    public  void addLayerGeojson(boolean estado, boolean paso)
    {
        if(estado==true && paso==false)
        {
            try {
                layer = new GeoJsonLayer(mgoogleMap, R.raw.marco_jmaria,getContext());
                layer.addLayerToMap();
                final GeoJsonPolygonStyle polygonStyle = layer.getDefaultPolygonStyle();
                polygonStyle.setStrokeColor(Color.RED);
                polygonStyle.setStrokeWidth(3);
                layer.setOnFeatureClickListener(new GeoJsonLayer.GeoJsonOnFeatureClickListener() {
                    @Override
                    public void onFeatureClick(Feature feature) {
                        datosManzanaEnvio.clear();
                        datosManzanaEnvio.add(new FusionItem(1,feature.getProperty("CODZONA"),feature.getProperty("CODMZNA")+""+feature.getProperty("SUFMZNA")));
                        formAccionManzana(feature.getProperty("CODMZNA")+""+feature.getProperty("SUFMZNA"));
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else
        {
            if(estado==true && paso==true)
            {
                try {
                    layer = new GeoJsonLayer(mgoogleMap, R.raw.marco_jmaria,getContext());
                    layer.addLayerToMap();
                    final GeoJsonPolygonStyle polygonStyle = layer.getDefaultPolygonStyle();
                    polygonStyle.setStrokeColor(Color.YELLOW);
                    polygonStyle.setStrokeWidth(3);
                    layer.setOnFeatureClickListener(new GeoJsonLayer.GeoJsonOnFeatureClickListener() {
                        @Override
                        public void onFeatureClick(Feature feature) {
                            if(filterManzana(datosManzanaEnvio,feature.getProperty("CODMZNA")+""+feature.getProperty("SUFMZNA")))
                            {
                                //datosManzanaEnvio.add(new FusionItem(1,feature.getProperty("CODZONA"),feature.getProperty("CODMZNA")+""+feature.getProperty("SUFMZNA")));
                                formSeleccionManzana(feature.getProperty("CODMZNA")+""+feature.getProperty("SUFMZNA"),feature.getProperty("CODZONA"),1);}
                            else{
                                String idmanzana = feature.getProperty("CODMZNA")+""+feature.getProperty("SUFMZNA");
                                OpenDialogFusion(idmanzana.trim(),2,datosManzanaEnvio);
                                Toast.makeText(getContext(),"Ya Selecciono Manzana",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                fab2.setVisibility(View.VISIBLE);
                fab4.setVisibility(View.VISIBLE);
                fab5.setVisibility(View.VISIBLE);
                layer.removeLayerFromMap();
            }
        }
    }

    /* ABRIR DIALOGO DE FUSION*/
    public void OpenDialogFusion(String idManzanna,int idAccion,ArrayList<FusionItem> manzanas){
        DialogFusionManzana dialogo = DialogFusionManzana.newInstance(idManzanna,idAccion,manzanas);
        dialogo.setTargetFragment(MapDibujarManzanaFragment.this,1);
        dialogo.show(getFragmentManager(),DialogFusion.TAG);
    }

    /*RECIBE PARAMETROS DE DIALOGO*/
    @Override
    public void receiveFusion(boolean estadoLayer,ArrayList<FusionItem> listaManzana,String idManzana,int idAccion,boolean paso) {
        layer.removeLayerFromMap();
        addLayerGeojson(estadoLayer,paso);
        newIdManzana = getNewManzana(listaManzana,idManzana);
        selectIdManzana = idManzana;
        selectIdAccion = idAccion;
        for (int i=0;i<listaManzana.size();i++)
        {
            datosManzanaCaptura.add(listaManzana.get(i));
        }
        datosManzanaCaptura.add(new FusionItem(1,"00",idManzana.trim()));
//        Toast.makeText(getContext(),"Mensaje:"+listaManzana.get(0).getIdManzana()+"-"+idManzana,Toast.LENGTH_SHORT).show();
    }

    public String getNewManzana(ArrayList<FusionItem> listaManzana,String idManzana){
        String valor="";
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


}
