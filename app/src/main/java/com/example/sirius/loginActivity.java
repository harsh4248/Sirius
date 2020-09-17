package com.example.sirius;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class loginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 1;
    TextView createAccountTextView;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    EditText EmailEditText,passwordEditText;
    String EmailString,PasswordString;
    Button login;
    ProgressBar showprogressBar;
    ImageView googleSignIn;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    String Emailid,username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        firebaseAuth= FirebaseAuth.getInstance();


        createAccountTextView = findViewById(R.id.textViewCreateAccount);
        EmailEditText=findViewById(R.id.editTextTextEmailAddress);
        passwordEditText=findViewById(R.id.editTextTextPassword);
        login=findViewById(R.id.buttonlogin);
        showprogressBar=findViewById(R.id.progressBarlogin);
        googleSignIn=findViewById(R.id.imageViewgoogle);

        showprogressBar.setVisibility(View.INVISIBLE);

        login.setOnClickListener(this);
        createAccountTextView.setOnClickListener(this);
        googleSignIn.setOnClickListener(this);

        passwordEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (passwordEditText.getRight() - passwordEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        return true;
                    }
                }
                passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                return false;
            }

        });

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

        gsc = GoogleSignIn.getClient(this,gso);

    }

    @Override
    protected void onStart() {
        super.onStart();
        //FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if(firebaseAuth.getCurrentUser() !=null) {
            Intent intent = new Intent(this,Dashboard.class);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.textViewCreateAccount) {
            Intent intent = new Intent(loginActivity.this,signupActivity.class);
            startActivity(intent);
        }
        if(id== R.id.buttonlogin) {
            loginMethod();
        }

        if(id==R.id.imageViewgoogle) {
            googleLogin();
        }
    }

    private void loginMethod() {

        EmailString=EmailEditText.getText().toString().trim();
        PasswordString=passwordEditText.getText().toString().trim();
        showprogressBar.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(EmailString,PasswordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                showprogressBar.setVisibility(View.GONE);
                if(task.isSuccessful()) {
                    Toast.makeText(loginActivity.this, "Successfully Login", Toast.LENGTH_SHORT).show();
                    firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                    Intent intent = new Intent(loginActivity.this,Dashboard.class);
                    startActivity(intent);

                }
                else {
                    Toast.makeText(loginActivity.this, "Invalid Credential", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void googleLogin() {
        showprogressBar.setVisibility(View.VISIBLE);
        Intent googleSignInIntent= gsc.getSignInIntent();
        startActivityForResult(googleSignInIntent,RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {

        try {

            GoogleSignInAccount account = task.getResult(ApiException.class);
            Emailid=account.getEmail();
            username=account.getDisplayName();
            firebaseAuthWithGoogle(account.getIdToken());
        }
        catch (ApiException e) {
            Toast.makeText(this, "Error Auth error", Toast.LENGTH_SHORT).show();
        }

    }

    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {
                    User user = new User(Emailid,username);
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            showprogressBar.setVisibility(View.GONE);
                            if(task.isSuccessful()) {
                                Toast.makeText(loginActivity.this, "google signIn successful", Toast.LENGTH_SHORT).show();
                                firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                                Intent intent = new Intent(loginActivity.this,Dashboard.class);
                                startActivity(intent);

                            }
                            else {
                                Toast.makeText(loginActivity.this, "google signIn failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

            }
        });
    }
}