package com.example.arrivalnotification;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Star extends Activity {
    static String a,b,c,d,e;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.star);
        TextView star= (TextView) findViewById(R.id.starData);
        star.append(e+"\n"+d+"\n주변 인식 거리 : "+c+"\n위도 : "+a+"\n경도 : "+b+"\n");
        BottomNavigationView bottom = findViewById(R.id.bottom_menu);
        bottom.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.alarm:
                        intent=new Intent(getApplicationContext(),Alarm.class);
                        startActivity(intent);
                        return true;
                    case R.id.change:
                        intent=new Intent(getApplicationContext(),culture_find.class);
                        startActivity(intent);
                        return true;

                    case R.id.star:
                }
                return false;
            }
        });
    }
}