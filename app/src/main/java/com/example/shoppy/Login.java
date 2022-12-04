package com.example.shoppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    TextInputLayout tiEmail, tiPass;
    Button btnLogin, btnRegister, btnForgotPass;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tiEmail = findViewById(R.id.reg_email);
        tiPass = findViewById(R.id.reg_password);

        btnLogin = findViewById(R.id.loginBtn);
        btnRegister = findViewById(R.id.signup_screen);
        btnForgotPass = findViewById(R.id.forgotPass_btn);

        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(){
        String email = tiEmail.getEditText().getText().toString();
        String password = tiPass.getEditText().getText().toString();

        if (TextUtils.isEmpty(email)){
            tiEmail.setError("Email cannot be empty");
            tiEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)){
            tiPass.setError("Password cannot be empty");
            tiPass.requestFocus();
        } else{
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(Login.this, "User logged in successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this, Dashboard.class));
                    } else{
                        Toast.makeText(Login.this, "Login Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}