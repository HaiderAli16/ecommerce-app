package com.example.ecommerce.Buyer;

import android.content.Intent;
import android.os.Bundle;

import com.example.ecommerce.Admin.AdminMatainActivity;
import com.example.ecommerce.Model.Products;
import com.example.ecommerce.Prevalent.Prevalent;
import com.example.ecommerce.R;
import com.example.ecommerce.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.ViewGroup;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager  layoutManager;

    String type = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle!=null)
        {
            type = getIntent().getExtras().get("Admin").toString();
        }

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");

        Paper.init(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                if (!type.equals("Admin"))
                {
                    Intent intent = new Intent(HomeActivity.this, cartActivity.class);
                    startActivity(intent);
                }


            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle
                (this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);


        View headerView = navigationView.getHeaderView(0);
        TextView usernameTextView = headerView.findViewById(R.id.user_profile_name);
        CircleImageView profileImageview = headerView.findViewById(R.id.user_profile_image);

        if (!type.equals("Admin")){
            usernameTextView.setText(Prevalent.currentonlineUser.getName());
            Picasso.get().load(Prevalent.currentonlineUser.getImage()).placeholder(R.drawable.profile).into(profileImageview);
        }

        recyclerView = findViewById(R.id.recycle_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(databaseReference.orderByChild("productState").equalTo("Approved"), Products.class).build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull final Products products) {
                productViewHolder.productname.setText(products.getName());
                productViewHolder.productdescription.setText(products.getDescription());
                productViewHolder.productprice.setText("Price = " + products.getPrice() + "$");

                Picasso.get().load(products.getImage()).into(productViewHolder.imageViewProduct);

                productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (type.equals("Admin"))
                        {
                            Intent intent = new Intent(HomeActivity.this, AdminMatainActivity.class);
                            intent.putExtra("productID", products.getP_id());
                            startActivity(intent);
                        }
                        else {
                            Intent intent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
                            intent.putExtra("productID", products.getP_id());
                            startActivity(intent);
                        }

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
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_cart) {
            // Handle the camera action
            if (!type.equals("Admin"))
            {
                Intent intent = new Intent(HomeActivity.this , cartActivity.class);
                startActivity(intent);
            }
        }
        else if (id == R.id.nav_search) {
            if (!type.equals("Admin"))
            {
                Intent intent = new Intent(HomeActivity.this, SearchProductActivity.class);
                startActivity(intent);
            }

        }
        else if (id == R.id.nav_category) {
            if (!type.equals("Admin"))
            {

            }

        }
        else if (id == R.id.nav_settings) {
            if (!type.equals("Admin"))
            {
                Intent intent= new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
            }

        }
        else if (id == R.id.nav_logout) {
            if (!type.equals("Admin"))
            {
                Paper.book().destroy();
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }


        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
