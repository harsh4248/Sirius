package com.example.sirius;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class Dashboard extends AppCompatActivity {

    FloatingActionButton cameraOpenButton;
    FirebaseAuth firebaseAuth;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int i = item.getItemId();

        if(i == R.id.power) {
            FirebaseAuth.getInstance().signOut();

            if(FirebaseAuth.getInstance().getCurrentUser()==null) {
                Toast.makeText(Dashboard.this, "Successful signOut", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Dashboard.this, loginActivity.class);
                startActivity(intent);
            }
            else {
                Toast.makeText(Dashboard.this, "SignOut error", Toast.LENGTH_SHORT).show();
            }

            return true;
        }
        return(super.onOptionsItemSelected(item));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //signout = findViewById(R.id.buttonsignout);
        cameraOpenButton = findViewById(R.id.floatingActionButton);



        firebaseAuth=FirebaseAuth.getInstance();

        cameraOpenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this,Camera.class);
                startActivity(intent);
            }
        });

       /* signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                if(FirebaseAuth.getInstance().getCurrentUser()==null) {
                    Toast.makeText(Dashboard.this, "Successful signOut", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Dashboard.this, loginActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(Dashboard.this, "SignOut error", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }
}