package com.example.befriend;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class Setup_second extends AppCompatActivity {

    private CircleImageView profileImg;
    private Button setupButton;
    private DatabaseReference UserReference;
    private StorageReference reference;
    String UserID;
    private FirebaseAuth auth;
    private ProgressDialog loading;

    final static int PHOTO_PICKED = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_second);

        auth = FirebaseAuth.getInstance();
        reference = FirebaseStorage.getInstance().getReference().child("Profile Images");
        UserID = auth.getCurrentUser().getUid();
        UserReference = FirebaseDatabase.getInstance().getReference().child("Users").child(UserID);

        profileImg = (CircleImageView) findViewById(R.id.circleImageView);
        setupButton = (Button) findViewById(R.id.setup_finish);
        loading = new ProgressDialog(this);



        setupButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                sendUserToMainActivity();
            }
        });
        profileImg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent photo = new Intent();
                photo.setAction(Intent.ACTION_GET_CONTENT);
                photo.setType("image/*");
                startActivityForResult(photo, PHOTO_PICKED);

            }
        });

        UserReference.child("profileImage").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String imageAddress = dataSnapshot.getValue().toString();
                    Picasso.get().load(imageAddress).placeholder(R.drawable.profile).into(profileImg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PHOTO_PICKED && resultCode == RESULT_OK && data!= null){
            Uri ImageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){
                Uri resultUri = result.getUri();
                final StorageReference filepath = reference.child(UserID+".jpg");
                loading.setTitle("加载中~");
                loading.setMessage("正在向世界注入灵魂，请稍等~");
                loading.show();
                loading.setCanceledOnTouchOutside(true);
                filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String downloadUrl = uri.toString();
                                UserReference.child("profileImage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Intent selfIntent = new Intent(Setup_second.this, Setup_second.class);
                                            startActivity(selfIntent);

                                            Toast.makeText(Setup_second.this,"帅照成功保存到数据库",Toast.LENGTH_SHORT).show();
                                                loading.dismiss();
                                            }
                                            else{
                                                String erroeMessage = task.getException().getMessage();
                                                Toast.makeText(Setup_second.this,erroeMessage,Toast.LENGTH_SHORT).show();
                                                loading.dismiss();
                                            }
                                        }
                                    });
                            }
                        });
                    }
                });

            }
            else{
                Toast.makeText(Setup_second.this, "出现错误，请重试",Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }
        }
    }
    private void sendUserToMainActivity() {
        Intent loginIntent = new Intent(Setup_second.this, MainActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
}
