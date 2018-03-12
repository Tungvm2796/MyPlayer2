package samsung.com.myplayer2.Fragments;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

import samsung.com.myplayer2.Adapter.RecyclerAlbumAdapter;
import samsung.com.myplayer2.Class.Album;
import samsung.com.myplayer2.R;


/**
 * A simple {@link Fragment} subclass.
 */

public class AlbumFragment extends Fragment {


    public AlbumFragment() {
        // Required empty public constructor
    }

    Button clickme;
    Button clickmeback;
    private ArrayList<Album> albumList;
    RecyclerView albumView;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_album, container, false);
        clickme = (Button) v.findViewById(R.id.btnlay);
        clickmeback = (Button) v.findViewById(R.id.btnlay2);
        context = super.getActivity();
        albumView = (RecyclerView) v.findViewById(R.id.albumView);

        final LinearLayout lin1 = (LinearLayout)v.findViewById(R.id.lin1);
        final LinearLayout lin2 = (LinearLayout)v.findViewById(R.id.lin2);

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

        albumList = new ArrayList<>();
        getAlbumsLists();

        RecyclerView.LayoutManager mManager = new GridLayoutManager(getContext(), 2);
        albumView.setLayoutManager(mManager);
        RecyclerAlbumAdapter albumAdt = new RecyclerAlbumAdapter(albumList);
        albumView.setItemAnimator(new DefaultItemAnimator());
        albumView.setAdapter(albumAdt);

        return v;
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

}
