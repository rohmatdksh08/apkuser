package com.jatmika.e_complaintrangkasbitung;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.jatmika.e_complaintrangkasbitung.Adapter.PagerAdapter;
import com.jatmika.e_complaintrangkasbitung.Model.DataUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView tvJudul;
    private static final String TAG = "mFirebaseIIDService";
    private static String SUBSCRIBE_TO;
    DatabaseReference databaseReference;
    String emailTopic;
    FirebaseAuth mAuth;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    FragmentComplaint complaintFragment = new FragmentComplaint();
                    tvJudul.setText("Laporan Komplain");
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.content, complaintFragment);
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_dashboard:
                    FragmentBerita beritaFragment = new FragmentBerita();
                    tvJudul.setText("Berita Terkini");
                    FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction2.replace(R.id.content, beritaFragment);
                    fragmentTransaction2.commit();
                    return true;
                case R.id.navigation_notifications:
                    FragmentProfile profilFragment = new FragmentProfile();
                    tvJudul.setText("Profil Saya");
                    FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction3.replace(R.id.content, profilFragment);
                    fragmentTransaction3.commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("data_user");
        databaseReference.orderByChild("email").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            for (DataSnapshot dataMahasiswa : dataSnapshot.getChildren()) {
                                DataUser data = dataMahasiswa.getValue(DataUser.class);
                                String email = (String) dataMahasiswa.child("email").getValue();

                                emailTopic = email;
                                emailTopic = emailTopic.replaceAll("[@.-]", "");

                                String token = FirebaseInstanceId.getInstance().getToken();
                                SUBSCRIBE_TO = String.valueOf(emailTopic);
                                FirebaseMessaging.getInstance().subscribeToTopic(SUBSCRIBE_TO);
                                Log.i(TAG, "onTokenRefresh completed with token: " + token);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvJudul = findViewById(R.id.tvJudul);
        tvJudul.setText("Laporan komplain");

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentComplaint complaintFragment = new FragmentComplaint();
        tvJudul.setText("Laporan Komplain");
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, complaintFragment);
        fragmentTransaction.commit();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            startActivity(new Intent(MainActivity.this, SplashActivity.class));
            moveTaskToBack(true);
        }
    }

    private void showDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        alertDialogBuilder.setTitle("Yakin ingin logout?");
        alertDialogBuilder
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(false)
                .setPositiveButton("Ya",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {

                        mAuth.signOut();

                        SUBSCRIBE_TO = String.valueOf(emailTopic);
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(String.valueOf(SUBSCRIBE_TO));

                        Toast.makeText(MainActivity.this, "Anda telah berhasil logout!", Toast.LENGTH_LONG).show();
                        Intent a = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(a);
                    }
                })
                .setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_kategori) {
            startActivity(new Intent(MainActivity.this, KategoriActivity.class));

        } else if (id == R.id.nav_kecamatan) {
            startActivity(new Intent(MainActivity.this, KecamatanActivity.class));

        } else if (id == R.id.nav_developer) {
            startActivity(new Intent(MainActivity.this, DeveloperActivity.class));

        } else if (id == R.id.nav_logout) {
            showDialog();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
