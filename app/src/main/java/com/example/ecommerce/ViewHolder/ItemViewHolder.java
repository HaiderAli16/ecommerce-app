package com.example.ecommerce.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.Interface.ItemClickListner;
import com.example.ecommerce.R;


public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView productname, productdescription, productprice, productstatus;
    public ImageView imageViewProduct;
    public ItemClickListner listner;

    public void setItemClickListner(ItemClickListner listner){
        this.listner = listner;
    }

    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);

        imageViewProduct = itemView.findViewById(R.id.product_seller_Image);
        productname = itemView.findViewById(R.id.product_seller_Name);
        productdescription = itemView.findViewById(R.id.product_seller_Description);
        productprice = itemView.findViewById(R.id.product_seller_Price);
        productstatus = itemView.findViewById(R.id.product_satus);

    }

    @Override
    public void onClick(View v) {
        listner.onClick(v, getAdapterPosition(), false);
    }
}

