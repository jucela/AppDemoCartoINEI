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

import com.android.volley.RequestQueue;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.inei.appcartoinei.R;
import com.inei.appcartoinei.modelo.DAO.Data;
import com.inei.appcartoinei.modelo.DAO.DataBaseHelper;

import org.spatialite.database.SQLiteDatabase;

import java.io.InputStream;
import java.util.ArrayList;


public class MapEjeVialFragment extends Fragment implements OnMapReadyCallback,GoogleMap.OnMapClickListener {

    GoogleMap mgoogleMap;
    MapView mapView;
    View view;
    InputStream stream;
    private LocationManager mLocationManager;
    private Location location;

    private Marker marker;
    private LatLng punto;
    private Polyline polyline;
    private ArrayList<LatLng> listPoints    = new ArrayList<LatLng>() ;
    private ArrayList<String> datosEjeVial = new ArrayList<String>() ;
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

    public MapEjeVialFragment() {
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
                insertarEjeVial(listPoints);

            }
        });

        /*ABRIR DIALOGO E INSERTAR DATOS*/
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                formEjeVial();
            }
        });
   }

    @Override
    public void onMapClick(LatLng latLng) {
        listPoints.add(latLng);
        polyline = mgoogleMap.addPolyline(new PolylineOptions().addAll(listPoints));

        if(listPoints.size() > 1){
            polyline.setWidth(4);
            polyline.setClickable(true);
            polyline.setColor(Color.RED);
        }

        marker =  mgoogleMap.addMarker( new MarkerOptions()
                .position(latLng)
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_add_location)));
    }


    /*METODO INSERTAR MANZANA A SQLITE INTERNO*/
    /*STRING*/
    public  void insertarEjeVial(ArrayList<LatLng> polyline){
        Polyline polylineAdd = mgoogleMap.addPolyline(new PolylineOptions().add(new LatLng(0, 0), new LatLng(0, 0), new LatLng(0, 0)));
        if(datosEjeVial.size()>0)
        { if(listPoints.size()>0) {
            String query = "INSERT INTO eje_vial(id,iduser,idcategoria,nombrevia,nombrealt,ubigeo,shape) VALUES (1,2,"+datosEjeVial.get(0)+",'"+datosEjeVial.get(1)+"','"+datosEjeVial.get(2)+"','"+datosEjeVial.get(3)+datosEjeVial.get(4)+datosEjeVial.get(5)+"',GeomFromText('POLYGON(("+formatGeom(polyline)+"))',4326));" ;
            db.execSQL(query);
            Toast.makeText(getContext(),"Se Registro Informaciónde Vivienda",Toast.LENGTH_SHORT).show();
            polylineAdd.setPoints(polyline);
            listPoints.clear();
            datosEjeVial.clear();
            Log.d("query",query);
        }
        else
        {Toast.makeText(getContext(),"Ingrese Linea en Eje Vial!",Toast.LENGTH_SHORT).show();}
        }
        else{Toast.makeText(getContext(),"Ingrese valores en el formulario!",Toast.LENGTH_SHORT).show();}

    }

    /*METODO DE FORMATO A POLYGONO*/
    public String formatGeom(ArrayList<LatLng> polyline){
        String format ="";
        for (int i = 0; i <polyline.size() ; i++) {
            if (i >0){
                format = format +"," + polyline.get(i).longitude+ " "+polyline.get(i).latitude;
            }
            else{
                format = polyline.get(i).longitude+ " "+polyline.get(i).latitude;
            }
        }
        return format;

    }

    /*METODO DE CREACION DE DIALOGO DE VIVIENDA*/
    public  void formEjeVial(){
        ArrayList<String> categorias = new ArrayList<>();
        categorias.add("1");
        categorias.add("2");
        categorias.add("3");
        categorias.add("4");
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
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.layout_form_ejevial, null);
        final Spinner  categoria    = (Spinner) dialogView.findViewById(R.id.id_edtEjeCategoria);
        final EditText nombre      = (EditText) dialogView.findViewById(R.id.id_edtEjeNombre);
        final EditText nombrealt      = (EditText) dialogView.findViewById(R.id.id_edtEjeNombreAlt);
        final Spinner  departamento = (Spinner) dialogView.findViewById(R.id.id_edtEjeDepartamento);
        final Spinner  provincia    = (Spinner) dialogView.findViewById(R.id.id_edtEjeProvincia);
        final Spinner  distrito     = (Spinner) dialogView.findViewById(R.id.id_edtEjeDistrito);
        ArrayAdapter<String> adapterCategoria    = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,categorias);
        ArrayAdapter<String> adapterDepartamento = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,departamentos);
        ArrayAdapter<String> adapterProvincia    = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,provincias);
        ArrayAdapter<String> adapterDistrito     = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,distritos);
        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterDepartamento.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterProvincia.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterDistrito.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categoria.setAdapter(adapterCategoria);
        departamento.setAdapter(adapterDepartamento);
        provincia.setAdapter(adapterProvincia);
        distrito.setAdapter(adapterDistrito);
        alert.setTitle("Eje Vial");
        alert.setIcon(R.drawable.ic_transfer_within_a_station_24);
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
                            datosEjeVial.add(categoria.getSelectedItem().toString());
                            datosEjeVial.add(nombre.getText().toString());
                            datosEjeVial.add(nombrealt.getText().toString());
                            datosEjeVial.add(departamento.getSelectedItem().toString());
                            datosEjeVial.add(provincia.getSelectedItem().toString());
                            datosEjeVial.add(distrito.getSelectedItem().toString());
                            alertDialog.dismiss();
                        }else{
                            Toast.makeText(getActivity().getApplicationContext(), "DEBE LLENAR NOMBRE", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        alertDialog.show();
    }








}
