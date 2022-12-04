package com.example.shoppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shoppy.Interface.ItemClickListener;
import com.example.shoppy.Model.Cart;
import com.example.shoppy.Model.User;
import com.example.shoppy.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

public class CartActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    Button btnPay, btnClear;
    TextView tvTotalAmount;

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mCart, reference;
    String userId, phone, name;

    FirebaseRecyclerAdapter<com.example.shoppy.Model.Cart, CartViewHolder>adapter;

    double totalPrice = 0.00;
    int childCount = 0;
    int m = 0;


    String[] PIDArray = null;
    String[] PNameArray = null;
    String[] PPriceArray = null;
    String[] PQuantityArray = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cart_list);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        btnPay = findViewById(R.id.payBtn);
        btnClear = findViewById(R.id.clearCart);

        tvTotalAmount = findViewById(R.id.total_price);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if (userProfile != null) {
//                    String name = userProfile.name;
//                    String phone = userProfile.phoneNo;
                    name = userProfile.name;
                    phone = userProfile.phoneNo;
                    mCart = mDatabase.getReference("Cart").child(phone);
                    Toast.makeText(CartActivity.this, "String.valueOf(Cart)", Toast.LENGTH_SHORT).show();
                    mCart.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            childCount = (int) snapshot.getChildrenCount();
                            PIDArray = new String[childCount];
                            PNameArray = new String[childCount];
                            PPriceArray = new String[childCount];
                            PQuantityArray = new String[childCount];
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    loadCart();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCart.removeValue();
            }
        });
    }

    private void loadCart() {
        adapter = new FirebaseRecyclerAdapter<com.example.shoppy.Model.Cart,
                CartViewHolder>(com.example.shoppy.Model.Cart.class, R.layout.cart_items_layout, CartViewHolder.class, mCart ) {
            @Override
            protected void populateViewHolder(CartViewHolder cartViewHolder, com.example.shoppy.Model.Cart cart, int i) {
                cartViewHolder.tvProductName.setText(cart.getName());
                cartViewHolder.tvProductPrice.setText("Rp. " + cart.getPrice());
                cartViewHolder.tvProductQuantity.setText(cart.getQuantity());

                Double DProductPrice =(Double.valueOf(cart.getPrice()))* (Double.valueOf(cart.getQuantity()));
                totalPrice = totalPrice +DProductPrice;
                DecimalFormat df2 = new DecimalFormat("#,###,##0.00");

                if (m < childCount) {
                    PIDArray[m] = cart.getId();
                    PNameArray[m] = cart.getName();
                    PPriceArray[m] = cart.getPrice();
                    PQuantityArray[m] = cart.getPrice();

                    ++m;
                }

                tvTotalAmount.setText("Total Price: Rp. " + String.valueOf(df2.format(totalPrice)));

                cartViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent update = new Intent(CartActivity.this, UpdateProduct.class);
                        update.putExtra("ID", cart.getId());
                        update.putExtra("PhoneNo", name);
                        update.putExtra("Quantity", cart.getQuantity());
                        startActivity(update);
                    }
                });
                cartViewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCart.child(cart.getId()).removeValue();
                        Toast.makeText(CartActivity.this, "This item is removed!", Toast.LENGTH_SHORT).show();

                        Double DProductPrice = (Double.valueOf(cart.getPrice())) * (Double.valueOf((cart.getQuantity())));
                        totalPrice  = totalPrice + DProductPrice;
                        DecimalFormat df2 = new DecimalFormat("#,###,##0.00");
                        tvTotalAmount.setText("Total Price: Rp. " + String.valueOf(df2.format(totalPrice)));
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }
}