package samsung.com.myplayer2.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import samsung.com.myplayer2.Class.Album;
import samsung.com.myplayer2.Class.Function;
import samsung.com.myplayer2.R;

/**
 * Created by 450G4 on 3/12/2018.
 */

public class RecyclerAlbumAdapter extends RecyclerView.Adapter<RecyclerAlbumAdapter.MyRecyclerAlbumHolder> {

    private ArrayList<Album> albumList;
    private AlbumClickListener mClickListener;
    Context mContext;
    Function function = new Function();

    public RecyclerAlbumAdapter(Context context, ArrayList<Album> albumList) {
        this.mContext = context;
        this.albumList = albumList;
    }

    public class MyRecyclerAlbumHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView albumName, albumArtist;
        ImageView albumImg;

        public MyRecyclerAlbumHolder(View albumLay) {
            super(albumLay);

            albumName = albumLay.findViewById(R.id.album_name);
            albumArtist = albumLay.findViewById(R.id.album_artist);
            albumImg = albumLay.findViewById(R.id.album_img);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onAlbumClick(view, getAdapterPosition());
        }
    }

    @Override
    public MyRecyclerAlbumHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View albumView = LayoutInflater.from(parent.getContext()).inflate(R.layout.albums, parent, false);
        return new MyRecyclerAlbumHolder(albumView);
    }

    @Override
    public void onBindViewHolder(MyRecyclerAlbumHolder holder, int position) {
        Album curAlbum = albumList.get(position);

        holder.albumName.setText(curAlbum.getAlbumName());
        holder.albumArtist.setText(curAlbum.getArtistName());
        Glide.with(mContext).load(function.BitmapToByte(curAlbum.getAlbumImg())).into(holder.albumImg);

    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    // allows clicks events to be caught
    public void setAlbumClickListener(AlbumClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface AlbumClickListener {
        void onAlbumClick(View view, int position);
    }
}
