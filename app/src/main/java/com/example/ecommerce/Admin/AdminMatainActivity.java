package com.example.ecommerce.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ecommerce.R;
import com.example.ecommerce.Sellers.SellerProductCategoryActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminMatainActivity extends AppCompatActivity {

    Button applyChanges, deleteProduct;
    EditText changename, changeprice, changedescription;
    ImageView imageView;

    String productID = "";
    DatabaseReference productRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_matain);

        productID = getIntent().getStringExtra("productID");
        productRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productID);

        applyChanges = findViewById(R.id.aplly_changes_button);
        changename  = findViewById(R.id.productName_adminmaintain);
        changeprice = findViewById(R.id.productPrice_adminmaintain);
        changedescription = findViewById(R.id.productDescription_adminmaintain);
        imageView = findViewById(R.id.productImage_adminmaintain);
        deleteProduct = findViewById(R.id.delete_changes_button);

        displaySpeceficProductInfo();


        deleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteThisProduct();
            }
        });


        applyChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applychanges();
            }
        });
    }

    private void deleteThisProduct() {
        productRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(AdminMatainActivity.this, "Data Deleted", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AdminMatainActivity.this, SellerProductCategoryActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void applychanges() {
        String name = changename.getText().toString();
        String description = changedescription.getText().toString();
        String price = changeprice.getText().toString();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(price))
        {
            Toast.makeText(this, "Please Fill Every Filed", Toast.LENGTH_SHORT).show();
        }
        else {
            HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("p_id" , productID);
            productMap.put("description" , description);
            productMap.put("price",price);
            productMap.put("name" ,name);

            productRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(AdminMatainActivity.this, "Changes Applied Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AdminMatainActivity.this, SellerProductCategoryActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }

    private void displaySpeceficProductInfo() {
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String name = dataSnapshot.child("name").getValue().toString();
                    String price = dataSnapshot.child("price").getValue().toString();
                    String description = dataSnapshot.child("description").getValue().toString();
                    String Image = dataSnapshot.child("image").getValue().toString();

                    changename.setText(name);
                    changedescription.setText(description);
                    changeprice.setText(price);
                    Picasso.get().load(Image).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
