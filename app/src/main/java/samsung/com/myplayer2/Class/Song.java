package samsung.com.myplayer2.Class;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by sev_user on 1/17/2018.
 */
public class Song implements Serializable {
    private long id;
    private String title;
    private String artist;
    private Bitmap img;
    private String data;
    private long albumid;

    public Song(long songID, String songTitle, String songArtist, Bitmap image, String input, long albumID) {
        id = songID;
        title = songTitle;
        artist = songArtist;
        img = image;
        data = input;
        albumid = albumID;
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

    public void setId(long id) {
        this.id = id;
    }

    public void setData(String data) {
        this.data = data;
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

    public long getAlbumid() {
        return albumid;
    }

    public void setAlbumid(long albumid) {
        this.albumid = albumid;
    }

}