package samsung.com.myplayer2.Adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.ArrayList;

import samsung.com.myplayer2.Class.Playlist;
import samsung.com.myplayer2.R;

/**
 * Created by 450G4 on 3/18/2018.
 */

public class RecyclerPlaylistAdapter extends RecyclerView.Adapter<RecyclerPlaylistAdapter.MyRecyclerPlaylistHolder> {

    private ArrayList<Playlist> playList;
    private ItemClickListener mClickListener;
    Context mContext;

    public RecyclerPlaylistAdapter(Context context, ArrayList<Playlist> PList) {
        this.mContext = context;
        this.playList = PList;
    }

    public class MyRecyclerPlaylistHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView ListName;
        ImageView ListImg;

        public MyRecyclerPlaylistHolder(View ListLay) {
            super(ListLay);

            ListName = (TextView) ListLay.findViewById(R.id.list_name);
            ListImg = (ImageView) ListLay.findViewById(R.id.list_img);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onPlaylistClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            if (mClickListener != null)
                mClickListener.onPlaylistLongClick(view, getAdapterPosition());
            return false;
        }
    }

    @Override
    public MyRecyclerPlaylistHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View PlayListView = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist, parent, false);
        return new MyRecyclerPlaylistHolder(PlayListView);
    }

    @Override
    public void onBindViewHolder(MyRecyclerPlaylistHolder holder, int position) {
        Playlist curPlayList = playList.get(position);

        holder.ListName.setText(curPlayList.getName());

        TypedArray images = mContext.getResources().obtainTypedArray(R.array.array_drawables);
        int choice = (int) (Math.random() * images.length());
        GlideDrawableImageViewTarget target = new GlideDrawableImageViewTarget(holder.ListImg);
        Glide.with(mContext).load(images.getResourceId(choice, R.drawable.playlist)).into(target);
        images.recycle();
    }

    @Override
    public int getItemCount() {
        return playList.size();
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onPlaylistClick(View view, int position);

        void onPlaylistLongClick(View view, int position);
    }

}
