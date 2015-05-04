package ch.mobpro.vibra;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;


public class MainActivity extends Activity {
    private static File musicFile;
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

    public void play() {
        if (musicFile != null) {
            musicService.play(musicFile);
        } else {
            Log.i("custom Vibra error", "Music File is null");
        }
    }

    public void setMusicFile(File musicFile) {
        this.musicFile = musicFile;
    }
}
