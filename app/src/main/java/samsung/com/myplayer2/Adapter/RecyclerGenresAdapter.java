package samsung.com.myplayer2.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import samsung.com.myplayer2.R;

public class RecyclerGenresAdapter extends RecyclerView.Adapter<RecyclerGenresAdapter.MyRecyclerGenresHolder> {

    private ArrayList<String> genres;
    private GenresClickListener mClickListener;
    Context mContext;

    public RecyclerGenresAdapter(Context context, ArrayList<String> genresList) {
        this.mContext = context;
        this.genres = genresList;
    }

    public class MyRecyclerGenresHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView genresName;

        public MyRecyclerGenresHolder(View genresLay) {
            super(genresLay);

            genresName = (TextView) genresLay.findViewById(R.id.genres_name);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onGenresClick(view, getAdapterPosition());
        }
    }

    @Override
    public MyRecyclerGenresHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View genresView = LayoutInflater.from(parent.getContext()).inflate(R.layout.genres, parent, false);
        return new MyRecyclerGenresHolder(genresView);
    }

    @Override
    public void onBindViewHolder(MyRecyclerGenresHolder holder, int position) {
        String curGenres = genres.get(position);

        holder.genresName.setText(curGenres);
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    // allows clicks events to be caught
    public void setGenresClickListener(GenresClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface GenresClickListener {
        void onGenresClick(View view, int position);
    }
}
