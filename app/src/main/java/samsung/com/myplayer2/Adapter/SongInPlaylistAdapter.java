package samsung.com.myplayer2.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.Collections;

import samsung.com.myplayer2.Class.Function;
import samsung.com.myplayer2.Class.ItemTouchHelperAdapter;
import samsung.com.myplayer2.Class.ItemTouchHelperViewHolder;
import samsung.com.myplayer2.Class.Song;
import samsung.com.myplayer2.Handler.DatabaseHandler;
import samsung.com.myplayer2.R;

public class SongInPlaylistAdapter extends RecyclerView.Adapter<SongInPlaylistAdapter.MyRecyclerSongHolder> implements ItemTouchHelperAdapter {

    private ArrayList<Song> songs;
    Context mContext;
    ArrayList<String> Namelist;
    ListView lv;
    Function function = new Function();
    private static String listId = "";

    public SongInPlaylistAdapter(Context context, ArrayList<Song> data) {
        this.mContext = context;
        this.songs = data;
    }

    public SongInPlaylistAdapter() {
    }

    public class MyRecyclerSongHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
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

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

    public SongInPlaylistAdapter.MyRecyclerSongHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //map to song layout
        View songView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song, parent, false);
        return new SongInPlaylistAdapter.MyRecyclerSongHolder(songView);
    }

    @Override
    public void onBindViewHolder(final SongInPlaylistAdapter.MyRecyclerSongHolder holder, final int position) {
        //get song using position
        final samsung.com.myplayer2.Class.Song currSong = songs.get(position);

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
                play.putExtra("pos", position);
                c.sendBroadcast(play);
            }
        });

        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPopUp(mContext, holder.btn, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    private void createPopUp(Context context, View view, final int curpos) {
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
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {

        if (fromPosition < songs.size() && toPosition < songs.size()) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(songs, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(songs, i, i - 1);
                }
            }

            DatabaseHandler db = new DatabaseHandler(mContext);
            db.SwapSongOfPlaylist(listId, Long.toString(songs.get(fromPosition).getID()), Long.toString(songs.get(toPosition).getID()));
            db.close();
            notifyItemMoved(fromPosition, toPosition);

            Intent change = new Intent("ToPlaylist");
            change.setAction("Changed");
            mContext.sendBroadcast(change);
        }

        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        songs.remove(position);
        notifyItemRemoved(position);
    }

    public void setListId(String id){
        listId = id;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }
}
