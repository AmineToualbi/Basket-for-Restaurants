package com.myapps.toualbiamine.basketbusiness.ViewHolder;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myapps.toualbiamine.basketbusiness.Common.Common;
import com.myapps.toualbiamine.basketbusiness.Interface.ItemClickListener;
import com.myapps.toualbiamine.basketbusiness.R;

/*Steps to creating & implementing a RecyclerView:
    1. Create an activity that will contain it, i.e.: FoodList.
    2. Create a Card for the items that will be displayed in the list.
    3. Create a ViewHolder following the implementation below.
    4. Do ur thing in FoodList.
 */

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

    public TextView menuName;
    public ImageView menuImg;

    private ItemClickListener itemClickListener;

    public MenuViewHolder(@NonNull View itemView) {
        super(itemView);
        menuName = (TextView) itemView.findViewById(R.id.menuName);
        menuImg = (ImageView) itemView.findViewById(R.id.menuImg);
        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select an action");
        menu.add(0, 0, getAdapterPosition(), Common.UPDATE);
        menu.add(0, 1, getAdapterPosition(), Common.DELETE);
    }
}
