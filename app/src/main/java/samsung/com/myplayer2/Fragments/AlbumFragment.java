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
import android.widget.TextView;

import java.util.ArrayList;

import samsung.com.myplayer2.Activities.MainActivity;
import samsung.com.myplayer2.Adapter.RecyclerAlbumAdapter;
import samsung.com.myplayer2.Adapter.RecyclerSongAdapter;
import samsung.com.myplayer2.Class.Album;
import samsung.com.myplayer2.Class.Function;
import samsung.com.myplayer2.Class.Song;
import samsung.com.myplayer2.Class.ToolbarHidingOnScrollListener;
import samsung.com.myplayer2.R;
import samsung.com.myplayer2.Service.MyService;


/**
 * A simple {@link Fragment} subclass.
 */

public class AlbumFragment extends Fragment implements RecyclerAlbumAdapter.AlbumClickListener {


    public AlbumFragment() {
        // Required empty public constructor
    }

    MyService myService;
    private boolean musicBound = false;
    private Intent playIntent;
    Button clickme;
    Button clickmeback;
    private ArrayList<Album> albumList;
    private ArrayList<Song> songListTake;
    private ArrayList<Song> songListInAlbum;
    RecyclerView albumView;
    RecyclerView songOfAlbum;
    Context context;
    LinearLayout lin1;
    LinearLayout lin2;
    TextView xemid;
    Function function;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_album, container, false);
        function = new Function();
        clickme = (Button) v.findViewById(R.id.btnlay);
        clickmeback = (Button) v.findViewById(R.id.btnlay2);
        context = super.getActivity();
        albumView = (RecyclerView) v.findViewById(R.id.albumView);
        songOfAlbum = (RecyclerView) v.findViewById(R.id.song_of_album);
        xemid = (TextView) v.findViewById(R.id.xemAlbumId);

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

        songListInAlbum = new ArrayList<>();
        songListTake = new ArrayList<>();
        //function.getSongList(getActivity(), songListTake);
        songListTake = ((MainActivity)getActivity()).getAllSong();

        albumList = new ArrayList<>();
        function.getAlbumsLists(getActivity(), albumList);

        View tabcontainer = getActivity().findViewById(R.id.tabcontainer);
        View toolbar = getActivity().findViewById(R.id.toolbar);
        View lasttab = getActivity().findViewById(R.id.viewpagertab);
        View coloredBackgroundView = getActivity().findViewById(R.id.colored_background_view);

        RecyclerView.LayoutManager mManager = new GridLayoutManager(getContext(), 2);
        albumView.setLayoutManager(mManager);
        RecyclerAlbumAdapter albumAdt = new RecyclerAlbumAdapter(getContext(), albumList);
        albumAdt.setAlbumClickListener(this);
        albumView.setAdapter(albumAdt);
        albumView.addOnScrollListener(new ToolbarHidingOnScrollListener(getActivity(), tabcontainer, toolbar, lasttab, coloredBackgroundView));

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        songOfAlbum.setLayoutManager(manager);
        songOfAlbum.setAdapter(null);

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
    public void onAlbumClick(View view, int position) {
        lin1.setVisibility(View.INVISIBLE);
        lin2.setVisibility(View.VISIBLE);
        songListInAlbum.clear();

        for (int i = 0; i < songListTake.size(); i++)
            if (songListTake.get(i).getAlbumid() == albumList.get(position).getId())
                songListInAlbum.add(songListTake.get(i));

        xemid.setText(Long.toString(songListInAlbum.size()));

        songOfAlbum.setAdapter(null);
        RecyclerSongAdapter songAdt = new RecyclerSongAdapter(getContext(), songListInAlbum);
        songOfAlbum.setAdapter(songAdt);
        myService.setSongListFrag2(songListInAlbum);

        Intent intent = new Intent("ToActivity");
        intent.setAction("FragIndex");
        intent.putExtra("key", 2);
        getActivity().sendBroadcast(intent);
    }
}
