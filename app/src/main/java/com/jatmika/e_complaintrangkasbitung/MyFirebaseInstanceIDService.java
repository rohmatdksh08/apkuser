package com.jatmika.e_complaintrangkasbitung;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.jatmika.e_complaintrangkasbitung.Model.DataUser;

import androidx.annotation.NonNull;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "mFirebaseIIDService";
    private static String SUBSCRIBE_TO;
    DatabaseReference databaseReference;

    @Override
    public void onTokenRefresh() {

        databaseReference = FirebaseDatabase.getInstance().getReference("data_user");
        databaseReference.orderByChild("email").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            for (DataSnapshot dataMahasiswa : dataSnapshot.getChildren()) {
                                DataUser data = dataMahasiswa.getValue(DataUser.class);
                                String email = (String) dataMahasiswa.child("email").getValue();

                                String token = FirebaseInstanceId.getInstance().getToken();
                                SUBSCRIBE_TO = email;
                                Log.i(TAG, "onTokenRefresh completed with token: " + token);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}