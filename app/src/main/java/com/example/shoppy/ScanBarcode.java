package com.example.shoppy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ScanBarcode extends AppCompatActivity {

    Button btnScan, btnCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode);

        btnScan = findViewById(R.id.scanBtn);
        btnCart = findViewById(R.id.cart);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productDetails = new Intent(ScanBarcode.this,ProductDetails.class);
                startActivity(productDetails);

            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cartDetails = new Intent(ScanBarcode.this,CartActivity.class);
                startActivity(cartDetails);

            }
        });
    }
}