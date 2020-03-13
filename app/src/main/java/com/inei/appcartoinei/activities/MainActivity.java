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
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;
import com.inei.appcartoinei.R;
import com.inei.appcartoinei.fragments.ListPoligonoFragment;
import com.inei.appcartoinei.fragments.MapActualizarManzanaFragment;
import com.inei.appcartoinei.fragments.MapAnadirManzanaFragment;
import com.inei.appcartoinei.fragments.MapAsignarViviendaFragment;
import com.inei.appcartoinei.fragments.MapDibujarManzanaFragment;
import com.inei.appcartoinei.fragments.MapFraccionarManzanaFragment;
import com.inei.appcartoinei.fragments.MapFusionarManzanaFragment;
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

        MapAnadirManzanaFragment newFragment = new MapAnadirManzanaFragment();
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
                cargarMarco();
                break;
            case R.id.capa5:
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
        MapAnadirManzanaFragment newFragment = new MapAnadirManzanaFragment();
        newFragment.setArguments(args);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contedor_fragments,newFragment).commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void viewFragment2(){
        Bundle args = new Bundle();
        args.putString("idUsuario",""+1);
        MapActualizarManzanaFragment newFragment = new MapActualizarManzanaFragment();
        newFragment.setArguments(args);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contedor_fragments,newFragment).commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void viewFragment3(){
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
        ArrayList<LatLng> listalatlog1= new ArrayList<>();
        ArrayList<LatLng> listalatlog41= new ArrayList<>();
        ArrayList<LatLng> listalatlog42= new ArrayList<>();
        ArrayList<LatLng> listalatlog43= new ArrayList<>();
        ArrayList<LatLng> listalatlog44= new ArrayList<>();
        ArrayList<LatLng> listalatlog45= new ArrayList<>();
        /*001*/
        listalatlog1.add(new LatLng(-12.065256655999974,-77.044274425999959));
        listalatlog1.add(new LatLng(-12.065391485999953,-77.045384263999949));
        listalatlog1.add(new LatLng(-12.066001731999961,-77.044634876999964));
        listalatlog1.add(new LatLng(-12.06571310299995,-77.044383404999962));
        listalatlog1.add(new LatLng(-12.065256655999974,-77.044274425999959));

        /*041*/
        listalatlog41.add(new LatLng(-12.066394786999979,-77.044391233999988));
        listalatlog41.add(new LatLng(-12.067573683999967,-77.044703653999989));
        listalatlog41.add(new LatLng(-12.067914064999968,-77.044226561999949));
        listalatlog41.add(new LatLng(-12.067080448999945,-77.043547487999945));
        listalatlog41.add(new LatLng(-12.066394786999979,-77.044391233999988));
        /*042*/
        listalatlog42.add(new LatLng(-12.066228606999971,-77.044578442999978));
        listalatlog42.add(new LatLng(-12.067107399999941,-77.045280209999987));
        listalatlog42.add(new LatLng(-12.067497371999934,-77.044794291999949));
        listalatlog42.add(new LatLng(-12.066297254999938,-77.04449155399999));
        listalatlog42.add(new LatLng(-12.066228606999971,-77.044578442999978));
        /*043*/
        listalatlog43.add(new LatLng(-12.065417885999977,-77.04554404999999));
        listalatlog43.add(new LatLng(-12.066270790999965,-77.046327907999967));
        listalatlog43.add(new LatLng(-12.066979834999927,-77.045446862999938));
        listalatlog43.add(new LatLng(-12.066131906999942,-77.044721458999959));
        listalatlog43.add(new LatLng(-12.065417885999977,-77.04554404999999));
        /*44*/
        listalatlog44.add(new LatLng(-12.066392049999934,-77.046393858999977));
        listalatlog44.add(new LatLng(-12.067275332999941,-77.04712865099998));
        listalatlog44.add(new LatLng(-12.067978914999969,-77.046234584999979));
        listalatlog44.add(new LatLng(-12.067104585999971,-77.04552433799995));
        listalatlog44.add(new LatLng(-12.066392049999934,-77.046393858999977));
        /*45*/
        listalatlog45.add(new LatLng(-12.06722861999998,-77.045396516999972));
        listalatlog45.add(new LatLng(-12.068081307999933,-77.04610396299995));
        listalatlog45.add(new LatLng(-12.068618938999975,-77.045423454999934));
        listalatlog45.add(new LatLng(-12.068171483999947,-77.045057299999939));
        listalatlog45.add(new LatLng(-12.067933634999974,-77.044941336999955));
        listalatlog45.add(new LatLng(-12.067660431999968,-77.044855764999966));
        listalatlog45.add(new LatLng(-12.06722861999998,-77.045396516999972));



        /*0*/
        //listalatlog0.add(new LatLng(0,0));

        try {
            Data data = new Data(MainActivity.this);
            data.open();
            Log.e("mensaje:","string de insercion:"+formatGeom(listalatlog1));
            data.insertManzanaCaptura(1,1,"15","01","13","001","00","001","",0,0,"GeomFromText('POLYGON(("+formatGeom(listalatlog1)+"))',4326)");
            data.insertManzanaCaptura(1,1,"15","01","13","001","00","041","",0,0,"GeomFromText('POLYGON(("+formatGeom(listalatlog41)+"))',4326)");
            data.insertManzanaCaptura(1,1,"15","01","13","001","00","042","",0,0,"GeomFromText('POLYGON(("+formatGeom(listalatlog42)+"))',4326)");
            data.insertManzanaCaptura(1,1,"15","01","13","001","00","043","",0,0,"GeomFromText('POLYGON(("+formatGeom(listalatlog43)+"))',4326)");
            data.insertManzanaCaptura(1,1,"15","01","13","001","00","044","",0,0,"GeomFromText('POLYGON(("+formatGeom(listalatlog44)+"))',4326)");
            data.insertManzanaCaptura(1,1,"15","01","13","001","00","045","",0,0,"GeomFromText('POLYGON(("+formatGeom(listalatlog45)+"))',4326)");
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
