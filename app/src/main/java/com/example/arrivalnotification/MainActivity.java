package com.example.arrivalnotification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.address_info.TMapAddressInfo;
import com.skt.Tmap.poi_item.TMapPOIItem;
import static com.example.arrivalnotification.culture_find.result;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {

    // - 리스트 전역변수 설정
    ArrayAdapter<String> adapter;
    ArrayList<String> listItem;
    ArrayList<String> listItemSave;
    double mylongitude;
    double mylatitude;
    // - 전역변수 설정
    private long backKeyPressedTime = 0;
    private Toast toast;
    int num = -1;
    String strData;
    public static String spinnerData="1";
    ArrayList<String> locationData = new ArrayList<>();
    ArrayList<TMapPOIItem> poiItem;
    ArrayList<TMapPOIItem> poiItem1 = new ArrayList<>();
    ArrayList<String> poiString = new ArrayList<>();
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
    String sort;
    int vus = 0, dud = 0, dh = 0, zk = 0, eh = 0, sh = 0, ekd = 0, ak = 0,dms = 0, pc = 0, qhf = 0, tlr = 0, ans = 0, qor= 0, ty= 0,rhd= 0, finalTotal=0;
    public static String city = " ";
    public static int near =1;
    public static TMapPoint state;
    public static double stateLon;
    public static double stateLan;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

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
                        intent=new Intent(getApplicationContext(),Alarm.class);
                        startActivity(intent);
                        return true;
                    case R.id.change:
                        intent=new Intent(getApplicationContext(),culture_find.class);
                        startActivity(intent);
                        return true;
                    case R.id.weather:

                    case R.id.star:

                }
                return false;
            }
        });

        this.alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // - xml 관련
        EditText findLocation = (EditText) findViewById(R.id.findLocation);
        Button findLocationButton = (Button) findViewById(R.id.button);
        Button endPointButton = (Button) findViewById(R.id.button2);
        LinearLayout linearLayoutTmap = (LinearLayout) findViewById(R.id.linearLayoutTmap);
      //  ListView listView = (ListView) findViewById(R.id.listview);
       // ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);


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

        // - 마커 우측 그림 클릭 이벤트
        tmapview.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @Override
            public void onCalloutRightButton(TMapMarkerItem tMapMarkerItem) {
                if(spinnerData.contains("1")){
                    near=1;
                }else if(spinnerData.contains("2")){
                    near=2;
                }else if(spinnerData.contains("3")){
                    near=3;
                }else if(spinnerData.contains("4")){
                    near=4;
                }else{
                    near=5;
                }
                String id = tMapMarkerItem.getID();
                Bitmap bitmap_green = BitmapFactory.decodeResource(getResources(), R.drawable.markerline_green);
                tMapMarkerItem.setIcon(bitmap_green);
                Intent intent=new Intent(getApplicationContext(),Marker_data.class);
                intent.putExtra("주", tMapMarkerItem.getCalloutTitle());
                intent.putExtra("서브", tMapMarkerItem.getCalloutSubTitle());
                ArrayList<TMapPOIItem> poiItem2;
                String category = result;
                poiItem2 = getAroundCF(tMapMarkerItem.getTMapPoint(), "편의점");//주변 편의시설 리스트
                intent.putExtra("편의시설", poiString);
                intent.putExtra("개수", "(총 "+finalTotal+"개)");
                intent.putExtra("상위",sort);
                try {
                    intent.putExtra("설정", Integer.toString(near));
                }catch(Exception e){
                    e.printStackTrace();
                }
                state=tMapMarkerItem.getTMapPoint();
                stateLan=state.getLatitude();
                stateLon=state.getLongitude();
                try {
                    Thread.sleep(1500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });

        // - 리스트 데이터를 넣을 준비
        listItem = new ArrayList<String>();
        listItemSave = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItemSave);
        //listView.setAdapter(adapter);


        // - 키보드 표시제어
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        // - 검색버튼 스레드 동작 구현
        threadfind = new ThreadFind();
        findLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tmapview.removeAllMarkerItem();
                tmapview.removeAllTMapCircle();
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
                //scrollView.setVisibility(View.VISIBLE);
                imm.hideSoftInputFromWindow(findLocationButton.getWindowToken(), 0);
            }
        });

      /*  findLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollView.setVisibility(View.INVISIBLE);
            }
        });*/

        // - 도착지로 버튼 클릭 이벤트
        endPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(spinnerData.contains("1")){
                        near=1;
                    }else if(spinnerData.contains("2")){
                        near=2;
                    }else if(spinnerData.contains("3")){
                        near=3;
                    }else if(spinnerData.contains("4")){
                        near=4;
                    }else{
                        near=5;
                    }
                    Bitmap check = BitmapFactory.decodeResource(getResources(), R.drawable.check);
                    TMapCircle tMapCircle = new TMapCircle();
                    tMapCircle.setCenterPoint(endPoint);
                    tMapCircle.setRadius(near*1000);
                    tMapCircle.setCircleWidth(2);
                    tMapCircle.setLineColor(Color.BLUE);
                    tMapCircle.setAreaColor(Color.GRAY);
                    tMapCircle.setAreaAlpha(100);
                    tmapview.addTMapCircle("circle1", tMapCircle);
                    try {
                        Location l1 = new Location("");
                        l1.setLatitude(tmapview.getLatitude());
                        l1.setLongitude(tmapview.getLongitude());
                        Location l2 = new Location("");
                        l2.setLatitude(endPoint.getLatitude());
                        l2.setLongitude(endPoint.getLongitude());
                        float distance1 = l1.distanceTo(l2) / 1000;
                        Log.d("거리", "" + distance1);
                        if (near > distanceKm(tmapview, endPoint.getLatitude(), endPoint.getLongitude())) {
                            Log.d("목표 접근", "" + endPoint.getLatitude() + "," + endPoint.getLongitude());
                            Toast.makeText(MainActivity.this,"주변에 도착했습니다",Toast.LENGTH_SHORT).show();
                            start1();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //markerItem1.setIcon(check);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        markerClick();

        // - 리스트 아이템 동작
        Bitmap bitmap_orange = BitmapFactory.decodeResource(getResources(), R.drawable.markerline_orange);
     /*   listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onIte   mClick(AdapterView<?> adapterView, View view, int i, long l) {
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
        });*/





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
        Bitmap bitmapClick = BitmapFactory.decodeResource(getResources(), R.drawable.plus);
        markerItem1.setCalloutRightButtonImage(bitmapClick);
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
                Log.d("현재위치", "" + tMapPointMy.getLatitude() + "," + tMapPointMy.getLongitude());

                try {
                    Location l1 = new Location("");
                    l1.setLatitude(tmapview.getLatitude());
                    l1.setLongitude(tmapview.getLongitude());
                    Location l2 = new Location("");
                    l2.setLatitude(endPoint.getLatitude());
                    l2.setLongitude(endPoint.getLongitude());
                    float distance1 = l1.distanceTo(l2) / 1000;
                    Log.d("거리", "" + distance1);
                    if(spinnerData.contains("1")){
                        near=1;
                    }else if(spinnerData.contains("2")){
                        near=2;
                    }else if(spinnerData.contains("3")){
                        near=3;
                    }else if(spinnerData.contains("4")){
                        near=4;
                    }else{
                        near=5;
                    }
                    //Log.d("목표 위치", "" + endPoint.getLatitude() + "," + endPoint.getLongitude());
                    // Toast.makeText(MainActivity.this,"위치"+tmapview.getLatitude()+"and"+tmapview.getLongitude(),Toast.LENGTH_SHORT).show();
                    if (near > distanceKm(tmapview, endPoint.getLatitude(), endPoint.getLongitude())) {
                        Log.d("목표 접근", "" + endPoint.getLatitude() + "," + endPoint.getLongitude());
                        Toast.makeText(MainActivity.this,"주변에 도착했습니다",Toast.LENGTH_SHORT).show();
                        start1();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }


    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "뒤로 가기를 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("정말로 종료하시겠습니까?");
            builder.setTitle("종료 알림창")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.setTitle("종료 알림창");
            alert.show();
        }
    }

    public ArrayList  getAroundCF(TMapPoint tpoint1, String category) {

        TMapData tmapdata = new TMapData();
        ArrayList<TMapPOIItem> poiItem2 = new ArrayList<>();
        poiItem = new ArrayList<>();
        poiString = new ArrayList<>();
        String result = culture_find.result;

        Thread thread = new Thread(() -> {
            if(!result.isEmpty()) {
                try {
                    if (result.contains("영화관")) {
                        poiItem = tmapdata.findAroundNamePOI(tpoint1, "영화관", 1, 60);
                        dud = 0;
                        if (null != poiItem) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                poiString.add(item.getPOIName());
                                dud++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (result.contains("편의점")) {
                        poiItem = tmapdata.findAroundNamePOI(tpoint1, "편의점", 1, 60);
                        vus = 0;
                        if (null != poiItem) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                poiString.add(item.getPOIName());
                                vus++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (result.contains("오락실")) {
                        poiItem = tmapdata.findAroundNamePOI(tpoint1, "오락실", 1, 60);
                        dh = 0;
                        if (null != poiItem) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                poiString.add(item.getPOIName());
                                dh++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (result.contains("카페")) {
                        poiItem = tmapdata.findAroundNamePOI(tpoint1, "카페", 1, 60);
                        zk = 0;
                        if (null != poiItem) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                poiString.add(item.getPOIName());
                                zk++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (result.contains("도서관")) {
                        poiItem = tmapdata.findAroundNamePOI(tpoint1, "도서관", 1, 60);
                        eh = 0;
                        if (null != poiItem) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                poiString.add(item.getPOIName());
                                eh++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (result.contains("노래방")) {
                        poiItem = tmapdata.findAroundNamePOI(tpoint1, "노래방", 1, 60);
                        sh = 0;
                        if (null != poiItem) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                poiString.add(item.getPOIName());
                                sh++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (result.contains("당구장")) {
                        poiItem = tmapdata.findAroundNamePOI(tpoint1, "당구장", 1, 60);
                        ekd = 0;
                        if (null != poiItem) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                poiString.add(item.getPOIName());
                                ekd++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (result.contains("마트")) {
                        poiItem = tmapdata.findAroundNamePOI(tpoint1, "마트", 1, 60);
                        ak = 0;
                        if (null != poiItem) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                poiString.add(item.getPOIName());
                                ak++;
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (result.contains("은행")) {
                        poiItem = tmapdata.findAroundNamePOI(tpoint1, "은행", 1, 60);
                        dms = 0;
                        if (null != poiItem) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                poiString.add(item.getPOIName());
                                dms++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (result.contains("피시방")) {
                        poiItem = tmapdata.findAroundNamePOI(tpoint1, "피시방", 1, 60);
                        pc = 0;
                        if (null != poiItem) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                poiString.add(item.getPOIName());
                                pc++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (result.contains("볼링장")) {
                        poiItem = tmapdata.findAroundNamePOI(tpoint1, "볼링장", 1, 60);
                        qhf = 0;
                        if (null != poiItem) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                poiString.add(item.getPOIName());
                                qhf++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (result.contains("식당")) {
                        poiItem = tmapdata.findAroundNamePOI(tpoint1, "식당", 1, 60);
                        tlr = 0;
                        if (null != poiItem) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                poiString.add(item.getPOIName());
                                tlr++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (result.contains("문화시설")) {
                        poiItem = tmapdata.findAroundNamePOI(tpoint1, "문화시설", 1, 60);
                        ans = 0;
                        if (null != poiItem) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                poiString.add(item.getPOIName());
                                ans++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (result.contains("백화점")) {
                        poiItem = tmapdata.findAroundNamePOI(tpoint1, "백화점", 1, 60);
                        qor = 0;
                        if (null != poiItem) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                poiString.add(item.getPOIName());
                                qor++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (result.contains("쇼핑센터")) {
                        poiItem = tmapdata.findAroundNamePOI(tpoint1, "쇼핑센터", 1, 60);
                        ty = 0;
                        if (null != poiItem) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                poiString.add(item.getPOIName());
                                ty++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (result.contains("공연장")) {
                        poiItem = tmapdata.findAroundNamePOI(tpoint1, "공연장", 1, 60);
                        rhd = 0;
                        if (null != poiItem) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                poiString.add(item.getPOIName());
                                rhd++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    poiItem = tmapdata.findAroundNamePOI(tpoint1, "", 1, 60);
                    if (null != poiItem) {
                        for (int i = 0; i < poiItem.size(); i++) {
                            TMapPOIItem item = poiItem.get(i);
                            poiString.add(item.getPOIName());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            int []arr = {dud,vus,dh,zk,eh,sh,ekd,ak,dms,pc,qhf,tlr,ans,qor,ty,rhd};

            int total=dud+vus+dh+zk+eh+sh+ekd+ak+dms+pc+qhf+tlr+ans+qor+ty+rhd;

            for(int i=1; i < arr.length; i++){
                for(int j=i ; j >= 1; j--){

                    if(arr[j] < arr[j-1]){  //한 칸씩 왼쪽으로 이동
                        int tmp = arr[j];
                        arr[j] = arr[j-1];
                        arr[j-1] = tmp;
                    }else break;  //자기보다 작은 데이터를 만나면 그 위치에서 멈춤

                }
            }
            if(arr[15]==0){
                sort="문화시설이 없습니다";
            }
            else if(arr[15]==dud) {
                sort="영화관";
                dud=0;
            }else if(arr[15]==vus) {
                sort="편의점";
                vus=0;
            }else if(arr[15]==dh) {
                sort="오락실";
                dh=0;
            }else if(arr[15]==zk) {
                sort="카페";
                zk=0;
            }else if(arr[15]==eh) {
                sort="도서관";
                eh=0;
            }else if(arr[15]==sh) {
                sort="노래방";
                sh=0;
            }else if(arr[15]==ekd) {
                sort="당구장";
                ekd=0;
            }else if(arr[15]==ak) {
                sort="마트";
                ak=0;
            }else if(arr[15]==dms) {
                sort="은행";
                dms=0;
            }else if(arr[15]==pc) {
                sort="피시방";
                pc=0;
            }else if(arr[15]==qhf) {
                sort="볼링장";
                qhf=0;
            }else if(arr[15]==tlr) {
                sort="식당";
                tlr=0;
            }else if(arr[15]==ans) {
                sort="문화시설";
                ans=0;
            }else if(arr[15]==qor) {
                sort="백화점";
                qor=0;
            }else if(arr[15]==ty) {
                sort="쇼핑센터";
                ty=0;
            }else if(arr[15]==rhd) {
                sort="공연장";
                rhd=0;
            }

            if(arr[14]==0){
                sort+=" ";
            }
            else if(arr[14]==dud) {
                sort+="  영화관";
                dud=0;
            }else if(arr[14]==vus) {
                sort+="  편의점";
                vus=0;
            }else if(arr[14]==dh) {
                sort+="  오락실";
                dh=0;
            }else if(arr[14]==zk) {
                sort+="  카페";
                zk=0;
            }else if(arr[14]==eh) {
                sort+="  도서관";
                eh=0;
            }else if(arr[14]==sh) {
                sort+="  노래방";
                sh=0;
            }else if(arr[14]==ekd) {
                sort+="  당구장";
                ekd=0;
            }else if(arr[14]==ak) {
                sort+=" 마트";
                ak=0;
            }else if(arr[14]==dms) {
                sort+=" 은행";
                dms=0;
            }else if(arr[14]==pc) {
                sort+=" 피시방";
                pc=0;
            }else if(arr[14]==qhf) {
                sort+=" 볼링장";
                qhf=0;
            }else if(arr[14]==tlr) {
                sort+=" 식당";
                tlr=0;
            }else if(arr[14]==ans) {
                sort+=" 문화시설";
                ans=0;
            }else if(arr[14]==qor) {
                sort+=" 백화점";
                qor=0;
            }else if(arr[14]==ty) {
                sort+=" 쇼핑센터";
                ty=0;
            }else if(arr[14]==rhd) {
                sort+=" 공연장";
                rhd=0;
            }
            finalTotal=total;
        });
        thread.start();
        try {
            thread.sleep(1000);
        }catch(Exception e){
            e.printStackTrace();
        }
        return poiItem;
    }


    /* 알람 시작 */
    private void start1() {
        // 시간 설정
        Calendar calendar = Calendar.getInstance();

        // Receiver 설정
        Intent intent = new Intent(this, AlarmReceiver.class);
        // state 값이 on 이면 알람시작, off 이면 중지
        intent.putExtra("state", "on");

        this.pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // 알람 설정
        this.alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
 }

    /* 알람 중지 */
    private void stop() {
        if (this.pendingIntent == null) {
            return;
        }

        // 알람 취소
        this.alarmManager.cancel(this.pendingIntent);

        // 알람 중지 Broadcast
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("state","off");

        sendBroadcast(intent);

        this.pendingIntent = null;
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