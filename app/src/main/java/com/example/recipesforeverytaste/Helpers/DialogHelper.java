package com.example.recipesforeverytaste.Helpers;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.example.recipesforeverytaste.Adapters.IngredientAdapter;
import com.example.recipesforeverytaste.LiveStreamActivity;
import com.example.recipesforeverytaste.Models.Ingredient;
import com.example.recipesforeverytaste.Models.LiveStream;
import com.example.recipesforeverytaste.Models.Recipe;
import com.example.recipesforeverytaste.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DialogHelper {
    public static final String APP_PREFERENCES = "mysettings";
    private SharedPreferences mSettings;
    private Context context;
    private Dialog dialog;
    //private ImageView recipeImg;
    private ArrayList<Bitmap> bitmaps;

    private Uri recipeVideo;

    private AnimatedVectorDrawableCompat animatedVectorDrawableCompat;
    private AnimatedVectorDrawable animatedVectorDrawable;
    private ActivityResultLauncher<String> getImages, getVideo;


    //Обычные диалоговые окна
    public DialogHelper(Context context) {
        this.dialog = new Dialog(context);
    }
    private void dialogCreate(int layout){
        dialog.setContentView(layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
    }
    private void dialogAnimationStart(ImageView circle, ImageView item){

        Drawable drawableCircle = circle.getDrawable();
        Drawable drawableDone = item.getDrawable();

        if(drawableCircle instanceof AnimatedVectorDrawableCompat){
            animatedVectorDrawableCompat = (AnimatedVectorDrawableCompat) drawableCircle;
            animatedVectorDrawableCompat.start();
        } else if (drawableCircle instanceof  AnimatedVectorDrawable) {
            animatedVectorDrawable = (AnimatedVectorDrawable) drawableCircle;
            animatedVectorDrawable.start();
        }

        if(drawableDone instanceof AnimatedVectorDrawableCompat){
            animatedVectorDrawableCompat = (AnimatedVectorDrawableCompat) drawableDone;
            animatedVectorDrawableCompat.start();
        } else if (drawableDone instanceof  AnimatedVectorDrawable) {
            animatedVectorDrawable = (AnimatedVectorDrawable) drawableDone;
            animatedVectorDrawable.start();
        }
    }
    public void showDialogProgressBar(){
        dialogCreate(R.layout.dialog_progress_registration);

        ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#FF9234"), PorterDuff.Mode.SRC_IN);
        progressBar.setVisibility(View.VISIBLE);

        dialog.show();
    }
    public void showDialogDoneRegistr(){
        dialogCreate(R.layout.dialog_done_registration);

        ImageView circle = dialog.findViewById(R.id.animationCircle);
        ImageView done = dialog.findViewById(R.id.animationDone);

        dialogAnimationStart(circle, done);

        dialog.show();
    }
    public void showDialogUndoneRegistr(){
        dialogCreate(R.layout.dialog_undone_registration);

        ImageView circle = dialog.findViewById(R.id.animationCircle);
        ImageView undone = dialog.findViewById(R.id.animationUndone);

        dialogAnimationStart(circle, undone);

        dialog.show();
    }


    //Диалоговые окна для дабовления рецепта
    public DialogHelper(Context context, ActivityResultLauncher<String> getImages, ActivityResultLauncher<String> getVideo) {
        this.dialog = new Dialog(context);
        this.context = context;
        this.getImages = getImages;
        this.getVideo = getVideo;
    }
    public void setRecipeImg(ArrayList<Bitmap> images) {
        bitmaps = new ArrayList<>();
        bitmaps.addAll(images);
    }
    public void showDialogAddRecipe(){
        dialog.setContentView(R.layout.dialog_add_recipe_start);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogRecipeAnimation;

        TextView imgRecipeDesc = dialog.findViewById(R.id.imgRecipeDesc);

        ImageView recipeImg = dialog.findViewById(R.id.imgRecipe);

        recipeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Открытие галлереи. Результат выбора принимается в MainActivity.
                getImages.launch("image/*");
            }
        });

        TextInputEditText recipeName = dialog.findViewById(R.id.nameRecipe);
        recipeName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                TextInputLayout parent = (TextInputLayout) dialog.findViewById(recipeName.getId()).getParent().getParent();

                Pattern pattern = Pattern.compile("[А-яA-z]+");
                Matcher matcher = pattern.matcher(recipeName.getText().toString().trim());

                if(matcher.find() || TextUtils.isEmpty(recipeName.getText().toString())){
                    parent.setErrorEnabled(false);
                }
                else{
                    parent.setErrorEnabled(true);
                    parent.setError("Некорректное название блюда");
                }
            }
        });


        Spinner recipeNationality = dialog.findViewById(R.id.spinnerNationalityRecipe);
        ArrayAdapter<CharSequence> adapterNationality = ArrayAdapter.createFromResource(
                context,
                R.array.nationality_array,
                android.R.layout.simple_spinner_item
        );
        adapterNationality.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recipeNationality.setAdapter(adapterNationality);

        Spinner recipeClassification = dialog.findViewById(R.id.spinnerClassificationRecipe);
        ArrayAdapter<CharSequence> adapterClassification = ArrayAdapter.createFromResource(
                context,
                R.array.classification_array,
                android.R.layout.simple_spinner_item
        );
        adapterClassification.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recipeClassification.setAdapter(adapterClassification);


        Button btnExit = dialog.findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDismiss();
            }
        });

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        //Создание ссылки на рецепты пользователя
        DatabaseReference userRecipe = db.getReference("users/" + auth.getUid());
        //Создание ссылки на все рецепты
        DatabaseReference recipes = db.getReference("recipes");

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        Button btnNextDialog = dialog.findViewById(R.id.btnNext);
        btnNextDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDismiss();

                dialog.setContentView(R.layout.dialog_add_recipe_final);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogRecipeAnimation;

                ArrayList<Ingredient> ingredients = new ArrayList<>();

                LinearLayoutManager layoutManager =
                        new LinearLayoutManager(context, RecyclerView.VERTICAL, true);

                RecyclerView ingredientsRecycler = dialog.findViewById(R.id.ingredientsRecycler);
                ingredientsRecycler.setLayoutManager(layoutManager);

                TextInputEditText descriptionRecipe = dialog.findViewById(R.id.descriptionRecipe);
                TextInputEditText ingredientRecipe = dialog.findViewById(R.id.ingredientRecipe);
                ingredientRecipe.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                        String ingredient = ingredientRecipe.getText().toString().trim();
                        if(!TextUtils.isEmpty(ingredient)) {
                            ingredients.add(new Ingredient("- "+ ingredient + ";"));
                            ingredientRecipe.setText("");
                            IngredientAdapter ingredientAdapter = new IngredientAdapter(context, ingredients);
                            ingredientsRecycler.setAdapter(ingredientAdapter);
                        }
                        return true;
                    }
                });

                Button btnBack = dialog.findViewById(R.id.btnBack);
                btnBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogDismiss();
                        showDialogAddRecipe();
                    }
                });

                ProgressBar progressBarAdd = dialog.findViewById(R.id.prgressBarAdd);
                LinearLayout layoutRecipe = dialog.findViewById(R.id.layoutRecipe);

                Button btnAddRecipe = dialog.findViewById(R.id.btnAdd);
                btnAddRecipe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!TextUtils.isEmpty(recipeName.getText().toString())
                                && !TextUtils.isEmpty(descriptionRecipe.getText().toString()) && ingredients.size() > 0){

                            dialog.setCancelable(false);
                            dialog.setCanceledOnTouchOutside(false);

                            ingredientRecipe.setFocusable(false);
                            descriptionRecipe.setFocusable(false);
                            descriptionRecipe.setFocusableInTouchMode(false);
                            btnBack.setClickable(false);
                            btnAddRecipe.setClickable(false);

                            layoutRecipe.animate().alpha(0f).setDuration(300).start();
                            progressBarAdd.getIndeterminateDrawable().setColorFilter(Color.parseColor("#FF9234"), PorterDuff.Mode.SRC_IN);
                            progressBarAdd.animate().alpha(1f).setDuration(500).start();

                            mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                            String recipeAuthor = mSettings.getString("userName", "");

                            ArrayList<String> ingredientsFinal = new ArrayList<>();
                            for(Ingredient i : ingredients){
                                ingredientsFinal.add(i.getIngredientName());
                            }

                            ArrayList<String> imagesName = new ArrayList<>();
                            for(Bitmap bitmap : bitmaps){
                                imagesName.add(String.valueOf(bitmap.hashCode()));
                            }
                            //Создание объекта "Рецепт"
                            Recipe recipe = new Recipe();
                            recipe.setNameRecipe(recipeName.getText().toString());
                            recipe.setNationalityRecipe(recipeNationality.getSelectedItem().toString());
                            recipe.setСlassificationRecipe(recipeClassification.getSelectedItem().toString());
                            recipe.setImages(imagesName);
                            recipe.setAuthor(recipeAuthor);
                            recipe.setIngredients(ingredientsFinal);
                            recipe.setDescriptionRecipe(descriptionRecipe.getText().toString().trim());
                            recipe.setLikes(0);
                            recipe.setFavorites(0);
                            recipe.setShares(0);

                            //Создание ссылки на загрузку в общую базу
                            DatabaseReference recipesKey = recipes.push();
                            //Загрузка ссылки на рецепт в объект пользователя
                            recipe.setIdRecipe(recipesKey.getKey());
                            userRecipe.child("userRecipes").push().setValue(recipesKey.getKey());
                            //Загрузка данных о рецепте в общую базу
                            recipesKey.setValue(recipe);

                            for(int i = 0; i < bitmaps.size(); i++){
                                //Создание ссылки на все фотографии рецептов
                                StorageReference images = storageReference.child("recipeImages/" + bitmaps.get(i).hashCode());

                                //Обработка изображения для последующей загрузки в БД
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bitmaps.get(i).compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] data = baos.toByteArray();

                                //Загрузка фотографии рецепта в общую базу
                                UploadTask uploadTaskimages = images.putBytes(data);
                                int finalI = i;
                                uploadTaskimages.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if(task.isComplete()){
                                            if(finalI == bitmaps.size() - 1){
                                                uploadTaskimages.cancel();
                                                progressBarAdd.animate().alpha(0f).setDuration(500).start();
                                                dialogDismiss();
                                            }
                                        }
                                    }
                                });
                            }
                        } else {
                            ingredientRecipe.setText("AAAAAAAAAAAA");
                        }
                    }
                });

                dialog.show();
            }
        });

        dialog.show();
    }

    //Диалоговое окно для дабовления видео рецепта
    public void setRecipeVideo(Uri video) {
        this.recipeVideo = video;
    }
    public Uri getRecipeVideo() {
        return this.recipeVideo;
    }
    public void showDialogAddRecipeVideo(){
        dialog.setContentView(R.layout.dialog_add_recipe_video);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogRecipeAnimation;

        TextView videoRecipeDesc = dialog.findViewById(R.id.videoRecipeDesc);

        ImageView recipeImg = dialog.findViewById(R.id.videoRecipe);
        recipeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Открытие галлереи. Результат выбора принимается в MainActivity.
                getVideo.launch("video/*");
            }
        });

        TextInputEditText recipeName = dialog.findViewById(R.id.nameRecipe);
        recipeName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                TextInputLayout parent = (TextInputLayout) dialog.findViewById(recipeName.getId()).getParent().getParent();

                Pattern pattern = Pattern.compile("[А-яA-z]+");
                Matcher matcher = pattern.matcher(recipeName.getText().toString().trim());

                if(matcher.find() || TextUtils.isEmpty(recipeName.getText().toString())){
                    parent.setErrorEnabled(false);
                }
                else{
                    parent.setErrorEnabled(true);
                    parent.setError("Некорректное название блюда");
                }
            }
        });


        Spinner recipeNationality = dialog.findViewById(R.id.spinnerNationalityRecipe);
        ArrayAdapter<CharSequence> adapterNationality = ArrayAdapter.createFromResource(
                context,
                R.array.nationality_array,
                android.R.layout.simple_spinner_item
        );
        adapterNationality.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recipeNationality.setAdapter(adapterNationality);

        Spinner recipeClassification = dialog.findViewById(R.id.spinnerClassificationRecipe);
        ArrayAdapter<CharSequence> adapterClassification = ArrayAdapter.createFromResource(
                context,
                R.array.classification_array,
                android.R.layout.simple_spinner_item
        );
        adapterClassification.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recipeClassification.setAdapter(adapterClassification);


        Button btnExit = dialog.findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDismiss();
            }
        });

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        //Создание ссылки на рецепты пользователя
        DatabaseReference userRecipe = db.getReference("users/" + auth.getUid());
        //Создание ссылки на все рецепты
        DatabaseReference recipes = db.getReference("videos");

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        Button btnAdd = dialog.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(recipeName.getText().toString())) {

                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);

                    recipeName.setFocusable(false);
                    recipeName.setFocusableInTouchMode(false);
                    btnExit.setClickable(false);
                    btnAdd.setClickable(false);

                    LinearLayout layoutRecipe = dialog.findViewById(R.id.linearLayout);
                    ProgressBar progressBar = dialog.findViewById(R.id.prgressBarAdd);

                    layoutRecipe.animate().alpha(0f).setDuration(300).start();
                    progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#FF9234"), PorterDuff.Mode.SRC_IN);
                    progressBar.animate().alpha(1f).setDuration(500).start();

                    mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                    String recipeAuthor = mSettings.getString("userName", "");

                    //Создание объекта "Рецепт"
                    Recipe recipe = new Recipe();
                    recipe.setNameRecipe(recipeName.getText().toString());
                    recipe.setNationalityRecipe(recipeNationality.getSelectedItem().toString());
                    recipe.setСlassificationRecipe(recipeClassification.getSelectedItem().toString());
                    recipe.setVideo(String.valueOf(getRecipeVideo().hashCode()));
                    recipe.setAuthor(recipeAuthor);
                    recipe.setLikes(0);
                    recipe.setFavorites(0);
                    recipe.setShares(0);

                    //Создание ссылки на загрузку в общую базу
                    DatabaseReference recipesKey = recipes.push();
                    //Загрузка ссылки на видео рецепта в объект пользователя
                    recipe.setIdRecipe(recipesKey.getKey());
                    userRecipe.child("userVideos").push().setValue(recipesKey.getKey());
                    //Загрузка данных о видео рецепта в общую базу
                    recipesKey.setValue(recipe);

                    //Создание ссылки на все видео рецептов
                    StorageReference videos = storageReference.child("recipeVideos/" + getRecipeVideo().hashCode());

                    //Загрузка видео рецепта в общую базу
                    UploadTask uploadTaskimages = videos.putFile(getRecipeVideo());
                    uploadTaskimages.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isComplete()) {
                                progressBar.animate().alpha(0f).setDuration(500).start();
                                dialogDismiss();
                                uploadTaskimages.cancel();
                            }
                        }
                    });

                } else {
                    Toast.makeText(context, "Заполните все поля корректно!", Toast.LENGTH_SHORT);
                }
            }

        });

        dialog.show();
    }

    //Диалоговое окно для добавление прямого эфира
    public void showDialogAddLiveStream(){
        dialog.setContentView(R.layout.dialog_add_live_stream);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogRecipeAnimation;

        TextInputEditText liveStreamName = dialog.findViewById(R.id.nameLiveStream);
        liveStreamName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                TextInputLayout parent = (TextInputLayout) dialog.findViewById(liveStreamName.getId()).getParent().getParent();

                Pattern pattern = Pattern.compile("[А-яA-z]+");
                Matcher matcher = pattern.matcher(liveStreamName.getText().toString().trim());

                if(matcher.find() || TextUtils.isEmpty(liveStreamName.getText().toString())){
                    parent.setErrorEnabled(false);
                }
                else{
                    parent.setErrorEnabled(true);
                    parent.setError("Некорректное название прямого эфира");
                }
            }
        });

        Button btnExit = dialog.findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDismiss();
            }
        });

        Button btnAdd = dialog.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(liveStreamName.getText().toString())) {

                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);

                    liveStreamName.setFocusable(false);
                    liveStreamName.setFocusableInTouchMode(false);
                    btnExit.setClickable(false);
                    btnAdd.setClickable(false);

                    LinearLayout layoutRecipe = dialog.findViewById(R.id.linearLayout);
                    ProgressBar progressBar = dialog.findViewById(R.id.prgressBarAdd);

                    layoutRecipe.animate().alpha(0f).setDuration(300).start();
                    progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#FF9234"), PorterDuff.Mode.SRC_IN);
                    progressBar.animate().alpha(1f).setDuration(500).start();

                    mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                    String liveStreamAuthor = mSettings.getString("userName", "");

                    FirebaseAuth auth = FirebaseAuth.getInstance();

                    //Создание объекта "Прямой эфир"
                    LiveStream liveStream = new LiveStream();
                    liveStream.setLiveID(auth.getUid());
                    liveStream.setLiveName(liveStreamName.getText().toString());

                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference liveStreams = db.getReference("liveStreams");

                    liveStreams.push().setValue(liveStream).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isComplete()){
                                dialogDismiss();
                                Intent intenLiveStream = new Intent(context, LiveStreamActivity.class);
                                intenLiveStream.putExtra("userID", auth.getUid());
                                intenLiveStream.putExtra("userName", liveStreamAuthor);
                                intenLiveStream.putExtra("liveID", auth.getUid());
                                intenLiveStream.putExtra("isHost", true);
                                context.startActivity(intenLiveStream);
                            }
                        }
                    });

                } else {
                    Toast.makeText(context, "Заполните все поля корректно!", Toast.LENGTH_SHORT);
                }
            }

        });

        dialog.show();
    }

    public void dialogDismiss(){
        dialog.dismiss();
    }

}
