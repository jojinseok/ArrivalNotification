package com.example.arrivalnotification;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

        TextView nameData = (TextView) findViewById(R.id.nameData);
        TextView addressData = (TextView) findViewById(R.id.addressData);
        TextView poi = (TextView) findViewById(R.id.poi);
        ListView listView = (ListView) findViewById(R.id.markerListView);
        Button write_button = (Button) findViewById(R.id.starButton);
     
        try{
            Intent secondIntent = getIntent();
            title = secondIntent.getStringExtra("주");
            subtitle = secondIntent.getStringExtra("서브");
            nameData.setText(title);
            addressData.setText(subtitle);
        }catch (Exception e){
            e.printStackTrace();
        }

        write_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            // MainActivity.stateLan, MainActivity.stateLon 즐겨찾기에 사용될 위도 경도 값, MainActivity.near = 주변 도착 인식 거리,title = 검색된 이름, subtitle = 주소
            // 위 값들과 알람 설정 값 Firebase에 넣으면 될것같습니다

            }
        });

    }
}