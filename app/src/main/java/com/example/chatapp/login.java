package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class login extends AppCompatActivity {
    Button button,geminibutton;
    EditText email,password;

    TextView signuptext;
    FirebaseAuth auth;
    String emailpattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    android.app.ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        auth= FirebaseAuth.getInstance();
        button=findViewById(R.id.logbutton);
        email=findViewById(R.id.editTextEmailLog);
        password=findViewById(R.id.editTextPasslog);
        signuptext=findViewById(R.id.signuplog);
        geminibutton=findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener()
        {@Override
        public void onClick(View v)
        {String Email=email.getText().toString();
            String pass=password.getText().toString();
            if((TextUtils.isEmpty(Email)))
            {progressDialog.dismiss();
                Toast.makeText(login.this,"Enter the email", Toast.LENGTH_SHORT).show();
            }
            else if((TextUtils.isEmpty(pass)))
            {progressDialog.dismiss();
                Toast.makeText(login.this,"Enter the password", Toast.LENGTH_SHORT).show();}
            else if(!Email.matches(emailpattern))
            {progressDialog.dismiss();
                email.setError("Givee proper email address");}
            else if(password.length()<6)
            {progressDialog.dismiss();
                password.setError("More than 6 characters");
                Toast.makeText(login.this,"Password needs to be more than 6 characters", Toast.LENGTH_SHORT).show();
            }
            else
            {auth.signInWithEmailAndPassword(Email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {progressDialog.show();
                        try{

                        Intent intent=new Intent(login.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    catch(Exception e)
                    {Toast.makeText(login.this,e.getMessage(), Toast.LENGTH_SHORT).show();}
                }
                    else
                    {Toast.makeText(login.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();}

        }});}}});

        signuptext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this,registration.class);
                startActivity(intent);
                finish();
            }
        });
        geminibutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, geminichat.class);
                startActivity(intent);
                finish();
            }
        });




    }
}