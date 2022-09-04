package com.jatmika.e_complaintrangkasbitung;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Context;
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
import com.jatmika.e_complaintrangkasbitung.API.API;
import com.jatmika.e_complaintrangkasbitung.API.APIUtility;
import com.jatmika.e_complaintrangkasbitung.Adapter.RecyclerAdapterKomentar;
import com.jatmika.e_complaintrangkasbitung.Adapter.RecyclerAdapterProses;
import com.jatmika.e_complaintrangkasbitung.Model.Komentar;
import com.jatmika.e_complaintrangkasbitung.Model.MySingleton;
import com.jatmika.e_complaintrangkasbitung.Model.Proses;
import com.jatmika.e_complaintrangkasbitung.Model.Suka;
import com.jatmika.e_complaintrangkasbitung.SharePref.SharePref;
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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class DetailKomplainSayaActivity extends AppCompatActivity {

    final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AIzaSyDL3TGOXPGwfM7iIf_pX4zqD3IbTr-I45w";
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;

    TextView nomorDetailTextView, namaDetailTextView, alamatDetailTextView, tanggalDetailTextView, tvBalas,
            tvStatusSuka, tvJudul, lokasi, statusDetailTextView, tvFoto;
    JustifiedTextView isiDetailTextView;
    ImageView fotoDetailImageView, imageSuka;
    Button btnTambahKomentar, btnDokumen;
    LinearLayout linear1, linear2, layout_kerangka, linearDokumen;
    RelativeLayout relative1, relativeLayout, btnBack;
    FrameLayout contentFrame;
    View pembatas;
    FloatingActionButton fab;
    Animation fromright;

    RecyclerView mRecyclerView;
    private RecyclerAdapterKomentar mAdapter;
    DatabaseReference mDatabaseRef;
    private List<Komentar> mKomentar;
    private ValueEventListener mDBListener;

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    API apiService;
    SharePref sharePref;

    int satuan;

    private void initializeWidgets(){
        nomorDetailTextView = findViewById(R.id.nomorTextView);
        namaDetailTextView = findViewById(R.id.namaTextView);
        alamatDetailTextView = findViewById(R.id.alamatTextView);
        tanggalDetailTextView = findViewById(R.id.tanggalTextView);
        isiDetailTextView = findViewById(R.id.isiTextView);
        statusDetailTextView = findViewById(R.id.statusTextView);
        fotoDetailImageView = findViewById(R.id.fotoDetailImageView);
        imageSuka = findViewById(R.id.imageSuka);
        tvStatusSuka = findViewById(R.id.tvStatusSuka);
        btnDokumen = findViewById(R.id.btnDokumen);
        linearDokumen = findViewById(R.id.linearLayout);
        tvFoto = findViewById(R.id.tvFoto);
        relativeLayout = findViewById(R.id.relativeLayout);
        linear1 = findViewById(R.id.linear1);
        pembatas = findViewById(R.id.pembatas);
        lokasi = findViewById(R.id.lokasi);
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
        setContentView(R.layout.app_bar_detail_komplain_saya);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        apiService = APIUtility.getAPI();
        sharePref = new SharePref(this);

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

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        fromright = AnimationUtils.loadAnimation(this, R.anim.fromright);
        tvJudul.startAnimation(fromright);
        linear2.setVisibility(View.GONE);

//        if(namalokasi.equals("Unknown") && latitude.equals("0.0") && longitude.equals("0.0")){
//            pembatas.setVisibility(View.GONE);
//            lokasi.setVisibility(View.GONE);
//            contentFrame.setVisibility(View.GONE);
//        }

        if (image.equals("")){
            relativeLayout.setVisibility(View.GONE);
            tvFoto.setVisibility(View.GONE);
        } else if (berkas.equals("")){
            linearDokumen.setVisibility(View.GONE);
        } else if (image.equals("") && berkas.equals("")){
            relativeLayout.setVisibility(View.GONE);
            tvFoto.setVisibility(View.GONE);
            linearDokumen.setVisibility(View.GONE);
        }

        nomorDetailTextView.setText(nomor);
        namaDetailTextView.setText(nama);
        alamatDetailTextView.setText(alamat);
        tanggalDetailTextView.setText(tanggal);
        isiDetailTextView.setText(isi);
        statusDetailTextView.setText(status);
        String urlImage = "https://api-rohmat.kosanbahari.xyz/uploads/"+image;
        Glide.with(this)
                .load(urlImage)
                .into(fotoDetailImageView);

        fotoDetailImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(DetailKomplainSayaActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.show_image, null);
                ImageView imageView = mView.findViewById(R.id.imageView);
                TextView btnClose = mView.findViewById(R.id.btnClose);

                Glide.with(DetailKomplainSayaActivity.this)
                        .load(urlImage)
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

        apiService.checkStatusLike("Bearer "+sharePref.getTokenApi(), getKey).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if(response.code() == 200) {
                    imageSuka.setVisibility(View.GONE);
                    tvStatusSuka.setText("Terima kasih telah menyukai ini");
                } else {
                    imageSuka.setVisibility(View.VISIBLE);
                    tvStatusSuka.setText("<~ Jadilah orang yang menyukai ini");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

        imageSuka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiService.addLike("Bearer "+sharePref.getTokenApi(), getKey).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        if (response.code() == 200){
                            linear1.setVisibility(View.VISIBLE);
                            imageSuka.setVisibility(View.GONE);
                            tvStatusSuka.setText("Terima kasih telah menyukai ini");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            }
        });

        relative1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status.equals("Menunggu Diproses")){
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(DetailKomplainSayaActivity.this);
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
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(DetailKomplainSayaActivity.this);
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
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(DetailKomplainSayaActivity.this));

                    mProsess = new ArrayList<>();
                    mAdapter = new RecyclerAdapterProses(DetailKomplainSayaActivity.this, mProsess);
                    mRecyclerView.setAdapter(mAdapter);

                    mDBListener = databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            mProsess.clear();
                            for (DataSnapshot kandidatSnapshot : dataSnapshot.getChildren()) {
                                Proses upload = kandidatSnapshot.getValue(Proses.class);
                                upload.setKey(kandidatSnapshot.getKey());
                                mProsess.add(upload);
                            }
                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(DetailKomplainSayaActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(DetailKomplainSayaActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.show_loading, null);

                mBuilder.setView(mView);
                mBuilder.setCancelable(false);
                final AlertDialog mDialog = mBuilder.create();
                mDialog.show();

                if (isEmpty(input.getText().toString())) {
                    Toast.makeText(DetailKomplainSayaActivity.this, "Balasan tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();

                } else {
//                    FirebaseDatabase.getInstance().getReference("data_komplain").child(getKey)
                    apiService.addComentar("Bearer "+sharePref.getTokenApi(), getKey, input.getText().toString()).enqueue(new Callback<Komentar>() {
                        @Override
                        public void onResponse(Call<Komentar> call, retrofit2.Response<Komentar> response) {
                            Log.i("responseAPI", response.body().toString());
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
                            displayKomentar();
                        }

                        @Override
                        public void onFailure(Call<Komentar> call, Throwable t) {
                            Log.i("responseAPI", t.toString());
                        }
                    });
                }
            }
        });

//        Bundle bundle = new Bundle();
//        bundle.putString("namalokasi", String.valueOf(namalokasi));
//        bundle.putString("latitude", String.valueOf(latitude));
//        bundle.putString("longitude", String.valueOf(longitude));
//        bundle.putString("getKey", String.valueOf(getKey));
//        FragmentMap fragmentMap = new FragmentMap();
//        fragmentMap.setArguments(bundle);
//        getFragmentManager().beginTransaction().replace(R.id.content_frame, fragmentMap).commit();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnDokumen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String berkasUrl = "https://api-rohmat.kosanbahari.xyz/uploads/"+berkas;
                if (kategori.equals("iumk")){
                    downloadFile(DetailKomplainSayaActivity.this, "dokumen-iumk"+nama, ".docx", DIRECTORY_DOWNLOADS, berkasUrl);
                } else if (kategori.equals("SPPT")){
                    downloadFile(DetailKomplainSayaActivity.this, "dokumen-sppt"+nama, ".docx", DIRECTORY_DOWNLOADS, berkasUrl);
                } else if (kategori.equals("surat nikah")){
                    downloadFile(DetailKomplainSayaActivity.this, "dokumen-nikah"+nama, ".docx", DIRECTORY_DOWNLOADS, berkasUrl);
                }
            }
        });
    }

    public void downloadFile(Context context, String fileName, String fileExtension, String downloadDirectory, String url){
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalFilesDir(context, downloadDirectory, fileName + fileExtension);
        downloadManager.enqueue(request);
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
                        Toast.makeText(DetailKomplainSayaActivity.this, "Request error!", Toast.LENGTH_LONG).show();
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
        mAdapter = new RecyclerAdapterKomentar (DetailKomplainSayaActivity.this, mKomentar);
        mRecyclerView.setAdapter(mAdapter);

        apiService.getComentar("Bearer "+sharePref.getTokenApi(), getKey).enqueue(new Callback<List<Komentar>>() {
            @Override
            public void onResponse(Call<List<Komentar>> call, retrofit2.Response<List<Komentar>> response) {
                Log.i("response", "code"+getKey);
                if(response.code() == 200){
                    for (Komentar komentar : response.body()){
                        if(komentar != null)
                            mKomentar.add(komentar);
                    }
                    mAdapter.notifyDataSetChanged();
                    tvBalas.setText("Balasan Komplain");
                    layout_kerangka.setVisibility(View.VISIBLE);
                }else{
                    tvBalas.setText("Belum Ada Balasan");
                    layout_kerangka.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(Call<List<Komentar>> call, Throwable t) {
                Log.i("errorResponse", t.toString());
            }
        });
    }
}
