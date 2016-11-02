package com.example.sebastian.recone.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.sebastian.recone.R;

/**
 * Created by Sebastian on 02/11/2016.
 */

public class ItemViewHolder extends RecyclerView.ViewHolder{

    public TextView labelView;
    public TextView estadoView;



    public ItemViewHolder(View itemView) {
        super(itemView);
        labelView = (TextView) itemView.findViewById(R.id.tvLabel);
        estadoView = (TextView) itemView.findViewById(R.id.tvEstado);
    }

    public void bindToItem(String label, String estado) {
        labelView.setText(label);
        estadoView.setText(estado);
    }
}
