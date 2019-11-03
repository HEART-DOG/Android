package com.example.heart_dog;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

public class Home extends FragmentActivity {

    ImageButton menu;
    DrawerLayout drawer;
    NavigationView nav;
    JSONObject json;
    String result = "";
    Intent doginfo, main, listVIew, info, howToUse;
    TextView heart;

    private static Home ins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ins = this;

        setContentView(R.layout.activity_home);
        locationPermissionCheck();

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_drawer_menu, new HomeFragment(Home.this));
            transaction.commit();
        }
        drawer = findViewById(R.id.drawer_layout);
        nav = findViewById(R.id.nav_view);
        heart = findViewById(R.id.tv_heart);

        menu = findViewById(R.id.ib_menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){

                    case R.id.nav_howToUse:
                        howToUse = new Intent(getApplicationContext(), howToUse.class);
                        startActivity(howToUse);
                        break;

                    case R.id.nav_info:
                        info = new Intent(getApplicationContext(), info.class);
                        startActivity(info);
                        break;

                    case R.id.nav_dog_info: // 강아지 정보 저장을 누른 경우
                        Log.d("Home","Home doginfo is clicked");
                        doginfo = new Intent(getApplicationContext(), DogInfo.class);
                        startActivity(doginfo);
                        break;

                    case R.id.nav_sign_out: // 로그아웃 버튼을 누른 경우
                        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

                        if(mBluetoothAdapter.isEnabled()) {
                            mBluetoothAdapter.disable();
                        }
                        else {

                        }
                        main = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(main);
                        break;

                    case R.id.nav_dog_list: // 강아지 리스트 버튼을 누른 경우
                        listVIew = new Intent(getApplicationContext(), dogList.class);
                        startActivity(listVIew);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

        }
    }
    public void locationPermissionCheck() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Home.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        }
    }
}
