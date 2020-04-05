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
import com.google.maps.android.data.geojson.GeoJsonPolygon;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;
import com.inei.appcartoinei.R;
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
    private ArrayList<FusionItem> listaManzanasSeleccionas = new ArrayList<FusionItem>();

    private ArrayList<LatLng> listados = new ArrayList<LatLng>();

    private int estadoDibujado = 0;
    private String newIdManzana = "";
    private String selectIdManzana = "";
    private FloatingActionButton fab2;
    private FloatingActionButton fab4;
    private FloatingActionButton fab5;
    private SQLiteDatabase db;
    private DataBaseHelper op;
    Data data;
    Context context;
    final String ubigeo;
    final String codigoZona;
    final String sufijoZona;


    GeoJsonLayer layer;
    GeoJsonPolygonStyle polygonStyle;

    private int accionManzana = 0;

    /*Replantear*/
    private int cantidad = 0;
    private int contadorManzana = 0;
    private String idManzanaSeleccionada = "";
    private ArrayList<ManzanaReplanteo> listaManzanasMemoria = new ArrayList<ManzanaReplanteo>();

    private OnFragmentInteractionListener mListener;

    public MapActualizarManzanaFragment(String ubigeo,String codigoZona,String sufijoZona,Context context) {
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
        Log.i("Zona:",""+codigoZona);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (estadoDibujado==0) {
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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*2. CARGAR FEATURE DE TODAS LAS MANZANAS */
    private void loadFeatureAllManzanas() {
        ArrayList<LatLng> listaVertices = new ArrayList<>();
        String codZona = "";
        String sufZona = "";
        String codMzna = "";
        String sufMzna = "";
        String estado  = "";
        if(getListaManzanaCapturaXZona().isEmpty()) {
            Toast.makeText(getContext(), "No se encontraron Manzanas", Toast.LENGTH_SHORT).show();
        }
        else {
            for(int i=0;i<getListaManzanaCapturaXZona().size();i++){

                codZona = getListaManzanaCapturaXZona().get(i).getCodzona();
                sufZona = getListaManzanaCapturaXZona().get(i).getSufzona();
                codMzna = getListaManzanaCapturaXZona().get(i).getCodmzna();
                sufMzna = getListaManzanaCapturaXZona().get(i).getSufmzna();
                estado = Integer.toString(getListaManzanaCapturaXZona().get(i).getEstado());
                Log.i("valor:",""+codZona+"/"+estado);
                listaVertices = getLatLngShapeManzana(getListaManzanaCapturaXZona().get(i).getShape());

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
                geoJsonFeature.setPolygonStyle(geoJsonPolygonStyle);
                layer.isLayerOnMap();

                if(estado.equals("0"))
                {
                    geoJsonPolygonStyle.setZIndex(0f);
                    geoJsonPolygonStyle.setStrokeColor(Color.RED);
                    geoJsonFeature.setPolygonStyle(geoJsonPolygonStyle);
                    layer.addFeature(geoJsonFeature);
                }
                if(estado.equals("1"))
                {
                    geoJsonPolygonStyle.setZIndex(0f);
                    geoJsonPolygonStyle.setStrokeColor(Color.MAGENTA);
                    geoJsonFeature.setPolygonStyle(geoJsonPolygonStyle);
                    layer.addFeature(geoJsonFeature);
                }
                if(estado.equals("2"))
                {
                    geoJsonPolygonStyle.setZIndex(2f);
                    geoJsonPolygonStyle.setFillColor(Color.GREEN);
                    geoJsonPolygonStyle.setStrokeColor(Color.GRAY);
                    geoJsonFeature.setPolygonStyle(geoJsonPolygonStyle);
                    layer.addFeature(geoJsonFeature);
                }
                if(estado.equals("3"))
                {
                    geoJsonPolygonStyle.setZIndex(2f);
                    geoJsonPolygonStyle.setFillColor(Color.CYAN);
                    geoJsonPolygonStyle.setStrokeColor(Color.GRAY);
                    geoJsonFeature.setPolygonStyle(geoJsonPolygonStyle);
                    layer.addFeature(geoJsonFeature);
                }
                if(estado.equals("4"))
                {
                    geoJsonPolygonStyle.setZIndex(3f);
                    geoJsonPolygonStyle.setFillColor(Color.YELLOW);
                    geoJsonPolygonStyle.setStrokeColor(Color.GRAY);
                    geoJsonFeature.setPolygonStyle(geoJsonPolygonStyle);
                    layer.addFeature(geoJsonFeature);
                }
                if(estado.equals("5"))
                {
                    geoJsonPolygonStyle.setZIndex(3f);
                    geoJsonPolygonStyle.setFillColor(Color.BLUE);
                    geoJsonPolygonStyle.setStrokeColor(Color.GRAY);
                    geoJsonFeature.setPolygonStyle(geoJsonPolygonStyle);
                    layer.addFeature(geoJsonFeature);
                }
                if(estado.equals("6"))
                {
                    geoJsonPolygonStyle.setZIndex(3f);
                    geoJsonPolygonStyle.setFillColor(Color.RED);
                    geoJsonPolygonStyle.setStrokeColor(Color.GRAY);
                    geoJsonFeature.setPolygonStyle(geoJsonPolygonStyle);
                    layer.addFeature(geoJsonFeature);
                }
                if(estado.equals("7"))
                {
                    geoJsonPolygonStyle.setZIndex(0f);
                    geoJsonPolygonStyle.setStrokeColor(Color.RED);
                    geoJsonFeature.setPolygonStyle(geoJsonPolygonStyle);
                    layer.addFeature(geoJsonFeature);
                }
                if(estado.equals("8"))
                {
                    geoJsonPolygonStyle.setZIndex(0f);
                    geoJsonPolygonStyle.setStrokeColor(Color.RED);
                    geoJsonFeature.setPolygonStyle(geoJsonPolygonStyle);
                    layer.addFeature(geoJsonFeature);
                }
                if(estado.equals("9"))
                {
                    geoJsonPolygonStyle.setZIndex(0f);
                    geoJsonPolygonStyle.setStrokeColor(Color.RED);
                    geoJsonFeature.setPolygonStyle(geoJsonPolygonStyle);
                    layer.addFeature(geoJsonFeature);
                }
            }
        }
    }

    /*3. CARGAR POLIGONOS PARA EDICION*/
    public void loadPolygonsEdicionManzanas() {
        if (listaManzanasSeleccionas.size()>0) {
            for(int i=0;i<listaManzanasSeleccionas.size();i++){
                ArrayList<LatLng> listaVertices = getLatLngShapeManzana(getObjectManzanaCapturaXZonaMzna(codigoZona,sufijoZona,getDigitos(listaManzanasSeleccionas.get(i).getIdManzana()),getUltimoDigito(listaManzanasSeleccionas.get(i).getIdManzana())).getShape());
                Polygon polygono = mgoogleMap.addPolygon(new PolygonOptions().addAll(listaVertices));
                polygono.setStrokeJointType(JointType.ROUND);
                polygono.setStrokeWidth(3f);
                polygono.setZIndex(0f);
                polygono.setClickable(false);
                polygono.setStrokeColor(Color.RED);
            }
        } else {
            Toast.makeText(getContext(), "No se cargo poligonos", Toast.LENGTH_SHORT).show();
        }
    }

    /*4. CARGAR POLIGONO PARA EDICION*/
    public void loadPolygonEdicionManzana(String codZona,String sufZona,String codMzna,String sufMzna) {
        if (codMzna!=null) {
            ArrayList<LatLng> listaVertices = getLatLngShapeManzana(getObjectManzanaCapturaXZonaMzna(codZona,sufZona,codMzna,sufMzna).getShape());
            Polygon polygono = mgoogleMap.addPolygon(new PolygonOptions().addAll(listaVertices));
            polygono.setStrokeJointType(JointType.ROUND);
            polygono.setStrokeWidth(3f);
            polygono.setZIndex(0f);
            polygono.setClickable(false);
            polygono.setStrokeColor(Color.RED);
        } else {
            Toast.makeText(getContext(), "No se cargo poligono", Toast.LENGTH_SHORT).show();
        }
    }

    /*5. CARGAR MANZANA MODIFICADA(CONFIRMADA,FUSION,FRACCIONAR,REPLANTEAR,ELIMINAR)*/
    public void loadOnlyManzanaModificada(String codZona,String sufZona,String codMzna,String sufMzna, int estado) {
        Log.i("valores:",""+codZona+"/"+sufZona+"/"+codMzna+"/"+sufMzna+"/"+estado);
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
            geoJsonFeature.setPolygonStyle(geoJsonPolygonStyle);
            Log.i("valores:",""+codMzna+"/"+estado);
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

    /*6. EJECUTAR EVENTO DEPENDIEDNO DEL ESTADO DEL POLIGONO*/
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
                            if(accionManzana==0)
                            {
                                visualizeValidacionManzana(feature.getProperty("CODZONA"),feature.getProperty("SUFZONA"),feature.getProperty("CODMZNA") , feature.getProperty("SUFMZNA"));
                            }
                            if(accionManzana==3)
                            {if(listaManzanasSeleccionas.size()>0)
                               {
                                if (filterManzana(listaManzanasSeleccionas, feature.getProperty("CODMZNA") + "" + feature.getProperty("SUFMZNA"))) {
                                    visualizeSeleccionManzana(feature.getProperty("CODMZNA") + "" + feature.getProperty("SUFMZNA"), feature.getProperty("CODZONA"), 1);
                                } else {
                                    String idmanzanax = feature.getProperty("CODMZNA") + "" + feature.getProperty("SUFMZNA");
                                    OpenDialogFusion(idmanzanax.trim(), listaManzanasSeleccionas);
                                    Toast.makeText(getContext(), "Ya Selecciono Manzana", Toast.LENGTH_SHORT).show();
                                }
                               }
                            }
                            break;
                        case 2:
                            Log.e("mensajesx:", "2");
                            visualizeRestaurarManzana(feature.getProperty("CODZONA"),feature.getProperty("SUFZONA"),feature.getProperty("CODMZNA"),feature.getProperty("SUFMZNA"), feature.getProperty("ESTADO"), (GeoJsonFeature) feature);
                            break;
                        case 3:
                            Log.e("mensajes:", "Fusionada");
                            visualizeRestaurarManzana(feature.getProperty("CODZONA"),feature.getProperty("SUFZONA"),feature.getProperty("CODMZNA"),feature.getProperty("SUFMZNA"), feature.getProperty("ESTADO"), (GeoJsonFeature) feature);
                            break;
                        case 4:
                            Log.e("mensajes:", "4");
                            visualizeMensajeManzana(feature.getProperty("CODZONA"),feature.getProperty("CODMZNA"),feature.getProperty("SUFMZNA"));
                            break;
                        case 5:
                            Log.e("mensajes:", "5");
                            visualizeRestaurarManzana(feature.getProperty("CODZONA"),feature.getProperty("SUFZONA"),feature.getProperty("CODMZNA"),feature.getProperty("SUFMZNA"), feature.getProperty("ESTADO"), (GeoJsonFeature) feature);
                            break;
                        case 6:
                            Log.e("mensajes:", "4");
                            visualizeRestaurarManzana(feature.getProperty("CODZONA"),feature.getProperty("SUFZONA"),feature.getProperty("CODMZNA"),feature.getProperty("SUFMZNA"), feature.getProperty("ESTADO"), (GeoJsonFeature) feature);
                            break;

                        case 8:
                            Log.e("mensajes:", "8");
                            visualizeRestaurarManzana(feature.getProperty("CODZONA"),feature.getProperty("SUFZONA"),feature.getProperty("CODMZNA"),feature.getProperty("SUFMZNA"), feature.getProperty("ESTADO"), (GeoJsonFeature) feature);
                            break;


                        default:
                    }
                }

            }
        });
    }

    /*7. REMOVER FEATURE DE LAYER DE MANZANA SELECCIONADA*/
    public void removeFeature(GeoJsonFeature feature) {
        layer.removeFeature(feature);
    }

    /*8. VISUALIZAR DIALOGO RESTAURAR MANZANA*/
    public void visualizeRestaurarManzana(final String codZona,final String sufZona,final String codMzna,final String sufMzna, final String estado, final GeoJsonFeature feature) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), R.style.ThemeOverlay_MaterialComponents_Dialog);
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.layout_form_poligono, null);
        final TextView mensaje = (TextView) dialogView.findViewById(R.id.id_form_mensaje);
        final LinearLayout ly = (LinearLayout) dialogView.findViewById(R.id.id_form_ly_mensaje);
        ly.setVisibility(View.VISIBLE);
        mensaje.setText("Desea Restaurar la Manzana?");
        alert.setTitle("Nro Manzana: "+codMzna+""+sufMzna);
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
                            /*Confirmada*/
                            updateManzanaCaptura(codZona,sufZona,codMzna,sufMzna,null, 0,0);
                            removeFeature(feature);
                            loadOnlyManzanaModificada(codZona,sufZona,codMzna,sufMzna,0);
                            Toast.makeText(getActivity().getApplicationContext(), "Manzana Restaurada", Toast.LENGTH_SHORT).show();
                        }
                        if (estado.equals("3")) {
                            /*Fusionada*/
                            final int size = getListaManzanaCapturaXMznabelong(codZona,sufZona,codMzna+""+sufMzna).size();
                            Log.i("mensaje",""+size);
                            final ArrayList<ManzanaCaptura> lista = getListaManzanaCapturaXMznabelong(codZona,sufZona,codMzna+""+sufMzna);
                            for (int i = 0; i < size; i++) {
                                Log.i("mensaje",""+i+":"+lista.get(i).getCodzona()+"/"+lista.get(i).getSufzona()+"/"+lista.get(i).getCodmzna()+"/"+lista.get(i).getSufmzna());
                                updateManzanaCaptura(lista.get(i).getCodzona(),lista.get(i).getSufzona(),lista.get(i).getCodmzna(),lista.get(i).getSufmzna(), null, 0,0);
                            }
                            removeFeature(feature);
                            deleteManzanaCaptura(codZona,sufZona,codMzna,sufMzna);
                        }
                        if (estado.equals("5")) {
                            /*Replanteada*/
                            updateManzanaCaptura(codZona,sufZona,codMzna,sufMzna,getObjectManzanaCapturaXMzna(codZona,sufZona,codMzna,sufMzna).getMznabelong(), 0,0);
                            removeFeature(feature);
                            deleteManzanaCaptura(codZona,sufZona,codMzna,sufMzna);
                        }
                        if (estado.equals("6")) {
                            updateManzanaCaptura(codZona,sufZona,codMzna,sufMzna, "", 0,0);
                            removeFeature(feature);
                            loadOnlyManzanaModificada(codZona,sufZona,codMzna,sufMzna,0);
                        }
                        if (estado.equals("8")) {
                            //Fraccionada
                            final int size = getListaManzanaCapturaXMznabelong(codZona,sufZona,codMzna+""+sufMzna).size();
                            Log.i("mensaje",""+size);
                            final ArrayList<ManzanaCaptura> lista = getListaManzanaCapturaXMznabelong(codZona,sufZona,codMzna+""+sufMzna);
                            for (int i = 0; i < size; i++) {
                                Log.i("mensaje",""+i+":"+lista.get(i).getCodzona()+"/"+lista.get(i).getSufzona()+"/"+lista.get(i).getCodmzna()+"/"+lista.get(i).getSufmzna());
                                deleteManzanaCaptura(lista.get(i).getCodzona(),lista.get(i).getSufzona(),lista.get(i).getCodmzna(),lista.get(i).getSufmzna());
                            }
                            //removeFeature(feature);
                            updateManzanaCaptura(codZona,sufZona,codMzna,sufMzna,null,0,0);
                        }
                        if (estado.equals("9")) {
                            updateManzanaCaptura(codZona,sufZona,codMzna,sufMzna,getManzanaCapturaXMznabelong(codMzna+""+sufMzna).getCodmzna(),  0,0);
                            removeFeature(feature);
                            deleteManzanaCaptura(codZona,sufZona,codMzna,sufMzna);
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
    public void visualizeValidacionManzana(final String codZona,final String sufZona,final String codMzna,final String sufMzna) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), R.style.ThemeOverlay_MaterialComponents_Dialog);
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.layout_form_poligono, null);
        final TextView mensaje = (TextView) dialogView.findViewById(R.id.id_form_mensaje);
        final LinearLayout ly = (LinearLayout) dialogView.findViewById(R.id.id_form_ly_mensaje);
        ly.setVisibility(View.VISIBLE);
        mensaje.setText("Confirme o Modifique manzana");
        alert.setTitle("Nro Manzana: " + codMzna+""+sufMzna);
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
                        updateManzanaCaptura(codZona,sufZona,codMzna,sufMzna, null, 2,1);
                        loadOnlyManzanaModificada(codZona,sufZona,codMzna,sufMzna, 2);
                    }
                });
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        visualizeModificarManzana(codZona,sufZona,codMzna,sufMzna);

                    }
                });
            }
        });
        alertDialog.show();
    }

    /*******MODIFICAR**********/

    /* VISUALIZAR DIALOGO DE MODIFICACION DE MANZANA (FUSION,REPLANTEAR,ETC)*/
    public void visualizeModificarManzana(final String codZona,final String sufZona, final String codMzna,final String sufMzna) {
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

        alert.setTitle("Nro Manzana: "+codMzna+""+sufMzna);
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
                                accionManzana = 3;
                                estadoDibujado = 1;
                                listaManzanasSeleccionas.clear();
                                listaManzanasSeleccionas.add(new FusionItem(1, codZona+""+sufijoZona,codMzna+""+sufMzna));
                                OpenDialogFusion(codMzna, listaManzanasSeleccionas);
                                break;
                            case 1://Fraccionar
                                accionManzana = 4;
                                estadoDibujado = 1;
                                idManzanaSeleccionada = codMzna+""+sufMzna;
                                cantidad = Integer.parseInt(String.valueOf(numero.getText()));
                                createPolygon();
                                setEstadoEdicion();
                                loadPolygonEdicionManzana(codZona,sufZona,codMzna,sufMzna);
                                fab2.setImageResource(R.drawable.ic_action_tick_18_white);
                                break;
                            case 2://Replantear
                                accionManzana = 5;
                                estadoDibujado = 1;
                                idManzanaSeleccionada = codMzna+""+sufMzna;
                                createPolygon();
                                setEstadoEdicion();
                                loadPolygonEdicionManzana(codZona,sufZona,codMzna,sufMzna);
                                break;
                            case 3:
                                visualizeEliminacionManzana(codZona,sufZona,codMzna,sufMzna);
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
        dialogo.show(getFragmentManager(), DialogFusionManzana.TAG);
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
                        listaManzanasSeleccionas.add(new FusionItem(estado, zona, idmanzana));
                        OpenDialogFusion(idmanzana, listaManzanasSeleccionas);
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
        newIdManzana = getNewManzanaFusion(listaManzana, listaManzana.get(0).getIdManzana());
        listaManzanasSeleccionas = listaManzana;
        //Cancelar
        if(estadoEdicion==0){
            listaManzanasSeleccionas.clear();
            accionManzana=0;
        }
        //Dibujar
        if(estadoEdicion==1){
            estadoDibujado = 1;
            setEstadoEdicion();
            loadPolygonsEdicionManzanas();
            createPolygon();/*fuera*/
        }
    }

    /*4. INSERTAR MANZANA_CAPTURA A SQLITE INTERNO*/
    public void saveManzanaCapturaFusion() {
        ArrayList<LatLng> listPointsDibujados = new ArrayList<>();
        if (listPoints.size() > 2) {
            if (listPoints.size() == 3) {
                listPointsDibujados = listPoints;
                LatLng dato = listPointsDibujados.get(0);
                listPointsDibujados.add(dato);
            } else {
                listPointsDibujados = listPoints;
            }
            insertManzanaCaptura(getDigitos(newIdManzana),getUltimoDigito(newIdManzana),"",3,listPointsDibujados);
            for (int i = 0; i < listaManzanasSeleccionas.size(); i++) {
                updateManzanaCaptura(codigoZona,sufijoZona,getDigitos(listaManzanasSeleccionas.get(i).getIdManzana()),getUltimoDigito(listaManzanasSeleccionas.get(i).getIdManzana()), newIdManzana.trim(), 7,1);
            }
            cleanPolygon();
            removeLayer();
            createLayerGeojsonMain();
            loadFeatureAllManzanas();
            cleanPolygon();
            setEventoEstado();
            accionManzana=0;
            estadoDibujado = 0;
            Toast.makeText(getContext(), "Se registro Manzana correctamente!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Dibuje una Manzana", Toast.LENGTH_SHORT).show();
        }
    }

    /*******FRACCIONAR**********/

    /*1. INSERTAR MANZANAS FRACCIONADAS*/
    @SuppressLint("RestrictedApi")
    public void saveManzanaCapturaFraccionada(){
        ArrayList<LatLng> listPointsDibujados = new ArrayList<>();
        ArrayList<String> listaManzanasNuevas = generateNewIdManzana(cantidad,idManzanaSeleccionada);
        String idManzanaNueva="";
        final int CANTIDAD= cantidad;
            if (listPoints.size() == 3) {
                listPointsDibujados = listPoints;
                LatLng dato = listPointsDibujados.get(0);
                listPointsDibujados.add(dato);
            } else {
                listPointsDibujados = listPoints;
            }
            if (contadorManzana < CANTIDAD) {

                if(listPointsDibujados.size()>0) {
                    idManzanaNueva = listaManzanasNuevas.get(contadorManzana);
                    listaManzanasMemoria.add(new ManzanaReplanteo(idManzanaNueva, new ArrayList<LatLng>(listPointsDibujados)));
                    cleanListMarker();
                    createPolygon();
                    if (contadorManzana == (CANTIDAD - 1)) {
                        poligon.remove();
                        cleanListMarker();
                        setEstadoGuardado();
                    }
                    contadorManzana++;
                }
                else{
                    Toast.makeText(getContext(), "Dibuje un poligono", Toast.LENGTH_SHORT).show();
                }
            } else {
                updateManzanaCaptura(codigoZona,sufijoZona,getDigitos(idManzanaSeleccionada),getUltimoDigito(idManzanaSeleccionada), "", 8,1);
                for (int i = 0; i < listaManzanasMemoria.size(); i++) {
                    ArrayList<LatLng> listafinal = new ArrayList<>();
                    for (int x = 0; x < listaManzanasMemoria.get(i).getLista().size(); x++) {
                        listafinal.add(listaManzanasMemoria.get(i).getLista().get(x));
                    }
                    insertManzanaCaptura(getDigitos(listaManzanasMemoria.get(i).getIdManzana()),getUltimoDigito(listaManzanasMemoria.get(i).getIdManzana()), idManzanaSeleccionada, 4, listafinal);
                    Toast.makeText(getContext(), "Se registro Manzana correctamente!", Toast.LENGTH_SHORT).show();
                }
                listaManzanasMemoria.clear();
                contadorManzana=0;
                cleanMapa();
                cleanPolygon();
                removeLayer();
                createLayerGeojsonMain();
                loadFeatureAllManzanas();
                setEventoEstado();
                accionManzana = 0;
                estadoDibujado = 0;
            }

    }

    /*2. VISUALIZAR DIALOGO DE INFORMACION DE MANZANA*/
    public  void visualizeMensajeManzana(final String codZona,final String codMzna,final String sufMzna){
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(),R.style.ThemeOverlay_MaterialComponents_Dialog);
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.layout_form_poligono, null);
        final TextView mensaje = (TextView) dialogView.findViewById(R.id.id_form_mensaje);
        final LinearLayout ly = (LinearLayout) dialogView.findViewById(R.id.id_form_ly_mensaje);
        ly.setVisibility(View.VISIBLE);
        mensaje.setText("Manzana Franccionada:"+codMzna+""+sufMzna);
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
        ArrayList<LatLng> listPointsDibujados = new ArrayList<>();
        if (listPoints.size() > 2) {
            if (listPoints.size() == 3) {
                listPointsDibujados = listPoints;
                LatLng dato = listPointsDibujados.get(0);
                listPointsDibujados.add(dato);
            } else {
                listPointsDibujados = listPoints;
            }
            String idManzanaNueva = generateNewIdManzana(1, idManzanaSeleccionada).get(0);
            updateManzanaCaptura(codigoZona,sufijoZona,idManzanaSeleccionada," ", "", 9,1);
            insertManzanaCaptura(getDigitos(idManzanaNueva),getUltimoDigito(idManzanaNueva), idManzanaSeleccionada, 5, listPointsDibujados);
            cleanPolygon();
            removeLayer();
            createLayerGeojsonMain();
            loadFeatureAllManzanas();
            setEventoEstado();
            accionManzana = 0;
            estadoDibujado = 0;
            Toast.makeText(getContext(), "Se registro Manzana correctamente!", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getContext(), "Dibuje una Manzana", Toast.LENGTH_SHORT).show();
        }

    }

    /*******ELIMINAR**********/
    /* VISUALIZAR DIALOGO ELIMNINAR MANZANA*/
    public void visualizeEliminacionManzana(final String codZona,final String sufZona,final String codMzna,final String sufMzna) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), R.style.ThemeOverlay_MaterialComponents_Dialog);
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.layout_form_poligono, null);
        final TextView mensaje = (TextView) dialogView.findViewById(R.id.id_form_mensaje);
        final LinearLayout ly = (LinearLayout) dialogView.findViewById(R.id.id_form_ly_mensaje);
        ly.setVisibility(View.VISIBLE);
        mensaje.setText("Desea Eliminar Manzana?");
        alert.setTitle("Nro Manzana: " +codMzna+""+sufMzna);
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
                        updateManzanaCaptura(codZona,sufZona,codMzna,sufMzna, "", 6,1);
                        loadOnlyManzanaModificada(codZona,sufZona,codMzna,sufMzna, 6);
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
    public void insertManzanaCaptura(String codMzna,String sufMzna,String mznaBelong,int estado,ArrayList<LatLng> puntos) {
                try {
                    data = new Data(context);
                    data.open();
                    data.insertManzanaCaptura("1", 2, "15", "01", "13", "001", "00", codMzna, sufMzna, mznaBelong, estado, 0, 2,"GeomFromText('POLYGON((" + formatGeom(puntos) + "))',4326)");
                } catch (IOException e) {
                    e.printStackTrace();
                }

    }

    public void updateManzanaCaptura(String codZona,String sufZona,String codMzna,String sufMzna,String mznaBelog,int estado,int cargado) {
        try {
            data = new Data(context);
            data.open();
            data.updateManzanaCaptura(codZona,sufZona,codMzna,sufMzna,mznaBelog,estado,cargado);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*2. ELIMINAR MANZANA_CAPTURA EN SQLITE INTERNO*/
    public void deleteManzanaCaptura(String codZona,String sufZona,String codMzna,String sufMzna) {
        try {
            data = new Data(context);
            data.open();
            data.deleteManzanaCaptura(codZona,sufZona,codMzna,sufMzna);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*******METODOS DE EDICION DE POLIGONOS Y MAPA***********/

    @SuppressLint("RestrictedApi")
    public void setEstadoEdicion() {
        fab2.setVisibility(View.VISIBLE);
        fab4.setVisibility(View.VISIBLE);
        fab5.setVisibility(View.VISIBLE);
        removeLayer();
        createLayerGeojsonMain();
    }


    /*ASIGNAR EDICION*/
    @SuppressLint("RestrictedApi")
    public void setEstadoGuardado() {
        fab2.setImageResource(R.drawable.ic_action_save);
        fab2.setBackgroundColor(Color.MAGENTA);
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
        listaManzanasSeleccionas.clear();
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
    }
    /*LIMPIAR MAPA*/
    public void cleanMapa() {
        mgoogleMap.clear();
    }

    /*************METODOS DE FLUJOS*************************/

    /*CREACION DE NUEVO ID DE MANZANA*/
    public String getNewManzanaFusion(ArrayList<FusionItem> listaManzana,String idManzana) {
        int menor;
        int valorid;
        int newValorId = 0;
        if (listaManzana.size() > 0) {
            if(listaManzana.get(0).getIdManzana().trim().length()==4){

                menor = Integer.parseInt(getDigitos(listaManzana.get(0).getIdManzana().trim()));
            }
            else {
                menor = Integer.parseInt(listaManzana.get(0).getIdManzana().trim());
            }
            for (FusionItem objeto : listaManzana) {
                    int numero = Integer.parseInt(cleanCadena(objeto.getIdManzana().trim()));
                    if (numero < menor) {
                        menor = numero;
                    }
            }
            valorid = Integer.parseInt(cleanCadena(idManzana.trim()));
            if (menor < valorid) {
                newValorId = menor;
            } else {
                newValorId = valorid;
            }
        }
        return setDigitos(newValorId) + ""+getLetra(listaManzana,setDigitos(newValorId));
    }

    /*ASIGNAR DIGITOS*/
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

    /*LIMPIAR CADENA*/
    public static String cleanCadena(String cadena){
        String resultado="";
        if (cadena.length()>3)
        {resultado=getDigitos(cadena);}
        else{
            resultado=cadena;
        }

        return resultado;
    }
    /*OBTENER CADENA SIN ULTIMO DIGITO*/
    public static String getDigitos(String cadena){
        String ultimo = cadena.substring(0, cadena.length() - 1);
        return  ultimo;
    }
    /*OBTENER ULTIMO DIGITO*/
    public static String getUltimoDigito(String cadena){
        String ultimo = cadena.substring(cadena.length() - 1);
        return  ultimo;
    }

    public static String getLetra(ArrayList<FusionItem> listaManzana,String idmanzana){
        String resultado1 ="";
        String resultado2 ="";
        for(FusionItem objeto :listaManzana)
        {
            if(cleanCadena(objeto.getIdManzana().trim()).equals(idmanzana))
            {resultado1=objeto.getIdManzana().trim();}

        }

        if(resultado1.length()>3)
        {
            char ultimocaracter = resultado1.charAt(resultado1.length()-1);
            int dato1 = castCaracter(ultimocaracter);
            char dato2 = castNumero(dato1+1);

            resultado2 = ""+dato2;
        }
        else{
            resultado2="A";
        }
        return resultado2;
    }

    public static char castNumero(int numero){
        char valor = (char) numero;
        return valor;
    }

    public static int castCaracter(char caracter){
        int numero = (int) caracter;
        return numero;
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

    public ManzanaCaptura getObjectManzanaCapturaXZonaMzna(String codZona,String sufZona,String codMzna,String sufMzna) {
        ManzanaCaptura manzanaCaptura = null;
        try {
            Data data = new Data(context);
            data.open();
            manzanaCaptura = data.getManzanaCapturaXZonaMzna(codZona,sufZona,codMzna,sufMzna);
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

    public ArrayList<ManzanaCaptura> getListaManzanaCapturaXZona() {
        ArrayList<ManzanaCaptura> listaManzana = new ArrayList<>();
        try {
            Data data = new Data(context);
            data.open();
            listaManzana = data.getAllManzanaCapturaXZona(codigoZona);
            data.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listaManzana;
    }

    /*METODO OBTENER ARRAYLIST DE OBJETO MANZANA CAPTURA POR MZNABELONG*/
    public ArrayList<ManzanaCaptura> getListaManzanaCapturaXMznabelong(String codZona,String sufZona,String mznaBelong) {
        ArrayList<ManzanaCaptura> listaManzana = new ArrayList<>();
        try {
            Data data = new Data(context);
            data.open();
            listaManzana = data.getAllManzanaCapturaXMznabelong(codZona,sufZona,mznaBelong);
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

    /*GENERAR NUEVO ID DE MANZANA*/
    public static ArrayList<String> generateNewIdManzana(int cantidad, String idmanzana){
        ArrayList<String> lista = new ArrayList<>();
        char valor='0';
        char letraObtenida='0';
        int  letraCastInt;
        int a = 65;
        String codmzna;

        if(idmanzana.trim().length()==4){
            letraObtenida = getUltimoDigito(idmanzana).charAt(0);
            letraCastInt = (int) letraObtenida;
            a = letraCastInt+1;
            codmzna = getDigitos(idmanzana).trim();
        }
        else{
            a = 65;
            codmzna=idmanzana.trim();
        }


        for (int i=0;i<cantidad;i++)
        {

            valor = (char) a;
            lista.add(codmzna+String.valueOf(valor));
            a++;
        }
        return lista;
    }

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
