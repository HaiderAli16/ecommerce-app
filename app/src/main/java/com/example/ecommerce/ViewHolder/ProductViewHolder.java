package com.example.ecommerce.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.Interface.ItemClickListner;
import com.example.ecommerce.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView productname, productdescription, productprice;
    public ImageView imageViewProduct;
    public ItemClickListner listner;

    public void setItemClickListner(ItemClickListner listner){
        this.listner = listner;
    }

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        imageViewProduct = itemView.findViewById(R.id.productImage);
        productname = itemView.findViewById(R.id.productName);
        productdescription = itemView.findViewById(R.id.productDescription);
        productprice = itemView.findViewById(R.id.productPrice);
    }

    @Override
    public void onClick(View v) {
        listner.onClick(v, getAdapterPosition(), false);
    }
}
