package com.example.jamiaaty;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText emailEt, passET;
    TextView geustTv;
    Button register_btn,login_btn ;
    CheckBox checkBox;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        emailEt = findViewById(R.id.login_email_et);
        passET = findViewById(R.id.login_password_et);
        register_btn = findViewById(R.id.login_to_signup);
        checkBox = findViewById(R.id.login_checkbox);
        progressBar = findViewById(R.id.progrssbar_login);
        login_btn = findViewById(R.id.button_login);
        mAuth = FirebaseAuth.getInstance();
        geustTv = findViewById(R.id.asgeust_tv);


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    passET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {
                    passET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        geustTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,GeustMainActivity.class);
                startActivity(intent);
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEt.getText().toString();
                String pass = passET.getText().toString();

                if(!TextUtils.isEmpty(email) ||!TextUtils.isEmpty(pass) ){
                        progressBar.setVisibility(View.VISIBLE);
                        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    sendToMain();
                                    progressBar.setVisibility(View.INVISIBLE);
                                }else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(LoginActivity.this, "Error "+error, Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                }else {
                    Toast.makeText(LoginActivity.this,"Please Fill all the blanks",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void sendToMain() {
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            sendToMain();
        }
    }
}