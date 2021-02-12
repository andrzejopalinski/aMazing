package com.example.amazing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onBtnAcc(View view) {

        Intent intent = new Intent(this, Acc.class);
        this.startActivity(intent);
    }

    public void onBtnTouch(View view) {

        Intent intent = new Intent(this, Touch.class);
        this.startActivity(intent);
    }
}