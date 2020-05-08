package com.example.ecommerce.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.ecommerce.Model.Products;
import com.example.ecommerce.Prevalent.Prevalent;
import com.example.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

    FloatingActionButton addtocartbtn;
    ImageView productImage;
    ElegantNumberButton numberofproduct;
    TextView pprice, pdescription, productname;
    String getProductID = null, state = "normal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        addtocartbtn = findViewById(R.id.add_product_cart);
        productImage = findViewById(R.id.product_image_details);
        numberofproduct = findViewById(R.id.number_btn);
        pprice = findViewById(R.id.product_price_details);
        pdescription = findViewById(R.id.product_discription_details);
        productname = findViewById(R.id.product_name_details);

        getProductID = getIntent().getStringExtra("productID");

        getProductDetails(getProductID);

        addtocartbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (state.equals("Order Placed") || state.equals("Order Shipped")){
                    Toast.makeText(ProductDetailsActivity.this, "once your order is shipped", Toast.LENGTH_LONG).show();
                }
                else {
                    addingProductToCartList();
                }
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        checkOrderstate();
    }

    private void addingProductToCartList() {
        String savecurrenttime, savecurrentdate;

        Calendar calfordate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        savecurrentdate = currentDate.format(calfordate.getTime());

        SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss a");
        savecurrenttime = currentDate.format(calfordate.getTime());

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Cart List");
        final HashMap<String, Object> cartHash = new HashMap<>();
        cartHash.put("pid", getProductID);
        cartHash.put("pname", productname.getText().toString());
        cartHash.put("price", pprice.getText().toString());
        cartHash.put("date", savecurrentdate);
        cartHash.put("time", savecurrenttime);
        cartHash.put("quantity", numberofproduct.getNumber());
        cartHash.put("discount" , "");

        databaseReference.child("User View").child(Prevalent.currentonlineUser.getPhone()).child("Products")
                .child(getProductID).updateChildren( cartHash).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    databaseReference.child("Admin View").child(Prevalent.currentonlineUser.getPhone()).child("Products")
                            .child(getProductID).updateChildren( cartHash)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(ProductDetailsActivity.this, "added to cart list", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ProductDetailsActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            });
                }
            }
        });



    }

    private void getProductDetails(String getProductID) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");
        databaseReference.child(getProductID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Products products = dataSnapshot.getValue(Products.class);
                    productname.setText(products.getName());
                    pprice.setText(products.getPrice());
                    pdescription.setText(products.getDescription());
                    Picasso.get().load(products.getImage()).into(productImage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




    private void checkOrderstate(){
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentonlineUser.getPhone());
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String shippingState = dataSnapshot.child("state").getValue().toString();
                    if (shippingState.equals("shipped")){
                       state = "Order Shipped";
                    }
                    else if (shippingState.equals("not shipped")){
                        state = "Order Placed";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}
