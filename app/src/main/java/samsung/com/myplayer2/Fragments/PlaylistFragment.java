package samsung.com.myplayer2.Fragments;


import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import samsung.com.myplayer2.Adapter.RecyclerPlaylistAdapter;
import samsung.com.myplayer2.Adapter.RecyclerSongAdapter;
import samsung.com.myplayer2.Class.Function;
import samsung.com.myplayer2.Class.Playlist;
import samsung.com.myplayer2.Class.Song;
import samsung.com.myplayer2.Handler.DatabaseHandler;
import samsung.com.myplayer2.R;
import samsung.com.myplayer2.Service.MyService;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistFragment extends Fragment implements RecyclerPlaylistAdapter.ItemClickListener {


    public PlaylistFragment() {
        // Required empty public constructor
    }

    MyService myService;
    private boolean musicBound = false;
    private Intent playIntent;
    RecyclerView playListView;
    RecyclerView songInPlaylist;
    Button btnViewSong;
    Button btnLay2;
    ImageButton btnAdd;
    ArrayList<Playlist> playlists;
    EditText edt;
    RecyclerPlaylistAdapter PlaylistAdapter;
    RecyclerSongAdapter songAdapterPlaylist;
    LinearLayout lin1;
    LinearLayout lin2;
    ArrayList<Song> songOfPlaylist;
    ArrayList<Song> AllSong;
    ArrayList<String> songIdArray;
    int pos;
    Function function;
    DatabaseHandler db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_playlist, container, false);

        function = new Function();

        btnViewSong = (Button) v.findViewById(R.id.btnSonginList);
        playListView = (RecyclerView) v.findViewById(R.id.PlayListView);
        btnAdd = (ImageButton) v.findViewById(R.id.btnAddPlaylist);
        btnLay2 = (Button) v.findViewById(R.id.btnlay2);
        songInPlaylist = (RecyclerView) v.findViewById(R.id.song_in_playlist);

        lin1 = (LinearLayout) v.findViewById(R.id.lin1);
        lin2 = (LinearLayout) v.findViewById(R.id.lin2);

        db = new DatabaseHandler(getActivity());
        playlists = db.getAllList();
        songIdArray = new ArrayList<>();
        songOfPlaylist = new ArrayList<>();
        AllSong = new ArrayList<>();
        function.getSongList(getActivity(), AllSong);


        RecyclerView.LayoutManager mManager = new GridLayoutManager(getContext(), 2);
        playListView.setLayoutManager(mManager);
        PlaylistAdapter = new RecyclerPlaylistAdapter(getActivity(), playlists);
        PlaylistAdapter.setClickListener(this);
        playListView.setAdapter(PlaylistAdapter);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        alertDialog.setTitle("Add new Playlist");
        alertDialog.setMessage("Insert Playlist Name");

        edt = new EditText(getActivity());
        alertDialog.setView(edt);

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (edt.getText().toString().trim().length() != 0) {
                    if (!db.CheckPlaylistExist(edt.getText().toString())) {
                        db.addPlaylist(new Playlist(Integer.toString(db.getMaxId() + 1), edt.getText().toString()));
                        playlists.clear();
                        playlists = db.getAllList();
                        PlaylistAdapter = new RecyclerPlaylistAdapter(getActivity(), playlists);
                        PlaylistAdapter.setClickListener(PlaylistFragment.this);
                        playListView.setAdapter(PlaylistAdapter);
                        Toast.makeText(getActivity(), "Added new Playlist", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Playlist Already Exist !", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please enter Playlist Name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final AlertDialog alert = alertDialog.create();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.show();
            }
        });

        btnViewSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lin1.setVisibility(View.INVISIBLE);
                lin2.setVisibility(View.VISIBLE);
            }
        });

        btnLay2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lin1.setVisibility(View.VISIBLE);
                lin2.setVisibility(View.INVISIBLE);
            }
        });

        return v;
    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            MyService.MusicBinder binder = (MyService.MusicBinder) service;
            //get service
            myService = binder.getService();
            //pass list
            //myService.setList(songListTake);
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
    public void onItemClick(View view, int position) {
        lin1.setVisibility(View.INVISIBLE);
        lin2.setVisibility(View.VISIBLE);

        songIdArray.clear();
        songInPlaylist.setAdapter(null);
        songOfPlaylist.clear();

        songIdArray = db.GetSongIdArray(playlists.get(position).getListid());

        for (int i=0; i<songIdArray.size(); i++){
            for (int j=0; j<AllSong.size(); j++){
                if(songIdArray.get(i).equals(Long.toString(AllSong.get(j).getID()))) {
                    songOfPlaylist.add(AllSong.get(j));
                    break;
                }
            }
        }

        songAdapterPlaylist = new RecyclerSongAdapter(getActivity(), songOfPlaylist);
        RecyclerView.LayoutManager mManager = new LinearLayoutManager(getContext());
        songInPlaylist.setLayoutManager(mManager);
        songInPlaylist.setAdapter(songAdapterPlaylist);
        myService.setSongListFrag4(songOfPlaylist);
    }

    @Override
    public void onItemLongClick(View view, int position) {
        registerForContextMenu(playListView);
        pos = position;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select The Action");
        menu.add(0, v.getId(), 0, "Change Playlist Name");//groupId, itemId, order, title
        menu.add(0, v.getId(), 0, "Delete Playlist");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Change Playlist Name")
            Toast.makeText(getActivity(), "Will be done later", Toast.LENGTH_SHORT).show();
        else if (item.getTitle() == "Delete Playlist") {
            AlertDialog.Builder aat = new AlertDialog.Builder(getActivity());
            aat.setTitle("Delete ?")
                    .setMessage("Are you sure to delete ?")
                    .setCancelable(true)
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub

                                    db.deletePlaylist(playlists.get(pos));
                                    playlists = db.getAllList();
                                    PlaylistAdapter = new RecyclerPlaylistAdapter(getActivity(), playlists);
                                    PlaylistAdapter.setClickListener(PlaylistFragment.this);
                                    playListView.setAdapter(PlaylistAdapter);
                                    Toast.makeText(getActivity(), "Playlist removed", Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
            AlertDialog art = aat.create();
            art.show();
        } else {
            return false;
        }
        return true;
    }
}
