package com.example.ecommerce.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.ecommerce.Buyer.HomeActivity;
import com.example.ecommerce.Buyer.MainActivity;
import com.example.ecommerce.Model.AdminOrders;
import com.example.ecommerce.R;

public class AdminHomeActivity extends AppCompatActivity {

    Button logoutbtn, checkorder, maintainbtn, check_approve_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        logoutbtn = findViewById(R.id.admin_logout);
        checkorder = findViewById(R.id.check_orders_btn);
        maintainbtn = findViewById(R.id.maintain_btn_admincategory);
        check_approve_btn = findViewById(R.id.check_approve_order_btn);


        check_approve_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                       Intent intent= new Intent(AdminHomeActivity.this, AdminCheckNewProductsActivity.class);
                       startActivity(intent);
            }
        });


        maintainbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(AdminHomeActivity.this, HomeActivity.class);
                    intent.putExtra("Admin", "Admin");
                    startActivity(intent);
                }catch (Exception e) {
                    Log.i("Exception" , e.getMessage().toString());
                }
            }
        });


        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        checkorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminNewOrdersActivity.class);
                startActivity(intent);
            }
        });
    }
}
