package ch.mobpro.vibra;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.MediaController.MediaPlayerControl;

import java.io.File;

/**
 * Created by remo on 04.05.2015.
 */
public class VibraMusicService extends Service  {
    private final IBinder mBinder = new VibraServiceBinder();
    private static MediaPlayer mp;

    public VibraMusicService() {
        mp = new MediaPlayer();
    }

    public void play(File musicFile) {
        try {
            VibraPlayMusicTask playTask = new VibraPlayMusicTask();
            playTask.execute(musicFile);
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
            for (File musicFile : musicFiles) {
                try {
                    mp.setDataSource(musicFile.getAbsolutePath());
                    mp.prepare();
                    mp.start();

                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class VibraServiceBinder extends Binder {
        public VibraMusicService getService() {
            return VibraMusicService.this;
        }
    }
}
