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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.inei.appcartoinei.NumericKeyBoardTransformationMethod;
import com.inei.appcartoinei.R;
import com.inei.appcartoinei.adapters.ExpandListAdapter;
import com.inei.appcartoinei.fragments.Capa1;
import com.inei.appcartoinei.fragments.Capa2;
import com.inei.appcartoinei.fragments.Capa3;
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
        //accountButton.setOnClickListener(this);
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        expListView = (ExpandableListView) findViewById(R.id.expandable_principal1);
        //prepareListData(listDataHeader, listDataChild);
        //listAdapter = new ExpandListAdapter(this, listDataHeader, listDataChild);
//        listAdapter.notifyDataSetChanged();
        listAdapter = new ExpandListAdapter(this,obtenerListDataHeader(),obtenerListDataChild(),expListView);
        expListView.setAdapter(listAdapter);
        enableExpandableList();




        img_add_capa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this,R.style.ThemeOverlay_MaterialComponents_Dialog);
                final View dialogView = MainActivity.this.getLayoutInflater().inflate(R.layout.layout_form_capa, null);
                final LinearLayout lytDialog = (LinearLayout) dialogView.findViewById(R.id.dialog_lyt);
                final EditText nombre       = (EditText) dialogView.findViewById(R.id.id_edtNombre);
                final EditText descripcion  = (EditText) dialogView.findViewById(R.id.id_edtDescripcion);
                final EditText tipo         = (EditText) dialogView.findViewById(R.id.id_edtTipo);
                final EditText srid         = (EditText) dialogView.findViewById(R.id.id_edtSRID);
                final EditText escalamin    = (EditText) dialogView.findViewById(R.id.id_edtEscalaMin);
                final EditText escalamax    = (EditText) dialogView.findViewById(R.id.id_edtEscalaMax);
                final EditText escalamineti = (EditText) dialogView.findViewById(R.id.id_edtEscalaMinEti);
                final EditText escalamaxeti = (EditText) dialogView.findViewById(R.id.id_edtEscalaMaxEti);

                //srid.setTransformationMethod(new NumericKeyBoardTransformationMethod());

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
                                    data.insertarDatos(new Capa(1,
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


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void selectItemDrawer(MenuItem menuItem){
        Fragment fragment = null;
        Class fragmentClass;
        switch (menuItem.getItemId()){
            case R.id.capa1:
                fragmentClass = Capa1.class;
                break;
            case R.id.capa2:
                fragmentClass = Capa2.class;
                break;
            case R.id.capa3:
                fragmentClass = Capa3.class;
                break;
            default:
                fragmentClass = Capa1.class;
        }
        try {
            fragment = (Fragment)fragmentClass.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contedor_fragments,fragment).commit();
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        drawerLayout.closeDrawers();

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
                Fragment fragment = null;
                Class fragmentClass=null;
                switch (groupPosition) {
                    case 0:
                        fragmentClass = Capa1.class;
                        Toast.makeText(MainActivity.this, "posicion 01", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        fragmentClass = Capa2.class;
                        Toast.makeText(MainActivity.this, "posicion 02", Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        fragmentClass = Capa3.class;
                        Toast.makeText(MainActivity.this, "posicion 03", Toast.LENGTH_LONG).show();
                        break;
                    case 3:
                        fragmentClass = Capa3.class;
                        Toast.makeText(MainActivity.this, "posicion 04", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        fragmentClass = Capa1.class;
                }
                try {
                    fragment = (Fragment)fragmentClass.newInstance();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contedor_fragments,fragment).commit();
//                menuItems.setChecked(true);
 //               setTitle(menuItems.getTitle());
//                drawerLayout.closeDrawers();
//                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
//                drawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, final int groupPosition, final int childPosition, long id) {

                switch (groupPosition) {
                    case 0:
                        switch (childPosition) {
                            case 0:
                                Toast.makeText(MainActivity.this, "Agregar 1", Toast.LENGTH_LONG).show();
                                break;
                            case 1:
                                Toast.makeText(MainActivity.this, "Editar 1", Toast.LENGTH_LONG).show();
                                break;
                            case 2:
                                Toast.makeText(MainActivity.this, "Eliminar 1", Toast.LENGTH_LONG).show();
                                break;
                        }
                        break;
                    case 1:
                        switch (childPosition) {
                            case 0:
                                Toast.makeText(MainActivity.this, "Agregar 2", Toast.LENGTH_LONG).show();
                                break;
                            case 1:
                                Toast.makeText(MainActivity.this, "Editar 2", Toast.LENGTH_LONG).show();
                                break;
                            case 2:
                                Toast.makeText(MainActivity.this, "Eliminar 2", Toast.LENGTH_LONG).show();
                                break;

                        }
                        break;
                    case 2:
                        switch (childPosition) {
                            case 0:
                                Toast.makeText(MainActivity.this, "Agregar 3", Toast.LENGTH_LONG).show();
                                break;
                            case 1:
                                Toast.makeText(MainActivity.this, "Editar 3", Toast.LENGTH_LONG).show();
                                break;
                            case 2:
                                Toast.makeText(MainActivity.this, "Eliminar 3", Toast.LENGTH_LONG).show();
                                break;

                        }
                        break;
                    case 3:
                        switch (childPosition) {
                            case 0:
                                Toast.makeText(MainActivity.this, "Agregar 4", Toast.LENGTH_LONG).show();
                                break;
                            case 1:
                                Toast.makeText(MainActivity.this, "Editar 4", Toast.LENGTH_LONG).show();
                                break;
                            case 2:
                                Toast.makeText(MainActivity.this, "Eliminar 4", Toast.LENGTH_LONG).show();
                                break;

                        }
                        break;
                }
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
                drawer.closeDrawer(GravityCompat.START);
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


}
