package com.audy.ecomplaint;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.audy.ecomplaint.api.Url;
import com.audy.ecomplaint.session.UserManagement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    TextView IDpelanggan, Nama, Nama2, Nohp, Nmeter, Daya, Alamat;
    Button update,lPass;
    ImageButton btn_photo_upload;
    ProgressDialog progressDialog;
    UserManagement userManagement;
    String mID;
    private Bitmap bitmap;
    CircleImageView profile_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        IDpelanggan = findViewById(R.id.tvIDPpelanggan);
        Nama = findViewById(R.id.tvNama);
        Nama2 = findViewById(R.id.tvNama2);
        Nohp = findViewById(R.id.tvNohp);
        Nmeter = findViewById(R.id.tvNometer);
        Daya = findViewById(R.id.tvDaya);
        Alamat = findViewById(R.id.tvAlamat);
        profile_image = findViewById(R.id.imageView2);

        userManagement = new UserManagement(this);
        userManagement.checkLogin();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);

        update = findViewById(R.id.btnEdit);
        lPass = findViewById(R.id.btnLPassword);
        profile_image = findViewById(R.id.imageView2);
        btn_photo_upload = findViewById(R.id.btn_photo);

        HashMap<String,String> user = userManagement.userDetails();
        mID = user.get(userManagement.ID);

        btn_photo_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseFile();
            }
        });

        lPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View resetpasswordlayout = LayoutInflater.from(ProfileActivity.this).inflate(R.layout.ubah_password,(ConstraintLayout)findViewById(R.id.layoutDialogContainer));
                EditText Oldpass = resetpasswordlayout.findViewById(R.id.eT_Oldpass);
                EditText Newpass = resetpasswordlayout.findViewById(R.id.eT_Newpass);
                EditText Conformpass = resetpasswordlayout.findViewById(R.id.eT_Confpass);

                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this, R.style.AlertDialogTheme);
                builder.setView(resetpasswordlayout);
                ((TextView) resetpasswordlayout.findViewById(R.id.textTitle)).setText("Ubah Password");
                ((Button) resetpasswordlayout.findViewById(R.id.buttoYes)).setText("Ubah Password");
                ((Button) resetpasswordlayout.findViewById(R.id.buttonNo)).setText("Tidak");
                ((ImageView) resetpasswordlayout.findViewById(R.id.imageIcon)).setImageResource(R.drawable.edit);

                final AlertDialog alertDialog = builder.create();

                resetpasswordlayout.findViewById(R.id.buttoYes).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String oldpassword = Oldpass.getText().toString().trim();
                        String newpassword = Newpass.getText().toString().trim();
                        String conformpassword = Conformpass.getText().toString().trim();

                        if(oldpassword.isEmpty() || newpassword.isEmpty() || conformpassword.isEmpty()){
                            message("Data Tidak Boleh Kosong!");
                        } else {
                            progressDialog.show();
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, Url.RESET_PASSWORD_URL,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            progressDialog.dismiss();
                                            message(response);
                                            alertDialog.dismiss();
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    progressDialog.dismiss();
                                    message(error.getMessage());
                                    alertDialog.dismiss();
                                }
                            }){
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String,String> params = new HashMap<>();
                                    params.put("oldpass",oldpassword);
                                    params.put("newpass", newpassword);
                                    params.put("confirmpass",conformpassword);
                                    params.put("id",mID);
                                    return params;
                                }
                            };
                            RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);
                            queue.add(stringRequest);
                        }
                        if(alertDialog.getWindow() != null){
                            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                        }
                        alertDialog.show();
                    }
                });
                resetpasswordlayout.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
                if(alertDialog.getWindow() != null){
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                alertDialog.show();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View updateDataLayout = LayoutInflater.from(ProfileActivity.this).inflate(R.layout.update_data,(ConstraintLayout)findViewById(R.id.layoutDialogContainer));

                final EditText ENama = updateDataLayout.findViewById(R.id.etNama);
                final EditText ENohp = updateDataLayout.findViewById(R.id.etNohp);
                final EditText ENmeter = updateDataLayout.findViewById(R.id.etNometer);
                final EditText EDaya = updateDataLayout.findViewById(R.id.etDaya);
                final EditText EAlamat = updateDataLayout.findViewById(R.id.etAlamat);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, Url.TAMPIL_DATA_PELANGGAN,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressDialog.dismiss();
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String success = jsonObject.getString("success");
                                    JSONArray jsonArray = jsonObject.getJSONArray("read");

                                    if(success.equals("1")){
                                        for(int i = 0; i < jsonArray.length(); i++){
                                            JSONObject object = jsonArray.getJSONObject(i);

                                            String mNama = object.getString("nama").trim();
                                            String mMeter = object.getString("no_meter").trim();
                                            String mNohp = object.getString("no_hp").trim();
                                            String mDaya = object.getString("daya").trim();
                                            String mAlamat = object.getString("alamat").trim();

                                            ENama.setText(mID);
                                            ENama.setText(mNama);
                                            ENmeter.setText(mMeter);
                                            ENohp.setText(mNohp);
                                            EDaya.setText(mDaya);
                                            EAlamat.setText(mAlamat);
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    progressDialog.dismiss();
                                    Toast.makeText(ProfileActivity.this,"Error Reading Detail " + e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileActivity.this,"Error Reading Detail " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("id", mID);
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(ProfileActivity.this);
                requestQueue.add(stringRequest);

                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this, R.style.AlertDialogTheme);
                builder.setView(updateDataLayout);
                ((TextView) updateDataLayout.findViewById(R.id.textTitle)).setText("Ubah Data");
                ((Button) updateDataLayout.findViewById(R.id.buttoYes)).setText("Ubah");
                ((Button) updateDataLayout.findViewById(R.id.buttonNo)).setText("Tidak");
                ((ImageView) updateDataLayout.findViewById(R.id.imageIcon)).setImageResource(R.drawable.edit);

                final AlertDialog alertDialog = builder.create();

                updateDataLayout.findViewById(R.id.buttoYes).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String nama = ENama.getText().toString().trim();
                        final String no_meter = ENmeter.getText().toString().trim();
                        final String no_hp = ENohp.getText().toString().trim();
                        final String daya = EDaya.getText().toString().trim();
                        final String alamat = EAlamat.getText().toString().trim();
                        if(nama.isEmpty() || no_meter.isEmpty() || no_hp.isEmpty() || daya.isEmpty() || alamat.isEmpty()){
                            message("Data Tidak Boleh Kosong!");
                        } else {
                            progressDialog.show();
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, Url.UPDATE_USER_INFO_URL,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            progressDialog.dismiss();
                                            message(response);
                                            alertDialog.dismiss();
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    progressDialog.dismiss();
                                    message(error.getMessage());
                                    alertDialog.dismiss();
                                }
                            }){
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String,String> params = new HashMap<>();
                                    params.put("nama",nama);
                                    params.put("no_meter", no_meter);
                                    params.put("daya",daya);
                                    params.put("no_hp",no_hp);
                                    params.put("alamat",alamat);
                                    params.put("id", mID);

                                    return params;
                                }
                            };
                            RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);
                            queue.add(stringRequest);
                        }

                        if(alertDialog.getWindow() != null){
                            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                        }
                        alertDialog.show();
                    }
                });

                updateDataLayout.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
                if(alertDialog.getWindow() != null){
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                alertDialog.show();
            }
        });
    }

    private void getUserDetail(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Url.TAMPIL_DATA_PELANGGAN,
                new Response.Listener<String>() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("read");

                            if(success.equals("1")){
                                for(int i = 0; i < jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String mNama = object.getString("nama").trim();
                                    String mMeter = object.getString("no_meter").trim();
                                    String mNohp = object.getString("no_hp").trim();
                                    String mDaya = object.getString("daya").trim();
                                    String mAlamat = object.getString("alamat").trim();
                                    String mPhoto = object.getString("photo").trim();

                                    IDpelanggan.setText(mID);
                                    Nama.setText(mNama);
                                    Nama2.setText(mNama);
                                    Nmeter.setText(mMeter);
                                    Nohp.setText(mNohp);
                                    Daya.setText(mDaya);
                                    Alamat.setText(mAlamat);

                                    if(mPhoto != null){
                                        Glide.with(getApplicationContext()).load(mPhoto).into(profile_image);
                                    } else {
                                        profile_image.setImageDrawable(getResources().getDrawable(R.drawable.profile, getApplicationContext().getTheme()));
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(ProfileActivity.this,"Error Reading Detail " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this,"Error Reading Detail " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", mID);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserDetail();
    }

    private void message(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    private void chooseFile(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profile_image.setImageBitmap(bitmap);
            } catch (IOException e){
                e.printStackTrace();
            }
            UploadPicture(mID, getStringImage(bitmap));
        }
    }

    private void UploadPicture(final String id, final String photo) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Url.UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if(success.equals("1")){
                                Toast.makeText(ProfileActivity.this,"Berhasil Upload Image", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(ProfileActivity.this,"Try Again " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this,"Try Again" + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", mID);
                params.put("photo", photo);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public String getStringImage(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] imageByteArray = byteArrayOutputStream.toByteArray();
        String encodeImage = Base64.encodeToString(imageByteArray, Base64.DEFAULT);

        return encodeImage;
    }

}