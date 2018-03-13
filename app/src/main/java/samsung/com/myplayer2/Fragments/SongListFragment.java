package samsung.com.myplayer2.Fragments;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
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
import java.util.Collections;
import java.util.Comparator;

import samsung.com.myplayer2.Adapter.RecyclerSongAdapter;
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
    private ArrayList<Song> SongFilterList;
    private RecyclerView songView;
    EditText searchbox;
    Context context;
    Animation animation;
    ImageButton nowplaying;
    int playornot = 0;

    MyService myService;
    private boolean musicBound = false;
    private Intent playintent;
    public static ImageButton btnPP;
    TextView textTimeSong;
    TextView textTotal;
    SeekBar seekBar;
    ImageButton btnsearch;

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

        songView = (RecyclerView) v.findViewById(R.id.song_list);

        btnPP = (ImageButton) getActivity().findViewById(R.id.btn_play_pause);

        btnsearch = (ImageButton) v.findViewById(R.id.btnsearch);

        textTimeSong = (TextView) getActivity().findViewById(R.id.time_song);

        textTotal = (TextView) getActivity().findViewById(R.id.time_total);

        seekBar = (SeekBar) getActivity().findViewById(R.id.seekbar_song);

        searchbox = (EditText) v.findViewById(R.id.searchbox);

        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.disc_rolate);

        context = super.getActivity();

        setRetainInstance(true);

        SongList = new ArrayList<Song>();

        SongFilterList = new ArrayList<Song>();

        getSongList();

        final RecyclerSongAdapter songAdt = new RecyclerSongAdapter(SongList);
        RecyclerView.LayoutManager mManager = new LinearLayoutManager(getContext());
        songView.setLayoutManager(mManager);
        songView.setItemAnimator(new DefaultItemAnimator());
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
                        RecyclerSongAdapter songAdapter1 = new RecyclerSongAdapter(SongFilterList);
                        songView.setAdapter(songAdapter1);
                        myService.setList(SongFilterList);
                    } else {
                        songView.setAdapter(null);
                        songView.setAdapter(songAdt);
                        myService.setList(SongList);
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
                            RecyclerSongAdapter songAdapter2 = new RecyclerSongAdapter(SongFilterList);
                            songView.setAdapter(songAdapter2);
                            myService.setList(SongFilterList);
                        } else {
                            songView.setAdapter(null);
                            songView.setAdapter(songAdt);
                            myService.setList(SongList);
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

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        if (bm == null)
            return bm;
        else {
            int width = bm.getWidth();
            int height = bm.getHeight();
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // CREATE A MATRIX FOR THE MANIPULATION
            Matrix matrix = new Matrix();
            // RESIZE THE BIT MAP
            matrix.postScale(scaleWidth, scaleHeight);

            // "RECREATE" THE NEW BITMAP
            Bitmap resizedBitmap = Bitmap.createBitmap(
                    bm, 0, 0, width, height, matrix, false);
            bm.recycle();
            return resizedBitmap;
        }
    }

    public void getSongList() {
        //retrieve song info
        ContentResolver musicResolver = getActivity().getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        final String album_id = MediaStore.Audio.Albums._ID;
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get collumn
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int artisColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int dataColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            //add song to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtis = musicCursor.getString(artisColumn);
                String thisData = musicCursor.getString(dataColumn);
                Bitmap songimg = GetBitmap(thisData);
                Bitmap lastimg = getResizedBitmap(songimg, 55, 60);
                long albumId = musicCursor.getLong(musicCursor.getColumnIndex(album_id));
                SongList.add(new Song(thisId, thisTitle, thisArtis, lastimg, thisData, albumId));
            }
            while (musicCursor.moveToNext());
        }
        SortByName();
        musicCursor.close();
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