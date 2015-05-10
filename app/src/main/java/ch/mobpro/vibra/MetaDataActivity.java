package ch.mobpro.vibra;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;

import org.cmc.music.common.ID3WriteException;
import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Matthias on 10.05.2015.
 */
public class MetaDataActivity extends Activity {
    EditText album, artist, song;
    MusicMetadataSet src_set;
    IMusicMetadata metadata;
    File src;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meta_data);

        if (getIntent().hasExtra("index")) {
            int index = getIntent().getExtras().getInt("index");
            src = MainActivity.getMusicFiles().get(index);
        }

        initEditText();
    }

    public void initEditText() {

        album = (EditText) findViewById(R.id.album);
        artist = (EditText) findViewById(R.id.artist);
        song = (EditText) findViewById(R.id.song);
        try {
            src_set = new MyID3().read(src);
            metadata = src_set.getSimplified();
        } catch (IOException e) {
            e.printStackTrace();
        }
        album.setText(metadata.getAlbum());
        artist.setText(metadata.getArtist());
        song.setText(metadata.getSongTitle());
    }

    public void editSongName(View view) {
        song.setText("");
    }

    public void editAlbumName(View view) {
        album.setText("");
    }

    public void editArtistName(View view) {
        artist.setText("");
    }

    public void saveMetaData(View view) {
        File dst = new File(Environment.getExternalStorageDirectory() + "/vibra_music/" + song.getText()+".mp3");
        MusicMetadata meta = new MusicMetadata(song.getText().toString());
        meta.setAlbum(album.getText().toString());
        meta.setArtist(artist.getText().toString());
        meta.setSongTitle(song.getText().toString());
        try {
            new MyID3().write(src, dst, src_set, meta);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ID3WriteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
