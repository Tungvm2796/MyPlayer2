package samsung.com.myplayer2.Class;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by 450G4 on 3/20/2018.
 */

public class Function {

    public Function() {
    }

    public void SortBySongName(ArrayList<Song> ArraySong) {
        Collections.sort(ArraySong, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
    }

    public void SortByAlbumName(ArrayList<Album> ArrayAlbum) {
        Collections.sort(ArrayAlbum, new Comparator<Album>() {
            public int compare(Album a, Album b) {
                return a.getAlbumName().compareTo(b.getAlbumName());
            }
        });
    }

    public void SortByArtistName(ArrayList<Artist> ArrayArtist) {
        Collections.sort(ArrayArtist, new Comparator<Artist>() {
            public int compare(Artist a, Artist b) {
                return a.getName().compareTo(b.getName());
            }
        });
    }

    public byte[] BitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (bitmap != null)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public Bitmap GetBitmap(String filePath) {
        Bitmap image;
        MediaMetadataRetriever mData = new MediaMetadataRetriever();
        mData.setDataSource(filePath);
        try {
            byte art[] = mData.getEmbeddedPicture();
            image = BitmapFactory.decodeByteArray(art, 0, art.length);
        } catch (Exception e) {
            image = null;
        }
        return image;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        if (bm == null)
            return bm;
        else {
            int width = bm.getWidth();
            int height = bm.getHeight();
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // CREATE A MATRIX FOR THE MANIPULATION
            Matrix matrix = new Matrix();
            // RESIZE THE BIT MAP
            matrix.postScale(scaleWidth, scaleHeight);

            // "RECREATE" THE NEW BITMAP
            Bitmap resizedBitmap = Bitmap.createBitmap(
                    bm, 0, 0, width, height, matrix, false);
            bm.recycle();
            return resizedBitmap;
        }
    }

    public void getSongList(Context mContext, ArrayList<Song> ArraySong) {
        //retrieve song info
        ContentResolver musicResolver = mContext.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String select = MediaStore.Audio.Media.DURATION + ">=30000";
        Cursor musicCursor = musicResolver.query(musicUri, null, select, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get collumn
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int artisColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int dataColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int albumIdColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            //add song to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtis = musicCursor.getString(artisColumn);
                String thisData = musicCursor.getString(dataColumn);
                //Bitmap songimg = GetBitmap(thisData);
                //Bitmap lastimg = getResizedBitmap(songimg, 55, 60);
                long albumId = musicCursor.getLong(albumIdColumn);
                ArraySong.add(new Song(thisId, thisTitle, thisArtis, thisData, albumId));
            }
            while (musicCursor.moveToNext());
        }
        SortBySongName(ArraySong);
        musicCursor.close();
    }

    public void getAlbumsLists(Context mContext, ArrayList<Album> albumList) {
        String where = null;

        final Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        final String _id = MediaStore.Audio.Albums._ID;
        final String album_name = MediaStore.Audio.Albums.ALBUM;
        final String artist = MediaStore.Audio.Albums.ARTIST;
        final String albumart = MediaStore.Audio.Albums.ALBUM_ART;
        final String tracks = MediaStore.Audio.Albums.NUMBER_OF_SONGS;

        final String[] columns = {_id, album_name, artist, albumart, tracks};
        Cursor cursor = mContext.getContentResolver().query(uri, columns, where, null, null);

        if (cursor != null && cursor.moveToFirst()) {

            do {

                long id = cursor.getLong(cursor.getColumnIndex(_id));
                String name = cursor.getString(cursor.getColumnIndex(album_name));
                String artist2 = cursor.getString(cursor.getColumnIndex(artist));
                String artPath = cursor.getString(cursor.getColumnIndex(albumart));
                Bitmap art = BitmapFactory.decodeFile(artPath);
                int nr = Integer.parseInt(cursor.getString(cursor.getColumnIndex(tracks)));

                albumList.add(new Album(id, name, artist2, nr, art));

            } while (cursor.moveToNext());
        }
        SortByAlbumName(albumList);
        cursor.close();
    }

    public void getArtist(Context mContext, ArrayList<Artist> artists) {
        String where = null;

        final Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        final String artist = MediaStore.Audio.Albums.ARTIST;

        final String[] columns = {artist};
        Cursor cursor = mContext.getContentResolver().query(uri, columns, where, null, null);

        if (cursor != null && cursor.moveToFirst()) {

            do {

                String artist2 = cursor.getString(cursor.getColumnIndex(artist));
                artists.add(new Artist(artist2));

            } while (cursor.moveToNext());
        }
        SortByArtistName(artists);
        cursor.close();
    }

}
