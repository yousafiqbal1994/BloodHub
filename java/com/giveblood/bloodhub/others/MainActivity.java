package com.giveblood.bloodhub.others;

/**
 * Created by YouCaf Iqbal on 8/9/2016.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.giveblood.bloodhub.R;
import com.giveblood.bloodhub.userprofile.DpActivity;
import com.giveblood.bloodhub.userprofile.ProfileFragment;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity{
    public DrawerLayout mDrawerLayout;
    public NavigationView mNavigationView;
    public FragmentManager mFragmentManager;
    public FragmentTransaction mFragmentTransaction;
    CircleImageView dp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getSupportActionBar().hide();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff);
        View headerLayout = mNavigationView.getHeaderView(0); // 0-index header
        dp = (CircleImageView)headerLayout.findViewById(R.id.userImage);
        setValues();
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
//        mNavigationView.setCheckedItem(R.id.nav_item_dp);
        mFragmentTransaction.replace(R.id.containerView,new TabsView()).commit();
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();
                if (menuItem.getItemId() == R.id.nav_item_profile) {
                    Intent intent = new Intent(getApplicationContext(), ProfileFragment.class);
                    startActivity(intent);
                }
                if (menuItem.getItemId() == R.id.nav_item_home) {
                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView,new TabsView()).commit();
                }
                if (menuItem.getItemId() == R.id.nav_item_share) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT,"Put Google Play link here");
                    intent.setType("text/plain");
                    startActivity(intent);
                }
                if (menuItem.getItemId() == R.id.nav_item_dp) {
                    Intent intent = new Intent(getApplicationContext(), DpActivity.class);
                    startActivity(intent);
                }
                return false;
            }

        });

        /**
         * Setup Drawer Toggle of the Toolbar
         */

        dp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), DpActivity.class);
                startActivity(intent);
            }
        });

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, toolbar,R.string.app_name,
                R.string.app_name);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setValues();
    }

    public void setValues() {
        SharedPreferences userDetails = MainActivity.this.getSharedPreferences("ProfileDetails", MODE_APPEND);
        String imageString = userDetails.getString("image","");
        if (imageString.length() >= 80) {
            // decode string back to image and set it
            byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            dp.setImageBitmap(decodedByte);
        }
    }

}
