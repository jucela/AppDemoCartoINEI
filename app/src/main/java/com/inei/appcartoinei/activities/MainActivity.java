package com.inei.appcartoinei.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.inei.appcartoinei.NumericKeyBoardTransformationMethod;
import com.inei.appcartoinei.R;
import com.inei.appcartoinei.adapters.ExpandListAdapter;
import com.inei.appcartoinei.fragments.Capa1;
import com.inei.appcartoinei.fragments.Capa2;
import com.inei.appcartoinei.fragments.Capa3;
import com.inei.appcartoinei.fragments.ListPoligonoFragment;
import com.inei.appcartoinei.fragments.MapCapas;
import com.inei.appcartoinei.fragments.MapManzanaFragment;
import com.inei.appcartoinei.modelo.DAO.Data;
import com.inei.appcartoinei.modelo.DAO.DataBaseHelper;
import com.inei.appcartoinei.modelo.pojos.Capa;

import org.spatialite.database.SQLiteDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity  {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Button boton;


    private ArrayList<String> listDataHeader;
    private ExpandableListView expListView;
    private HashMap<String, List<String>> listDataChild;
    private ExpandListAdapter listAdapter;
    private MenuItem menuItems;

    private SQLiteDatabase db ;
    private DataBaseHelper op;

    Data    data;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout =(DrawerLayout)findViewById(R.id.drawer);
        createDB();


        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_drawer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupDrawerContent(navigationView);

        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        ImageView img_add_capa = (ImageView) headerView.findViewById(R.id.img_add_capa);
        ImageView img_delete_capa = (ImageView) headerView.findViewById(R.id.img_delete_capa);
        ImageView img_list_capa = (ImageView) headerView.findViewById(R.id.img_list_capa);
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        expListView = (ExpandableListView) findViewById(R.id.expandable_principal1);
        listAdapter = new ExpandListAdapter(this,cargarListDataHeader(),cargarListDataChild(),expListView);
        expListView.setAdapter(listAdapter);
        enableExpandableList();




        img_add_capa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this,R.style.ThemeOverlay_MaterialComponents_Dialog);
                final View dialogView = MainActivity.this.getLayoutInflater().inflate(R.layout.layout_form_capa, null);
                final EditText id       = (EditText) dialogView.findViewById(R.id.id_edtId);
                final EditText nombre       = (EditText) dialogView.findViewById(R.id.id_edtNombre);
                final EditText descripcion  = (EditText) dialogView.findViewById(R.id.id_edtDescripcion);
                final EditText tipo         = (EditText) dialogView.findViewById(R.id.id_edtTipo);
                final EditText srid         = (EditText) dialogView.findViewById(R.id.id_edtSRID);
                final EditText escalamin    = (EditText) dialogView.findViewById(R.id.id_edtEscalaMin);
                final EditText escalamax    = (EditText) dialogView.findViewById(R.id.id_edtEscalaMax);
                final EditText escalamineti = (EditText) dialogView.findViewById(R.id.id_edtEscalaMinEti);
                final EditText escalamaxeti = (EditText) dialogView.findViewById(R.id.id_edtEscalaMaxEti);

                alert.setTitle("Crear Capa Vectorial");
                alert.setIcon(R.drawable.ic_layers_36);
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
                                try {
                                    data = new Data(context);
                                    data.open();
                                    data.insertarCapa(new Capa(Integer.parseInt(id.getText().toString()),
                                            nombre.getText().toString(),
                                            descripcion.getText().toString(),
                                            tipo.getText().toString(),
                                            Integer.parseInt(srid.getText().toString()),
                                            Integer.parseInt(escalamin.getText().toString()),
                                            Integer.parseInt(escalamax.getText().toString()),
                                            Integer.parseInt(escalamineti.getText().toString()),
                                            Integer.parseInt(escalamaxeti.getText().toString())));
                                    listAdapter.notifyDataSetChanged();
                                    data.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                alertDialog.dismiss();
                            }
                        });
                    }
                });
                alertDialog.show();
            }
        });

        img_delete_capa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    data = new Data(context);
                    data.open();
                    data.deletePoligono();
                    data.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        img_list_capa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListPoligonoFragment newFragment = new ListPoligonoFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contedor_fragments,newFragment).commit();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        MapCapas newFragment = new MapCapas();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contedor_fragments,newFragment).commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void selectItemDrawer(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.capa1:
                viewFragment();
                break;
            case R.id.capa2:
                formVivienda();
                break;
            case R.id.capa3:
                formEjeVial();
                break;
            case R.id.capa4:
                formNewCapa();
                break;
            default:

        }
    }

    private void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                selectItemDrawer(menuItem);
                return true;
            }
        });
    }


    private void enableExpandableList() {

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
//                Fragment fragment = null;
//                Class fragmentClass=null;
//                switch (groupPosition) {
//                    case 0:
//                        fragmentClass = Capa1.class;
//                        Toast.makeText(MainActivity.this, "posicion 01", Toast.LENGTH_LONG).show();
//                        break;
//                    case 1:
//                        fragmentClass = Capa2.class;
//                        Toast.makeText(MainActivity.this, "posicion 02", Toast.LENGTH_LONG).show();
//                        break;
//                    case 2:
//                        fragmentClass = Capa3.class;
//                        Toast.makeText(MainActivity.this, "posicion 03", Toast.LENGTH_LONG).show();
//                        break;
//                    case 3:
//                        fragmentClass = Capa3.class;
//                        Toast.makeText(MainActivity.this, "posicion 04", Toast.LENGTH_LONG).show();
//                        break;
//                    default:
//                        fragmentClass = Capa1.class;
//                }
//                try {
//                    fragment = (Fragment)fragmentClass.newInstance();
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                } catch (InstantiationException e) {
//                    e.printStackTrace();
//                }
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.contedor_fragments,fragment).commit();
//                menuItems.setChecked(true);
 //               setTitle(menuItems.getTitle());
//                drawerLayout.closeDrawers();
//                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
//                drawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });
//        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//            @Override
//            public boolean onChildClick(ExpandableListView parent, View v, final int groupPosition, final int childPosition, long id) {
//                Fragment fragment = null;
//                Class fragmentClass=null;
//                if(groupPosition>=0 && childPosition==0)
//                {
//                    fragmentClass = MapCapas.class;
//                    try {
//                        fragment = (Fragment)fragmentClass.newInstance();
//                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                    } catch (InstantiationException e) {
//                        e.printStackTrace();
//                    }
//                    FragmentManager fragmentManager = getSupportFragmentManager();
//                    fragmentManager.beginTransaction().replace(R.id.contedor_fragments,fragment).commit();
//                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
//                    drawer.closeDrawer(GravityCompat.START);
//
//                }
//                return false;
//            }
//        });


        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, final int groupPosition, final int childPosition, long id) {
                  Toast.makeText(MainActivity.this,""+obtenerListDataHeader2().get(groupPosition),Toast.LENGTH_SHORT).show();

                if(groupPosition>=0 && childPosition==0)
                {
                    Bundle args = new Bundle();
                    args.putString("idCapa",""+obtenerListDataHeader2().get(groupPosition));
                    MapCapas newFragment = new MapCapas();
                    newFragment.setArguments(args);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.contedor_fragments,newFragment).commit();
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
                    drawer.closeDrawer(GravityCompat.START);
                }
                return false;
            }
        });
    }


    private void prepareListData(List<String> listDataHeader, Map<String, List<String>> listDataChild) {
        ArrayList<String> items = new ArrayList<>();
        items.add("Juxe");
        items.add("Jorge");
        items.add("Luis");
        items.add("Pedro");

        List<String> subItems = new ArrayList<String>();
        subItems.add(0,"Agregar");
        subItems.add(1,"Editar");
        subItems.add(2,"Eliminar");

        for(int i=0;i<obtenerAllCapa().size();i++){
            listDataHeader.add(i,obtenerAllCapa().get(i).getNombre());
        }

        for(int i=0;i<obtenerAllCapa().size();i++){
            listDataChild.put(listDataHeader.get(i),subItems);
        }



//        listDataHeader.add(0,"Capa 01");
//        listDataHeader.add(1,"Capa 02");
//        listDataHeader.add(2,"Capa 03");
//        listDataHeader.add(3,"Capa 04");
//
//         //Adding child data
//        List<String> grupo1 = new ArrayList<String>();
//        grupo1.add(0,"Agregar");
//        grupo1.add(1,"Editar");
//        grupo1.add(2,"Eliminar");
//
//        List<String> grupo2 = new ArrayList<String>();
//        grupo2.add(0,"Agregar");
//        grupo2.add(1,"Editar");
//        grupo2.add(2,"Eliminar");
//
//
//        List<String> grupo3 = new ArrayList<String>();
//        grupo3.add(0,"Agregar");
//        grupo3.add(1,"Editar");
//        grupo3.add(2,"Eliminar");
//
//        List<String> grupo4 = new ArrayList<String>();
//        grupo4.add(0,"Agregar");
//        grupo4.add(1,"Editar");
//        grupo4.add(2,"Eliminar");
//
//
//        listDataChild.put(listDataHeader.get(0),subItems);// Header, Child data
//        listDataChild.put(listDataHeader.get(1),subItems);
//        listDataChild.put(listDataHeader.get(2),subItems);
//        listDataChild.put(listDataHeader.get(3),subItems);
    }

    private void createDB(){
        op = new DataBaseHelper(this);
        db = op.getWritableDatabase();
    }

    public ArrayList<Capa> obtenerAllCapa()
    { ArrayList<Capa> capas = new ArrayList<>();
        try {
            Data data = new Data(context);
            data.open();
            capas = data.getAllCapa();
            data.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return capas;
    }

    public ArrayList<String> obtenerListDataHeader() {
       ArrayList<String> header = new ArrayList<>();
        for (int i = 0; i < obtenerAllCapa().size(); i++) {
            header.add(i, obtenerAllCapa().get(i).getNombre());
        }
        return header;
    }

    public ArrayList<Integer> obtenerListDataHeader2() {
        ArrayList<Integer> header = new ArrayList<>();
        for (int i = 0; i < obtenerAllCapa().size(); i++) {
            header.add(i, obtenerAllCapa().get(i).getId());
        }
        return header;
    }

    public HashMap<String, List<String>> obtenerListDataChild(){
        HashMap<String, List<String>> child = new HashMap<String, List<String>>();
        ArrayList<String> subItems = new ArrayList<>();
        subItems.add(0,"Agregar");
        subItems.add(1,"Editar");
        subItems.add(2,"Eliminar");


        for(int i=0;i<obtenerAllCapa().size();i++){
            child.put(obtenerListDataHeader().get(i),subItems);
        }
        return child;
    }


    //Arraylist de Cabezera
    public ArrayList<String> cargarListDataHeader() {
        ArrayList<String> header = new ArrayList<>();
        header.add(0,"Capa 1");
        header.add(1,"Capa 2");
        return header;
    }
    //ArrayList de Item
    public HashMap<String, List<String>> cargarListDataChild(){
        HashMap<String, List<String>> child = new HashMap<String, List<String>>();
        List<String> grupo1 = new ArrayList<String>();
        grupo1.add(0,"Agregar");
        grupo1.add(1,"Editar");
        List<String> grupo2 = new ArrayList<String>();


        child.put(cargarListDataHeader().get(0),grupo1);// Header, Child data
        child.put(cargarListDataHeader().get(1),grupo1);

        return child;
    }

    public  void formManzana(){
        ArrayList<String> zonas = new ArrayList<>();
        zonas.add("00100");
        zonas.add("00200");
        zonas.add("00300");
        zonas.add("00400");
        zonas.add("00500");
        ArrayList<String> departamentos = new ArrayList<>();
        departamentos.add("Lima");
        departamentos.add("Amazonas");
        departamentos.add("Lambayeque");
        departamentos.add("La Libertad");
        departamentos.add("Amazonas");
        ArrayList<String> provincias = new ArrayList<>();
        provincias.add("Lima");
        provincias.add("Yauyos");
        provincias.add("Huarochiri");
        provincias.add("Cañete");
        ArrayList<String> distritos = new ArrayList<>();
        distritos.add("Ate");
        distritos.add("Jesús Maria");
        distritos.add("La victoria");
        distritos.add("San Juan de Miraflores");
        distritos.add("Villa el Salvador");
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this,R.style.ThemeOverlay_MaterialComponents_Dialog);
        final View dialogView = MainActivity.this.getLayoutInflater().inflate(R.layout.layout_form_manzana, null);
        final EditText nombre =       (EditText) dialogView.findViewById(R.id.id_edtManzanaNombre);
        final Spinner  zona =         (Spinner) dialogView.findViewById(R.id.id_edtManzanaZona);
        final Spinner  departamento = (Spinner) dialogView.findViewById(R.id.id_edtManzanaDepartamento);
        final Spinner  provincia =    (Spinner) dialogView.findViewById(R.id.id_edtManzanaProvincia);
        final Spinner  distrito =     (Spinner) dialogView.findViewById(R.id.id_edtManzanaDistrito);
        ArrayAdapter<String> adapterZona         = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,zonas);
        ArrayAdapter<String> adapterDepartamento = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,departamentos);
        ArrayAdapter<String> adapterProvincia    = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,provincias);
        ArrayAdapter<String> adapterDistrito     = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,distritos);
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
                        // TODO Do something
                        try {
                            data = new Data(context);
                            data.open();
                            //data.insertarCapa();
                            listAdapter.notifyDataSetChanged();
                            data.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        alertDialog.dismiss();
                    }
                });
            }
        });
        alertDialog.show();
    }

    public  void formVivienda(){
        ArrayList<String> manzanas = new ArrayList<>();
        manzanas.add("08010200200003A");
        manzanas.add("08010200200003B");
        ArrayList<String> zonas = new ArrayList<>();
        zonas.add("00100");
        zonas.add("00200");
        zonas.add("00300");
        zonas.add("00400");
        zonas.add("00500");
        ArrayList<String> departamentos = new ArrayList<>();
        departamentos.add("Lima");
        departamentos.add("Amazonas");
        departamentos.add("Lambayeque");
        departamentos.add("La Libertad");
        departamentos.add("Amazonas");
        ArrayList<String> provincias = new ArrayList<>();
        provincias.add("Lima");
        provincias.add("Yauyos");
        provincias.add("Huarochiri");
        provincias.add("Cañete");
        ArrayList<String> distritos = new ArrayList<>();
        distritos.add("Ate");
        distritos.add("Jesús Maria");
        distritos.add("La victoria");
        distritos.add("San Juan de Miraflores");
        distritos.add("Villa el Salvador");
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this,R.style.ThemeOverlay_MaterialComponents_Dialog);
        final View dialogView = MainActivity.this.getLayoutInflater().inflate(R.layout.layout_form_vivienda, null);
        final Spinner manzana =       (Spinner) dialogView.findViewById(R.id.id_edtViviendaManzana);
        final EditText descripcion =  (EditText) dialogView.findViewById(R.id.id_edtviviendaDescripcion);
        final Spinner  zona =         (Spinner) dialogView.findViewById(R.id.id_edtViviendaZona);
        final EditText frente =       (EditText) dialogView.findViewById(R.id.id_edtViviendaFrente);
        final EditText puerta =       (EditText) dialogView.findViewById(R.id.id_edtViviendaPuerta);
        final Spinner  departamento = (Spinner) dialogView.findViewById(R.id.id_edtViviendaDepartamento);
        final Spinner  provincia =    (Spinner) dialogView.findViewById(R.id.id_edtViviendaProvincia);
        final Spinner  distrito =     (Spinner) dialogView.findViewById(R.id.id_edtViviendaDistrito);
        ArrayAdapter<String> adapterManzana         = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,manzanas);
        ArrayAdapter<String> adapterZona         = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,zonas);
        ArrayAdapter<String> adapterDepartamento = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,departamentos);
        ArrayAdapter<String> adapterProvincia    = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,provincias);
        ArrayAdapter<String> adapterDistrito     = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,distritos);
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
                        // TODO Do something
                        try {
                            data = new Data(context);
                            data.open();
                            //data.insertarCapa();
                            listAdapter.notifyDataSetChanged();
                            data.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        alertDialog.dismiss();
                    }
                });
            }
        });
        alertDialog.show();
    }

    public  void formEjeVial(){

        ArrayList<String> categorias = new ArrayList<>();
        categorias.add("1. AV");
        categorias.add("2. CAL");
        categorias.add("3. JR");
        ArrayList<String> departamentos = new ArrayList<>();
        departamentos.add("Lima");
        departamentos.add("Amazonas");
        departamentos.add("Lambayeque");
        departamentos.add("La Libertad");
        departamentos.add("Amazonas");
        ArrayList<String> provincias = new ArrayList<>();
        provincias.add("Lima");
        provincias.add("Yauyos");
        provincias.add("Huarochiri");
        provincias.add("Cañete");
        ArrayList<String> distritos = new ArrayList<>();
        distritos.add("Ate");
        distritos.add("Jesús Maria");
        distritos.add("La victoria");
        distritos.add("San Juan de Miraflores");
        distritos.add("Villa el Salvador");
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this,R.style.ThemeOverlay_MaterialComponents_Dialog);
        final View dialogView = MainActivity.this.getLayoutInflater().inflate(R.layout.layout_form_ejevial, null);
        final Spinner categoria =       (Spinner) dialogView.findViewById(R.id.id_edtEjeCategoria);
        final EditText nombre =       (EditText) dialogView.findViewById(R.id.id_edtEjeNombre);
        final EditText nombrealt =       (EditText) dialogView.findViewById(R.id.id_edtEjeNombreAlt);
        final Spinner  departamento = (Spinner) dialogView.findViewById(R.id.id_edtEjeDepartamento);
        final Spinner  provincia =    (Spinner) dialogView.findViewById(R.id.id_edtEjeProvincia);
        final Spinner  distrito =     (Spinner) dialogView.findViewById(R.id.id_edtEjeDistrito);
        ArrayAdapter<String> adapterCategoria       = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,categorias);
        ArrayAdapter<String> adapterDepartamento = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,departamentos);
        ArrayAdapter<String> adapterProvincia    = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,provincias);
        ArrayAdapter<String> adapterDistrito     = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,distritos);
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
                        // TODO Do something
                        try {
                            data = new Data(context);
                            data.open();
                            //data.insertarCapa();
                            listAdapter.notifyDataSetChanged();
                            data.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        alertDialog.dismiss();
                    }
                });
            }
        });
        alertDialog.show();
    }

    public  void formNewCapa(){

        ArrayList<String> tipos = new ArrayList<>();
        tipos.add("Punto");
        tipos.add("Linea");
        tipos.add("Poligono");

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this,R.style.ThemeOverlay_MaterialComponents_Dialog);
        final View dialogView = MainActivity.this.getLayoutInflater().inflate(R.layout.layout_form_agregar_capa, null);
        final EditText nombre =       (EditText) dialogView.findViewById(R.id.id_edtCapaNombre);
        final EditText descripcion =  (EditText) dialogView.findViewById(R.id.id_edtCapaDescripcion);
        final Spinner  tipo =         (Spinner) dialogView.findViewById(R.id.id_edtCapaTipo);
        ArrayAdapter<String> adapterTipo       = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,tipos);
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        tipo.setAdapter(adapterTipo);
        alert.setTitle("Agregar Capa");
        alert.setIcon(R.drawable.ic_layers_36);
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
                        try {
                            data = new Data(context);
                            data.open();
                            //data.insertarCapa();
                            listAdapter.notifyDataSetChanged();
                            data.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        alertDialog.dismiss();
                    }
                });
            }
        });
        alertDialog.show();
    }

    public void viewFragment(){
        Bundle args = new Bundle();
        args.putString("idUsuario",""+1);
        MapManzanaFragment newFragment = new MapManzanaFragment();
        newFragment.setArguments(args);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contedor_fragments,newFragment).commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);
    }




}
