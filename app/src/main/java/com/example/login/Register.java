package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class Register extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;

    private TextView textView2;
    private EditText registerName, registerEmail, registerPassword;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);

        registerName = (EditText) findViewById(R.id.registerName);
        registerEmail = (EditText) findViewById(R.id.registerEmail);
        registerPassword = (EditText) findViewById(R.id.registerPassword);
    }

    @Override
    public void onClick(View view) {
        registerUser();
    }

    private void registerUser() {
        String email = registerEmail.getText().toString().trim();
        String name = registerName.getText().toString().trim();
        String password = registerPassword.getText().toString().trim();

        if(name.isEmpty()){
            registerName.setError("Name is required");
            registerName.requestFocus();
            return;
        }
        if(email.isEmpty()){
            registerEmail.setError("Email is required");
            registerEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            registerEmail.setError("Please provide valid email");
            registerEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            registerPassword.setError("Password is required");
            registerPassword.requestFocus();
            return;
        }
        if(password.length() < 6){
            registerPassword.setError("Much more than 6 characters");
            registerPassword.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                 if(task.isSuccessful()){
                     User user = new User(name, email);
                     FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                             if(task.isSuccessful()){
                                 Toast.makeText(Register.this, "Account created successfully",Toast.LENGTH_LONG).show();
                             }
                             else{
                                 Toast.makeText(Register.this, "Cannot create new account! Try again",Toast.LENGTH_LONG).show();
                             }
                         }
                     });
                 }
                 else{
                     Toast.makeText(Register.this, "Cannot create new account",Toast.LENGTH_LONG).show();
                 }
            }
        });
    }
}