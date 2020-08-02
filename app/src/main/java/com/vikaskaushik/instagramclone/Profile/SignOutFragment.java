package com.vikaskaushik.instagramclone.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vikaskaushik.instagramclone.Home.HomeActivity;
import com.vikaskaushik.instagramclone.Login.LoginActivity;
import com.vikaskaushik.instagramclone.R;

public class SignOutFragment extends Fragment {
    private static final String TAG = "SignOutFragment";

    //firebase
    private FirebaseAuth mAuth;

    private ProgressBar mProgressBar;
    private TextView tvSignOut, mPleaseWait;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signout, container, false);

        tvSignOut = view.findViewById(R.id.tvConfirmSignout);
        mProgressBar = view.findViewById(R.id.progressbar);
        mPleaseWait = view.findViewById(R.id.pleaseWait);
        Button btnConfirmSignOut = view.findViewById(R.id.btnConfirmSignout);
        mAuth = FirebaseAuth.getInstance();

        mProgressBar.setVisibility(View.GONE);
        mPleaseWait.setVisibility(View.GONE);

        btnConfirmSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to sign out");

                mProgressBar.setVisibility(View.VISIBLE);
                mPleaseWait.setVisibility(View.VISIBLE);
                mAuth.signOut();
                getActivity().finish();
                onStart();
            }
        });

        return view;
    }

    /*****************************Firebase***************************************/
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Log.d(TAG, "onStart: user is signed in");

        } else {
            Log.d(TAG, "onStart: user is not signed out");

            Log.d(TAG, "onStart: navigating to login screen");
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
