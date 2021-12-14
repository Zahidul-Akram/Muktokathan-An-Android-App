package com.example.chatapp.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.R;
import com.example.chatapp.ModelClass.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import de.hdodenhof.circleimageview.CircleImageView;

public class SignupActivity extends AppCompatActivity {
    TextView txt_signin;
    EditText reg_email,reg_name,reg_pass,reg_phnno;
    CircularProgressButton btn_signup;
    CircleImageView profile_image;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri imageUri;
    String imageURI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        txt_signin=findViewById(R.id.txt_signin);
        reg_email=findViewById(R.id.reg_email);
        reg_name=findViewById(R.id.reg_name);
        reg_pass=findViewById(R.id.reg_pass);
        reg_phnno=findViewById(R.id.reg_phnno);
        btn_signup=findViewById(R.id.btn_signup);
        profile_image=findViewById(R.id.profile_image);
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=reg_name.getText().toString();
                String email=reg_email.getText().toString();
                String phone_no=reg_phnno.getText().toString();
                String password=reg_pass.getText().toString();

                String status="Hey there I'm using Mukto kothon";
                if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone_no) || TextUtils.isEmpty(password))
                {
                    Toast.makeText(SignupActivity.this,"Enter Valid Data",Toast.LENGTH_SHORT).show();
                }
                else if(!email.matches(emailPattern))
                {
                    reg_email.setError("Enter Valid Email");
                    Toast.makeText(SignupActivity.this,"Enter Valid Email",Toast.LENGTH_SHORT).show();

                }
                else if(password.length()<6)
                {
                    reg_pass.setError("Invalid Password");
                    Toast.makeText(SignupActivity.this,"Enter 6 character Password",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                DatabaseReference reference=database.getReference().child("user").child(auth.getUid());
                                StorageReference storageReference=storage.getReference().child("upload").child(auth.getUid());

                                if(imageUri!=null)
                                {
                                    storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if(task.isSuccessful())
                                            {
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imageURI=uri.toString();
                                                        Users users=new Users(name,email,auth.getUid(),phone_no,status,imageURI);
                                                        reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful())
                                                                {
                                                                    Toast.makeText(SignupActivity.this,"Created Account Successfully",Toast.LENGTH_SHORT).show();
                                                                    new Handler().postDelayed(new Runnable() {
                                                                        @Override
                                                                        public void run() {

                                                                            Intent i = new Intent(SignupActivity.this, SigninActivity.class);
                                                                            startActivity(i);

                                                                        }
                                                                    }, 2500);


                                                                }
                                                                else
                                                                {
                                                                    Toast.makeText(SignupActivity.this,"Error in Creating",Toast.LENGTH_SHORT).show();

                                                                }
                                                            }
                                                        });

                                                    }
                                                });
                                            }

                                        }
                                    });
                                }else{
                                    String status="Hey there I'm using Mukto kothon";
                                    imageURI="https://firebasestorage.googleapis.com/v0/b/chat-app---muktokothon.appspot.com/o/profile_image.png?alt=media&token=4636d637-b0a5-4c31-8fa9-19565d3ff045";
                                    Users users=new Users(name,email,auth.getUid(),phone_no,status,imageURI);
                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(SignupActivity.this,"Created Account Successfully",Toast.LENGTH_SHORT).show();
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        Intent i = new Intent(SignupActivity.this, SigninActivity.class);
                                                        startActivity(i);

                                                    }
                                                }, 2500);


                                            }
                                            else
                                            {
                                                Toast.makeText(SignupActivity.this,"Error in Creating",Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });

                                }






                            }
                            else{
                                Toast.makeText(SignupActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }


            }
        });
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
            }
        });

        txt_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this,SigninActivity.class));
            }


        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode==10) {
            if(data!=null)
            {
                imageUri=data.getData();
                profile_image.setImageURI(imageUri);
            }
        }
    }
}