package samsung.com.myplayer2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import samsung.com.myplayer2.R;

/**
 * Created by sev_user on 1/17/2018.
 */
public class SongAdapter extends BaseAdapter {
    @Override
    public int getCount() {
        return listName.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //map to song layout
        LinearLayout songLay = (LinearLayout) songInf.inflate
                (R.layout.list_playlist, parent, false);
        //get title and artist views
        TextView NameView = (TextView) songLay.findViewById(R.id.name_list);

        //get song using position
        String currName = listName.get(position);

        //get title and artist strings
        NameView.setText(currName);

        //set position as tag
        songLay.setTag(position);
        return songLay;
    }

    private ArrayList<String> listName;
    private LayoutInflater songInf;

    public SongAdapter(Context c, ArrayList<String> input) {
        listName = input;
        songInf = LayoutInflater.from(c);
    }
}