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
import android.widget.LinearLayout;
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
import com.google.maps.android.data.Layer;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPolygon;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;
import com.inei.appcartoinei.R;
import com.inei.appcartoinei.dialogs.DialogFusion;
import com.inei.appcartoinei.dialogs.DialogFusionManzana;
import com.inei.appcartoinei.modelo.DAO.Data;
import com.inei.appcartoinei.modelo.DAO.DataBaseHelper;
import com.inei.appcartoinei.modelo.pojos.FusionItem;
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
    private LocationManager mLocationManager;
    private Location location;
    private Polygon poligon;
    private Marker vertice;
    private ArrayList<Marker> listaMarker = new ArrayList<Marker>();
    private ArrayList<LatLng> listPoints = new ArrayList<LatLng>() ;

    private int idAccionManzana = 1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;
    private FloatingActionButton fab4;
    private FloatingActionButton fab5;
    private SQLiteDatabase db ;
    private DataBaseHelper op;
    Data    data;
    Context context;

    GeoJsonLayer layer;
    GeoJsonPolygonStyle polygonStyle;

    private OnFragmentInteractionListener mListener;

    public MapAnadirManzanaFragment() {
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
            public void onClick(View v) {
                insertManzanaCaptura();
            }
        });

        /*MUESTRA DIALOGO PARA AÑADIR MANZANA */
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visualizeSeleccionManzana(getNewManzana(getListaManzanas("001")));
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
                addLayerGeojson(1);
            }
        });
        /*MOSTRAR CARGA DE TRABAJO*/
        addLayerGeojson(1);
        /*MOSTRAR MANZANAS AÑADIDAS*/
        loadAllManzanaAnadidas();

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

    /*******METODOS DE INTERACCION CON EL MAPA Y SQLITE***********/

    /*1. CARGAR LAYER MARCO DE TRABAJO*/
    @SuppressLint("RestrictedApi")
    public  void addLayerGeojson(int estado){
        switch (estado){
            case 0:
                //Crea poligono y muestra botones de edicion de poligono
                Log.e("mensajes:","0->sin capa, dibujar poligono");
                fab2.setVisibility(View.VISIBLE);
                fab3.setVisibility(View.GONE);
                fab4.setVisibility(View.VISIBLE);
                fab5.setVisibility(View.VISIBLE);
                break;
            case 1:
                //Muestra el marco de trabajo en poligonos rojos
                Log.e("mensajes:","1->capa roja,marco de trabajo");
                fab3.setVisibility(View.VISIBLE);
                try {
                    layer = new GeoJsonLayer(mgoogleMap, R.raw.marco_jmaria,getContext());
                    layer.addLayerToMap();
                    polygonStyle = layer.getDefaultPolygonStyle();
                    polygonStyle.setStrokeWidth(3);
                    polygonStyle.setZIndex(0f);
                    polygonStyle.setStrokeColor(Color.RED);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Evento Click al seleccionar manzana
                layer.setOnFeatureClickListener(new GeoJsonLayer.GeoJsonOnFeatureClickListener() {
                    @Override
                    public void onFeatureClick(Feature feature) {
                        if(validatePolygon(feature.getProperty("CODZONA"),feature.getProperty("CODMZNA")+""+feature.getProperty("SUFMZNA"))){
                            visualizeBorrarManzana(feature.getProperty("CODMZNA")+""+feature.getProperty("SUFMZNA"), (GeoJsonFeature) feature); }
                        else{
                            if(listPoints.isEmpty())
                            {visualizeMensajeManzana(feature.getProperty("CODMZNA")+""+feature.getProperty("SUFMZNA"));}
                            else{
                                Log.e("mensajes:","Seguir pintando");
                            }
                        }
                    }
                });
                break;
            default:
        }
    }

    /*2. CARGAR TODAS LAS MANZANAS AÑADIDAS*/
    private void loadAllManzanaAnadidas(){
        ArrayList<LatLng> listaVertices = new ArrayList<>();
        String codzona = "";
        String codmzna = "";
        if(getListaManzanaCaptura(1).isEmpty())
        {Toast.makeText(getContext(),"No se Añadieron Manzanas",Toast.LENGTH_SHORT).show();}
        else{
            for(int i=0;i<getListaManzanaCaptura(1).size();i++)
            {
                codzona = getListaManzanaCaptura(1).get(i).getCodzona();
                codmzna = getListaManzanaCaptura(1).get(i).getCodmzna();
                listaVertices = getLatLngShapeManzana(getListaManzanaCaptura(1).get(i).getShape());
                if(listaVertices.size()>0)
                {
                    GeoJsonPolygon geoJsonPolygon = new GeoJsonPolygon(Collections.singletonList(listaVertices));
                    HashMap<String, String> properties = new HashMap<String, String>();
                    properties.put("CODZONA",codzona);
                    properties.put("CODMZNA",codmzna);
                    properties.put("SUFMZNA","");
                    GeoJsonFeature geoJsonFeature = new GeoJsonFeature(geoJsonPolygon,codmzna,properties,null);

                    GeoJsonPolygonStyle geoJsonPolygonStyle = new GeoJsonPolygonStyle();
                    //geoJsonPolygonStyle.setFillColor(Color.MAGENTA);
                    geoJsonPolygonStyle .setStrokeColor(Color.MAGENTA);
                    geoJsonPolygonStyle .setStrokeWidth(3);
                    geoJsonPolygonStyle .setZIndex(2f);
                    geoJsonFeature.setPolygonStyle(geoJsonPolygonStyle);
                    layer.addFeature(geoJsonFeature);
                }
            }
            for (GeoJsonFeature feature : layer.getFeatures()) {
                Log.e("mensajes-id-mzna :",""+feature.getId()+"->"+feature.getProperty("CODMZNA"));

            }
        }
    }

    /*3. MOSTRAR DIALOGO DE INFORMACION DE MANZANA*/
    public  void visualizeMensajeManzana(final String idmanzana){
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(),R.style.ThemeOverlay_MaterialComponents_Dialog);
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.layout_form_poligono, null);
        final TextView manzana = (TextView) dialogView.findViewById(R.id.id_form_manzana);
        final LinearLayout ly = (LinearLayout) dialogView.findViewById(R.id.id_form_ly_datos);
        ly.setVisibility(View.VISIBLE);
        manzana.setText(idmanzana);
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

    /*4. MOSTRAR DIALOGO PARA AÑADIR O DIBUJAR MANZANA*/
    public  void visualizeSeleccionManzana(final String idmanzana){
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
                        addLayerGeojson(0);
                        createPolygon();
                        alertDialog.dismiss();
                    }
                });
            }
        });
        alertDialog.show();
    }

    /*5. INSERTAR MANZANA_CAPTURA A SQLITE INTERNO*/
    public  void insertManzanaCaptura(){
        final String numero = getNewManzana(getListaManzanas("001"));
        if(listPoints.size()>2) {
            if (listPoints.size() == 3) {
                LatLng dato = listPoints.get(0);
                listPoints.add(dato);
                try {
                    data = new Data(context);
                    data.open();
                    data.insertManzanaCaptura(1, 2, "15", "01", "13", "001", "00", numero, "","", idAccionManzana, 0, "GeomFromText('POLYGON((" + formatGeom(listPoints) + "))',4326)");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                loadOnlyManzanaAnadida(numero, 1, 1, null);
                cleanPolygon();
                Toast.makeText(getContext(), "Se registro Manzana correctamente!", Toast.LENGTH_SHORT).show();
            }
            else{
                try {
                    data = new Data(context);
                    data.open();
                    data.insertManzanaCaptura(1, 2, "15", "01", "13", "001", "00", numero, "","", idAccionManzana, 0, "GeomFromText('POLYGON((" + formatGeom(listPoints) + "))',4326)");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                loadOnlyManzanaAnadida(numero, 1, 1, null);
                cleanPolygon();
                Toast.makeText(getContext(), "Se registro Manzana correctamente!", Toast.LENGTH_SHORT).show();

            }
        }
        else
        {Toast.makeText(getContext(),"Dibuje una Manzana",Toast.LENGTH_SHORT).show();}
    }

    /*6. MOSTRAR DIALOGO PARA ELIMINACION DE MANZANA */
    public  void visualizeBorrarManzana(final String idmanzana, final GeoJsonFeature feature){
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(),R.style.ThemeOverlay_MaterialComponents_Dialog);
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.layout_form_poligono, null);
        final TextView mensaje = (TextView) dialogView.findViewById(R.id.id_form_mensaje);
        final LinearLayout ly = (LinearLayout) dialogView.findViewById(R.id.id_form_ly_mensaje);
        ly.setVisibility(View.VISIBLE);
        mensaje.setText("Desea Eliminar Manzana N°"+idmanzana+"?");
        alert.setTitle("Eliminar Manzana");
        alert.setIcon(R.drawable.ic_delete_forever_24);
        alert.setView(dialogView);
        alert.setPositiveButton("Si",null);
        alert.setNegativeButton("No",null);
        final AlertDialog alertDialog = alert.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button b1 = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button b2 = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                b1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteManzanaCaptura(idmanzana.trim(),1,0,feature);
                        alertDialog.dismiss();
                        Toast.makeText(getActivity().getApplicationContext(), "Manzana Eliminada", Toast.LENGTH_SHORT).show();
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

    /*7. ELIMINAR MANZANA_CAPTURA EN SQLITE INTERNO*/
    public  void deleteManzanaCaptura(String idmanzana,int estado,int caso,GeoJsonFeature feature){
        loadOnlyManzanaAnadida(idmanzana,estado,caso,feature);
        try {
            data = new Data(context);
            data.open();
            data.deleteManzanaCaptura(idmanzana);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*8. CARGAR O REMOVER MANZANA AÑADIDA*/
    @SuppressLint("RestrictedApi")
    private void loadOnlyManzanaAnadida(String idmanzana, int estado, int caso, GeoJsonFeature feature){
        if(getObjectManzanaCaptura(idmanzana,estado)!=null){
            String codzona = getObjectManzanaCaptura(idmanzana,estado).getCodzona();
            String codmzna = getObjectManzanaCaptura(idmanzana,estado).getCodmzna();
            ArrayList<LatLng> listaVertices = getLatLngShapeManzana(getObjectManzanaCaptura(idmanzana,estado).getShape());
            switch (caso){
                case 0:
                    //Elimina manzana añadida
                    layer.removeFeature(feature);
                    break;
                case 1:
                    //Muestra el boton '+' y pinta el feature polygon
                    fab3.setVisibility(View.VISIBLE);
                    GeoJsonPolygon geoJsonPolygon = new GeoJsonPolygon(Collections.singletonList(listaVertices));
                    HashMap<String, String> properties = new HashMap<String, String>();
                    properties.put("CODZONA",codzona);
                    properties.put("CODMZNA",codmzna);
                    properties.put("SUFMZNA","");
                    GeoJsonFeature geoJsonFeature = new GeoJsonFeature(geoJsonPolygon,codmzna,properties,null);
                    GeoJsonPolygonStyle geoJsonPolygonStyle = new GeoJsonPolygonStyle();
                    geoJsonPolygonStyle .setStrokeColor(Color.MAGENTA);
                    geoJsonPolygonStyle .setStrokeWidth(3);
                    geoJsonPolygonStyle .setZIndex(3f);
                    geoJsonFeature.setPolygonStyle(geoJsonPolygonStyle );
                    layer.addFeature(geoJsonFeature);
                    break;
                default:
            }
        }
        else{
            Toast.makeText(getContext(),"No se cargo manzana en el marco",Toast.LENGTH_SHORT).show();
        }
    }

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

    /*CREACION DE ID DE MANZANA AÑADIDA*/
    public String getNewManzana(ArrayList<String> lista){
        ArrayList<String> listaManzana = new ArrayList<>();
        listaManzana = filtrarNumero(lista);
        int mayor;
        if(listaManzana.size()>0){
            mayor = Integer.parseInt(listaManzana.get(0));
            for(int i=0;i<listaManzana.size();i++)
            {
                int numero = Integer.parseInt(listaManzana.get(i));
                if (numero>mayor)
                {mayor =numero;}
            }
        }
        else {
            mayor=0;
        }
        //return "0"+(mayor+1);
        return countDigitos(mayor+1);
    }

    /*VALIDAR SI EXISTE O POLIGONO SELECCIONADO EN SQLITE*/
    public boolean validatePolygon(String idzona,String idmzna){
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

    /*OBTENER LISTA(CODIGOMANZANA)DE STRING POR ZONA DE SQLITE*/
    public ArrayList<String> getListaManzanas(String idzona){
        ArrayList<String> lista = new ArrayList<>();
        try {
            Data data = new Data(context);
            data.open();
            lista = data.getListaManzanas(idzona);
            data.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return lista;
    }

    /*OBTENER OBJETO(MANZANACAPTURA) POR ESTADO DE SQLITE*/
    public ManzanaCaptura getObjectManzanaCaptura(String idmzna,int estado){
        ManzanaCaptura manzanaCaptura = null;
        try {
            Data data = new Data(context);
            data.open();
            manzanaCaptura = data.getManzanaCapturaXIdEstado(idmzna,estado);
            Log.e("Mensajezx:",""+manzanaCaptura);
            data.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        catch (NullPointerException e){
            e.getCause();
        }
        return manzanaCaptura;
    }

    /*OBTENER LISTA(MANZANACAPTURA)DE OBJETO POR ESTADO DE SQLITE*/
    public ArrayList<ManzanaCaptura> getListaManzanaCaptura(int estado){
        ArrayList<ManzanaCaptura> listaManzana = new ArrayList<>();
        try {
            Data data = new Data(context);
            data.open();
            listaManzana= data.getAllManzanaCapturaXEstado(estado);
            data.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return listaManzana;
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

    /*CONTADOR DE DIGITOS*/
    public static String countDigitos(int numero){
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

    /*VERIFICAR SI ES NUMERO*/
    public static boolean checkNumero(String numero){
        try{
            Integer.parseInt(numero);
            return true;
        }catch(NumberFormatException e){
            return false;
        }
    }

    /*FILTRAR ARRAYLIST*/
    public static ArrayList<String>  filtrarNumero(ArrayList<String> lista){
        ArrayList<String> newlista = new ArrayList<>();
        for(int i=0;i<lista.size();i++){
            if(checkNumero(lista.get(i)))
            {newlista.add(lista.get(i));}
        }
        return newlista;
    }

}
