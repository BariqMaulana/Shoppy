package com.example.shoppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.shoppy.Model.Cart;
import com.example.shoppy.Model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class UpdateProduct extends AppCompatActivity {

    Button btnAddToCart;
    ImageView ivProductImage;
    ElegantNumberButton btnNumber;
    TextView tvProductPrice, tvProductName, tvProductId;

    FirebaseDatabase database;
    DatabaseReference id, order;
    String pID, phoneNo, quantity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);

        btnAddToCart = findViewById(R.id.addToCart_button1);
        ivProductImage = findViewById(R.id.image_details);
        tvProductId = findViewById(R.id.product_ID);
        tvProductName = findViewById(R.id.name_details);
        tvProductPrice = findViewById(R.id.price_details);
        btnNumber = findViewById(R.id.number_Btn1);

        database = FirebaseDatabase.getInstance();
        id = database.getReference("Product");
        order = database.getReference("Cart");
        pID = getIntent().getStringExtra("ID");
        phoneNo = getIntent().getStringExtra("PhoneNo");
        quantity = getIntent().getStringExtra("Quantity");

        id.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                id.child(pID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Product product = snapshot.getValue(Product.class);

//                        Picasso.with(getBaseContext()).load(product.getImage()).into(ivProductImage);
                        tvProductName.setText(product.getName());
                        tvProductPrice.setText("Rp. " + product.getPrice());
                        tvProductId.setText(product.getId());

                        btnNumber.setNumber(quantity);

                        btnAddToCart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Cart cart = new Cart(product.getName(), product.getPrice(), btnNumber.getNumber(), product.getId());
                                order.child(phoneNo).child(pID).setValue(cart);
                                Toast.makeText(UpdateProduct.this, "Item is updated!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(UpdateProduct.this, CartActivity.class);
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}