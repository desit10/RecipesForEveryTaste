package com.example.recipesforeverytaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.recipesforeverytaste.Helpers.FirebaseHelper;
import com.example.recipesforeverytaste.Models.LiveStream;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class LiveStreamActivity extends AppCompatActivity {

    String userID, userName, liveID;
    Boolean isHost;
    FirebaseHelper firebaseHelper;
    DatabaseReference liveStream;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_stream);

        firebaseHelper = new FirebaseHelper();
        liveStream = firebaseHelper.Request("liveStreams");

        userID = getIntent().getStringExtra("userID");
        userName = getIntent().getStringExtra("userName");
        liveID = getIntent().getStringExtra("liveID");
        isHost = getIntent().getBooleanExtra("isHost", true);

        LiveStream liveStream = new LiveStream(this, userID, userName, liveID, isHost);
        liveStream.Stream();
    }

    @Override
    protected void onPause() {
        super.onPause();
        deleteLiveStream();
    }

    private void deleteLiveStream(){
        if(isHost) {
            liveStream.orderByChild("liveID").equalTo(liveID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        dataSnapshot.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}