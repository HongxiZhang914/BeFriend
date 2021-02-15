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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class Setup extends AppCompatActivity {

    private EditText userName;
    private ImageButton nextButton;
    private FirebaseAuth auth;
    private DatabaseReference reference;
    private ProgressDialog loading;
    String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        auth = FirebaseAuth.getInstance();
        UserID = auth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(UserID);

        userName = (EditText) findViewById(R.id.setup_username);
        nextButton = (ImageButton) findViewById(R.id.setup_next);
        loading = new ProgressDialog(this);

        nextButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SaveSetupInformation();
            }
        });
    }

    private void SaveSetupInformation() {
        String niceName = userName.getText().toString();
        if(TextUtils.isEmpty(niceName)){
            Toast.makeText(this, "您还没有起名字~", Toast.LENGTH_SHORT).show();
        }

        else{
            HashMap userMap = new HashMap();
            userMap.put("userName", niceName);
            userMap.put("Status", "Not Assigned");
            userMap.put("Gender", "Not assigned");
            userMap.put("Age", "Not Assigned");
            loading.setTitle("加载中~");
            loading.setMessage("正在向世界注入灵魂，请稍等~");
            loading.show();
            loading.setCanceledOnTouchOutside(true);
            reference.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        sendToSecondSetup();
                        Toast.makeText(Setup.this, "信息保存成功！", Toast.LENGTH_LONG).show();
                        loading.dismiss();
                    }
                    else{
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(Setup.this, errorMessage, Toast.LENGTH_LONG).show();
                        loading.dismiss();
                    }
                }
            });

        }
    }

    private void sendToSecondSetup() {
        Intent secondSetupActivity = new Intent(Setup.this,Setup_second.class);
        startActivity(secondSetupActivity);
    }
}
