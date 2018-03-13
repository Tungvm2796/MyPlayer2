package samsung.com.myplayer2.Fragments;


import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
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

import samsung.com.myplayer2.Adapter.RecyclerAlbumAdapter;
import samsung.com.myplayer2.Adapter.RecyclerSongAdapter;
import samsung.com.myplayer2.Class.Album;
import samsung.com.myplayer2.Class.Song;
import samsung.com.myplayer2.R;
import samsung.com.myplayer2.Service.MyService;


/**
 * A simple {@link Fragment} subclass.
 */

public class AlbumFragment extends Fragment implements RecyclerAlbumAdapter.ItemClickListener{


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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_album, container, false);
        clickme = (Button) v.findViewById(R.id.btnlay);
        clickmeback = (Button) v.findViewById(R.id.btnlay2);
        context = super.getActivity();
        albumView = (RecyclerView) v.findViewById(R.id.albumView);
        songOfAlbum = (RecyclerView) v.findViewById(R.id.song_of_album);
        xemid = (TextView) v.findViewById(R.id.xemAlbumId);

        lin1 = (LinearLayout)v.findViewById(R.id.lin1);
        lin2 = (LinearLayout)v.findViewById(R.id.lin2);

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
        getSongList();
        albumList = new ArrayList<>();
        getAlbumsLists();

        RecyclerView.LayoutManager mManager = new GridLayoutManager(getContext(), 2);
        albumView.setLayoutManager(mManager);
        RecyclerAlbumAdapter albumAdt = new RecyclerAlbumAdapter(albumList);
        albumAdt.setClickListener(this);
        albumView.setAdapter(albumAdt);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        songOfAlbum.setLayoutManager(manager);
        songOfAlbum.setAdapter(null);

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
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get collumn
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int artisColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int dataColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int albumIdColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            //add song to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtis = musicCursor.getString(artisColumn);
                String thisData = musicCursor.getString(dataColumn);
                Bitmap songimg = GetBitmap(thisData);
                Bitmap lastimg = getResizedBitmap(songimg, 55, 60);
                long albumId = musicCursor.getLong(albumIdColumn);
                songListTake.add(new Song(thisId, thisTitle, thisArtis, lastimg, thisData, albumId));
            }
            while (musicCursor.moveToNext());
        }
        musicCursor.close();
    }

    public void getAlbumsLists(){
        String where = null;

        final Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        final String _id = MediaStore.Audio.Albums._ID;
        final String album_name = MediaStore.Audio.Albums.ALBUM;
        final String artist = MediaStore.Audio.Albums.ARTIST;
        final String albumart = MediaStore.Audio.Albums.ALBUM_ART;
        final String tracks = MediaStore.Audio.Albums.NUMBER_OF_SONGS;

        final String[] columns = { _id, album_name, artist, albumart, tracks };
        Cursor cursor = getActivity().getContentResolver().query(uri, columns, where, null, null);

        if(cursor!=null && cursor.moveToFirst()){

            do {

                long id = cursor.getLong(cursor.getColumnIndex(_id));
                String name = cursor.getString(cursor.getColumnIndex(album_name));
                String artist2 = cursor.getString(cursor.getColumnIndex(artist));
                String artPath = cursor.getString(cursor.getColumnIndex(albumart));
                Bitmap art = BitmapFactory.decodeFile(artPath);
                int nr =Integer.parseInt(cursor.getString(cursor.getColumnIndex(tracks)));

                albumList.add(new Album(id, name, artist2, nr, art));

            } while (cursor.moveToNext());
        }

        cursor.close();
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
    public void onItemClick(View view, int position) {
        lin1.setVisibility(View.INVISIBLE);
        lin2.setVisibility(View.VISIBLE);
        songListInAlbum.clear();

                for(int i=0; i<songListTake.size(); i++)
                    if(songListTake.get(i).getAlbumid() == albumList.get(position).getId())
                        songListInAlbum.add(songListTake.get(i));

        xemid.setText(Long.toString(songListInAlbum.size()));

        songOfAlbum.setAdapter(null);
        RecyclerSongAdapter songAdt = new RecyclerSongAdapter(songListInAlbum);
        songOfAlbum.setAdapter(songAdt);
        myService.setList(songListInAlbum);
    }
}
