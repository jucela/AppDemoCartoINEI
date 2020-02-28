package com.inei.appcartoinei.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.inei.appcartoinei.R;
import com.inei.appcartoinei.modelo.pojos.FusionItem;

import java.util.ArrayList;

public class ItemFusionAdapter extends RecyclerView.Adapter<ItemFusionAdapter.ViewHolderItem> {
    ArrayList<FusionItem> datos;
    Context context;
    OnCheckedChangeListener onCheckedChangeListener;

    public interface OnCheckedChangeListener {
        public void onCheckedChanged(CompoundButton compoundButton,boolean b, int pos);}


    public ItemFusionAdapter(ArrayList<FusionItem> datos, Context context,OnCheckedChangeListener onCheckedChangeListener){
        this.datos = datos;
        this.context = context;
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    @NonNull
    @Override
    public ViewHolderItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fusion_adapter,parent,false);
        ViewHolderItem viewHolder = new ViewHolderItem(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderItem holder, final int position) {
        holder.checkViewItem.setOnCheckedChangeListener(null);
        if(datos.get(position).getEstado() == 0) holder.checkViewItem.setChecked(false);
        if(datos.get(position).getEstado() == 1) holder.checkViewItem.setChecked(true);
        holder.checkViewItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                onCheckedChangeListener.onCheckedChanged(compoundButton,b,position);
            }
        });

        holder.txtViewItem1.setText(datos.get(position).getIdzona().toString());
        holder.txtViewItem2.setText(datos.get(position).getIdManzana().toString());

    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    public class ViewHolderItem extends RecyclerView.ViewHolder{
        CardView cardViewItem;
        CheckBox checkViewItem;
        TextView txtViewItem1;
        TextView txtViewItem2;
        public ViewHolderItem(@NonNull View itemView) {
            super(itemView);
            cardViewItem = (CardView) itemView.findViewById(R.id.idcard_fusionadapter);
            checkViewItem = (CheckBox) itemView.findViewById(R.id.idcheck_fusionadapter);
            txtViewItem1 = (TextView) itemView.findViewById(R.id.idzona_fusionadapter);
            txtViewItem2 = (TextView) itemView.findViewById(R.id.idmanzana_fusionadapter);

        }
    }
}
