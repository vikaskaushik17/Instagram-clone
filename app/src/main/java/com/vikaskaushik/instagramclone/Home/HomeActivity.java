package com.vikaskaushik.instagramclone.Home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.vikaskaushik.instagramclone.Login.LoginActivity;
import com.vikaskaushik.instagramclone.R;
import com.vikaskaushik.instagramclone.Utils.BottomNavigationViewHelper;
import com.vikaskaushik.instagramclone.Utils.MainfeedListAdapter;
import com.vikaskaushik.instagramclone.Utils.SectionPagerAdapter;
import com.vikaskaushik.instagramclone.Utils.UniversalImageLoader;
import com.vikaskaushik.instagramclone.Utils.ViewCommentsFragment;
import com.vikaskaushik.instagramclone.models.Photo;
import com.vikaskaushik.instagramclone.models.UserAccountSettings;

public class HomeActivity extends AppCompatActivity implements
        MainfeedListAdapter.OnLoadMoreItemsListener {

    public static final int ACTIVITY_NUM = 0;
    public static final int HOME_FRAGMENT = 1;
    private static final String TAG = "HomeActivity";
    //firebase
    private FirebaseAuth mAuth;
    //widgets
    private ViewPager viewPager;
    private FrameLayout mFrameLayout;
    private RelativeLayout mRelativeLayout;

    @Override
    public void onLoadMoreItems() {
        Log.d(TAG, "onLoadMoreItems: displaying more photos");
        HomeFragment fragment = (HomeFragment) getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" +
                        R.id.container_pager + ":" + viewPager.getCurrentItem());

        if (fragment != null) {
            fragment.displayMorePhotos();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: start");

        viewPager = findViewById(R.id.container_pager);
        mFrameLayout = findViewById(R.id.container_view);
        mRelativeLayout = findViewById(R.id.relLayoutParent);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        initImageLoader();
        setupBottomNavigationView();
        setupViewPager();

    }


    public void onCommentThreadSelected(Photo photo, String callingActivity) {
        Log.d(TAG, "onCommentThreadSelected: selected a comment thread");

        ViewCommentsFragment fragment = new ViewCommentsFragment();
        Bundle args = new Bundle();
        Log.d(TAG, "onCommentThreadSelected: photo is: " + photo);
        args.putParcelable(getString(R.string.photo), photo);
        args.putString(getString(R.string.home_activity), getString(R.string.home_activity));
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container_view, fragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        transaction.commit();

    }

    public void hideLayout() {
        Log.d(TAG, "hideLayout: hiding layout ");
        mRelativeLayout.setVisibility(View.GONE);
        mFrameLayout.setVisibility(View.VISIBLE);
    }

    public void showLayout() {
        Log.d(TAG, "hideLayout: showing layout ");
        mRelativeLayout.setVisibility(View.VISIBLE);
        mFrameLayout.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mFrameLayout.getVisibility() == View.VISIBLE) {
            showLayout();
        }
    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(HomeActivity.this);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    /*
     * Responsible for adding the 3 tabs: camera, home, messages
     * */
    private void setupViewPager() {
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CameraFragment());
        adapter.addFragment(new HomeFragment());
        adapter.addFragment(new MessagesFragment());

        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_camera);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_action_name);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_arrow);
    }

    /*
     * BottomNavigationView setup
     * */
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(HomeActivity.this, this, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


    /**
     * ************************Firebase********************************************
     */

    /**
     * checks to see if the @param 'user' is logged in
     *
     * @param user
     */
    private void checkCurrentUser(FirebaseUser user) {
        Log.d(TAG, "checkCurrentUser: checking is user is logged in");

        if (user == null) {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        viewPager.setCurrentItem(HOME_FRAGMENT);
        checkCurrentUser(currentUser);
        if (currentUser != null) {
            Log.d(TAG, "onStart: login done");
        } else {
            Log.d(TAG, "onStart: user not registered");
        }
    }


}
