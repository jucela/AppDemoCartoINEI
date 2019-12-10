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
import com.inei.appcartoinei.R;
import com.inei.appcartoinei.modelo.ConexionSpatiaLiteHelper;
import com.inei.appcartoinei.modelo.DAO.Data;
import com.inei.appcartoinei.modelo.DAO.DataBaseHelper;
import com.inei.appcartoinei.modelo.pojos.Manzana;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.spatialite.database.SQLiteDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MapViviendaFragment extends Fragment implements OnMapReadyCallback,GoogleMap.OnMapClickListener {

    GoogleMap mgoogleMap;
    MapView mapView;
    View view;
    InputStream stream;
    private LocationManager mLocationManager;
    private Location location;

    private Marker marker;
    private LatLng punto;
    private ArrayList<String> datosVivienda = new ArrayList<String>() ;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;

    private SQLiteDatabase db ;
    private DataBaseHelper op;
    private RequestQueue mQueue;
    Data    data;
    Context context;
    String  idCapa;
    Cursor res=null;


    private OnFragmentInteractionListener mListener;

    public MapViviendaFragment() {
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

        /*SUBIR DATOS SERVIDOR*/
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /*INSERTAR GEOMETRIA + 3 CAMPOS*/
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertarVivienda(punto);

            }
        });

        /*ABRIR DIALOGO E INSERTAR DATOS*/
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                formVivienda();
            }
        });
   }

    @Override
    public void onMapClick(LatLng latLng) {
        punto = latLng;

        if (marker != null) {
            marker.remove();
        }
        marker =  mgoogleMap.addMarker( new MarkerOptions()
                .position(punto));
    }


    /*METODO INSERTAR MANZANA A SQLITE INTERNO*/
    /*STRING*/
    public  void insertarVivienda(LatLng punto){
        Marker marcadorAdd = mgoogleMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)));
        if(datosVivienda.size()>0)
        { if(punto!=null) {
            String query = "INSERT INTO vivienda(id,iduser,idviv,idmanzana,nommanzana,idzona,zona,ubigeo,nrofrente,nropuerta,descripcion,shape) VALUES (1,2,'A001','M001','"+datosVivienda.get(0)+"','002','"+datosVivienda.get(2)+"','"+datosVivienda.get(5)+datosVivienda.get(6)+datosVivienda.get(7)+"',"+datosVivienda.get(3)+","+datosVivienda.get(4)+",'"+datosVivienda.get(1)+"',GeomFromText('POLYGON(("+formatGeom(punto)+"))',4326));" ;
            db.execSQL(query);
            Toast.makeText(getContext(),"Se Registro Informaciónde Vivienda",Toast.LENGTH_SHORT).show();
            marcadorAdd.setPosition(punto);
            datosVivienda.clear();
            Log.d("query",query);
        }
        else
        {Toast.makeText(getContext(),"Ingrese Marcador en Vivienda!",Toast.LENGTH_SHORT).show();}
        }
        else{Toast.makeText(getContext(),"Ingrese valores en el formulario!",Toast.LENGTH_SHORT).show();}

    }

    /*METODO DE FORMATO A POLYGONO*/
    public String formatGeom(LatLng punto){
        String format ="";

            if (punto!=null){
                format = format +punto.longitude+ " "+punto.latitude;
            }

        return format;

    }

    /*METODO DE CREACION DE DIALOGO DE VIVIENDA*/
    public  void formVivienda(){
        ArrayList<String> manzanas = new ArrayList<>();
        manzanas.add("M1");
        manzanas.add("M2");
        manzanas.add("M3");
        manzanas.add("M4");
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
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.layout_form_vivienda, null);
        final Spinner  manzana      = (Spinner) dialogView.findViewById(R.id.id_edtViviendaManzana);
        final EditText descripcion  = (EditText) dialogView.findViewById(R.id.id_edtviviendaDescripcion);
        final Spinner  zona         = (Spinner) dialogView.findViewById(R.id.id_edtViviendaZona);
        final EditText nfrente      = (EditText) dialogView.findViewById(R.id.id_edtViviendaFrente);
        final EditText npuerta      = (EditText) dialogView.findViewById(R.id.id_edtViviendaPuerta);
        final Spinner  departamento = (Spinner) dialogView.findViewById(R.id.id_edtViviendaDepartamento);
        final Spinner  provincia    = (Spinner) dialogView.findViewById(R.id.id_edtViviendaProvincia);
        final Spinner  distrito     = (Spinner) dialogView.findViewById(R.id.id_edtViviendaDistrito);
        ArrayAdapter<String> adapterManzana         = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,manzanas);
        ArrayAdapter<String> adapterZona         = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,zonas);
        ArrayAdapter<String> adapterDepartamento = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,departamentos);
        ArrayAdapter<String> adapterProvincia    = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,provincias);
        ArrayAdapter<String> adapterDistrito     = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,distritos);
        adapterManzana.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterZona.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterDepartamento.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterProvincia.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterDistrito.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        manzana.setAdapter(adapterManzana);
        zona.setAdapter(adapterZona);
        departamento.setAdapter(adapterDepartamento);
        provincia.setAdapter(adapterProvincia);
        distrito.setAdapter(adapterDistrito);
        alert.setTitle("Vivienda");
        alert.setIcon(R.drawable.ic_home_24);
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
                        if(!descripcion.getText().toString().equals("")){
                            datosVivienda.add(manzana.getSelectedItem().toString());
                            datosVivienda.add(descripcion.getText().toString());
                            datosVivienda.add(zona.getSelectedItem().toString());
                            datosVivienda.add(nfrente.getText().toString());
                            datosVivienda.add(npuerta.getText().toString());
                            datosVivienda.add(departamento.getSelectedItem().toString());
                            datosVivienda.add(provincia.getSelectedItem().toString());
                            datosVivienda.add(distrito.getSelectedItem().toString());
                            alertDialog.dismiss();
                        }else{
                            Toast.makeText(getActivity().getApplicationContext(), "DEBE LLENAR DESCRIPCION", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        alertDialog.show();
    }








}
