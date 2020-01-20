package com.inei.appcartoinei.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.inei.appcartoinei.R;
import com.inei.appcartoinei.fragments.MapManzanaFragment;
import com.inei.appcartoinei.fragments.MapViviendaFragment;
import com.inei.appcartoinei.modelo.DAO.DataBaseHelper;

import org.spatialite.database.SQLiteDatabase;

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



        MapManzanaFragment newFragment = new MapManzanaFragment();
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





}
