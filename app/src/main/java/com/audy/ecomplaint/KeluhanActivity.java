package com.audy.ecomplaint;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.audy.ecomplaint.api.Url;
import com.audy.ecomplaint.session.UserManagement;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

public class KeluhanActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    Button tambahKeluhan;
    TextView Nama;
    TextInputEditText Keluhan;
    UserManagement userManagement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keluhan);

        Nama = findViewById(R.id.tvNama);
        Keluhan = findViewById(R.id.eTkeluhan);
        tambahKeluhan = findViewById(R.id.btnTambah);

        userManagement = new UserManagement(this);
        userManagement.checkLogin();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);

        HashMap<String,String> user = userManagement.userDetails();
        final String mIDpelanggan = user.get(userManagement.ID);
        String mNama = user.get(userManagement.NAME);

        Nama.setText(mNama);

        tambahKeluhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keluhan = Keluhan.getText().toString().trim();

                if(keluhan.isEmpty()){
                    message("Data Tidak Boleh Kosong!");
                } else {
                    progressDialog.setTitle("Menambahkan...");
                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Url.TAMBAH_KELUHAN_INFO_URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(KeluhanActivity.this,KeluhanTerkirim.class);
                                    startActivity(intent);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            message(error.getMessage());
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> insertParams = new HashMap<>();
                            insertParams.put("keluhan",keluhan);
                            insertParams.put("id_pelanggan", mIDpelanggan);
                            return insertParams;
                        }
                    };
                    RequestQueue queue = Volley.newRequestQueue(KeluhanActivity.this);
                    queue.add(stringRequest);
                }
            }
        });
    }

    private void message(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}