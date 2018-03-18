package samsung.com.myplayer2.Class;

/**
 * Created by 450G4 on 3/17/2018.
 */

public class Playlist {
    String listid;
    String name;

    public Playlist() {
    }

    public Playlist(String listid, String name) {
        this.listid = listid;
        this.name = name;

    }

    public String getListid() {
        return listid;
    }

    public void setListid(String listid) {
        this.listid = listid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
