package com.example.android_player;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toolbar;

import java.util.ArrayList;


public class PlayMusicActivity extends AppCompatActivity {


    public static final String Broadcast_PAUSE_AUDIO = "com.example.android_player.pauseAudio";
    public static final String Broadcast_RESUME_AUDIO = "com.example.android_player.resumeAudio";
    public static final String Broadcast_SKIP_TO_PREV_AUDIO = "com.example.android_player.skipToPrevAudio";
    public static final String Broadcast_SKIP_TO_NEXT_AUDIO = "com.example.android_player.skipToNextAudio";
    public static final String Broadcast_SET_MEDIA_PLAYER_INFO = "com.example.android_player.SET_MP_INFO";
    public static final String Broadcast_SET_POSITION = "com.example.android_player.SET_POSITION";
    public static final String Broadcast_SEND_DURATION = "com.example.android_player.SET_DURATION";



    boolean serviceBound = false;

    private ImageButton btn_toPrev;
    private ImageButton btn_toNext;
    private ImageButton btn_playPause;
    public SeekBar seekBar;
    private TextView currentTime;
    private TextView lastDurationSong;
    private TextView songName;
    private TextView songArtist;
    private Handler mHandler = new Handler();
    private Handler mSeekbarUpdateHandler = new Handler();

    ArrayList<Audio> audioList;

    private String currentTitle;
    private String currentArtist;

    private int audioIndex;

    private int set_duration;
    private long duration;
    private long current_position;

    private final String ARTIST = "ARTIST";
    private final String SONG = "SONG";

    private Utilities utils = new Utilities();

    public MediaPlayer mMediaPlayer;
    private MediaPlayerService player;
    private MediaSession mediaSession;
    private MainActivity mainActivity;
    private MediaControllerCompat mediaControllerCompat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playing_song_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        sendBCForSetDuration();

        sendBcForDuration();
        register_setMPInfo_reciever();
        register_setDuration_reciever();
        player = new MediaPlayerService();

       // checkServiceConnection();
        Intent playerIntent = new Intent(this, MediaPlayerService.class);
        startService(playerIntent);
        bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        updateSeekBar();
        setMusicPlayerComponents();
        setOnCliclListeners();


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(fromUser)
                {
                        sendBCForSetPosition(progress);
                }

                           }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });





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
    private void checkServiceConnection()
    {
        if (!serviceBound) {
            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    }


    private void setMusicPlayerComponents()
    {
        btn_toPrev = findViewById(R.id.btn_prev);
        btn_toNext= findViewById(R.id.btn_next);
        btn_playPause = findViewById(R.id.btn_play_pause);
        seekBar = findViewById(R.id.song_progress_seekbar);
        currentTime = findViewById(R.id.current_time_song);
        lastDurationSong = findViewById(R.id.last_duration_song);
        songArtist = findViewById(R.id.song_Artist);
        songName = findViewById(R.id.song_Name);
    }

    private void setOnCliclListeners()
    {
        btn_playPause.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btn_playPause.setSelected(!btn_playPause.isSelected());
                        if (btn_playPause.isSelected()) {
                            Intent broadcastIntent = new Intent(Broadcast_PAUSE_AUDIO);
                            sendBroadcast(broadcastIntent);
                        } else {
                            Intent broadcastIntent = new Intent(Broadcast_RESUME_AUDIO);
                            sendBroadcast(broadcastIntent);
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

    public void updateSeekBar()
    {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {
            long totalDuration = duration;
            long currentDuration = current_position;

            // Displaying Total Duration time
            lastDurationSong.setText(""+utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            currentTime.setText(""+utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            //int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
            int progress =(int)((currentDuration)/1000);
            //Log.d("Progress", ""+progress);
            seekBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
        };

    private void  sendBcForDuration()
    {
        Intent intent = new Intent(Broadcast_SET_MEDIA_PLAYER_INFO);
        sendBroadcast(intent);
    }

    private BroadcastReceiver getMPInfo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                duration = intent.getLongExtra("duration",duration);
                current_position = intent.getLongExtra("current_time",current_position);
                currentArtist = intent.getStringExtra("artist");
                currentTitle = intent.getStringExtra("title");
                songArtist.setText(currentArtist);
                songName.setText(currentTitle);

        }
    } ;
    private BroadcastReceiver getDuration = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            set_duration = intent.getIntExtra("duration",set_duration);
            seekBar.setMax(set_duration/1000);
        }
    } ;

    private void register_setMPInfo_reciever() {
        //Register Media Player Info receiver
        IntentFilter filter = new IntentFilter(PlayMusicActivity.Broadcast_SET_MEDIA_PLAYER_INFO);
        registerReceiver(getMPInfo, filter);
    }
    private void  sendBCForSetPosition(int position)
    {
        Intent intent = new Intent(Broadcast_SET_POSITION);
        intent.putExtra("position",position);
        sendBroadcast(intent);
    }
    private void  sendBCForSetDuration()
    {
        Intent intent = new Intent(Broadcast_SEND_DURATION);
        sendBroadcast(intent);
    }
    private void register_setDuration_reciever() {

        IntentFilter filter = new IntentFilter(PlayMusicActivity.Broadcast_SEND_DURATION);
        registerReceiver(getDuration, filter);
    }

}


