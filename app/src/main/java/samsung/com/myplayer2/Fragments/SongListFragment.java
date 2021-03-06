package samsung.com.myplayer2.Fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import samsung.com.myplayer2.Activities.MainActivity;
import samsung.com.myplayer2.Adapter.RecyclerSongAdapter;
import samsung.com.myplayer2.Class.Function;
import samsung.com.myplayer2.Class.Song;
import samsung.com.myplayer2.Class.ToolbarHidingOnScrollListener;
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
    //private ArrayList<Song> SongFilterList;
    private RecyclerView songView;
    //    EditText searchbox;
    Context context;
    //Animation animation;

    MyService myService;
    private boolean musicBound = false;
    private Intent playintent;
//    ImageButton btnsearch;

    Toolbar toolbar;

    Function function;

//    public void SetTimeTotal() {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
//        textTotal.setText(simpleDateFormat.format(myService.getDur()));
//        seekBar.setMax(myService.getDur());
//    }
//
//    public void UpdateTimeSong() {
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
//                textTimeSong.setText(simpleDateFormat.format(myService.getPosn()));
//                seekBar.setProgress(myService.getPosn());
//                handler.postDelayed(this, 500);
//            }
//        }, 100);
//    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_song_list, container, false);

        function = new Function();

        songView =  v.findViewById(R.id.song_list);

//        btnsearch = (ImageButton) v.findViewById(R.id.btnsearch);

//        searchbox = (EditText) v.findViewById(R.id.searchbox);

        //animation = AnimationUtils.loadAnimation(getActivity(), R.anim.disc_rolate);

        context = v.getContext();

        setRetainInstance(true);

        SongList = new ArrayList<>();

        //SongFilterList = new ArrayList<>();

//        function.getSongList(getActivity(), SongList);
        SongList = ((MainActivity) getActivity()).getAllSong();

        View tabcontainer = getActivity().findViewById(R.id.tabcontainer);
        toolbar = getActivity().findViewById(R.id.toolbar);
        View lasttab = getActivity().findViewById(R.id.viewpagertab);
        View coloredBackgroundView = getActivity().findViewById(R.id.colored_background_view);

        final RecyclerSongAdapter songAdt = new RecyclerSongAdapter(getContext(), SongList);
        RecyclerView.LayoutManager mManager = new LinearLayoutManager(getContext());
        songView.setLayoutManager(mManager);
        songView.setAdapter(songAdt);
        songView.addOnScrollListener(new ToolbarHidingOnScrollListener(getActivity(), tabcontainer, toolbar, lasttab, coloredBackgroundView));


//        searchbox.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (charSequence.length() == 0) {
//                    //SongList.clear();
//                    //getSongList();
//                    //RecyclerSongAdapter songAdapter1 = new RecyclerSongAdapter(SongList);
//                    //songView.setAdapter(songAdapter1);
//                } else {
//                    songView.setAdapter(null);
//                    SongList.clear();
//                    getSongByName(charSequence.toString().toLowerCase());
//                    RecyclerSongAdapter songAdapter2 = new RecyclerSongAdapter(SongList);
//                    songView.setAdapter(songAdapter2);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//            }
//        });
//
//        searchbox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                     Write your logic here that will be executed when user taps next button
//                    if (searchbox.getText() != null) {
//                        songView.setAdapter(null);
//                        SongFilterList.clear();
//                        getSongByName(searchbox.getText().toString().toLowerCase(), SongList);
//                        RecyclerSongAdapter songAdapter1 = new RecyclerSongAdapter(getContext(), SongFilterList);
//                        songView.setAdapter(songAdapter1);
//                        myService.setSongListFrag1(SongFilterList);
//                    } else {
//                        songView.setAdapter(null);
//                        songView.setAdapter(songAdt);
//                        myService.setSongListFrag1(SongList);
//                    }
//                }
//                return false;
//            }
//        });


//        btnsearch.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                switch (motionEvent.getAction()) {
//                    case MotionEvent.ACTION_DOWN: {
//                        ImageButton v = (ImageButton) view;
//                        v.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
//                        view.invalidate();
//                        break;
//                    }
//                    case MotionEvent.ACTION_UP:
//
//                        // Your action here on button click
//                        if (searchbox.getText() != null) {
//                            songView.setAdapter(null);
//                            SongFilterList.clear();
//                            getSongByName(searchbox.getText().toString().toLowerCase(), SongList);
//                            RecyclerSongAdapter songAdapter2 = new RecyclerSongAdapter(getContext(), SongFilterList);
//                            songView.setAdapter(songAdapter2);
//                            myService.setSongListFrag1(SongFilterList);
//                        } else {
//                            songView.setAdapter(null);
//                            songView.setAdapter(songAdt);
//                            myService.setSongListFrag1(SongList);
//                        }
//
//
//                    case MotionEvent.ACTION_CANCEL: {
//                        ImageButton v = (ImageButton) view;
//                        v.getBackground().clearColorFilter();
//                        v.invalidate();
//                        break;
//                    }
//                }
//                return false;
//            }
//        });

        return v;
    }


//    public void getSongByName(String entry, ArrayList<Song> entryArray) {
//        for (int i = 0; i < entryArray.size(); i++) {
//            if (entryArray.get(i).getTitle().toLowerCase().contains(entry) || entryArray.get(i).getArtist().toLowerCase().contains(entry))
//                SongFilterList.add(entryArray.get(i));
//        }
//    }

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