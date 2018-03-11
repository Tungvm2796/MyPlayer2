package samsung.com.myplayer2.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

import samsung.com.myplayer2.Activities.MainActivity;
import samsung.com.myplayer2.Class.Constants;
import samsung.com.myplayer2.Class.Song;
import samsung.com.myplayer2.Fragments.SongListFragment;
import samsung.com.myplayer2.R;

/*
 * This is demo code to accompany the Mobiletuts+ series:
 * Android SDK: Creating a Music Player
 *
 * Sue Smith - February 2014
 */

public class MyService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    //media player
    public static MediaPlayer player;
    //song list
    private ArrayList<Song> songs;
    //current position
    private int songPosn;
    //binder
    private final IBinder musicBind = new MusicBinder();
    //title of current song
    private String songTitle = "";
    //artist of current song
    private String songArtist = "";
    //notification id
    private static final int NOTIFY_ID = 27;
    //shuffle flag and random
    private boolean shuffle = false;
    private Random rand;

    public static boolean bothRun = true;

    public Context context;

    public static WeakReference<SeekBar> seekPro;
    public static WeakReference<TextView> txtTotal;
    public static WeakReference<TextView> txtCurTime;
    public static WeakReference<ImageButton> btnPayPause;

    public void onCreate() {
        //create the service
        super.onCreate();
        //initialize position
        songPosn = 0;
        //random
        rand = new Random();
        //create player
        player = new MediaPlayer();

        context = this;

        IntentFilter svintent = new IntentFilter("ToService");
        svintent.addAction("SvPlayPause");
        svintent.addAction("SvPlayOne");
        registerReceiver(myServBroadcast, svintent);

        //initialize
        initMusicPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() != null) {
            if (intent.getAction().toString().equals(Constants.ACTION.PAUSE_ACTION)) {
                pausePlayer();
                showNoti(2);

                Intent intent1 = new Intent();
                intent1.setAction("PlayPause");
                intent1.putExtra("key", "pause");
                sendBroadcast(intent1);

                progressHandler.removeCallbacks(run);

            } else if (intent.getAction().toString().equals(Constants.ACTION.PLAY_ACTION)) {
                go();
                showNoti(1);

                Intent intent2 = new Intent();
                intent2.setAction("PlayPause");
                intent2.putExtra("key", "play");
                sendBroadcast(intent2);

                updateProgress();

            } else if (intent.getAction().toString().equals(Constants.ACTION.EXIT_ACTION)) {
                if(!player.isPlaying()) {
                    if (bothRun = true) {
                        stopForeground(true);
                        checkBothRun();
                    }
                    else if (bothRun = false){
                        stopForeground(true);
                        unregisterReceiver(myServBroadcast);
                        stopSelf();
                    }
                }
            }
        }

        initUI();

        return START_NOT_STICKY;
    }

    public void initUI() {
        seekPro = new WeakReference<>(MainActivity.seekBar);
        txtCurTime = new WeakReference<>(MainActivity.txtTimeSong);
        txtTotal = new WeakReference<>(MainActivity.txtTotal);
        btnPayPause = new WeakReference<>(SongListFragment.btnPP);
    }

    public void initMusicPlayer() {

        //set player properties
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //set listeners
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    //pass song list
    public void setList(ArrayList<Song> theSongs) {
        songs = theSongs;
    }

    //binder
    public class MusicBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

    //activity will bind to service
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    //release resources when unbind
    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }

    //play a song
    public void playSong() {
        //play
        player.reset();
        //get song
        Song playSong = songs.get(songPosn);
        //get title
        songTitle = playSong.getTitle();
        //get artist
        songArtist = playSong.getArtist();
        //get id
        long currSong = playSong.getID();
        //set uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);
        //set the data source
        try {
            player.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        try {
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent setup = new Intent();
        setup.setAction("StartPlay");
        setup.putExtra("title", songTitle);
        setup.putExtra("artist", songArtist);
        sendBroadcast(setup);

        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                updateProgress();
                showNoti(1);
            }
        });
    }

    //set the song
    public void setSong(int songIndex) {
        songPosn = songIndex;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.v("MUSIC PLAYER", "Playback Error");
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        mp.start();
        updateProgress();
        showNoti(1);
    }


    public void showNoti(int casenum) {
        //notification
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0, notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pauseIntent = new Intent(this, MyService.class);
        pauseIntent.setAction(Constants.ACTION.PAUSE_ACTION);
        PendingIntent ppauseIntent = PendingIntent.getService(this, 0, pauseIntent, 0);

        Intent playIntent = new Intent(this, MyService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0, playIntent, 0);

        Intent closeIntent = new Intent(this, MyService.class);
        closeIntent.setAction(Constants.ACTION.EXIT_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0, closeIntent, 0);

        Notification.Builder builder = new Notification.Builder(this);

        Notification.MediaStyle style = new Notification.MediaStyle();

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.ic_play_circle_outline_white_24dp)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Now Playing")
                .setContentText(songTitle)
                .setAutoCancel(true)
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_STOP))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        ;
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notice_layout);
        switch (casenum) {
            case 1:
                contentView.setImageViewResource(R.id.notice_play, R.drawable.ic_pause_circle_outline_white_24dp);
                contentView.setOnClickPendingIntent(R.id.notice_play, ppauseIntent);
                break;
            case 2:
                contentView.setOnClickPendingIntent(R.id.notice_play, pplayIntent);
                contentView.setImageViewResource(R.id.notice_play, R.drawable.ic_play_circle_outline_white_24dp);
                break;
        }

        contentView.setTextViewText(R.id.song_name, songTitle);
        contentView.setTextViewText(R.id.song_artist, songArtist);
        contentView.setOnClickPendingIntent(R.id.exit_notice, pcloseIntent);


        Notification noti = builder.build();
        noti.contentView = contentView;

        startForeground(NOTIFY_ID, noti);
    }

    //playback methods
    public int getPosn() {
        return player.getCurrentPosition();
    }

    public int getDur() {
        return player.getDuration();
    }

    public boolean isPng() {
        return player.isPlaying();
    }

    public void pausePlayer() {
        player.pause();
    }

    public void seek(int posn) {
        player.seekTo(posn);
    }

    public void go() {
        player.start();
    }

    public int CountNoti() {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        StatusBarNotification[] notifications = notificationManager.getActiveNotifications();
        int count = 0;
        for (StatusBarNotification notification : notifications) {
            if (notification.getId() == 27) {
                count++;
            }
        }
        return count;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public String getSongArtist() {
        return songArtist;
    }

    //skip to previous track
    public void playPrev() {
        songPosn--;
        if (songPosn < 0) songPosn = songs.size() - 1;
        playSong();
    }

    //skip to next
    public void playNext() {
        if (shuffle) {
            int newSong = songPosn;
            while (newSong == songPosn) {
                newSong = rand.nextInt(songs.size());
            }
            songPosn = newSong;
        } else {
            songPosn++;
            if (songPosn >= songs.size()) songPosn = 0;
        }
        playSong();
    }

    //toggle shuffle
    public void setShuffle() {
        if (shuffle) shuffle = false;
        else shuffle = true;
    }

    public boolean checkBothRun() {
        if (bothRun) return false;
        else
            return true;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        if(!player.isPlaying()){
            stopForeground(true);
            unregisterReceiver(myServBroadcast);
            stopSelf();
        }else{ checkBothRun();}

        Log.i("DKM", "Da kill task xxxxxxxxxxxxxxxxx ");
    }

    BroadcastReceiver myServBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().toString().equals("SvPlayPause")) {
                if (intent.getStringExtra("key").equals("pause")) {
                    Toast.makeText(getApplicationContext(), "Service received: Pause", Toast.LENGTH_SHORT).show();
                    showNoti(2);
                    progressHandler.removeCallbacks(run);
                } else if (intent.getStringExtra("key").equals("play")) {
                    Toast.makeText(getApplicationContext(), "Service received: Play", Toast.LENGTH_SHORT).show();
                    showNoti(1);
                    updateProgress();
                }
            } else if (intent.getAction().toString().equals("SvPlayOne")) {
                Toast.makeText(getApplicationContext(), "Service received: Play One", Toast.LENGTH_SHORT).show();
                Integer posn = intent.getIntExtra("pos", 0);
                setSong(posn);
                playSong();
                btnPayPause.get().setImageResource(R.drawable.ic_pause_circle_outline_white_24dp);
            }
        }
    };

    public void updateProgress() {
        try {
            progressHandler.postDelayed(run, 100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static Handler progressHandler = new Handler();

    static Runnable run = new Runnable() {
        @Override
        public void run() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");

            try {
                seekPro.get().setMax(player.getDuration());
                txtTotal.get().setText(simpleDateFormat.format(player.getDuration()));
                txtCurTime.get().setText(simpleDateFormat.format(player.getCurrentPosition()));
                seekPro.get().setProgress(player.getCurrentPosition());
                progressHandler.postDelayed(this, 100);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    @Override
    public void onCompletion(MediaPlayer mp) {
        //check if playback has reached the end of a track
        seekPro.get().setProgress(0);
        progressHandler.removeCallbacks(run);
        if (player.getCurrentPosition() > 0) {
            mp.reset();
            playNext();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        progressHandler.removeCallbacks(run);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        progressHandler.removeCallbacks(run);
        seek(seekBar.getProgress());
        updateProgress();
    }
}
