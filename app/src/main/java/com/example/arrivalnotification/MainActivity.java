package com.example.arrivalnotification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapMarkerItem2;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.address_info.TMapAddressInfo;
import com.skt.Tmap.poi_item.TMapPOIItem;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {

    // - 리스트 전역변수 설정
    ArrayAdapter<String> adapter;
    ArrayList<String> listItem;
    ArrayList<String> listItemSave;
    double mylongitude;
    double mylatitude;
    // - 전역변수 설정
    int num = -1;
    String strData;
    ArrayList<String> locationData = new ArrayList<>();
    ArrayList<TMapPOIItem> poiItem;
    ArrayList<TMapPoint> par;
    TMapData tmapdata;
    TMapView tmapview;
    ThreadFind threadfind;
    TMapPoint startPoint;
    TMapPoint endPoint;
    TMapGpsManager tMapGPS = null;
    TMapMarkerItem markerItem1 = new TMapMarkerItem();
    Context mContext1;
    TMapAddressInfo ti;
    public static String city = " ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.road_search);

        BottomNavigationView bottom = findViewById(R.id.bottom_menu);
        bottom.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.alarm:

                    case R.id.change:

                    case R.id.weather:

                    case R.id.star:

                }
                return false;
            }
        });


        // - xml 관련
        EditText findLocation = (EditText) findViewById(R.id.findLocation);
        Button findLocationButton = (Button) findViewById(R.id.button);
        Button endPointButton = (Button) findViewById(R.id.button2);
        LinearLayout linearLayoutTmap = (LinearLayout) findViewById(R.id.linearLayoutTmap);
        ListView listView = (ListView) findViewById(R.id.listview);
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);


        // - 위치 권한 요청
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) { //위치 권한 확인
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }


        // - GPS 세팅
        tMapGPS = new TMapGpsManager(this);
        tMapGPS.setMinTime(1000);
        tMapGPS.setMinDistance(10);
        tMapGPS.setProvider(tMapGPS.GPS_PROVIDER);
        tMapGPS.OpenGps();
        tMapGPS.setProvider(tMapGPS.NETWORK_PROVIDER);
        tMapGPS.OpenGps();


        // - tmap 세팅
        tmapdata = new TMapData();
        tmapview = new TMapView(this);
        tmapview.setSKTMapApiKey("l7xx68283586cc574df29d04f4e27d6eaad4"); //이부분은 아까 발급 받은 T-Map API를 입력하면 된다.
        linearLayoutTmap.addView(tmapview);



        // - 리스트 데이터를 넣을 준비
        listItem = new ArrayList<String>();
        listItemSave = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItemSave);
        listView.setAdapter(adapter);


        // - 키보드 표시제어
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        // - 검색버튼 스레드 동작 구현
        threadfind = new ThreadFind();
        findLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tmapview.removeAllMarkerItem();
                if (findLocation.getText().toString().equals("")) {
                } else if (threadfind.getState() == Thread.State.TERMINATED) {
                    System.out.println("스레드 상태 = " + threadfind.getState());
                    threadfind.interrupt();
                    threadfind = new ThreadFind();
                    threadfind.start();
                } else {
                    System.out.println("스레드 상태 = " + threadfind.getState());
                    threadfind.start();
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println(listItem);
                listItemSave.clear();
                listSave();
                scrollView.setVisibility(View.VISIBLE);
                imm.hideSoftInputFromWindow(findLocationButton.getWindowToken(), 0);
            }
        });

        findLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollView.setVisibility(View.INVISIBLE);
            }
        });

        endPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap check = BitmapFactory.decodeResource(getResources(), R.drawable.check);
                TMapCircle tMapCircle = new TMapCircle();
                tMapCircle.setCenterPoint( endPoint );
                tMapCircle.setRadius(300);
                tMapCircle.setCircleWidth(2);
                tMapCircle.setLineColor(Color.BLUE);
                tMapCircle.setAreaColor(Color.GRAY);
                tMapCircle.setAreaAlpha(100);
                tmapview.addTMapCircle("circle1", tMapCircle);
                try {
                     if(1>distanceKm(tmapview,endPoint.getLatitude(), endPoint.getLongitude())) {
                         Log.d("목표 접근", "" + endPoint.getLatitude() + "," + endPoint.getLongitude());
                    }
                } catch (Exception e) {
                e.printStackTrace();
                }
                //markerItem1.setIcon(check);
            }
        });

        markerClick();

        // - 리스트 아이템 동작
        Bitmap bitmap_orange = BitmapFactory.decodeResource(getResources(), R.drawable.markerline_orange);
        Bitmap bitmap_green = BitmapFactory.decodeResource(getResources(), R.drawable.markerline_green);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String data = (String) adapterView.getAdapter().getItem(i);
                markerItem1 = tmapview.getMarkerItemFromID("marker" + i);
                endPoint = markerItem1.getTMapPoint();
                markerItem1.setIcon(bitmap_green);
                tmapview.setCenterPoint(endPoint.getLongitude(), endPoint.getLatitude(), true);
                if (num > -1) {
                    markerItem1 = tmapview.getMarkerItemFromID("marker" + num);
                    markerItem1.setIcon(bitmap_orange);
                }
                num = i;

            }
        });




    }

    @Override
    protected void onResume() {
        super.onResume();
        listSave();
    }

    private static double distanceKm(TMapView mapView, double lat2, double lon2) {

        TMapPoint tpoint = mapView.getCenterPoint();
        double lat1 = tpoint.getLatitude();
        double lon1 = tpoint.getLongitude();
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1.609344;

        return (dist);
    }


    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


    // - 마커 클릭시 클릭좌표 저장 구현
    public void markerClick() {
        tmapview.setOnClickListenerCallBack(new TMapView.OnClickListenerCallback() {
            @Override
            public boolean onPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
                if (!arrayList.isEmpty()) {
                    endPoint = arrayList.get(0).getTMapPoint();
                }
                return false;
            }

            @Override
            public boolean onPressUpEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
                return false;
            }
        });
    }

    public void listSave() {
        for (String i : listItem) {
            listItemSave.add(i);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    // - 마커 생성
    public void markerSave(TMapView tmapview, ArrayList poiItem, int i) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.markerline_orange);
        TMapPOIItem item = (TMapPOIItem) poiItem.get(i);
        TMapPoint tMapPoint = item.getPOIPoint();
        markerItem1 = new TMapMarkerItem();
        markerItem1.setTMapPoint(tMapPoint);
        markerItem1.setName(item.getPOIName());
        markerItem1.setCanShowCallout(true);
        markerItem1.setIcon(bitmap);
        markerItem1.setCalloutTitle(item.getPOIName().toString());
        markerItem1.setCalloutSubTitle(item.getPOIAddress().replace("null", ""));
        tmapview.addMarkerItem("marker" + i, markerItem1);
        listItem.add(item.getPOIName().toString());
        if (i == 0) {
            tmapview.setCenterPoint(item.getPOIPoint().getLongitude(), item.getPOIPoint().getLatitude() - 0.006);
            tmapview.setZoomLevel(14);
        }
    }


    // - GPS 현재위치 받아오기
    @Override
    public void onLocationChange(Location location) {
        if (location != null) {
            TMapPoint tMapPointMy = tMapGPS.getLocation();
            mylatitude = tMapPointMy.getLatitude();
            mylongitude = tMapPointMy.getLongitude();
            tmapview.setLocationPoint(mylongitude, mylatitude); // 현재위치로 표시될 좌표의 위도, 경도를 설정
            tmapview.setIconVisibility(true);
            tmapview.setCenterPoint(mylongitude, mylatitude, true); // 현재 위치로 이동
            startPoint = tMapPointMy;
            Log.d("현재위치1", "" + tMapPointMy.getLatitude() + "," + tMapPointMy.getLongitude());

        }
    }


    // - 검색 동작 스레드
    class ThreadFind extends Thread {
        EditText findLocation = (EditText) findViewById(R.id.findLocation);
        @Override
        public void run() {
            poiItem = new ArrayList<>();
            try {
                listItem = new ArrayList<String>();
                strData = findLocation.getText().toString();
                try {
                    poiItem = tmapdata.findAroundNamePOI(startPoint, strData, 4, 100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(poiItem.isEmpty()) {
                    poiItem = tmapdata.findAllPOI(strData);
                    tmapdata.findAllPOI(strData, new TMapData.FindAllPOIListenerCallback() {
                        @Override
                        public void onFindAllPOI(ArrayList<TMapPOIItem> arrayList) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                markerSave(tmapview, poiItem, i);
                            }
                        }
                    });
                } else {
                    poiItem = tmapdata.findAroundNamePOI(startPoint, strData);
                    tmapdata.findAroundNamePOI(startPoint, strData, new TMapData.FindAroundNamePOIListenerCallback() {
                        @Override
                        public void onFindAroundNamePOI(ArrayList<TMapPOIItem> arrayList) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                markerSave(tmapview, poiItem, i);
                            }
                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}