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
    static final String TABLE_PLAYLIST = "Playlist";
    static final String TABLE_SONGID = "SongId";

    // Table Columns names
    static final String KEY_PLID = "PlaylistId";
    static final String KEY_PLNAME = "PlaylistName";
    static final String KEY_SONGID = "SongId";

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
    void addPlaylist(Playlist playlist) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PLID, playlist.getListid());
        values.put(KEY_PLNAME, playlist.getName());

        db.insert(TABLE_PLAYLIST, null, values);
        db.close();
    }

    //Add song to Playlist
    void addSongToPlaylist(String songId, String playlistId) {
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

        return returnlist;
    }

    //Get SongId Array by Playlist Id
    public ArrayList<String> GetSongIdArray(String listId) {
        ArrayList<String> returnList = new ArrayList<>();

        String query = "SELECT " + KEY_SONGID + " FROM " + TABLE_SONGID +
                " WHERE " + KEY_PLID + " LIKE '" + listId + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                returnList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        return returnList;
    }

    // Deleting single Playlist
    public void deletePPlaylist(Playlist playlist) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_PLAYLIST +
                " WHERE " + KEY_PLID + " LIKE '" + new String[]{String.valueOf(playlist.getListid())} + "'";
        db.execSQL(query);
        db.close();
    }
}
