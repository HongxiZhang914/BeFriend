package com.example.befriend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class login extends AppCompatActivity {

    private ImageButton loginButton;
    private EditText email;
    private EditText password;
    private TextView newAccount;
    private FirebaseAuth auth;
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
        //initialize button and text
        newAccount = (TextView) findViewById(R.id.go_to_register);
        email = (EditText) findViewById(R.id.login_Email);
        password = (EditText) findViewById(R.id.login_password);
        loginButton = (ImageButton) findViewById(R.id.login_button);
        loading = new ProgressDialog(this);

        //go to register page if click on register
        newAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToRegisterActivity();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                letUserLogin();

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            sendUserToMainActivity();
        }
    }

    private void letUserLogin() {
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();
        if(TextUtils.isEmpty(userEmail)){
            Toast.makeText(this,"请输入用户名", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userPassword)){
            Toast.makeText(this,"请输入密码", Toast.LENGTH_SHORT).show();
        }
        else{
            loading.setTitle("登录中~");
            loading.setMessage("正在登录，请稍等~");
            loading.show();
            loading.setCanceledOnTouchOutside(true);
            auth.signInWithEmailAndPassword(userEmail,userPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                sendUserToMainActivity();
                                Toast.makeText(login.this,"登陆成功",Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                            }
                            else{
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(login.this,errorMessage,Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                            }
                        }
                    });
        }
    }

    private void sendUserToMainActivity() {
        Intent loginIntent = new Intent(login.this, MainActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    //send user to register page
    private void sendToRegisterActivity() {
        Intent registerActivity = new Intent(login.this,Register.class);
        startActivity(registerActivity);
    }
}
