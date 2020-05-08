package com.example.ecommerce.Sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ecommerce.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class SellerAddNewProductActivity extends AppCompatActivity {

    String categoryname, description, p_name, p_price, saveCurrentDate, saveCurrentTime, productRandomKey, downloadImageurl;
    Button AddnewProduct;
    EditText productname, productdescription, productprice;
    ImageView imageProduct;
    private StorageReference productImageRef;
    private Uri imageUri;
    private DatabaseReference productRef , sellerRef;
    private static final int GalleryPick = 1;
    private ProgressDialog progressDialog;

    String sName, sPhone, sAddress, sEmail, sID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_addnew_product);

        imageProduct = findViewById(R.id.selectImageofProduct);
        AddnewProduct = findViewById(R.id.add_new_product);
        productname= findViewById(R.id.productName);
        productdescription = findViewById(R.id.productDescription);
        productprice = findViewById(R.id.productPrice);

        progressDialog = new ProgressDialog(this);
        categoryname = getIntent().getExtras().get("category").toString();

        productImageRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        sellerRef = FirebaseDatabase.getInstance().getReference().child("Sellers");

        imageProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        AddnewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductData();
            }
        });


        sellerRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    sName =dataSnapshot.child("name").getValue().toString();
                    sPhone =dataSnapshot.child("phone").getValue().toString();
                    sAddress =dataSnapshot.child("address").getValue().toString();
                    sEmail =dataSnapshot.child("email").getValue().toString();
                    sID =dataSnapshot.child("sid").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GalleryPick && resultCode == RESULT_OK && data!=null){
            imageUri = data.getData();
            imageProduct.setImageURI(imageUri);
        }
    }

    private void ValidateProductData() {
        description = productdescription.getText().toString();
        p_price = productprice.getText().toString();
        p_name = productname.getText().toString();

        if(imageUri == null || TextUtils.isEmpty(description) || TextUtils.isEmpty(p_name) || TextUtils.isEmpty(p_price)){
            Toast.makeText(this, "Please Fill Every Field and set Image also . . !", Toast.LENGTH_SHORT).show();
        }
        else {
            StoreProductInformation();
        }
    }

    private void StoreProductInformation() {
        progressDialog.setTitle("Addding New Product");
        progressDialog.setMessage("Uploading Your Data, Please Wait . . .");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentData = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentData.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filepath = productImageRef.child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");
        final UploadTask uploadTask = filepath.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(SellerAddNewProductActivity.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(SellerAddNewProductActivity.this, "Image uploaded successful", Toast.LENGTH_SHORT).show();
                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                            throw task.getException();
                        }
                        else {
                            downloadImageurl = filepath.getDownloadUrl().toString();
                            return filepath.getDownloadUrl();
                        }
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            downloadImageurl = task.getResult().toString();
                            Toast.makeText(SellerAddNewProductActivity.this, "getting product image successfully", Toast.LENGTH_SHORT).show();
                            saveProductToDataBase();
                        }
                    }
                });
            }
        });
    }

    private void saveProductToDataBase() {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("p_id" , productRandomKey);
        productMap.put("date" , saveCurrentDate);
        productMap.put("time" , saveCurrentTime);
        productMap.put("description" , description);
        productMap.put("image", downloadImageurl);
        productMap.put("category", categoryname);
        productMap.put("price",p_price);
        productMap.put("name" ,p_name);

        productMap.put("sellerName" ,sName);
        productMap.put("sellerAddress" ,sAddress);
        productMap.put("sellerPhone" ,sPhone);
        productMap.put("sellerEmail" ,sEmail);
        productMap.put("sid" ,sID);
        productMap.put("productState" ,"Not Approved");

        productRef.child(productRandomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent(SellerAddNewProductActivity.this, SellerHomeActivity.class);
                    startActivity(intent);
                    progressDialog.dismiss();
                    productname.setText("");
                    productdescription.setText("");
                    productprice.setText("");
                    Toast.makeText(SellerAddNewProductActivity.this, "Product is Added", Toast.LENGTH_SHORT).show();
                }
                else {
                    progressDialog.dismiss();
                    String error = task.getException().toString();
                    Toast.makeText(SellerAddNewProductActivity.this, "Error : "+error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
