package com.example.shoppy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;


import org.json.JSONException;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class CartActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    Button btnPay, btnClear;
    TextView tvTotalAmount;

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mCart, reference;
    String userId, phone, name, a, cName;

    FirebaseRecyclerAdapter<com.example.shoppy.Model.Cart, CartViewHolder>adapter;

    double totalPrice = 0.00;
    int childCount = 0;
    int m = 0;

    private static final String CLIENT_ID = "AYG1dTwC1gcbvuH-WkKycoG60vklp9SF7DJJIliFD_knl5dncFHp5nCE06hwoTypbwCF4luVygF0HIcV";
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    public static final String EXTRA_NUMBER = "com.example.shoppy.EXTRA_TEXT";
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static PayPalConfiguration config;
    PayPalPayment thingsToBuy;



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
                    name = userProfile.name;
                    phone = userProfile.phoneNo;
                    cName = name;
                    a = phone;
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
                tvTotalAmount.setText("Total Price: Rp. 0.00");
            }
        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                makePayment();
                doPayment();
            }
        });

//        configPayPal();
    }


    private void doPayment() {
        DecimalFormat df = new DecimalFormat("#,###,##0.00");
        Intent intent = new Intent(this,Receipt.class);
        intent.putExtra(EXTRA_NUMBER, df.format(totalPrice));
        intent.putExtra("pID",PIDArray);
        intent.putExtra("pName",PNameArray);
        intent.putExtra("pPrice",PPriceArray);
        intent.putExtra("pQuantity",PQuantityArray);
        intent.putExtra("count",String.valueOf(childCount));
        intent.putExtra("cName",cName);
        intent.putExtra("cPhone",a);
        startActivity(intent);
        mCart.removeValue();

    }

//    private void configPayPal() {
//
//        config = new PayPalConfiguration()
//                .environment(CONFIG_ENVIRONMENT)
//                .clientId(CLIENT_ID)
//                .merchantName("Shoppy Payment")
//                .merchantPrivacyPolicyUri(Uri.parse("https://wwww.example.com/privacy"))
//                .merchantUserAgreementUri(Uri.parse("https://wwww.example.com/legal"));
//
//    }
//
//    private void makePayment() {
//        Intent intent = new Intent(this, PayPalService.class);
//        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
//        startService(intent);
//
//        thingsToBuy = new PayPalPayment(
//                new BigDecimal(String.valueOf(""+ totalPrice)), "MYR", "Payment", PayPalPayment.PAYMENT_INTENT_SALE);
//        Intent payment = new Intent(this, PaymentActivity.class);
//        payment.putExtra(PaymentActivity.EXTRA_PAYMENT, thingsToBuy);
//        payment.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
//        startActivityForResult(payment, REQUEST_CODE_PAYMENT);
//
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_CODE_PAYMENT) {
//            if (resultCode == Activity.RESULT_OK) {
//                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
//
//                if (confirm != null) {
//
//                    try {
//                        System.out.println(confirm.toJSONObject().toString(4));
//                        System.out.println(confirm.getPayment().toJSONObject().toString(4));
//                        Toast.makeText(this, "Payment Successful", Toast.LENGTH_LONG).show();
//
//                        DecimalFormat df = new DecimalFormat("#,###,##0.00");
//                        //priceTotal = df.format(TotalPrice);
//                        //Double priceTotal = Double.parseDouble(String.valueOf(TotalPrice));
//                        Intent intent = new Intent(this,Receipt.class);
//                        intent.putExtra(EXTRA_NUMBER, df.format(totalPrice));
//                        intent.putExtra("pID",PIDArray);
//                        intent.putExtra("pName",PNameArray);
//                        intent.putExtra("pPrice",PPriceArray);
//                        intent.putExtra("pQuantity",PQuantityArray);
//                        intent.putExtra("count",String.valueOf(childCount));
//                        intent.putExtra("cName",cName);
//                        intent.putExtra("cPhone",a);
//                        //m = 0;
//                        startActivity(intent);
//                        mCart.removeValue();
//
//                    } catch (JSONException e) {
//
//                        Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
//
//                    }
//
//
//                }
//
//            } else if (resultCode == Activity.RESULT_CANCELED) {
//                Toast.makeText(this, "Payment has been canceled", Toast.LENGTH_LONG).show();
//            } else if (requestCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
//                Toast.makeText(this, "error occurred", Toast.LENGTH_LONG).show();
//            }
//        } else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
//            if (resultCode == Activity.RESULT_OK) {
//                PayPalAuthorization authorization = data.getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
//                if (authorization != null) {
//                    try {
//                        Log.i("FuturePaymentExample", authorization.toJSONObject().toString(4));
//                        String authorization_code = authorization.getAuthorizationCode();
//                        Log.d("FuturePaymentExample", authorization_code);
//
//                        Log.e("paypal", "future payment code received from PayPal  :" + authorization_code);
//
//                    } catch (JSONException e) {
//                        Toast.makeText(this, "Failure Occurred", Toast.LENGTH_LONG).show();
//                        Log.e("FuturePaymentExample", "an extremely unlikely failure occurred:  ", e);
//
//                    }
//                }
//            } else if (resultCode == Activity.RESULT_CANCELED) {
//                Toast.makeText(this, "payment has been cancelled", Toast.LENGTH_LONG).show();
//                Log.d("FuturePaymentExample", "The user cancelled.");
//            } else if (requestCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
//                Toast.makeText(this, "error occurred", Toast.LENGTH_LONG).show();
//                Log.d("FuturePaymentExample", "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs");
//            }
//        }
//    }

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
                    PQuantityArray[m] = cart.getQuantity();

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