package samsung.com.myplayer2.Class;

import java.io.Serializable;

/**
 * Created by sev_user on 1/17/2018.
 */
public class Song implements Serializable {
    private long id;
    private String title;
    private String artist;
    //private Bitmap img;
    private String data;
    private long albumid;
    private String genres;

    public Song(long songID, String songTitle, String songArtist, String input, long albumID, String SongGen) {
        id = songID;
        title = songTitle;
        artist = songArtist;
        //img = image;
        data = input;
        albumid = albumID;
        genres = SongGen;

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

    public long getAlbumid() {
        return albumid;
    }

    public void setAlbumid(long albumid) {
        this.albumid = albumid;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

}