package com.audy.ecomplaint.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.audy.ecomplaint.R;
import com.audy.ecomplaint.api.Url;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RiwayatsAdapter extends RecyclerView.Adapter<RiwayatsAdapter.RiwayatsHolder>{
    Context context;
    List<Riwayats> riwayatsList;

    public RiwayatsAdapter(Context context, List<Riwayats> riwayatsList) {
        this.context = context;
        this.riwayatsList = riwayatsList;
    }

    @NonNull
    @Override
    public RiwayatsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View riwayatLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view,parent,false);
        return new RiwayatsHolder(riwayatLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull RiwayatsHolder holder, int position) {
        final Riwayats riwayats = riwayatsList.get(position);
        holder.ID.setText(riwayats.getId());
        holder.Type.setText(riwayats.getType());
        holder.Keluhan.setText(riwayats.getKeluhan());
        holder.Waktu.setText(riwayats.getWaktu());
        holder.Tanggal.setText(riwayats.getTanggal());
        holder.Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View hapusLayout = LayoutInflater.from(context).inflate(R.layout.hapus_data,(ConstraintLayout) view.findViewById(R.id.layoutDialogContainer));

                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
                builder.setView(hapusLayout);
                ((TextView) hapusLayout.findViewById(R.id.textTitle)).setText("Hapus Data Riwayat");
                ((TextView) hapusLayout.findViewById(R.id.textMessage)).setText("Anda Ingin Menghapus Riwayat Keluhan! \n" + riwayats.getKeluhan());
                ((Button) hapusLayout.findViewById(R.id.buttoYes)).setText("Hapus");
                ((Button) hapusLayout.findViewById(R.id.buttonNo)).setText("Tidak");
                ((ImageView) hapusLayout.findViewById(R.id.imageIcon)).setImageResource(R.drawable.warning);

                final AlertDialog alertDialog = builder.create();

                hapusLayout.findViewById(R.id.buttoYes).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, Url.HAPUS_KELUHAN_INFO_URL,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject object = new JSONObject(response);
                                            String check = object.getString("state");
                                            if(check.equals("delete")){
                                                Delete(position);
                                                Toast.makeText(context, "Berhasil Dihapus!", Toast.LENGTH_SHORT).show();
                                                alertDialog.dismiss();
                                            } else {
                                                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        }){
                            @Nullable
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                HashMap<String,String> deleteParams = new HashMap<>();
                                deleteParams.put("id", riwayats.getId());
                                return deleteParams;
                            }
                        };
                        RequestQueue requestQueue = Volley.newRequestQueue(context);
                        requestQueue.add(stringRequest);
                    }
                });

                hapusLayout.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
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

    @Override
    public int getItemCount() {
        return riwayatsList.size();
    }

    public class RiwayatsHolder extends RecyclerView.ViewHolder{
        TextView ID,Type,Keluhan,Waktu,Tanggal;
        ImageButton Delete;
        public RiwayatsHolder(@NonNull View itemView) {
            super(itemView);
            ID = itemView.findViewById(R.id.rcy_id);
            Type = itemView.findViewById(R.id.rcy_type);
            Keluhan = itemView.findViewById(R.id.rcy_keluhan);
            Waktu = itemView.findViewById(R.id.rcy_waktu);
            Tanggal = itemView.findViewById(R.id.rcy_tanggal);
            Delete = itemView.findViewById(R.id.rcy_delete);
        }
    }

    public void Delete(int item) {
        riwayatsList.remove(item);
        notifyItemRemoved(item);
    }
}
