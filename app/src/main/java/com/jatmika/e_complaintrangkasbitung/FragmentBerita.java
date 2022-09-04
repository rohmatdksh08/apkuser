package com.jatmika.e_complaintrangkasbitung;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.jatmika.e_complaintrangkasbitung.API.API;
import com.jatmika.e_complaintrangkasbitung.API.APIUtility;
import com.jatmika.e_complaintrangkasbitung.Model.DataBerita;
import com.jatmika.e_complaintrangkasbitung.SharePref.SharePref;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentBerita extends Fragment implements RecyclerAdapter.OnItemClickListener{

    private TextView tvNoData;

    RecyclerView mRecyclerView;
    private RecyclerAdapter mAdapter;
    private ProgressBar mProgressBar;
    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private List<DataBerita> mPengaduans;
    SharePref sharePref;
    API apiService;

    private void openDetailActivity(String[] data){
        Intent intent = new Intent(getActivity(), DetailBeritaActivity.class);
        intent.putExtra("JUDUL_KEY",data[0]);
        intent.putExtra("TANGGAL_KEY",data[1]);
        intent.putExtra("PENULIS_KEY",data[2]);
        intent.putExtra("BERITA_KEY",data[3]);
        intent.putExtra("IMAGE_KEY",data[4]);
        intent.putExtra("GETPRIMARY_KEY",data[5]);
        startActivity(intent);
    }


    public FragmentBerita() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_berita, container, false);

        tvNoData = view.findViewById(R.id.tvNoData);

        mRecyclerView = view.findViewById(R.id.mRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mProgressBar = view.findViewById(R.id.myDataLoaderProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        sharePref = new SharePref(getActivity().getApplicationContext());
        apiService = APIUtility.getAPI();

        mPengaduans = new ArrayList<>();
        mAdapter = new RecyclerAdapter(getActivity(), mPengaduans);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

//        mDatabaseRef = FirebaseDatabase.getInstance().getReference("data_berita");
        apiService.getBerita("Bearer "+sharePref.getTokenApi()).enqueue(new Callback<List<DataBerita>>() {
            @Override
            public void onResponse(Call<List<DataBerita>> call, Response<List<DataBerita>> response) {
                if(response.body().size() > 0){
                    for (DataBerita dataBerita : response.body()){
                        mPengaduans.add(dataBerita);
                    }
                    mAdapter.notifyDataSetChanged();
                    tvNoData.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                }else{
                    tvNoData.setVisibility(View.VISIBLE);
                    tvNoData.setText("Belum Ada Berita");
                    mProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<DataBerita>> call, Throwable t) {

            }
        });

//        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                if (dataSnapshot.exists()) {
//                    mPengaduans.clear();
//                    for (DataSnapshot kandidatSnapshot : dataSnapshot.getChildren()) {
//                        DataBerita upload = kandidatSnapshot.getValue(DataBerita.class);
//                        upload.setKey(kandidatSnapshot.getKey());
//                        mPengaduans.add(upload);
//                    }
//                    mAdapter.notifyDataSetChanged();
//                    tvNoData.setVisibility(View.GONE);
//                    mProgressBar.setVisibility(View.GONE);
//                } else {
//                    tvNoData.setVisibility(View.VISIBLE);
//                    tvNoData.setText("Belum Ada Berita");
//                    mProgressBar.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                mProgressBar.setVisibility(View.INVISIBLE);
//            }
//        });

        return view;
    }

    @Override
    public void onItemClick(int position) {
        DataBerita clickedBerita = mPengaduans.get(position);
        String[] beritaData = {clickedBerita.getjudul(), clickedBerita.getcreated_at(), clickedBerita.getnama(),
                clickedBerita.getisi(), clickedBerita.getFoto(), clickedBerita.getid_berita()};
        openDetailActivity(beritaData);
    }
}
