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

import java.util.ArrayList;


public class ItemPoligonoAdapter extends RecyclerView.Adapter<ItemPoligonoAdapter.ViewHolderItem> {
    private ArrayList<Manzana> manzanas;
    OnItemClickListener onItemClickListener;
    Context context;

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public ItemPoligonoAdapter(ArrayList<Manzana> manzanas, OnItemClickListener onItemClickListener) {
        this.manzanas = manzanas;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }


    @NonNull
    @Override
    public ViewHolderItem onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row_poligono,viewGroup,false);
        ViewHolderItem viewHolder = new ViewHolderItem(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderItem viewHolder, final int position) {
        final Manzana manzana = manzanas.get(position);
        viewHolder.txtItem1.setText(""+manzana.getId());
        viewHolder.txtItem2.setText(""+manzana.getNommanzana());
        viewHolder.txtItem3.setText(""+manzana.getUbigeo());
        viewHolder.txtItem4.setText(""+manzana.getZona());
        viewHolder.txtItem5.setText(""+manzana.getShape());

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
        TextView txtItem5;
        CardView cv;
        public ViewHolderItem(@NonNull View itemView) {
            super(itemView);
            cv =       itemView.findViewById(R.id.cardview_item_poligono);
            txtItem1 = itemView.findViewById(R.id.txt_item_id);
            txtItem2 = itemView.findViewById(R.id.txt_item_manzana);
            txtItem3 = itemView.findViewById(R.id.txt_item_ubigeo);
            txtItem4 = itemView.findViewById(R.id.txt_item_zona);
            txtItem5 = itemView.findViewById(R.id.txt_item_area);

        }
    }


}
