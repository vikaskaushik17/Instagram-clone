package com.vikaskaushik.instagramclone.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vikaskaushik.instagramclone.Home.HomeActivity;
import com.vikaskaushik.instagramclone.Likes.LikesActivity;
import com.vikaskaushik.instagramclone.Profile.ProfileActivity;
import com.vikaskaushik.instagramclone.R;
import com.vikaskaushik.instagramclone.Search.SearchActivity;
import com.vikaskaushik.instagramclone.Share.ShareActivity;

public class BottomNavigationViewHelper {

    private static final String TAG = "BottomNavigationViewHol";

    public static void enableNavigation(final Context context, final Activity callingActivity, BottomNavigationView view) {

        Log.d(TAG, "enableNavigation: started");
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.d(TAG, "onNavigationItemSelected: id : " + item.getItemId());
                switch (item.getItemId()) {

                    case R.id.ic_house:
                        Intent homeIntent = new Intent(context, HomeActivity.class);
                        context.startActivity(homeIntent);
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                    case R.id.ic_search:
                        Intent searchIntent = new Intent(context, SearchActivity.class);
                        context.startActivity(searchIntent);
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                    case R.id.ic_circle:
                        Intent circleIntent = new Intent(context, ShareActivity.class);
                        context.startActivity(circleIntent);
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                    case R.id.ic_alert:
                        Intent alert = new Intent(context, LikesActivity.class);
                        context.startActivity(alert);
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                    case R.id.ic_android:
                        Log.d(TAG, "onNavigationItemSelected: clicked");
                        Intent profile = new Intent(context, ProfileActivity.class);
                        context.startActivity(profile);
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    default:
                        //

                }
                return false;
            }
        });
    }
}
