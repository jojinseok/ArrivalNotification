package com.example.arrivalnotification;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.skt.Tmap.poi_item.TMapPOIItem;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Marker_data extends AppCompatActivity {
    String title;
    String subtitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marker_data);

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
                        intent=new Intent(getApplicationContext(),Star.class);
                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });

        TextView nameData = (TextView) findViewById(R.id.nameData);
        TextView addressData = (TextView) findViewById(R.id.addressData);
        TextView poi = (TextView) findViewById(R.id.poi);
        TextView customAlarm = (TextView) findViewById(R.id.customAlarm_data);
        TextView many_data = (TextView) findViewById(R.id.many_data);
        ListView listView = (ListView) findViewById(R.id.markerListView);
        Button write_button = (Button) findViewById(R.id.starButton);
        ArrayAdapter<String> adapter;
        Intent secondIntent = getIntent();
        try{
            title = secondIntent.getStringExtra("???");
            subtitle = secondIntent.getStringExtra("??????");
            nameData.setText(title);
            addressData.setText(subtitle);
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            String custom = secondIntent.getStringExtra("??????");
            customAlarm.append("????????? ?????? "+custom+"KM ?????? ??? ??????");
        }
        catch(Exception e){
            e.printStackTrace();
        }
        try{
            String total = secondIntent.getStringExtra("??????");
            poi.append("  "+total);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        try{
            String sort = secondIntent.getStringExtra("??????");
            many_data.append("  "+sort);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        try {
            ArrayList<String> ar = secondIntent.getStringArrayListExtra("????????????");
            ArrayList<String> inputData = new ArrayList<String>();
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, inputData);
            listView.setAdapter(adapter);
            for(int i = 0;i < ar.size();i++) {
                inputData.add(ar.get(i));
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        write_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            // MainActivity.stateLan ?????? ???, MainActivity.stateLon ?????? ???, MainActivity.near = ?????? ?????? ?????? ??????,title = ????????? ??????, subtitle = ??????
            // ??? ????????? ?????? ?????? ??? Firebase??? ????????? ??????????????????
                Toast.makeText(Marker_data.this," ???????????? ????????? ????????????????????? "+subtitle,Toast.LENGTH_SHORT).show();
Star.a=Double.toString(MainActivity.stateLan);
Star.b=Double.toString(MainActivity.stateLon);
Star.c=Integer.toString(MainActivity.near);
Star.d=title;
Star.e=subtitle;
            }
        });

    }
}