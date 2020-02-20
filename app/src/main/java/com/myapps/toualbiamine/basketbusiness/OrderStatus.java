package com.myapps.toualbiamine.basketbusiness;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapps.toualbiamine.basketbusiness.Common.Common;
import com.myapps.toualbiamine.basketbusiness.Interface.ItemClickListener;
import com.myapps.toualbiamine.basketbusiness.Model.Order;
import com.myapps.toualbiamine.basketbusiness.Model.Request;
import com.myapps.toualbiamine.basketbusiness.ViewHolder.OrderViewHolder;

import java.util.ArrayList;
import java.util.List;

public class OrderStatus extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        //Initialize the Firebase database.
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerOrder);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        loadOrders();
    }

    /*private void loadOrders() {
        requests.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Request r = dataSnapshot.getValue(Request.class);
                Log.e("TAG", "Request retrieved -> " + r.getName() + " key = " + dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/

      private void loadOrders() {
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests.orderByChild("restaurantID").equalTo(Common.currentUser.getRestaurantID())
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder orderViewHolder, Request request, int i) {
                orderViewHolder.orderID.setText(request.getName());
                orderViewHolder.orderStatus.setText(Common.convertCodeToStatus(request.getStatus()));
                String orderMenu = "";

                List<Order> orders = request.getOrder();
                for(int pos=0; pos<orders.size(); pos++) {
                    String currentMenu = orders.get(pos).getMenuName();
                    String currentQuantity = orders.get(pos).getQuantity();
                    //orderMenu += currentMenu + " x" + currentQuantity + " | ";
                    if(pos != 0) {
                        orderMenu += "\n";
                    }
                    orderMenu += currentQuantity + " " + currentMenu;
                }

                orderViewHolder.orderMenu.setText(orderMenu);
                orderViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Empty -> just to fix crash because of context menu
                    }
                });
            }
        };

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getIntent().equals(Common.UPDATE))
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        else if(item.getIntent().equals(Common.DELETE))
            deleteOrder(adapter.getRef(item.getOrder()).getKey());
        return super.onContextItemSelected(item);
    }

    private void showUpdateDialog(String key, Request item) {

    }

    private void deleteOrder(String key) {

    }
}
