package com.jatmika.e_complaintrangkasbitung;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.jatmika.e_complaintrangkasbitung.Model.DataUser;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentProfile extends Fragment {

    private TextView tvNik, tvEmail, tvPassword, tvNama, tvTTL, tvJenkel, tvAlamat, tvNohp;
    private CircleImageView fotoProfile;

    FirebaseAuth auth;
    FirebaseUser user;
    private StorageReference mStorage;
    DatabaseReference databaseReference;

    private String getKey, foto, nik, email, password, nama, ttl, jenkel, alamat, nohp, jenkelEdit, emailTopic;;
    private Context context;
    private static String SUBSCRIBE_TO;
    private static final int PICK_IMAGE_REQUEST = 1;

    public FragmentProfile() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        context = getActivity().getApplicationContext();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        tvNik = view.findViewById(R.id.tvNik);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPassword = view.findViewById(R.id.tvPassword);
        tvNama = view.findViewById(R.id.tvNama);
        tvTTL = view.findViewById(R.id.tvTTL);
        tvJenkel = view.findViewById(R.id.tvJenkel);
        tvAlamat = view.findViewById(R.id.tvAlamat);
        tvNohp = view.findViewById(R.id.tvNohp);
        fotoProfile = view.findViewById(R.id.imageProfile);
        ImageView chooseBtn = view.findViewById(R.id.chooseBtn);
        ImageView btnEditNik = view.findViewById(R.id.btnEditNik);
        ImageView btnEditNama = view.findViewById(R.id.btnEditNama);
        ImageView btnEditTTL = view.findViewById(R.id.btnEditTTL);
        ImageView btnEditJenkel = view.findViewById(R.id.btnEditJenkel);
        ImageView btnEditAlamat = view.findViewById(R.id.btnEditAlamat);
        ImageView btnEditNohp = view.findViewById(R.id.btnEditNohp);
        Button btnLaporan = view.findViewById(R.id.btnPengaduan);
        Button btnHapus = view.findViewById(R.id.btnHapus);

        mStorage = FirebaseStorage.getInstance().getReference("foto_user");
        databaseReference = FirebaseDatabase.getInstance().getReference("data_user");

        showProfile();

        btnEditNik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getLayoutInflater().inflate(R.layout.show_edit_profile, null);

                TextView tvTitle = mView.findViewById(R.id.tvTitle);
                TextView tvBatal = mView.findViewById(R.id.tvBatal);
                final TextView tvSimpan = mView.findViewById(R.id.tvSimpan);
                final EditText edEdit = mView.findViewById(R.id.edEdit);
                final ProgressBar progressBar = mView.findViewById(R.id.progressBar);

                tvTitle.setText("Masukkan NIK Anda");
                edEdit.setText(nik);
                edEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
                edEdit.setHint("Nomor Induk Keluarga");

                mBuilder.setView(mView);
                mBuilder.setCancelable(false);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                tvBatal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                tvSimpan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        edEdit.setEnabled(false);
                        tvSimpan.setEnabled(false);
                        progressBar.setVisibility(View.VISIBLE);

                        databaseReference.orderByChild("nik").equalTo(edEdit.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Toast.makeText(getActivity(), "NIK yang Anda ubah sudah terdaftar!", Toast.LENGTH_LONG).show();
                                    edEdit.setEnabled(true);
                                    tvSimpan.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    databaseReference.child(getKey).child("nik").setValue(edEdit.getText().toString())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Handler handler = new Handler();
                                                    handler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            dialog.dismiss();
                                                            Toast.makeText(getActivity(), "Nomor Induk Keluarga Anda berhasil diubah!", Toast.LENGTH_LONG).show();
                                                            showProfile();
                                                        }}, 1500);
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

        btnEditNama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getLayoutInflater().inflate(R.layout.show_edit_profile, null);

                TextView tvTitle = mView.findViewById(R.id.tvTitle);
                TextView tvBatal = mView.findViewById(R.id.tvBatal);
                final TextView tvSimpan = mView.findViewById(R.id.tvSimpan);
                final EditText edEdit = mView.findViewById(R.id.edEdit);
                final ProgressBar progressBar = mView.findViewById(R.id.progressBar);

                tvTitle.setText("Masukkan nama Anda");
                edEdit.setText(nama);
                edEdit.setHint("Nama");

                mBuilder.setView(mView);
                mBuilder.setCancelable(true);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                tvBatal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                tvSimpan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        edEdit.setEnabled(false);
                        tvSimpan.setEnabled(false);
                        progressBar.setVisibility(View.VISIBLE);

                        databaseReference.child(getKey).child("nama").setValue(edEdit.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog.dismiss();
                                                Toast.makeText(getActivity(), "Nama Anda berhasil diubah!", Toast.LENGTH_LONG).show();
                                                showProfile();
                                            }}, 1500);
                                    }
                                });
                    }
                });
            }
        });

        btnEditTTL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getLayoutInflater().inflate(R.layout.show_edit_profile, null);

                final Calendar myCalendar;
                final DatePickerDialog.OnDateSetListener date;
                TextView tvTitle = mView.findViewById(R.id.tvTitle);
                TextView tvBatal = mView.findViewById(R.id.tvBatal);
                final TextView tvSimpan = mView.findViewById(R.id.tvSimpan);
                final EditText edEdit = mView.findViewById(R.id.edEdit);
                final EditText edEditTanggal = mView.findViewById(R.id.edEditTanggal);
                final ProgressBar progressBar = mView.findViewById(R.id.progressBar);

                tvTitle.setText("Masukkan tanggal lahir Anda");
                edEdit.setVisibility(View.GONE);
                edEditTanggal.setVisibility(View.VISIBLE);
                edEditTanggal.setText(ttl);

                myCalendar = Calendar.getInstance();
                date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String myFormat = "dd MMMM yyyy";
                        DateFormat sdf = new SimpleDateFormat(myFormat);
                        edEditTanggal.setText(sdf.format(myCalendar.getTime()));
                    }
                };

                edEditTanggal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DatePickerDialog(getActivity(), date,
                                myCalendar.get(Calendar.YEAR),
                                myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });

                mBuilder.setView(mView);
                mBuilder.setCancelable(true);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                tvBatal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                tvSimpan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        edEditTanggal.setEnabled(false);
                        tvSimpan.setEnabled(false);
                        progressBar.setVisibility(View.VISIBLE);

                        databaseReference.child(getKey).child("ttl").setValue(edEditTanggal.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog.dismiss();
                                                Toast.makeText(getActivity(), "Tanggal lahir Anda berhasil diubah!", Toast.LENGTH_LONG).show();
                                                showProfile();
                                            }}, 1500);
                                    }
                                });
                    }
                });
            }
        });

        btnEditJenkel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getLayoutInflater().inflate(R.layout.show_edit_profile, null);

                TextView tvTitle = mView.findViewById(R.id.tvTitle);
                TextView tvBatal = mView.findViewById(R.id.tvBatal);
                final TextView tvSimpan = mView.findViewById(R.id.tvSimpan);
                RelativeLayout relativeJenkel = mView.findViewById(R.id.relativeJenkel);
                final RadioButton radioPria = mView.findViewById(R.id.radioPria);
                final RadioButton radioWanita = mView.findViewById(R.id.radioWanita);
                final EditText edEdit = mView.findViewById(R.id.edEdit);
                final ProgressBar progressBar = mView.findViewById(R.id.progressBar);

                tvTitle.setText("Masukkan jenis kelamin Anda");
                edEdit.setVisibility(View.GONE);
                relativeJenkel.setVisibility(View.VISIBLE);

                mBuilder.setView(mView);
                mBuilder.setCancelable(true);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                radioPria.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        radioWanita.setChecked(false);
                        jenkelEdit = "Pria";
                    }
                });

                radioWanita.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        radioPria.setChecked(false);
                        jenkelEdit = "Wanita";
                    }
                });

                tvBatal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                tvSimpan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        radioPria.setEnabled(false);
                        radioWanita.setEnabled(false);
                        tvSimpan.setEnabled(false);
                        progressBar.setVisibility(View.VISIBLE);

                        databaseReference.child(getKey).child("jenkel").setValue(jenkelEdit)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog.dismiss();
                                                Toast.makeText(getActivity(), "Jenis kelamin Anda berhasil diubah!", Toast.LENGTH_LONG).show();
                                                showProfile();
                                            }}, 1500);
                                    }
                                });
                    }
                });
            }
        });

        btnEditAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getLayoutInflater().inflate(R.layout.show_edit_profile, null);

                TextView tvTitle = mView.findViewById(R.id.tvTitle);
                TextView tvBatal = mView.findViewById(R.id.tvBatal);
                final TextView tvSimpan = mView.findViewById(R.id.tvSimpan);
                final EditText edEdit = mView.findViewById(R.id.edEdit);
                final ProgressBar progressBar = mView.findViewById(R.id.progressBar);

                tvTitle.setText("Masukkan alamat Anda");
                edEdit.setText(alamat);
                edEdit.setHint("Alamat");

                mBuilder.setView(mView);
                mBuilder.setCancelable(true);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                tvBatal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                tvSimpan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        edEdit.setEnabled(false);
                        tvSimpan.setEnabled(false);
                        progressBar.setVisibility(View.VISIBLE);

                        databaseReference.child(getKey).child("alamat").setValue(edEdit.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog.dismiss();
                                                Toast.makeText(getActivity(), "Alamat Anda berhasil diubah!", Toast.LENGTH_LONG).show();
                                                showProfile();
                                            }}, 1500);
                                    }
                                });
                    }
                });
            }
        });

        btnEditNohp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getLayoutInflater().inflate(R.layout.show_edit_profile, null);

                TextView tvTitle = mView.findViewById(R.id.tvTitle);
                TextView tvBatal = mView.findViewById(R.id.tvBatal);
                final TextView tvSimpan = mView.findViewById(R.id.tvSimpan);
                final EditText edEdit = mView.findViewById(R.id.edEdit);
                final ProgressBar progressBar = mView.findViewById(R.id.progressBar);

                tvTitle.setText("Masukkan nomor telpon Anda");
                edEdit.setText(nohp);
                edEdit.setHint("Nomor telpon");
                edEdit.setInputType(InputType.TYPE_CLASS_NUMBER);

                mBuilder.setView(mView);
                mBuilder.setCancelable(true);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                tvBatal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                tvSimpan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        edEdit.setEnabled(false);
                        tvSimpan.setEnabled(false);
                        progressBar.setVisibility(View.VISIBLE);

                        databaseReference.child(getKey).child("nohp").setValue(edEdit.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog.dismiss();
                                                Toast.makeText(getActivity(), "Nomor telpon Anda berhasil diubah!", Toast.LENGTH_LONG).show();
                                                showProfile();
                                            }}, 1500);
                                    }
                                });
                    }
                });
            }
        });

//        chooseBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openFileChooser();
//            }
//        });

        btnLaporan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), KomplainSayaActivity.class));
            }
        });

        btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
            }
        });

        return view;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            final Uri mImageUri = data.getData();

            Glide.with(this).load(mImageUri).into(fotoProfile);

            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle("Ubah foto profil?");
            alertDialogBuilder.setIcon(R.mipmap.ic_launcher).setCancelable(false)
                    .setPositiveButton("Ya",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.cancel();

                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                            View mView = getLayoutInflater().inflate(R.layout.show_loading, null);

                            mBuilder.setView(mView);
                            mBuilder.setCancelable(true);
                            final AlertDialog dialog2 = mBuilder.create();
                            dialog2.show();

                            StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(foto);
                            imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    final StorageReference fileReference = mStorage.child(System.currentTimeMillis()
                                            + "." + getFileExtension(mImageUri));

                                    StorageTask mUploadTask = fileReference.putFile(mImageUri)
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            databaseReference.child(getKey).child("photo").setValue(uri.toString())
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            dialog2.dismiss();
                                                                            Toast.makeText(getActivity(), "Foto profil Anda berhasil diubah!", Toast.LENGTH_LONG).show();
                                                                            showProfile();
                                                                        }
                                                                    });
                                                        }
                                                    });
                                                }
                                            });
                                }
                            });
                        }
                    })
                    .setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Glide.with(FragmentProfile.this.context).load(foto).into(fotoProfile);
                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = FragmentProfile.this.context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void showProfile(){
//        databaseReference.orderByChild("email").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists()){
//                            for (DataSnapshot dataUser : dataSnapshot.getChildren()) {
//                                DataUser data = dataUser.getValue(DataUser.class);
//                                getKey = (String) dataUser.getKey();
//                                foto = (String) dataUser.child("photo").getValue();
//                                nik = (String) dataUser.child("nik").getValue();
//                                email = (String) dataUser.child("email").getValue();
//                                password = (String) dataUser.child("password").getValue();
//                                nama = (String) dataUser.child("nama").getValue();
//                                ttl = (String) dataUser.child("ttl").getValue();
//                                jenkel = (String) dataUser.child("jenkel").getValue();
//                                alamat = (String) dataUser.child("alamat").getValue();
//                                nohp = (String) dataUser.child("nohp").getValue();
//
//                                Glide.with(FragmentProfile.this.context).load(foto).into(fotoProfile);
//                                tvNik.setText(nik);
//                                tvEmail.setText(email);
//                                tvPassword.setText(password);
//                                tvNama.setText(nama);
//                                tvTTL.setText(ttl);
//                                tvJenkel.setText(jenkel);
//                                tvAlamat.setText(alamat);
//                                tvNohp.setText(nohp);
//
//                                emailTopic = email;
//                                emailTopic = emailTopic.replaceAll("[@.-]", "");
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
    }

    private void deleteAccount() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Hapus akun?");
        alertDialogBuilder
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                        View mView = getLayoutInflater().inflate(R.layout.show_loading, null);

                        mBuilder.setView(mView);
                        mBuilder.setCancelable(false);
                        final AlertDialog mDialog = mBuilder.create();
                        mDialog.show();

                        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(foto);
                                            imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    databaseReference.child(getKey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            auth = FirebaseAuth.getInstance();
                                                            auth.signOut();

                                                            SUBSCRIBE_TO = String.valueOf(emailTopic);
                                                            FirebaseMessaging.getInstance().unsubscribeFromTopic(String.valueOf(SUBSCRIBE_TO));

                                                            mDialog.dismiss();
                                                            Toast.makeText(getActivity(), "Akun berhasil dihapus!", Toast.LENGTH_SHORT).show();
                                                            Intent a = new Intent(getActivity(), LoginActivity.class);
                                                            startActivity(a);
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        });
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
