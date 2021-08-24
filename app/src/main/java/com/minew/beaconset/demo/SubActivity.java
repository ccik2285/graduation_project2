package com.minew.beaconset.demo;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
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

import org.w3c.dom.Text;

import java.util.Collections;
import java.util.List;

public class SubActivity extends AppCompatActivity {


    private TextView tv_sub;
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



    private TextView tv1;
    private TextView tv2;
    private TextView tv3;



    private String str1 = SubActivity.distance;
    private String str2 = SubActivity.location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        initView();
        initManager();
        checkBluetooth();
        checkLocation();

        dialogshow();
        mMinewBeaconManager.startService();


        final float a[] = subBeaconListAdapter.i;

        tv1= findViewById(R.id.test1);
        tv2= findViewById(R.id.test2);
        tv3= findViewById(R.id.test3);

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
                                tv1.setText("정현이꺼 " + a[0]);
                                tv2.setText("은윤이꺼 "+ a[1]);
                                tv3.setText("충헌이꺼 "+ a[2]);

                            }
                        });
                    }
                    catch (InterruptedException e)
                    {
                        // ooops
                    }
            }
        })).start();

        tv_sub = findViewById(R.id.tv_sub);
        dis_result = findViewById(R.id.dis_result);

        Intent intent = getIntent();
        String str = intent.getStringExtra("str");
        tv_sub.setText(str);

        dis_result.setText(str1+" "+str2);




    }

    private void initView() {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
//        setSupportActionBar(toolbar);
        mRecycle = (RecyclerView) findViewById(R.id.sub_recyeler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycle.setLayoutManager(layoutManager);
        mAdapter = new subBeaconListAdapter();
        mRecycle.setAdapter(mAdapter);
        mRecycle.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager
                .HORIZONTAL));
    }

    private void initManager() {
        mMinewBeaconManager = MinewBeaconManager.getInstance(this);
    }


    /*
     * check location
     * */
    private void checkLocation(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},101);
        }

    }
    /**
     * check Bluetooth state
     */
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
        //scan listener;
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

}

