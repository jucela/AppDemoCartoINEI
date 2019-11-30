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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.inei.appcartoinei.R;
import com.inei.appcartoinei.modelo.ConexionSpatiaLiteHelper;
import com.inei.appcartoinei.modelo.DAO.Data;
import com.inei.appcartoinei.modelo.DAO.DataBaseHelper;
import com.inei.appcartoinei.modelo.pojos.Capa;

import org.json.JSONArray;
import org.json.JSONObject;
import org.spatialite.database.SQLiteDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MapCapas extends Fragment implements OnMapReadyCallback,GoogleMap.OnMapClickListener {

    GoogleMap mgoogleMap;
    MapView mapView;
    View view;
    private LocationManager mLocationManager;
    private Location location;
    private static final int LOCATION_REQUEST_CODE  = 1 ;
    public static final int LOCATION_UPDATE_MIN_DISTANCE = 10;
    public static final int LOCATION_UPDATE_MIN_TIME = 5000;
    private final static int COLOR_FILL_POLYGON = 0x7F00FF00;
    private final static int COLOR_FILL_POLYGON_GREEN = 0x5500ff00;

    private Polygon poligon;
    private ArrayList<LatLng> listPoints = new ArrayList<LatLng>() ;
    private ArrayList<String> valores = new ArrayList<String>() ;
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

    public MapCapas() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        consulta();
//        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
//            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,LOCATION_UPDATE_MIN_TIME,LOCATION_UPDATE_MIN_DISTANCE, (LocationListener) this);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_capa3, container, false);
        idCapa = getArguments().getString("idCapa","aaa");
        Log.i("idCapa",idCapa);
        return view;
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        mapView = (MapView) view.findViewById(R.id.map);
        fab1 =  (FloatingActionButton) view.findViewById(R.id.fab1);
        fab2 =  (FloatingActionButton) view.findViewById(R.id.fab2);
        fab3 =  (FloatingActionButton) view.findViewById(R.id.fab3);

        //conn = new ConexionSpatiaLiteHelper(getContext());
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
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
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
                exportarDatos();
            }
        });

        /*INSERTAR GEOMETRIA + 3 CAMPOS*/
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //agregarPoligono(listPoints);
                insertarPoligono(listPoints);

            }
        });

        /*ABRIR DIALOGO E INSERTAR DATOS*/
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(),R.style.ThemeOverlay_MaterialComponents_Dialog);
                final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.layout_formdialog, null);
                final EditText edtUbigeo = (EditText) dialogView.findViewById(R.id.id_edtUbigeo);
                final EditText edtZona = (EditText) dialogView.findViewById(R.id.id_edtZona);
                final EditText edtManzana = (EditText) dialogView.findViewById(R.id.id_edtmanzana);

                edtUbigeo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                edtZona.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                edtManzana.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                alert.setTitle("Ingresar Datos");
                alert.setIcon(R.drawable.ic_action_pin);
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
                                // TODO Do something
                                if(!edtUbigeo.getText().toString().equals("") && !edtZona.getText().toString().equals("")&&!edtManzana.getText().toString().equals("")){
                                    valores.add(edtUbigeo.getText().toString());
                                    valores.add(edtZona.getText().toString());
                                    valores.add(edtManzana.getText().toString());
                                    alertDialog.dismiss();
                                }else{
                                    Toast.makeText(getActivity().getApplicationContext(), "DEBE LLENAR TODOS LOS CAMPOS", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                alertDialog.show();
            }
        });


        PolygonOptions rectOptions = new PolygonOptions()
                .add(
                new LatLng(-12.067416,-77.0492049),
                new LatLng(-12.068313,-77.0499774),
                new LatLng(-12.0690894,-77.049044),
                new LatLng(-12.0681504,-77.0482662),
                new LatLng(-12.0677622,-77.0487785),
                new LatLng(-12.067416,-77.0492049)
        );

        Polygon polygono = googleMap.addPolygon(new PolygonOptions()
                .add(   new LatLng(-12.067416,-77.0492049),
                        new LatLng(-12.068313,-77.0499774),
                        new LatLng(-12.0690894,-77.049044),
                        new LatLng(-12.0681504,-77.0482662),
                        new LatLng(-12.0677622,-77.0487785),
                        new LatLng(-12.067416,-77.0492049))
                .strokeColor(Color.BLUE)
                .strokeWidth(2)
                .strokeJointType(JointType.ROUND)
                .visible(true));

        polygono.setClickable(true);
        polygono.setStrokeJointType(JointType.ROUND);
        googleMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                Toast.makeText(getContext(),"Hola",Toast.LENGTH_SHORT).show();
            }
        });




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


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == LOCATION_REQUEST_CODE) {
//            // ¿Permisos asignados?
//            if (permissions.length > 0 &&
//                    permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
//                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    mgoogleMap.setMyLocationEnabled(true);
//            } else {
//
//                Toast.makeText(getContext(), "Error de permisos", Toast.LENGTH_LONG).show();
//            }
//
//        }
//    }

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

    public  void agregarPoligono(ArrayList<LatLng> poligono){
        Polygon poligonAdd = mgoogleMap.addPolygon(new PolygonOptions().add(new LatLng(0, 0), new LatLng(0, 0), new LatLng(0, 0)).fillColor(COLOR_FILL_POLYGON_GREEN).strokeWidth(8));
        if(valores.size()>0)
        {  if(listPoints.size()>0) {
            poligonAdd.setPoints(poligono);
            String query = "INSERT INTO poligonos(geometry_column,export,ubigeo,zona,manzana) VALUES ( GeomFromText('POLYGON(("+formatGeom(poligono)+"))',4326),0,'"+valores.get(0)+"','"+valores.get(1)+"','"+valores.get(2)+"');" ;
            db.execSQL(query);
            Toast.makeText(getContext(),"Se Registro Información",Toast.LENGTH_SHORT).show();
            listPoints.clear();
            valores.clear();
            Log.d("query",query);
        }
        else
        {Toast.makeText(getContext(),"Ingrese Poligono!",Toast.LENGTH_SHORT).show();}
        }
        else{Toast.makeText(getContext(),"Ingrese valores (Ubigeo,Manzana,Zona)!",Toast.LENGTH_SHORT).show();}

    }

    public void insertarServicio( final JSONArray arrayGeom)
    {
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

    public void insertarDatosCapa(){
        try {
            data = new Data(context);
            data.open();
            data.insertarCapa(new Capa(1,"juxe","Lavado","poligono",1,2,3,4,5));
            data.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public  void insertarPoligono(ArrayList<LatLng> poligono){
        Polygon poligonAdd = mgoogleMap.addPolygon(new PolygonOptions().add(new LatLng(0, 0), new LatLng(0, 0), new LatLng(0, 0)).fillColor(COLOR_FILL_POLYGON_GREEN).strokeWidth(8));
        if(valores.size()>0)
        { if(listPoints.size()>0) {
                poligonAdd.setPoints(poligono);
                    String query = "INSERT INTO poligonos(id,geometry_column,export,ubigeo,zona,manzana) VALUES ( '"+Integer.parseInt(idCapa)+"',GeomFromText('POLYGON(("+formatGeom(poligono)+"))',4326),0,'"+valores.get(0)+"','"+valores.get(1)+"','"+valores.get(2)+"');" ;
                    db.execSQL(query);
                    Toast.makeText(getContext(),"Se Registro Información",Toast.LENGTH_SHORT).show();
                    listPoints.clear();
                    valores.clear();
                    Log.d("query",query);

        }
        else
        {Toast.makeText(getContext(),"Ingrese Poligono!",Toast.LENGTH_SHORT).show();}
        }
        else{Toast.makeText(getContext(),"Ingrese valores (Ubigeo,Manzana,Zona)!",Toast.LENGTH_SHORT).show();}

    }

    public void exportarDatos(){

        String queryJson = "SELECT AsGeoJSON(geometry_column) geom,ubigeo,zona,manzana from poligonos where export=0;" ;
        Cursor res = db.rawQuery( queryJson, null );
        int contador = res.getCount();
        if(contador>0)
        {
            Log.i("datos_contador1",""+contador);
            res.moveToFirst();

            while(res.isAfterLast() == false) {

                Log.i("datos_contador2",""+contador);

                String campoGeom=res.getString(res.getColumnIndex("geom"));
                String campoUbigeo=res.getString(res.getColumnIndex("ubigeo"));
                String campoZona=res.getString(res.getColumnIndex("zona"));
                String campoManzana=res.getString(res.getColumnIndex("manzana"));


                String stringJsonFinal = "";

                try {
                    Log.i("datos_contador3",""+contador);
                    JSONObject geom = new JSONObject(campoGeom);
                    String rings=geom.get("coordinates").toString();
                    stringJsonFinal = "{\"geometry\":{\"rings\":"+ rings+", \"spatialReference\" : {\"wkid\" : 4326}},\"attributes\":{\"UBIGEO\":"+campoUbigeo+",\"ZONA\":"+campoZona+",\"MANZANA\":"+campoManzana+"}}";
                    JSONArray arrayGeom = new JSONArray();
                    Log.i("datos_contador3",""+contador);
                    //arrayGeom.put();
                    JSONObject obj = new JSONObject(stringJsonFinal);
                    arrayGeom.put(obj);
                    Log.d("My App", arrayGeom.toString());
                    insertarServicio(arrayGeom);
                } catch (Throwable tx) {
                    Log.e("My App", "Could not parse malformed JSON: \"" + stringJsonFinal + "\"");
                }
                res.moveToNext();

            }
        }
        else
        {
            Toast.makeText(getContext(),"No hay Registros para subir",Toast.LENGTH_SHORT).show();
        }


    }

    public void consulta(){
        String queryJson = "SELECT id,AsGeoJSON(geometry_column) geom,ubigeo,zona,manzana from poligonos where export=0;" ;
        try {
            Cursor res=db.rawQuery(queryJson, null);
            int contador = res.getCount();
            if (contador > 0) {
                Log.i("contador", "" + contador);
                res.moveToFirst();

                while (res.isAfterLast() == false) {

                    Log.i("contador1", "" + contador);

                    String campoGeom = res.getString(res.getColumnIndex("geom"));
                    String campoUbigeo = res.getString(res.getColumnIndex("ubigeo"));
                    String campoZona = res.getString(res.getColumnIndex("zona"));
                    String campoManzana = res.getString(res.getColumnIndex("manzana"));
                    String stringJsonFinal = "";

                    try {
                        Log.i("contador2", "" + contador);
                        JSONObject geom = new JSONObject(campoGeom);
                        String rings = geom.get("coordinates").toString();
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
                Toast.makeText(getContext(), "No hay Registros para subir", Toast.LENGTH_SHORT).show();
            }
        }catch (NullPointerException e){
            e.getMessage();
            Toast.makeText(getContext(), "Error de lista"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }



    }




}
