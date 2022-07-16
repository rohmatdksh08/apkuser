package com.jatmika.e_complaintrangkasbitung;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.jatmika.e_complaintrangkasbitung.Model.DataUser;
import com.jatmika.e_complaintrangkasbitung.Model.Komplain;
import com.jatmika.e_complaintrangkasbitung.Model.MySingleton;
import com.jatmika.e_complaintrangkasbitung.Model.PersentaseKomplain;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.text.TextUtils.isEmpty;

public class TambahKomplainNikahActivity extends AppCompatActivity {

    final private String serverKey = "key=" + "AIzaSyDL3TGOXPGwfM7iIf_pX4zqD3IbTr-I45w";
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";

    String FCM_API = "https://fcm.googleapis.com/fcm/send";
    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;

    private static final int PICK_FILE = 003;
    Button btnBerkas, sendBtn;
    EditText edNomor, edNik, edNama, edAlamat, edTanggal, edIsi;
    ImageView imageView;
    RelativeLayout btnBack;
    TextView tvJudul, tvNamaFile;

    Animation fromright;
    Calendar myCalendar;

    Uri mImageUri;
    StorageReference mStorageRef;
    DatabaseReference mDatabaseRef, databaseReference;
    StorageTask mUploadTask;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    private String namalokasi = "Unknown";
    private String email;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private String Kategori = "Komplain Nikah";
    private String Status = "Menunggu Diproses";
    private String Lihat = "0";
    private String Suka = "0";
    private String Balas = "0";
    String jumlahKomplainIUMK, jumlahKomplainKependudukan, jumlahKomplainKTP,
            jumlahKomplainNikah, jumlahKomplainSPPT, jumlahKomplainTutupJalan;

    long totalBaru;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_tambah_nikah);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnBerkas = findViewById(R.id.btnBerkas);
        sendBtn = findViewById(R.id.sendBtn);
        edNomor = findViewById(R.id.edNomor);
        edNik = findViewById(R.id.edNIK);
        edNama = findViewById(R.id.edNama);
        edAlamat = findViewById(R.id.edAlamat);
        edTanggal = findViewById(R.id.edTanggal);
        edIsi = findViewById(R.id.edIsi);
        imageView = findViewById(R.id.imageView);
        tvNamaFile = findViewById(R.id.tvNamaFile);
        btnBack = findViewById(R.id.btnBack);
        tvJudul = findViewById(R.id.tvJudul);

        fromright = AnimationUtils.loadAnimation(this, R.anim.fromright);
        tvJudul.startAnimation(fromright);

        myCalendar = Calendar.getInstance();
        String myFormat = "dd MMMM yyyy";
        final SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        String myFormat2 = "yyyy";
        final SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat2);
        edTanggal.setText(sdf.format(myCalendar.getTime()));

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference("data_user");
        databaseReference.orderByChild("email").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            for (DataSnapshot dataUser : dataSnapshot.getChildren()) {
                                DataUser data = dataUser.getValue(DataUser.class);
                                final String getKey = (String) dataUser.getKey();
                                String nik = (String) dataUser.child("nik").getValue();
                                String email2 = (String) dataUser.child("email").getValue();
                                String nama = (String) dataUser.child("nama").getValue();
                                String alamat = (String) dataUser.child("alamat").getValue();

                                edNik.setText(nik);
                                email = email2;
                                edNama.setText(nama);
                                edAlamat.setText(alamat);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        FirebaseDatabase.getInstance().getReference("data_komplain").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    int satuan = 1;
                    long totalData;
                    totalData = dataSnapshot.getChildrenCount();
                    totalBaru = satuan + totalData;

                    if(totalBaru > 9){
                        edNomor.setText("411.3/"+String.valueOf(totalBaru)+"/MekarBaru/"+sdf2.format(myCalendar.getTime()));
                    } else {
                        edNomor.setText("411.3/0"+String.valueOf(totalBaru)+"/MekarBaru/"+sdf2.format(myCalendar.getTime()));
                    }
                } else {
                    edNomor.setText("411.3/01/MekarBaru/"+sdf2.format(myCalendar.getTime()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference("data_persentase_komplain").orderByChild("kategori").equalTo("Komplain IUMK")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            for (DataSnapshot dataPersentase : dataSnapshot.getChildren()) {
                                PersentaseKomplain data = dataPersentase.getValue(PersentaseKomplain.class);
                                final String getKey = (String) dataPersentase.getKey();
                                final String kategori = (String) dataPersentase.child("kategori").getValue();
                                jumlahKomplainIUMK = (String) dataPersentase.child("jumlah_komplain").getValue();
                            }
                        } else {
                            jumlahKomplainIUMK = "0";
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        FirebaseDatabase.getInstance().getReference("data_persentase_komplain").orderByChild("kategori").equalTo("Komplain Kependudukan")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            for (DataSnapshot dataPersentase : dataSnapshot.getChildren()) {
                                PersentaseKomplain data = dataPersentase.getValue(PersentaseKomplain.class);
                                final String getKey = (String) dataPersentase.getKey();
                                final String kategori = (String) dataPersentase.child("kategori").getValue();
                                jumlahKomplainKependudukan = (String) dataPersentase.child("jumlah_komplain").getValue();
                            }
                        } else {
                            jumlahKomplainKependudukan = "0";
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        FirebaseDatabase.getInstance().getReference("data_persentase_komplain").orderByChild("kategori").equalTo("Komplain KTP")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            for (DataSnapshot dataPersentase : dataSnapshot.getChildren()) {
                                PersentaseKomplain data = dataPersentase.getValue(PersentaseKomplain.class);
                                final String getKey = (String) dataPersentase.getKey();
                                final String kategori = (String) dataPersentase.child("kategori").getValue();
                                jumlahKomplainKTP = (String) dataPersentase.child("jumlah_komplain").getValue();
                            }
                        } else {
                            jumlahKomplainKTP = "0";
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        FirebaseDatabase.getInstance().getReference("data_persentase_komplain").orderByChild("kategori").equalTo("Komplain Nikah")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            for (DataSnapshot dataPersentase : dataSnapshot.getChildren()) {
                                PersentaseKomplain data = dataPersentase.getValue(PersentaseKomplain.class);
                                final String getKey = (String) dataPersentase.getKey();
                                final String kategori = (String) dataPersentase.child("kategori").getValue();
                                jumlahKomplainNikah = (String) dataPersentase.child("jumlah_komplain").getValue();
                            }
                        } else {
                            jumlahKomplainNikah = "0";
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        FirebaseDatabase.getInstance().getReference("data_persentase_komplain").orderByChild("kategori").equalTo("Komplain SPPT")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            for (DataSnapshot dataPersentase : dataSnapshot.getChildren()) {
                                PersentaseKomplain data = dataPersentase.getValue(PersentaseKomplain.class);
                                final String getKey = (String) dataPersentase.getKey();
                                final String kategori = (String) dataPersentase.child("kategori").getValue();
                                jumlahKomplainSPPT = (String) dataPersentase.child("jumlah_komplain").getValue();
                            }
                        } else {
                            jumlahKomplainSPPT = "0";
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        FirebaseDatabase.getInstance().getReference("data_persentase_komplain").orderByChild("kategori").equalTo("Komplain Tutup Jalan")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            for (DataSnapshot dataPersentase : dataSnapshot.getChildren()) {
                                PersentaseKomplain data = dataPersentase.getValue(PersentaseKomplain.class);
                                final String getKey = (String) dataPersentase.getKey();
                                final String kategori = (String) dataPersentase.child("kategori").getValue();
                                jumlahKomplainTutupJalan = (String) dataPersentase.child("jumlah_komplain").getValue();
                            }
                        } else {
                            jumlahKomplainTutupJalan = "0";
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        mStorageRef = FirebaseStorage.getInstance().getReference("foto_komplain");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("data_komplain");

        btnBerkas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        startActivityForResult(intent, PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            tvNamaFile.setVisibility(View.VISIBLE);
            tvNamaFile.setText(data.getData().getLastPathSegment());
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (isEmpty(edNomor.getText().toString()) || isEmpty(edNik.getText().toString()) || isEmpty(edNama.getText().toString())
                || isEmpty(edAlamat.getText().toString()) || isEmpty(edTanggal.getText().toString()) || isEmpty(edIsi.getText().toString())) {
            Toast.makeText(this, "Data tidak boleh kosong!", Toast.LENGTH_SHORT).show();

        } else if (mImageUri == null) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(TambahKomplainNikahActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.show_loading, null);

            mBuilder.setView(mView);
            mBuilder.setCancelable(false);
            final AlertDialog mDialog = mBuilder.create();
            mDialog.show();

            Komplain upload = new Komplain("", "", edNomor.getText().toString(), edNik.getText().toString(), email,
                    edNama.getText().toString(), edAlamat.getText().toString(), edTanggal.getText().toString(), namalokasi,
                    latitude, longitude, edIsi.getText().toString(), Kategori, Status, Lihat, Suka, Balas);

            String uploadId = mDatabaseRef.push().getKey();
            mDatabaseRef.child(uploadId).setValue(upload).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    FirebaseDatabase.getInstance().getReference("data_komplain").orderByChild("kategori").equalTo("Komplain Nikah").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                long totalData = dataSnapshot.getChildrenCount();
                                FirebaseDatabase.getInstance().getReference("data_persentase_komplain").child("Komplain Nikah").child("jumlah_komplain").setValue(String.valueOf(totalData));
                                FirebaseDatabase.getInstance().getReference("data_persentase_komplain").child("Komplain Nikah").child("kategori").setValue("Komplain Nikah");
                                FirebaseDatabase.getInstance().getReference("data_persentase_komplain").orderByChild("kategori").equalTo("Komplain Nikah").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()){
                                            for (DataSnapshot dataPersentase: dataSnapshot.getChildren()) {
                                                PersentaseKomplain data = dataPersentase.getValue(PersentaseKomplain.class);
                                                final String getKey = (String) dataPersentase.getKey();
                                                final String kategori = (String) dataPersentase.child("kategori").getValue();
                                                jumlahKomplainNikah = (String) dataPersentase.child("jumlah_komplain").getValue();

                                                double komplainIUMK = Double.parseDouble(jumlahKomplainIUMK);
                                                double komplainKependudukan = Double.parseDouble(jumlahKomplainKependudukan);
                                                double komplainKTP = Double.parseDouble(jumlahKomplainKTP);
                                                double komplainNikah = Double.parseDouble(jumlahKomplainNikah);
                                                double komplainSPPT = Double.parseDouble(jumlahKomplainSPPT);
                                                double komplainTutupJalan = Double.parseDouble(jumlahKomplainTutupJalan);
                                                double TotalPersen2 = (komplainIUMK / (komplainIUMK + komplainKependudukan + komplainKTP + komplainNikah + komplainSPPT + komplainTutupJalan)) * 100;
                                                double TotalPersen3 = (komplainKependudukan / (komplainIUMK + komplainKependudukan + komplainKTP + komplainNikah + komplainSPPT + komplainTutupJalan)) * 100;
                                                double TotalPersen4 = (komplainKTP / (komplainIUMK + komplainKependudukan + komplainKTP + komplainNikah + komplainSPPT + komplainTutupJalan)) * 100;
                                                double TotalPersen5 = (komplainNikah / (komplainIUMK + komplainKependudukan + komplainKTP + komplainNikah + komplainSPPT + komplainTutupJalan)) * 100;
                                                double TotalPersen6 = (komplainSPPT / (komplainIUMK + komplainKependudukan + komplainKTP + komplainNikah + komplainSPPT + komplainTutupJalan)) * 100;
                                                double TotalPersen7 = (komplainTutupJalan / (komplainIUMK + komplainKependudukan + komplainKTP + komplainNikah + komplainSPPT + komplainTutupJalan)) * 100;

                                                FirebaseDatabase.getInstance().getReference("data_persentase_komplain").child("Komplain IUMK").child("persen").setValue(String.valueOf(TotalPersen2));
                                                FirebaseDatabase.getInstance().getReference("data_persentase_komplain").child("Komplain Kependudukan").child("persen").setValue(String.valueOf(TotalPersen3));
                                                FirebaseDatabase.getInstance().getReference("data_persentase_komplain").child("Komplain KTP").child("persen").setValue(String.valueOf(TotalPersen4));
                                                FirebaseDatabase.getInstance().getReference("data_persentase_komplain").child("Komplain Nikah").child("persen").setValue(String.valueOf(TotalPersen5));
                                                FirebaseDatabase.getInstance().getReference("data_persentase_komplain").child("Komplain SPPT").child("persen").setValue(String.valueOf(TotalPersen6));
                                                FirebaseDatabase.getInstance().getReference("data_persentase_komplain").child("Komplain Tutup Jalan").child("persen").setValue(String.valueOf(TotalPersen7));

                                                TOPIC = "/topics/komplain";
                                                NOTIFICATION_TITLE = "Komplain Baru Diterima";
                                                NOTIFICATION_MESSAGE = "Dari : " +edNama.getText().toString();

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

                                                Toast.makeText(TambahKomplainNikahActivity.this, "Komplain berhasil dikirim!", Toast.LENGTH_LONG).show();
                                                mDialog.dismiss();
                                                startActivity(new Intent(TambahKomplainNikahActivity.this, MainActivity.class));
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

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

        } else {
            final StorageReference fileReference = mStorageRef.child("dokumen-nikah"+System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            AlertDialog.Builder mBuilder = new AlertDialog.Builder(TambahKomplainNikahActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.show_loading, null);

            mBuilder.setView(mView);
            mBuilder.setCancelable(false);
            final AlertDialog mDialog = mBuilder.create();
            mDialog.show();

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Komplain upload = new Komplain("", uri.toString(), edNomor.getText().toString(), edNik.getText().toString(), email,
                                            edNama.getText().toString(), edAlamat.getText().toString(), edTanggal.getText().toString(), namalokasi,
                                            latitude, longitude, edIsi.getText().toString(), Kategori, Status, Lihat, Suka, Balas);

                                    String uploadId = mDatabaseRef.push().getKey();
                                    mDatabaseRef.child(uploadId).setValue(upload).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            FirebaseDatabase.getInstance().getReference("data_komplain").orderByChild("kategori").equalTo("Komplain Nikah").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()){
                                                        long totalData = dataSnapshot.getChildrenCount();
                                                        FirebaseDatabase.getInstance().getReference("data_persentase_komplain").child("Komplain Nikah").child("jumlah_komplain").setValue(String.valueOf(totalData));
                                                        FirebaseDatabase.getInstance().getReference("data_persentase_komplain").child("Komplain Nikah").child("kategori").setValue("Komplain Nikah");
                                                        FirebaseDatabase.getInstance().getReference("data_persentase_komplain").orderByChild("kategori").equalTo("Komplain Nikah").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot.exists()){
                                                                    for (DataSnapshot dataPersentase: dataSnapshot.getChildren()) {
                                                                        PersentaseKomplain data = dataPersentase.getValue(PersentaseKomplain.class);
                                                                        final String getKey = (String) dataPersentase.getKey();
                                                                        final String kategori = (String) dataPersentase.child("kategori").getValue();
                                                                        jumlahKomplainNikah = (String) dataPersentase.child("jumlah_komplain").getValue();

                                                                        double komplainIUMK = Double.parseDouble(jumlahKomplainIUMK);
                                                                        double komplainKependudukan = Double.parseDouble(jumlahKomplainKependudukan);
                                                                        double komplainKTP = Double.parseDouble(jumlahKomplainKTP);
                                                                        double komplainNikah = Double.parseDouble(jumlahKomplainNikah);
                                                                        double komplainSPPT = Double.parseDouble(jumlahKomplainSPPT);
                                                                        double komplainTutupJalan = Double.parseDouble(jumlahKomplainTutupJalan);
                                                                        double TotalPersen2 = (komplainIUMK / (komplainIUMK + komplainKependudukan + komplainKTP + komplainNikah + komplainSPPT + komplainTutupJalan)) * 100;
                                                                        double TotalPersen3 = (komplainKependudukan / (komplainIUMK + komplainKependudukan + komplainKTP + komplainNikah + komplainSPPT + komplainTutupJalan)) * 100;
                                                                        double TotalPersen4 = (komplainKTP / (komplainIUMK + komplainKependudukan + komplainKTP + komplainNikah + komplainSPPT + komplainTutupJalan)) * 100;
                                                                        double TotalPersen5 = (komplainNikah / (komplainIUMK + komplainKependudukan + komplainKTP + komplainNikah + komplainSPPT + komplainTutupJalan)) * 100;
                                                                        double TotalPersen6 = (komplainSPPT / (komplainIUMK + komplainKependudukan + komplainKTP + komplainNikah + komplainSPPT + komplainTutupJalan)) * 100;
                                                                        double TotalPersen7 = (komplainTutupJalan / (komplainIUMK + komplainKependudukan + komplainKTP + komplainNikah + komplainSPPT + komplainTutupJalan)) * 100;

                                                                        FirebaseDatabase.getInstance().getReference("data_persentase_komplain").child("Komplain IUMK").child("persen").setValue(String.valueOf(TotalPersen2));
                                                                        FirebaseDatabase.getInstance().getReference("data_persentase_komplain").child("Komplain Kependudukan").child("persen").setValue(String.valueOf(TotalPersen3));
                                                                        FirebaseDatabase.getInstance().getReference("data_persentase_komplain").child("Komplain KTP").child("persen").setValue(String.valueOf(TotalPersen4));
                                                                        FirebaseDatabase.getInstance().getReference("data_persentase_komplain").child("Komplain Nikah").child("persen").setValue(String.valueOf(TotalPersen5));
                                                                        FirebaseDatabase.getInstance().getReference("data_persentase_komplain").child("Komplain SPPT").child("persen").setValue(String.valueOf(TotalPersen6));
                                                                        FirebaseDatabase.getInstance().getReference("data_persentase_komplain").child("Komplain Tutup Jalan").child("persen").setValue(String.valueOf(TotalPersen7));

                                                                        TOPIC = "/topics/komplain";
                                                                        NOTIFICATION_TITLE = "Komplain Baru Diterima";
                                                                        NOTIFICATION_MESSAGE = "Dari : " +edNama.getText().toString();

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

                                                                        Toast.makeText(TambahKomplainNikahActivity.this, "Komplain berhasil dikirim!", Toast.LENGTH_LONG).show();
                                                                        mDialog.dismiss();
                                                                        startActivity(new Intent(TambahKomplainNikahActivity.this, MainActivity.class));
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(TambahKomplainNikahActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        }
                    });
        }
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
                        Toast.makeText(TambahKomplainNikahActivity.this, "Request error!", Toast.LENGTH_LONG).show();
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
}
