package com.example.ecommerce.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.ecommerce.Model.AdminOrders;
import com.example.ecommerce.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminNewOrdersActivity extends AppCompatActivity {

    RecyclerView orderlist;
    DatabaseReference orderRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);

        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        orderlist = findViewById(R.id.ordersList);
        orderlist.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<AdminOrders> options = new FirebaseRecyclerOptions
                .Builder<AdminOrders>().setQuery(orderRef,AdminOrders.class).build();
        FirebaseRecyclerAdapter<AdminOrders, AdminOrderViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrderViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrderViewHolder adminOrderViewHolder, final int i, @NonNull final AdminOrders adminOrders) {
                        adminOrderViewHolder.username.setText("Name : " + adminOrders.getName());
                        adminOrderViewHolder.userphone.setText("Phone : " + adminOrders.getPhone());
                        adminOrderViewHolder.usertotalprice.setText("Amount : $" + adminOrders.getTotalAmount());
                        adminOrderViewHolder.usershipmentaddress.setText("Address : " + adminOrders.getAddress() + " , " + adminOrders.getCity());
                        adminOrderViewHolder.userdatetime.setText("Date : " + adminOrders.getDate() + " " + adminOrders.getTime());

                        adminOrderViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]
                                        {
                                          "Yes",
                                          "No"
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrdersActivity.this);
                                builder.setTitle("Have You Shipped This Project ?");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0){
                                            String uid = getRef(i).getKey();
                                            RemoveOrder(uid);
                                        }
                                        else if (which == 1){
                                            finish();
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });

                        adminOrderViewHolder.showorderbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String uid = getRef(i).getKey();

                                Intent intent = new Intent(AdminNewOrdersActivity.this, AdminUserProductsActivity.class);
                                intent.putExtra("uid",uid);
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public AdminOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout,parent,false);
                        return new AdminOrderViewHolder(view);
                    }
                };
        orderlist.setAdapter(adapter);
        adapter.startListening();
    }




    public static class AdminOrderViewHolder extends RecyclerView.ViewHolder{
         public TextView username, userphone, usertotalprice, userdatetime, usershipmentaddress;
         public Button showorderbtn;
        public AdminOrderViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.orderuser_name);
            userphone = itemView.findViewById(R.id.orderphone_number);
            usertotalprice = itemView.findViewById(R.id.order_totalprice);
            userdatetime = itemView.findViewById(R.id.order_date_time);
            usershipmentaddress = itemView.findViewById(R.id.order_city_address);
            showorderbtn = itemView.findViewById(R.id.show_all_products_btn);

        }
    }


    private void RemoveOrder(String uid) {
        orderRef.child(uid).removeValue();
    }

}
