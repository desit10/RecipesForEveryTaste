package com.example.recipesforeverytaste.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipesforeverytaste.Helpers.FirebaseHelper;
import com.example.recipesforeverytaste.Models.Recipe;
import com.example.recipesforeverytaste.R;
import com.example.recipesforeverytaste.RecipeActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    Context context;
    ArrayList<Recipe> recipeList;
    LinearLayoutManager layoutManager;
    FirebaseHelper firebaseHelper;
    String idUser;
    String idRecipe;
    int currentColor, colorAccent;
    public RecipeAdapter(Context context, ArrayList<Recipe> recipeList, LinearLayoutManager layoutManager) {
        this.context = context;
        this.recipeList = recipeList;
        this.layoutManager = layoutManager;
    }

    //Какой дизайн
    @NonNull
    @Override
    public RecipeAdapter.RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View recipeItem = LayoutInflater.from(context).inflate(R.layout.recipe_item, parent, false);

        return new RecipeAdapter.RecipeViewHolder(recipeItem);
    }

    //Взаимодействие с объектом и его элементами
    @Override
    public void onBindViewHolder(@NonNull RecipeAdapter.RecipeViewHolder holder, @SuppressLint("RecyclerView") int position) {

        firebaseHelper = new FirebaseHelper();
        idUser = firebaseHelper.getIdUser();

        Recipe recipe = recipeList.get(position);

        //Обработка кликов recyclerImage
        View.OnClickListener onClickRecyclerImage = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentionToGoToTheRecipePage(holder, recipe, position);
            }
        };
        ImageAdapter imageAdapter = new ImageAdapter(context, recipe.getImages(), onClickRecyclerImage);

        checkTheUserLikesRecipes(holder, recipe.getIdRecipe());
        checkTheUserFavoriteRecipes(holder, recipe.getIdRecipe());

        //Подставляем данные
        holder.recyclerImage.setAdapter(imageAdapter);
        holder.recipeName.setText(recipe.getNameRecipe());
        holder.recipeNationality.setText(recipe.getNationalityRecipe());
        holder.recipeClassification.setText(recipe.getСlassificationRecipe());
        holder.recipeAuthor.setText(recipe.getAuthor());
        holder.recipeLikes.setText(String.valueOf(recipe.getLikes()));
        holder.recipeFavorites.setText(String.valueOf(recipe.getFavorites()));
        holder.recipeShares.setText(String.valueOf(recipe.getShares()));

        //Обработка кликов recyclerRecipe
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idRecipe = recipe.getIdRecipe();
                idUser = firebaseHelper.getIdUser();

                if(view.getId() == holder.itemView.getId()){
                    holder.itemView.setClickable(false);
                    intentionToGoToTheRecipePage(holder, recipe, position);
                }

                if(view.getId() == holder.recipeHeart.getId()){
                    currentColor = ImageViewCompat.getImageTintList(holder.recipeHeart).getDefaultColor();
                    colorAccent = context.getResources().getColor(R.color.red);

                    DatabaseReference recipeLikes = firebaseHelper.Request("recipes/" + idRecipe + "/likes");
                    DatabaseReference userLikesRecipe = firebaseHelper.Request("users/" + idUser + "/likesRecipe/");
                    recipeLikes.runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                            Integer currentValue = currentData.getValue(Integer.class);
                            if (currentValue == null) {
                                currentData.setValue(1);
                            } else {
                                if (currentColor == colorAccent) {
                                    currentData.setValue(currentValue - 1);
                                } else {
                                    currentData.setValue(currentValue + 1);
                                }
                            }

                            return Transaction.success(currentData);
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                            if (currentColor == colorAccent) {
                                userLikesRecipe.orderByValue().equalTo(idRecipe).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Log.e("data", snapshot.toString());
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            dataSnapshot.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                ImageViewCompat.setImageTintList(holder.recipeHeart, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black)));
                                holder.recipeLikes.setText(currentData.getValue().toString());
                            } else {
                                userLikesRecipe.push().setValue(idRecipe);
                                ImageViewCompat.setImageTintList(holder.recipeHeart, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red)));
                                holder.recipeLikes.setText(currentData.getValue().toString());
                            }

                        }
                    });
                }

                if(view.getId() == holder.recipeSave.getId()){
                    currentColor = ImageViewCompat.getImageTintList(holder.recipeSave).getDefaultColor();
                    colorAccent = context.getResources().getColor(R.color.orange);

                    DatabaseReference recipeFavorites = firebaseHelper.Request("recipes/" + idRecipe + "/favorites");
                    DatabaseReference userFavoritesRecipe = firebaseHelper.Request("users/" + idUser + "/favoritesRecipe/");
                    recipeFavorites.runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                            Integer currentValue = currentData.getValue(Integer.class);
                            if (currentValue == null) {
                                currentData.setValue(1);
                            } else {
                                if (currentColor == colorAccent) {
                                    currentData.setValue(currentValue - 1);
                                } else {
                                    currentData.setValue(currentValue + 1);
                                }
                            }

                            return Transaction.success(currentData);
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                            if (currentColor == colorAccent) {
                                userFavoritesRecipe.orderByValue().equalTo(idRecipe).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                ImageViewCompat.setImageTintList(holder.recipeSave, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black)));
                                holder.recipeFavorites.setText(currentData.getValue().toString());
                            } else {
                                ImageViewCompat.setImageTintList(holder.recipeSave, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.orange)));
                                holder.recipeFavorites.setText(currentData.getValue().toString());
                                userFavoritesRecipe.push().setValue(idRecipe);
                            }
                        }
                    });
                }

                if(view.getId() == holder.recipeShare.getId()){
                    intenShare();
                    DatabaseReference recipeShare = firebaseHelper.Request("recipes/" + idRecipe + "/shares");
                    recipeShare.runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                            Integer currentValue = currentData.getValue(Integer.class);
                            if (currentValue == null) {
                                currentData.setValue(1);
                            } else {
                                currentData.setValue(currentValue + 1);
                            }

                            return Transaction.success(currentData);
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                            holder.recipeShares.setText(currentData.getValue().toString());
                        }
                    });

                }

            }
            private void intenShare(){
                String images = "";
                for (String i : recipe.getImages()){
                    images += i + "\n";
                }

                String ingredients = "Ингредиенты:\n";
                for (String i : recipe.getIngredients()){
                    ingredients += i + "\n";
                }

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, images + "\n"
                        + recipe.getAuthor() + "\n"
                        + recipe.getNameRecipe() + "\n"
                        + recipe.getNationalityRecipe() + " "
                        + recipe.getСlassificationRecipe() + "\n"
                        + ingredients + "\n"
                        + recipe.getDescriptionRecipe());
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                context.startActivity(shareIntent);
            }
        };
        holder.itemView.setOnClickListener(onClickListener);
        holder.recipeHeart.setOnClickListener(onClickListener);
        holder.recipeSave.setOnClickListener(onClickListener);
        holder.recipeShare.setOnClickListener(onClickListener);


    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    private void intentionToGoToTheRecipePage(RecipeViewHolder holder, Recipe recipe, int position) {
        layoutManager.scrollToPositionWithOffset(position, 1);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, RecipeActivity.class);

                intent.putStringArrayListExtra("recipeImages", recipe.getImages());
                intent.putStringArrayListExtra("recipeIngredients", recipe.getIngredients());
                intent.putExtra("recipeAuthor", recipe.getAuthor());
                intent.putExtra("recipeName", recipe.getNameRecipe());
                intent.putExtra("recipeNationality", recipe.getNationalityRecipe());
                intent.putExtra("recipeСlassification", recipe.getСlassificationRecipe());
                intent.putExtra("recipeDecsription", recipe.getDescriptionRecipe());
                intent.putExtra("recipeLikes", String.valueOf(recipe.getLikes()));
                intent.putExtra("recipeFavorites", String.valueOf(recipe.getFavorites()));
                intent.putExtra("recipeShares", String.valueOf(recipe.getShares()));


                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                        (Activity) context,
                        new Pair<View, String>(holder.recyclerImage, "recyclerImage"),
                        new Pair<View, String>(holder.layoutRecipeAuthor, "layoutRecipeAuthor"),
                        new Pair<View, String>(holder.recipeName, "recipeName"),
                        new Pair<View, String>(holder.layoutRecipeInfo, "layoutRecipeInfo"),
                        new Pair<View, String>(holder.recipeSave, "recipeSave"),
                        new Pair<View, String>(holder.recipeHeart, "recipeHeart"),
                        new Pair<View, String>(holder.recipeShare, "recipeShare"),
                        new Pair<View, String>(holder.recipeLikes, "recipeLikes"),
                        new Pair<View, String>(holder.recipeFavorites, "recipeFavorites"),
                        new Pair<View, String>(holder.recipeShares, "recipeShares")
                        );

                context.startActivity(intent, options.toBundle());
                holder.itemView.setClickable(true);
            }
        }, 1);
    }
    private void checkTheUserLikesRecipes(RecipeViewHolder holder, String idRecipe){
        firebaseHelper.Request("users/" + idUser + "/likesRecipe/").orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if (dataSnapshot.getValue().toString().equals(idRecipe)){
                        ImageViewCompat.setImageTintList(holder.recipeHeart, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red)));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void checkTheUserFavoriteRecipes(RecipeViewHolder holder, String idRecipe){
        firebaseHelper.Request("users/" + idUser + "/favoritesRecipe/").orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if (dataSnapshot.getValue().equals(idRecipe)){
                        ImageViewCompat.setImageTintList(holder.recipeSave, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.orange)));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Элементы с объекта с которыми работаем
    public static class RecipeViewHolder extends RecyclerView.ViewHolder{
        LinearLayout recipeLayout;
        RecyclerView recyclerImage;
        FrameLayout layoutRecipeAuthor;
        HorizontalScrollView layoutRecipeInfo;
        ImageButton recipeSave, recipeHeart, recipeShare;
        TextView recipeAuthor, recipeName, recipeNationality, recipeClassification, recipeLikes, recipeFavorites, recipeShares;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);

            recipeLayout = itemView.findViewById(R.id.recipeLayout);

            recyclerImage = itemView.findViewById(R.id.recyclerImage);

            layoutRecipeInfo = itemView.findViewById(R.id.layoutRecipeInfo);

            layoutRecipeAuthor = itemView.findViewById(R.id.layoutRecipeAuthor);

            recipeHeart = itemView.findViewById(R.id.recipeHeart);
            recipeSave = itemView.findViewById(R.id.recipeSave);
            recipeShare = itemView.findViewById(R.id.recipeShare);

            recipeAuthor = itemView.findViewById(R.id.authorRecipe);
            recipeName = itemView.findViewById(R.id.nameRecipe);
            recipeNationality = itemView.findViewById(R.id.nationalityRecipe);
            recipeClassification = itemView.findViewById(R.id.сlassificationRecipe);

            recipeLikes = itemView.findViewById(R.id.recipeLikes);
            recipeFavorites = itemView.findViewById(R.id.recipeFavorites);
            recipeShares = itemView.findViewById(R.id.recipeShares);
        }
    }
}
