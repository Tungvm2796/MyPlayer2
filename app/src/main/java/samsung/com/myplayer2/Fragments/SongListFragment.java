package samsung.com.myplayer2.Fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import samsung.com.myplayer2.Adapter.RecyclerSongAdapter;
import samsung.com.myplayer2.Class.Function;
import samsung.com.myplayer2.Class.Song;
import samsung.com.myplayer2.R;
import samsung.com.myplayer2.Service.MyService;

/**
 * A simple {@link Fragment} subclass.
 */
public class SongListFragment extends Fragment {
    public SongListFragment() {
        // Required empty public constructor
    }

    public ArrayList<Song> SongList;
    private ArrayList<Song> SongFilterList;
    private RecyclerView songView;
    EditText searchbox;
    Context context;
    Animation animation;

    MyService myService;
    private boolean musicBound = false;
    private Intent playintent;
    TextView textTimeSong;
    TextView textTotal;
    TextView txtTitle;
    SeekBar seekBar;
    ImageButton btnsearch;

    Function function;

    public void SetTimeTotal() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        textTotal.setText(simpleDateFormat.format(myService.getDur()));
        seekBar.setMax(myService.getDur());
    }

    public void UpdateTimeSong() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
                textTimeSong.setText(simpleDateFormat.format(myService.getPosn()));
                seekBar.setProgress(myService.getPosn());
                handler.postDelayed(this, 500);
            }
        }, 100);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_song_list, container, false);

        function = new Function();

        songView = (RecyclerView) v.findViewById(R.id.song_list);

        btnsearch = (ImageButton) v.findViewById(R.id.btnsearch);

        textTimeSong = (TextView) getActivity().findViewById(R.id.time_song);

        textTotal = (TextView) getActivity().findViewById(R.id.time_total);

        txtTitle = (TextView) getActivity().findViewById(R.id.title);

        seekBar = (SeekBar) getActivity().findViewById(R.id.seekbar_song);

        searchbox = (EditText) v.findViewById(R.id.searchbox);

        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.disc_rolate);

        context = v.getContext();

        setRetainInstance(true);

        SongList = new ArrayList<>();

        SongFilterList = new ArrayList<>();

        function.getSongList(getActivity(), SongList);

        final RecyclerSongAdapter songAdt = new RecyclerSongAdapter(getContext(), SongList);
        RecyclerView.LayoutManager mManager = new LinearLayoutManager(getContext());
        songView.setLayoutManager(mManager);
        songView.setAdapter(songAdt);

        searchbox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    //SongList.clear();
                    //getSongList();
                    //RecyclerSongAdapter songAdapter1 = new RecyclerSongAdapter(SongList);
                    //songView.setAdapter(songAdapter1);
                } /*else {
                    songView.setAdapter(null);
                    SongList.clear();
                    getSongByName(charSequence.toString().toLowerCase());
                    RecyclerSongAdapter songAdapter2 = new RecyclerSongAdapter(SongList);
                    songView.setAdapter(songAdapter2);
                }*/
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        searchbox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                  /* Write your logic here that will be executed when user taps next button */
                    if (searchbox.getText() != null) {
                        songView.setAdapter(null);
                        SongFilterList.clear();
                        getSongByName(searchbox.getText().toString().toLowerCase(), SongList);
                        RecyclerSongAdapter songAdapter1 = new RecyclerSongAdapter(getContext(), SongFilterList);
                        songView.setAdapter(songAdapter1);
                        myService.setSongListFrag1(SongFilterList);
                    } else {
                        songView.setAdapter(null);
                        songView.setAdapter(songAdt);
                        myService.setSongListFrag1(SongList);
                    }
                }
                return false;
            }
        });

        btnsearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageButton v = (ImageButton) view;
                        v.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                        view.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:

                        // Your action here on button click
                        if (searchbox.getText() != null) {
                            songView.setAdapter(null);
                            SongFilterList.clear();
                            getSongByName(searchbox.getText().toString().toLowerCase(), SongList);
                            RecyclerSongAdapter songAdapter2 = new RecyclerSongAdapter(getContext(), SongFilterList);
                            songView.setAdapter(songAdapter2);
                            myService.setSongListFrag1(SongFilterList);
                        } else {
                            songView.setAdapter(null);
                            songView.setAdapter(songAdt);
                            myService.setSongListFrag1(SongList);
                        }


                    case MotionEvent.ACTION_CANCEL: {
                        ImageButton v = (ImageButton) view;
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
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

        return v;
    }


    public void getSongByName(String entry, ArrayList<Song> entryArray) {
        for (int i = 0; i < entryArray.size(); i++) {
            if (entryArray.get(i).getTitle().toLowerCase().contains(entry) || entryArray.get(i).getArtist().toLowerCase().contains(entry))
                SongFilterList.add(entryArray.get(i));
        }
    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            MyService.MusicBinder binder = (MyService.MusicBinder) service;
            //get service
            myService = binder.getService();
            //pass list
            myService.setAllSongs(SongList);
            myService.setSongListFrag1(SongList);

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

        playintent = new Intent(getActivity(), MyService.class);
        getActivity().startService(playintent);
        getActivity().bindService(playintent, musicConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (musicBound) {
            getActivity().unbindService(musicConnection);
            musicBound = false;
        }
    }

}