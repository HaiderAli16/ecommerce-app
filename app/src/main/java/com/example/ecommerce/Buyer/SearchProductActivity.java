package com.example.ecommerce.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.ecommerce.Model.Products;
import com.example.ecommerce.R;
import com.example.ecommerce.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rey.material.widget.EditText;
import com.squareup.picasso.Picasso;

public class SearchProductActivity extends AppCompatActivity {

    Button searchbtn;
    EditText searchData;
    RecyclerView recyclerView;
    String searchInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);

        searchData = findViewById(R.id.serch_product_name);
        searchbtn = findViewById(R.id.searchbtnresult);
        recyclerView = findViewById(R.id.searchlist_recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchProductActivity.this));
        
        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(searchData.getText().toString())){
                    Toast.makeText(SearchProductActivity.this, "Please Fill The Field", Toast.LENGTH_SHORT).show();
                }
                else {
                    searchInput = searchData.getText().toString();
                    onStart();
                }
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");

        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(reference.orderByChild("name").startAt(searchInput), Products.class).build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull final Products products) {
                productViewHolder.productname.setText(products.getName());
                productViewHolder.productdescription.setText(products.getDescription());
                productViewHolder.productprice.setText("Price = " + products.getPrice() + "$");

                Picasso.get().load(products.getImage()).into(productViewHolder.imageViewProduct);

                productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SearchProductActivity.this, ProductDetailsActivity.class);
                        intent.putExtra("productID", products.getP_id());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                ProductViewHolder holder = new ProductViewHolder(view);
                return holder;
            }
        } ;
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
