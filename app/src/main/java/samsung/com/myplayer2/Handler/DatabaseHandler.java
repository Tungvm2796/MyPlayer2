package samsung.com.myplayer2.Handler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import samsung.com.myplayer2.Class.Playlist;

/**
 * Created by 450G4 on 3/17/2018.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "Manager";

    // Table name
    private static final String TABLE_PLAYLIST = "Playlist";
    private static final String TABLE_SONGID = "SongId";

    // Table Columns names
    private static final String KEY_PLID = "PlaylistId";
    private static final String KEY_PLNAME = "PlaylistName";
    private static final String KEY_SONGID = "SongId";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PLAYLIST_TABLE = "CREATE TABLE " + TABLE_PLAYLIST + "("
                + KEY_PLID + " TEXT PRIMARY KEY,"
                + KEY_PLNAME + " TEXT" + ")";

        String CREATE_SONGID_TABLE = "CREATE TABLE " + TABLE_SONGID + "("
                + KEY_PLID + " TEXT,"
                + KEY_SONGID + " TEXT," + "PRIMARY KEY(" + KEY_PLID + ", " + KEY_SONGID + ")" + ")";

        db.execSQL(CREATE_PLAYLIST_TABLE);
        db.execSQL(CREATE_SONGID_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGID);

        // Create tables again
        onCreate(db);
    }

    //Add new Playlist
    public void addPlaylist(Playlist playlist) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PLID, playlist.getListid());
        values.put(KEY_PLNAME, playlist.getName());

        db.insert(TABLE_PLAYLIST, null, values);
        db.close();
    }

    //Add song to Playlist
    public void addSongToPlaylist(String songId, String playlistId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PLID, playlistId);
        values.put(KEY_SONGID, songId);

        db.insert(TABLE_SONGID, null, values);
        db.close();
    }

    //Get all Playlist
    public ArrayList<Playlist> getAllList() {
        ArrayList<Playlist> returnlist = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_PLAYLIST;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Playlist playlist = new Playlist();
                playlist.setListid(cursor.getString(0));
                playlist.setName(cursor.getString(1));
                // Adding contact to list
                returnlist.add(playlist);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return returnlist;
    }

    //Get SongId Array by Playlist Id
    public ArrayList<String> GetSongIdArray(String listId) {
        ArrayList<String> returnList = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_SONGID +
                " WHERE " + KEY_PLID + " LIKE '" + listId + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                returnList.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return returnList;
    }

    // Deleting single Playlist
    public void deletePlaylist(Playlist playlist) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PLAYLIST, KEY_PLID + " = ?",
                new String[]{String.valueOf(playlist.getListid())});
        db.close();
    }

    //Count all Playlist
    public int CountList() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PLAYLIST;
        Cursor cursor = db.rawQuery(query, null);
        cursor.close();
        return cursor.getCount();
    }

    public boolean CheckPlaylistExist(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + KEY_PLNAME + " FROM " + TABLE_PLAYLIST +
                " WHERE " + KEY_PLNAME + " LIKE '" + name + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        } else {
            cursor.close();
            return true;
        }
    }

    public boolean CheckSongAdded(String songId, String listId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_SONGID +
                " WHERE " + KEY_PLID + " LIKE '" + listId + "' AND " + KEY_SONGID + " LIKE '" + songId + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        } else {
            cursor.close();
            return true;
        }
    }

    public int getMaxId() {
        int num = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PLAYLIST + " ORDER BY " + KEY_PLID + " DESC LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                num = Integer.parseInt(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return num;
    }

    public String getIdByName(String Name) {
        String num = "";
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PLAYLIST + " WHERE " + KEY_PLNAME + " LIKE '" + Name + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                num = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return num;
    }

    public ArrayList<String> GetAllPlaylistName() {
        ArrayList<String> listName = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + KEY_PLNAME + " FROM " + TABLE_PLAYLIST;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                listName.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listName;
    }

    public void RemoveSongFromPlaylist(String PlayID, String SongId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_SONGID +
                " WHERE " + KEY_PLID + " = '" + PlayID + "' AND " + KEY_SONGID + " = '" + SongId + "'";
        db.execSQL(query);
        db.close();
    }

    public void SwapSongOfPlaylist(String PlayID, String SongIdOld, String SongIdNew) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SONGID, SongIdNew);

        ContentValues values1 = new ContentValues();
        values1.put(KEY_SONGID, SongIdOld);

        ContentValues values2 = new ContentValues();
        values2.put(KEY_SONGID, String.valueOf(-1));

        db.update(TABLE_SONGID, values2, KEY_PLID + " = ? AND " + KEY_SONGID + " = ?", new String[]{PlayID, SongIdOld});
        db.update(TABLE_SONGID, values1, KEY_PLID + " = ? AND " + KEY_SONGID + " = ?", new String[]{PlayID, SongIdNew});
        db.update(TABLE_SONGID, values, KEY_PLID + " = ? AND " + KEY_SONGID + " = ?", new String[]{PlayID, String.valueOf(-1)});

        values.clear();
        values1.clear();
        values2.clear();
        db.close();
    }
}
