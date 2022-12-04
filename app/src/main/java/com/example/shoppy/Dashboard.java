package com.example.shoppy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Dashboard extends AppCompatActivity implements View.OnClickListener {

    CardView scanner, cart, receipt, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //define cards
        scanner =  findViewById(R.id.toScan);
        cart =  findViewById(R.id.toCart);
        receipt =  findViewById(R.id.toReceipts);

        scanner.setOnClickListener(this);
        cart.setOnClickListener(this);
        receipt.setOnClickListener(this);

        logout = (CardView)findViewById(R.id.toLogout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }

        });

    }

    @Override
    public void onClick(View view) {

        Intent i;

        switch (view.getId()){

            case R.id.toScan : i = new Intent(this, ProductDetails.class);startActivity(i);break;
            case R.id.toCart : i = new Intent(this, CartActivity.class);startActivity(i); break;
            case R.id.toReceipts : i = new Intent(this, PurchaseHistory.class);startActivity(i); break;
            default:break;
        }
    }
}