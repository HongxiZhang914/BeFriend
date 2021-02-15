package com.example.befriend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    private EditText emailAddress;
    private EditText password;
    private EditText confirmPassword;
    private Button createAccount;
    private FirebaseAuth auth;
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        loading = new ProgressDialog(this);
        emailAddress = (EditText) findViewById(R.id.register_Email);
        password = (EditText) findViewById(R.id.register_password);
        confirmPassword = (EditText) findViewById(R.id.register_confirmPassword);
        createAccount = (Button) findViewById(R.id.register_button);

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
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

    private void CreateNewAccount() {
        String email = emailAddress.getText().toString();
        String pw = password.getText().toString();
        String cpw = confirmPassword.getText().toString();
        //check if email address,password and confirmPassword is empty
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"请输入邮箱地址", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(pw)){
            Toast.makeText(this,"请输入密码", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(cpw)){
            Toast.makeText(this,"请输入确认密码", Toast.LENGTH_SHORT).show();
        }

        //check if password and confirmPassword is the same
        else if(!pw.equals(cpw)){
            Toast.makeText(this,"两次密码不一致", Toast.LENGTH_SHORT).show();
        }

        else if(pw.length() < 8 || pw.length() > 25){
            Toast.makeText(this,"请输入8位以上，25位以下的密码", Toast.LENGTH_SHORT).show();
        }

        else{
            // display loading dialog
            loading.setTitle("创建账户~");
            loading.setMessage("正在创建账户，请稍等~");
            loading.show();
            loading.setCanceledOnTouchOutside(true);
            //register the user to the firebase, show fail message if fail
            auth.createUserWithEmailAndPassword(email,pw)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                sendUserToMainActivity();
                                Toast.makeText(Register.this, "注册成功！", Toast.LENGTH_SHORT).show();
                                loading.dismiss(); //loading dialog disappear when register successfully done
                            }
                            else{
                                String ErrorMessage = task.getException().getMessage();
                                Toast.makeText(Register.this, ErrorMessage, Toast.LENGTH_SHORT).show();
                                loading.dismiss(); //loading dialog disappear if fail
                            }
                        }
                    });
        }
    }

    private void sendUserToMainActivity() {
        Intent registerIntent = new Intent(Register.this, MainActivity.class);
        registerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(registerIntent);
        finish();
    }
}
