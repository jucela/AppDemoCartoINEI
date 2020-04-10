package com.inei.appcartoinei.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.inei.appcartoinei.R;
import com.inei.appcartoinei.modelo.DAO.Data;
import com.inei.appcartoinei.modelo.DAO.DataBaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.spatialite.database.SQLiteDatabase;
import java.io.IOException;

public class CargarMarcoActivity extends AppCompatActivity {
    Button btn_cargar;
    Button btn_salir;
    private RequestQueue mQueue;
    Data data;
    private SQLiteDatabase db ;
    private DataBaseHelper op;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargar_marco);
        createDB();

        mQueue = Volley.newRequestQueue(CargarMarcoActivity.this);

        btn_cargar = (Button) findViewById(R.id.btn_cargar);
        btn_salir = (Button) findViewById(R.id.btn_salir);

        btn_cargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDatos();
            }
        });

        btn_salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   finish();
            }
        });

    }

    private void createDB(){
        op = new DataBaseHelper(this);
        db = op.getWritableDatabase();
    }

    public void loadDatos(){
        if(validateTable("manzana_marco") && validateTable("manzana_captura")  ){
            downloadMarcoDistrito();
            copyTable();
        }
        else{
            Toast.makeText(CargarMarcoActivity.this,"El marco ya fue cargado",Toast.LENGTH_LONG).show();
        }
    }

    public boolean validateTable(String tabla){
        boolean estado = false;
        try {
            Data data = new Data(CargarMarcoActivity.this);
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
            Data data = new Data(CargarMarcoActivity.this);
            data.open();
            data.copyTableMarco();
            data.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void downloadMarcoDistrito() {
        Toast.makeText(CargarMarcoActivity.this, "Descargando...", Toast.LENGTH_LONG).show();
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
                                data = new Data(CargarMarcoActivity.this);
                                data.open();
                                data.insertManzanaMarco(idManzana,Integer.parseInt(idUser),ccdd,ccpp,ccdi,codZona,sufZona,codMzna,sufMzna,shape);
                                data.close();
                                if(i==response.length()-1)
                                {
                                    copyTable();
                                    Toast.makeText(CargarMarcoActivity.this, "Se Descargo "+(i+1)+" Manzanas", Toast.LENGTH_LONG).show();
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
                Toast.makeText(CargarMarcoActivity.this,"No se pudo Sincronizar, Vuelva a intentarlo en unos minutos.",Toast.LENGTH_LONG).show();
            }
        });
        mQueue.add(jsonArrayRequest);
    }

}
