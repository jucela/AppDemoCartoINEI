package com.inei.appcartoinei.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inei.appcartoinei.R;
import com.inei.appcartoinei.adapters.ItemPoligonoAdapter;
import com.inei.appcartoinei.adapters.ItemReporteAdapter;
import com.inei.appcartoinei.modelo.DAO.Data;
import com.inei.appcartoinei.modelo.pojos.Manzana;
import com.inei.appcartoinei.modelo.pojos.ManzanaCaptura;

import java.io.IOException;
import java.util.ArrayList;


public class ReporteFragment extends Fragment {

    View view;
    RecyclerView recyclerView;
    ItemReporteAdapter itemReporteAdapter;
    Context context;


    private OnFragmentInteractionListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_reporte, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_lista_reporte);
        return view;
    }

    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        itemReporteAdapter = new ItemReporteAdapter(obtenerAllManzanaCaptura(),new ItemReporteAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getContext(),"posiciòn:"+position,Toast.LENGTH_SHORT).show();

            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(itemReporteAdapter);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public ArrayList<ManzanaCaptura> obtenerAllManzanaCaptura()
    { ArrayList<ManzanaCaptura> manzanas = new ArrayList<>();
        try {
            Data data = new Data(context);
            data.open();
            manzanas = data.getAllManzanaCaptura();
            data.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return manzanas;
    }
}
