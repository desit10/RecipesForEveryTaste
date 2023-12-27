package com.example.recipesforeverytaste;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.recipesforeverytaste.Helpers.DialogHelper;
import com.example.recipesforeverytaste.Helpers.NetworkHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DialogHelper dialogHelper;
    private NetworkHelper networkHelper;
    private FloatingActionButton floatingActionButton;
    private BottomNavigationView bottomNavigationView;
    private NavHostFragment navHostFragment;
    private NavController navController;
    private int id = R.id.newsFragment;
    private int BackFromAppCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialogHelper = new DialogHelper(this, getImages, getVideo);
        networkHelper = new NetworkHelper(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                networkHelper.checkNetworkConnection();
            }
        }, 2000);

        //Оболочка для фрагментов
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.setSelectedItemId(R.id.newsFragment);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                BackFromAppCount = 0;
                item.setChecked(true);
                navController.popBackStack();
                navController.navigate(item.getItemId());
                return false;
            }
        });

        //Вызов меню
        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BackFromAppCount = 0;
                showBottomDialog();
            }
        });

        //Выход из приложения
        OnBackPressedCallback exitApp = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                BackFromAppCount++;

                if(BackFromAppCount == 1){
                    Toast.makeText(MainActivity.this, "Нажмите ещё раз для выхода ", Toast.LENGTH_SHORT).show();
                }

                if(BackFromAppCount == 2){
                    finishAffinity();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, exitApp);
    }

    public void showBottomDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        LinearLayout layoutRecipe = dialog.findViewById(R.id.layoutRecipe);
        LinearLayout layoutVideo = dialog.findViewById(R.id.layoutVideo);
        LinearLayout layoutLive = dialog.findViewById(R.id.layoutLive);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.layoutRecipe){
                    dialog.dismiss();
                    dialogHelper.showDialogAddRecipe();
                }
                if(v.getId() == R.id.layoutVideo){
                    dialog.dismiss();
                    dialogHelper.showDialogAddRecipeVideo();
                }
                if(v.getId() == R.id.layoutLive){
                    dialog.dismiss();
                    dialogHelper.showDialogAddLiveStream();
                }
                if(v.getId() == R.id.cancelButton){
                    dialog.dismiss();
                }
            }
        };
        layoutRecipe.setOnClickListener(onClickListener);
        layoutVideo.setOnClickListener(onClickListener);
        layoutLive.setOnClickListener(onClickListener);
        cancelButton.setOnClickListener(onClickListener);

        dialog.show();
    }

    //Обработка полученного выбора из галлереи, полученого из DialogHelper
    ActivityResultLauncher<String> getImages = registerForActivityResult(new ActivityResultContracts.GetMultipleContents(),
        new ActivityResultCallback<List<Uri>>() {
            @Override
            public void onActivityResult(List<Uri> result) {
                if(result.size() > 0){

                    ArrayList<Bitmap> bitmaps = new ArrayList<>();

                    for(Uri uri : result){
                        try {
                            bitmaps.add(MediaStore.Images.Media.getBitmap(getContentResolver(), uri));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    dialogHelper.setRecipeImg(bitmaps);
                }
            }
        });

    ActivityResultLauncher<String> getVideo = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    dialogHelper.setRecipeVideo(result);
                }
            });

    public boolean getStateNews(){
        return true;
    }
}