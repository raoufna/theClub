package com.unimib.wardrobe.ui.home;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.unimib.wardrobe.R;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main); //collegamento tra xml e java
    }
}