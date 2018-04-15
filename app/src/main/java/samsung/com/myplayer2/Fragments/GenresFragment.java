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
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import samsung.com.myplayer2.Activities.MainActivity;
import samsung.com.myplayer2.Adapter.RecyclerGenresAdapter;
import samsung.com.myplayer2.Adapter.RecyclerSongAdapter;
import samsung.com.myplayer2.Class.Function;
import samsung.com.myplayer2.Class.Song;
import samsung.com.myplayer2.Class.ToolbarHidingOnScrollListener;
import samsung.com.myplayer2.R;
import samsung.com.myplayer2.Service.MyService;

/**
 * A simple {@link Fragment} subclass.
 */
public class GenresFragment extends Fragment implements RecyclerGenresAdapter.GenresClickListener {


    public GenresFragment() {
        // Required empty public constructor
    }

    MyService myService;
    private boolean musicBound = false;
    private Intent playIntent;

    RecyclerGenresAdapter recyclerGenresAdapter;
    RecyclerView genresView;
    ArrayList<String> genList;
    Function function;

    Toolbar toolbar;

    ArrayList<Song> AllSong;
    ArrayList<Song> songList;
    LinearLayout lin1;
    LinearLayout lin2;
    RecyclerView songOfGenres;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_genres, container, false);
        function = new Function();

        lin1 = (LinearLayout) v.findViewById(R.id.lin1);
        lin2 = (LinearLayout) v.findViewById(R.id.lin2);


        songList = new ArrayList<>();

        AllSong = new ArrayList<>();
        AllSong = ((MainActivity) getActivity()).getAllSong();

        genList = new ArrayList<>();
        function.getGenres(getContext(), genList);

        genresView = (RecyclerView) v.findViewById(R.id.genresView);

        songOfGenres = (RecyclerView) v.findViewById(R.id.song_of_genres);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        songOfGenres.setLayoutManager(manager);
        songOfGenres.setAdapter(null);

        View tabcontainer = getActivity().findViewById(R.id.tabcontainer);
        toolbar = getActivity().findViewById(R.id.toolbar);
        View lasttab = getActivity().findViewById(R.id.viewpagertab);
        View coloredBackgroundView = getActivity().findViewById(R.id.colored_background_view);

        recyclerGenresAdapter = new RecyclerGenresAdapter(getContext(), genList);
        recyclerGenresAdapter.setGenresClickListener(this);
        RecyclerView.LayoutManager mManager = new GridLayoutManager(getContext(), 2);
        genresView.setLayoutManager(mManager);
        genresView.setAdapter(recyclerGenresAdapter);
        genresView.addOnScrollListener(new ToolbarHidingOnScrollListener(getActivity(), tabcontainer, toolbar, lasttab, coloredBackgroundView));

        return v;
    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            MyService.MusicBinder binder = (MyService.MusicBinder) service;
            //get service
            myService = binder.getService();
            //pass list
            //myService.setList(AllSong);
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
    public void onGenresClick(View view, int position) {
        songList.clear();
        songOfGenres.setAdapter(null);

        lin1.setVisibility(View.INVISIBLE);
        lin2.setVisibility(View.VISIBLE);

        for (int i = 0; i < AllSong.size(); i++) {
            if (AllSong.get(i).getGenres() != null) {
                if (AllSong.get(i).getGenres().equals(genList.get(position)))
                    songList.add(AllSong.get(i));
            }
        }

        RecyclerSongAdapter newSongAdt = new RecyclerSongAdapter(getContext(), songList);
        songOfGenres.setAdapter(newSongAdt);
        myService.setSongListFrag5(songList);
    }
}
