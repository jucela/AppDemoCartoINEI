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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPoint;
import com.google.maps.android.data.geojson.GeoJsonPolygon;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;
import com.inei.appcartoinei.R;
import com.inei.appcartoinei.dialogs.DialogFusion;
import com.inei.appcartoinei.dialogs.DialogFusionManzana;
import com.inei.appcartoinei.modelo.DAO.Data;
import com.inei.appcartoinei.modelo.DAO.DataBaseHelper;
import com.inei.appcartoinei.modelo.pojos.FusionItem;
import com.inei.appcartoinei.modelo.pojos.ManzanaCaptura;
import com.inei.appcartoinei.modelo.pojos.ManzanaReplanteo;

import org.json.JSONException;
import org.json.JSONObject;
import org.spatialite.database.SQLiteDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class MapActualizarManzanaFragment extends Fragment implements OnMapReadyCallback,GoogleMap.OnMapClickListener, DialogFusionManzana.SendDialogListener {

    GoogleMap mgoogleMap;
    MapView mapView;
    View view;
    private LocationManager mLocationManager;
    private Location location;
    private Polygon poligon;
    private Marker vertice;
    private ArrayList<Marker> listaMarker = new ArrayList<Marker>();
    private ArrayList<LatLng> listPoints = new ArrayList<LatLng>();
    private ArrayList<FusionItem> manzanaSeleccionadaEnvio = new ArrayList<FusionItem>();

    private ArrayList<LatLng> listados = new ArrayList<LatLng>();

    private int idAccionManzana = 0;
    private String newIdManzana = "";
    private String selectIdManzana = "";
    private FloatingActionButton fab2;
    private FloatingActionButton fab4;
    private FloatingActionButton fab5;
    private SQLiteDatabase db;
    private DataBaseHelper op;
    Data data;
    Context context;

    GeoJsonLayer layer;
    GeoJsonPolygonStyle polygonStyle;

    private int accionManzana = 0;

    /*Replantear*/
    private int cantidad = 0;
    private int contadorManzana = 0;
    private String idManzanaSeleccionada = "";
    private ArrayList<ManzanaReplanteo> listaManzanasMemoria = new ArrayList<ManzanaReplanteo>();

    private OnFragmentInteractionListener mListener;

    public MapActualizarManzanaFragment() {
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) view.findViewById(R.id.map);
        fab2 = (FloatingActionButton) view.findViewById(R.id.fab2);
        fab4 = (FloatingActionButton) view.findViewById(R.id.fab4);
        fab5 = (FloatingActionButton) view.findViewById(R.id.fab5);

        op = new DataBaseHelper(getContext());
        db = op.getWritableDatabase();

        if (mapView != null) {
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
        mgoogleMap = googleMap;
        mgoogleMap.getUiSettings().setCompassEnabled(false);//Brujula
        mgoogleMap.getUiSettings().setZoomControlsEnabled(true);//Zoom
        mgoogleMap.getUiSettings().setMyLocationButtonEnabled(true);//GPS
        mgoogleMap.setOnMapClickListener(this);
        final LatLng peru = new LatLng(-9, -74);

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        CameraPosition Liberty = CameraPosition.builder().target(peru).zoom(16).bearing(0).tilt(45).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(Liberty));

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mgoogleMap.setMyLocationEnabled(true);
            location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location != null) {
                LatLng gps = new LatLng(location.getLatitude(), location.getLongitude());
                mgoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, 12));
            } else {
                mgoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(peru, 5));
            }
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }


        /*INSERTAR GEOMETRIA + PARAMETROS*/
        fab2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                if(accionManzana==3)
                {saveManzanaCapturaFusion();}
                if(accionManzana==4)
                {saveManzanaCapturaFraccionada();}
                if(accionManzana==5)
                {saveManzanaCapturaReplanteada();}
            }
        });

        /*DESHACER ULTIMO PUNTO DE POLIGONO*/
        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                undoPolygon();
            }
        });

        /*ANULAR ACCION*/
        fab5.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                cleanPolygon();
                removeLayer();
                createLayerGeojsonMain();
                loadFeatureAllManzanas();
                setEventoEstado();
            }
        });
        /*CREAR LAYER MAIN*/
        createLayerGeojsonMain();
        setEventoEstado();
        loadFeatureAllManzanas();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (manzanaSeleccionadaEnvio.size() <= 1) {
            Toast.makeText(getContext(), "Seleccione una Manzana para Actualizar", Toast.LENGTH_SHORT).show();
        } else {
            listPoints.add(latLng);
            Log.e("mensaje:", "Se agrego a punto->" + latLng);
            poligon.setPoints(listPoints);
            Log.e("mensaje:", "Se agrego poligono con puntos->" + listPoints.size());
            vertice = mgoogleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_edit_location)));
            Log.e("mensaje", "Se agrego vertice" + vertice.getPosition());
            listaMarker.add(vertice);
            Log.e("mensaje", "Se grego vertices a Arraylist" + listaMarker.size());
        }
    }

    /*******METODOS DE INTERACCION CON EL MAPA ***********/

    /*1. CREAR LAYER MARCO DE TRABAJO*/
    @SuppressLint("RestrictedApi")
    public void createLayerGeojsonMain() {
        try {
            layer = new GeoJsonLayer(mgoogleMap, R.raw.marco_inicio, getContext());
            layer.addLayerToMap();
            polygonStyle = layer.getDefaultPolygonStyle();
            polygonStyle.setStrokeColor(Color.RED);
            polygonStyle.setStrokeWidth(3);
            polygonStyle.setZIndex(1f);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*2. CARGAR FEATURE DE TODAS LAS MANZANAS */
    private void loadFeatureAllManzanas() {
        ArrayList<LatLng> listaVertices = new ArrayList<>();
        String codzona = "";
        String codmzna = "";
        String estado = "";
        if(getListaManzanaCaptura().isEmpty()) {
            Toast.makeText(getContext(), "No se encontraron Manzanas", Toast.LENGTH_SHORT).show();
        }
        else {
            for(int i=0;i<getListaManzanaCaptura().size();i++){

                codzona = getListaManzanaCaptura().get(i).getCodzona();
                codmzna = getListaManzanaCaptura().get(i).getCodmzna();
                estado = Integer.toString(getListaManzanaCaptura().get(i).getEstado());
                listaVertices = getLatLngShapeManzana(getListaManzanaCaptura().get(i).getShape());

                GeoJsonPolygon geoJsonPolygon = new GeoJsonPolygon(Collections.singletonList(listaVertices));
                HashMap<String, String> properties = new HashMap<String, String>();
                properties.put("CODZONA", codzona);
                properties.put("CODMZNA", codmzna);
                properties.put("SUFMZNA", "");
                properties.put("ESTADO", estado);
                GeoJsonFeature geoJsonFeature = new GeoJsonFeature(geoJsonPolygon, codmzna, properties, null);
                GeoJsonPolygonStyle geoJsonPolygonStyle = new GeoJsonPolygonStyle();
                geoJsonPolygonStyle.setStrokeWidth(3);
                geoJsonFeature.setPolygonStyle(geoJsonPolygonStyle);

                if(getListaManzanaCaptura().get(i).getEstado()==0)
                {
                    geoJsonPolygonStyle.setZIndex(0f);
                    geoJsonPolygonStyle.setStrokeColor(Color.RED);
                    geoJsonFeature.setPolygonStyle(geoJsonPolygonStyle);
                    layer.addFeature(geoJsonFeature);
                }
                if(getListaManzanaCaptura().get(i).getEstado()==2)
                {
                    geoJsonPolygonStyle.setZIndex(2f);
                    geoJsonPolygonStyle.setFillColor(Color.GREEN);
                    geoJsonPolygonStyle.setStrokeColor(Color.GRAY);
                    geoJsonFeature.setPolygonStyle(geoJsonPolygonStyle);
                    layer.addFeature(geoJsonFeature);
                }
                if(getListaManzanaCaptura().get(i).getEstado()==3)
                {
                    geoJsonPolygonStyle.setZIndex(2f);
                    geoJsonPolygonStyle.setFillColor(Color.CYAN);
                    geoJsonPolygonStyle.setStrokeColor(Color.GRAY);
                    geoJsonFeature.setPolygonStyle(geoJsonPolygonStyle);
                    layer.addFeature(geoJsonFeature);
                }
                if(getListaManzanaCaptura().get(i).getEstado()==4)
                {
                    geoJsonPolygonStyle.setZIndex(3f);
                    geoJsonPolygonStyle.setFillColor(Color.YELLOW);
                    geoJsonPolygonStyle.setStrokeColor(Color.GRAY);
                    geoJsonFeature.setPolygonStyle(geoJsonPolygonStyle);
                    layer.addFeature(geoJsonFeature);
                }
                if(getListaManzanaCaptura().get(i).getEstado()==5)
                {
                    geoJsonPolygonStyle.setZIndex(3f);
                    geoJsonPolygonStyle.setFillColor(Color.BLUE);
                    geoJsonPolygonStyle.setStrokeColor(Color.GRAY);
                    geoJsonFeature.setPolygonStyle(geoJsonPolygonStyle);
                    layer.addFeature(geoJsonFeature);
                }
                if(getListaManzanaCaptura().get(i).getEstado()==6)
                {
                    geoJsonPolygonStyle.setZIndex(3f);
                    geoJsonPolygonStyle.setFillColor(Color.RED);
                    geoJsonPolygonStyle.setStrokeColor(Color.GRAY);
                    geoJsonFeature.setPolygonStyle(geoJsonPolygonStyle);
                    layer.addFeature(geoJsonFeature);
                }
                if(getListaManzanaCaptura().get(i).getEstado()==7)
                {
                    geoJsonPolygonStyle.setZIndex(0f);
                    geoJsonPolygonStyle.setStrokeColor(Color.RED);
                    geoJsonFeature.setPolygonStyle(geoJsonPolygonStyle);
                    layer.addFeature(geoJsonFeature);
                }
                if(getListaManzanaCaptura().get(i).getEstado()==8)
                {
                    geoJsonPolygonStyle.setZIndex(0f);
                    geoJsonPolygonStyle.setStrokeColor(Color.RED);
                    geoJsonFeature.setPolygonStyle(geoJsonPolygonStyle);
                    layer.addFeature(geoJsonFeature);
                }
                if(getListaManzanaCaptura().get(i).getEstado()==9)
                {
                    geoJsonPolygonStyle.setZIndex(0f);
                    geoJsonPolygonStyle.setStrokeColor(Color.RED);
                    geoJsonFeature.setPolygonStyle(geoJsonPolygonStyle);
                    layer.addFeature(geoJsonFeature);
                }
            }
        }
    }

    /*3. CARGAR POLIGONOS DE TODAS LAS MANZANAS PARA EDICION*/
    private void loadPolygonAllManzanaEdicion() {
        ArrayList<LatLng> listaVertices = new ArrayList<>();
        if(getListaManzanaCaptura().isEmpty()) {
            Toast.makeText(getContext(), "No se encontraron ManzanasXX ", Toast.LENGTH_SHORT).show();
        }
        else {
            for(int i=0;i<getListaManzanaCaptura().size();i++){
                listaVertices = getLatLngShapeManzana(getListaManzanaCaptura().get(i).getShape());
                Polygon polygono = mgoogleMap.addPolygon(new PolygonOptions().addAll(listaVertices));
                polygono.setStrokeJointType(JointType.ROUND);
                polygono.setStrokeWidth(3f);
                polygono.setZIndex(0f);
                polygono.setClickable(false);

                if(getListaManzanaCaptura().get(i).getEstado()==0)
                {
                    polygono.setStrokeColor(Color.RED);
                }
                if(getListaManzanaCaptura().get(i).getEstado()==2)
                {
                    polygono.setFillColor(Color.GREEN);
                    polygono.setStrokeColor(Color.GRAY);
                }
                if(getListaManzanaCaptura().get(i).getEstado()==3)
                {
                    polygono.setFillColor(Color.CYAN);
                    polygono.setStrokeColor(Color.GRAY);
                }
                if(getListaManzanaCaptura().get(i).getEstado()==4)
                {
                    polygono.setFillColor(Color.YELLOW);
                    polygono.setStrokeColor(Color.GRAY);
                }
                if(getListaManzanaCaptura().get(i).getEstado()==5)
                {
                    polygono.setFillColor(Color.BLUE);
                    polygono.setStrokeColor(Color.GRAY);
                }
                if(getListaManzanaCaptura().get(i).getEstado()==6)
                {
                    polygono.setFillColor(Color.RED);
                    polygono.setStrokeColor(Color.GRAY);
                }
                if(getListaManzanaCaptura().get(i).getEstado()==7)
                {
                    polygono.setStrokeColor(Color.RED);
                }
                if(getListaManzanaCaptura().get(i).getEstado()==8)
                {
                    polygono.setStrokeColor(Color.RED);
                }
                if(getListaManzanaCaptura().get(i).getEstado()==9)
                {
                    polygono.setStrokeColor(Color.RED);
                }
            }
        }
    }

    /*4. CARGAR MANZANA MODIFICADA(CONFIRMADA,FUSION,FRACCIONAR,REPLANTEAR,ELIMINAR)*/
    public void loadOnlyManzanaModificada(String idmanzana, int estado) {
        if (getObjectManzanaCapturaXIDEstado(idmanzana, estado) != null) {
            String codzona = getObjectManzanaCapturaXIDEstado(idmanzana, estado).getCodzona();
            String codmzna = getObjectManzanaCapturaXIDEstado(idmanzana, estado).getCodmzna();
            ArrayList<LatLng> listaVertices = getLatLngShapeManzana(getObjectManzanaCapturaXIDEstado(idmanzana, estado).getShape());

            GeoJsonPolygon geoJsonPolygon = new GeoJsonPolygon(Collections.singletonList(listaVertices));
            HashMap<String, String> properties = new HashMap<String, String>();
            properties.put("CODZONA", codzona);
            properties.put("CODMZNA", codmzna);
            properties.put("SUFMZNA", "");
            properties.put("ESTADO", Integer.toString(estado));
            GeoJsonFeature geoJsonFeature = new GeoJsonFeature(geoJsonPolygon, codmzna, properties, null);
            GeoJsonPolygonStyle geoJsonPolygonStyle = new GeoJsonPolygonStyle();
            geoJsonPolygonStyle.setStrokeWidth(3);
            geoJsonPolygonStyle.setZIndex(3f);
            geoJsonFeature.setPolygonStyle(geoJsonPolygonStyle);
            switch (estado) {
                case 0:
                    geoJsonPolygonStyle.setStrokeColor(Color.RED);
                    layer.addFeature(geoJsonFeature);
                    break;
                case 2:
                    geoJsonPolygonStyle.setFillColor(Color.GREEN);
                    geoJsonPolygonStyle.setStrokeColor(Color.GRAY);
                    layer.addFeature(geoJsonFeature);
                    break;
                case 3:
                    geoJsonPolygonStyle.setFillColor(Color.CYAN);
                    geoJsonPolygonStyle.setStrokeColor(Color.GRAY);
                    layer.addFeature(geoJsonFeature);
                    break;
                case 4:
                    geoJsonPolygonStyle.setFillColor(Color.YELLOW);
                    geoJsonPolygonStyle.setStrokeColor(Color.GRAY);
                    layer.addFeature(geoJsonFeature);
                    break;
                case 5:
                    geoJsonPolygonStyle.setFillColor(Color.BLUE);
                    geoJsonPolygonStyle.setStrokeColor(Color.GRAY);
                    layer.addFeature(geoJsonFeature);
                    break;
                case 6:
                    geoJsonPolygonStyle.setFillColor(Color.RED);
                    geoJsonPolygonStyle.setStrokeColor(Color.GRAY);
                    layer.addFeature(geoJsonFeature);
                    break;
                default:
            }
        } else {
            Toast.makeText(getContext(), "No se cargo manzana en el marco", Toast.LENGTH_SHORT).show();
        }
    }

    /*5. EJECUTAR EVENTO DEPENDIEDNO DEL ESTADO DEL POLIGONO*/
    @SuppressLint("RestrictedApi")
    public void setEventoEstado() {
        layer.setOnFeatureClickListener(new GeoJsonLayer.GeoJsonOnFeatureClickListener() {
            @Override
            public void onFeatureClick(Feature feature) {
                Log.e("mensajesZ:", ""+feature);
                if(feature!=null){
                    int idManzana = getObjectManzanaCapturaXID(feature.getProperty("CODMZNA")).getEstado();
                    final int EVENTO = idManzana;
                    switch (EVENTO) {
                        case 0:
                            Log.e("mensajes:", "0");
                            if(manzanaSeleccionadaEnvio.size()>0)
                            {
                                if (filterManzana(manzanaSeleccionadaEnvio, feature.getProperty("CODMZNA") + "" + feature.getProperty("SUFMZNA"))) {
                                    visualizeSeleccionManzana(feature.getProperty("CODMZNA") + "" + feature.getProperty("SUFMZNA"), feature.getProperty("CODZONA"), 1);
                                } else {
                                    String idmanzanax = feature.getProperty("CODMZNA") + "" + feature.getProperty("SUFMZNA");
                                    OpenDialogFusion(idmanzanax.trim(), manzanaSeleccionadaEnvio);
                                    Toast.makeText(getContext(), "Ya Selecciono Manzana", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                visualizeValidacionManzana(feature.getProperty("CODMZNA") + "" + feature.getProperty("SUFMZNA"));
                            }
                            break;
                        case 2:
                            Log.e("mensajes:", "2");
                            visualizeRestaurarManzana(feature.getProperty("CODMZNA") + "" + feature.getProperty("SUFMZNA"), feature.getProperty("ESTADO"), (GeoJsonFeature) feature);
                            break;
                        case 3:
                            Log.e("mensajes:", "3");
                            visualizeRestaurarManzana(feature.getProperty("CODMZNA") + "" + feature.getProperty("SUFMZNA"), feature.getProperty("ESTADO"), (GeoJsonFeature) feature);
                            break;
                        case 4:
                            Log.e("mensajes:", "4");
                            visualizeMensajeManzana(feature.getProperty("CODMZNA") + "" + feature.getProperty("SUFMZNA"));
                            break;
                        case 5:
                            Log.e("mensajes:", "5");
                            visualizeRestaurarManzana(feature.getProperty("CODMZNA") + "" + feature.getProperty("SUFMZNA"), feature.getProperty("ESTADO"), (GeoJsonFeature) feature);
                            break;
                        case 6:
                            Log.e("mensajes:", "4");
                            visualizeRestaurarManzana(feature.getProperty("CODMZNA") + "" + feature.getProperty("SUFMZNA"), feature.getProperty("ESTADO"), (GeoJsonFeature) feature);
                            break;

                        case 8:
                            Log.e("mensajes:", "8");
                            visualizeRestaurarManzana(feature.getProperty("CODMZNA") + "" + feature.getProperty("SUFMZNA"), feature.getProperty("ESTADO"), (GeoJsonFeature) feature);
                            break;


                        default:
                    }
                }

            }
        });
    }

    /*6. REMOVER FEATURE DE LAYER DE MANZANA SELECCIONADA*/
    public void removeFeature(GeoJsonFeature feature) {
        layer.removeFeature(feature);
    }

    /*7. VISUALIZAR DIALOGO RESTAURAR MANZANA*/
    public void visualizeRestaurarManzana(final String idmanzana, final String estado, final GeoJsonFeature feature) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), R.style.ThemeOverlay_MaterialComponents_Dialog);
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.layout_form_poligono, null);
        final TextView mensaje = (TextView) dialogView.findViewById(R.id.id_form_mensaje);
        final LinearLayout ly = (LinearLayout) dialogView.findViewById(R.id.id_form_ly_mensaje);
        ly.setVisibility(View.VISIBLE);
        mensaje.setText("Desea Restaurar la Manzana?");
        alert.setTitle("Nro Manzana: " + idmanzana);
        alert.setIcon(R.drawable.ic_action_turn_right);
        alert.setView(dialogView);
        alert.setPositiveButton("Restaurar", null);
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
                        if (estado.equals("2")) {
                            updateManzanaCaptura(idmanzana.trim(), "", 0);
                            removeFeature(feature);
                            loadOnlyManzanaModificada(idmanzana.trim(),0);
                            Toast.makeText(getActivity().getApplicationContext(), "Manzana Restaurada", Toast.LENGTH_SHORT).show();
                        }
                        if (estado.equals("3")) {
                            final int size = getListaManzanaCapturaXEstadoXMznabelong(idmanzana).size();
                            final ArrayList<ManzanaCaptura> lista = getListaManzanaCapturaXEstadoXMznabelong(idmanzana);
                            for (int i = 0; i < size; i++) {
                                updateManzanaCaptura(lista.get(i).getCodmzna(), "", 0);
                            }
                            removeFeature(feature);
                            deleteManzanaCaptura(idmanzana.trim());
                        }
                        if (estado.equals("5")) {
                            //updateManzanaCaptura(getManzanaCapturaXMznabelong(idmanzana).getCodmzna(), "", 0);
                            updateManzanaCaptura(getObjectManzanaCapturaXID(idmanzana).getMznabelong(), "", 0);
                            removeFeature(feature);
                            deleteManzanaCaptura(idmanzana.trim());
                        }
                        if (estado.equals("6")) {
                            updateManzanaCaptura(idmanzana.trim(), "", 0);
                            removeFeature(feature);
                            loadOnlyManzanaModificada(idmanzana.trim(),0);
                        }
                        if (estado.equals("8")) {
                            final int size = getListaManzanaCapturaXEstadoXMznabelong(idmanzana).size();
                            final ArrayList<ManzanaCaptura> lista = getListaManzanaCapturaXEstadoXMznabelong(idmanzana);
                            for (int i = 0; i < size; i++) {
                                deleteManzanaCaptura(lista.get(i).getCodmzna());
                            }
                            //removeFeature(feature);
                            updateManzanaCaptura(idmanzana.trim(),"",0);
                        }
                        if (estado.equals("9")) {
                            updateManzanaCaptura(getManzanaCapturaXMznabelong(idmanzana).getCodmzna(), "", 0);
                            removeFeature(feature);
                            deleteManzanaCaptura(idmanzana.trim());
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

    /*******CONFIRMAR**********/

    /* VISUALIZAR DIALOGO CONFIRMAR O MODIFICAR MANZANA*/
    public void visualizeValidacionManzana(final String idmanzana) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), R.style.ThemeOverlay_MaterialComponents_Dialog);
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.layout_form_poligono, null);
        final TextView mensaje = (TextView) dialogView.findViewById(R.id.id_form_mensaje);
        final LinearLayout ly = (LinearLayout) dialogView.findViewById(R.id.id_form_ly_mensaje);
        ly.setVisibility(View.VISIBLE);
        mensaje.setText("Confirme o Modifique manzana");
        alert.setTitle("Nro Manzana: " + idmanzana);
        alert.setIcon(R.drawable.ic_view_module_26);
        alert.setView(dialogView);
        alert.setPositiveButton("Confirmar", null);
        alert.setNegativeButton("Modificar", null);
        final AlertDialog alertDialog = alert.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button b1 = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button b2 = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                b1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        updateManzanaCaptura(idmanzana.trim(), "", 2);
                        loadOnlyManzanaModificada(idmanzana, 2);
                    }
                });
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        visualizeModificarManzana("001", idmanzana);

                    }
                });
            }
        });
        alertDialog.show();
    }

    /*******MODIFICAR**********/

    /* VISUALIZAR DIALOGO DE MODIFICACION DE MANZANA (FUSION,REPLANTEAR,ETC)*/
    public void visualizeModificarManzana(final String codzona, final String idmanzana) {
        ArrayList<String> acciones = new ArrayList<>();
        acciones.add("Fusionar");
        acciones.add("Fraccionar");
        acciones.add("Replantear");
        acciones.add("Eliminar");

        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), R.style.ThemeOverlay_MaterialComponents_Dialog);
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.layout_form_accion, null);
        final Spinner accion = (Spinner) dialogView.findViewById(R.id.id_accion_sp_accion);
        final EditText numero = (EditText) dialogView.findViewById(R.id.id_accion_edt_numero);
        final LinearLayout ly = (LinearLayout) dialogView.findViewById(R.id.id_accion_ly_numero);

        ArrayAdapter<String> adapterZona = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, acciones);
        adapterZona.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accion.setAdapter(adapterZona);
        accion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        ly.setVisibility(View.GONE);
                        break;
                    case 1:
                        ly.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        ly.setVisibility(View.GONE);
                        break;
                    case 3:
                        ly.setVisibility(View.GONE);
                        break;
                    default:
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        alert.setTitle("Nro Manzana: " + idmanzana);
        alert.setIcon(R.drawable.ic_view_module_26);
        alert.setView(dialogView);
        alert.setPositiveButton("OK", null);
        alert.setNegativeButton("Cancelar", null);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        int itemAccion = accion.getSelectedItemPosition();
                        switch (itemAccion) {
                            case 0://Fusionar
                                idAccionManzana = 3;
                                manzanaSeleccionadaEnvio.clear();
                                manzanaSeleccionadaEnvio.add(new FusionItem(1, codzona, idmanzana));
                                OpenDialogFusion(idmanzana, manzanaSeleccionadaEnvio);
                                break;
                            case 1://Fraccionar
                                accionManzana = 4;
                                idManzanaSeleccionada = idmanzana;
                                cantidad = Integer.parseInt(String.valueOf(numero.getText()));
                                manzanaSeleccionadaEnvio.add(new FusionItem(1,"",""));
                                manzanaSeleccionadaEnvio.add(new FusionItem(1,"",""));
                                createPolygon();
                                setEstadoEdicion();
                                fab2.setImageResource(R.drawable.ic_action_tick_18_white);
                                break;
                            case 2://Replantear
                                accionManzana = 5;
                                idManzanaSeleccionada = idmanzana;
                                manzanaSeleccionadaEnvio.add(new FusionItem(1,"",""));
                                manzanaSeleccionadaEnvio.add(new FusionItem(1,"",""));
                                createPolygon();
                                setEstadoEdicion();
                                break;
                            case 3:
                                visualizeEliminacionManzana(idmanzana);
                                break;
                            default:
                        }
                    }
                });
            }
        });
        alertDialog.show();
    }

    /*******FUSIONAR**********/
    /*1. ABRIR DIALOGO (DialogFusionManzana) DE FUSION Y ENVIAR PARAMETROS*/
    public void OpenDialogFusion(String idManzanna, ArrayList<FusionItem> manzanas) {
        DialogFusionManzana dialogo = DialogFusionManzana.newInstance(idManzanna, manzanas);
        dialogo.setTargetFragment(MapActualizarManzanaFragment.this, 1);
        dialogo.show(getFragmentManager(), DialogFusion.TAG);
    }

    /*2. VISUALIZAR DIALOGO PARA APROBAR SELECCION DE MANZANA*/
    public void visualizeSeleccionManzana(final String idmanzana, final String zona, final int estado) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), R.style.ThemeOverlay_MaterialComponents_Dialog);
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.layout_form_selectmanzana, null);
        final TextView text_selection = (TextView) dialogView.findViewById(R.id.id_txt_select_manzana);
        text_selection.setText(idmanzana);
        alert.setTitle("Desea Seleccionar Manzana?");
        alert.setIcon(R.drawable.ic_info_outline);
        alert.setView(dialogView);
        alert.setPositiveButton("OK", null);
        alert.setNegativeButton("Cancelar", null);
        final AlertDialog alertDialog = alert.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        manzanaSeleccionadaEnvio.add(new FusionItem(estado, zona, idmanzana));
                        OpenDialogFusion(idmanzana, manzanaSeleccionadaEnvio);
                        alertDialog.dismiss();
                    }
                });
            }
        });
        alertDialog.show();
    }

    /*3. RECIBE PARAMETROS DE DIALOGO (DialogFusionManzana)*/
    @Override
    public void receiveFusion(final int estadoEdicion, final ArrayList<FusionItem> listaManzana, String idManzana) {
        newIdManzana = getNewManzana(listaManzana, listaManzana.get(0).getIdManzana());
        selectIdManzana = idManzana;
        manzanaSeleccionadaEnvio = listaManzana;
        accionManzana = 3;

        if(estadoEdicion==1){
            setEstadoEdicion();
            createPolygon();/*fuera*/
        }
    }

    /*4. INSERTAR MANZANA_CAPTURA A SQLITE INTERNO*/
    public void saveManzanaCapturaFusion() {
        if (listPoints.size() > 2) {
            if (listPoints.size() == 3) {
                LatLng dato = listPoints.get(0);
                listPoints.add(dato);
                insertManzanaCaptura(newIdManzana,"",3,listPoints);
                for (int i = 0; i < manzanaSeleccionadaEnvio.size(); i++) {
                    updateManzanaCaptura(manzanaSeleccionadaEnvio.get(i).getIdManzana().trim(), newIdManzana, 7);
                }
                cleanPolygon();
                removeLayer();
                createLayerGeojsonMain();
                loadFeatureAllManzanas();
                setEventoEstado();
                Toast.makeText(getContext(), "Se registro Manzana correctamente!", Toast.LENGTH_SHORT).show();
            } else {
                insertManzanaCaptura(newIdManzana,"",3,listPoints);
                for (int i = 0; i < manzanaSeleccionadaEnvio.size(); i++) {
                    updateManzanaCaptura(manzanaSeleccionadaEnvio.get(i).getIdManzana().trim(), newIdManzana, 7);
                }
                cleanPolygon();
                removeLayer();
                createLayerGeojsonMain();
                loadFeatureAllManzanas();
                cleanPolygon();
                setEventoEstado();
                Toast.makeText(getContext(), "Se registro Manzana correctamente!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Dibuje una Manzana", Toast.LENGTH_SHORT).show();
        }
    }

    /*******FRACCIONAR**********/

    /*1. INSERTAR MANZANAS FRACCIONADAS*/
    @SuppressLint("RestrictedApi")
    public void saveManzanaCapturaFraccionada(){
        ArrayList<String> listaManzanasNuevas = generateNewIdManzana(cantidad,idManzanaSeleccionada);
        String idManzanaNueva;
        final int CANTIDAD= cantidad;
        if (listPoints.size() == 3){
            LatLng dato = listPoints.get(0);
            listPoints.add(dato);

            if(contadorManzana<CANTIDAD){
                idManzanaNueva = listaManzanasNuevas.get(contadorManzana);
                listaManzanasMemoria.add(new ManzanaReplanteo(idManzanaNueva,new ArrayList<LatLng>(listPoints)));
                cleanListMarker();
                createPolygon();
                if(contadorManzana==(CANTIDAD-1))
                {
                    poligon.remove();
                    cleanListMarker();
                    setEstadoGuardado();
                }
                contadorManzana++;

            }
            else{
                updateManzanaCaptura(idManzanaSeleccionada,"",8);
                for (int i=0;i<listaManzanasMemoria.size();i++){
                    ArrayList<LatLng> listafinal = new ArrayList<>();
                    for (int x=0;x<listaManzanasMemoria.get(i).getLista().size();x++)
                    {
                        listafinal.add(listaManzanasMemoria.get(i).getLista().get(x));
                    }
                    insertManzanaCaptura(listaManzanasMemoria.get(i).getIdManzana(),idManzanaSeleccionada,4,listafinal);
                    cleanPolygon();
                    removeLayer();
                    createLayerGeojsonMain();
                    loadFeatureAllManzanas();
                    setEventoEstado();
                    Toast.makeText(getContext(), "Se registro Manzana correctamente!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else{
            if(contadorManzana<CANTIDAD){
                idManzanaNueva = listaManzanasNuevas.get(contadorManzana);
                listaManzanasMemoria.add(new ManzanaReplanteo(idManzanaNueva,new ArrayList<LatLng>(listPoints)));
                cleanListMarker();
                createPolygon();
                if(contadorManzana==(CANTIDAD-1))
                {
                    poligon.remove();
                    cleanListMarker();
                    setEstadoGuardado();
                }
                contadorManzana++;

            }
            else{
                updateManzanaCaptura(idManzanaSeleccionada,"",8);
                for (int i=0;i<listaManzanasMemoria.size();i++){
                    ArrayList<LatLng> listafinal = new ArrayList<>();
                    for (int x=0;x<listaManzanasMemoria.get(i).getLista().size();x++)
                    {
                        listafinal.add(listaManzanasMemoria.get(i).getLista().get(x));
                    }
                    insertManzanaCaptura(listaManzanasMemoria.get(i).getIdManzana(),idManzanaSeleccionada,4,listafinal);
                    cleanPolygon();
                    removeLayer();
                    createLayerGeojsonMain();
                    loadFeatureAllManzanas();
                    setEventoEstado();
                    Toast.makeText(getContext(), "Se registro Manzana correctamente!", Toast.LENGTH_SHORT).show();
                }
            }
        }
//        if(contadorManzana<CANTIDAD){
//            idManzanaNueva = listaManzanasNuevas.get(contadorManzana);
//            listaManzanasMemoria.add(new ManzanaReplanteo(idManzanaNueva,new ArrayList<LatLng>(listPoints)));
//            cleanListMarker();
//            createPolygon();
//            if(contadorManzana==(CANTIDAD-1))
//            {
//                poligon.remove();
//                cleanListMarker();
//                setEstadoGuardado();
//            }
//            contadorManzana++;
//
//        }
//        else{
//             updateManzanaCaptura(idManzanaSeleccionada,"",8);
//            for (int i=0;i<listaManzanasMemoria.size();i++){
//                ArrayList<LatLng> listafinal = new ArrayList<>();
//                for (int x=0;x<listaManzanasMemoria.get(i).getLista().size();x++)
//                {
//                    listafinal.add(listaManzanasMemoria.get(i).getLista().get(x));
//                }
//                insertManzanaCaptura(listaManzanasMemoria.get(i).getIdManzana(),idManzanaSeleccionada,4,listafinal);
//                cleanPolygon();
//                removeLayer();
//                createLayerGeojsonMain();
//                loadFeatureAllManzanas();
//                setEventoEstado();
//                Toast.makeText(getContext(), "Se registro Manzana correctamente!", Toast.LENGTH_SHORT).show();
//            }
//        }
    }

    /*2. VISUALIZAR DIALOGO DE INFORMACION DE MANZANA*/
    public  void visualizeMensajeManzana(final String idmanzana){
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(),R.style.ThemeOverlay_MaterialComponents_Dialog);
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.layout_form_poligono, null);
        final TextView mensaje = (TextView) dialogView.findViewById(R.id.id_form_mensaje);
        final LinearLayout ly = (LinearLayout) dialogView.findViewById(R.id.id_form_ly_mensaje);
        ly.setVisibility(View.VISIBLE);
        mensaje.setText("Manzana Franccionada:"+idmanzana);
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

    /*3. REESTABLECER MANZANAS FRACCIONADAS*/
    @SuppressLint("RestrictedApi")
    public void restoreManzanaCapturaFraccionada(String idmanzana){
    }

    /*******REPLANTEAR**********/
    /*1. INSERTAR MANZANAS FRACCIONADAS*/
    @SuppressLint("RestrictedApi")
    public void saveManzanaCapturaReplanteada(){
        if (listPoints.size() == 3) {
            LatLng dato = listPoints.get(0);
            listPoints.add(dato);

            //ArrayList<String> listaManzanasNuevas = replantear(cantidad,idManzanaSeleccionada);
            String idManzanaNueva = generateNewIdManzana(1,idManzanaSeleccionada).get(0);
            updateManzanaCaptura(idManzanaSeleccionada,"",9);
            insertManzanaCaptura(idManzanaNueva,idManzanaSeleccionada,5,listPoints);
            cleanPolygon();
            removeLayer();
            createLayerGeojsonMain();
            loadFeatureAllManzanas();
            setEventoEstado();
            Toast.makeText(getContext(), "Se registro Manzana correctamente!", Toast.LENGTH_SHORT).show();
        }
        else{
            //ArrayList<String> listaManzanasNuevas = replantear(cantidad,idManzanaSeleccionada);
            String idManzanaNueva = generateNewIdManzana(1,idManzanaSeleccionada).get(0);
            updateManzanaCaptura(idManzanaSeleccionada,"",9);
            insertManzanaCaptura(idManzanaNueva,idManzanaSeleccionada,5,listPoints);
            cleanPolygon();
            removeLayer();
            createLayerGeojsonMain();
            loadFeatureAllManzanas();
            setEventoEstado();
            Toast.makeText(getContext(), "Se registro Manzana correctamente!", Toast.LENGTH_SHORT).show();
        }
    }

    /*******ELIMINAR**********/
    /* VISUALIZAR DIALOGO ELIMNINAR MANZANA*/
    public void visualizeEliminacionManzana(final String idmanzana) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), R.style.ThemeOverlay_MaterialComponents_Dialog);
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.layout_form_poligono, null);
        final TextView mensaje = (TextView) dialogView.findViewById(R.id.id_form_mensaje);
        final LinearLayout ly = (LinearLayout) dialogView.findViewById(R.id.id_form_ly_mensaje);
        ly.setVisibility(View.VISIBLE);
        mensaje.setText("Desea Eliminar Manzana?");
        alert.setTitle("Nro Manzana: " + idmanzana);
        alert.setIcon(R.drawable.ic_delete_forever_24);
        alert.setView(dialogView);
        alert.setPositiveButton("Eliminar", null);
        alert.setNegativeButton("Cancelar", null);
        final AlertDialog alertDialog = alert.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button b1 = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button b2 = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                b1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        updateManzanaCaptura(idmanzana.trim(), "", 6);
                        loadOnlyManzanaModificada(idmanzana, 6);
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

    /*******METODOS DE INTERACCION CON SQLITE***********/

    /*1. ACTUALIZAR ESTADO(CONFIRMAR,RESTAURAR Y OTROS) DE MANZANA EN SQLITE*/
    public void insertManzanaCaptura(String idManzana,String mznaBelong,int estado,ArrayList<LatLng> puntos) {
                try {
                    data = new Data(context);
                    data.open();
                    data.insertManzanaCaptura(1, 2, "15", "01", "13", "001", "00", idManzana, "", mznaBelong, estado, 0, "GeomFromText('POLYGON((" + formatGeom(puntos) + "))',4326)");
                } catch (IOException e) {
                    e.printStackTrace();
                }

    }

    public void updateManzanaCaptura(String idmanzana, String mznabelog, int estado) {
        try {
            data = new Data(context);
            data.open();
            data.updateManzanaCaptura(idmanzana, mznabelog, estado);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*2. ELIMINAR MANZANA_CAPTURA EN SQLITE INTERNO*/
    public void deleteManzanaCaptura(String idmanzana) {
        try {
            data = new Data(context);
            data.open();
            data.deleteManzanaCaptura(idmanzana);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*************METODOS DE RESPALDO*************************/

    /*METODO CONVERTIR LISTA(STRING) A LISTA(LATLNG)*/
    public ArrayList<LatLng> getLatLngShapeManzana(String shape) {
        ArrayList<LatLng> listapintado = new ArrayList<LatLng>();
        String campoGeom = shape;
        try {
            JSONObject jsonObject = new JSONObject(campoGeom);
            String dato = jsonObject.getString("coordinates");
            //Log.e("mensaje:","String de datos->[]:"+dato);
            String ncadena1 = dato.substring(1, dato.length() - 1);
            String ncadena2 = ncadena1.substring(1, ncadena1.length() - 1);
            String ncadena3 = ncadena2.replace("],[", "];[");
            String[] parts = ncadena3.split(";");
            for (int i = 0; i < parts.length; i++) {
                String part1 = parts[i];
                String cadena4 = part1.substring(1, part1.length() - 1);
                String[] latlog = cadena4.split(",");
                for (int x = 0; x < 1; x++) {
                    listapintado.add(new LatLng(Double.parseDouble(latlog[0]), Double.parseDouble(latlog[1])));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        for(int x=0;x<listapintado.size();x++){
//            Log.e("Mensaje:","Puntos GPS metodo ->["+x+"]:"+listapintado.get(x));
//        }
        return listapintado;
    }

    /*METODO DE FORMATO A POLYGONO*/
    public String formatGeom(ArrayList<LatLng> poligono) {
        String format = "";
        for (int i = 0; i < poligono.size(); i++) {
            if (i > 0) {
                format = format + "," + poligono.get(i).latitude + " " + poligono.get(i).longitude;
            } else {
                format = poligono.get(i).latitude + " " + poligono.get(i).longitude;
            }
        }
        return format;
    }

    /*CREACION DE NUEVO ID DE MANZANA*/
    public String getNewManzana(ArrayList<FusionItem> listaManzana, String idManzana) {
        int menor;
        int valorid;
        int newValorId = 0;
        if (listaManzana.size() > 0) {
            menor = Integer.parseInt(listaManzana.get(0).getIdManzana().trim());
            for (FusionItem objeto : listaManzana) {
                int numero = Integer.parseInt(objeto.getIdManzana().trim());
                if (numero < menor) {
                    menor = numero;
                }
            }
            valorid = Integer.parseInt(idManzana.trim());
            if (menor < valorid) {
                newValorId = menor;
            } else {
                newValorId = valorid;
            }
        }
        return countDigitos(newValorId) + "A";
    }

    /*FILTRAR MANZANAS*/
    private boolean filterManzana(ArrayList<FusionItem> manzanas, String manzana) {
        boolean respuesta = true;
        manzana = manzana.toLowerCase().trim();
        for (FusionItem valor1 : manzanas) {
            String valor2 = valor1.getIdManzana().toLowerCase();
            if (valor2.contains(manzana)) {
                respuesta = false;
            }
        }
        return respuesta;
    }

    /*OBTENER UN REGISTRO DEL OBJETO CAPTURA MANZANA*/
    public ManzanaCaptura getObjectManzanaCapturaXIDEstado(String idmzna, int estado) {
        ManzanaCaptura manzanaCaptura = null;
        try {
            Data data = new Data(context);
            data.open();
            manzanaCaptura = data.getManzanaCapturaXIdEstado(idmzna, estado);
            Log.e("Mensajezx:", "" + manzanaCaptura);
            data.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.getCause();
        }
        return manzanaCaptura;
    }

    /*OBTENER UN REGISTRO DEL OBJETO CAPTURA MANZANA X ID*/
    public ManzanaCaptura getObjectManzanaCapturaXID(String idmzna) {
        ManzanaCaptura manzanaCaptura = null;
        try {
            Data data = new Data(context);
            data.open();
            manzanaCaptura = data.getManzanaCapturaXId(idmzna);
            data.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.getCause();
        }
        return manzanaCaptura;
    }

    /*METODO OBTENER ARRAYLIST DE OBJETO MANZANA CAPTURA*/
    public ArrayList<ManzanaCaptura> getListaManzanaCaptura() {
        ArrayList<ManzanaCaptura> listaManzana = new ArrayList<>();
        try {
            Data data = new Data(context);
            data.open();
            listaManzana = data.getAllManzanaCaptura();
            data.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listaManzana;
    }

    /*METODO OBTENER ARRAYLIST DE OBJETO MANZANA CAPTURA POR MZNABELONG*/
    public ArrayList<ManzanaCaptura> getListaManzanaCapturaXEstadoXMznabelong(String mznabelong) {
        ArrayList<ManzanaCaptura> listaManzana = new ArrayList<>();
        try {
            Data data = new Data(context);
            data.open();
            listaManzana = data.getAllManzanaCapturaXMznabelong(mznabelong);
            data.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listaManzana;
    }

    /*METODO OBTENER OBJETO MANZANA CAPTURA POR MZNABELONG*/
    public ManzanaCaptura getManzanaCapturaXMznabelong(String mznabelong) {
        ManzanaCaptura manzana = new ManzanaCaptura();
        try {
            Data data = new Data(context);
            data.open();
            manzana = data.getManzanaCapturaXMznabelong(mznabelong);
            data.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return manzana;
    }

    /*ASIGNAR EDICION*/
    @SuppressLint("RestrictedApi")
    public void setEstadoEdicion() {
        fab2.setVisibility(View.VISIBLE);
        fab4.setVisibility(View.VISIBLE);
        fab5.setVisibility(View.VISIBLE);
        removeLayer();
        createLayerGeojsonMain();
        loadPolygonAllManzanaEdicion();
    }

    /*ASIGNAR EDICION*/
    @SuppressLint("RestrictedApi")
    public void setEstadoGuardado() {
        fab2.setImageResource(R.drawable.ic_action_save);
        fab4.setVisibility(View.GONE);
        fab5.setVisibility(View.GONE);
    }

    /*CREAR POLIGONO*/
    public void createPolygon() {
        poligon = mgoogleMap.addPolygon(new PolygonOptions()
                .add(new LatLng(1, 1))
                .fillColor(Color.GREEN)
                .clickable(false)
                .zIndex(2f)
                .strokeWidth(5));
        Log.e("Mensaje:", "poligono creado");
    }

    /*LIMPIAR POLIGONO*/
    @SuppressLint("RestrictedApi")
    public void cleanPolygon() {
        manzanaSeleccionadaEnvio.clear();
        if (poligon != null) {
            poligon.remove();
        }
        fab2.setVisibility(View.GONE);
        fab4.setVisibility(View.GONE);
        fab5.setVisibility(View.GONE);
        if (listaMarker != null) {
            for (int i = 0; i < listaMarker.size(); i++) {
                listaMarker.get(i).remove();
            }
        }
        listaMarker.clear();
        listPoints.clear();

    }

    /*LIMPIAR POLIGONO*/
    @SuppressLint("RestrictedApi")
    public void cleanListMarker() {
        if (listaMarker != null) {
            for (int i = 0; i < listaMarker.size(); i++) {
                listaMarker.get(i).remove();
            }
        }
        listaMarker.clear();
        listPoints.clear();

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
                    .zIndex(3f)
                    .strokeWidth(5));
            }
            else{poligon = mgoogleMap.addPolygon(new PolygonOptions()
                    .addAll(listPoints)
                    .fillColor(Color.GREEN)
                    .zIndex(3f)
                    .strokeWidth(5));}
        }
    }

    /*QUITAR CAPA*/
    public void removeLayer() {
        layer.removeLayerFromMap();
        Log.e("Mensaje:", "Capa removida");
    }

    /*GENERAR NUEVO ID DE MANZANA*/
    public static ArrayList<String> generateNewIdManzana(int cantidad, String idmanzana){
        ArrayList<String> lista = new ArrayList<>();
        char valor='0';
        int a = 65;
        for (int i=0;i<cantidad;i++)
        {
            valor = (char) a;
            lista.add(idmanzana+String.valueOf(valor));
            a++;
        }
        return lista;
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





}
