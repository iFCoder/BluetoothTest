package com.bftv.seele.bluetoothtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.bftv.seele.bluetoothtest.activity.AndroidActivity;
import com.bftv.seele.bluetoothtest.activity.BlueClientActivity;
import com.bftv.seele.bluetoothtest.activity.BluetoothServerActivity;
import com.bftv.seele.bluetoothtest.activity.IOSActivity;

/**
 * @author LiFei
 * @time 2018/1/18
 * @description:
 */
public class MainActivity extends AppCompatActivity {
    private Button bAndroid;
    private Button bIos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mian);
        initView();

        bAndroid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AndroidActivity.class));
            }
        });

        bIos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, IOSActivity.class));
            }
        });

    }

    private void initView() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("MoTou");
        bAndroid = findViewById(R.id.btn_android);
        bIos = findViewById(R.id.btn_ios);
        Button client = findViewById(R.id.btn_client);
        Button server = findViewById(R.id.btn_server);
        client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent client = new Intent(MainActivity.this, BlueClientActivity.class);
                startActivity(client);
            }
        });
        server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent server = new Intent(MainActivity.this, BluetoothServerActivity.class);
                startActivity(server);
            }
        });
    }


}
