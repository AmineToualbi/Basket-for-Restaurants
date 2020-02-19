package com.myapps.toualbiamine.basketbusiness.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myapps.toualbiamine.basketbusiness.Interface.ItemClickListener;
import com.myapps.toualbiamine.basketbusiness.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener{

    public TextView orderID;
    public TextView orderStatus;
    public TextView orderMenu;

    private ItemClickListener itemClickListener;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        orderID = (TextView) itemView.findViewById(R.id.orderID);
        orderStatus = (TextView) itemView.findViewById(R.id.orderStatus);
        orderMenu = (TextView) itemView.findViewById(R.id.orderMenu);
        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select an action");
        menu.add(0, 0, getAdapterPosition(), "Update");
        menu.add(0, 1, getAdapterPosition(), "Delete");

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        //No onClick => otherwise it throws an error.
        //itemClickListener.onClick(v, getAdapterPosition(), false);
    }

}
