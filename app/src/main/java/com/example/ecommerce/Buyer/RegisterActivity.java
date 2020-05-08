package com.example.ecommerce.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    AlertDialog.Builder builder;

    private EditText name, phoneNumber, password;
    private Button login;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        login = findViewById(R.id.Loginregister);
        name = findViewById(R.id.registername);
        phoneNumber = findViewById(R.id.registerphone);
        password = findViewById(R.id.password);
        builder = new AlertDialog.Builder(this);

        progressDialog = new ProgressDialog(this);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        String Name, Ph, Pass;
        Name = name.getText().toString();
        Ph = phoneNumber.getText().toString();
        Pass = password.getText().toString();

        if (TextUtils.isEmpty(Name) || TextUtils.isEmpty(Ph) || TextUtils.isEmpty(Pass)){
            String tile = "Error", msg = "Please Fill Every Field . . !";
            showBuilderMsg(tile,msg);
        }
        else {
            progressDialog.setTitle("Creating Account");
            progressDialog.setMessage("Please Wait");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            ValidateAndCreateAccount(Name, Ph, Pass);
    }

    }

    private void ValidateAndCreateAccount(final String Name, final String ph, final String pass) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.child("Users").child(ph).exists())){
                    HashMap<String, Object> userData =  new HashMap<>();
                    userData.put("phone" , ph);
                    userData.put("password", pass);
                    userData.put("name", Name);
                    rootRef.child("Users").child(ph).updateChildren(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                progressDialog.dismiss();

                                builder.setMessage("Your Account Has Been Created !")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                startActivity(intent);
                                            }
                                        });

                                AlertDialog alert = builder.create();
                                alert.setTitle("Successful");
                                alert.show();
                            }
                            else {
                                progressDialog.dismiss();
                                String title = "Error", msg = "Network Issue, Try again later . . !";
                                showBuilderMsg(title, msg);

                            }
                        }
                    });
                }
                else {

                    progressDialog.dismiss();
                    String title = "Number " + ph + " already Exist";
                    String msg = "Please try again with another number";
                    showBuilderMsg(title,msg);
                    name.setText("");
                    phoneNumber.setText("");
                    password.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void showBuilderMsg(String title, String msg){
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.setTitle(title);
        alert.show();
    }



}