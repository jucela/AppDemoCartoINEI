package com.inei.appcartoinei.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
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

import androidx.annotation.NonNull;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.inei.appcartoinei.R;
import com.inei.appcartoinei.modelo.ConexionSpatiaLiteHelper;

import org.json.JSONArray;
import org.json.JSONObject;
import org.spatialite.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Capa3 extends Fragment implements OnMapReadyCallback,GoogleMap.OnMapClickListener {

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
    private ConexionSpatiaLiteHelper conn;
    private RequestQueue mQueue;


    private OnFragmentInteractionListener mListener;

    public Capa3() {
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
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_capa3, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        mapView = (MapView) view.findViewById(R.id.map);
        fab1 =  (FloatingActionButton) view.findViewById(R.id.fab1);
        fab2 =  (FloatingActionButton) view.findViewById(R.id.fab2);
        fab3 =  (FloatingActionButton) view.findViewById(R.id.fab3);

        conn = new ConexionSpatiaLiteHelper(getContext());
        db = conn.getWritableDatabase();
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
                //agregarPoligono(newListPoints);
                agregarPoligono(listPoints);

            }
        });

        /*ABRIR DIALOGO E INSERTAR DATOS*/
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.layout_formdialog, null);
                final LinearLayout lytDialog = (LinearLayout) dialogView.findViewById(R.id.dialog_lyt);
                final EditText edtUbigeo = (EditText) dialogView.findViewById(R.id.id_edtUbigeo);
                final EditText edtZona = (EditText) dialogView.findViewById(R.id.id_edtZona);
                final EditText edtManzana = (EditText) dialogView.findViewById(R.id.id_edtmanzana);


                edtUbigeo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                edtZona.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                edtManzana.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                alert.setTitle("Datos");
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
                                if(!edtUbigeo.getText().toString().equals("") && !edtZona.getText().toString().equals("")&&!edtManzana.getText().toString().equals("0")){
                                    //dato = edtUbigeo.getText().toString();
                                    //txt.setText(dato);
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



    }

    @Override
    public void onMapClick(LatLng latLng) {
        listPoints.add(latLng);
        poligon = mgoogleMap.addPolygon(new PolygonOptions().addAll(listPoints));
        if(listPoints.size() > 1){

            poligon.setPoints(listPoints);
            poligon.setFillColor(0xffff0000);
            poligon.setStrokeWidth(5);
        }
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

    public void exportarDatos(){

        String queryJson = "SELECT AsGeoJSON(geometry_column) geom,ubigeo,zona,manzana from poligonos where export=0;" ;
        Cursor res = db.rawQuery( queryJson, null );
        int contador = res.getCount();
        if(contador>0)
        {
            Log.i("contador",""+contador);
            res.moveToFirst();

            while(res.isAfterLast() == false) {

                Log.i("contador1",""+contador);

                String campoGeom=res.getString(res.getColumnIndex("geom"));
                String campoUbigeo=res.getString(res.getColumnIndex("ubigeo"));
                String campoZona=res.getString(res.getColumnIndex("zona"));
                String campoManzana=res.getString(res.getColumnIndex("manzana"));


                String stringJsonFinal = "";

                try {
                    Log.i("contador2",""+contador);
                    JSONObject geom = new JSONObject(campoGeom);
                    String rings=geom.get("coordinates").toString();
                    stringJsonFinal = "{\"geometry\":{\"rings\":"+ rings+", \"spatialReference\" : {\"wkid\" : 4326}},\"attributes\":{\"UBIGEO\":"+campoUbigeo+",\"ZONA\":"+campoZona+",\"MANZANA\":"+campoManzana+"}}";
                    JSONArray arrayGeom = new JSONArray();
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

            /*@Override
            public byte[] getBody() throws AuthFailureError{
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }*/

            /*@Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                    // can get more details such as response.headers
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }*/




        };






/*
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, url, arrayGeom,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("SUCCESS", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("ERROR", error.toString());
            }
        }
        );*/

       /* JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("employees");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject employee = jsonArray.getJSONObject(i);

                                String firstName = employee.getString("firstname");
                                int age = employee.getInt("age");
                                String mail = employee.getString("mail");

                                mTextViewResult.append(firstName + ", " + String.valueOf(age) + ", " + mail + "\n\n");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });*/

        mQueue.add(request);

    }




}
