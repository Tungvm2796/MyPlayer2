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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import samsung.com.myplayer2.Adapter.RecyclerPlaylistAdapter;
import samsung.com.myplayer2.Class.Playlist;
import samsung.com.myplayer2.Handler.DatabaseHandler;
import samsung.com.myplayer2.R;
import samsung.com.myplayer2.Service.MyService;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistFragment extends Fragment implements RecyclerPlaylistAdapter.ItemClickListener{


    public PlaylistFragment() {
        // Required empty public constructor
    }

    MyService myService;
    private boolean musicBound = false;
    private Intent playIntent;
    RecyclerView playListView;
    Button btnAdd;
    ArrayList<Playlist> playlists;
    EditText edt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_playlist, container, false);

        btnAdd = (Button) v.findViewById(R.id.btnSonginList);
        playListView = (RecyclerView) v.findViewById(R.id.PlayListView);

        final DatabaseHandler db = new DatabaseHandler(getActivity());
        playlists = db.getAllList();

        RecyclerView.LayoutManager mManager = new GridLayoutManager(getContext(), 2);
        playListView.setLayoutManager(mManager);
        RecyclerPlaylistAdapter recyclerPlaylistAdapter = new RecyclerPlaylistAdapter(getActivity(), playlists);
        recyclerPlaylistAdapter.setClickListener(this);
        playListView.setAdapter(recyclerPlaylistAdapter);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        alertDialog.setTitle("Add new Playlist");
        alertDialog.setMessage("Insert Playlist Name");

        edt = new EditText(getActivity());
        alertDialog.setView(edt);

        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getActivity(), "Clicked Ok", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.setNegativeButton("Cancel", null);

         final AlertDialog alert = alertDialog.create();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.show();
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

    }
}
