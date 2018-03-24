package samsung.com.myplayer2.Activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import samsung.com.myplayer2.Adapter.CustomPagerAdapter;
import samsung.com.myplayer2.Adapter.FragmentAdapter;
import samsung.com.myplayer2.Adapter.RecyclerSongAdapter;
import samsung.com.myplayer2.Class.Song;
import samsung.com.myplayer2.R;
import samsung.com.myplayer2.Service.MyService;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Song> MainSongList;

    Context context;

    SlidingUpPanelLayout slidingLayout;
    FloatingSearchView searchView;

    ViewPager viewPager;
    CustomPagerAdapter customPagerAdapter;

    TabLayout tabLayout;
    SmartTabLayout smartTabLayout;
    //PagerTabStrip pagerTabStrip;
    MyService myService;
    private boolean musicBound = false;
    private Intent playintent;
    public static ImageButton btnPlayPause;
    ImageButton next;
    ImageButton prev;
    ImageButton shuffle;
    ImageButton repeat;
    TextView txtArtist;
    TextView txtTitle;
    public static TextView txtTimeSong;
    public static TextView txtTotal;
    public static SeekBar seekBar;
    ImageView imgDisc;
    String SongPath;
    RecyclerSongAdapter songAdapter;

    LinearLayout mainlay1;
    LinearLayout mainlay2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initPermission();

        context = this;

        IntentFilter toActivity = new IntentFilter("ToActivity");
        toActivity.addAction("PlayPause");
        toActivity.addAction("StartPlay");
        registerReceiver(myMainBroadcast, toActivity);

        initView();

        searchView = (FloatingSearchView) findViewById(R.id.floating_search_view);

        //set layout slide listener
        slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingLayout.getChildAt(1).setOnClickListener(null);
        //slidingLayout.setDragView(findViewById(R.id.dragview));

        imgDisc = (ImageView) findViewById(R.id.imageViewDisc);
        songAdapter = new RecyclerSongAdapter();

        slidingLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    if (myService.isPng())
                        btnPlayPause.setImageResource(R.drawable.ic_pause_circle_outline_white_24dp);
                } else if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    if (myService.isPng())
                        btnPlayPause.setImageResource(R.drawable.ic_pause_circle_outline_white_24dp);
                }
            }
        });

        slidingLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (musicBound) {
                    switch (position) {
                        case 0:
                            myService.setListNumber(1);
                            songAdapter.setOnPlaylist(0);
                            break;
                        case 1:
                            myService.setListNumber(2);
                            songAdapter.setOnPlaylist(0);
                            break;
                        case 2:
                            myService.setListNumber(3);
                            songAdapter.setOnPlaylist(0);
                            break;
                        case 3:
                            myService.setListNumber(4);
                            songAdapter.setOnPlaylist(1);
                            break;
                        case 4:
                            myService.setListNumber(5);
                            break;
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
            }

            @Override
            public void onSearchAction(String currentQuery) {
                mainlay1.setVisibility(View.INVISIBLE);
                mainlay2.setVisibility(View.VISIBLE);
            }
        });

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

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                myService.seek(seekBar.getProgress());
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

        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myService.setShuffle();
            }
        });

        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myService.setRepeat();
            }
        });
    }

    private void initView() {
        txtArtist = (TextView) findViewById(R.id.artist);

        txtTitle = (TextView) findViewById(R.id.title);

        txtTimeSong = (TextView) findViewById(R.id.time_song);

        txtTotal = (TextView) findViewById(R.id.time_total);

        seekBar = (SeekBar) findViewById(R.id.seekbar_song);

        smartTabLayout = (SmartTabLayout) findViewById(R.id.viewpagertab);

        customPagerAdapter = new CustomPagerAdapter(this, getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.viewpager1);

        viewPager.setAdapter(customPagerAdapter);

        viewPager.setOffscreenPageLimit(customPagerAdapter.getCount() - 1);

        smartTabLayout.setViewPager(viewPager);

        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentAdapter fragmentAdapter = new FragmentAdapter(fragmentManager, MainActivity.this);

        btnPlayPause = (ImageButton) findViewById(R.id.btn_play_pause);
        next = (ImageButton) findViewById(R.id.btn_next);
        prev = (ImageButton) findViewById(R.id.btn_prev);
        shuffle = (ImageButton) findViewById(R.id.btn_shuffle);
        repeat = (ImageButton) findViewById(R.id.btn_repeat);

        mainlay1 = (LinearLayout) findViewById(R.id.mainlay1);
        mainlay2 = (LinearLayout) findViewById(R.id.mainlay2);
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


 /*   private SlidingUpPanelLayout.PanelSlideListener onSlideListener() {
        return new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

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
    }*/

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

        IntentFilter toActivity = new IntentFilter("ToActivity");
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
        String saveTitle = settings.getString("Title", "0");
        String saveArtist = settings.getString("Artist", "0");
        String savePath = settings.getString("Path", "0");
        txtTitle.setText(saveTitle);
        txtArtist.setText(saveArtist);
        if (!savePath.equals("0"))
            imgDisc.setImageBitmap(GetBitmap(savePath));

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
        /*SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("curSongName", myService.getSongTitle());
        editor.putString("curArtist", myService.getSongArtist());
        editor.putString("curSongImg", SongPath);
        editor.apply();*/
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
                btnPlayPause.setImageResource(R.drawable.ic_pause_circle_outline_white_24dp);
                SongPath = intent.getStringExtra("songpath");
                imgDisc.setImageBitmap(GetBitmap(SongPath));
            }
        }
    };

    Bitmap GetBitmap(String filePath) {
        Bitmap image;
        MediaMetadataRetriever mData = new MediaMetadataRetriever();
        mData.setDataSource(filePath);
        try {
            byte art[] = mData.getEmbeddedPicture();
            image = BitmapFactory.decodeByteArray(art, 0, art.length);
        } catch (Exception e) {
            image = null;
        }
        return image;
    }

    @Override
    public void onBackPressed() {
        if (slidingLayout != null &&
                (slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED ||
                        slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else if ((slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) &&
                (mainlay2.getVisibility() == View.VISIBLE)) {
            mainlay2.setVisibility(View.INVISIBLE);
            mainlay1.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

}
