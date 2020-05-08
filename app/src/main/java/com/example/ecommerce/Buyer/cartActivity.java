package com.example.ecommerce.Buyer;

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
import android.widget.Toast;

import com.example.ecommerce.Model.Cart;
import com.example.ecommerce.Prevalent.Prevalent;
import com.example.ecommerce.R;
import com.example.ecommerce.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class cartActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    Button nextbtn;
    TextView textTotalNumber, txtmsgfinalOrder;

    int totalcost = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cartList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        nextbtn = findViewById(R.id.next_processBtn);
        textTotalNumber = findViewById(R.id.price);
        txtmsgfinalOrder = findViewById(R.id.orderFinalmsg);

        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent intent = new Intent(cartActivity.this, ConfirmFinalOrderActivity.class);
                intent.putExtra("Total Price" , String.valueOf(totalcost));
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        checkOrderstate();


        final DatabaseReference cartref = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>().setQuery(cartref.child("User View")
                .child(Prevalent.currentonlineUser.getPhone()).child("Products"),Cart.class).build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull final Cart cart) {
                cartViewHolder.txtProductquantity.setText("Quantity : " + cart.getQuantity());
                cartViewHolder.txtProductprice.setText("Price : $" + cart.getPrice() );
                cartViewHolder.txtProductname.setText("Product Name : " + cart.getPname());

                int oneProductPrice = ((Integer.valueOf(cart.getPrice())) * Integer.valueOf(cart.getQuantity())) ;
                totalcost = totalcost + oneProductPrice;

                textTotalNumber.setText("Total Price = $" + String.valueOf(totalcost));

                cartViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence option[] = new CharSequence[]
                                {
                                  "Edit", "Remove"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(cartActivity.this);
                        builder.setTitle("Product Options:");
                        builder.setItems(option, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0){
                                    Intent intent = new Intent(cartActivity.this, ProductDetailsActivity.class);
                                    intent.putExtra("productID", cart.getPid());
                                    startActivity(intent);
                                }
                                else if (which == 1){
                                    cartref.child("User View").child(Prevalent.currentonlineUser.getPhone()).child("Products")
                                            .child(cart.getPid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(cartActivity.this, "Item Removedx", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(cartActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                            }    
                                        }
                                    });
                                }
                            }

                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    private void checkOrderstate(){
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentonlineUser.getPhone());
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String shippingState = dataSnapshot.child("state").getValue().toString();
                    String username = dataSnapshot.child("name").getValue().toString();
                    if (shippingState.equals("shipped")){
                        textTotalNumber.setText("Dear "+username+",\n order is shipped successfuly.");
                        recyclerView.setVisibility(View.GONE);
                        txtmsgfinalOrder.setVisibility(View.VISIBLE);
                        nextbtn.setVisibility(View.GONE);
                        Toast.makeText(cartActivity.this, "You can purchase more product once your order is placed", Toast.LENGTH_SHORT).show();
                    }
                    else if (shippingState.equals("not shipped")){
                        textTotalNumber.setText("shiping state = not shipped");
                        recyclerView.setVisibility(View.GONE);
                        txtmsgfinalOrder.setText("Admin will soon aprove your order.Thankyou");
                        txtmsgfinalOrder.setVisibility(View.VISIBLE);
                        nextbtn.setVisibility(View.GONE);
                        Toast.makeText(cartActivity.this, "You can purchase more product once your order is placed", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
