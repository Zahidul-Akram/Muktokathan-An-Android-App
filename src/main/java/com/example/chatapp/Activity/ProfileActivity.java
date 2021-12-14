package com.example.chatapp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.ModelClass.Users;
import com.example.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
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
import de.hdodenhof.circleimageview.CircleImageView;
public class ProfileActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    EditText profile_email,profile_phoneno,profile_status;
    CircleImageView profile_image;
    ImageView save;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    TextView profile_name;
    Uri setimageuri;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        bottomNavigationView=findViewById(R.id.bottom);
        bottomNavigationView.setOnItemReselectedListener(new NavigationBarView.OnItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.profile:

                        break;

                    case R.id.chatlist:
                        Intent intent3=new Intent(ProfileActivity.this,HomeActivity.class);
                        startActivity(intent3);
                        break;

                }


            }
        });



       auth= FirebaseAuth.getInstance();
       profile_email=findViewById(R.id.profile_email);
       profile_image=findViewById(R.id.profile_image);
       profile_phoneno=findViewById(R.id.profile_phoneno);
       profile_status=findViewById(R.id.profile_status);
        profile_name=findViewById(R.id.profile_name);
       save=findViewById(R.id.save);
       database=FirebaseDatabase.getInstance();
       storage=FirebaseStorage.getInstance();
        DatabaseReference reference=database.getReference().child("user").child(auth.getUid());
        StorageReference storageReference=storage.getReference().child("upload").child(auth.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email=snapshot.child("email").getValue().toString();
                String phone_no=snapshot.child("phone_no").getValue().toString();
                String status=snapshot.child("status").getValue().toString();
                String image=snapshot.child("imageUri").getValue().toString();
                name=snapshot.child("name").getValue().toString();

                profile_email.setText(email);
                profile_name.setText(name);
                profile_status.setText(status);
                profile_phoneno.setText(phone_no);
                Picasso.get().load(image).into(profile_image);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

       save.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String email= profile_email.getText().toString();
               String phone_no=profile_phoneno.getText().toString();
               String status=profile_status.getText().toString();


               if(setimageuri!=null)
               {
                   storageReference.putFile(setimageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                       @Override
                       public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                           storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                               @Override
                               public void onSuccess(Uri uri) {
                                   String finalimguri=uri.toString();
                                   Users users=new Users(name,email,auth.getUid(),phone_no,status,finalimguri);

                                   reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                           if(task.isSuccessful())
                                           {
                                               Toast.makeText(ProfileActivity.this,"Profile Updated Successfully",Toast.LENGTH_SHORT).show();

                                           }else
                                           {
                                               Toast.makeText(ProfileActivity.this,"Something Went Wrong!!",Toast.LENGTH_SHORT).show();
                                           }
                                       }
                                   });
                               }
                           });

                       }
                   });

               }else {
                   storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                       @Override
                       public void onSuccess(Uri uri) {
                           String finalimguri=uri.toString();
                           Users users=new Users(name,email,auth.getUid(),phone_no,status,finalimguri);

                           reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   if(task.isSuccessful())
                                   {
                                       Toast.makeText(ProfileActivity.this,"Profile Updated Successfully",Toast.LENGTH_SHORT).show();

                                   }else
                                   {
                                       Toast.makeText(ProfileActivity.this,"Something Went Wrong!!",Toast.LENGTH_SHORT).show();
                                   }
                               }
                           });
                       }
                   });
               }

           }
       });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode==10) {
            if(data!=null)
            {
                setimageuri=data.getData();
                profile_image.setImageURI(setimageuri);

            }
        }
    }
}