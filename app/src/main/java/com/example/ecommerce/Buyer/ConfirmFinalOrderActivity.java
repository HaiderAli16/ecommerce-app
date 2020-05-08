package com.example.ecommerce.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ecommerce.Prevalent.Prevalent;
import com.example.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {


    EditText name, phone, address, city;
    Button confrimOrderBtn;

    String totalAmount= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        totalAmount = getIntent().getStringExtra("Total Price");

        confrimOrderBtn = findViewById(R.id.confirm_final_orderBtn);
        name = findViewById(R.id.shipment_name);
        phone = findViewById(R.id.shipment_phone);
        address = findViewById(R.id.shipment_address);
        city = findViewById(R.id.shipment_city);


    }

    private void confirmOrder() {

        Calendar calfordate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        final String savecurrentdate = currentDate.format(calfordate.getTime());

        SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss a");
        final String savecurrenttime = currentDate.format(calfordate.getTime());

        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentonlineUser.getPhone());

        HashMap<String, Object> orderHash = new HashMap<>();
        orderHash.put("totalAmount", totalAmount);
        orderHash.put("name", name.getText().toString());
        orderHash.put("phone", phone.getText().toString());
        orderHash.put("address", address.getText().toString());
        orderHash.put("city", city.getText().toString());
        orderHash.put("date", savecurrentdate);
        orderHash.put("time" , savecurrenttime);
        orderHash.put("state","not shipped");

        orderRef.updateChildren(orderHash).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference().child("Cart List")
                            .child("User View").child(Prevalent.currentonlineUser.getPhone()).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(ConfirmFinalOrderActivity.this, "Final order has been placed successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });

    }

    public void orderConfirmed(View view) {
        if (TextUtils.isEmpty(name.getText().toString()) || TextUtils.isEmpty(phone.getText().toString()) || TextUtils.isEmpty(address.getText().toString()) || TextUtils.isEmpty(city.getText().toString())){
            Toast.makeText(ConfirmFinalOrderActivity.this, "Pkease Fill Every Field . .!", Toast.LENGTH_SHORT).show();
        }
        else {
            confirmOrder();
        }
    }
}
