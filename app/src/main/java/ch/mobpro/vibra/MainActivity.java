package ch.mobpro.vibra;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.util.Collection;


public class MainActivity extends Activity {
    private static Collection<File> musicFiles;
    private static File musicFolder;
    private static VibraMusicService musicService;
    private static final String MUSIC_FOLDER_NAME = "vibra_music";

    public VibraMusicService getMusicService() {
        if (musicService == null) {
            musicService = new VibraMusicService();
        }
        return musicService;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createMusicDirectory();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        getMusicService();
    }

    public void createMusicDirectory() {
        musicFolder = new File(Environment.getExternalStorageDirectory(), MUSIC_FOLDER_NAME);
        musicFolder.mkdirs();
    }

    public void playOnclick(View v) {
        if (musicFiles != null) {
            musicService.play(musicFiles);
        } else {
            Log.i("custom Vibra error", "Music File is null");
        }
    }

    public void pauseOnClick(View v) {
        musicService.pause();
    }

    public void browseOnClick(View v) {

    }

    public void setMusicFile(Collection<File> musicFiles) {
        this.musicFiles = musicFiles;
    }
}
