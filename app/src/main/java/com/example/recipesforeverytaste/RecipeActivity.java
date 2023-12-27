package com.example.recipesforeverytaste;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.recipesforeverytaste.Adapters.ImageAdapter;

import java.util.ArrayList;

public class RecipeActivity extends AppCompatActivity {

    ArrayList<String> images;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        ImageButton recipeHeart, recipeSave, recipeShare;
        recipeHeart = findViewById(R.id.recipeHeart);
        recipeSave = findViewById(R.id.recipeSave);
        recipeShare = findViewById(R.id.recipeShare);

        RecyclerView recyclerImage = findViewById(R.id.recyclerImage);
        TextView recipeAuthor = findViewById(R.id.authorRecipe);
        TextView recipeName = findViewById(R.id.nameRecipe);
        TextView recipeNationality= findViewById(R.id.nationalityRecipe);
        TextView recipeClassification = findViewById(R.id.сlassificationRecipe);
        TextView recipeIngredients = findViewById(R.id.ingredients);
        TextView recipeDescription = findViewById(R.id.descriptionRecipe);
        TextView recipeLikes = findViewById(R.id.recipeLikes);
        TextView recipeFavorites = findViewById(R.id.recipeFavorites);
        TextView recipeShares = findViewById(R.id.recipeShares);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(RecipeActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_image);

                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setGravity(Gravity.CENTER);

                RecyclerView mRecyclerView =  dialog.findViewById(R.id.recyclerImage);
                ImageAdapter imageAdapter = new ImageAdapter(RecipeActivity.this, images, null);
                mRecyclerView.setAdapter(imageAdapter);

                dialog.show();
            }
        };
        images = new ArrayList<>();
        images = (ArrayList<String>) getIntent().getSerializableExtra("recipeImages");
        ImageAdapter imageAdapter = new ImageAdapter(this, images, clickListener);
        recyclerImage.setAdapter(imageAdapter);

        recipeAuthor.setText(getIntent().getStringExtra("recipeAuthor"));
        recipeName.setText(getIntent().getStringExtra("recipeName"));
        recipeNationality.setText(getIntent().getStringExtra("recipeNationality"));
        recipeClassification.setText(getIntent().getStringExtra("recipeСlassification"));
        recipeDescription.setText(getIntent().getStringExtra("recipeDecsription"));
        recipeLikes.setText(getIntent().getStringExtra("recipeLikes"));
        recipeFavorites.setText(getIntent().getStringExtra("recipeFavorites"));
        recipeShares.setText(getIntent().getStringExtra("recipeShares"));

        ArrayList<String> ingredientList = getIntent().getStringArrayListExtra("recipeIngredients");
        String ingredients = "Ингредиенты:\n";
        for (String i : ingredientList){
            ingredients += i + "\n";
        }
        recipeIngredients.setText(ingredients);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId() == recipeShare.getId()){
                    String imagesShare = "";
                    for(String img : images){
                        imagesShare += img + "\n\n";
                    }

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,  imagesShare + "\n"
                            + recipeAuthor.getText().toString() + "\n"
                            + recipeName.getText().toString() + "\n"
                            + recipeNationality.getText().toString() + " "
                            + recipeClassification.getText().toString() + "\n"
                            + recipeIngredients.getText().toString() + "\n"
                            + recipeDescription.getText().toString());
                    sendIntent.setType("text/*");

                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                    startActivity(shareIntent);
                }
            }
        };
        recyclerImage.setOnClickListener(onClickListener);
        recipeShare.setOnClickListener(onClickListener);
    }
}