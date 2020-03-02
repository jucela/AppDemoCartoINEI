package com.inei.appcartoinei.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;
import com.inei.appcartoinei.R;
import com.inei.appcartoinei.fragments.ListPoligonoFragment;
import com.inei.appcartoinei.fragments.MapAsignarViviendaFragment;
import com.inei.appcartoinei.fragments.MapDibujarManzanaFragment;
import com.inei.appcartoinei.fragments.MapManzanaFragment;
import com.inei.appcartoinei.fragments.MapManzanaPolylineFragment;
import com.inei.appcartoinei.fragments.MapViviendaFragment;
import com.inei.appcartoinei.fragments.ReporteFragment;
import com.inei.appcartoinei.modelo.DAO.Data;
import com.inei.appcartoinei.modelo.DAO.DataBaseHelper;

import org.spatialite.database.SQLiteDatabase;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private SQLiteDatabase db ;
    private DataBaseHelper op;
    private ArrayList<LatLng> listalatlog= new ArrayList<LatLng>();

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

        MapDibujarManzanaFragment newFragment = new MapDibujarManzanaFragment();
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
                viewFragment1();
                break;
            case R.id.capa2:
                viewFragment2();
                break;
            case R.id.capa3:
                viewFragment3();
                break;
            case R.id.capa4:
                viewFragment4();
                break;
            case R.id.capa5:
                viewFragment5();
                break;
            case R.id.capa6:
                cargarMarco();
                break;
            case R.id.capa7:
                resetBD();
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

    private void createDB(){
        op = new DataBaseHelper(this);
        db = op.getWritableDatabase();
    }

    public void viewFragment1(){
        Bundle args = new Bundle();
        args.putString("idUsuario",""+1);
        MapManzanaFragment newFragment = new MapManzanaFragment();
        newFragment.setArguments(args);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contedor_fragments,newFragment).commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void viewFragment2(){
        Bundle args = new Bundle();
        args.putString("idUsuario",""+1);
        MapViviendaFragment newFragment = new MapViviendaFragment();
        newFragment.setArguments(args);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contedor_fragments,newFragment).commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void viewFragment3(){
        Bundle args = new Bundle();
        args.putString("idUsuario",""+1);
        MapAsignarViviendaFragment newFragment = new MapAsignarViviendaFragment();
        newFragment.setArguments(args);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contedor_fragments,newFragment).commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void viewFragment4(){
        Bundle args = new Bundle();
        args.putString("idUsuario",""+1);
        MapDibujarManzanaFragment newFragment = new MapDibujarManzanaFragment();
        newFragment.setArguments(args);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contedor_fragments,newFragment).commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void viewFragment5(){
        Bundle args = new Bundle();
        args.putString("idUsuario",""+1);
        ReporteFragment newFragment = new ReporteFragment();
        newFragment.setArguments(args);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contedor_fragments,newFragment).commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void resetBD(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Está seguro que desea borrar los datos?")
                .setTitle("Aviso")
                .setCancelable(false)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            Data data = new Data(MainActivity.this);
                            data.open();
                            data.deleteTblManzana();
                            data.deleteTblVivienda();
                            data.deleteTblManzanaCaptura();
                            data.close();
                            Toast.makeText(MainActivity.this,"Se Reseteo Base de Datos",Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void cargarMarco(){
        listalatlog.add(new LatLng(0,0));
        listalatlog.add(new LatLng(0,0));
        try {
            Data data = new Data(MainActivity.this);
            data.open();
            data.insertManzanaCaptura(1,1,"15","01","13","001","00","001","",0,0,"GeomFromText('POLYGON(("+formatGeom(listalatlog)+"))',4326)");
            data.insertManzanaCaptura(1,1,"15","01","13","001","00","041","",0,0,"GeomFromText('POLYGON(("+formatGeom(listalatlog)+"))',4326)");
            data.insertManzanaCaptura(1,1,"15","01","13","001","00","042","",0,0,"GeomFromText('POLYGON(("+formatGeom(listalatlog)+"))',4326)");
            data.insertManzanaCaptura(1,1,"15","01","13","001","00","043","",0,0,"GeomFromText('POLYGON(("+formatGeom(listalatlog)+"))',4326)");
            data.insertManzanaCaptura(1,1,"15","01","13","001","00","044","",0,0,"GeomFromText('POLYGON(("+formatGeom(listalatlog)+"))',4326)");
            data.insertManzanaCaptura(1,1,"15","01","13","001","00","045","",0,0,"GeomFromText('POLYGON(("+formatGeom(listalatlog)+"))',4326)");
            data.close();
            Toast.makeText(MainActivity.this,"Se Cargo Marco",Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
