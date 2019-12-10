package com.inei.appcartoinei.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cocoahero.android.geojson.GeoJSON;
import com.cocoahero.android.geojson.GeoJSONObject;
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
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPolygon;
import com.inei.appcartoinei.R;
import com.inei.appcartoinei.modelo.ConexionSpatiaLiteHelper;
import com.inei.appcartoinei.modelo.DAO.Data;
import com.inei.appcartoinei.modelo.DAO.DataBaseHelper;
import com.inei.appcartoinei.modelo.pojos.Capa;
import com.inei.appcartoinei.modelo.pojos.Departamento;
import com.inei.appcartoinei.modelo.pojos.Manzana;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.spatialite.database.SQLiteDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MapManzanaFragment extends Fragment implements OnMapReadyCallback,GoogleMap.OnMapClickListener {

    GoogleMap mgoogleMap;
    MapView mapView;
    View view;
    InputStream stream;
    private LocationManager mLocationManager;
    private Location location;
    private static final int LOCATION_REQUEST_CODE  = 1 ;
    public static final int LOCATION_UPDATE_MIN_DISTANCE = 10;
    public static final int LOCATION_UPDATE_MIN_TIME = 5000;
    private final static int COLOR_FILL_POLYGON = 0x7F00FF00;
    private final static int COLOR_FILL_POLYGON_GREEN = 0x5500ff00;

    private Polygon poligon;
    private ArrayList<LatLng> listPoints = new ArrayList<LatLng>() ;
    private ArrayList<LatLng> lista = new ArrayList<LatLng>();
    private ArrayList<String> datosManzana = new ArrayList<String>() ;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;

    private SQLiteDatabase db ;
    private DataBaseHelper op;
    private ConexionSpatiaLiteHelper conn;
    private RequestQueue mQueue;
    Data    data;
    Context context;
    String  idCapa;
    Cursor res=null;


    private OnFragmentInteractionListener mListener;

    public MapManzanaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

//        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
//            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,LOCATION_UPDATE_MIN_TIME,LOCATION_UPDATE_MIN_DISTANCE, (LocationListener) this);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_capa3, container, false);
        idCapa = getArguments().getString("idUsuario","0");
        return view;
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        mapView = (MapView) view.findViewById(R.id.map);
        fab1 =  (FloatingActionButton) view.findViewById(R.id.fab1);
        fab2 =  (FloatingActionButton) view.findViewById(R.id.fab2);
        fab3 =  (FloatingActionButton) view.findViewById(R.id.fab3);

        op = new DataBaseHelper(getContext());
        db = op.getWritableDatabase();
        mQueue = Volley.newRequestQueue(getContext());

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
        LatLng peru = new LatLng(-9, -74);

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.addMarker(new MarkerOptions().position(new LatLng(40.689247,-74.044502)).title("prueba").snippet("xxx"));
        CameraPosition Liberty = CameraPosition.builder().target(new LatLng(40.689247,-74.044502)).zoom(16).bearing(0).tilt(45).build();
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

        /*SUBIR DATOS SERVIDOR*/
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                   exportarManzanaxx();
            }
        });

        /*INSERTAR GEOMETRIA + 3 CAMPOS*/
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertarManzana(listPoints);

            }
        });

        /*ABRIR DIALOGO E INSERTAR DATOS*/
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                formManzana();
            }
        });


//        PolygonOptions rectOptions = new PolygonOptions()
//                .add(
//                new LatLng(-12.067416,-77.0492049),
//                new LatLng(-12.068313,-77.0499774),
//                new LatLng(-12.0690894,-77.049044),
//                new LatLng(-12.0681504,-77.0482662),
//                new LatLng(-12.0677622,-77.0487785),
//                new LatLng(-12.067416,-77.0492049)
//        );

//        Polygon polygono = googleMap.addPolygon(new PolygonOptions()
//                .add(   new LatLng(-12.067416,-77.0492049),
//                        new LatLng(-12.068313,-77.0499774),
//                        new LatLng(-12.0690894,-77.049044),
//                        new LatLng(-12.0681504,-77.0482662),
//                        new LatLng(-12.0677622,-77.0487785),
//                        new LatLng(-12.067416,-77.0492049))
//                .strokeColor(Color.BLUE)
//                .strokeWidth(2)
//                .strokeJointType(JointType.ROUND)
//                .visible(true));
//
//        polygono.setClickable(true);
//        polygono.setStrokeJointType(JointType.ROUND);
//        googleMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
//            @Override
//            public void onPolygonClick(Polygon polygon) {
//                Toast.makeText(getContext(),"",Toast.LENGTH_SHORT).show();
//            }
//        });

        lista.add(new LatLng(-12.067416,-77.0492049));
        lista.add(new LatLng(-12.067416,-77.0492050));
        lista.add(new LatLng(-12.067416,-77.0492051));
        lista.add(new LatLng(-12.067416,-77.0492052));

        Log.i("contador_jux1",""+lista);
        for(int i=0;i<lista.size();i++){
            Log.i("contador_jux2",""+lista.get(i));
        }
        String data = "{\"type\":\"Polygon\",\"coordinates\":[[[-76.93461824208498,-12.22585474309521],[-76.93433459848165,-12.22569352831877],[-76.93467758595943,-12.2249441404559],[-76.93499341607093,-12.2251410720589]]]}";
        try {
            JSONObject geom = new JSONObject(data);
            //GeoJSONObject geoJSON = GeoJSON.parse(data);
            GeoJsonLayer layer = new GeoJsonLayer(googleMap,geom);
        } catch (JSONException e) {
            e.printStackTrace();
        }




    }

    @Override
    public void onMapClick(LatLng latLng) {
        listPoints.add(latLng);
        poligon = mgoogleMap.addPolygon(new PolygonOptions().addAll(listPoints).strokeJointType(JointType.DEFAULT));
        //poligon = mgoogleMap.addPolygon(new PolygonOptions().add(new LatLng(0, 0), new LatLng(0, 0), new LatLng(0, 0)).strokeJointType(JointType.DEFAULT));



        if(listPoints.size() > 1){
            poligon.setFillColor(Color.GREEN);
            poligon.setStrokeWidth(5);
            poligon.setClickable(true);
            poligon.setStrokeColor(Color.BLACK);
            poligon.setStrokeJointType(JointType.ROUND);
        }

        Marker marker = mgoogleMap.addMarker(new
                MarkerOptions().
                position(latLng).
                icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_edit_location)));
    }


    /*METODO INSERTAR MANZANA A SQLITE INTERNO*/
    /*OBJETO*/
    public  void insertarManzanax(ArrayList<LatLng> poligono){
        Polygon poligonAdd = mgoogleMap.addPolygon(new PolygonOptions().add(new LatLng(0, 0), new LatLng(0, 0), new LatLng(0, 0)).fillColor(COLOR_FILL_POLYGON_GREEN).strokeWidth(8));
        if(datosManzana.size()>0)
        { if(listPoints.size()>0) {
            poligonAdd.setPoints(poligono);
            try {
            data = new Data(context);
            data.open();
            data.insertManzana(new Manzana(1,2,"001",datosManzana.get(0),"002",datosManzana.get(1),datosManzana.get(2)+datosManzana.get(3)+datosManzana.get(4),"GeomFromText('POLYGON(("+formatGeom(poligono)+"))',4326)"));
            data.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(getContext(),"Se Registro Información",Toast.LENGTH_SHORT).show();
            listPoints.clear();
            datosManzana.clear();
        }
        else
        {Toast.makeText(getContext(),"Ingrese Poligono!",Toast.LENGTH_SHORT).show();}
        }
        else{Toast.makeText(getContext(),"Ingrese valores (Ubigeo,Manzana,Zona)!",Toast.LENGTH_SHORT).show();}
    }
    /*STRING*/
    public  void insertarManzana(ArrayList<LatLng> poligono){
        Polygon poligonAdd = mgoogleMap.addPolygon(new PolygonOptions().add(new LatLng(0, 0), new LatLng(0, 0), new LatLng(0, 0)).fillColor(COLOR_FILL_POLYGON_GREEN).strokeWidth(8));
        if(datosManzana.size()>0)
        { if(listPoints.size()>0) {
            String query = "INSERT INTO manzana(id,iduser,idmanzana,nommanzana,idzona,zona,ubigeo,shape) VALUES (1,2,'001','"+datosManzana.get(0)+"','002','"+datosManzana.get(1)+"','"+datosManzana.get(2)+datosManzana.get(3)+datosManzana.get(4)+"',GeomFromText('POLYGON(("+formatGeom(poligono)+"))',4326));" ;
            db.execSQL(query);
            Toast.makeText(getContext(),"Se Registro Información de Manzana",Toast.LENGTH_SHORT).show();
            poligonAdd.setPoints(poligono);
            listPoints.clear();
            datosManzana.clear();
            Log.d("query",query);
        }
        else
        {Toast.makeText(getContext(),"Ingrese Poligono!",Toast.LENGTH_SHORT).show();}
        }
        else{Toast.makeText(getContext(),"Ingrese valores (Ubigeo,Manzana,Zona)!",Toast.LENGTH_SHORT).show();}

    }


    /*METODO DE OBTENCION DE MANZANA DE SQLITE INTERNO*/
    public void exportarManzana(){
        String queryJson = "SELECT id,iduser,idmanzana,nommanzana,idzona,zona,ubigeo,AsGeoJSON(shape) geom from manzana where id=1;" ;
        String queryJson2 = "SELECT i, AsText(shape) as geom from manzana where geom not null";
        try {
            Cursor res=db.rawQuery(queryJson, null);
            Log.i("contador_res", "" +res);
            int contador = res.getCount();
            if (contador > 0) {
                Log.i("contador", "" + contador);
                res.moveToFirst();
                while (res.isAfterLast() == false) {
                    Log.i("contador1", "" + contador);
                    String campoUbigeo = res.getString(res.getColumnIndex("ubigeo"));
                    String campoZona =    res.getString(res.getColumnIndex("idzona"));
                    String campoManzana = res.getString(res.getColumnIndex("nommanzana"));
                    String campoGeom =    res.getString(res.getColumnIndex("geom"));
                    String stringJsonFinal = "";
                    Log.i("contador_Geomm", "" + campoGeom);
                    try {

//                        JSONArray json = new JSONArray(campoGeom);
//                        JSONObject objeto = json.getJSONObject(1);
//                        String nombre = objeto.getString("coordinates");


                        Log.i("contador2", "" + contador);
                        JSONObject geom = new JSONObject(campoGeom);
                        Log.i("Contador_geom",""+geom);
                        String rings = geom.get("coordinates").toString();

                        //JSONObject json = geom.getJSONObject("coordinates");
                        //Log.i("Contador_Juxe",""+json);



                        Log.i("Contador_ring",""+rings);
                        stringJsonFinal = "{\"geometry\":{\"rings\":" + rings + ", \"spatialReference\" : {\"wkid\" : 4326}},\"attributes\":{\"UBIGEO\":" + campoUbigeo + ",\"ZONA\":" + campoZona + ",\"MANZANA\":" + campoManzana + "}}";
                        JSONArray arrayGeom = new JSONArray();
                        Log.i("contador_JsonFinal", "" + stringJsonFinal);
                        //arrayGeom.put();
                        JSONObject obj = new JSONObject(stringJsonFinal);
                        Log.i("contador_obj", "" + obj);
                        arrayGeom.put(obj);
                        Log.d("contador_arraygeon", arrayGeom.toString());
                        //insertarServicio(arrayGeom);
                    } catch (Throwable tx) {
                        Log.e("My App", "Could not parse malformed JSON: \"" + stringJsonFinal + "\"");
                    }
                    res.moveToNext();
                }
            } else {
                Toast.makeText(getContext(), "No hay Registros para subir...", Toast.LENGTH_SHORT).show();
            }
        }catch (NullPointerException e){
            e.getMessage();
            Toast.makeText(getContext(), "Error de lista"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }



    }

    public void exportarManzanax(){
        //String queryJson = "SELECT id,iduser,idmanzana,nommanzana,idzona,zona,ubigeo,AsGeoJSON(shape) geom from manzana where id=1;" ;
        String queryJson = "SELECT id,iduser,idmanzana,nommanzana,idzona,zona,ubigeo,AsText(shape) as geom from manzana where geom not null";
        try {
            Cursor res=db.rawQuery(queryJson, null);
            Log.i("contador_res", "" +res);
            int contador = res.getCount();
            if (contador > 0) {
                Log.i("contador", "" + contador);
                res.moveToFirst();
                while (res.isAfterLast() == false) {
                    Log.i("contador1", "" + contador);
                    String campoUbigeo = res.getString(res.getColumnIndex("ubigeo"));
                    String campoZona =    res.getString(res.getColumnIndex("idzona"));
                    String campoManzana = res.getString(res.getColumnIndex("nommanzana"));
                    String campoGeom =    res.getString(res.getColumnIndex("geom"));
                    String stringJson1 = "";
                    String stringJson2 = "";
                    String stringJson3 = "";
                    String stringJson4 = "";
                    Log.i("contador_Geomm", "" + campoGeom);
                    stringJson1 = campoGeom.replace("POLYGON((","[lat/lng: (");
                    Log.i("contador_j1", "" + stringJson1);
                    stringJson2 = stringJson1.replace(", -","),lat/lng: (-");
                    Log.i("contador_j2", "" + stringJson2);
                    stringJson3 = stringJson2.replace(" -",",-");
                    Log.i("contador_j3", "" + stringJson3);
                    stringJson4 = stringJson3.replace("))",")]");
                    Log.i("contador_j4", "" + stringJson4);
                    res.moveToNext();
                }
            } else {
                Toast.makeText(getContext(), "No hay Registros para subir...", Toast.LENGTH_SHORT).show();
            }
        }catch (NullPointerException e){
            e.getMessage();
            Toast.makeText(getContext(), "Error de lista"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }



    }

    public void exportarManzanaxx(){
        String queryJson = "SELECT id,iduser,idmanzana,nommanzana,idzona,zona,ubigeo,AsGeoJSON(shape) geom from manzana where id=1;" ;
        //String queryJson = "SELECT id,iduser,idmanzana,nommanzana,idzona,zona,ubigeo,AsText(shape) as geom from manzana where geom not null";
        try {
            Cursor res=db.rawQuery(queryJson, null);
            Log.i("contador_res", "" +res);
            int contador = res.getCount();
            if (contador > 0) {
                Log.i("contador", "" + contador);
                res.moveToFirst();
                while (res.isAfterLast() == false) {
                    Log.i("contador1", "" + contador);
                    String campoUbigeo = res.getString(res.getColumnIndex("ubigeo"));
                    String campoZona =    res.getString(res.getColumnIndex("idzona"));
                    String campoManzana = res.getString(res.getColumnIndex("nommanzana"));
                    String campoGeom =    res.getString(res.getColumnIndex("geom"));
                    String stringJson1 = "";
                    String stringJson2 = "";
                    String stringJson3 = "";
                    String stringJson4 = "";
                    Log.i("contador_Geomm", "" + campoGeom);

                    try {
                        GeoJSONObject geoJSON = GeoJSON.parse(campoGeom);
                        //GeoJsonLayer layer = new GeoJsonLayer(googleMap,geom);
                        Log.i("contador_geojson", "" + geoJSON.toJSON().getString("coordinates"));
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }


                    stringJson1 = campoGeom.replace("POLYGON((","lat/lng: (");
                    Log.i("contador_j1", "" + stringJson1);
                    stringJson2 = stringJson1.replace(", -",");lat/lng: (-");
                    Log.i("contador_j2", "" + stringJson2);
                    stringJson3 = stringJson2.replace(" -",",-");
                    Log.i("contador_j3", "" + stringJson3);
                    stringJson4 = stringJson3.replace("))",")");
                    Log.i("contador_j4", "" + stringJson4);
                    String num = ""+stringJson4;
                    String str[] = num.split(";");
                    List<String> al = new ArrayList<String>();
                    al = Arrays.asList(str);
                    Log.i("contador_j5", "" + al);
                    for(String s: al){
                        System.out.println(s);
                    }



                    res.moveToNext();
                }
            } else {
                Toast.makeText(getContext(), "No hay Registros para subir...", Toast.LENGTH_SHORT).show();
            }
        }catch (NullPointerException e){
            e.getMessage();
            Toast.makeText(getContext(), "Error de lista"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }



    }

    /*METODO DE INSERCION DE MANZANA A SQLSERVER MEDIANTE SERVICIO GIS*/
    public void insertarServicio( final JSONArray arrayGeom){
        String url = "http://arcgis4.inei.gob.pe:6080/arcgis/rest/services/DESARROLLO/servicio_prueba_captura/FeatureServer/0/addFeatures";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("SUCCESS", response);
                String queryJsonUpdate = "UPDATE poligonos SET export =1  where export=0;" ;
                db.execSQL(queryJsonUpdate);
                Toast.makeText(getContext(),"Se Inserto en el Servidor Correctamente!",Toast.LENGTH_SHORT).show();
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

    /*METODO DE FORMATO A POLYGONO*/
    public String formatGeom(ArrayList<LatLng> poligono){
        String format ="";
        for (int i = 0; i <poligono.size() ; i++) {
            if (i >0){
                format = format +"," + poligono.get(i).longitude+ " "+poligono.get(i).latitude;
            }
            else{
                format = poligono.get(i).longitude+ " "+poligono.get(i).latitude;
            }
        }
        return format;

    }

    /*METODO DE CREACION DE DIALOGO DE MANZANA*/
    public  void formManzana(){
        ArrayList<String> zonas = new ArrayList<>();
        zonas.add("00100");
        zonas.add("00200");
        zonas.add("00300");
        zonas.add("00400");
        zonas.add("00500");
        ArrayList<String> depas = new ArrayList<>();
        depas.add("15");
        depas.add("01");
        depas.add("14");
        depas.add("13");
        ArrayList<String> departamentos = new ArrayList<>();
        departamentos.add("15");
        departamentos.add("01");
        departamentos.add("14Lambayeque");
        departamentos.add("13");
        departamentos.add("12");
        ArrayList<String> provincias = new ArrayList<>();
        provincias.add("01");
        provincias.add("02");
        provincias.add("03");
        provincias.add("04");
        ArrayList<String> distritos = new ArrayList<>();
        distritos.add("01");
        distritos.add("02");
        distritos.add("03");
        distritos.add("04");
        distritos.add("05");
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(),R.style.ThemeOverlay_MaterialComponents_Dialog);
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.layout_form_manzana, null);
        final EditText nombre =       (EditText) dialogView.findViewById(R.id.id_edtManzanaNombre);
        final Spinner zona =         (Spinner) dialogView.findViewById(R.id.id_edtManzanaZona);
        final Spinner  departamento = (Spinner) dialogView.findViewById(R.id.id_edtManzanaDepartamento);
        final Spinner  provincia =    (Spinner) dialogView.findViewById(R.id.id_edtManzanaProvincia);
        final Spinner  distrito =     (Spinner) dialogView.findViewById(R.id.id_edtManzanaDistrito);
        ArrayAdapter<String> adapterZona         = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,zonas);
        ArrayAdapter<String> adapterDepartamento = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,departamentos);
        ArrayAdapter<String> adapterProvincia    = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,provincias);
        ArrayAdapter<String> adapterDistrito     = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,distritos);
        adapterZona.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterDepartamento.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterProvincia.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterDistrito.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        zona.setAdapter(adapterZona);
        departamento.setAdapter(adapterDepartamento);
        provincia.setAdapter(adapterProvincia);
        distrito.setAdapter(adapterDistrito);
        alert.setTitle("Manzana");
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
                        if(!nombre.getText().toString().equals("")){
                            datosManzana.add(nombre.getText().toString());
                            datosManzana.add(zona.getSelectedItem().toString());
                            datosManzana.add(departamento.getSelectedItem().toString());
                            datosManzana.add(provincia.getSelectedItem().toString());
                            datosManzana.add(distrito.getSelectedItem().toString());
                            Toast.makeText(getContext(),"Valores"+datosManzana.get(0)+""+datosManzana.get(1),Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }else{
                            Toast.makeText(getActivity().getApplicationContext(), "DEBE LLENAR CAMPO NOMBRE", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        alertDialog.show();
    }








}
