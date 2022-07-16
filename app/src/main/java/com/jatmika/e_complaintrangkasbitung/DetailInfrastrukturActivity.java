package com.jatmika.e_complaintrangkasbitung;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jatmika.e_complaintrangkasbitung.Adapter.RecyclerAdapterKomentar;
import com.jatmika.e_complaintrangkasbitung.Adapter.RecyclerAdapterProses;
import com.jatmika.e_complaintrangkasbitung.Model.DataUser;
import com.jatmika.e_complaintrangkasbitung.Model.Komentar;
import com.jatmika.e_complaintrangkasbitung.Model.MySingleton;
import com.jatmika.e_complaintrangkasbitung.Model.Proses;
import com.jatmika.e_complaintrangkasbitung.Model.Suka;
import com.uncopt.android.widget.text.justify.JustifiedTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.text.TextUtils.isEmpty;

public class DetailInfrastrukturActivity extends AppCompatActivity {

    final private String serverKey = "key=" + "AIzaSyDL3TGOXPGwfM7iIf_pX4zqD3IbTr-I45w";
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";

    String FCM_API = "https://fcm.googleapis.com/fcm/send";
    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;

    TextView nomorDetailTextView, namaDetailTextView, emailDetailTextView, alamatDetailTextView,
            tanggalDetailTextView, tvBalas, tvStatusSuka, tvJudul, statusDetailtextView;
    JustifiedTextView isiDetailTextView;
    ImageView fotoDetailImageView, imageSuka;
    Button btnTambahKomentar;
    LinearLayout linear1, linear2, layout_kerangka;
    RelativeLayout relative1, btnBack;
    FrameLayout contentFrame;
    FloatingActionButton fab;
    Animation fromright;

    RecyclerView mRecyclerView;
    private RecyclerAdapterKomentar mAdapter;
    private StorageReference mStorageRef;
    FirebaseStorage mStorage;
    DatabaseReference mDatabaseRef, databaseReference;
    private List<Komentar> mKomentar;
    ValueEventListener mDBListener;

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    int satuan;

    private void initializeWidgets(){
        nomorDetailTextView = findViewById(R.id.nomorTextView);
        namaDetailTextView = findViewById(R.id.namaTextView);
        emailDetailTextView = findViewById(R.id.emailTextView);
        alamatDetailTextView = findViewById(R.id.alamatTextView);
        tanggalDetailTextView = findViewById(R.id.tanggalTextView);
        isiDetailTextView = findViewById(R.id.isiTextView);
        statusDetailtextView = findViewById(R.id.statusTextView);
        fotoDetailImageView = findViewById(R.id.fotoDetailImageView);
        imageSuka = findViewById(R.id.imageSuka);
        tvStatusSuka = findViewById(R.id.tvStatusSuka);
        linear1 = findViewById(R.id.linear1);
        contentFrame = findViewById(R.id.content_frame);
        relative1 = findViewById(R.id.relative1);
        btnTambahKomentar = findViewById(R.id.btnTambahKomentar);
        linear2 = findViewById(R.id.linear2);
        layout_kerangka = findViewById(R.id.layout_kerangka);
        tvBalas = findViewById(R.id.tvBalas);
        fab = findViewById(R.id.fab);
        tvJudul = findViewById(R.id.tvJudul);
        btnBack = findViewById(R.id.btnBack);
    }

    String image, berkas, nomor, nik, email, nama, alamat, tanggal, namalokasi, latitude, longitude, isi, kategori, status,
            jml_lihat, jml_suka, jml_balas, getKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_detail_infrastruktur);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        Intent i = this.getIntent();
        image = i.getExtras().getString("IMAGE_KEY");
        berkas = i.getExtras().getString("BERKAS_KEY");
        nomor = i.getExtras().getString("NOMOR_KEY");
        nik = i.getExtras().getString("NIK_KEY");
        email = i.getExtras().getString("EMAIL_KEY");
        nama = i.getExtras().getString("NAMA_KEY");
        alamat = i.getExtras().getString("ALAMAT_KEY");
        tanggal = i.getExtras().getString("TANGGAL_KEY");
        namalokasi = i.getExtras().getString("NAMALOKASI_KEY");
        latitude = i.getExtras().getString("LATITUDE_KEY");
        longitude = i.getExtras().getString("LONGITUDE_KEY");
        isi = i.getExtras().getString("ISI_KEY");
        kategori = i.getExtras().getString("KATEGORI_KEY");
        status = i.getExtras().getString("STATUS_KEY");
        jml_lihat = i.getExtras().getString("JML_LIHAT_KEY");
        jml_suka = i.getExtras().getString("JML_SUKA_KEY");
        jml_balas = i.getExtras().getString("JML_BALAS_KEY");
        getKey = i.getExtras().getString("GETPRIMARY_KEY");

        initializeWidgets();
        displayKomentar();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fromright = AnimationUtils.loadAnimation(this, R.anim.fromright);
        tvJudul.startAnimation(fromright);
        linear2.setVisibility(View.GONE);

        nomorDetailTextView.setText(nomor);
        emailDetailTextView.setText(email);
        namaDetailTextView.setText(nama);
        alamatDetailTextView.setText(alamat);
        tanggalDetailTextView.setText(tanggal);
        isiDetailTextView.setText("''"+isi+"''");
        statusDetailtextView.setText(status);
        Glide.with(this)
                .load(image)
                .into(fotoDetailImageView);

        fotoDetailImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(DetailInfrastrukturActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.show_image, null);
                ImageView imageView = mView.findViewById(R.id.imageView);
                TextView btnClose = mView.findViewById(R.id.btnClose);

                Glide.with(DetailInfrastrukturActivity.this)
                        .load(image)
                        .into(imageView);

                mBuilder.setView(mView);
                final AlertDialog mDialog = mBuilder.create();
                mDialog.show();

                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });
            }
        });

        FirebaseDatabase.getInstance().getReference("data_komplain").child(getKey).child("suka").orderByChild("email")
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    imageSuka.setVisibility(View.GONE);
                    tvStatusSuka.setText("Terima kasih telah menyukai ini");
                } else {
                    imageSuka.setVisibility(View.VISIBLE);
                    tvStatusSuka.setText("<~ Jadilah orang yang menyukai ini");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference("data_komplain").child(getKey).child("suka").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                satuan = 1;
                int jml_lihatlama = Integer.parseInt(jml_lihat);
                final int total = satuan + jml_lihatlama;

                if (dataSnapshot.exists()) {
                    final long totalSuka;
                    totalSuka = (dataSnapshot.getChildrenCount());

                    FirebaseDatabase.getInstance().getReference("data_komplain").child(getKey).child("jml_lihat").setValue(String.valueOf(total))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    FirebaseDatabase.getInstance().getReference("data_komplain").child(getKey).child("jml_suka").setValue(String.valueOf(totalSuka));
                                }
                            });
                } else {
                    FirebaseDatabase.getInstance().getReference("data_komplain").child(getKey).child("jml_lihat").setValue(String.valueOf(total))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    FirebaseDatabase.getInstance().getReference("data_komplain").child(getKey).child("jml_suka").setValue("0")
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    imageSuka.setVisibility(View.VISIBLE);
                                                    tvStatusSuka.setText("<~ Jadilah orang yang menyukai ini");
                                                }
                                            });
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        imageSuka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("data_komplain").child(getKey).child("suka")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(new Suka(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                        nama))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FirebaseDatabase.getInstance().getReference("data_komplain").child(getKey).child("suka").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            long totalSuka;
                                            totalSuka = (dataSnapshot.getChildrenCount());
                                            FirebaseDatabase.getInstance().getReference("data_komplain").child(getKey).child("jml_suka").setValue(String.valueOf(totalSuka))
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            linear1.setVisibility(View.VISIBLE);
                                                            imageSuka.setVisibility(View.GONE);
                                                            tvStatusSuka.setText("Terima kasih telah menyukai ini");
                                                        }
                                                    });
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        });
            }
        });

        if (mAuth.getCurrentUser().getEmail().equals(emailDetailTextView.getText().toString())) {
            btnTambahKomentar.setVisibility(View.VISIBLE);
        } else {
            btnTambahKomentar.setVisibility(View.GONE);
        }

        relative1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status.equals("Menunggu Diproses")){
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(DetailInfrastrukturActivity.this);
                    View mView = getLayoutInflater().inflate(R.layout.show_menunggu, null);
                    TextView btnClose = mView.findViewById(R.id.btnClose);
                    TextView tvNomor = mView.findViewById(R.id.tvNomor);

                    mBuilder.setView(mView);
                    final AlertDialog mDialog = mBuilder.create();
                    mDialog.show();

                    tvNomor.setText("No : "+nomor);
                    btnClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                        }
                    });

                } else {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(DetailInfrastrukturActivity.this);
                    View mView = getLayoutInflater().inflate(R.layout.show_proses, null);

                    TextView btnClose = mView.findViewById(R.id.btnClose);
                    TextView tvNomor = mView.findViewById(R.id.tvNomor);
                    TextView tvStatus = mView.findViewById(R.id.tvStatus);
                    RecyclerView mRecyclerView = mView.findViewById(R.id.mRecyclerView);
                    final RecyclerAdapterProses mAdapter;
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("data_komplain").child(getKey).child("status_komplain");
                    final List<Proses> mProsess;

                    mBuilder.setView(mView);
                    final AlertDialog mDialog = mBuilder.create();
                    mDialog.show();

                    mRecyclerView.setHasFixedSize(true);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(DetailInfrastrukturActivity.this));

                    mProsess = new ArrayList<>();
                    mAdapter = new RecyclerAdapterProses(DetailInfrastrukturActivity.this, mProsess);
                    mRecyclerView.setAdapter(mAdapter);

                    mDBListener = databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            mProsess.clear();
                            for (DataSnapshot prosesSnapshot : dataSnapshot.getChildren()) {
                                Proses upload = prosesSnapshot.getValue(Proses.class);
                                upload.setKey(prosesSnapshot.getKey());
                                mProsess.add(upload);
                            }
                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(DetailInfrastrukturActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    tvStatus.setText(status);
                    tvNomor.setText("No : "+nomor);
                    btnClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                        }
                    });
                }
            }
        });

        btnTambahKomentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ScrollView scrollView = findViewById(R.id.scrollView);
                btnTambahKomentar.setVisibility(View.GONE);
                scrollView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        scrollView.isSmoothScrollingEnabled(); }
                }, 200);
                linear2.setVisibility(View.VISIBLE);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText input = findViewById(R.id.input);
                final ScrollView scrollView = findViewById(R.id.scrollView);

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(DetailInfrastrukturActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.show_loading, null);

                mBuilder.setView(mView);
                mBuilder.setCancelable(false);
                final AlertDialog mDialog = mBuilder.create();
                mDialog.show();

                if (isEmpty(input.getText().toString())) {
                    Toast.makeText(DetailInfrastrukturActivity.this, "Balasan tidak boleh kosong!",
                            Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();

                } else {
                    FirebaseDatabase.getInstance().getReference("data_komplain").child(getKey)
                            .child("balasan").push().setValue(new Komentar(input.getText().toString(),
                            namaDetailTextView.getText().toString(), firebaseUser.getEmail())).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) { FirebaseDatabase.getInstance().getReference("data_komplain").child(getKey).child("balasan").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            long totalBalas;
                                            totalBalas = (dataSnapshot.getChildrenCount());
                                            FirebaseDatabase.getInstance().getReference("data_komplain").child(getKey).child("jml_balas").setValue(String.valueOf(totalBalas))
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            TOPIC = "/topics/komplain";
                                                            NOTIFICATION_TITLE = "Balasan Komplain Diterima";
                                                            NOTIFICATION_MESSAGE = "Dari : " +namaDetailTextView.getText().toString();

                                                            JSONObject notification = new JSONObject();
                                                            JSONObject notifcationBody = new JSONObject();
                                                            try {
                                                                notifcationBody.put("title", NOTIFICATION_TITLE);
                                                                notifcationBody.put("message", NOTIFICATION_MESSAGE);

                                                                notification.put("to", TOPIC);
                                                                notification.put("data", notifcationBody);
                                                            } catch (JSONException e) {
                                                                Log.e(TAG, "onCreate: " + e.getMessage() );
                                                            }
                                                            sendNotification(notification);

                                                            linear2.setVisibility(View.GONE);
                                                            input.setText("");
                                                            btnTambahKomentar.setVisibility(View.VISIBLE);
                                                            mDialog.dismiss();

                                                            scrollView.postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                                                                    scrollView.isSmoothScrollingEnabled();
                                                                }
                                                                }, 200);
                                                        }
                                                    });
                                        } else {
                                            FirebaseDatabase.getInstance().getReference("data_komplain").child(getKey).child("jml_balas").setValue("0")
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            TOPIC = "/topics/komplain";
                                                            NOTIFICATION_TITLE = "Balasan Komplain Diterima";
                                                            NOTIFICATION_MESSAGE = "Dari : " +namaDetailTextView.getText().toString();

                                                            JSONObject notification = new JSONObject();
                                                            JSONObject notifcationBody = new JSONObject();
                                                            try {
                                                                notifcationBody.put("title", NOTIFICATION_TITLE);
                                                                notifcationBody.put("message", NOTIFICATION_MESSAGE);

                                                                notification.put("to", TOPIC);
                                                                notification.put("data", notifcationBody);
                                                            } catch (JSONException e) {
                                                                Log.e(TAG, "onCreate: " + e.getMessage() );
                                                            }
                                                            sendNotification(notification);

                                                            linear2.setVisibility(View.GONE);
                                                            input.setText("");
                                                            btnTambahKomentar.setVisibility(View.VISIBLE);
                                                            mDialog.dismiss();

                                                            scrollView.postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                                                                    scrollView.isSmoothScrollingEnabled();
                                                                }
                                                                }, 200);
                                                        }
                                                    });
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                }
                            });
                }
            }
        });

        Bundle bundle = new Bundle();
        bundle.putString("namalokasi", String.valueOf(namalokasi));
        bundle.putString("latitude", String.valueOf(latitude));
        bundle.putString("longitude", String.valueOf(longitude));
        bundle.putString("getKey", String.valueOf(getKey));
        FragmentMap fragmentMap = new FragmentMap();
        fragmentMap.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.content_frame, fragmentMap).commit();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DetailInfrastrukturActivity.this, "Request error!", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void displayKomentar() {
        mRecyclerView = findViewById(R.id.list_of_komentar);
        RecyclerView.LayoutManager layoutManager = new FlexboxLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mKomentar = new ArrayList<>();
        mAdapter = new RecyclerAdapterKomentar (DetailInfrastrukturActivity.this, mKomentar);
        mRecyclerView.setAdapter(mAdapter);

        mStorage = FirebaseStorage.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("foto_komentar");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("data_komplain").child(getKey).child("balasan");
        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mKomentar.clear();
                    for (DataSnapshot komentarSnapshot : dataSnapshot.getChildren()) {
                        Komentar upload = komentarSnapshot.getValue(Komentar.class);
                        upload.setKey(komentarSnapshot.getKey());
                        mKomentar.add(upload);
                    }
                    mAdapter.notifyDataSetChanged();
                    tvBalas.setText("Balasan Komplain");
                    layout_kerangka.setVisibility(View.VISIBLE);

                } else if (mAuth.getCurrentUser().getEmail().equals(emailDetailTextView.getText().toString())
                        || dataSnapshot.exists()){
                    tvBalas.setText("Balasan Komplain");
                    layout_kerangka.setVisibility(View.VISIBLE);

                } else{
                    tvBalas.setText("Belum Ada Balasan");
                    layout_kerangka.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DetailInfrastrukturActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
