package com.audy.ecomplaint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class KeluhanTerkirim extends AppCompatActivity {
    Button btnTerkirim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keluhan_terkirim);

        btnTerkirim = findViewById(R.id.btnKembali);
         btnTerkirim.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 startActivity(new Intent(KeluhanTerkirim.this,DashboardActivity.class));
             }
         });
    }
}