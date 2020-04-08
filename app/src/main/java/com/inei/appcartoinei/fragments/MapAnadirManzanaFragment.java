package com.inei.appcartoinei.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPolygon;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;
import com.inei.appcartoinei.R;
import com.inei.appcartoinei.modelo.DAO.Data;
import com.inei.appcartoinei.modelo.DAO.DataBaseHelper;
import com.inei.appcartoinei.modelo.pojos.ManzanaCaptura;

import org.json.JSONException;
import org.json.JSONObject;
import org.spatialite.database.SQLiteDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MapAnadirManzanaFragment extends Fragment implements OnMapReadyCallback,GoogleMap.OnMapClickListener{

    GoogleMap mgoogleMap;
    MapView mapView;
    View view;
    GeoJsonLayer layer;
    private LocationManager mLocationManager;
    private Location location;
    private Polygon poligon;
    private Marker vertice;
    private ArrayList<Marker> listaMarker = new ArrayList<Marker>();
    private ArrayList<LatLng> listPoints = new ArrayList<LatLng>() ;

    private int accionManzana = 1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;
    private FloatingActionButton fab4;
    private FloatingActionButton fab5;
    private SQLiteDatabase db ;
    private DataBaseHelper op;
    Data    data;
    Context context;

    final String ubigeo;
    final String codigoZona;
    final String sufijoZona;

    private OnFragmentInteractionListener mListener;

    public MapAnadirManzanaFragment(String ubigeo,String codigoZona,String sufijoZona,Context context) {
        this.ubigeo = ubigeo;
        this.codigoZona = codigoZona;
        this.sufijoZona = sufijoZona;
        this.context = context;
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
        fab3 =  (FloatingActionButton) view.findViewById(R.id.fab3);
        fab4 =  (FloatingActionButton) view.findViewById(R.id.fab4);
        fab5 =  (FloatingActionButton) view.findViewById(R.id.fab5);

        fab3.setVisibility(View.VISIBLE);

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
        final LatLng jmaria = new LatLng(-12.065256655999974, -77.044274425999959);

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        CameraPosition Liberty = CameraPosition.builder().target(jmaria).zoom(16).bearing(0).tilt(45).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(Liberty));

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            mgoogleMap.setMyLocationEnabled(true);
            location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location!=null){
                LatLng gps=new LatLng(location.getLatitude(),location.getLongitude());
                mgoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(jmaria,16));
            }
            else{
                mgoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(jmaria,16));
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
            public void onClick(View v) {
                saveManzanaCapturaAnadida();
            }
        });

        /*MUESTRA DIALOGO PARA AÑADIR MANZANA */
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visualizeAnadirManzana(getNewManzanaAnadida(codigoZona));
            }
        });

        /*DESHACER ULTIMO PUNTO DE POLIGONO*/
        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undoPolygon();
            }
        });

        /*ANULAR ACCION*/
        fab5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanPolygon();
                removeLayer();
                createLayerGeojsonMain();
                loadFeatureAllManzanas();
                setEventoEstado();
                setEstadoAnadir(true);
            }
        });
        /*MOSTRAR CARGA DE TRABAJO*/
        createLayerGeojsonMain();
        loadFeatureAllManzanas();
        setEventoEstado();
    }

    @Override
    public void onMapClick(LatLng latLng) {
         if (poligon!=null){
             if(poligon.getPoints().get(0).equals(new LatLng(0,0)))
             {
                 Toast.makeText(getContext(),"Presione el boton '+' para añadir la nueva manzana1",Toast.LENGTH_SHORT).show();
             }
             else{
                 listPoints.add(latLng);
                 poligon.setPoints(listPoints);;
                 vertice = mgoogleMap.addMarker(new MarkerOptions()
                         .position(latLng)
                         .zIndex(2f)
                         .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_edit_location)));
                 listaMarker.add(vertice);
             }
         }
         else {
                    Toast.makeText(getContext(),"Presione el boton '+' para añadir la nueva manzana2",Toast.LENGTH_SHORT).show();
              }

    }

    /*******METODOS DE INTERACCION CON EL MAPA**********/

    /*1. CREAR LAYER PRINCIPAL */
    @SuppressLint("RestrictedApi")
    public void createLayerGeojsonMain() {
        try {
            layer = new GeoJsonLayer(mgoogleMap, R.raw.marco_inicio, getContext());
            layer.addLayerToMap();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*2. CARGAR FEATURE DE TODAS LAS MANZANAS CAPTURA*/
    private void loadFeatureAllManzanas() {
        ArrayList<LatLng> listaVertices = new ArrayList<>();
        String codZona = "";
        String sufZona = "";
        String codMzna = "";
        String sufMzna = "";
        String estado = "";
        if(getListaManzanaCapturaXZona().isEmpty()) {
            Toast.makeText(getContext(), "No se encontraron Manzanas Captura", Toast.LENGTH_SHORT).show();
        }
        else {
            for(ManzanaCaptura manzana : getListaManzanaCapturaXZona()){
                codZona = manzana.getCodzona();
                sufZona = manzana.getSufzona();
                codMzna = manzana.getCodmzna();
                sufMzna = manzana.getSufmzna();
                estado = Integer.toString(manzana.getEstado());
                listaVertices = getLatLngShapeManzana(manzana.getShape());

                setNumeroManzanaText(getContext(),codMzna+""+sufMzna,listaVertices);

                GeoJsonPolygon geoJsonPolygon = new GeoJsonPolygon(Collections.singletonList(listaVertices));
                HashMap<String, String> properties = new HashMap<String, String>();
                properties.put("CODZONA", codZona);
                properties.put("SUFZONA", sufZona);
                properties.put("CODMZNA", codMzna);
                properties.put("SUFMZNA", sufMzna);
                properties.put("ESTADO", estado);
                GeoJsonFeature geoJsonFeature = new GeoJsonFeature(geoJsonPolygon, codMzna, properties, null);
                GeoJsonPolygonStyle geoJsonPolygonStyle = new GeoJsonPolygonStyle();
                geoJsonPolygonStyle.setStrokeWidth(3);
                if(!estado.equals("1"))
                {
                    geoJsonPolygonStyle.setZIndex(0f);
                    geoJsonPolygonStyle.setStrokeColor(Color.RED);
                }
                if(estado.equals("1"))
                {
                    geoJsonPolygonStyle.setZIndex(1f);
                    geoJsonPolygonStyle.setStrokeColor(Color.MAGENTA);
                }
                geoJsonFeature.setPolygonStyle(geoJsonPolygonStyle);
                layer.addFeature(geoJsonFeature);
            }
        }
    }

    /*3 EJECUTAR EVENTO DEPENDIENDO DEL ESTADO DEL POLIGONO*/
    @SuppressLint("RestrictedApi")
    public void setEventoEstado() {
        layer.setOnFeatureClickListener(new GeoJsonLayer.GeoJsonOnFeatureClickListener() {
            @Override
            public void onFeatureClick(Feature feature) {
                if(feature!=null){
                    int idManzana = getObjectManzanaCapturaXMzna(feature.getProperty("CODZONA"),feature.getProperty("SUFZONA"),feature.getProperty("CODMZNA"),feature.getProperty("SUFMZNA")).getEstado();
                    final int EVENTO = idManzana;
                        switch (EVENTO) {
                        case 0:
                            Log.e("mensajes:", "0");
                            visualizeMensajeManzana(feature.getProperty("CODZONA"),feature.getProperty("SUFZONA"),feature.getProperty("CODMZNA"),feature.getProperty("SUFMZNA"));
                            break;
                        case 1:
                            Log.e("mensajes:", "1");
                            visualizeEliminarManzana(feature.getProperty("CODZONA"),feature.getProperty("SUFZONA"),feature.getProperty("CODMZNA"),feature.getProperty("SUFMZNA"), feature.getProperty("ESTADO"), (GeoJsonFeature) feature);
                            break;
                        default:
                    }
                }
            }
        });
    }

    /*4. CARGAR MANZANA MODIFICADA(AÑADIDA)*/
    public void loadOnlyManzanaModificada(String codZona,String sufZona,String codMzna,String sufMzna, int estado) {
        if (getObjectManzanaCaptura(codZona,sufZona,codMzna,sufMzna) != null) {
            String codzona = getObjectManzanaCaptura(codZona,sufZona,codMzna,sufMzna).getCodzona();
            String sufzona = getObjectManzanaCaptura(codZona,sufZona,codMzna,sufMzna).getSufzona();
            String codmzna = getObjectManzanaCaptura(codZona,sufZona,codMzna,sufMzna).getCodmzna();
            String sufmzna = getObjectManzanaCaptura(codZona,sufZona,codMzna,sufMzna).getSufmzna();
            ArrayList<LatLng> listaVertices = getLatLngShapeManzana(getObjectManzanaCaptura(codZona,sufZona,codMzna,sufMzna).getShape());

            GeoJsonPolygon geoJsonPolygon = new GeoJsonPolygon(Collections.singletonList(listaVertices));
            HashMap<String, String> properties = new HashMap<String, String>();
            properties.put("CODZONA", codzona);
            properties.put("SUFZONA", sufzona);
            properties.put("CODMZNA", codmzna);
            properties.put("SUFMZNA", sufmzna);
            properties.put("ESTADO", Integer.toString(estado));
            GeoJsonFeature geoJsonFeature = new GeoJsonFeature(geoJsonPolygon, codmzna, properties, null);
            GeoJsonPolygonStyle geoJsonPolygonStyle = new GeoJsonPolygonStyle();
            geoJsonPolygonStyle.setStrokeWidth(3);
            geoJsonPolygonStyle.setZIndex(3f);
            switch (estado) {
                case 0:
                    geoJsonPolygonStyle.setStrokeColor(Color.RED);
                    break;
                case 1:
                    geoJsonPolygonStyle.setStrokeColor(Color.MAGENTA);
                    break;
                default:
            }
            geoJsonFeature.setPolygonStyle(geoJsonPolygonStyle);
            layer.addFeature(geoJsonFeature);
        } else {
            Toast.makeText(getContext(), "No se cargo manzana en el marco", Toast.LENGTH_SHORT).show();
        }
    }

    /*5. REMOVER FEATURE DE LAYER DE MANZANA SELECCIONADA*/
    public void removeFeature(GeoJsonFeature feature) {
        layer.removeFeature(feature);
    }

    /*6. VISUALIZAR DIALOGO RESTAURAR MANZANA*/
    public void visualizeEliminarManzana(final String codZona,final String sufZona,final String codMzna,final String sufMzna, final String estado, final GeoJsonFeature feature) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), R.style.ThemeOverlay_MaterialComponents_Dialog);
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.layout_form_poligono, null);
        final TextView mensaje = (TextView) dialogView.findViewById(R.id.id_form_mensaje);
        final LinearLayout ly = (LinearLayout) dialogView.findViewById(R.id.id_form_ly_mensaje);
        ly.setVisibility(View.VISIBLE);
        mensaje.setText("Desea Eliminar Manzana N° "+codMzna+""+sufMzna+" ?");
        alert.setTitle("Eliminar Manzana");
        alert.setIcon(R.drawable.ic_delete_forever_24);
        alert.setView(dialogView);
        alert.setPositiveButton("Eliminar", null);
        alert.setNegativeButton("Salir", null);
        final AlertDialog alertDialog = alert.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button b1 = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button b2 = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                b1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (estado.equals("1")) {
                            deleteManzanaCaptura(codZona,sufZona,codMzna,sufMzna);
                            removeFeature(feature);
                            Toast.makeText(getActivity().getApplicationContext(), "Manzana Eliminada", Toast.LENGTH_SHORT).show();
                        }
                        alertDialog.dismiss();
                    }
                });
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
            }
        });
        alertDialog.show();
    }

    /*************AÑADIR**********/
    /*1. MOSTRAR DIALOGO DE INFORMACION DE MANZANA*/
    public  void visualizeMensajeManzana(final String codZona,final String sufZona,final String codMzna,final String sufMzna){
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(),R.style.ThemeOverlay_MaterialComponents_Dialog);
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.layout_form_poligono, null);
        final TextView ubigeos  = (TextView) dialogView.findViewById(R.id.id_form_ubigeo);
        final TextView zona    = (TextView) dialogView.findViewById(R.id.id_form_zona);
        final TextView manzana = (TextView) dialogView.findViewById(R.id.id_form_manzana);
        final LinearLayout ly = (LinearLayout) dialogView.findViewById(R.id.id_form_ly_datos);
        ly.setVisibility(View.VISIBLE);
        ubigeos.setText(""+ubigeo);
        zona.setText(""+codZona);
        manzana.setText(""+codMzna+""+sufMzna);
        alert.setTitle("Información de Manzana");
        alert.setIcon(R.drawable.ic_info_outline);
        alert.setView(dialogView);
        alert.setNegativeButton("Salir",null);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button b = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
            }
        });
        alertDialog.show();
    }

    /*2. MOSTRAR DIALOGO PARA AÑADIR O DIBUJAR MANZANA*/
    public  void visualizeAnadirManzana(final String idmanzana){
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(),R.style.ThemeOverlay_MaterialComponents_Dialog);
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.layout_form_poligono, null);
        final TextView mensaje = (TextView) dialogView.findViewById(R.id.id_form_mensaje);
        final LinearLayout ly = (LinearLayout) dialogView.findViewById(R.id.id_form_ly_mensaje);
        ly.setVisibility(View.VISIBLE);
        mensaje.setText("Desea Añadir Manzana N°"+idmanzana+" ?");
        alert.setTitle("Añadir Manzana");
        alert.setIcon(R.drawable.ic_library_add_48_black);
        alert.setView(dialogView);
        alert.setPositiveButton("Dibujar",null);
        alert.setNegativeButton("Cancelar",null);
        final AlertDialog alertDialog = alert.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setEstadoEdicion();
                        createPolygon();
                        alertDialog.dismiss();
                    }
                });
            }
        });
        alertDialog.show();
    }

    /*3. INSERTAR MANZANA_CAPTURA A SQLITE INTERNO*/
    public  void saveManzanaCapturaAnadida(){
        final String numero = getNewManzanaAnadida(codigoZona);
        ArrayList<LatLng> listPointsDibujados = new ArrayList<>();
        if(listPoints.size()>2) {
            if (listPoints.size() == 3) {
                listPointsDibujados = listPoints;
                LatLng dato = listPointsDibujados.get(0);
                listPointsDibujados.add(dato);
            } else {
                listPointsDibujados = listPoints;
            }
            insertManzanaCaptura(numero," " ," ", accionManzana,listPointsDibujados);
            loadOnlyManzanaModificada(codigoZona,sufijoZona,numero," ", 1);
            cleanPolygon();
            setEstadoAnadir(true);
            Toast.makeText(getContext(), "Se registro Manzana correctamente!", Toast.LENGTH_SHORT).show();
        }
        else
        {Toast.makeText(getContext(),"Dibuje una Manzana",Toast.LENGTH_SHORT).show();}
    }

    /*******METODOS DE INTERACCION CON SQLITE***********/
    /*1. ELIMINAR MANZANA_CAPTURA EN SQLITE INTERNO*/
    public void insertManzanaCaptura(String codMzna,String sufMzna,String mznaBelong,int estado,ArrayList<LatLng> puntos) {
        try {
            data = new Data(context);
            data.open();
            data.insertManzanaCaptura("1", 2, "15", "01", "13", "001", "00", codMzna, sufMzna, mznaBelong, estado, 0, 2,"GeomFromText('POLYGON((" + formatGeom(puntos) + "))',4326)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*2. ELIMINAR MANZANA_CAPTURA EN SQLITE INTERNO*/
    public  void deleteManzanaCaptura(String codZona,String sufZona,String codMzna,String sufMzna){
        try {
            data = new Data(context);
            data.open();
            data.deleteManzanaCaptura(codZona,sufZona,codMzna,sufMzna);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*******METODOS DE EDICION DE POLIGONOS Y MAPA***********/
    /*LIMPIAR POLIGONO*/
    @SuppressLint("RestrictedApi")
    public void cleanPolygon(){
        fab2.setVisibility(View.GONE);
        fab4.setVisibility(View.GONE);
        fab5.setVisibility(View.GONE);

        if(poligon!=null)
        {
            ArrayList<LatLng> nulos = new ArrayList<>();
            nulos.add(new LatLng(0,0));
            poligon.remove();
            poligon.setPoints(nulos);
            listPoints.clear();
        }

        if(listaMarker!=null) {
            for (int i = 0; i < listaMarker.size(); i++) {
                listaMarker.get(i).remove();
            }

        }
        listPoints.clear();
        listaMarker.clear();
    }

    /*CREAR POLIGONO*/
    public void createPolygon(){
        poligon = mgoogleMap.addPolygon(new PolygonOptions()
                .add(new LatLng(1,1))
                .fillColor(Color.GREEN)
                .clickable(false)
                .zIndex(1f)
                .strokeWidth(5));
    }

    /*QUITAR CAPA*/
    public void removeLayer() {
        layer.removeLayerFromMap();
        Log.e("Mensaje:", "Capa removida");
    }

    /*ASIGNAR EDICION*/
    @SuppressLint("RestrictedApi")
    public void setEstadoEdicion() {
        fab3.setVisibility(View.GONE);
        fab2.setVisibility(View.VISIBLE);
        fab4.setVisibility(View.VISIBLE);
        fab5.setVisibility(View.VISIBLE);
    }

    /*METODO DE DESHACER ULTIMO PUNTO DIBUJADO*/
    public void undoPolygon(){
        if(listPoints.isEmpty())
        { Toast.makeText(getContext(),"No ha Dibujado una manzana",Toast.LENGTH_SHORT).show();}
        else{

            listaMarker.get(listaMarker.size()-1).remove();
            listaMarker.remove(listaMarker.size()-1);
            listPoints.remove(listPoints.size()-1);
            poligon.remove();
            if(listPoints.isEmpty())
            {    poligon = mgoogleMap.addPolygon(new PolygonOptions()
                    .add(new LatLng(1, 1))
                    .fillColor(Color.GREEN)
                    .strokeWidth(5));
            }
            else{poligon = mgoogleMap.addPolygon(new PolygonOptions()
                    .addAll(listPoints)
                    .fillColor(Color.GREEN)
                    .strokeWidth(5));}
        }
    }

    @SuppressLint("RestrictedApi")
    public void setEstadoAnadir(boolean estado) {
        if(estado){
            fab3.setVisibility(View.VISIBLE);
        }
        else{
            fab3.setVisibility(View.GONE);
        }
    }

    /*******METODOS DE FLUJOS***********/

    /*******CREACION DE ID DE NUEVA MANZANA********/
    /*OBTENER ID DE MANZANA AÑADIDA*/
    public String getNewManzanaAnadida(String idzona){
        ArrayList<String> listaManzana1 = new ArrayList<>();
        ArrayList<String> listaManzana2 = new ArrayList<>();
        int mayor;
        listaManzana1 = getListaManzanasXZona(idzona);
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

    /*OBTENER LISTA(CODIGOMANZANA)DE STRING POR ZONA DE SQLITE*/
    public ArrayList<String> getListaManzanasXZona(String idzona){
        ArrayList<String> lista = new ArrayList<>();
        try {
            Data data = new Data(context);
            data.open();
            lista = data.getListaManzanasXZona(idzona);
            data.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return lista;
    }

    /*ASIGNAR DE DIGITOS*/
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

    /*LIMPIAR DIGITOS*/
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
    /*OBTENER CADENA SIN ULTIMO DIGITO*/
    public static String getDigito(String cadena){
        String ultimo = cadena.substring(0, cadena.length() - 1);
        return  ultimo;
    }

    /**************MANZANA CAPTURA************/

    /*OBTENER LISTA(MANZANACAPTURA)DE OBJETO POR ESTADO DE SQLITE*/
    public ArrayList<ManzanaCaptura> getListaManzanaCapturaXZona(){
        ArrayList<ManzanaCaptura> listaManzana = new ArrayList<>();
        try {
            Data data = new Data(context);
            data.open();
            listaManzana= data.getAllManzanaCapturaXZona(codigoZona);
            data.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return listaManzana;
    }

    public ManzanaCaptura getObjectManzanaCaptura(String codZona,String sufZona,String codMzna,String sufMzna) {
        ManzanaCaptura manzanaCaptura = null;
        try {
            Data data = new Data(context);
            data.open();
            manzanaCaptura = data.getManzanaCaptura(codZona,sufZona,codMzna,sufMzna);
            data.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.getCause();
        }
        return manzanaCaptura;
    }

    /*OBTENER UN REGISTRO DEL OBJETO CAPTURA MANZANA X ID*/
    public ManzanaCaptura getObjectManzanaCapturaXMzna(String codZona,String sufZona,String codMzna,String sufMzna) {
        ManzanaCaptura manzanaCaptura = null;
        try {
            Data data = new Data(context);
            data.open();
            manzanaCaptura = data.getManzanaCapturaXMzna(codZona,sufZona,codMzna,sufMzna);
            data.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.getCause();
        }
        return manzanaCaptura;
    }

    /*METODO CONVERTIR LISTA(STRING) DE SQLITE A LISTA(LATLNG)*/
    public ArrayList<LatLng> getLatLngShapeManzana(String shape){
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

    /*METODO DE FORMATO POLIGONO PARA INSERCION A SQLITE (SPATIALITE) */
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

    /*ASIGNAR NUMERO DE MANZANA POLIGONO*/
    public Marker setNumeroManzanaText(final Context context,String codmanzana,ArrayList<LatLng> latLngList) {
        Marker marker = null;

        final TextView textView = new TextView(context);
        textView.setText(codmanzana);
        textView.setTextSize(16);

        final Paint paintText = textView.getPaint();

        final Rect boundsText = new Rect();
        paintText.getTextBounds(codmanzana, 0, textView.length(), boundsText);
        paintText.setTextAlign(Paint.Align.CENTER);

        final Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        final Bitmap bmpText = Bitmap.createBitmap(boundsText.width() + 2
                * 16, boundsText.height() + 2 * 16, conf);

        final Canvas canvasText = new Canvas(bmpText);
        paintText.setColor(Color.BLACK);

        canvasText.drawText(codmanzana, canvasText.getWidth() / 2,
                canvasText.getHeight() - 16 - boundsText.bottom, paintText);

        final MarkerOptions markerOptions = new MarkerOptions()
                .position(getCenterOfPolygon(latLngList))
                .icon(BitmapDescriptorFactory.fromBitmap(bmpText))
                .zIndex(-1f)
                .anchor(0.5f, 1);

        marker = mgoogleMap.addMarker(markerOptions);

        return marker;
    }

    /*OBTENER CENTRO DE POLIGONO*/
    public LatLng getCenterOfPolygon(ArrayList<LatLng> latLngList) {
        double[] centroid = {0.0, 0.0};
        for (int i = 0; i < latLngList.size(); i++) {
            centroid[0] += latLngList.get(i).latitude;
            centroid[1] += latLngList.get(i).longitude;
        }
        int totalPoints = latLngList.size();

        return new LatLng(centroid[0] / totalPoints, centroid[1] / totalPoints);
    }

}
