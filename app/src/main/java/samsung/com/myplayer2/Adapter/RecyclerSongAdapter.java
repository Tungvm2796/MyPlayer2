package samsung.com.myplayer2.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import samsung.com.myplayer2.Class.Function;
import samsung.com.myplayer2.Class.Song;
import samsung.com.myplayer2.Handler.DatabaseHandler;
import samsung.com.myplayer2.R;

/**
 * Created by 450G4 on 3/10/2018.
 */

public class RecyclerSongAdapter extends RecyclerView.Adapter<RecyclerSongAdapter.MyRecyclerSongHolder> {


    private ArrayList<samsung.com.myplayer2.Class.Song> songs;
    Context mContext;
    ArrayList<String> Namelist;
    ListView lv;
    private static int onPlaylist = 0;
    Function function = new Function();

    public RecyclerSongAdapter(Context context, ArrayList<Song> data) {
        this.mContext = context;
        this.songs = data;
    }

    public RecyclerSongAdapter() {
    }

    public class MyRecyclerSongHolder extends RecyclerView.ViewHolder {
        TextView songView, artistView;
        ImageView coverimg;
        Button btn;

        public MyRecyclerSongHolder(View songLay) {
            super(songLay);
            //get title and artist views
            songView = songLay.findViewById(R.id.song_title);
            artistView = songLay.findViewById(R.id.song_artist);
            coverimg = songLay.findViewById(R.id.coverImg);
            btn = songLay.findViewById(R.id.menuSong);
        }
    }

    public MyRecyclerSongHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //map to song layout
        View songView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song, parent, false);
        return new MyRecyclerSongHolder(songView);
    }

    @Override
    public void onBindViewHolder(final MyRecyclerSongHolder holder, int position) {
        //get song using position
        final samsung.com.myplayer2.Class.Song currSong = songs.get(position);
        final int pos = position;

        //get title and artist strings, embedded pictures
        holder.songView.setText(currSong.getTitle());
        holder.artistView.setText(currSong.getArtist());

        Glide.with(mContext).load(function.GetBitMapByte(currSong.getData()))
                .override(57, 63)
                .diskCacheStrategy(DiskCacheStrategy.RESULT).skipMemoryCache(true)
                .error(R.drawable.noteicon)
                .into(holder.coverimg);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context c = view.getContext();
                Intent play = new Intent("ToService");
                play.setAction("SvPlayOne");
                play.putExtra("pos", pos);
                c.sendBroadcast(play);
            }
        });

        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPopUp(mContext, holder.btn, onPlaylist, pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public void setOnPlaylist(int input) {
        onPlaylist = input;
    }

    private void createPopUp(Context context, View view, int index, final int curpos) {

        switch (index) {

            case 0:
                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, view);
                //inflating menu from xml resource
                popup.inflate(R.menu.popup_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.action1:

                                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View customview = inflater.inflate(R.layout.alert_layout, null);
                                lv = customview.findViewById(R.id.listPlaylist);
                                Namelist = new ArrayList<>();
                                final DatabaseHandler db = new DatabaseHandler(mContext);
                                Namelist = db.GetAllPlaylistName();
                                SongAdapter adapter = new SongAdapter(mContext, Namelist);
                                lv.setAdapter(adapter);

                                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                                        long idsong = songs.get(curpos).getID();
                                        String idpl = db.getIdByName(Namelist.get(pos));
                                        if (!db.CheckSongAdded(Long.toString(idsong), idpl)) {
                                            db.addSongToPlaylist(Long.toString(idsong), idpl);
                                            Toast.makeText(mContext, "Song added", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(mContext, "Song already Exist !", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
                                alertDialog.setTitle("Add To Playlist");
                                alertDialog.setMessage("Select Playlist Name");

                                alertDialog.setView(customview);

                                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });

                                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                });

                                AlertDialog alert = alertDialog.create();
                                alert.show();

                                break;

                            case R.id.action2:

                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
                break;

            case 1:
                //creating a popup menu
                PopupMenu popup2 = new PopupMenu(context, view);
                //inflating menu from xml resource
                popup2.inflate(R.menu.popup_menu_in_playlist);
                //adding click listener
                popup2.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.action_in_pl_1:
                                long idsong = songs.get(curpos).getID();

                                Intent remove = new Intent("ToPlaylist");
                                remove.setAction("Remove");
                                remove.putExtra("songid", idsong);
                                mContext.sendBroadcast(remove);

                                break;

                            case R.id.action_in_pl_2:
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup2.show();
                break;
        }
    }
}
