package com.inei.appcartoinei.fragments;

import android.Manifest;
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
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.inei.appcartoinei.R;
import com.inei.appcartoinei.modelo.DAO.Data;
import com.inei.appcartoinei.modelo.DAO.DataBaseHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.spatialite.database.SQLiteDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class MapAsignarViviendaFragment extends Fragment implements OnMapReadyCallback,GoogleMap.OnMapClickListener {

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

    private OnFragmentInteractionListener mListener;

    public MapAsignarViviendaFragment() {
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
                mostrarConsulta();
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

        /*MOSTRAR MANZANAS*/
        if(obtenerListaShapeManzana().isEmpty())
        {Toast.makeText(getContext(),"No se encontraron Manzanas",Toast.LENGTH_SHORT).show();}
        else{
            for(int i=0;i<obtenerListaShapeManzana().size();i++)
            {   ArrayList<LatLng> listados    = new ArrayList<LatLng>();
                ArrayList<LatLng> newlistados = new ArrayList<LatLng>();
                listados = obtenerLatLngShapeManzana(obtenerListaShapeManzana().get(i));
                if(listados.size()>0)
                {   for(int j=0;j<listados.size();j++)
                    {
                        newlistados.add(listados.get(j));
                    }
                    newlistados.add(listados.get(0));
                    Polyline polyline = googleMap.addPolyline(new PolylineOptions()
                            .addAll(newlistados)
                            .color(Color.BLUE)
                            .width(3)
                            .jointType(JointType.ROUND)
                            .visible(true));
                }
            }
        }

        /*MOSTRAR PUNTOS*/
        if(obtenerListaShape().isEmpty())
        {Toast.makeText(getContext(),"No se encontraron Puntos",Toast.LENGTH_SHORT).show();}
        else{
            for(int i=0;i<obtenerListaShape().size();i++)
            {    LatLng punto = null;
                punto = obtenerLatLngShape(obtenerListaShape().get(i));
                if(punto!=null)
                {
                    googleMap.addMarker(new MarkerOptions()
                            .position(punto)
                            .title("punto")
                            .snippet("aaa")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                }
            }
        }
   }

    @Override
    public void onMapClick(LatLng latLng) {
        punto = latLng;
        if (marker != null) {
            marker.remove();
        }
        marker =  mgoogleMap.addMarker( new MarkerOptions().position(punto));
    }

    /*METODO INSERTAR VIVIENDA A SQLITE INTERNO*/
    public  void insertarVivienda(LatLng punto){
        Marker marcadorAdd = mgoogleMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        if(datosVivienda.size()>0)
        { if(punto!=null) {
            try {
                data = new Data(context);
                data.open();
                data.insertVivienda(2,2,"A001","M001",datosVivienda.get(0),"002",datosVivienda.get(2),datosVivienda.get(5)+datosVivienda.get(6)+datosVivienda.get(7),Integer.valueOf(datosVivienda.get(3)),Integer.valueOf(datosVivienda.get(4)),datosVivienda.get(1),"GeomFromText('POINT("+formatGeom(punto)+")')");
                data.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(getContext(),"Se registro Vivienda correctamente!",Toast.LENGTH_SHORT).show();
            marker.remove();
            marcadorAdd.setPosition(punto);
            datosVivienda.clear();;
        }
        else
        {Toast.makeText(getContext(),"Agrege ubicación de la Vivienda",Toast.LENGTH_SHORT).show();}
        }
        else{Toast.makeText(getContext(),"Ingrese valores en el Formulario",Toast.LENGTH_SHORT).show();}
    }

    /*METODO OBTENER LISTA(STRING) DE SHAPE VIVIENDA*/
    public ArrayList<String> obtenerListaShape(){
        ArrayList<String > listashape = new ArrayList<>();
        try {
            Data data = new Data(context);
            data.open();
            ArrayList<String> query = data.getAllShapeVivienda();
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

    /*METODO OBTENER LISTA(STRING) DE SHAPE MANZANA*/
    public ArrayList<String> obtenerListaShapeManzana(){
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

    /*METODO CONVERTIR LISTA(STRING) A LISTA MANZANA(LATLNG)*/
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


    /*METODO CONVERTIR LISTA(STRING) A PUNTO (LATLNG)*/
    public LatLng obtenerLatLngShape(String shape){
        LatLng punto = null;
        String campoGeom = shape;
        try {
            JSONObject jsonObject = new JSONObject(campoGeom);
            String dato = jsonObject.getString("coordinates");
                String cadena4= dato.substring(1,dato.length()-1);
                String[] latlog = cadena4.split(",");
                for(int x=0;x<1;x++){
                    punto = new LatLng(Double.parseDouble(latlog[0]),Double.parseDouble(latlog[1]));
                }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return punto;
    }

    /*METODO DE FORMATO A PUNTO*/
    public String formatGeom(LatLng punto){
        String format ="";
            if (punto!=null){
                format = punto.latitude+ " "+punto.longitude;
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

    /*PRUEBAS*/
    public void mostrarPunto(LatLng punto){
        Toast.makeText(getContext(),"PUNTO:"+punto,Toast.LENGTH_SHORT).show();
    }

    public  void mostrarConsulta(){
        try {
            Data data = new Data(context);
            data.open();
            // query  = data.getArea();
            ArrayList<String> query = data.getAllShapeVivienda();
            for(int i=0;i<query.size();i++){
                Log.i("cadena_shape","["+i+"]="+query.get(i));
            }
            //Toast.makeText(getContext(),"Valor:"+query.get(i),Toast.LENGTH_SHORT).show();
            //Log.i("cadena->",""+query);
            data.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }

}
