package com.example.recipesforeverytaste.Models;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recipesforeverytaste.R;
import com.zegocloud.uikit.prebuilt.livestreaming.ZegoUIKitPrebuiltLiveStreamingConfig;
import com.zegocloud.uikit.prebuilt.livestreaming.ZegoUIKitPrebuiltLiveStreamingFragment;
import com.zegocloud.uikit.prebuilt.livestreaming.core.ZegoDialogInfo;

public class LiveStream {
    AppCompatActivity activity;
    long appID = 592983220;
    String appSign = "ceb67e683d248b3ce2a0a3f20826bbba037678b8394ebad50be8ee1d4a93e2b3";
    String userID, userName, liveID, liveName;
    boolean isHost;

    public LiveStream() {}

    public LiveStream(AppCompatActivity activity, String userID, String userName, String liveID, boolean isHost) {
        this.activity = activity;
        this.userID = userID;
        this.userName = userName;
        this.liveID = liveID;
        this.isHost = isHost;
    }
    public void Stream() {
        ZegoUIKitPrebuiltLiveStreamingConfig config;
        ZegoDialogInfo confirmDialogInfo = new ZegoDialogInfo();
        if (isHost) {
            config = ZegoUIKitPrebuiltLiveStreamingConfig.host();

            confirmDialogInfo.title= "Выберите действие";
            confirmDialogInfo.message= "Вы хотите завершить прямой эфир?";
            confirmDialogInfo.cancelButtonName= "Продолжить";
            confirmDialogInfo.confirmButtonName= "Завершить";

            config.confirmDialogInfo = confirmDialogInfo;
            config.translationText.startLiveStreamingButton = "Начать прямой эфир";
            config.translationText.noHostOnline = "Запуск прямого эфира";
        } else {
            config = ZegoUIKitPrebuiltLiveStreamingConfig.audience();

            confirmDialogInfo.title= "Выберите действие";
            confirmDialogInfo.message= "Вы хотите покинуть прямой эфир?";
            confirmDialogInfo.cancelButtonName= "Остаться";
            confirmDialogInfo.confirmButtonName= "Выйти";

            config.confirmDialogInfo = confirmDialogInfo;
            config.translationText.startLiveStreamingButton = "Подключение к прямому эфиру";
            config.translationText.noHostOnline = "Прямой эфир завершен";
        }

        ZegoUIKitPrebuiltLiveStreamingFragment fragment = ZegoUIKitPrebuiltLiveStreamingFragment.newInstance(
                appID, appSign, userID, userName,liveID,config);

        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commitNow();
    }

    public LiveStream(String liveID, String liveName) {
        this.liveID = liveID;
        this.liveName = liveName;
    }

    public String getLiveID() {
        return liveID;
    }

    public void setLiveID(String liveID) {
        this.liveID = liveID;
    }

    public String getLiveName() {
        return liveName;
    }

    public void setLiveName(String liveName) {
        this.liveName = liveName;
    }
}
