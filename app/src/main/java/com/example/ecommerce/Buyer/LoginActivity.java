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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ecommerce.Admin.AdminHomeActivity;
import com.example.ecommerce.Sellers.SellerProductCategoryActivity;
import com.example.ecommerce.Model.Users;
import com.example.ecommerce.Prevalent.Prevalent;
import com.example.ecommerce.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    EditText inputusername, inputpassword;
    Button loginbtn;
    TextView adminLink, notadminLink, forgotPassword;


    private CheckBox checkBoxRememberMe;


    ProgressDialog progressDialog;
    AlertDialog.Builder builder;

    private String parentDB = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputusername = findViewById(R.id.login_phonenumber);
        forgotPassword = findViewById(R.id.forgetPassword_login);
        inputpassword = findViewById(R.id.login_password);
        loginbtn = findViewById(R.id.login_loginbtn);
        builder = new AlertDialog.Builder(this);
        progressDialog = new ProgressDialog(this);
        checkBoxRememberMe = findViewById(R.id.checkbox_rememberme_login);
        Paper.init(this);
        adminLink = findViewById(R.id.admin_login);
        notadminLink = findViewById(R.id.notadmin_login);


        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                intent.putExtra("check", "login");
                startActivity(intent);
            }
        });


        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });


        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginbtn.setText("Login As Admin");
                adminLink.setVisibility(View.INVISIBLE);
                notadminLink.setVisibility(View.VISIBLE);
                parentDB = "Admins";
            }
        });

        notadminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginbtn.setText("Login");
                adminLink.setVisibility(View.VISIBLE);
                notadminLink.setVisibility(View.INVISIBLE);
                parentDB = "Users";
            }
        });
    }

    private void LoginUser() {
        String phnumber, password;
        phnumber = inputusername.getText().toString();
        password = inputpassword.getText().toString();

        if (TextUtils.isEmpty(phnumber) || TextUtils.isEmpty(password)){
            String tile = "Error", msg = "Please Fill Every Field . . !";
            showBuilderMsg(tile,msg);
        }
        else {
            progressDialog.setTitle("Login Account");
            progressDialog.setMessage("Loging . . .");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            AllowAccessToAcount(phnumber, password);
        }
    }

    private void AllowAccessToAcount(final String phnumber, final String password) {

        if (checkBoxRememberMe.isChecked()){
            Paper.book().write(Prevalent.UserPhoneKey, phnumber);
            Paper.book().write(Prevalent.UserPasswordKey, password);
        }


        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(parentDB).child(phnumber).exists()){
                    Users usersData = dataSnapshot.child(parentDB).child(phnumber).getValue(Users.class);

                    if (usersData.getPhone().equals(phnumber) && usersData.getPassword().equals(password)){
                        //login successful
                        if (parentDB.equals("Admins")){
                            progressDialog.dismiss();
                            inputpassword.setText("");
                            inputusername.setText("");
                            Intent intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                            startActivity(intent);
                        }
                        else if (parentDB.equals("Users")){

                            progressDialog.dismiss();
                            inputpassword.setText("");
                            inputusername.setText("");
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            Prevalent.currentonlineUser = usersData;
                            startActivity(intent);
                        }
                    }
                }
                else {
                    String title = "Error", msg = "Account does'nt exist . . !";
                    showBuilderMsg(title, msg);
                    progressDialog.dismiss();
                    inputpassword.setText("");
                    inputusername.setText("");
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