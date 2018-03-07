package samsung.com.myplayer2.Fragments;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import samsung.com.myplayer2.Adapter.SongAdapter;
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

    public void SortByName() {
        Collections.sort(SongList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
    }

    private ArrayList<Song> SongList;
    private ListView songView;
    EditText searchbox;
    Context context;
    Animation animation;
    ImageButton nowplaying;
    int playornot = 0;

    MyService myService;
    private boolean musicBound = false;
    private Intent playintent;
    ImageButton btnPP;
    TextView textTitle;
    TextView textArtist;
    TextView textTimeSong;
    TextView textTotal;
    SeekBar seekBar;

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

        songView = (ListView) v.findViewById(R.id.song_list);

        btnPP = (ImageButton) getActivity().findViewById(R.id.btn_play_pause);

        textTimeSong = (TextView) getActivity().findViewById(R.id.time_song);

        textTotal = (TextView) getActivity().findViewById(R.id.time_total);

        seekBar = (SeekBar) getActivity().findViewById(R.id.seekbar_song);

        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.disc_rolate);

        SongList = new ArrayList<Song>();

        context = super.getActivity();

        getSongList();

        final SongAdapter songAdt = new SongAdapter(getActivity(), SongList);

        songView.setAdapter(songAdt);

        searchbox = (EditText) v.findViewById(R.id.searchbox);

        searchbox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    SongList.clear();
                    getSongList();
                    SongAdapter songAdapter1 = new SongAdapter(getActivity(), SongList);
                    songView.setAdapter(songAdapter1);
                } else {
                    songView.setAdapter(null);
                    SongList.clear();
                    getSongByName(charSequence.toString().toLowerCase());
                    SongAdapter songAdapter2 = new SongAdapter(getActivity(), SongList);
                    songView.setAdapter(songAdapter2);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        songView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                btnPP.setImageResource(R.drawable.ic_pause_circle_outline_white_24dp);
                myService.setSong(position);
                myService.playSong();
                SetTimeTotal();
                UpdateTimeSong();

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

    public void getSongList() {
        //retrieve song info
        ContentResolver musicResolver = getActivity().getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get collumn
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int artisColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int dataColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            long coverid = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            //add song to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtis = musicCursor.getString(artisColumn);
                String thisData = musicCursor.getString(dataColumn);
                Uri solidUri = Uri.parse("content://media/external/audio/");
                Uri songUri = ContentUris.withAppendedId(solidUri, thisId);
                SongList.add(new Song(thisId, thisTitle, thisArtis, null, thisData));
            }
            while (musicCursor.moveToNext());
        }
        SortByName();
    }

    public void getSongByName(String entry) {
        //retrieve song info
        ContentResolver musicResolver = getActivity().getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get collumn
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int artisColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int dataColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            long coverid = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            //add song to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtis = musicCursor.getString(artisColumn);
                String thisData = musicCursor.getString(dataColumn);
                Uri solidUri = Uri.parse("content://media/external/audio/");
                Uri songUri = ContentUris.withAppendedId(solidUri, thisId);
                if (thisTitle.toLowerCase().contains(entry))
                    SongList.add(new Song(thisId, thisTitle, thisArtis, null, thisData));
            }
            while (musicCursor.moveToNext());
        }
        SortByName();
    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            MyService.MusicBinder binder = (MyService.MusicBinder) service;
            //get service
            myService = binder.getService();
            //pass list
            myService.setList(SongList);
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