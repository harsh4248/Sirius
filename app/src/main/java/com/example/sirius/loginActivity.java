package com.example.sirius;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class loginActivity extends AppCompatActivity implements View.OnClickListener {

    TextView createAccountTextView;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        firebaseAuth= FirebaseAuth.getInstance();

        createAccountTextView = findViewById(R.id.textViewCreateAccount);
        createAccountTextView.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.textViewCreateAccount) {
            Intent intent = new Intent(loginActivity.this,signupActivity.class);
            startActivity(intent);
        }
    }
}