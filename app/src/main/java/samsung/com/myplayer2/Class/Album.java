package samsung.com.myplayer2.Class;

import android.graphics.Bitmap;

/**
 * Created by 450G4 on 3/12/2018.
 */

public class Album {
    private long id;
    private String albumName;
    private String artistName;
    private int nr_of_songs;
    private Bitmap albumImg;

    public Album(long id, String albumName, String artistName, int nr_of_songs, Bitmap albumImg) {
        this.id = id;
        this.albumName = albumName;
        this.artistName = artistName;
        this.nr_of_songs = nr_of_songs;
        this.albumImg = albumImg;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public int getNr_of_songs() {
        return nr_of_songs;
    }

    public void setNr_of_songs(int nr_of_songs) {
        this.nr_of_songs = nr_of_songs;
    }

    public Bitmap getAlbumImg() {
        return albumImg;
    }

    public void setAlbumImg(Bitmap albumImg) {
        this.albumImg = albumImg;
    }
}
