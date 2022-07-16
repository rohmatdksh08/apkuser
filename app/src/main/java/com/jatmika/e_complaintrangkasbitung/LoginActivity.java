package com.jatmika.e_complaintrangkasbitung;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText edEmail, edPass;
    TextView tvRegister;
    ImageView btnShow;
    Button btnLogin;
    ProgressBar progressBar;
    FirebaseAuth auth;
    String show = "SHOW";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent a = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(a);
        }

        edEmail = findViewById(R.id.edEmail);
        edPass = findViewById(R.id.edPass);
        tvRegister = findViewById(R.id.tvRegister);
        btnShow = findViewById(R.id.ic_toggle);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progress_bar);

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

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

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edEmail.getText().toString();
                final String password = edPass.getText().toString();

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(LoginActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.show_loading, null);

                mBuilder.setView(mView);
                mBuilder.setCancelable(false);
                final AlertDialog mDialog = mBuilder.create();
                mDialog.show();

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(LoginActivity.this, "Email harus diisi!", Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(LoginActivity.this, "Email tidak valid!", Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this, "Password harus diisi!", Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                    return;
                }

                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(!task.isSuccessful()){
                                    if (password.length() < 6) {
                                        Toast.makeText(LoginActivity.this, "Password minimal harus 6 karakter!", Toast.LENGTH_SHORT).show();
                                        mDialog.dismiss();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Akun belum terdaftar!", Toast.LENGTH_SHORT).show();
                                        mDialog.dismiss();
                                    }
                                } else {
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    mDialog.dismiss();
                                    finish();
                                }
                            }
                        });
            }
        });
    }

    public void onBackPressed(){
        startActivity(new Intent(LoginActivity.this, SplashActivity.class));
        moveTaskToBack(true);
    }
}
