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

import com.example.recipesforeverytaste.Adapters.LiveStreamAdapter;
import com.example.recipesforeverytaste.Adapters.RecipeAdapter;
import com.example.recipesforeverytaste.Adapters.RecipeVideosAdapter;
import com.example.recipesforeverytaste.Helpers.SnapHelperOneByOne;
import com.example.recipesforeverytaste.MainActivity;
import com.example.recipesforeverytaste.Models.LiveStream;
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

@UnstableApi public class NewsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewsFragment newInstance(String param1, String param2) {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setExitTransition(inflater.inflateTransition(R.transition.slide_right));
    }

    private ArrayList<Recipe> recipes;
    private ArrayList<LiveStream> liveStreams;
    private ProgressBar progressBarNews;
    private RecyclerView dataRecycler;
    private LinearLayoutManager layoutManager;
    private RecipeAdapter recipeAdapter;
    private RecipeVideosAdapter recipeVideosAdapter;
    private LiveStreamAdapter liveStreamAdapter;
    private LinearSnapHelper linearSnapHelper;
    private FrameLayout recipeMenuItem, videoMenuItem, liveMenuItem;
    private ImageView recipeImg, videoImg, liveImg;
    private TextView errorText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);


        linearSnapHelper = new SnapHelperOneByOne();
        dataRecycler = view.findViewById(R.id.recipeRecycler);
        layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);

        errorText = view.findViewById(R.id.errorText);

        recipeImg = view.findViewById(R.id.recipeImg);
        videoImg = view.findViewById(R.id.videoImg);
        liveImg = view.findViewById(R.id.liveImg);

        recipeMenuItem = view.findViewById(R.id.recipes);
        animationOfTheAppearanceOfAnAdditionalBottomMenuWithShaking(view, recipeMenuItem, 800);

        videoMenuItem = view.findViewById(R.id.videos);
        animationOfTheAppearanceOfAnAdditionalBottomMenuWithShaking(view, videoMenuItem, 600);

        liveMenuItem = view.findViewById(R.id.lives);
        animationOfTheAppearanceOfAnAdditionalBottomMenuWithShaking(view, liveMenuItem, 400);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == recipeMenuItem.getId()){
                    selectAdditionalBottomMenuItem("#FF9234", "#FFFFFF", "#FFFFFF");
                    //enablingDisablingAdditionalMenuItems(false);
                    setRecipeRecycler(view);
                }
                if(v.getId() == videoMenuItem.getId()){
                    selectAdditionalBottomMenuItem("#FFFFFF", "#FF9234", "#FFFFFF");
                    //enablingDisablingAdditionalMenuItems(false);
                    setRecipeVideoRecycler(view);
                }
                if(v.getId() == liveMenuItem.getId()){
                    selectAdditionalBottomMenuItem("#FFFFFF", "#FFFFFF", "#FF9234");
                    //enablingDisablingAdditionalMenuItems(false);
                    setLiveStreamRecycler(view);
                }
            }
        };
        recipeMenuItem.setOnClickListener(onClickListener);
        videoMenuItem.setOnClickListener(onClickListener);
        liveMenuItem.setOnClickListener(onClickListener);

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
    private void enablingDisablingAdditionalMenuItems(boolean state) {
        recipeMenuItem.setClickable(state);
        videoMenuItem.setClickable(state);
        liveMenuItem.setClickable(state);
    }
    private void selectAdditionalBottomMenuItem(String recipeColor, String videoColor, String liveColor){
        recipeImg.getDrawable().setTint(Color.parseColor(recipeColor));
        videoImg.getDrawable().setTint(Color.parseColor(videoColor));
        liveImg.getDrawable().setTint(Color.parseColor(liveColor));
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
        recipeAdapter = new RecipeAdapter(getContext(), NewsFragment.this.recipes, layoutManager);

        linearSnapHelper.attachToRecyclerView(null);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dataRecycler.setAdapter(recipeAdapter);
                dataRecycler.setTranslationX(-1000f);
                dataRecycler.animate().translationXBy(1000f).setDuration(500).start();
            }
        }, 50);

    }
    private void downloadingRecipesFromFirebase(DatabaseReference recipes, LinearLayoutManager layoutManager){
        recipes.orderByKey().limitToFirst(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Прекращение загрузки при отсутсвии данных
                if(dataSnapshot.getValue() == null){
                    removeProgressBar();
                    showRecipes(layoutManager);
                    //enablingDisablingAdditionalMenuItems(true);
                    return;
                }

                //Перебор данных
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //Создание объекта "Рецепт" и передаём в него данные
                    Recipe recipe = snapshot.getValue(Recipe.class);

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
                                    NewsFragment.this.recipes.add(recipe);
                                }
                                if(dataSnapshot.getChildrenCount() == NewsFragment.this.recipes.size()){
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
                    NewsFragment.this.recipes.add(recipe);
                    if(dataSnapshot.getChildrenCount() == NewsFragment.this.recipes.size()){
                        removeProgressBar();
                        showRecipes(layoutManager);
                        //enablingDisablingAdditionalMenuItems(true);
                    }*/
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
        dataRecycler.setLayoutManager(layoutManager);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference recipes = db.getReference("recipes");
        downloadingRecipesFromFirebase(recipes, layoutManager);

        dataRecycler.animate().translationXBy(2000f).setDuration(500).start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dataRecycler.setClipToPadding(false);
                dataRecycler.setPadding(0,0,0, (int) getResources().getDimension(R.dimen.get_padding_bottom));
            }
        }, 300);

    }

    //Обработка видео
    private void showRecipeVideos(LinearLayoutManager layoutManager){
        recipeVideosAdapter = new RecipeVideosAdapter(getContext(), NewsFragment.this.recipes, layoutManager);

        linearSnapHelper.attachToRecyclerView(dataRecycler);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dataRecycler.setAdapter(recipeVideosAdapter);
                dataRecycler.setTranslationX(-1000f);
                dataRecycler.animate().translationXBy(1000f).setDuration(500).start();
            }
        }, 50);

    }
    private void downloadingRecipeVideosFromFirebase(DatabaseReference recipeVideos, LinearLayoutManager layoutManager){
        recipeVideos.orderByKey().limitToFirst(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Прекращение загрузки при отсутсвии данных
                if(dataSnapshot.getValue() == null){
                    removeProgressBar();
                    showRecipeVideos(layoutManager);
                    //enablingDisablingAdditionalMenuItems(true);
                    return;
                }

                //Перебор данных
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //Создание объекта "Рецепт" и передаём в него данные
                    Recipe recipe = snapshot.getValue(Recipe.class);

                    //Созданеии ссылки на видео рецепта
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference("recipeVideos/" + recipe.getVideo());
                    storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isComplete()){
                                recipe.setVideo(String.valueOf(task.getResult()));
                                NewsFragment.this.recipes.add(recipe);
                                if(dataSnapshot.getChildrenCount() == NewsFragment.this.recipes.size()){
                                    removeProgressBar();
                                    showRecipeVideos(layoutManager);
                                    enablingDisablingAdditionalMenuItems(true);
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
                    NewsFragment.this.recipes.add(recipe);
                    if(dataSnapshot.getChildrenCount() == NewsFragment.this.recipes.size()){
                        removeProgressBar();
                        showRecipeVideos(layoutManager);
                        //enablingDisablingAdditionalMenuItems(true);
                    }*/
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
        dataRecycler.setLayoutManager(layoutManager);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference recipeVideos = db.getReference("videos");
        downloadingRecipeVideosFromFirebase(recipeVideos, layoutManager);

        dataRecycler.animate().translationXBy(2000f).setDuration(500).start();
        dataRecycler.setClipToPadding(true);
        dataRecycler.setPadding(0,0,0,0);
    }

    //Обработка прямых эфиров
    private void showLiveStreams(LinearLayoutManager layoutManager){
        liveStreamAdapter = new LiveStreamAdapter(getContext(), NewsFragment.this.liveStreams, layoutManager);

        linearSnapHelper.attachToRecyclerView(dataRecycler);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dataRecycler.setAdapter(liveStreamAdapter);
                dataRecycler.setTranslationX(-1000f);
                dataRecycler.animate().translationXBy(1000f).setDuration(500).start();
            }
        }, 50);

    }
    private void downloadingLiveStreamsFromFirebase(DatabaseReference liveStream, LinearLayoutManager layoutManager){
        liveStream.orderByKey().limitToFirst(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Прекращение загрузки при отсутсвии данных
                if(dataSnapshot.getValue() == null){
                    removeProgressBar();
                    showLiveStreams(layoutManager);
                    //enablingDisablingAdditionalMenuItems(true);
                    return;
                }

                //Перебор данных
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //Создание объекта "Рецепт" и передаём в него данные
                    LiveStream liveStream = snapshot.getValue(LiveStream.class);
                    liveStream.setLiveName(liveStream.getLiveName());
                    liveStream.setLiveID(liveStream.getLiveID());

                    NewsFragment.this.liveStreams.add(liveStream);

                    if(dataSnapshot.getChildrenCount() == NewsFragment.this.liveStreams.size()){
                        removeProgressBar();
                        showLiveStreams(layoutManager);
                        //enablingDisablingAdditionalMenuItems(true);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setLiveStreamRecycler(View view){
        showProgressBar(view);

        liveStreams = new ArrayList<>();
        dataRecycler.setLayoutManager(layoutManager);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference liveStream = db.getReference("liveStreams");
        downloadingLiveStreamsFromFirebase(liveStream, layoutManager);

        dataRecycler.animate().translationXBy(2000f).setDuration(500).start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dataRecycler.setClipToPadding(false);
                dataRecycler.setPadding(0,0,0, (int) getResources().getDimension(R.dimen.get_padding_bottom));
            }
        }, 300);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        MainActivity mainActivity = new MainActivity();

        if (mainActivity.getStateNews()){
            dataRecycler.setAdapter(null);
        }
    }
}