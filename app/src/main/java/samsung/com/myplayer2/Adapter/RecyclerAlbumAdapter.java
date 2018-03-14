package samsung.com.myplayer2.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import samsung.com.myplayer2.Class.Album;
import samsung.com.myplayer2.R;

/**
 * Created by 450G4 on 3/12/2018.
 */

public class RecyclerAlbumAdapter extends RecyclerView.Adapter<RecyclerAlbumAdapter.MyRecyclerHolder2>{

    private ArrayList<Album> albumList;
    private ItemClickListener mClickListener;
    Context mContext;

    public class MyRecyclerHolder2 extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView albumName, albumArtist;
        ImageView albumImg;
        public MyRecyclerHolder2(View albumLay){
            super(albumLay);

            albumName = (TextView) albumLay.findViewById(R.id.album_name);
            albumArtist = (TextView) albumLay.findViewById(R.id.album_artist);
            albumImg = (ImageView) albumLay.findViewById(R.id.album_img);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if(mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public RecyclerAlbumAdapter(Context context, ArrayList<Album> albumList) {
        this.mContext = context;
        this.albumList = albumList;
    }

    @Override
    public MyRecyclerHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
        View albumView = LayoutInflater.from(parent.getContext()).inflate(R.layout.albums, parent, false);
        return new MyRecyclerHolder2(albumView);
    }

    @Override
    public void onBindViewHolder(MyRecyclerHolder2 holder, int position) {
        Album curAlbum = albumList.get(position);

        holder.albumName.setText(curAlbum.getAlbumName());
        holder.albumArtist.setText(curAlbum.getArtistName());
        holder.albumImg.setImageBitmap(curAlbum.getAlbumImg());

    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
