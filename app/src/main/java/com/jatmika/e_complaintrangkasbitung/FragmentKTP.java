package com.jatmika.e_complaintrangkasbitung;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jatmika.e_complaintrangkasbitung.API.API;
import com.jatmika.e_complaintrangkasbitung.API.APIUtility;
import com.jatmika.e_complaintrangkasbitung.Adapter.RecyclerAdapterKomplain;
import com.jatmika.e_complaintrangkasbitung.Model.Komplain;
import com.jatmika.e_complaintrangkasbitung.SharePref.SharePref;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentKTP extends Fragment implements RecyclerAdapterKomplain.OnItemClickListener {

    private RecyclerAdapterKomplain mAdapter;
    private DatabaseReference mDatabaseRef;
    private List<Komplain> mPengaduans;
    private TextView tvNoData;
    private Context context;

    SharePref sharePref;
    API apiService;

    private void openDetailKomplainKTP(String[] data){
        Intent intent = new Intent(getActivity(), DetailKTPActivity.class);
        intent.putExtra("IMAGE_KEY",data[0]);
        intent.putExtra("BERKAS_KEY",data[1]);
        intent.putExtra("NOMOR_KEY",data[2]);
        intent.putExtra("NIK_KEY",data[3]);
        intent.putExtra("EMAIL_KEY",data[4]);
        intent.putExtra("NAMA_KEY",data[5]);
        intent.putExtra("ALAMAT_KEY",data[6]);
        intent.putExtra("TANGGAL_KEY",data[7]);
        intent.putExtra("NAMALOKASI_KEY",data[8]);
        intent.putExtra("LATITUDE_KEY",data[9]);
        intent.putExtra("LONGITUDE_KEY",data[10]);
        intent.putExtra("ISI_KEY",data[11]);
        intent.putExtra("KATEGORI_KEY",data[12]);
        intent.putExtra("STATUS_KEY",data[13]);
        intent.putExtra("JML_LIHAT_KEY",data[14]);
        intent.putExtra("JML_SUKA_KEY",data[15]);
        intent.putExtra("JML_BALAS_KEY",data[16]);
        intent.putExtra("GETPRIMARY_KEY",data[17]);
        startActivity(intent);
    }

    public FragmentKTP() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ktp, container, false);

        tvNoData = view.findViewById(R.id.tvNoData);
        context = getActivity().getApplicationContext();

        RecyclerView mRecyclerView = view.findViewById(R.id.mRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        sharePref = new SharePref(context);
        apiService = APIUtility.getAPI();

        mPengaduans = new ArrayList<>();
        mAdapter = new RecyclerAdapterKomplain(getActivity(), mPengaduans);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("data_komplain");

        apiService.getComplain("Bearer "+sharePref.getTokenApi(), "ktp").enqueue(new Callback<List<Komplain>>() {
            @Override
            public void onResponse(Call<List<Komplain>> call, Response<List<Komplain>> response) {
                Log.i("response", response.body().toString());
                for (Komplain komplain : response.body()){
                    mPengaduans.add(komplain);
                }
                mAdapter.notifyDataSetChanged();
                tvNoData.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Komplain>> call, Throwable t) {
                Log.i("responseError", t.toString());
            }
        });

        return view;
    }

    @Override
    public void onItemClick(int position) {
        Komplain clickedKomplain = mPengaduans.get(position);
        String[] komplainData = {clickedKomplain.getFoto(), clickedKomplain.getBerkas(), clickedKomplain.getNomor(),
                clickedKomplain.getNik(), clickedKomplain.getEmail(), clickedKomplain.getNama(), clickedKomplain.getAlamat(),
                clickedKomplain.getTanggal(), clickedKomplain.getNamalokasi(), String.valueOf(clickedKomplain.getLatitude()),
                String.valueOf(clickedKomplain.getLongitude()), clickedKomplain.getIsi(), clickedKomplain.getKategori(),
                clickedKomplain.getStatus(), clickedKomplain.getJml_lihat(), clickedKomplain.getJml_suka(),
                clickedKomplain.getJml_balas(), clickedKomplain.getKey()};

        openDetailKomplainKTP(komplainData);
    }
}
