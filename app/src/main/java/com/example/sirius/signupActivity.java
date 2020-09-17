package com.example.sirius;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signupActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;
    EditText email,password,passwordConfirm,username;
    String EmailString,passwordString,confirmpasswordString,usernamestring;
    Button signupbutton;
    TextView signintextview;
    ProgressBar showprogressbar;

    private DatabaseReference mdatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_signup);

        email= findViewById(R.id.editTextTextEmailAddress2);
        password= findViewById(R.id.editTextTextPassword2);
        passwordConfirm= findViewById(R.id.editTextTextPassword3);
        username= findViewById(R.id.editTextTextPersonName);
        signupbutton= findViewById(R.id.buttonsignin);
        signintextview= findViewById(R.id.textViewsignin);
        showprogressbar= findViewById(R.id.progressBar);

        showprogressbar.setVisibility(View.INVISIBLE);
        signupbutton.setOnClickListener(this);
        signintextview.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();


        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (password.getRight() - password.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        return true;
                    }
                }
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                return false;
            }
        });

        passwordConfirm.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (passwordConfirm.getRight() - passwordConfirm.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        passwordConfirm.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        return true;
                    }
                }
                passwordConfirm.setTransformationMethod(PasswordTransformationMethod.getInstance());
                return false;
            }
        });






    }

    @Override
    public void onClick(View v) {
        int id= v.getId();

        if(id==R.id.buttonsignin) {
            createAccount();
        }
        if(id==R.id.textViewsignin) {
            Intent intent= new Intent(signupActivity.this,loginActivity.class);
            startActivity(intent);
        }
    }

    private void createAccount() {
        EmailString= email.getText().toString().trim();
        passwordString=password.getText().toString().trim();
        confirmpasswordString=passwordConfirm.getText().toString().trim();
        usernamestring=username.getText().toString().trim();



        if(!passwordString.matches(confirmpasswordString)) {
            passwordConfirm.setError("Doesn't Match");
        }
        else {
            showprogressbar.setVisibility(View.VISIBLE);
            firebaseAuth.createUserWithEmailAndPassword(EmailString, passwordString).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        User user = new User(EmailString,usernamestring);
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                showprogressbar.setVisibility(View.GONE);
                                if(task.isSuccessful()) {
                                    Toast.makeText(signupActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(signupActivity.this,Dashboard.class);
                                    startActivity(intent);
                                }
                                else {
                                    Toast.makeText(signupActivity.this, "Unknown Error Occurred", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });



                    } else {

                        try {
                            throw task.getException();
                        }
                        catch (FirebaseAuthWeakPasswordException e) {

                            Toast.makeText(signupActivity.this, "Create Strong password", Toast.LENGTH_SHORT).show();

                        }
                        catch(FirebaseAuthUserCollisionException e) {
                            Toast.makeText(signupActivity.this, "Email already exist", Toast.LENGTH_SHORT).show();
                        }
                        catch (FirebaseAuthInvalidCredentialsException malformed) {
                            Toast.makeText(signupActivity.this, "Malformed credential", Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e) {

                            Toast.makeText(signupActivity.this, "Unexpected Error occurred", Toast.LENGTH_SHORT).show();
                        }
                        // Toast.makeText(signupActivity.this, "Failed to Create", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
    }
}