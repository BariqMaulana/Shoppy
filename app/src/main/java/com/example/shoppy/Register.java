package com.example.shoppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.shoppy.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class Register extends AppCompatActivity {

    TextInputLayout tiName, tiPhoneNo, tiEmail,tiPass;
    Button btnLogin, btnRegister;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tiName = findViewById(R.id.reg_name);
        tiPhoneNo = findViewById(R.id.reg_phoneNo);
        tiEmail = findViewById(R.id.reg_email);
        tiPass = findViewById(R.id.reg_password);

        btnLogin = findViewById(R.id.login_btn);
        btnRegister = findViewById(R.id.regBtn);

        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent (Register.this, Login.class);
                startActivity(i);
            }
        });


    }


    private void createUser(){
        String email = tiEmail.getEditText().getText().toString();
        String password = tiPass.getEditText().getText().toString();
        String name = tiName.getEditText().getText().toString();
        String phoneNo = tiPhoneNo.getEditText().getText().toString();

        if (TextUtils.isEmpty(email)){
            tiEmail.setError("Email cannot be empty!");
            tiEmail.requestFocus();
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            tiEmail.setError("Please provide valid email!");
            tiEmail.requestFocus();
            return;
        } else if (TextUtils.isEmpty(password)){
            tiPass.setError("Password cannot be empty!");
            tiPass.requestFocus();
            return;
        } else if (password.length() < 6){
            tiPass.setError("Password length should be above 5 characters!");
            tiPass.requestFocus();
            return;
        } else if (TextUtils.isEmpty(name)){
            tiName.setError("Username cannot be empty!");
            tiName.requestFocus();
            return;
        } else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        User user = new User(name, email, phoneNo);

                        FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser())
                                .getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    Toast.makeText(Register.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Register.this, Login.class));
                                } else {
                                    Toast.makeText(Register.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } else{
                        Toast.makeText(Register.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}