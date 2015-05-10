package ch.mobpro.vibra;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by remo on 04.05.2015.
 */
public class VibraMusicService extends Service  implements MediaPlayer.OnCompletionListener {
    private IBinder mBinder = new VibraServiceBinder();
    private static MediaPlayer mp = new MediaPlayer();
    private VibraPlayMusicTask task;
    private ArrayList<File> playList;
    int current_index = 0;

    public VibraMusicService() {
        mp = new MediaPlayer();
    }

    public class VibraServiceBinder extends Binder {
        public VibraMusicService getService() {
            return VibraMusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public MediaPlayer getMediaPlayer() {
        return mp;
    }

    public boolean isPlaying() {
        return mp.isPlaying();
    }

    public void start() {
        if (!isPlaying()) {
            mp.start();
        }
    }

    public void pause() {
        if (isPlaying()) {
            mp.pause();
        }
    }

    public void setPlayList(ArrayList<File> musicFiles) {
        playList = musicFiles;
        current_index = 0;
    }

    public int getTrackIndex() {
        return current_index;
    }

    public void preparePlayer(int trackNr) {
        current_index = trackNr % playList.size();

        try {
            if (task == null) {
                task = new VibraPlayMusicTask();
            } else {
                task.cancel(true);
            }

            task.execute(playList.get(current_index));
        } catch (ArrayIndexOutOfBoundsException e ) {
            Log.e("Vibra Service Error","Track index out of bounds");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new VibraPlayMusicTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return START_STICKY;
    }

    private final class VibraPlayMusicTask extends AsyncTask<File, Void, Void> {

        @Override
        protected Void doInBackground(File... musicFiles) {
            File musicFile = musicFiles[0];

            try {
                mp.setDataSource(musicFile.getAbsolutePath());
                mp.prepare();

                Log.i("progress", mp.getCurrentPosition() + "");
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onCancelled() {
            mp.stop();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.i("vibra musicservice","Song completed");
        preparePlayer(++current_index % playList.size());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.release();
        mp = null;
    }
}
