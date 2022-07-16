package com.jatmika.e_complaintrangkasbitung;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import pl.aprilapps.easyphotopicker.EasyImage;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import com.bumptech.glide.Glide;
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
import com.jatmika.e_complaintrangkasbitung.Model.GPSTrack;
import com.jatmika.e_complaintrangkasbitung.Model.Komplain;
import com.jatmika.e_complaintrangkasbitung.Model.MySingleton;
import com.jatmika.e_complaintrangkasbitung.Model.PersentaseKomplain;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.text.TextUtils.isEmpty;

public class TambahKomplainInfrastrukturActivity extends AppCompatActivity {

    final private String serverKey = "key=" + "AIzaSyDL3TGOXPGwfM7iIf_pX4zqD3IbTr-I45w";
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";

    String FCM_API = "https://fcm.googleapis.com/fcm/send";
    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;

    private static final int PICK_IMAGE_CAMERA = 001;
    private static final int PICK_IMAGE_GALLERY = 002;
    Button chooseBtn, sendBtn;
    EditText edNomor, edNik, edNama, edAlamat, edTanggal, edNamaLokasi, edIsi;
    ImageView imageView;
    TextView tvJudul;
    RelativeLayout btnBack, relative1;
    Animation fromright;
    Calendar myCalendar;

    StorageReference mStorageRef;
    DatabaseReference mDatabaseRef, databaseReference;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    private String email;
    private double latitude;
    private double longitude;
    private String Kategori = "Komplain Infrastruktur";
    private String Status = "Menunggu Diproses";
    private String Lihat = "0";
    private String Suka = "0";
    private String Balas = "0";
    String jumlahKomplainInfrastruktur, jumlahKomplainIUMK, jumlahKomplainKependudukan, jumlahKomplainKTP,
            jumlahKomplainNikah, jumlahKomplainSPPT, jumlahKomplainTutupJalan;

    long totalBaru;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_tambah_infrastruktur);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        chooseBtn = findViewById(R.id.chooseBtn);
        sendBtn = findViewById(R.id.sendBtn);
        edNomor = findViewById(R.id.edNomor);
        edNik = findViewById(R.id.edNIK);
        edNama = findViewById(R.id.edNama);
        edAlamat = findViewById(R.id.edAlamat);
        edTanggal = findViewById(R.id.edTanggal);
        edNamaLokasi = findViewById(R.id.edNamaLokasi);
        relative1 = findViewById(R.id.relative1);
        edIsi = findViewById(R.id.edIsi);
        imageView = findViewById(R.id.imageView);
        btnBack = findViewById(R.id.btnBack);
        tvJudul = findViewById(R.id.tvJudul);

        GPSTrack gps = new GPSTrack(this);
        latitude = gps.getLatitude();
        longitude = gps.getLongitude();

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
                        edNomor.setText("411.3/"+String.valueOf(totalBaru)+"/Rangkasbitung/"+sdf2.format(myCalendar.getTime()));
                    } else {
                        edNomor.setText("411.3/0"+String.valueOf(totalBaru)+"/Rangkasbitung/"+sdf2.format(myCalendar.getTime()));
                    }
                } else {
                    edNomor.setText("411.3/01/Rangkasbitung/"+sdf2.format(myCalendar.getTime()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference("data_persentase_komplain").orderByChild("kategori").equalTo("Komplain Infrastruktur")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            for (DataSnapshot dataPersentase : dataSnapshot.getChildren()) {
                                PersentaseKomplain data = dataPersentase.getValue(PersentaseKomplain.class);
                                final String getKey = (String) dataPersentase.getKey();
                                final String kategori = (String) dataPersentase.child("kategori").getValue();
                                jumlahKomplainInfrastruktur = (String) dataPersentase.child("jumlah_komplain").getValue();
                            }
                        } else {
                            jumlahKomplainInfrastruktur = "0";
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

        chooseBtn.setOnClickListener(new View.OnClickListener() {
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

        relative1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GPSTrack gps = new GPSTrack(TambahKomplainInfrastrukturActivity.this);
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
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
        CharSequence[] item = {"Kamera", "Galeri"};
        AlertDialog.Builder request = new AlertDialog.Builder(TambahKomplainInfrastrukturActivity.this)
                .setTitle("Tambah Foto")
                .setItems(item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        switch (i){
                            case 0:
                                EasyImage.openCamera(TambahKomplainInfrastrukturActivity.this, PICK_IMAGE_CAMERA);
                                break;
                            case 1:
                                EasyImage.openGallery(TambahKomplainInfrastrukturActivity.this, PICK_IMAGE_GALLERY);
                                break;
                        }
                    }
                });
        request.create();
        request.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new EasyImage.Callbacks() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {

            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                switch (type){
                    case PICK_IMAGE_CAMERA:
                        Glide.with(TambahKomplainInfrastrukturActivity.this)
                                .asBitmap()
                                .load(imageFile)
                                .into(imageView);
                        break;
                    case PICK_IMAGE_GALLERY:
                        Glide.with(TambahKomplainInfrastrukturActivity.this)
                                .load(imageFile)
                                .into(imageView);
                        break;
                }
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {

            }
        });
    }

    private void uploadFile() {
        if (isEmpty(edNomor.getText().toString()) || isEmpty(edNik.getText().toString()) || isEmpty(edNama.getText().toString())
                || isEmpty(edAlamat.getText().toString()) || isEmpty(edTanggal.getText().toString()) || isEmpty(edIsi.getText().toString())) {
            Toast.makeText(this, "Data tidak boleh kosong!", Toast.LENGTH_SHORT).show();

        } else if (latitude == 0.0 && longitude == 0.0){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(TambahKomplainInfrastrukturActivity.this);
            alertDialog.setTitle("Lokasi Tidak Ditemukan");
            alertDialog.setMessage("Tekan tombol refresh di samping Lokasi Kejadian");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();

        } else {
            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(data));

            AlertDialog.Builder mBuilder = new AlertDialog.Builder(TambahKomplainInfrastrukturActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.show_loading, null);

            mBuilder.setView(mView);
            mBuilder.setCancelable(false);
            final AlertDialog mDialog = mBuilder.create();
            mDialog.show();

            UploadTask uploadTask = fileReference.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                        }
                    }, 300);

                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Komplain upload = new Komplain(uri.toString(), "", edNomor.getText().toString(), edNik.getText().toString(), email,
                                    edNama.getText().toString(), edAlamat.getText().toString(), edTanggal.getText().toString(), edNamaLokasi.getText().toString(),
                                    latitude, longitude, edIsi.getText().toString(), Kategori, Status, Lihat, Suka, Balas);

                            String uploadId = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadId).setValue(upload).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    FirebaseDatabase.getInstance().getReference("data_komplain").orderByChild("kategori").equalTo("Komplain Infrastruktur").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()){
                                                long totalData = dataSnapshot.getChildrenCount();
                                                FirebaseDatabase.getInstance().getReference("data_persentase_komplain").child("Komplain Infrastruktur").child("jumlah_komplain").setValue(String.valueOf(totalData));
                                                FirebaseDatabase.getInstance().getReference("data_persentase_komplain").child("Komplain Infrastruktur").child("kategori").setValue("Komplain Infrastruktur");
                                                FirebaseDatabase.getInstance().getReference("data_persentase_komplain").orderByChild("kategori").equalTo("Komplain Infrastruktur").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists()){
                                                            for (DataSnapshot dataPersentase: dataSnapshot.getChildren()) {
                                                                PersentaseKomplain data = dataPersentase.getValue(PersentaseKomplain.class);
                                                                final String getKey = (String) dataPersentase.getKey();
                                                                final String kategori = (String) dataPersentase.child("kategori").getValue();
                                                                jumlahKomplainInfrastruktur = (String) dataPersentase.child("jumlah_komplain").getValue();

                                                                double komplainInfrastruktur = Double.parseDouble(jumlahKomplainInfrastruktur);
                                                                double komplainIUMK = Double.parseDouble(jumlahKomplainIUMK);
                                                                double komplainKependudukan = Double.parseDouble(jumlahKomplainKependudukan);
                                                                double komplainKTP = Double.parseDouble(jumlahKomplainKTP);
                                                                double komplainNikah = Double.parseDouble(jumlahKomplainNikah);
                                                                double komplainSPPT = Double.parseDouble(jumlahKomplainSPPT);
                                                                double komplainTutupJalan = Double.parseDouble(jumlahKomplainTutupJalan);
                                                                double TotalPersen1 = (komplainInfrastruktur / (komplainInfrastruktur + komplainIUMK + komplainKependudukan + komplainKTP + komplainNikah + komplainSPPT + komplainTutupJalan)) * 100;
                                                                double TotalPersen2 = (komplainIUMK / (komplainInfrastruktur + komplainIUMK + komplainKependudukan + komplainKTP + komplainNikah + komplainSPPT + komplainTutupJalan)) * 100;
                                                                double TotalPersen3 = (komplainKependudukan / (komplainInfrastruktur + komplainIUMK + komplainKependudukan + komplainKTP + komplainNikah + komplainSPPT + komplainTutupJalan)) * 100;
                                                                double TotalPersen4 = (komplainKTP / (komplainInfrastruktur + komplainIUMK + komplainKependudukan + komplainKTP + komplainNikah + komplainSPPT + komplainTutupJalan)) * 100;
                                                                double TotalPersen5 = (komplainNikah / (komplainInfrastruktur + komplainIUMK + komplainKependudukan + komplainKTP + komplainNikah + komplainSPPT + komplainTutupJalan)) * 100;
                                                                double TotalPersen6 = (komplainSPPT / (komplainInfrastruktur + komplainIUMK + komplainKependudukan + komplainKTP + komplainNikah + komplainSPPT + komplainTutupJalan)) * 100;
                                                                double TotalPersen7 = (komplainTutupJalan / (komplainInfrastruktur + komplainIUMK + komplainKependudukan + komplainKTP + komplainNikah + komplainSPPT + komplainTutupJalan)) * 100;

                                                                FirebaseDatabase.getInstance().getReference("data_persentase_komplain").child("Komplain Infrastruktur").child("persen").setValue(String.valueOf(TotalPersen1));
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

                                                                Toast.makeText(TambahKomplainInfrastrukturActivity.this, "Komplain berhasil dikirim!", Toast.LENGTH_LONG).show();
                                                                mDialog.dismiss();
                                                                startActivity(new Intent(TambahKomplainInfrastrukturActivity.this, MainActivity.class));
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
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(TambahKomplainInfrastrukturActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                }
            });

        }
    }

    private byte[] getFileExtension(byte[] data) {
        return data;
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
                        Toast.makeText(TambahKomplainInfrastrukturActivity.this, "Request error!", Toast.LENGTH_LONG).show();
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
