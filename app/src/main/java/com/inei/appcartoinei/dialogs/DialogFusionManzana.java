package com.inei.appcartoinei.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.inei.appcartoinei.R;
import com.inei.appcartoinei.adapters.ItemFusionSeleccionAdapter;
import com.inei.appcartoinei.modelo.pojos.FusionItem;
import java.util.ArrayList;

public class DialogFusionManzana extends DialogFragment {
    public static final String TAG = DialogFusionManzana.class.getSimpleName();
    private RecyclerView recyclerView;
    private ItemFusionSeleccionAdapter itemFusionSeleccionAdapter;
    private static ArrayList<FusionItem> manzanaSeleccionada = new ArrayList<>();
    private SendDialogListener listener;
    private static final String ID = "ID";
    private FloatingActionButton btn_agregar;

    /*RECIBE PARAMETROS DEL FRAGMENT*/
    public static DialogFusionManzana newInstance(String idManzana,ArrayList<FusionItem> manzanas){
        Bundle arg = new Bundle();
        arg.putString(ID,idManzana);
        manzanaSeleccionada = manzanas;
        DialogFusionManzana frag = new DialogFusionManzana();
        frag.setArguments(arg);
        return frag;
    }

    /*ENVIA PARAMETROS AL FRAGMENT*/
    public interface SendDialogListener {
        void receiveFusion(int estadoLayer, ArrayList<FusionItem> listaManzana, String idManzana);
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
        itemFusionSeleccionAdapter = new ItemFusionSeleccionAdapter(manzanaSeleccionada, getContext(), new ItemFusionSeleccionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position,String idmanzana) {
                Toast.makeText(getContext(),"Se Elimino Manzana Nº"+idmanzana,Toast.LENGTH_SHORT).show();
                manzanaSeleccionada.remove(position);
                recyclerView.removeViewAt(position);
                itemFusionSeleccionAdapter.notifyItemRemoved(position);
                itemFusionSeleccionAdapter.notifyItemRangeChanged(position, manzanaSeleccionada.size());
                itemFusionSeleccionAdapter.notifyDataSetChanged();
            }
        });

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(itemFusionSeleccionAdapter);


        btn_agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.receiveFusion(2,manzanaSeleccionada,getArguments().getString(ID).trim());
                Toast.makeText(getContext(),"Seleccione una manzana",Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        builder.setView(view)
                .setTitle("FUSION DE MANZANAS")
                .setIcon(R.drawable.ic_action_pin)
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.receiveFusion(0,manzanaSeleccionada,getArguments().getString(ID).trim());
                    }
                })
                .setPositiveButton("Dibujar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (manzanaSeleccionada.size()>1)
                        {listener.receiveFusion(1,manzanaSeleccionada,getArguments().getString(ID).trim());
                         Toast.makeText(getContext(),"Dibuje la fusiòn de manzanas",Toast.LENGTH_SHORT).show();
                        }
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
}
