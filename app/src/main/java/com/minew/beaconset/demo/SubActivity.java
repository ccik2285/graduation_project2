package com.minew.beaconset.demo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.minew.beaconset.BluetoothState;
import com.minew.beaconset.ConnectionState;
import com.minew.beaconset.MinewBeacon;
import com.minew.beaconset.MinewBeaconConnection;
import com.minew.beaconset.MinewBeaconConnectionListener;
import com.minew.beaconset.MinewBeaconManager;
import com.minew.beaconset.MinewBeaconManagerListener;
import com.minew.beaconset.R;

import pl.polidea.view.ZoomView;

import java.util.Collections;
import java.util.List;


public class SubActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "AnimationActivity";
    private LinearLayout ll1;
    private Button btn1;
    private ImageView btn2;
    private Button btn3;
    private float screenWidth;
    private float screenHeight;
    private int section =0;
    public static String arr = "";
    private float toX = 0, fromY = 0;
    private float toY = 0;
    private int nowX=0, nowY = 0;
    private int cnt = Optimal_Distance.cnt;
    private int lw = 0;
    private int rw = 480;
    private int uw = 0;
    private int dw = 650;
    private int mx = 250;
    private int my = 350;
  //  public static int opt[];
    private Optimal_Distance test ;

    Optimal_Distance SubActivity2 = new Optimal_Distance().start();
  //  public static int opt[] = SubActivity2.arr;
    public int opt[] = SubActivity2.arr;
    private float x = nowX;
    private float y = nowY;

    public static int now_x = 300;
    public static int now_y = 300;
    public static int [] item_x = home.item_location_x;
    public static int [] item_y = home.item_location_y;
/*    private float nowx = Optimal_Distance.nowx;
    private float nowy = Optimal_Distance.nowy;*/

    /////////////////////////
    //LinearLayout linearView;
    LinearLayout linearView;
    FrameLayout frameView;

    int[] pos=new int[4];

    public static TextView tv_sub;
    private TextView dis_result;
    BeaconListAdapter SubActivity = new BeaconListAdapter();

    private TextView tv_id, tv_pass;
    private RecyclerView       mRecycle;
    private MinewBeaconManager mMinewBeaconManager;

    private subBeaconListAdapter  mAdapter;
    UserRssi comp = new UserRssi();
    private       ProgressDialog mpDialog;
    public static MinewBeacon    clickBeacon;
    private static final int REQUEST_ENABLE_BT = 2;

    final float a[] = subBeaconListAdapter.i;
    private TextView tv1;
    private TextView tv2;
    private TextView tv3;
    private TextView tv4;
    private TextView tv5;
    private TextView item_id[] = new TextView[4];
    private TextView test_view;

    private float[] filtered = new float[4];
    private KalmanFilter Kdis1;
    private KalmanFilter Kdis2;
    private KalmanFilter Kdis3;
    private KalmanFilter Kdis4;
    private float[] sum_dis = new float[4];
    private int count = 0;

    private String str1 = SubActivity.distance;
    private String str2 = SubActivity.location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        View v = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.zoom_item, null, false);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        final ZoomView zoomView = new ZoomView(this);
        zoomView.addView(v);
        zoomView.setLayoutParams(layoutParams);
        zoomView.setMiniMapEnabled(false); // 좌측 상단 검은색 미니맵 설정
        zoomView.setMaxZoom(4f); // 줌 Max 배율 설정  1f 로 설정하면 줌 안됩니다.
        zoomView.setMiniMapCaption("Mini Map Test"); //미니 맵 내용
        zoomView.setMiniMapCaptionSize(20); // 미니 맵 내용 글씨 크기 설정
        zoomView.zoomTo(1,x,y);

        RelativeLayout container = (RelativeLayout) findViewById(R.id.container1);
        container.addView(zoomView);


        initView();
        initManager();
        checkBluetooth();
        checkLocation();

        dialogshow();

        init(zoomView);
        //createTextView();
        mMinewBeaconManager.startService();

        Kdis1 = new KalmanFilter(0.0f);
        Kdis2= new KalmanFilter(0.0f);
        Kdis3 = new KalmanFilter(0.0f);
        Kdis4 = new KalmanFilter(0.0f);

        tv1= findViewById(R.id.test1);
        tv2= findViewById(R.id.test2);
        tv3= findViewById(R.id.test3);
        tv4= findViewById(R.id.test4);
        tv5 = findViewById(R.id.test5);

        btn2.setX(nowX);  //이미지 초기 값 -> 정 가운데
        btn2.setY(nowY);
        btn2.getLayoutParams().height = 123;
        btn2.getLayoutParams().width = 123;
        (new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                while (!Thread.interrupted())
                    try
                    {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() // start actions in UI thread
                        {

                            @Override
                            public void run()
                            {
                                (new Thread(new Runnable() {

                                    @Override
                                    public void run() {
                                        while (!Thread.interrupted())
                                            try {
                                                Thread.sleep(1000);
                                                runOnUiThread(new Runnable() // start actions in UI thread
                                                {
                                                    @Override
                                                    public void run() {
                                                        filtered[0] = (float) Kdis1.update(a[0]);
                                                        filtered[1] = (float) Kdis2.update(a[1]);
                                                        filtered[2] = (float) Kdis3.update(a[2]);
                                                        filtered[3] = (float) Kdis4.update(a[3]);
                                                        if(count == 5){
                                                            for(int i=0; i<4; i++){
                                                                sum_dis[i] = sum_dis[i] / count;
                                                            }
                                                            tv1.setText("정현이꺼 " + sum_dis[0]);
                                                            tv2.setText("은윤이꺼 " + sum_dis[1]);
                                                            tv3.setText("충헌이꺼 " + sum_dis[2]);
                                                            tv4.setText("교수님꺼 " + sum_dis[3]);
                                                            count = 0;
                                                            for(int i=0; i<4; i++){
                                                                sum_dis[i] = 0;
                                                            }
                                                        }
                                                        else {
                                                            for(int i=0; i<4; i++){
                                                                sum_dis[i] += filtered[i];
                                                            }
                                                            count++;
                                                        }

                                                        // for(int i=0; i<cnt; i++
                                                        //+"x:"+linearView.getWidth()+" y:"+linearView.getHeight()
                                                        // );
                                                        //  tv5.setText(Optimal_Distance.result_array[0]+ " " + Optimal_Distance.result_array[1]+ " " + Optimal_Distance.result_array[2]+ " " + Optimal_Distance.result_array[3]+ " " );
                                                        tv_sub = findViewById(R.id.tv_sub);
                                                        tv_sub.setText(Integer.toString(home.item_location_x[Optimal_Distance.arr[0]]*10) + " " + Integer.toString(home.item_location_y[Optimal_Distance.arr[0]]*10) );
                                                    }
                                                });
                                            } catch (InterruptedException e) {
                                                // ooops
                                            }
                                    }
                                })).start();
                            }
                        });
                    }
                    catch (InterruptedException e)
                    {
                        // ooops
                    }

            }
        })).start();
    }

    private void createTextView(float x, float y, String txt){
        //(float x, float y, String txt):인자

        TextView textViewNm = new TextView(getApplicationContext());
        textViewNm.setText(txt);
        textViewNm.setTextSize(10);
        textViewNm.setTextColor(Color.parseColor("#CC0066"));
        textViewNm.setId(0);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.leftMargin=(int)x;
        params.topMargin=(int)y;

        textViewNm.setLayoutParams(params);
        textViewNm.setBackgroundColor(Color.rgb(0,255, 0));
        //textViewNm.setText(txt);
        //linearView=findViewById(R.id.mapView);
        //linearView.addView(textViewNm);
        frameView = findViewById(R.id.frameView);
        frameView.addView(textViewNm);

    }

    private void init(View v){
        ll1 = findViewById(R.id.ll1);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);


        test_view = v.findViewById(R.id.print_test);

        btn1.setOnClickListener((View.OnClickListener) this);
        btn2.setOnClickListener((View.OnClickListener) this);
        btn3.setOnClickListener((View.OnClickListener) this);

        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);

        Log.e(TAG, "Width : " + point.x + " , Height : " + point.y);

        // 각 아이템의 위치에 해당 index

        //createTextView(100, 0, "(100,0)");
        //createTextView(-200, 100, "(-200,100)");
        //createTextView(500, 500, "(540,300)");
        //createTextView(linearView.getWidth()/2, linearView.getHeight()/2, "mid");

        test_view.setX(0);
        test_view.setY(0);
        test_view.setText("(0,0)");


        for(int i = 0; i < home.Basket_index; i++){
            if(item_x[i]>1200)  item_x[i] = 1200;
            if(item_y[i]>1000)  item_y[i]=1000;
            createTextView(item_y[i]*10,item_x[i]*10,Integer.toString(home.id[i]));
        }

        screenWidth = point.x;
        screenHeight = point.y;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.btn1 :
                fromY = screenHeight - ll1.getHeight() - btn2.getHeight();
                Log.e(TAG, "fromX : " + toX + ",  fromY : " +fromY);

                (new Thread(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        while (!Thread.interrupted())
                            try
                            {
                                Thread.sleep(10);
                                runOnUiThread(new Runnable() // start actions in UI thread
                                {

                                    @Override
                                    public void run()
                                    {

                                        //4구역

                                        //6구역
                                        if(a[2]<=1.1&&a[1]<=1.1&&(a[1]>1.1||a[3]>1.1)){
                                            if(nowX<=rw&&nowY<my){
                                                nowY++; nowX++;
                                            }
                                            if(nowX<=rw&&nowY>my){
                                                nowX++; nowY--;
                                            }
                                            if(nowX>rw&&nowY>my){
                                                nowX=rw; nowY--;
                                            }
                                            if(nowX>rw&&nowY<my){
                                                nowX=rw; nowY++;
                                            }
                                            section=6;
                                        }

                                        //2구역
                                        else if(a[0]<=2&&a[2]<=2&&a[1]>2&&a[3]>2){
                                            if(nowX<mx&&nowY<uw){
                                                nowY=uw; nowX++;
                                            }
                                            if(nowX>mx&&nowY<uw){
                                                nowX--; nowY=uw;
                                            }
                                            if(nowX<mx&&nowY>=uw){
                                                nowX++; nowY--;
                                            }

                                            if(nowX>mx&&nowY>=uw){
                                                nowX--; nowY--;
                                            }
                                            section=2;
                                        }

                                        //8구역
                                        else if(a[3]<=1&&a[1]<=1&&a[2]>1&&a[0]>1) {
                                            if (nowX < mx && nowY > dw) {
                                                nowY = dw;
                                                nowX++;
                                            }
                                            if (nowX > mx && nowY >uw ) {
                                                nowX--;
                                                nowY = uw;
                                            }
                                            if (nowX < mx && nowY <= uw) {
                                                nowX++;
                                                nowY++;
                                            }

                                            if (nowX > mx && nowY <= uw) {
                                                nowX--;
                                                nowY++;
                                            }
                                            section=8;
                                        }


                                        //1구역
                                        else if(a[0]<a[1]&&a[0]<a[2]&&a[0]<a[3]){
                                            if(nowX<lw&&nowY<uw){
                                                nowX=lw;
                                                nowY=uw;
                                            }
                                            if(nowX<lw&&nowY>uw){
                                                nowX=lw;
                                                nowY--;
                                            }
                                            if(nowX>=lw&&nowY<=uw){
                                                nowX--;
                                                nowY=uw;
                                            }
                                            nowX--;
                                            nowY--;
                                            section=1;

                                        }

                                        //9구역
                                        else if(a[1]<a[0]&&a[1]<a[2]&&a[1]<a[3]){

                                            if(nowX>rw&&nowY>dw){
                                                nowX=rw;
                                                nowY=dw;
                                            }

                                            if( nowX>rw&&nowY<=dw){
                                                nowX=rw;
                                                nowY++;
                                            }
                                            if(nowX<=rw&&nowY>dw){
                                                nowY=dw;
                                                nowX++;
                                            }
                                            nowX++;
                                            nowY++;
                                            section=9;



                                        }
                                        //3구역
                                        else if(a[2]<a[0]&&a[2]<a[1]&&a[2]<a[3]){

                                            if(nowX>rw&&nowY<uw){
                                                nowX=rw;
                                                nowY=uw;
                                            }

                                            if( nowX<=rw&&nowY<uw){
                                                nowX++;
                                                nowY=uw;
                                            }
                                            if(nowX>rw&&nowY>=uw){
                                                nowY--;
                                                nowX=rw;
                                            }
                                            nowX++;
                                            nowY--;
                                            section=3;


                                        }

                                        //7구역
                                        else if(a[3]<a[0]&&a[3]<a[1]&&a[3]<a[2]){

                                            if(nowX<lw&&nowY>dw){
                                                nowX=lw;
                                                nowY=dw;
                                            }

                                            if(nowX<lw&&nowY<=dw){
                                                nowX=lw;
                                                nowY++;
                                            }

                                            if( nowX>=lw&&nowY>dw){
                                                nowX--;
                                                nowY=dw;
                                            }

                                            nowX--;
                                            nowY++;
                                            section =7;

                                        }

                                        //중간구역
//                                        else if(a[0]<=1.5&&a[2]<=1.5&&a[1]<=1.5&&a[3]<=1.5){
//                                            if(nowX>=0&&nowX<=480&&nowY<=650&&nowY>=0){
//
//                                                if(section==1){
//                                                    nowX++; nowY++;
//                                                }
//                                                else if(section==2){
//                                                    nowY++;
//                                                }
//                                                else if(section==3){
//                                                    nowX--; nowY++;
//                                                }
//                                                else if(section==4){
//                                                    nowX++;
//                                                }
//                                                else if(section==6){
//                                                    nowX--;
//                                                }
//                                                else if(section==7){
//                                                    nowX++; nowY--;
//                                                }
//                                                else if(section==8){
//                                                    nowY--;
//                                                }
//                                                else if(section==9){
//                                                    nowX--; nowY--;
//                                                }
//
//                                            }
//
//
//                                        }


                                        btn2.setX(nowX);  //이미지 초기 값 -> 정 가운데
                                        btn2.setY(nowY);
                                        now_x=nowX;
                                        now_y =nowY;
                                        TranslateAnimation animation = new TranslateAnimation(nowX,nowX, nowY, nowY);

                                        animation.setDuration(5000);
                                        animation.setFillAfter(false);
                                        animation.setFillEnabled(true);
                                        animation.setAnimationListener(new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationStart(Animation animation) {
                                            }
                                            @Override
                                            public void onAnimationEnd(Animation animation) {
                                                pos[0]= btn2.getLeft();
                                                pos[1]= btn2.getTop() ;
                                                pos[2]= btn2.getRight()+ (int)fromY;;
                                                pos[3]= btn2.getTop();
                                                btn2.layout(pos[0], pos[1], pos[2], pos[3]);
                                                Log.e(TAG, " 1: " + btn2.getLeft() + " , 2 : " + btn2.getTop() + "  , 3 : "  + btn2.getRight() + "  , 4 : " + btn2.getBottom());
                                            }
                                            @Override
                                            public void onAnimationRepeat(Animation animation) {

                                            }
                                        });
                                        btn2.startAnimation(animation);

                              /*          if(toX==500)nowX+=41;
                                        else nowX-=41;*/

                                    }
                                });
                            }
                            catch (InterruptedException e)
                            {
                                // ooops
                            }
                    }
                })).start();

                break;
            case R.id.btn3 :
           //     Optimal_Distance SubActivity2 = new Optimal_Distance().start();
          //      opt = SubActivity2.arr;
                for(int i=0; i<home.Basket_index; i++){
                    arr += opt[i] + " ";
                }
                tv5.setText(arr);
                break;
            default:
                break;
        }
    }

    private void initView() {
        mRecycle = (RecyclerView) findViewById(R.id.sub_recyeler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycle.setLayoutManager(layoutManager);
        mAdapter = new subBeaconListAdapter();
        mRecycle.setAdapter(mAdapter);
        mRecycle.addItemDecoration(new RecycleViewDivider(this, LinearLayout.HORIZONTAL));
    }
    private void initManager() {
        mMinewBeaconManager = MinewBeaconManager.getInstance(this);
    }
    private void checkLocation(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},101);
        }
    }
    private void checkBluetooth() {
        BluetoothState bluetoothState = mMinewBeaconManager.checkBluetoothState();
        switch (bluetoothState) {
            case BluetoothStateNotSupported:
                Toast.makeText(this, "Not Support BLE", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case BluetoothStatePowerOff:
                showBLEDialog();
                break;
            case BluetoothStatePowerOn:
                break;
        }
    }
    private void initListener() {
        mMinewBeaconManager.setMinewbeaconManagerListener(new MinewBeaconManagerListener() {
            @Override
            public void onUpdateBluetoothState(BluetoothState state) {
                switch (state) {
                    case BluetoothStatePowerOff:
                        Toast.makeText(getApplicationContext(), "bluetooth off", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothStatePowerOn:
                        Toast.makeText(getApplicationContext(), "bluetooth on", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            @Override
            public void onRangeBeacons(List<MinewBeacon> beacons) {
                Collections.sort(beacons, comp);
                mAdapter.setData(beacons);
            }
            @Override
            public void onAppearBeacons(List<MinewBeacon> beacons) {
            }
            @Override
            public void onDisappearBeacons(List<MinewBeacon> beacons) {
            }
        });
        mAdapter.setOnItemClickLitener(new subBeaconListAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                mpDialog.setMessage(getString(R.string.connecting)
                        + mAdapter.getData(position).getName());
                mpDialog.show();
                mMinewBeaconManager.stopScan();
                //connect to beacon
                MinewBeacon minewBeacon = mAdapter.getData(position);
                MinewBeaconConnection minewBeaconConnection = new MinewBeaconConnection(SubActivity.this, minewBeacon);
                minewBeaconConnection.setMinewBeaconConnectionListener(minewBeaconConnectionListener);
                minewBeaconConnection.connect();
            }
            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
    }
    //connect listener;
    MinewBeaconConnectionListener minewBeaconConnectionListener = new MinewBeaconConnectionListener() {
        @Override
        public void onChangeState(MinewBeaconConnection connection, ConnectionState state) {
            switch (state) {
                case BeaconStatus_Connected:
                    mpDialog.dismiss();
                    Intent intent = new Intent(SubActivity.this, DetilActivity.class);
                    intent.putExtra("mac", connection.setting.getMacAddress());
                    startActivity(intent);
                    break;
                case BeaconStatus_ConnectFailed:
                case BeaconStatus_Disconnect:
                    if (mpDialog != null) {
                        mpDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "연결이 끊어졌습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
            }
        }
        @Override
        public void onWriteSettings(MinewBeaconConnection connection, boolean success) {
        }
    };

    @Override
    protected void onResume() {
        mMinewBeaconManager.startScan();
        initListener();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mMinewBeaconManager.stopScan();
        super.onPause();
    }

    protected void dialogshow() {
        mpDialog = new ProgressDialog(SubActivity.this);
        mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mpDialog.setTitle(null);//
        mpDialog.setIcon(null);//
        mpDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface arg0) {
            }
        });
        mpDialog.setCancelable(true);//
        mpDialog.setCanceledOnTouchOutside(false);
    }
    private void showBLEDialog() {
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                mMinewBeaconManager.startScan();
                break;
        }
    }
    @Override
    protected void onDestroy() {
        mMinewBeaconManager.stopService();
        super.onDestroy();
    }
    class KalmanFilter {
        public double Q = 0.00000001;
        public double R = 0.00001;
        public double X = 0, P = 1, K;

        KalmanFilter(double initValue) {
            X = initValue;
        }

        private void measurementUpdate() {
            K = (P + Q) / (P + Q + R);
            P = R * (P + Q) / (R + P + Q);
        }

        public double update(double measurement) {
            measurementUpdate();
            X = X + (measurement - X) * K;

            return X;
        }
    }
}