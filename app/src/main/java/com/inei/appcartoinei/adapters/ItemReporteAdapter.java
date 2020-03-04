package com.inei.appcartoinei.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.inei.appcartoinei.R;
import com.inei.appcartoinei.modelo.pojos.Manzana;
import com.inei.appcartoinei.modelo.pojos.ManzanaCaptura;

import java.util.ArrayList;


public class ItemReporteAdapter extends RecyclerView.Adapter<ItemReporteAdapter.ViewHolderItem> {
    private ArrayList<ManzanaCaptura> manzanas;
    OnItemClickListener onItemClickListener;
    Context context;

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public ItemReporteAdapter(ArrayList<ManzanaCaptura> manzanas, OnItemClickListener onItemClickListener) {
        this.manzanas = manzanas;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolderItem onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row_reporte,viewGroup,false);
        ViewHolderItem viewHolder = new ViewHolderItem(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderItem viewHolder, final int position) {
        final ManzanaCaptura manzana = manzanas.get(position);
        viewHolder.txtItem1.setText(""+manzana.getCodzona());
        viewHolder.txtItem2.setText(""+manzana.getCodmzna());
        viewHolder.txtItem3.setText(convertAccion(manzana.getEstado()));
        viewHolder.txtItem4.setText(""+manzana.getFrentes());
    }

    @Override
    public int getItemCount() {
        return manzanas.size();
    }

    public static class ViewHolderItem extends RecyclerView.ViewHolder {
        TextView txtItem1;
        TextView txtItem2;
        TextView txtItem3;
        TextView txtItem4;
        CardView cv;
        public ViewHolderItem(@NonNull View itemView) {
            super(itemView);
            cv =       itemView.findViewById(R.id.cardview_item_reporte);
            txtItem1 = itemView.findViewById(R.id.txt_item_rp_zona);
            txtItem2 = itemView.findViewById(R.id.txt_item_rp_manzana);
            txtItem3 = itemView.findViewById(R.id.txt_item_rp_estado);
            txtItem4 = itemView.findViewById(R.id.txt_item_rp_frentes);

        }
    }

    public String convertAccion(int dato){
        String newdato="";
        switch (dato){
            case 0:
                newdato="Activo";
                break;
            case 1:
                newdato="Con Cambios";
                break;
            case 2:
                newdato="Fusionadox";
                break;
            case 3:
                newdato="Fragmentado";
                break;
            default:

        }
        return  newdato;
    }


}
