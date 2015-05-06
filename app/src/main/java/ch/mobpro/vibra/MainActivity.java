package ch.mobpro.vibra;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.ServiceConfigurationError;


public class MainActivity extends Activity {

    private static ArrayList<File> musicFiles;
    private int musicFilesIndex = 0;
    private File musicFolder;
    private VibraMusicService musicService;
    private static boolean mBound = false;
    private static final String MUSIC_FOLDER_NAME = "vibra_music";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createMusicDirectory();
        loadMusicList();

        if (getIntent().hasExtra("songIndex")) {
            int index = getIntent().getExtras().getInt("songIndex");
            musicFilesIndex = index;

            File song = musicFiles.get(index);
            Log.i("play: ", song.getName());
        }

        //debugCreateMusicFiles();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, VibraMusicService.class);
        bindService(intent, vibraServiceConnection, Context.BIND_AUTO_CREATE);

        Log.i("Vibra custom Message", "onStart()");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.i("Vibra custom Message", "onStop()");

        if (mBound) {
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

    public void loadMusicList() {
        AsyncTask<Void, Void, ArrayList<File>> loadMusic = new AsyncTask<Void, Void, ArrayList<File>>() {
            @Override
            protected ArrayList<File> doInBackground(Void... params) {
                ArrayList<File> files = new ArrayList<>();
                if (musicFolder.exists()) {
                    File[] fileList = musicFolder.listFiles(new FileExtensionFilter());
                    if (fileList.length > 0) {
                        for (File file : fileList) {
                            files.add(file);
                            Log.i("file: ", file.getAbsolutePath());
                        }
                    } else {
                        Log.i("info :", "empty folder");
                    }
                } else {
                    Log.i("error: ", "folder dos not exist " + musicFolder.getAbsolutePath());
                }
                return files;
            }

            @Override
            protected void onPostExecute(ArrayList<File> files) {
                super.onPostExecute(files);
                musicFiles = files;
            }

            class FileExtensionFilter implements FilenameFilter {
                public boolean accept(File dir, String name) {
                    return (name.endsWith(".mp3") || name.endsWith(".MP3"));
                }
            }
        };
        loadMusic.execute((Void) null);
    }


    /* Temporär zum testen: arraylist mit Music-File Objekten */
    public void debugCreateMusicFiles() {
        ArrayList<File> files = new ArrayList<>();
        files.add(new File(musicFolder.getAbsolutePath(), "colour_haze_aquamaria.mp3"));

        for (File file : files) {
            Log.i("file", file.getAbsolutePath());
        }

        musicFiles = files;
    }

    public void createMusicDirectory() {
        musicFolder = new File(Environment.getExternalStorageDirectory(), MUSIC_FOLDER_NAME);
        musicFolder.mkdirs();
    }

    public static ArrayList<File> getMusicFiles() {
        return musicFiles;
    }

    private ServiceConnection vibraServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            VibraMusicService.VibraServiceBinder binder = (VibraMusicService.VibraServiceBinder) service;
            musicService = binder.getService();
            mBound = true;
            if (musicFiles != null) {
                musicService.setPlayList(musicFiles);
                musicService.preparePlayer(musicFilesIndex);
            } else {
                Log.e("Vibra error", "No Music Files");
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
        musicService.preparePlayer(musicFilesIndex);

        try {
            if (musicFiles == null) {
                throw new FileNotFoundException("Music File is null");
            }
            if (!mBound) {
                throw new ServiceConfigurationError("not Bound");
            }

            if (!musicService.isPlaying()) {
                musicService.start();
                ((Button) v).setText("Pause");
            } else {
                musicService.pause();
                ((Button) v).setText("Play");
            }
        } catch (ServiceConfigurationError e) {
            Log.e("Vibra Error", e.getMessage());
        } catch (FileNotFoundException e) {
            Log.e("Vibra Error", e.getMessage());
        }
    }

    public void nextOnClick(View v) {
        musicService.preparePlayer(musicService.getTrackIndex() + 1);
        Log.i("Player msg", "next track");
    }

    public void prevOnClick(View v) {
        musicService.preparePlayer(musicService.getTrackIndex() - 1);
        Log.i("Player msg", "previous track");
    }

    public void browseOnClick(View v) {
        Intent intent = new Intent(this, MusicBrowserActivity.class);
        startActivity(intent);
    }

}
