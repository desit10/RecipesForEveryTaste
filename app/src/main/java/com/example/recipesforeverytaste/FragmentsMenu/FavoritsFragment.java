package com.example.recipesforeverytaste.FragmentsMenu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recipesforeverytaste.Adapters.RecipeAdapter;
import com.example.recipesforeverytaste.Adapters.RecipeVideosAdapter;
import com.example.recipesforeverytaste.Helpers.FirebaseHelper;
import com.example.recipesforeverytaste.Helpers.SnapHelperOneByOne;
import com.example.recipesforeverytaste.MainActivity;
import com.example.recipesforeverytaste.Models.Recipe;
import com.example.recipesforeverytaste.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

@UnstableApi /**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoritsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FavoritsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MedcardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoritsFragment newInstance(String param1, String param2) {
        FavoritsFragment fragment = new FavoritsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (getArguments() != null) {
        //    mParam1 = getArguments().getString(ARG_PARAM1);
        //    mParam2 = getArguments().getString(ARG_PARAM2);
        //}
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        this.setExitTransition(inflater.inflateTransition(R.transition.slide_right));
    }

    private ArrayList<Recipe> recipes;
    private ProgressBar progressBarNews;
    private RecyclerView favoritRecycler;
    private LinearLayoutManager layoutManager;
    private RecipeAdapter recipeAdapter;
    private LinearSnapHelper linearSnapHelper;
    private RecipeVideosAdapter recipeVideosAdapter;
    private FrameLayout recipeMenuItem, videoMenuItem;
    private ImageView recipeImg, videoImg;
    private TextView errorText;
    String idUser;
    FirebaseHelper firebaseHelper;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        firebaseHelper = new FirebaseHelper();
        idUser = firebaseHelper.getIdUser();

        linearSnapHelper = new SnapHelperOneByOne();
        favoritRecycler = view.findViewById(R.id.favoritRecycler);
        layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);

        errorText = view.findViewById(R.id.errorText);

        recipeImg = view.findViewById(R.id.recipeImg);
        videoImg = view.findViewById(R.id.videoImg);

        recipeMenuItem = view.findViewById(R.id.btnRecipes);
        videoMenuItem = view.findViewById(R.id.btnVideos);

        recipeMenuItem = view.findViewById(R.id.btnRecipes);
        animationOfTheAppearanceOfAnAdditionalBottomMenuWithShaking(view, recipeMenuItem, 600);

        videoMenuItem = view.findViewById(R.id.btnVideos);
        animationOfTheAppearanceOfAnAdditionalBottomMenuWithShaking(view, videoMenuItem, 400);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == recipeMenuItem.getId()){
                    selectAdditionalBottomMenuItem("#FF9234", "#FFFFFF");
                    //enablingDisablingAdditionalMenuItems(false);
                    setRecipeRecycler(view);
                }
                if(v.getId() == videoMenuItem.getId()){
                    selectAdditionalBottomMenuItem("#FFFFFF", "#FF9234");
                    //enablingDisablingAdditionalMenuItems(false);
                    setRecipeVideoRecycler(view);
                }
            }
        };
        recipeMenuItem.setOnClickListener(onClickListener);
        videoMenuItem.setOnClickListener(onClickListener);

        //Выводим список рецептов
        recipeMenuItem.callOnClick();
        return view;
    }

    private void animationOfTheAppearanceOfAnAdditionalBottomMenuWithShaking(View view, FrameLayout menuItem, int duration){
        menuItem.setTranslationX(-1000f);
        menuItem.animate().translationXBy(1000f).setDuration(duration).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                menuItem.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.shaking));
            }
        }).start();
    }
    private void selectAdditionalBottomMenuItem(String recipeColor, String videoColor){
        recipeImg.getDrawable().setTint(Color.parseColor(recipeColor));
        videoImg.getDrawable().setTint(Color.parseColor(videoColor));
    }
    private void showProgressBar(View view){
        progressBarNews = view.findViewById(R.id.progressBarNews);
        progressBarNews.getIndeterminateDrawable().setColorFilter(Color.parseColor("#FF9234"), PorterDuff.Mode.SRC_IN);
        progressBarNews.animate().alpha(1f).setDuration(1000).start();
    }
    private void removeProgressBar(){
        progressBarNews.animate().alpha(0f).setDuration(500).start();
    }

    //Обработка рецептов
    private void showRecipes(LinearLayoutManager layoutManager){
        recipeAdapter = new RecipeAdapter(getContext(), FavoritsFragment.this.recipes, layoutManager);

        linearSnapHelper.attachToRecyclerView(null);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                favoritRecycler.setAdapter(recipeAdapter);
                favoritRecycler.setTranslationX(-1000f);
                favoritRecycler.animate().translationXBy(1000f).setDuration(500).start();
            }
        }, 50);

    }
    private ArrayList<String> getIdFovoritesRecipe(DatabaseReference favoritesRecipe){
        ArrayList<String> favorites = new ArrayList<String>();
        favoritesRecipe.orderByKey().limitToFirst(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    favorites.add(dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return favorites;
    }
    private void downloadingRecipesFromFirebase(DatabaseReference recipes, ArrayList<String> favorites, LinearLayoutManager layoutManager){
        recipes.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Прекращение загрузки при отсутсвии данных
                if(favorites.size() == 0){
                    removeProgressBar();
                    showRecipes(layoutManager);
                    //enablingDisablingAdditionalMenuItems(true);
                    return;
                }

                //Перебор данных
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    //Создание объекта "Рецепт" и передаём в него данные
                    Recipe recipe = snapshot.getValue(Recipe.class);
                    for(String favorit : favorites){

                        if(recipe.getIdRecipe().equals(favorit)){
                            ArrayList<String> stringsImage = new ArrayList<>();
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference("recipeImages/");

                            for(String imageName : recipe.getImages()){
                                //Созданеии ссылки на фотографию рецепта
                                storageReference.child(imageName).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if(task.isComplete()){
                                            stringsImage.add(task.getResult().toString());
                                        }
                                        if(recipe.getImages().size() == stringsImage.size()){
                                            recipe.setImages(stringsImage);
                                            FavoritsFragment.this.recipes.add(recipe);
                                        }
                                        if(favorites.size() == FavoritsFragment.this.recipes.size()){
                                            removeProgressBar();
                                            showRecipes(layoutManager);
                                            //enablingDisablingAdditionalMenuItems(true);
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //errorText.setText("Ошибка загрузки");
                                    }
                                });
                            }

                            /*stringsImage.add(String.valueOf(R.drawable.a));
                            stringsImage.add(String.valueOf(R.drawable.b));
                            recipe.setImages(stringsImage);
                            FavoritsFragment.this.recipes.add(recipe);
                            if(favorites.size() == FavoritsFragment.this.recipes.size()){
                                removeProgressBar();
                                showRecipes(layoutManager);
                                return;
                                //enablingDisablingAdditionalMenuItems(true);
                            }*/
                        }

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setRecipeRecycler(View view){
        showProgressBar(view);

        recipes = new ArrayList<>();
        favoritRecycler.setLayoutManager(layoutManager);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference favoritesRecipe = db.getReference("users/" + idUser + "/favoritesRecipe/");
        DatabaseReference recipes = db.getReference("recipes");

        downloadingRecipesFromFirebase(recipes, getIdFovoritesRecipe(favoritesRecipe), layoutManager);

        favoritRecycler.animate().translationXBy(2000f).setDuration(500).start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                favoritRecycler.setClipToPadding(false);
                favoritRecycler.setPadding(0,0,0, (int) getResources().getDimension(R.dimen.get_padding_bottom));
            }
        }, 300);

    }

    //Обработка видео
    private void showRecipeVideos(LinearLayoutManager layoutManager){
        recipeVideosAdapter = new RecipeVideosAdapter(getContext(), FavoritsFragment.this.recipes, layoutManager);

        linearSnapHelper.attachToRecyclerView(favoritRecycler);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                favoritRecycler.setAdapter(recipeVideosAdapter);
                favoritRecycler.setTranslationX(-1000f);
                favoritRecycler.animate().translationXBy(1000f).setDuration(500).start();
            }
        }, 50);

    }
    private ArrayList<String> getIdFovoritesVideo(DatabaseReference favoritesVideo){
        ArrayList<String> favorites = new ArrayList<String>();
        favoritesVideo.orderByKey().limitToFirst(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    favorites.add(dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return favorites;
    }
    private void downloadingRecipeVideosFromFirebase(DatabaseReference recipeVideos, ArrayList<String> favorites, LinearLayoutManager layoutManager){
        recipeVideos.orderByKey().limitToFirst(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Прекращение загрузки при отсутсвии данных
                if(favorites.size() == 0){
                    removeProgressBar();
                    showRecipeVideos(layoutManager);
                    //enablingDisablingAdditionalMenuItems(true);
                    return;
                }

                //Перебор данных
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //Создание объекта "Рецепт" и передаём в него данные
                    Recipe recipe = snapshot.getValue(Recipe.class);

                    for (String favorit : favorites){

                        if(recipe.getIdRecipe().equals(favorit)){
                            //Созданеии ссылки на видео рецепта
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference("recipeVideos/" + recipe.getVideo());
                            storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if(task.isComplete()){
                                        recipe.setVideo(String.valueOf(task.getResult()));
                                        FavoritsFragment.this.recipes.add(recipe);
                                        if(favorites.size() == FavoritsFragment.this.recipes.size()){
                                            removeProgressBar();
                                            showRecipeVideos(layoutManager);
                                            //enablingDisablingAdditionalMenuItems(true);
                                        }
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //errorText.setText("Ошибка загрузки");
                                }
                            });
                            /*recipe.setVideo("android.resource://" + getResources() + "/" + R.raw.video);
                            FavoritsFragment.this.recipes.add(recipe);
                            if(dataSnapshot.getChildrenCount() == FavoritsFragment.this.recipes.size()){
                                removeProgressBar();
                                showRecipeVideos(layoutManager);
                                //enablingDisablingAdditionalMenuItems(true);
                            }*/
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setRecipeVideoRecycler(View view){
        showProgressBar(view);

        recipes = new ArrayList<>();
        favoritRecycler.setLayoutManager(layoutManager);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference favoritesVideo = db.getReference("users/" + idUser + "/favoritesVideo/");
        DatabaseReference recipeVideos = db.getReference("videos");
        downloadingRecipeVideosFromFirebase(recipeVideos,getIdFovoritesVideo(favoritesVideo), layoutManager);

        favoritRecycler.animate().translationXBy(2000f).setDuration(500).start();
        favoritRecycler.setClipToPadding(true);
        favoritRecycler.setPadding(0,0,0,0);
    }

    //Обработка прямых эфиров


    @Override
    public void onDetach() {
        super.onDetach();
        MainActivity mainActivity = new MainActivity();

        if (mainActivity.getStateNews()){
            favoritRecycler.setAdapter(null);
        }
    }
}