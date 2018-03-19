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

import samsung.com.myplayer2.Class.Playlist;
import samsung.com.myplayer2.R;

/**
 * Created by 450G4 on 3/18/2018.
 */

public class RecyclerPlaylistAdapter extends RecyclerView.Adapter<RecyclerPlaylistAdapter.MyRecyclerHolder3> {

    private ArrayList<Playlist> playList;
    private ItemClickListener mClickListener;
    Context mContext;

    public RecyclerPlaylistAdapter(Context context, ArrayList<Playlist> PList) {
        this.mContext = context;
        this.playList = PList;
    }

    public class MyRecyclerHolder3 extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView ListName;
        ImageView ListImg;

        public MyRecyclerHolder3(View ListLay) {
            super(ListLay);

            ListName = (TextView) ListLay.findViewById(R.id.list_name);
            ListImg = (ImageView) ListLay.findViewById(R.id.list_img);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemLongClick(view, getAdapterPosition());
            return false;
        }
    }

    @Override
    public MyRecyclerHolder3 onCreateViewHolder(ViewGroup parent, int viewType) {
        View PlayListView = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist, parent, false);
        return new MyRecyclerHolder3(PlayListView);
    }

    @Override
    public void onBindViewHolder(MyRecyclerHolder3 holder, int position) {
        Playlist curPlayList = playList.get(position);

        holder.ListName.setText(curPlayList.getName());
        Glide.with(mContext).load(R.drawable.playlist).into(holder.ListImg);

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
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

}
