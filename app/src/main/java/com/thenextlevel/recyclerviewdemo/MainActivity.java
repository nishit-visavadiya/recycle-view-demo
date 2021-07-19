package com.thenextlevel.recyclerviewdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.*;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.thenextlevel.recyclerviewdemo.Model.Upload;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int PICK_IMAGE_REQUEST = 7;

    private Button buttonChooseImage;
    private Button buttonUpload;
    private TextView textViewShowUploads;
    private EditText editTextFileName;
    private ImageView mImageView;
    private ProgressBar mProgressBar;

    private Uri mImageUrl;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonChooseImage = findViewById(R.id.button_choose_image);
        buttonUpload = findViewById(R.id.button_upload);
        textViewShowUploads = findViewById(R.id.text_view_show_uploads);
        editTextFileName = findViewById(R.id.edit_text_file_name);
        mImageView = findViewById(R.id.image_view);
        mProgressBar = findViewById(R.id.progress_bar);

        mStorageRef = FirebaseStorage.getInstance().getReference("Uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Uploads");

        buttonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(MainActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });

        textViewShowUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImagesActivity();
            }
        });

    }

    private void openFileChooser() {
        Intent fileChooserIntent = new Intent();
        fileChooserIntent.setType("image/*");
        fileChooserIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(fileChooserIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUrl = data.getData();

            Glide.with(MainActivity.this).load(mImageUrl).into(mImageView);

            //Picasso.get().load(mImageUrl).into(mImageView);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mine = MimeTypeMap.getSingleton();
        return mine.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadFile() {

        if (mImageUrl != null) {
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUrl));

            mUploadTask = fileReference.putFile(mImageUrl)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);
                            
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Upload upload = new Upload(editTextFileName.getText().toString().trim(), uri.toString());
                                    String uploadId = mDatabaseRef.push().getKey();
                                    mDatabaseRef.child(uploadId).setValue(upload);
                                    Toast.makeText(MainActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                }
                            });

                            //TODO Old method to retrieve image from firebase

                            /*Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful()) ;
                            Uri downloadUrl = urlTask.getResult();

                            Log.d(TAG, "onSuccess: firebase download url: " + downloadUrl.toString());
                            Upload upload = new Upload(editTextFileName.getText().toString().trim(), downloadUrl.toString());

                            String uploadId = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadId).setValue(upload);*/

                            //TODO Old method to retrieve image from firebase

                            /*Toast.makeText(MainActivity.this, "Upload Successful", Toast.LENGTH_LONG).show();
                            Upload upload = new Upload(editTextFileName.getText().toString(),
                                    taskSnapshot.getUploadSessionUri().toString());

                            String uploadId = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadId).setValue(upload);*/
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) (progress));
                        }
                    });
        } else {
            Toast.makeText(this, "Select a file", Toast.LENGTH_SHORT).show();
        }

    }

    private void openImagesActivity() {
        startActivity(new Intent(MainActivity.this, ImagesActivity.class));
    }
}
