package com.audy.ecomplaint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.audy.ecomplaint.adapter.Riwayats;
import com.audy.ecomplaint.adapter.RiwayatsAdapter;
import com.audy.ecomplaint.api.Url;
import com.audy.ecomplaint.session.UserManagement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RiwayatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RiwayatsAdapter riwayatsAdapter;
    private List<Riwayats> riwayatsList;
    UserManagement userManagement;
    String getId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat);

        userManagement = new UserManagement(this);
        userManagement.checkLogin();

        HashMap<String,String> user = userManagement.userDetails();
        getId = user.get(userManagement.ID);

        recyclerView = findViewById(R.id.recyclerList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        riwayatsList = new ArrayList<>();

        loadAllRiwayats();
    }

    private void loadAllRiwayats() {
        JsonArrayRequest request = new JsonArrayRequest(Url.TAMPIL_KELUHAN_INFO_URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray array) {
                for(int i = 0; i < array.length(); i++){
                    try {
                        JSONObject object = array.getJSONObject(i);
                        String id = object.getString("id").trim();
                        String keluhan = object.getString("keluhan").trim();
                        String type = object.getString("type").trim();
                        String waktu = object.getString("waktu").trim();
                        String tanggal = object.getString("tanggal").trim();

                        Riwayats riwayats = new Riwayats();
                        riwayats.setId(id);
                        riwayats.setType(type);
                        riwayats.setKeluhan(keluhan);
                        riwayats.setWaktu(waktu);
                        riwayats.setTanggal(tanggal);
                        riwayatsList.add(riwayats);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                riwayatsAdapter = new RiwayatsAdapter(RiwayatActivity.this,riwayatsList);
                recyclerView.setAdapter(riwayatsAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RiwayatActivity.this,"Error Reading Detail " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(RiwayatActivity.this);
        queue.add(request);
    }
}