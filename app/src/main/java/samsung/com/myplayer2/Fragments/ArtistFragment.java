package samsung.com.myplayer2.Fragments;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

import samsung.com.myplayer2.Activities.MainActivity;
import samsung.com.myplayer2.Adapter.RecyclerArtistAdapter;
import samsung.com.myplayer2.Adapter.RecyclerSongAdapter;
import samsung.com.myplayer2.Class.Artist;
import samsung.com.myplayer2.Class.Function;
import samsung.com.myplayer2.Class.Song;
import samsung.com.myplayer2.Class.ToolbarHidingOnScrollListener;
import samsung.com.myplayer2.R;
import samsung.com.myplayer2.Service.MyService;


/**
 * A simple {@link Fragment} subclass.
 */

public class ArtistFragment extends Fragment implements RecyclerArtistAdapter.ArtistClickListener {


    public ArtistFragment() {
        // Required empty public constructor
    }

    MyService myService;
    private boolean musicBound = false;
    private Intent playIntent;
    Button clickme;
    Button clickmeback;
    private ArrayList<Artist> artists;
    private ArrayList<Song> songListTake;
    private ArrayList<Song> songListOfArtist;
    RecyclerView artistView;
    RecyclerView songOfArtist;
    Context context;
    LinearLayout lin1;
    LinearLayout lin2;
    Function function;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_artist, container, false);
        function = new Function();
        clickme = (Button) v.findViewById(R.id.btnlay);
        clickmeback = (Button) v.findViewById(R.id.btnlay2);
        context = super.getActivity();
        artistView = (RecyclerView) v.findViewById(R.id.artistView);
        songOfArtist = (RecyclerView) v.findViewById(R.id.song_of_artist);

        lin1 = (LinearLayout) v.findViewById(R.id.lin1);
        lin2 = (LinearLayout) v.findViewById(R.id.lin2);

        setRetainInstance(true);

        clickme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lin1.setVisibility(View.INVISIBLE);
                lin2.setVisibility(View.VISIBLE);
            }
        });

        clickmeback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lin1.setVisibility(View.VISIBLE);
                lin2.setVisibility(View.INVISIBLE);
            }
        });

        songListOfArtist = new ArrayList<>();

        songListTake = new ArrayList<>();
        //function.getSongList(getActivity(), songListTake);
        songListTake = ((MainActivity)getActivity()).getAllSong();

        artists = new ArrayList<>();
        function.getArtist(getActivity(), artists);

        View tabcontainer = getActivity().findViewById(R.id.tabcontainer);
        View toolbar = getActivity().findViewById(R.id.toolbar);
        View lasttab = getActivity().findViewById(R.id.viewpagertab);
        View coloredBackgroundView = getActivity().findViewById(R.id.colored_background_view);

        RecyclerView.LayoutManager mManager = new GridLayoutManager(getContext(), 2);
        artistView.setLayoutManager(mManager);
        RecyclerArtistAdapter artistAdt = new RecyclerArtistAdapter(getContext(), artists);
        artistAdt.setArtistClickListener(this);
        artistView.setAdapter(artistAdt);
        artistView.addOnScrollListener(new ToolbarHidingOnScrollListener(getActivity(), tabcontainer, toolbar, lasttab, coloredBackgroundView));

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        songOfArtist.setLayoutManager(manager);
        songOfArtist.setAdapter(null);

        return v;
    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            MyService.MusicBinder binder = (MyService.MusicBinder) service;
            //get service
            myService = binder.getService();
            //pass list
            myService.setList(songListTake);
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
        playIntent = new Intent(getActivity(), MyService.class);
        getActivity().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (musicBound) {
            getActivity().unbindService(musicConnection);
            musicBound = false;
        }
    }

    @Override
    public void onArtistClick(View view, int position) {
        lin1.setVisibility(View.INVISIBLE);
        lin2.setVisibility(View.VISIBLE);
        songListOfArtist.clear();

        for (int i = 0; i < songListTake.size(); i++)
            if (songListTake.get(i).getArtist().equals(artists.get(position).getName()))
                songListOfArtist.add(songListTake.get(i));

        songOfArtist.setAdapter(null);
        RecyclerSongAdapter songAdt = new RecyclerSongAdapter(getContext(), songListOfArtist);
        songOfArtist.setAdapter(songAdt);
        myService.setSongListFrag3(songListOfArtist);
    }
}
