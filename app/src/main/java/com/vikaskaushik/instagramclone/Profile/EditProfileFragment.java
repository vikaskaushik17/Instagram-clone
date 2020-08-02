package com.vikaskaushik.instagramclone.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vikaskaushik.instagramclone.R;
import com.vikaskaushik.instagramclone.Share.ShareActivity;
import com.vikaskaushik.instagramclone.Utils.FirebaseMethods;
import com.vikaskaushik.instagramclone.Utils.UniversalImageLoader;
import com.vikaskaushik.instagramclone.dialogs.ConfirmPasswordDialog;
import com.vikaskaushik.instagramclone.models.User;
import com.vikaskaushik.instagramclone.models.UserAccountSettings;
import com.vikaskaushik.instagramclone.models.UserSettings;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment implements ConfirmPasswordDialog.OnConfirmPasswordListener {
    private static final String TAG = "EditProfileFragment";
    private EditText mDisplayName, mUsername, mWebsite, mDescription, mEmail, mPhoneNumber;
    private TextView mChangeProfilePhoto;
    private CircleImageView mProfilePhoto;
    private String userID;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    //vars
    private UserSettings mUserSettings;

    /**
     * method to receive password from ConfirmPasswordsDialog fragment
     *
     * @param password
     */
    @Override
    public void onConfirmPassword(String password) {
        //        Log.d(TAG, "onConfirmPassword: got the password: " + password);

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential(mAuth.getCurrentUser().getEmail(), password);

        // Prompt the user to re-provide their sign-in credentials
        mAuth.getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User re-authenticated.");

                            /**********************check if email is already present in database*********************/
                            mAuth.fetchSignInMethodsForEmail(mEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                    if (task.isSuccessful()) {
                                        try {
                                            if (task.getResult().getSignInMethods().size() == 1) {
                                                Log.d(TAG, "onComplete: that email is already in use");
                                                Toast.makeText(getActivity(), "That email is already in use", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.d(TAG, "onComplete: That email is available");


                                                //Updating the email
                                                mAuth.getCurrentUser().updateEmail(mEmail.getText().toString())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Log.d(TAG, "User email address updated.");
                                                                    mFirebaseMethods.updateEmail(mEmail.getText().toString());
                                                                    Toast.makeText(getActivity(), "Email updated", Toast.LENGTH_SHORT).show();

                                                                }
                                                            }
                                                        });
                                            }
                                        } catch (NullPointerException e) {
                                            Log.d(TAG, "onComplete: NullPointerException :" + e.getMessage());

                                        }

                                    }
                                }
                            });

                        } else {
                            Log.d(TAG, "User re-authenticated failed");
                        }
                    }
                });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        mProfilePhoto = view.findViewById(R.id.profile_photo);
        mAuth = FirebaseAuth.getInstance();
        mProfilePhoto = view.findViewById(R.id.profile_photo);
        mDisplayName = view.findViewById(R.id.display_name);
        mUsername = view.findViewById(R.id.username);
        mWebsite = view.findViewById(R.id.website);
        mDescription = view.findViewById(R.id.description);
        mEmail = view.findViewById(R.id.email);
        mPhoneNumber = view.findViewById(R.id.phoneNumber);
        mChangeProfilePhoto = view.findViewById(R.id.changeProfilePhoto);
        mFirebaseMethods = new FirebaseMethods(getActivity());


//        setProfileImage();

        //back arrow  for navigating back to ProfileActivity
        ImageView backArrow = view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to ProfileActivity");
                getActivity().finish();
            }
        });


        ImageView checkMark = view.findViewById(R.id.saveChanges);
        checkMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileSettings();
            }
        });

        return view;
    }


//    private void setProfileImage(){
//        Log.d(TAG, "setProfileImage: setting profile image");
//        String imgURL = "encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTQ52fvJXC1pjrB6auoYy0AItH4M8ftFVK1HQ&usqp=CAU";
//        UniversalImageLoader.setImage(imgURL, mProfilePhoto, null, "https://");
//    }

    /**
     * Retrieves the data contained in the widgets and submit it to the database
     * Before doing so it checks to make sure the username chosen is unique
     */
    private void saveProfileSettings() {
        final String displayName = mDisplayName.getText().toString();
        final String username = mUsername.getText().toString();
        final String website = mWebsite.getText().toString();
        final String description = mDescription.getText().toString();
        final String email = mEmail.getText().toString();
        final long phoneNumber = Long.parseLong(mPhoneNumber.getText().toString());

        //case 1: The user change the username.
        if (!mUserSettings.getUser().getUsername().equals(username)) {

            checkIfUsernameExists(username);
        }
        //case 2: if the user made a change to their email
        if (!mUserSettings.getUser().getEmail().equals(email)) {

            //Step 1: Re-Authenticate
            //                  - Confirm the password and email
            ConfirmPasswordDialog dialog = new ConfirmPasswordDialog();
            dialog.show(getFragmentManager(), getString(R.string.confirm_password_dialog));
            dialog.setTargetFragment(EditProfileFragment.this, 1);

            //Step 2: Check if the email already is registered
            //          - 'FetchProvidersForEmail(String email)'
            //Step 3: Change the email
            //          -Submit the new Email to the database and authentication
        }

        /**
         * Updating the rest of the things that do not require uniqueness
         */
        if (!mUserSettings.getSettings().getDisplay_name().equals(displayName)) {
            //update display name
            mFirebaseMethods.updateUserAccountSettings(displayName, null, null, 0);
        }
        if (!mUserSettings.getSettings().getWebsite().equals(website)) {
            //update website
            mFirebaseMethods.updateUserAccountSettings(null, website, null, 0);

        }
        if (!mUserSettings.getSettings().getDescription().equals(description)) {
            //update description
            mFirebaseMethods.updateUserAccountSettings(null, null, description, 0);

        }
        if (!(mUserSettings.getUser().getPhone_number() == phoneNumber)) {
            //update phone number
            mFirebaseMethods.updateUserAccountSettings(null, null, null, phoneNumber);

        }
    }

    /**
     * checks if username already exists in database.
     *
     * @param username
     */
    private void checkIfUsernameExists(final String username) {
        Log.d(TAG, "checkIfUsernameExists: checking if " + username + " already exists");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_username))
                .equalTo(username);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    mFirebaseMethods.updateUsername(username);
                    Toast.makeText(getActivity(), "Username saved", Toast.LENGTH_SHORT).show();

                }
                for (DataSnapshot singleSnapShot : snapshot.getChildren()) {
                    if (singleSnapShot.exists()) {
                        Log.d(TAG, "checkIfUsernameExists : FOUND A MATCH " + singleSnapShot.getValue(User.class).getUsername());
                        Toast.makeText(getActivity(), "Username already exists", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void setProfileWidgets(UserSettings userSettings) {
//        Log.d(TAG, "setProfileWidgets: setting widget with data retrieving from firebase database: " + userSettings.toString());

//        User user = userSettings.getUser();
        mUserSettings = userSettings;
        UserAccountSettings settings = userSettings.getSettings();

        UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePhoto, null, "");

        mDisplayName.setText(settings.getDisplay_name());
        mUsername.setText(settings.getUsername());
        mWebsite.setText(settings.getWebsite());
        mDescription.setText(settings.getDescription());
        mEmail.setText(userSettings.getUser().getEmail());
        mPhoneNumber.setText(String.valueOf(userSettings.getUser().getPhone_number()));

        mChangeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: changing profile photo : ");

                Intent intent = new Intent(getActivity(), ShareActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //268435456 give new task to the activity
                getActivity().startActivity(intent);
                getActivity().finish();

            }
        });
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
        userID = mAuth.getCurrentUser().getUid();


        if (currentUser != null) {
            Log.d(TAG, "onStart: login done");
        } else {
            Log.d(TAG, "onStart: user not registered");
        }

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //retrieve user information from database
                setProfileWidgets(mFirebaseMethods.getUserSettings(snapshot));

                //retrieve images for the user in question

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}
