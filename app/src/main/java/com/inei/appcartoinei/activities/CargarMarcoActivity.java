package com.inei.appcartoinei.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.inei.appcartoinei.R;
import com.inei.appcartoinei.modelo.DAO.Data;

import java.io.IOException;
import java.util.ArrayList;

public class CargarMarcoActivity extends AppCompatActivity {
    Button btn_cargar;
    Button btn_salir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargar_marco);

        btn_cargar = (Button) findViewById(R.id.btn_cargar);
        btn_salir = (Button) findViewById(R.id.btn_salir);

        btn_cargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateTable("manzana_captura")){
                    insertManzanaCaptura();
                    Toast.makeText(CargarMarcoActivity.this,"Se Cargo Marco",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(CargarMarcoActivity.this,"El marco ya fue cargado",Toast.LENGTH_LONG).show();
                }

            }
        });

        btn_salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   finish();
            }
        });

    }

    public boolean validateTable(String tabla){
        boolean estado = false;
        try {
            Data data = new Data(CargarMarcoActivity.this);
            data.open();
            estado = data.getValidacionTabla("manzana_captura");
            data.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return estado;
    }

    public void insertManzanaCaptura(){
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
        try {
            Data data = new Data(CargarMarcoActivity.this);
            data.open();
            data.insertManzanaCaptura(1,1,"15","01","13","001","00","001","","",0,0,"GeomFromText('POLYGON(("+formatGeom(listalatlog1)+"))',4326)");
            data.insertManzanaCaptura(1,1,"15","01","13","001","00","041","","",0,0,"GeomFromText('POLYGON(("+formatGeom(listalatlog41)+"))',4326)");
            data.insertManzanaCaptura(1,1,"15","01","13","001","00","042","","",0,0,"GeomFromText('POLYGON(("+formatGeom(listalatlog42)+"))',4326)");
            data.insertManzanaCaptura(1,1,"15","01","13","001","00","043","","",0,0,"GeomFromText('POLYGON(("+formatGeom(listalatlog43)+"))',4326)");
            data.insertManzanaCaptura(1,1,"15","01","13","001","00","044","","",0,0,"GeomFromText('POLYGON(("+formatGeom(listalatlog44)+"))',4326)");
            data.insertManzanaCaptura(1,1,"15","01","13","001","00","045","","",0,0,"GeomFromText('POLYGON(("+formatGeom(listalatlog45)+"))',4326)");
            data.close();
            Toast.makeText(CargarMarcoActivity.this,"Se Cargo Marco",Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
