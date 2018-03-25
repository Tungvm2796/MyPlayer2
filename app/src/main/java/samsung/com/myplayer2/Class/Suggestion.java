package samsung.com.myplayer2.Class;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

/**
 * Created by 450G4 on 3/25/2018.
 */

public class Suggestion implements SearchSuggestion {
    private String mName;
    private boolean mIsHistory = false;

    public Suggestion(String suggestion) {
        mName= suggestion.toLowerCase();
    }

    public void setIsHistory(boolean isHistory) {
        this.mIsHistory = isHistory;
    }

    public boolean getIsHistory() {
        return this.mIsHistory;
    }

    @Override
    public String getBody() {
        return mName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
