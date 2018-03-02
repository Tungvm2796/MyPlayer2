package samsung.com.myplayer2.Activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import samsung.com.myplayer2.Class.Song;
import samsung.com.myplayer2.Service.MyService;

public class PlayActivity extends AppCompatActivity {
    static MediaPlayer mediaPlayer = new MediaPlayer();
    TextView txtTitle, txtTimeSong, txtTimeTotal, txtArtist;
    SeekBar seekSong;
    ImageButton btnPrev, btnNext, btnPlay;
    Animation animation;
    ImageView img;
    ArrayList<Song> arraySong;
    MyService myService;
    private boolean musicBound = false;
    private Intent playintent;
    /*public void setSong(int position) throws IOException {
        mediaPlayer.reset();
        mediaPlayer.setDataSource(arraySong.get(position).getDta());
        txtTitle.setText(arraySong.get(position).getTitle());
        txtArtist.setText(arraySong.get(position).getArtist());
        mediaPlayer.prepare();
    }*/
    /*public void SetTimeTotal() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        txtTimeTotal.setText(simpleDateFormat.format(myService.getPlayer().getDuration()));
        seekSong.setMax(myService.getPlayer().getDuration());
    }
    public void UpdateTimeSong() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
                txtTimeSong.setText(simpleDateFormat.format(myService.getPlayer().getCurrentPosition()));
                seekSong.setProgress(myService.getPlayer().getCurrentPosition());
                handler.postDelayed(this, 500);
            }
        }, 100);
    }
    int position = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        btnNext = (ImageButton) findViewById(R.id.btn_next);
        btnPrev = (ImageButton) findViewById(R.id.btn_prev);
        txtTitle = (TextView) findViewById(R.id.title);
        btnPlay = (ImageButton) findViewById(R.id.btn_play);
        txtTimeSong = (TextView) findViewById(R.id.time_song);
        txtTimeTotal = (TextView) findViewById(R.id.time_total);
        txtArtist = (TextView) findViewById(R.id.artist);
        Intent intent = getIntent();
        position = intent.getIntExtra("songid", -1);
        arraySong = new ArrayList<Song>();
        arraySong = (ArrayList<Song>) intent.getSerializableExtra("arraysong");
        seekSong = (SeekBar) findViewById(R.id.seekbar_song);
        img = (ImageView) findViewById(R.id.imageViewDisc);
        animation = AnimationUtils.loadAnimation(this, R.anim.disc_rolate);

        /*if (curpos != position) {
            curpos = position;
            try {
                myService.setsong(position);
                myService.playsong();
                btnPlay.setImageResource(R.drawable.ic_pause_circle_outline_white_24dp);
                img.startAnimation(animation);
                SetTimeTotal();
                UpdateTimeSong();
            } catch (IOException e) {
                Toast.makeText(PlayActivity.this, "Can not play", Toast.LENGTH_LONG).show();
            }
        } else {
            try {
                myService.setsong(position);
                myService.playsong();
                btnPlay.setImageResource(R.drawable.ic_pause_circle_outline_white_24dp);
                img.startAnimation(animation);
                SetTimeTotal();
                UpdateTimeSong();
            } catch (IOException e) {
                Toast.makeText(PlayActivity.this, "Can not play", Toast.LENGTH_LONG).show();
            }
        }*/


        /*btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (myService.isPlaying()) {
                    myService.onPause();
                    btnPlay.setImageResource(R.drawable.ic_play_circle_outline_white_24dp);
                    img.clearAnimation();
                    SetTimeTotal();
                    UpdateTimeSong();
                } else {
                    myService.onStart();
                    btnPlay.setImageResource(R.drawable.ic_pause_circle_outline_white_24dp);
                    img.startAnimation(animation);
                    SetTimeTotal();
                    UpdateTimeSong();
                }*/

                    //myService.playsong();



       /* btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position < arraySong.size() - 1)
                    position++;
                else if (position == arraySong.size() - 1)
                    position = 0;
                try {
                    myService.setsong(position);
                    myService.playsong();
                    btnPlay.setImageResource(R.drawable.ic_pause_circle_outline_white_24dp);
                    SetTimeTotal();
                    UpdateTimeSong();
                } catch (IOException e) {
                    Toast.makeText(PlayActivity.this, "Can not play", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position > 0)
                    position--;
                else if (position == 0)
                    position = arraySong.size() - 1;
                try {
                    myService.setsong(position);
                    myService.playsong();
                    btnPlay.setImageResource(R.drawable.ic_pause_circle_outline_white_24dp);
                    SetTimeTotal();
                    UpdateTimeSong();
                } catch (IOException e) {
                    Toast.makeText(PlayActivity.this, "Can not play", Toast.LENGTH_LONG).show();
                }
            }
        });
*/
        /*seekSong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                myService.getPlayer().seekTo(seekSong.getProgress());
            }
        });*/

    }

    /*private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            MyService.MusicBinder binder = (MyService.MusicBinder)service;
            //get service
            myService = binder.getService();
            //pass list
            myService.setList(arraySong);
            myService.setSong(position);
            musicBound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if(playintent==null){
            playintent = new Intent(this, MyService.class);
            bindService(playintent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playintent);
        }
    }*/
