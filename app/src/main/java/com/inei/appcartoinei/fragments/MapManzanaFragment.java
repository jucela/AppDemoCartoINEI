package com.inei.appcartoinei.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import com.inei.appcartoinei.modelo.DAO.Data;
import com.inei.appcartoinei.modelo.DAO.DataBaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.spatialite.database.SQLiteDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MapManzanaFragment extends Fragment implements OnMapReadyCallback,GoogleMap.OnMapClickListener {

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
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;
    private FloatingActionButton fab4;
    private SQLiteDatabase db ;
    private DataBaseHelper op;
    private RequestQueue mQueue;
    Data    data;
    Context context;
    String  idCapa;

    private OnFragmentInteractionListener mListener;

    public MapManzanaFragment() {
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
        fab1 =  (FloatingActionButton) view.findViewById(R.id.fab1);
        fab2 =  (FloatingActionButton) view.findViewById(R.id.fab2);
        fab3 =  (FloatingActionButton) view.findViewById(R.id.fab3);
        fab4 =  (FloatingActionButton) view.findViewById(R.id.fab4);
        fab4.setVisibility(View.VISIBLE);

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

        /*SUBIR DATOS A SERVIDOR*/
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  exportarManzana();
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

        /*MOSTRAR MANZANAS*/
        if(obtenerListaShape().isEmpty())
        {Toast.makeText(getContext(),"No se encontraron Manzanas",Toast.LENGTH_SHORT).show();}
        else{
            for(int i=0;i<obtenerListaShape().size();i++)
            {    ArrayList<LatLng> listados = new ArrayList<LatLng>();
                 listados = obtenerLatLngShape(obtenerListaShape().get(i));
                if(listados.size()>0)
                {
                    Polygon polygono = googleMap.addPolygon(new PolygonOptions()
                            .addAll(listados)
                            .strokeColor(Color.BLUE)
                            .strokeWidth(3)
                            .strokeJointType(JointType.ROUND)
                            .visible(true));
                    polygono.setClickable(true);
                    polygono.setStrokeJointType(JointType.ROUND);
                }
            }
        }
        googleMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                Toast.makeText(getContext(),"x",Toast.LENGTH_SHORT).show();
            }
        });

        /*CREAR POLIGONO*/
        poligon = googleMap.addPolygon(new PolygonOptions()
                .add(new LatLng(0, 0), new LatLng(0, 0), new LatLng(0, 0))
                .fillColor(Color.GREEN)
                .strokeWidth(5));
    }

    @Override
    public void onMapClick(LatLng latLng) {
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

    /*METODO INSERTAR MANZANA A SQLITE INTERNO*/
    public  void insertarManzana(ArrayList<LatLng> poligono){
        Polygon poligonAdd = mgoogleMap.addPolygon(new PolygonOptions().add(new LatLng(0, 0), new LatLng(0, 0), new LatLng(0, 0)).strokeColor(Color.BLUE).strokeWidth(3).strokeJointType(JointType.ROUND).visible(true));
        if(datosManzana.size()>0)
        { if(listPoints.size()>2) {
            try {
            data = new Data(context);
            data.open();
            data.insertManzana(1,2,"001",datosManzana.get(0),"002",datosManzana.get(1),datosManzana.get(2)+datosManzana.get(3)+datosManzana.get(4),"GeomFromText('POLYGON(("+formatGeom(poligono)+"))',4326)");
            data.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(getContext(),"Se registro Manzana correctamente!",Toast.LENGTH_SHORT).show();
            poligon.remove();
            poligon = mgoogleMap.addPolygon(new PolygonOptions()
                    .add(new LatLng(0, 0), new LatLng(0, 0), new LatLng(0, 0))
                    .fillColor(Color.GREEN)
                    .strokeWidth(5));
            poligonAdd.setPoints(poligono);
            for(int i=0;i<listaMarker.size();i++)
            {
                listaMarker.get(i).remove();
            }
            listaMarker.clear();
            listPoints.clear();
            datosManzana.clear();
        }
        else
        {Toast.makeText(getContext(),"Dibuje una Manzana",Toast.LENGTH_SHORT).show();}
        }
        else{Toast.makeText(getContext(),"Ingrese valores en el Formulario",Toast.LENGTH_SHORT).show();}
    }

    /*METODO OBTENER LISTA(STRING) DE SHAPE MANZANA*/
    public ArrayList<String> obtenerListaShape(){
        ArrayList<String> listashape = new ArrayList<>();
            try {
                Data data = new Data(context);
                data.open();
                ArrayList<String> query = data.getAllShapeManzana();
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
    public ArrayList<LatLng> obtenerLatLngShape(String shape){
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

    /*METODO DE OBTENCION DE MANZANA DE SQLITE INTERNO*/
    public void exportarManzana(){
        String queryJson = "SELECT id,iduser,idmanzana,nommanzana,idzona,zona,ubigeo,AsGeoJSON(shape) geom from manzana where id=1;" ;
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
                        insertarServicio(arrayGeom);
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
                format = format +"," + poligono.get(i).latitude+ " "+poligono.get(i).longitude;
            }
            else{
                format = poligono.get(i).latitude+ " "+poligono.get(i).longitude;
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

     /*OTROS*/
     public ArrayList<LatLng> pintarManzana(){
         ArrayList<LatLng> listapintado = new ArrayList<LatLng>();
         String queryJson = "SELECT id,iduser,idmanzana,nommanzana,idzona,zona,ubigeo,AsGeoJSON(shape) geom from manzana where id=1;" ;
         try {
             Cursor res=db.rawQuery(queryJson, null);
             int contador = res.getCount();
             if (contador > 0) {
                 res.moveToFirst();
                 while (res.isAfterLast() == false) {
                     String campoGeom =    res.getString(res.getColumnIndex("geom"));
                     Log.i("cadena_Geomm", "" + campoGeom);
                     try {
                         JSONObject jsonObject = new JSONObject(campoGeom);
                         String dato = jsonObject.getString("coordinates");
                         Log.i("cadena_object",""+jsonObject);
                         Log.i("cadena_dato",""+dato);
                         String ncadena1= dato.substring(1,dato.length()-1);
                         Log.i("cadena_nueva1", "" + ncadena1);
                         String ncadena2= ncadena1.substring(1,ncadena1.length()-1);
                         Log.i("cadena_nueva2", "" + ncadena2);
                         String ncadena3 = ncadena2.replace("],[", "];[");
                         Log.i("cadena_nueva3", "" + ncadena3);

                         String[] parts = ncadena3.split(";");

                         for(int i =0;i<parts.length;i++)
                         {
                             String part1 = parts[i];
                             String cadena4= part1.substring(1,part1.length()-1);
                             Log.i("cadena_valor2", "" + cadena4);
                             String[] latlog = cadena4.split(",");
                             Log.i("cadena_latlog", "" + latlog);
                             for(int x=0;x<1;x++){
                                 listapintado.add(new LatLng(Double.parseDouble(latlog[0]),Double.parseDouble(latlog[1])));
                                 //Log.i("cadena_lista", "["+x+"]=" + listalatlog.get(x));
                             }
                         }
                         for (int i=0;i<listalatlog.size();i++){
                             Log.i("valor1", "" + listalatlog.get(i));
                         }
                         for (int i=0;i<listapintado.size();i++){
                             Log.i("valor2", "" + listapintado.get(i));
                         }
                     }
                     catch (JSONException e) {
                         e.printStackTrace();
                     }
                     res.moveToNext();
                 }
             } else {
                 Toast.makeText(getContext(), "No hay poligonos para pintar", Toast.LENGTH_SHORT).show();
             }
         }catch (NullPointerException e){
             e.getMessage();
             Toast.makeText(getContext(), "Error de lista"+e.getMessage(), Toast.LENGTH_SHORT).show();
         }
         return listapintado;
     }

     public  void mostrarConsulta(){
        try {
            Data data = new Data(context);
            data.open();
            // query  = data.getArea();
            ArrayList<String> query = data.getAllShapeManzana();
            for(int i=0;i<query.size();i++){
                Log.i("cadena_shape","["+i+"]="+query.get(i));
            }
            Toast.makeText(getContext(),"Area:"+query+" m",Toast.LENGTH_SHORT).show();
            data.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }





}
