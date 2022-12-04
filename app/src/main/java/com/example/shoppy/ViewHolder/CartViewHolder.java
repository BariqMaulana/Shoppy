package com.example.shoppy.ViewHolder;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppy.Interface.ItemClickListener;
import com.example.shoppy.Model.Cart;
import com.example.shoppy.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView tvProductName, tvProductPrice, tvProductQuantity;
    public Button btnDelete;

    ItemClickListener itemClickListener;

    public CartViewHolder(View itemView) {
        super(itemView);

        tvProductName = itemView.findViewById(R.id.cart_productName);
        tvProductPrice = itemView.findViewById(R.id.cart_productPrice);
        tvProductQuantity = itemView.findViewById(R.id.cart_productQuantity);

        btnDelete = itemView.findViewById(R.id.deleteBtn);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}
