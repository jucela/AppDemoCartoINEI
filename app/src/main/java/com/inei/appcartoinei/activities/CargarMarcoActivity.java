package com.inei.appcartoinei.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.inei.appcartoinei.utils.UtilsImportData.getUltimoTresDigito;

public class CargarMarcoActivity extends AppCompatActivity {
    Button btn_cargar;
    Button btn_salir;
    Button btn_importar;
    Button btn_seleccionar;
    EditText edt_ruta;
    private RequestQueue mQueue;
    Data data;
    private SQLiteDatabase db ;
    private DataBaseHelper op;
    private Uri fileUri;
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargar_marco);
        createDB();

        mQueue = Volley.newRequestQueue(CargarMarcoActivity.this);

        btn_cargar      = (Button) findViewById(R.id.btn_cargar);
        btn_importar    = (Button) findViewById(R.id.btn_importar);
        btn_salir       = (Button) findViewById(R.id.btn_salir);
        btn_seleccionar = (Button) findViewById(R.id.btn_seleccionar);
        edt_ruta = (EditText) findViewById(R.id.edt_ruta);

        btn_cargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDatos(1);
            }
        });

        btn_seleccionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("*/*");
                chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(chooseFile, 1);
            }
        });
        btn_importar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDatos(2);
            }
        });
        btn_salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   finish();
            }
        });



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent datos) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == -1) {
                    fileUri = datos.getData();
                    filePath = fileUri.getPath();
                    if(getUltimoTresDigito(filePath).equals(".json"))
                    {
                        edt_ruta.setText(filePath);
                    }
                    else
                    {
                        Toast.makeText(CargarMarcoActivity.this, "El archivo Seleccionado no es compatible", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
        Log.i("mensaje2:",""+requestCode+"->"+resultCode+"->"+data);
    }

    private void createDB(){
        op = new DataBaseHelper(this);
        db = op.getWritableDatabase();
    }

    public void loadDatos(int tipoConexion){
        if(validateTable("manzana_marco") && validateTable("manzana_captura")  ){
            switch (tipoConexion){
            case 1:
                downloadMarcoOnline();
                copyTable();
                break;
            case 2:
                if(edt_ruta.getText().toString().length()>1)
                {
                    downloadMarcoOffline2();
                    copyTable();
                }
                else {
                    Toast.makeText(CargarMarcoActivity.this, "Seleccione un archivo", Toast.LENGTH_LONG).show();
                }

                break;
            default:
            }
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

    private void downloadMarcoOnline() {
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

    private void downloadMarcoOffline() {
            Toast.makeText(CargarMarcoActivity.this, "Copiando...", Toast.LENGTH_LONG).show();
            String fileContent = readTextFile(fileUri);
            try {
                JSONArray jsonArray = new JSONArray(fileContent);
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
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
                    Log.i("mensajex:",""+codMzna);
                    data = new Data(CargarMarcoActivity.this);
                    data.open();
                    data.insertManzanaMarco(idManzana,Integer.parseInt(idUser),ccdd,ccpp,ccdi,codZona,sufZona,codMzna,sufMzna,shape);
                    data.close();
                    if(i==jsonArray.length()-1)
                    {
                        edt_ruta.setText("");
                        copyTable();
                        Toast.makeText(CargarMarcoActivity.this, "Se Copiaron "+(i+1)+" Manzanas", Toast.LENGTH_LONG).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("mensaje1:",""+fileUri+"->"+filePath);
            Log.i("mensaje2:",""+fileContent);
    }

    private void downloadMarcoOffline2() {
        Toast.makeText(CargarMarcoActivity.this, "Copiando...", Toast.LENGTH_LONG).show();
        String fileContent = readTextFile(fileUri);
        try {
            JSONObject jsonObject1 = new JSONObject(fileContent);
            JSONArray jsonArray = jsonObject1.getJSONArray("marco");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
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
                Log.i("mensajex:",""+codMzna);
                data = new Data(CargarMarcoActivity.this);
                data.open();
                data.insertManzanaMarco(idManzana,Integer.parseInt(idUser),ccdd,ccpp,ccdi,codZona,sufZona,codMzna,sufMzna,shape);
                data.close();
                if(i==jsonArray.length()-1)
                {
                    edt_ruta.setText("");
                    copyTable();
                    Toast.makeText(CargarMarcoActivity.this, "Se Copiaron "+(i+1)+" Manzanas", Toast.LENGTH_LONG).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("mensaje1:",""+fileUri+"->"+filePath);
        Log.i("mensaje2:",""+fileContent);
    }

    private String readTextFile(Uri uri){
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(uri)));
            String line = "";

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }

}
