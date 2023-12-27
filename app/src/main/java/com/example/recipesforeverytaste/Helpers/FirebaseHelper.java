package com.example.recipesforeverytaste.Helpers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.recipesforeverytaste.Adapters.RecipeAdapter;
import com.example.recipesforeverytaste.Models.Recipe;
import com.example.recipesforeverytaste.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseHelper {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = db.getReference();

    public FirebaseHelper() {}

    public String getIdUser() {
        return auth.getUid();
    }

    public DatabaseReference Request(String path){
      return databaseReference.child(path);
    };


}
