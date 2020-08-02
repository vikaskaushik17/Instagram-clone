package com.vikaskaushik.instagramclone.Likes;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.vikaskaushik.instagramclone.Home.HomeActivity;
import com.vikaskaushik.instagramclone.R;
import com.vikaskaushik.instagramclone.Utils.BottomNavigationViewHelper;

public class LikesActivity extends AppCompatActivity {
    public static final int ACTIVITY_NUM = 3;
    private static final String TAG = "LikesActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: started in likes activity");

        setupBottomNavigationView();
    }

    /*
     * BottomNavigationView setup
     * */
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(LikesActivity.this, this, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
