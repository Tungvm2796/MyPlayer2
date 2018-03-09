package samsung.com.myplayer2.Class;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.Serializable;

/**
 * Created by sev_user on 1/17/2018.
 */
public class Song implements Serializable {
    private long id;
    private String title;
    private String artist;
    private Uri path;
    private Bitmap img;
    private String data;

    public Song(long songID, String songTitle, String songArtist, Uri songlink, Bitmap image, String input) {
        id = songID;
        title = songTitle;
        artist = songArtist;
        path = songlink;
        img = image;
        data = input;
    }

    public long getID() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public Uri getPath() {
        return path;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setPath(Uri path) {
        this.path = path;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getData() {
        return data;
    }

    public void setImg(Bitmap image){this.img = image;}

    public Bitmap getImg(){return this.img;}

}