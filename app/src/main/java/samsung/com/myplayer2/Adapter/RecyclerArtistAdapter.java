package samsung.com.myplayer2.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import samsung.com.myplayer2.Class.Artist;
import samsung.com.myplayer2.R;

/**
 * Created by 450G4 on 3/26/2018.
 */

public class RecyclerArtistAdapter extends RecyclerView.Adapter<RecyclerArtistAdapter.MyRecyclerArtistHolder> {

    private ArrayList<Artist> artists;
    private ArtistClickListener mClickListener;
    Context mContext;

    public RecyclerArtistAdapter(Context context, ArrayList<Artist> artistList) {
        this.mContext = context;
        this.artists = artistList;
    }

    public class MyRecyclerArtistHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView artistName;

        public MyRecyclerArtistHolder(View artistLay) {
            super(artistLay);

            artistName = (TextView) artistLay.findViewById(R.id.artist_name);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onArtistClick(view, getAdapterPosition());
        }
    }

    @Override
    public MyRecyclerArtistHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View artistView = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist, parent, false);
        return new MyRecyclerArtistHolder(artistView);
    }

    @Override
    public void onBindViewHolder(MyRecyclerArtistHolder holder, int position) {
        Artist curArtist = artists.get(position);

        holder.artistName.setText(curArtist.getName());
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    // allows clicks events to be caught
    public void setArtistClickListener(ArtistClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ArtistClickListener {
        void onArtistClick(View view, int position);
    }
}
