package com.jatmika.e_complaintrangkasbitung;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.jatmika.e_complaintrangkasbitung.API.API;
import com.jatmika.e_complaintrangkasbitung.API.APIUtility;
import com.jatmika.e_complaintrangkasbitung.Model.DataUser;
import com.jatmika.e_complaintrangkasbitung.Model.Penduduk;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;

import static android.text.TextUtils.isEmpty;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    RelativeLayout btnBack;
    TextView tvJudul;
    Animation fromright;

    ImageView photo, chooseBtn, btnShow;
    EditText edNik, edEmail, edPass, edNohp;
    TextView edNama, edTTL, edAlamat, tvjenkel;
    RadioButton radioPria, radioWanita;
    Button btnDaftar;
    API apiService;

    Uri mImageUri;
    StorageReference mStorageRef;
    DatabaseReference mDatabaseRef;
    StorageTask mUploadTask;
    FirebaseAuth auth;

    private static final int PICK_IMAGE_REQUEST = 1;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;

    String jenkel;
    String id_penduduk = "";
    String show = "SHOW";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        apiService = APIUtility.getAPI();

        btnBack = findViewById(R.id.btnBack);
        tvJudul = findViewById(R.id.tvJudul);
        photo = findViewById(R.id.photo);
        chooseBtn = findViewById(R.id.chooseBtn);
        edNik = findViewById(R.id.edNIK);
        edEmail = findViewById(R.id.edEmail);
        edPass = findViewById(R.id.edPass);
        edNama = findViewById(R.id.edNama);
        edTTL = findViewById(R.id.edTTL);
        edAlamat = findViewById(R.id.edAlamat);
        edNohp = findViewById(R.id.edNohp);
        btnShow = findViewById(R.id.ic_toggle);
        btnDaftar = findViewById(R.id.btnDaftar);
        tvjenkel = findViewById(R.id.tvJenkelReg);

        fromright = AnimationUtils.loadAnimation(this, R.anim.fromright);
        tvJudul.startAnimation(fromright);

        edNik.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i("ukuran", "ukuran"+s.length());
                    if (s.length() >= 16){
                        apiService.searchPenduduk(edNik.getText().toString()).enqueue(new Callback<Penduduk>() {
                            @Override
                            public void onResponse(Call<Penduduk> call, Response<Penduduk> response) {
                                if(response.code() == 200){
                                    edNama.setText(response.body().getNama_penduduk());
                                    LocalDate ld = LocalDate.parse(response.body().getTanggal_lahir());
                                    String ttl = ld.getDayOfMonth()+" "+ld.getMonth()+" "+ld.getYear();
                                    edTTL.setText(ttl);
                                    edAlamat.setText(response.body().getAlamat());
                                    tvjenkel.setText(response.body().getJenis_kelamin());
                                    id_penduduk = response.body().getId_penduduk().toString();
                                }else{
                                    Toast.makeText(RegisterActivity.this, "NIK tidak ditemukan! Harap menghubungi Admin", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Penduduk> call, Throwable t) {

                            }
                        });
                    }
                }
        });

        auth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("foto_user");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("data_user");

        edPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(show.equals("SHOW")) {
                    show = "HIDE";
                    edPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    edPass.setSelection(edPass.length());
                } else {
                    show = "SHOW";
                    edPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    edPass.setSelection(edPass.length());
                }
            }

        });

        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        edTTL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(RegisterActivity.this, date,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        btnDaftar.setOnClickListener(new View.OnClickListener() {
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

    private void updateLabel(){
        String myFormat = "dd MMMM yyyy";
        DateFormat sdf = new SimpleDateFormat(myFormat);
        edTTL.setText(sdf.format(myCalendar.getTime()));
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Glide.with(this).load(mImageUri).into(photo);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        String email = edEmail.getText().toString();
        final String password = edPass.getText().toString();

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(RegisterActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.show_loading, null);

        mBuilder.setView(mView);
        mBuilder.setCancelable(false);
        final AlertDialog mDialog = mBuilder.create();
        mDialog.show();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(RegisterActivity.this, "Email harus diisi!", Toast.LENGTH_SHORT).show();
            mDialog.dismiss();

        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(RegisterActivity.this, "Email tidak valid!", Toast.LENGTH_SHORT).show();
            mDialog.dismiss();

        } else if (TextUtils.isEmpty(password)){
            Toast.makeText(RegisterActivity.this, "Password harus diisi!", Toast.LENGTH_SHORT).show();
            mDialog.dismiss();

        } else if (password.length() < 6){
            Toast.makeText(RegisterActivity.this, "Password minimal harus 6 karakter", Toast.LENGTH_SHORT).show();
            mDialog.dismiss();

        } else if (isEmpty(edNama.getText().toString()) || isEmpty(edTTL.getText().toString())
                || isEmpty(edAlamat.getText().toString()) || isEmpty(edNohp.getText().toString())){
            Toast.makeText(this, "Data harus dilengkapi!", Toast.LENGTH_SHORT).show();
            mDialog.dismiss();
        } else {
            apiService.register(id_penduduk, email, edPass.getText().toString(), edNohp.getText().toString()).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    mDialog.dismiss();
                    if(response.code() == 200){
                        Toast.makeText(RegisterActivity.this, "User Berhasil dibuat, Silahkan login!", Toast.LENGTH_SHORT).show();
                    }
                    if(response.code() == 302){
                        Toast.makeText(RegisterActivity.this, "NIK sudah terdaftar!", Toast.LENGTH_SHORT).show();
                    }
                    if(response.code() == 409){
                        Toast.makeText(RegisterActivity.this, "Email sudah digunakan!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }
    }
}
