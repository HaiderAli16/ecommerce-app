package com.example.ecommerce.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.Prevalent.Prevalent;
import com.example.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ResetPasswordActivity extends AppCompatActivity {

    String check = "";
    TextView pagetitle, titleQuestions;
    EditText phonenumber, question1, question2;
    Button VerifyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        check = getIntent().getStringExtra("check");
        pagetitle = findViewById(R.id.reset_pass);
        titleQuestions = findViewById(R.id.title_questions);
        phonenumber = findViewById(R.id.findPhonenumber);
        question1 = findViewById(R.id.question_1);
        question2 = findViewById(R.id.question_2);
        VerifyButton = findViewById(R.id.btn);
    }

    @Override
    protected void onStart() {
        super.onStart();

        phonenumber.setVisibility(View.GONE);
        if (check.equals("settings"))
        {
            pagetitle.setText("Set Questions");
            titleQuestions.setText("Please Give The Answers OF The Following Questions");
            VerifyButton.setText("SET");

            displayPreviousAnswers();

            VerifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setAnswers();
                }
            });
        }
        else if (check.equals("login"))
        {
            phonenumber.setVisibility(View.VISIBLE);

            VerifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verifyUser();
                }
            });
        }
    }



    private void setAnswers()
    {
        String answer01 = question1.getText().toString().toLowerCase();
        String answer02 = question2.getText().toString().toLowerCase();
        if (TextUtils.isEmpty(answer01) || TextUtils.isEmpty(answer02))
        {
            Toast.makeText(ResetPasswordActivity.this, "Please Answer Both Questions", Toast.LENGTH_SHORT).show();
        }
        else {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                    .getReference().child("Users").child(Prevalent.currentonlineUser.getPhone());
            HashMap<String, Object> userData =  new HashMap<>();
            userData.put("answer1" ,answer01);
            userData.put("answer2", answer02);

            databaseReference.child("Security Questions").updateChildren(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(ResetPasswordActivity.this, "You have answer the security questions successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ResetPasswordActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }

    }


    private void displayPreviousAnswers(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference().child("Users").child(Prevalent.currentonlineUser.getPhone());
        databaseReference.child("Security Questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    String answer1 = dataSnapshot.child("answer1").getValue().toString();
                    String answer2 = dataSnapshot.child("answer2").getValue().toString();
                    question1.setText(answer1);
                    question2.setText(answer2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void verifyUser(){
        final String phone = phonenumber.getText().toString();
        final String answer01 = question1.getText().toString().toLowerCase();
        final String answer02 = question2.getText().toString().toLowerCase();

        if (!phone.equals("") && !answer01.equals("") && !answer02.equals(""))
        {
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                    .getReference().child("Users").child(phone);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                    {
                        String mPhone = dataSnapshot.child("phone").getValue().toString();
                        if (dataSnapshot.hasChild("Security Questions")){

                            String ans1 = dataSnapshot.child("Security Questions").child("answer1").getValue().toString();
                            String ans2 = dataSnapshot.child("Security Questions").child("answer2").getValue().toString();
                            if (!ans1.equals(answer01))
                            {
                                Toast.makeText(ResetPasswordActivity.this, "Your Answer 1st is incorrect", Toast.LENGTH_SHORT).show();
                            }
                            else if (!ans2.equals(answer02)){
                                Toast.makeText(ResetPasswordActivity.this, "Your Answer 2st is incorrect", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
                                builder.setTitle("New Password");
                                final EditText newPassword = new EditText(ResetPasswordActivity.this);
                                newPassword.setHint("Write Your New Password ");
                                builder.setView(newPassword);
                                builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (!newPassword.getText().toString().equals(""))
                                        {
                                            databaseReference.child("password").setValue(newPassword.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful())
                                                            {
                                                                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                                                startActivity(intent);
                                                                Toast.makeText(ResetPasswordActivity.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                        else {
                                            Toast.makeText(ResetPasswordActivity.this, "Write The Password", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                builder.show();
                            }

                        }
                        else {
                            Toast.makeText(ResetPasswordActivity.this, "You have not set the security questins", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(ResetPasswordActivity.this, "This phone number Not Exists", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            Toast.makeText(this, "Please Fill Every Field", Toast.LENGTH_SHORT).show();
        }
    }
}
