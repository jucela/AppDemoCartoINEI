package com.inei.appcartoinei.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.inei.appcartoinei.R;
import com.inei.appcartoinei.adapters.ExpandListAdapter;
import com.inei.appcartoinei.fragments.Capa1;
import com.inei.appcartoinei.fragments.Capa2;
import com.inei.appcartoinei.fragments.Capa3;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout =(DrawerLayout)findViewById(R.id.drawer);

        boton =(Button) findViewById(R.id.boton);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        enableExpandableList();
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_drawer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupDrawerContent(navigationView);


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
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        expListView = (ExpandableListView) findViewById(R.id.expandable_principal1);

        prepareListData(listDataHeader, listDataChild);
        listAdapter = new ExpandListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);

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
        listDataHeader.add(0,"Capa 01");
        listDataHeader.add(1,"Capa 02");
        listDataHeader.add(2,"Capa 03");
        listDataHeader.add(3,"Capa 04");

        // Adding child data
        List<String> grupo1 = new ArrayList<String>();
        grupo1.add(0,"Agregar");
        grupo1.add(1,"Editar");
        grupo1.add(2,"Eliminar");

        List<String> grupo2 = new ArrayList<String>();
        grupo2.add(0,"Agregar");
        grupo2.add(1,"Editar");
        grupo2.add(2,"Eliminar");


        List<String> grupo3 = new ArrayList<String>();
        grupo3.add(0,"Agregar");
        grupo3.add(1,"Editar");
        grupo3.add(2,"Eliminar");

        List<String> grupo4 = new ArrayList<String>();
        grupo4.add(0,"Agregar");
        grupo4.add(1,"Editar");
        grupo4.add(2,"Eliminar");


        listDataChild.put(listDataHeader.get(0),grupo1);// Header, Child data
        listDataChild.put(listDataHeader.get(1),grupo2);
        listDataChild.put(listDataHeader.get(2),grupo3);
        listDataChild.put(listDataHeader.get(3),grupo4);
    }
}
