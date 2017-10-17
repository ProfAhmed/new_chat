package com.pro.ahmed.navigationdrawertest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.BtLogin)
    Button BtLogin;
    @BindView(R.id.TvNewUser)
    TextView TvNewUser;
    @BindView(R.id.EtEmailLogin)
    EditText EtEmailLogin;
    @BindView(R.id.EtPasswordLogin)
    EditText EtPasswordLogin;
    //firebase auth object
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    //progress dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        //getting firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        //if the objects getcurrentuser method is not null
        //means user is already logged in
        if (firebaseAuth.getCurrentUser() != null) {
            //close this activity
            databaseReference = FirebaseDatabase.getInstance().getReference("users");
            databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("userState").setValue("online");
            finish();
            //opening profile activity
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        EtEmailLogin.setText(getIntent().getStringExtra("email"));

        BtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });
        TvNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });
    }

    //method for user login
    private void userLogin() {
        String email = EtEmailLogin.getText().toString().trim();
        String password = EtPasswordLogin.getText().toString().trim();


        //checking if email and passwords are empty
        if (TextUtils.isEmpty(email)) {
            EtEmailLogin.setError("Please enter email");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            EtPasswordLogin.setError("Please enter password");
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Logging Please Wait...");
        progressDialog.show();

        //logging in the user
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        //if the task is successfull
                        if (task.isSuccessful()) {
                            //start the profile activity
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(LoginActivity.this, "Email or Password is Wrong ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
