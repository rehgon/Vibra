package ch.mobpro.vibra;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ServiceConfigurationError;


public class MainActivity extends Activity {
    private static ArrayList<File> musicFiles;
    private static File musicFolder;
    private static VibraMusicService musicService;
    private static boolean mBound = false;
    private static final String MUSIC_FOLDER_NAME = "vibra_music";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createMusicDirectory();

        debugCreateMusicFiles();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, VibraMusicService.class);
        bindService(intent, vibraServiceConnection, Context.BIND_AUTO_CREATE);

        Log.i("Vibra custom Message","onStart()");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.i("Vibra custom Message","onStop()");

        if(mBound) {
            unbindService(vibraServiceConnection);
            mBound = false;
        }
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

    /* Temporär zum testen: arraylist mit Music-File Objekten */
    public void debugCreateMusicFiles() {
        ArrayList<File> files = new ArrayList<>();
        files.add(new File(musicFolder.getAbsolutePath(),"colour_haze_aquamaria.mp3"));

        for (File file : files) {
            Log.i("file",file.getAbsolutePath());
        }

        musicFiles = files;
    }

    public void createMusicDirectory() {
        musicFolder = new File(Environment.getExternalStorageDirectory(), MUSIC_FOLDER_NAME);
        musicFolder.mkdirs();
    }

    private ServiceConnection vibraServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            VibraMusicService.VibraServiceBinder binder = (VibraMusicService.VibraServiceBinder) service;
            musicService = binder.getService();
            mBound = true;
            if (musicFiles != null) {
                musicService.setPlayList(musicFiles);
                musicService.preparePlayer(0);
            } else {
                Log.e("Vibra error","No Music Files");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };


    /* onClick Events */

    public void startStopOnClick(View v) {
        Button playButton = (Button) v;

        try {
            if (musicFiles == null) { throw new FileNotFoundException("Music File is null"); }
            if (!mBound) { throw new ServiceConfigurationError("not Bound"); }

            if(!musicService.isPlaying()) {
                musicService.start();
                ((Button) v).setText("Pause");
            } else {
                musicService.pause();
                ((Button) v).setText("Play");
            }
        } catch (ServiceConfigurationError e) {
            Log.e("Vibra Error",e.getMessage());
        } catch (FileNotFoundException e) {
            Log.e("Vibra Error",e.getMessage());
        }
    }

    public void nextOnClick(View v) {
        musicService.preparePlayer(musicService.getTrackIndex() + 1);
        Log.i("Player msg","next track");
    }

    public void prevOnClick(View v) {
        musicService.preparePlayer(musicService.getTrackIndex() - 1);
        Log.i("Player msg","previous track");
    }

    public void browseOnClick(View v) {

    }
}
