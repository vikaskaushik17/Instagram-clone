package com.vikaskaushik.instagramclone.Share;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.vikaskaushik.instagramclone.R;
import com.vikaskaushik.instagramclone.Utils.BottomNavigationViewHelper;
import com.vikaskaushik.instagramclone.Utils.Permissions;
import com.vikaskaushik.instagramclone.Utils.SectionPagerAdapter;


public class ShareActivity extends AppCompatActivity {
    //    constants
    public static final int ACTIVITY_NUM = 2;
    public static final int VERIFY_PERMISSION_REQUEST = 1;
    private static final String TAG = "ShareActivity";
    private ViewPager mViewPager;

    private Context mContext = ShareActivity.this;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        Log.d(TAG, "onCreate: started in share activity");

        if (checkPermissionArray(Permissions.PERMISSION)) {
            setupViewPager();
        } else {
            verifyPermission(Permissions.PERMISSION);
        }

//        setupBottomNavigationView();
    }

    /**
     * return the current tab number
     * 0 = GalleryFragment
     * 1 = PhotoFragment
     *
     * @return
     */
    public int getCurrentTabNumber() {
        return mViewPager.getCurrentItem();
    }

    /**
     * setup ViewPager for the tabs
     */
    private void setupViewPager() {
        Log.d(TAG, "setupViewPager: started");
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GalleryFragment());
        adapter.addFragment(new PhotoFragment());

        mViewPager = findViewById(R.id.container_pager);
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabsBottom);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setText(getString(R.string.gallery));
        tabLayout.getTabAt(1).setText(getString(R.string.photo));

    }


    public int getTask() {
        Log.d(TAG, "getTask: TASK: " + getIntent().getFlags());
        return getIntent().getFlags();
    }

    /**
     * Verify all the permission passed to the array
     *
     * @param permission
     */
    public void verifyPermission(String[] permission) {
        Log.d(TAG, "verifyPermission: verifying permissions");

        ActivityCompat.requestPermissions(
                ShareActivity.this,
                permission,
                VERIFY_PERMISSION_REQUEST
        );
    }

    /**
     * check an array of permission
     *
     * @param permissions
     * @return
     */
    public boolean checkPermissionArray(String[] permissions) {
        Log.d(TAG, "checkPermissionArray: checking permission array");

        for (int i = 0; i < permissions.length; i++) {
            String check = permissions[i];
            if (!checkPermissions(check)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check a single permission if it has been verified
     *
     * @param permission
     * @return
     */
    public boolean checkPermissions(String permission) {
        Log.d(TAG, "checkPermissions: checking permission");

        int permissionRequest = ActivityCompat.checkSelfPermission(ShareActivity.this, permission);
        if (permissionRequest != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkPermissions: permission was not granted for: " + permission);
            return false;
        } else {
            Log.d(TAG, "checkPermissions: permission was granted for: " + permission);
            return true;
        }
    }

    /*
     * BottomNavigationView setup
     * */
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(ShareActivity.this, this, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
