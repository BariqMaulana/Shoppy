package com.example.shoppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.shoppy.Model.Cart;
import com.example.shoppy.Model.Product;
import com.example.shoppy.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

public class ProductDetails extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference id, order, reference;
    String y, a, userId = "";

    Button btnAddToCart;
    ImageView ivProductImage;
    ElegantNumberButton numberButton;
    TextView tvProductPrice, tvProductName,tvProductID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        database = FirebaseDatabase.getInstance();
        id = database.getReference("Product");
        order = database.getReference("Cart");

        numberButton = findViewById(R.id.number_btn);
        btnAddToCart = findViewById(R.id.add_to_cart_button);

        tvProductPrice = findViewById(R.id.product_price_details);
        tvProductName = findViewById(R.id.product_name_details);
        tvProductID = findViewById(R.id.product_ID);

        ivProductImage = findViewById(R.id.product_image_details);

        scanCode();

        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if (userProfile != null) {
                    a = userProfile.phoneNo;
                    Toast.makeText(ProductDetails.this, "TESTESTEST", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void scanCode(){

        IntentIntegrator integrator = new IntentIntegrator(ProductDetails.this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning Code");
        integrator.initiateScan();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents()!=null){
                y = result.getContents();
                if(!y.isEmpty()){
                    id.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child(y).exists()){
                                id.child(y).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        final Product product = dataSnapshot.getValue(Product.class);

                                        tvProductName.setText(product.getName());
                                        tvProductPrice.setText("Rp. "+product.getPrice());
                                        tvProductID.setText(product.getId());

                                        btnAddToCart.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Cart cart = new Cart(product.getName(),product.getPrice(), numberButton.getNumber(),product.getId());
                                                order.child(a).child(y).setValue(cart);
                                                Toast.makeText(ProductDetails.this,"Item is added",Toast.LENGTH_SHORT).show();

                                                AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetails.this);
                                                builder.setTitle("ITEM IS ADDED");
                                                builder.setPositiveButton("Continue Scanning Products", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        scanCode();
                                                    }
                                                }).setNegativeButton("To Shopping Cart", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        Intent intent = new Intent(ProductDetails.this, CartActivity.class);
                                                        startActivity(intent);
                                                    }
                                                });
                                                AlertDialog dialog = builder.create();
                                                dialog.show();


                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }else{

                                AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetails.this);
                                builder.setMessage("Product cannot be found in database.");
                                builder.setTitle("Scanning Result");
                                builder.setPositiveButton("Scan Again", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        scanCode();
                                    }
                                }).setNegativeButton("Finish", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
            else {
                Toast.makeText(ProductDetails.this, "No Results", Toast.LENGTH_LONG).show();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}