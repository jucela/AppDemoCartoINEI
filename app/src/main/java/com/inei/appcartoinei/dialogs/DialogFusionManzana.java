package com.inei.appcartoinei.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.inei.appcartoinei.R;
import com.inei.appcartoinei.adapters.ItemFusionAdapter;
import com.inei.appcartoinei.adapters.ItemFusionSeleccionAdapter;
import com.inei.appcartoinei.modelo.DAO.Data;
import com.inei.appcartoinei.modelo.pojos.FusionItem;
import com.inei.appcartoinei.modelo.pojos.ManzanaCaptura;

import java.io.IOException;
import java.util.ArrayList;

public class DialogFusionManzana extends AppCompatDialogFragment {
    public static final String TAG = DialogFusionManzana.class.getSimpleName();
    private RecyclerView recyclerView;
    private ItemFusionSeleccionAdapter itemFusionSeleccionAdapter;
    public CardView cardView;
    public CheckBox checkEstado;
    public TextView txtIdzona,txtIdmanzana;
    private ArrayList<FusionItem> datos = new ArrayList<>();
    private static ArrayList<FusionItem> datoss = new ArrayList<>();
    private ArrayList<FusionItem> datosAEnviar = new ArrayList<>();
    ArrayList<FusionItem> listaManzanas = new ArrayList<>();
    ArrayList<FusionItem> listaFiltrada = new ArrayList<>();
    private SendDialogListener listener;
    private static final String ID = "ID";
    private static final String IDACCION = "IDACCION";
    private static final String LISTAMANZANAS = "IDACCION";
    private FloatingActionButton btn_agregar;

    /*RECIBE PARAMETROS DEL FRAGMENT*/
    public static DialogFusionManzana newInstance(String idManzana, int idAcccion,ArrayList<FusionItem> manzanas){
        Bundle arg = new Bundle();
        arg.putString(ID,idManzana);
        arg.putInt(IDACCION,idAcccion);
        datoss = manzanas;
        DialogFusionManzana frag = new DialogFusionManzana();
        frag.setArguments(arg);
        return frag;
    }

    /*ENVIA PARAMETROS AL FRAGMENT*/
    public interface SendDialogListener {
        void receiveFusion(boolean estadoLayer, ArrayList<FusionItem> listaManzana, String idManzana, int idAccion,boolean paso);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.ThemeOverlay_MaterialComponents_Dialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_form_fusion_manzana,null);
        recyclerView = view.findViewById(R.id.fusion_seleccion_recycler);
        btn_agregar = view.findViewById(R.id.fab_add_manzana);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        itemFusionSeleccionAdapter = new ItemFusionSeleccionAdapter(datoss, getContext(), new ItemFusionSeleccionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position,String idmanzana) {
                Toast.makeText(getContext(),"ID:"+idmanzana,Toast.LENGTH_SHORT).show();
                //datos.clear();
                datoss.remove(position);
                //recyclerView.removeViewAt(position);
                //itemFusionSeleccionAdapter.notifyItemRemoved(position);
                //itemFusionSeleccionAdapter.notifyItemRangeChanged(position, datoss.size());
                itemFusionSeleccionAdapter.notifyDataSetChanged();
            }
        });


        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(itemFusionSeleccionAdapter);


        btn_agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.receiveFusion(true,datoss,getArguments().getString(ID).trim(),getArguments().getInt(IDACCION),true);
                dismiss();
            }
        });

        builder.setView(view)
                .setTitle("FUSION DE MANZANAS")
                .setIcon(R.drawable.ic_action_pin)
                .setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (datoss.size()>1)
                        {listener.receiveFusion(false,datoss,getArguments().getString(ID),getArguments().getInt(IDACCION),false);}
                        else{
                            Toast.makeText(getContext(),"Debe seleccionar màs de una manzana",Toast.LENGTH_SHORT).show();
                        }
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