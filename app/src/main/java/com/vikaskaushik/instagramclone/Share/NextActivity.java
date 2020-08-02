package com.vikaskaushik.instagramclone.Share;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vikaskaushik.instagramclone.R;
import com.vikaskaushik.instagramclone.Utils.FirebaseMethods;
import com.vikaskaushik.instagramclone.Utils.UniversalImageLoader;

public class NextActivity extends AppCompatActivity {
    private static final String TAG = "NextActivity";
    public String mAppend = "file:/";
    //vars
    private Context mContext = NextActivity.this;
    private int imageCount = 0;
    private String imgURL;
    private Intent intent;
    private Bitmap bitmap;

    //widgets
    private ImageView backArrow;
    private TextView share;
    private EditText mCaption;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseMethods = new FirebaseMethods(mContext);
        backArrow = findViewById(R.id.ivBackArrow);
        share = findViewById(R.id.tvShare);
        mCaption = findViewById(R.id.caption);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the activity Next");
                finish();
            }
        });


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: uploading the image to firebase");
                Toast.makeText(mContext, "Attempting to upload new photo", Toast.LENGTH_SHORT).show();

                String caption = mCaption.getText().toString();

                if (intent.hasExtra(getString(R.string.selected_image))) {

                    imgURL = intent.getStringExtra(getString(R.string.selected_image));
                    mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo), caption, imageCount, imgURL, null);

                } else if (intent.hasExtra(getString(R.string.selected_bitmap))) {

                    bitmap = (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));
                    mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo), caption, imageCount, null, bitmap);

                }
            }
        });

        setImage();
    }

    public void someMethod() {
        /**
         * Step 1)
         * Create a data model for photos
         *
         * Step 2)
         * Add properties to the photo objects(Caption, data, imageURL, photo_id, tags, user_id)
         *
         * Step 3)
         * Count the number of photos that the user already has
         *
         * Step 4)
         * a) Upload the photos to firebase storage
         * b) insert into 'photos' node
         * c) insert into 'user_photos' node
         */
    }


    /**
     * gets the image URL from incoming intent and displays the chosen image
     */
    private void setImage() {
        intent = getIntent();
        ImageView image = findViewById(R.id.imageShare);

        if (intent.hasExtra(getString(R.string.selected_image))) {
            imgURL = intent.getStringExtra(getString(R.string.selected_image));
            Log.d(TAG, "setImage: got new image url: " + imgURL);
            UniversalImageLoader.setImage(imgURL, image, null, mAppend);
        } else if (intent.hasExtra(getString(R.string.selected_bitmap))) {
            bitmap = (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));
            Log.d(TAG, "setImage: got new bitmap");
            image.setImageBitmap(bitmap);
        }

    }


    /**
     * ************************Firebase********************************************
     */
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        Log.d(TAG, "onDataChange: image count : " + imageCount);


        if (currentUser != null) {
            Log.d(TAG, "onStart: login done");
        } else {
            Log.d(TAG, "onStart: user not registered");
        }

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                imageCount = mFirebaseMethods.getImageCount(snapshot);
                Log.d(TAG, "onDataChange: image count : " + imageCount);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
