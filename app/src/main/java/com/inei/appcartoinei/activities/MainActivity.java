package com.inei.appcartoinei.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.inei.appcartoinei.R;
import com.inei.appcartoinei.fragments.MapActualizarManzanaFragment;
import com.inei.appcartoinei.fragments.MapAnadirManzanaFragment;
import com.inei.appcartoinei.fragments.ReporteFragment;
import com.inei.appcartoinei.modelo.DAO.Data;
import com.inei.appcartoinei.modelo.DAO.DataBaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.spatialite.database.SQLiteDatabase;

import java.io.IOException;

public class MainActivity extends AppCompatActivity  {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private SQLiteDatabase db ;
    private DataBaseHelper op;
    private RequestQueue mQueue;
    Data data;
    final String ubigeo     = "150113";
    final String codigoZona = "001";
    final String sufijoZona = "00";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout =(DrawerLayout)findViewById(R.id.drawer);
        createDB();
        mQueue = Volley.newRequestQueue(MainActivity.this);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_drawer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupDrawerContent(navigationView);

        MapActualizarManzanaFragment newFragment = new MapActualizarManzanaFragment(ubigeo,codigoZona,sufijoZona,MainActivity.this);
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
                loadDatos();
                break;
            case R.id.capa5:
                resetBD();
                break;
            case R.id.capa6:
                finish();
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
        MapAnadirManzanaFragment newFragment = new MapAnadirManzanaFragment(ubigeo,codigoZona,sufijoZona,MainActivity.this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contedor_fragments,newFragment).commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void viewFragment2(){
        MapActualizarManzanaFragment newFragment = new MapActualizarManzanaFragment(ubigeo,codigoZona,sufijoZona,MainActivity.this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contedor_fragments,newFragment).commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void viewFragment3(){
        ReporteFragment newFragment = new ReporteFragment(ubigeo,codigoZona,sufijoZona,MainActivity.this);
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
                            data.deleteTblManzanaCaptura();
                            data.deleteTblManzanaMarco();
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

    public void loadDatos(){
        if(validateTable("manzana_marco") && validateTable("manzana_captura")  ){
            downloadMarcoDistrito();
            copyTable();
        }
        else{
            Toast.makeText(MainActivity.this,"El marco ya fue cargado",Toast.LENGTH_LONG).show();
        }
    }

    public boolean validateTable(String tabla){
        boolean estado = false;
        try {
            Data data = new Data(MainActivity.this);
            data.open();
            estado = data.getValidacionTabla(tabla);
            data.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return estado;
    }

    public void copyTable(){
        try {
            Data data = new Data(MainActivity.this);
            data.open();
            data.copyTableMarco();
            data.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void downloadMarcoDistrito() {
        Toast.makeText(MainActivity.this, "Descargando...", Toast.LENGTH_LONG).show();
        String DjangoAPI_PROD_352 = "https://api.npoint.io/6e22a17095ca1c654675";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                DjangoAPI_PROD_352,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try{
                            for(int i=0;i<response.length();i++){
                                JSONObject jsonObject = response.getJSONObject(i);
                                String idManzana = (String) jsonObject.get("idmanzana");
                                String idUser    = "1";
                                String ccdd      = (String) jsonObject.get("ccdd");
                                String ccpp      = (String) jsonObject.get("ccpp");
                                String ccdi      = (String) jsonObject.get("ccdi");
                                String codZona   = (String) jsonObject.get("codzona");
                                String sufZona   = (String) jsonObject.get("sufzona");
                                String codMzna   = (String) jsonObject.get("codmzna");
                                String sufMzna   = (String) jsonObject.get("sufmzna");
                                String shape     = (String) jsonObject.get("polygon");
                                Log.i("Contador:",""+i);
                                data = new Data(MainActivity.this);
                                data.open();
                                data.insertManzanaMarco(idManzana,Integer.parseInt(idUser),ccdd,ccpp,ccdi,codZona,sufZona,codMzna,sufMzna,shape);
                                data.close();
                                if(i==response.length()-1)
                                {
                                    copyTable();
                                    Toast.makeText(MainActivity.this, "Se Descargo Marco: "+(i+1)+" Registros", Toast.LENGTH_LONG).show();
                                }
                            }
                        }catch (JSONException | IOException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(MainActivity.this,"No se pudo Sincronizar, Vuelva a intentarlo en unos minutos.",Toast.LENGTH_LONG).show();
            }
        });
        mQueue.add(jsonArrayRequest);
    }
}
