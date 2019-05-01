package com.example.android_player;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;


public class PlayMusicActivity extends AppCompatActivity  {


    public static final String Broadcast_PAUSE_AUDIO = "com.example.android_player.pauseAudio";
    public static final String Broadcast_RESUME_AUDIO = "com.example.android_player.resumeAudio";
    public static final String Broadcast_SKIP_TO_PREV_AUDIO = "com.example.android_player.skipToPrevAudio";
    public static final String Broadcast_SKIP_TO_NEXT_AUDIO = "com.example.android_player.skipToNextAudio";


    private ImageButton btn_toPrev;
    private ImageButton btn_toNext;
    private ImageButton btn_playPause;
    private SeekBar seekBar;
    private TextView currentTime;
    private TextView lastDurationSong;
    private Handler handler = new Handler();

    ArrayList<Audio> audioList;

    private final String ARTIST = "ARTIST";
    private final String SONG = "SONG";

    private MediaPlayerService player;
    boolean serviceBound = false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playing_song_activity);

        Bundle arguments = getIntent().getExtras();
        int playing_song_index = arguments.getInt("playing_song_index");

        StorageUtil storage = new StorageUtil(getApplicationContext());
        storage.storeAudio(audioList);



        setMusicPlayerComponents();
        btn_playPause.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    MediaPlayerService check = new MediaPlayerService();
                    if(check.isMediaSessionActive()) {
                        Intent broadcastIntent = new Intent(Broadcast_PAUSE_AUDIO);
                        sendBroadcast(broadcastIntent);
                        btn_playPause.setImageDrawable(getDrawable(R.drawable.ic_btn_play));
                    } else {
                        Intent broadcastIntent = new Intent(Broadcast_RESUME_AUDIO);
                        sendBroadcast(broadcastIntent);
                        btn_playPause.setImageDrawable(getDrawable(R.drawable.ic_btn_pause));
                    }
                    }
                }

        );
        btn_toNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent broadcastIntent = new Intent(Broadcast_SKIP_TO_NEXT_AUDIO);
                sendBroadcast(broadcastIntent);
            }
        });
        btn_toPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent broadcastIntent = new Intent(Broadcast_SKIP_TO_PREV_AUDIO);
                sendBroadcast(broadcastIntent);
            }
        });



    }

    private void setMusicPlayerComponents()
    {
        btn_toPrev = findViewById(R.id.btn_prev);
        btn_toNext= findViewById(R.id.btn_next);
        btn_playPause = findViewById(R.id.btn_play_pause);
        seekBar = findViewById(R.id.song_progress_seekbar);
        currentTime = findViewById(R.id.current_time_song);
        lastDurationSong = findViewById(R.id.last_duration_song);

        btn_playPause.setImageDrawable(getDrawable(R.drawable.ic_btn_pause));

    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

}
