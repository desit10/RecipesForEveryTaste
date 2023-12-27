package com.example.recipesforeverytaste.FragmentsMenu;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.recipesforeverytaste.AuthorizationActivity;
import com.example.recipesforeverytaste.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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

    public static final String APP_PREFERENCES = "mysettings";
    private SharedPreferences mSettings;
    private SharedPreferences.Editor editor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        mSettings = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        int speed = 350;

        Button btnExitAcc = view.findViewById(R.id.btnExitAcc);
        btnExitAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor = mSettings.edit();
                editor.putBoolean("stateAcc", false);
                editor.apply();

                editor.putString("userName", "");
                editor.apply();

                getActivity().finish();
                startActivity(new Intent(getActivity(), AuthorizationActivity.class));
            }
        });

        FrameLayout frameThemes = view.findViewById(R.id.frameThemes);
        frameThemes.setTranslationX(-1000f);
        frameThemes.animate().translationXBy(1000f).setDuration(speed).start();

        FrameLayout frameNotifications = view.findViewById(R.id.frameNotifications);
        frameNotifications.setTranslationX(-1000f);
        frameNotifications.animate().translationXBy(1000f).setDuration(speed).start();

        LinearLayout layoutNotifications = view.findViewById(R.id.layoutNotifications);
        ValueAnimator animNotify = ValueAnimator.ofInt(0, 500);
        animNotify.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = layoutNotifications.getLayoutParams();
                layoutParams.height = value;
                layoutNotifications.requestLayout();
            }
        });
        animNotify.setStartDelay(speed);
        animNotify.setDuration(speed);
        animNotify.start();

        LinearLayout layoutThemes  = view.findViewById(R.id.layoutThemes);
        ValueAnimator animThemes = ValueAnimator.ofInt(0, 500);
        animThemes.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = layoutThemes.getLayoutParams();
                layoutParams.height = value;
                layoutThemes.requestLayout();
            }
        });
        animThemes.setStartDelay(350);
        animThemes.setDuration(speed);
        animThemes.start();

        btnExitAcc.setTranslationX(-1000f);
        btnExitAcc.animate().translationXBy(1000f).setStartDelay(400).setDuration(speed).start();

        return view;
    }
}