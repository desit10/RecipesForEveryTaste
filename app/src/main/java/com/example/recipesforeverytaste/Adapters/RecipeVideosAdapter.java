package com.example.recipesforeverytaste.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipesforeverytaste.Helpers.FirebaseHelper;
import com.example.recipesforeverytaste.Models.Recipe;
import com.example.recipesforeverytaste.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

@UnstableApi public class RecipeVideosAdapter extends RecyclerView.Adapter<RecipeVideosAdapter.RecipeVideoViewHolder> {
    Context context;
    ArrayList<Recipe> recipeList;
    LinearLayoutManager layoutManager;
    RecipeVideosAdapter.RecipeVideoViewHolder lastHolder;
    FirebaseHelper firebaseHelper;
    String idUser;
    String idRecipe;
    int currentColor, colorAccent;

    public RecipeVideosAdapter(Context context, ArrayList<Recipe> recipeList, LinearLayoutManager layoutManager) {
        this.context = context;
        this.recipeList = recipeList;
        this.layoutManager = layoutManager;
    }


    //Какой дизайн
    @NonNull
    @Override
    public RecipeVideosAdapter.RecipeVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View recipeItem = LayoutInflater.from(context).inflate(R.layout.recipe_video_item, parent, false);

        return new RecipeVideosAdapter.RecipeVideoViewHolder(recipeItem);
    }

    //Взаимодействие с объектом и его элементами
    @Override
    public void onBindViewHolder(@NonNull RecipeVideosAdapter.RecipeVideoViewHolder holder, @SuppressLint("RecyclerView") int position) {

        firebaseHelper = new FirebaseHelper();
        idUser = firebaseHelper.getIdUser();

        Recipe recipe = recipeList.get(position);

        checkTheUserFavoriteVideos(holder, recipe.getIdRecipe());
        checkTheUserLikesVideos(holder, recipe.getIdRecipe());

        ExoPlayer player = new ExoPlayer.Builder(context)
                .setSeekForwardIncrementMs(5000)
                .build();

        if(recipe.getVideo() != null){
            player.setMediaItem(MediaItem.fromUri(Uri.parse(recipe.getVideo())));
            player.setRepeatMode(Player.REPEAT_MODE_ALL);
        }

        //Подставляем данные
        holder.recipeVideo.setPlayer(player);
        holder.recipeName.setText(recipe.getNameRecipe());
        holder.recipeNationality.setText(recipe.getNationalityRecipe());
        holder.recipeClassification.setText(recipe.getСlassificationRecipe());
        holder.recipeAuthor.setText(recipe.getAuthor());
        holder.recipeLikes.setText(String.valueOf(recipe.getLikes()));
        holder.recipeFavorites.setText(String.valueOf(recipe.getFavorites()));
        holder.recipeShares.setText(String.valueOf(recipe.getShares()));

        //Обработка кликов
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idRecipe = recipe.getIdRecipe();
                idUser = firebaseHelper.getIdUser();

                if(view.getId() == holder.videoHeart.getId()){
                    currentColor = ImageViewCompat.getImageTintList(holder.videoHeart).getDefaultColor();
                    colorAccent = context.getResources().getColor(R.color.red);

                    DatabaseReference recipeLikes = firebaseHelper.Request("videos/" + idRecipe + "/likes");
                    DatabaseReference userLikesRecipe = firebaseHelper.Request("users/" + idUser + "/likesVideo/");
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
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            dataSnapshot.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                ImageViewCompat.setImageTintList(holder.videoHeart, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white)));
                                holder.recipeLikes.setText(currentData.getValue().toString());
                            } else {
                                userLikesRecipe.push().setValue(idRecipe);
                                ImageViewCompat.setImageTintList(holder.videoHeart, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red)));
                                holder.recipeLikes.setText(currentData.getValue().toString());
                            }
                        }
                    });
                }
                if(view.getId() == holder.videoSave.getId()){
                    currentColor = ImageViewCompat.getImageTintList(holder.videoSave).getDefaultColor();
                    colorAccent = context.getResources().getColor(R.color.orange);

                    DatabaseReference recipeFavorites = firebaseHelper.Request("videos/" + idRecipe + "/favorites");
                    DatabaseReference userFavoritesRecipe = firebaseHelper.Request("users/" + idUser + "/favoritesVideo/");
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
                                ImageViewCompat.setImageTintList(holder.videoSave, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white)));
                                holder.recipeFavorites.setText(currentData.getValue().toString());
                            } else {
                                userFavoritesRecipe.push().setValue(idRecipe);
                                ImageViewCompat.setImageTintList(holder.videoSave, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.orange)));
                                holder.recipeFavorites.setText(currentData.getValue().toString());
                            }
                        }
                    });

                }
                if(view.getId() ==  holder.videoShare.getId()){
                    intenShare();
                    DatabaseReference recipeShare = firebaseHelper.Request("videos/" + idRecipe + "/shares");
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
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, recipe.getVideo() + "\n"
                        + recipe.getAuthor() + "\n"
                        + recipe.getNameRecipe() + "\n"
                        + recipe.getNationalityRecipe() + " "
                        + recipe.getСlassificationRecipe());
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                context.startActivity(shareIntent);
            }

        };
        holder.videoHeart.setOnClickListener(onClickListener);
        holder.videoSave.setOnClickListener(onClickListener);
        holder.videoShare.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecipeVideoViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        lastHolder = holder;
        holder.recipeVideo.getPlayer().prepare();
        holder.recipeVideo.getPlayer().play();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                holder.recipeVideo.hideController();
            }
        }, 1000);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecipeVideoViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.recipeVideo.getPlayer().stop();
    }

    private void checkTheUserLikesVideos(RecipeVideosAdapter.RecipeVideoViewHolder holder, String idRecipe){
        firebaseHelper.Request("users/" + idUser + "/likesVideo/").orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if (dataSnapshot.getValue().toString().equals(idRecipe)){
                        ImageViewCompat.setImageTintList(holder.videoHeart, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red)));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkTheUserFavoriteVideos(RecipeVideosAdapter.RecipeVideoViewHolder holder, String idRecipe){
        firebaseHelper.Request("users/" + idUser + "/favoritesVideo/").orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if (dataSnapshot.getValue().toString().equals(idRecipe)){
                        ImageViewCompat.setImageTintList(holder.videoSave, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.orange)));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Элементы с объекта с которыми работаем
    public static final class RecipeVideoViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout recipeVideoLayout;
        PlayerView recipeVideo;
        TextView recipeAuthor, recipeName, recipeNationality, recipeClassification, recipeLikes, recipeFavorites, recipeShares;
        ImageButton videoHeart, videoSave, videoShare;

        public RecipeVideoViewHolder(@NonNull View itemView) {
            super(itemView);

            recipeVideoLayout = itemView.findViewById(R.id.recipeVideoLayout);

            recipeVideo = itemView.findViewById(R.id.videoRecipe);
            recipeVideo.setControllerShowTimeoutMs(1000);

            recipeAuthor = itemView.findViewById(R.id.authorRecipe);
            recipeName = itemView.findViewById(R.id.nameRecipe);
            recipeNationality = itemView.findViewById(R.id.nationalityRecipe);
            recipeClassification = itemView.findViewById(R.id.сlassificationRecipe);

            videoHeart = itemView.findViewById(R.id.recipeHeart);
            videoSave = itemView.findViewById(R.id.recipeSave);
            videoShare = itemView.findViewById(R.id.recipeShare);

            recipeLikes = itemView.findViewById(R.id.recipeLikes);
            recipeFavorites = itemView.findViewById(R.id.recipeFavorites);
            recipeShares = itemView.findViewById(R.id.recipeShares);
        }
    }
}
