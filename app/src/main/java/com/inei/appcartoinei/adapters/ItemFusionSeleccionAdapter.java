package com.inei.appcartoinei.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.inei.appcartoinei.R;
import com.inei.appcartoinei.modelo.pojos.FusionItem;

import java.util.ArrayList;

public class ItemFusionSeleccionAdapter extends RecyclerView.Adapter<ItemFusionSeleccionAdapter.ViewHolderItem> {
    ArrayList<FusionItem> datos;
    Context context;
    OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        public void onItemClick(View view,int position,String idmanzana);}


    public ItemFusionSeleccionAdapter(ArrayList<FusionItem> datos, Context context, OnItemClickListener onItemClickListener){
        this.datos = datos;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolderItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fusion_seleccion_adapter,parent,false);
        ViewHolderItem viewHolder = new ViewHolderItem(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderItem holder, final int position) {
        if(position==0)
        {
            holder.txtViewItem1.setText(datos.get(position).getIdzona().toString());
            holder.txtViewItem2.setText(datos.get(position).getIdManzana().toString());
            holder.checkViewItem.setImageResource(R.drawable.ic_check_circle_24);
        }
        else {
            holder.checkViewItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(view, position, datos.get(position).getIdManzana().trim());
                }
            });
            holder.txtViewItem1.setText(datos.get(position).getIdzona().toString());
            holder.txtViewItem2.setText(datos.get(position).getIdManzana().toString());
        }
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    public class ViewHolderItem extends RecyclerView.ViewHolder{
        CardView cardViewItem;
        ImageView checkViewItem;
        TextView txtViewItem1;
        TextView txtViewItem2;
        public ViewHolderItem(@NonNull View itemView) {
            super(itemView);
            cardViewItem = (CardView) itemView.findViewById(R.id.idcard_fusionadapter_seleccion);
            checkViewItem = (ImageView) itemView.findViewById(R.id.idcheck_fusionadapter_seleccion);
            txtViewItem1 = (TextView) itemView.findViewById(R.id.idzona_fusionadapter_seleccion);
            txtViewItem2 = (TextView) itemView.findViewById(R.id.idmanzana_fusionadapter_seleccion);

        }
    }
}
