package samsung.com.myplayer2.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import samsung.com.myplayer2.Handler.DatabaseHandler;
import samsung.com.myplayer2.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistFragment extends Fragment {

    Button btn;



    public ArtistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_artist, container, false);
        btn = (Button) v.findViewById(R.id.btnxemid);

        final DatabaseHandler db = new DatabaseHandler(getActivity());
        return v;
    }

}
