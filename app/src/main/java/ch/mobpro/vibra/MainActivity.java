package ch.mobpro.vibra;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.ServiceConfigurationError;


public class MainActivity extends Activity {

    private static ArrayList<File> musicFiles;
    private static ArrayList<String> songs;
    private int musicFilesIndex = -1;
    private File musicFolder;
    private VibraMusicService musicService;
    private static boolean mBound = false;
    private static final String MUSIC_FOLDER_NAME = "vibra_music";

    private VisualizerView mVisualizerView;
    private Visualizer mVisualizer;

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

            Intent intent = new Intent(this, VibraMusicService.class);
            bindService(intent, vibraServiceConnection, Context.BIND_AUTO_CREATE);
        }

        //init visualizerView
        mVisualizerView = (VisualizerView) findViewById(R.id.visualizerView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (musicFilesIndex >= 0) {
            TextView st = (TextView) findViewById(R.id.songTitle);
            st.setText(songs.get(musicFilesIndex));
            //MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();
            //metaRetriver.setDataSource(musicFiles.get(musicFilesIndex).getAbsolutePath());
        }

        Log.i("Vibra custom Message", "onStart()");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.i("Vibra custom Message", "onStop()");
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
                ArrayList<String> s = new ArrayList<>();
                for (File file : files) {
                    s.add(file.getName().substring(0, file.getName().length() - 4));
                }
                songs = s;
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

    private void createMusicDirectory() {
        musicFolder = new File(Environment.getExternalStorageDirectory(), MUSIC_FOLDER_NAME);
        Log.i("Vibra Msg", "Music Folder created");
    }

    public static ArrayList<String> getSongs() {
        return songs;
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

            //init visualizer
            initAudio();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    public static ArrayList<File> getMusicFiles() {
        return musicFiles;
    }
/* onClick Events */

    public void startStopOnClick(View v) {
        ImageButton ib = (ImageButton) v;

        try {
            if (musicFiles == null) {
                throw new FileNotFoundException("Music File is null");
            }
            if (!mBound) {
                throw new ServiceConfigurationError("not Bound");
            }

            if (!musicService.isPlaying()) {
                musicService.start();
                ib.setImageResource(R.drawable.ic_pause);
            } else {
                musicService.pause();
                ib.setImageResource(R.drawable.play);
            }
        } catch (ServiceConfigurationError e) {
            Log.e("Vibra Error", e.getMessage());
        } catch (FileNotFoundException e) {
            Log.e("Vibra Error", e.getMessage());
        }
    }

    public void browseOnClick(View v) {
        Intent intent = new Intent(this, MusicBrowserActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBound) {
            unbindService(vibraServiceConnection);
            mBound = false;
        }
    }

    public void back(View view) {
        musicService.preparePlayer(musicService.getTrackIndex() - 1);
        Log.i("Player msg", "previous track");
    }

    public void next(View view) {
        musicService.preparePlayer(musicService.getTrackIndex() + 1);
        Log.i("Player msg", "next track");
    }

    public void backward(View view) {
        //ToDO
    }

    public void forward(View view) {
        //ToDo
    }

    public void edit(View view) {
        Intent intent = new Intent(getApplicationContext(), MetaDataActivity.class);
        intent.putExtra("index", musicFilesIndex);
        startActivity(intent);
    }


    /*
        Visualizer Methoden
     */

    private void initAudio() {
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        setupVisualizerFxAndUI();
        mVisualizer.setEnabled(true);
        musicService.getMediaPlayer().setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mVisualizer.setEnabled(false);
                    }
                }
        );
        musicService.getMediaPlayer().start();
    }

    private void setupVisualizerFxAndUI() {
        // Create the Visualizer object and attach it to our media player.
        mVisualizer = new Visualizer(musicService.getMediaPlayer().getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                        mVisualizerView.updateVisualizer(bytes);
                    }

                    public void onFftDataCapture(Visualizer visualizer,
                                                 byte[] bytes, int samplingRate) {
                    }
                }
                , Visualizer.getMaxCaptureRate() / 2, true, false
        );
    }
}
