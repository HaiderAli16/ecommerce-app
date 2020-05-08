package com.example.ecommerce.Sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.drm.ProcessedData;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ecommerce.Buyer.MainActivity;
import com.example.ecommerce.Buyer.RegisterActivity;
import com.example.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SellerRegistrationActivity extends AppCompatActivity {

    Button alreadyHaveAccount, registerBtn;
    EditText name, password, address, phone, email;
    FirebaseAuth mAuth;

    String Name, Phone, Address, Email, Pass;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_registration);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        alreadyHaveAccount = findViewById(R.id.seller_alreadyHaveAnAccount);
        name = findViewById(R.id.seller_name);
        password = findViewById(R.id.seller_password);
        address = findViewById(R.id.seller_address);
        phone = findViewById(R.id.seller_phone);
        email = findViewById(R.id.seller_email);
        registerBtn = findViewById(R.id.seller_register_btn);


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerSeller();
            }
        });


        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(SellerRegistrationActivity.this, SellerLoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerSeller() {
        Name = name.getText().toString();
        Pass = password.getText().toString();
        Address = address.getText().toString();
        Phone = phone.getText().toString();
        Email = email.getText().toString();
        
        if (TextUtils.isEmpty(Name) || TextUtils.isEmpty(Pass) || TextUtils.isEmpty(Address) || TextUtils.isEmpty(Phone) ||TextUtils.isEmpty(Email))
        {
            Toast.makeText(this, "Please Fill Every Field", Toast.LENGTH_SHORT).show();
        }
        else
        {
            progressDialog.setTitle("Creating Seller Account");
            progressDialog.setMessage("Please Wait");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();


            mAuth.createUserWithEmailAndPassword(Email,Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                        String sid = mAuth.getCurrentUser().getUid();
                        HashMap<String, Object> sellerMap = new HashMap<>();
                        sellerMap.put("sid", sid);
                        sellerMap.put("phone", Phone);
                        sellerMap.put("email", Email);
                        sellerMap.put("address", Address);
                        sellerMap.put("name", Name);
                        sellerMap.put("password", Pass);


                        rootRef.child("Sellers").child(sid).updateChildren(sellerMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        progressDialog.dismiss();
                                        Toast.makeText(SellerRegistrationActivity.this, "You are Registered Successfully", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(SellerRegistrationActivity.this, SellerHomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else {
                                        finish();
                                    }


                            }
                        });

                    }
                }
            });
        }
    }
}
