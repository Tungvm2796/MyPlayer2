package samsung.com.myplayer2.Activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import samsung.com.myplayer2.Adapter.FragmentAdapter;
import samsung.com.myplayer2.Class.Song;
import samsung.com.myplayer2.R;
import samsung.com.myplayer2.Service.MyService;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Song> SongList;

    private ListView songView;

    Context context;

    EditText searchbox;

    ViewPager viewPager;

    TabLayout tabLayout;
    private SlidingUpPanelLayout slidingLayout;
    MyService myService;
    private boolean musicBound = false;
    private Intent playintent;
    ImageButton btnPlayPause;
    ImageButton next;
    ImageButton prev;
    private Intent PPIntent;
    TextView txtArtist;
    TextView txtTitle;
    public static TextView txtTimeSong;
    public static TextView txtTotal;
    public static SeekBar seekBar;

    public void SetTimeTotal() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        txtTotal.setText(simpleDateFormat.format(myService.getDur()));
        seekBar.setMax(myService.getDur());
    }

    public void UpdateTimeSong() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
                txtTimeSong.setText(simpleDateFormat.format(myService.getPosn()));
                seekBar.setProgress(myService.getPosn());
                handler.postDelayed(this, 500);
            }
        }, 100);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initPermission();

        context = this;

        IntentFilter toActivity = new IntentFilter();
        toActivity.addAction("PlayPause");
        toActivity.addAction("StartPlay");
        registerReceiver(myMainBroadcast, toActivity);

        initView();

        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myService.isPng()) {
                    myService.pausePlayer();
                    btnPlayPause.setImageResource(R.drawable.ic_play_circle_outline_white_24dp);
                    Intent pauseIntent = new Intent("ToService");
                    pauseIntent.setAction("SvPlayPause");
                    pauseIntent.putExtra("key", "pause");
                    sendBroadcast(pauseIntent);

                } else {
                    myService.go();
                    btnPlayPause.setImageResource(R.drawable.ic_pause_circle_outline_white_24dp);
                    Intent playIntent = new Intent("ToService");
                    playIntent.setAction("SvPlayPause");
                    playIntent.putExtra("key", "play");
                    sendBroadcast(playIntent);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myService.playNext();
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myService.playPrev();
            }
        });
    }

    private void initView() {
        txtArtist = (TextView) findViewById(R.id.artist);

        txtTitle = (TextView) findViewById(R.id.title);

        txtTimeSong = (TextView) findViewById(R.id.time_song);

        txtTotal = (TextView) findViewById(R.id.time_total);

        seekBar = (SeekBar) findViewById(R.id.seekbar_song);

        viewPager = (ViewPager) findViewById(R.id.viewpager1);

        tabLayout = (TabLayout) findViewById(R.id.tab1);

        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentAdapter fragmentAdapter = new FragmentAdapter(fragmentManager, MainActivity.this);

        viewPager.setAdapter(fragmentAdapter);

        viewPager.setOffscreenPageLimit(fragmentAdapter.getCount() - 1);

        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setTabsFromPagerAdapter(fragmentAdapter);

        //set layout slide listener
        slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        //some "demo" event
        slidingLayout.setPanelSlideListener(onSlideListener());
        slidingLayout.getChildAt(1).setOnClickListener(null);

        btnPlayPause = (ImageButton) findViewById(R.id.btn_play_pause);
        next = (ImageButton) findViewById(R.id.btn_next);
        prev = (ImageButton) findViewById(R.id.btn_prev);
    }

    public void initPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            //if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            //}
            //if (checkSelfPermission(Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WAKE_LOCK}, 1);
            //}
            //if (checkSelfPermission(Manifest.permission.MEDIA_CONTENT_CONTROL) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.MEDIA_CONTENT_CONTROL}, 1);
            //}
            //if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.INTERNET}, 1);
            //}
        }
    }

    private SlidingUpPanelLayout.PanelSlideListener onSlideListener() {
        return new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {

            }

            @Override
            public void onPanelCollapsed(View view) {
                if (myService.isPng())
                    btnPlayPause.setImageResource(R.drawable.ic_pause_circle_outline_white_24dp);
            }

            @Override
            public void onPanelExpanded(View view) {
                if (myService.isPng())
                    btnPlayPause.setImageResource(R.drawable.ic_pause_circle_outline_white_24dp);
            }

            @Override
            public void onPanelAnchored(View view) {

            }

            @Override
            public void onPanelHidden(View view) {

            }
        };
    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            MyService.MusicBinder binder = (MyService.MusicBinder) service;
            //get service
            myService = binder.getService();
            //pass list

            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicBound = false;
        }
    };

    @Override
    public void onStart() {
        super.onStart();

        playintent = new Intent(this, MyService.class);
        this.startService(playintent);
        this.bindService(playintent, musicConnection, Context.BIND_AUTO_CREATE);
        registerReceiver(myMainBroadcast, new IntentFilter("ToActivity"));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (musicBound) {
            this.unbindService(musicConnection);
            musicBound = false;
        }
    }

    @Override
    protected void onResume() {

        IntentFilter toActivity = new IntentFilter();
        toActivity.addAction("PlayPause");
        toActivity.addAction("StartPlay");
        registerReceiver(myMainBroadcast, toActivity);

        try {
            if (myService.isPng())
                btnPlayPause.setImageResource(R.drawable.ic_pause_circle_outline_white_24dp);
            else
                btnPlayPause.setImageResource(R.drawable.ic_play_circle_outline_white_24dp);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String saveTitle = settings.getString("curSongName", "0");
        String saveArtist = settings.getString("curArtist", "0");
        txtTitle.setText(saveTitle);
        txtArtist.setText(saveArtist);

        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(myMainBroadcast);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("curSongName", myService.getSongTitle());
        editor.putString("curArtist", myService.getSongArtist());
        editor.apply();
    }

    BroadcastReceiver myMainBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().toString().equals("PlayPause")) {
                String od = intent.getStringExtra("key");
                Toast.makeText(context, od, Toast.LENGTH_SHORT).show();
                if (od.equals("pause"))
                    btnPlayPause.setImageResource(R.drawable.ic_play_circle_outline_white_24dp);
                else if (od.equals("play"))
                    btnPlayPause.setImageResource(R.drawable.ic_pause_circle_outline_white_24dp);
            } else if (intent.getAction().toString().equals("StartPlay")) {
                txtTitle.setText(intent.getStringExtra("title"));
                txtArtist.setText(intent.getStringExtra("artist"));
            }
        }
    };
}
