package com.inei.appcartoinei.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inei.appcartoinei.R;
import com.inei.appcartoinei.adapters.ItemFusionAdapter;
import com.inei.appcartoinei.modelo.DAO.Data;
import com.inei.appcartoinei.modelo.pojos.FusionItem;
import com.inei.appcartoinei.modelo.pojos.ManzanaCaptura;

import java.io.IOException;
import java.util.ArrayList;

public class DialogFusion extends AppCompatDialogFragment {
    public static final String TAG = DialogFusion.class.getSimpleName();
    private RecyclerView recyclerView;
    private ItemFusionAdapter itemFusionAdapter;
    public CardView cardView;
    public CheckBox checkEstado;
    public TextView txtIdzona,txtIdmanzana;
    private ArrayList<FusionItem> datos = new ArrayList<>();
    private ArrayList<FusionItem> datoss;
    private ArrayList<FusionItem> datosAEnviar = new ArrayList<>();
    ArrayList<FusionItem> listaManzanas = new ArrayList<>();
    ArrayList<FusionItem> listaFiltrada = new ArrayList<>();
    private SendDialogListener listener;
    private static final String ID = "ID";
    private static final String IDACCION = "IDACCION";




    /*RECIBE PARAMETROS DEL FRAGMENT*/
    public static DialogFusion newInstance(String idManzana,int idAcccion){
        Bundle arg = new Bundle();
        arg.putString(ID,idManzana);
        arg.putInt(IDACCION,idAcccion);
        DialogFusion frag = new DialogFusion();
        frag.setArguments(arg);
        return frag;
    }

    /*ENVIA PARAMETROS AL FRAGMENT*/
    public interface SendDialogListener {
        void receiveFusion(boolean estadoLayer,ArrayList<FusionItem> listaManzana,String idManzana,int idAccion);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.ThemeOverlay_MaterialComponents_Dialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_form_fusion, null);
        recyclerView = view.findViewById(R.id.fusion_recycler);
//        datos.add(new FusionItem(0,"003","001"));
//        datos.add(new FusionItem(0,"003","041"));
//        datos.add(new FusionItem(0,"003","042"));
//        datos.add(new FusionItem(0,"003","043"));
//        datos.add(new FusionItem(0,"003","044"));
//        datos.add(new FusionItem(0,"003","045"));

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        itemFusionAdapter = new ItemFusionAdapter(filter(obtenerManzanaCaptura(),getArguments().getString(ID)), getContext(), new ItemFusionAdapter.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b, int pos) {
                if(b){
                    filter(obtenerManzanaCaptura(),getArguments().getString(ID)).get(pos).setEstado(1);
                }else{
                    filter(obtenerManzanaCaptura(),getArguments().getString(ID)).get(pos).setEstado(0);
                }
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(itemFusionAdapter);

        builder.setView(view)
                .setTitle("Fusionar")
                .setIcon(R.drawable.ic_action_pin)
                .setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //listener.receiveFusion("hola1");
                    }
                })
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for(FusionItem item: filter(obtenerManzanaCaptura(),getArguments().getString(ID)) )
                        {
                            if(item.getEstado()==1){
                                datosAEnviar.add(item);
                            }
                        }
                        listener.receiveFusion(false,datosAEnviar,getArguments().getString(ID),getArguments().getInt(IDACCION));
                    }
                });
        return builder.create();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (SendDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
//            throw new ClassCastException("must implement ExampleDialogListener");
        }
    }

    public ArrayList<FusionItem> obtenerManzanaCaptura(){
        ArrayList<ManzanaCaptura> manzanas = new ArrayList<>();
        try {
            Data data = new Data(getContext());
            data.open();
            manzanas = data.getAllManzanaCapturaEstado();
            data.close();
            for(int i=0;i<manzanas.size();i++)
            {
              listaManzanas.add(new FusionItem(manzanas.get(i).getEstado(),manzanas.get(i).getCodzona(),manzanas.get(i).getCodmzna()));
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return listaManzanas;
    }

    private ArrayList<FusionItem> filter(ArrayList<FusionItem> notas, String texto){

        texto = texto.toLowerCase().trim();
            for(FusionItem nota: notas){
                String nota2= nota.getIdManzana().toLowerCase();
                if(nota2.contains(texto)==false){
                    listaFiltrada.add(new FusionItem(nota.getEstado(),nota.getIdzona(),nota.getIdManzana()));
                }
            }


        return listaFiltrada;
    }





}
