package com.audy.ecomplaint;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    EditText no_Hp,Password;
    Button btnLogin;
    ProgressDialog progressDialog;
    UserManagement userManagement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        no_Hp  = findViewById(R.id.no_hp);
        Password  = findViewById(R.id.password);
        btnLogin  = findViewById(R.id.btnLogin);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);

        userManagement = new UserManagement(this);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserLoginProcces();
            }
        });
    }

    private void UserLoginProcces() {
        String noHp = no_Hp.getText().toString().trim();
        String password = Password.getText().toString().trim();
        if(noHp.isEmpty() || password.isEmpty()){
            message("Data Tidak Boleh Kosong!");
        } else {
            progressDialog.show();
            StringRequest request = new StringRequest(Request.Method.POST, Url.LOGIN_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String result = jsonObject.getString("status");

                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                if(result.equals("success")){
                                    progressDialog.dismiss();
                                    for(int i = 0; i < jsonArray.length(); i++){
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        String id_pelanggan = object.getString("id_pelanggan");
                                        String nama = object.getString("nama");
                                        String no_hp = object.getString("no_hp");
                                        String no_meter = object.getString("no_meter");
                                        String daya = object.getString("daya");
                                        String alamat = object.getString("alamat");

                                        userManagement.UserSessionManage(id_pelanggan,nama,no_hp,no_meter,daya,alamat);

                                        Intent intent = new Intent(LoginActivity.this,DashboardActivity.class);
                                        startActivity(intent);finish();
                                        message("User Berhasil Login");
                                    }
                                } else {
                                    progressDialog.dismiss();
                                    message("User Gagal Login");
                                }
                            } catch (JSONException e){
                                e.printStackTrace();
                            }
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
                    Map<String,String> params = new HashMap<>();
                    params.put("no_hp",noHp);
                    params.put("password", password);
                    return params;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
            queue.add(request);
        }
    }

    private void message(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}