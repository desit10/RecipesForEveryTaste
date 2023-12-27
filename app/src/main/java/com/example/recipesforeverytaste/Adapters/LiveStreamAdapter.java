package com.example.recipesforeverytaste.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipesforeverytaste.Helpers.FirebaseHelper;
import com.example.recipesforeverytaste.LiveStreamActivity;
import com.example.recipesforeverytaste.Models.LiveStream;
import com.example.recipesforeverytaste.R;

import java.util.ArrayList;

public class LiveStreamAdapter extends RecyclerView.Adapter<LiveStreamAdapter.ViewHolder> {
    public static final String APP_PREFERENCES = "mysettings";
    private SharedPreferences mSettings;
    Context context;
    ArrayList<LiveStream> liveStreams;
    LinearLayoutManager layoutManager;
    FirebaseHelper firebaseHelper;
    String idUser;

    public LiveStreamAdapter(Context context, ArrayList<LiveStream> liveStreams, LinearLayoutManager layoutManager) {
        this.context = context;
        this.liveStreams = liveStreams;
        this.layoutManager = layoutManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.live_stream_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        firebaseHelper = new FirebaseHelper();

        LiveStream liveStream = liveStreams.get(position);

        holder.liveStreamsName.setText(liveStream.getLiveName());

        holder.btnOpenLiveStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idUser = firebaseHelper.getIdUser();

                mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                String liveStreamAuthor = mSettings.getString("userName", "");

                Intent intenLiveStream = new Intent(context, LiveStreamActivity.class);
                intenLiveStream.putExtra("userID", idUser);
                intenLiveStream.putExtra("userName", liveStreamAuthor);
                intenLiveStream.putExtra("liveID", liveStream.getLiveID());
                intenLiveStream.putExtra("isHost", false);
                context.startActivity(intenLiveStream);
            }
        });

    }

    @Override
    public int getItemCount() {
        return liveStreams.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgUser;
        TextView liveStreamsName;
        Button btnOpenLiveStream;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgUser = itemView.findViewById(R.id.imgUser);
            liveStreamsName = itemView.findViewById(R.id.nameLiveStream);
            btnOpenLiveStream = itemView.findViewById(R.id.btnOpenLiveStream);

        }
    }

}
