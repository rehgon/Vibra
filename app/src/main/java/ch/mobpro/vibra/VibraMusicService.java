package ch.mobpro.vibra;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.SeekBar;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by remo on 04.05.2015.
 */
public class VibraMusicService extends Service  implements MediaPlayer.OnCompletionListener {
    private IBinder mBinder = new VibraServiceBinder();
    private static MediaPlayer mp;
    private static ArrayList<File>  playList;
    int current_index = 0;
    SeekBar seekBar;

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

    protected void onStart() {
        if (mp == null) {
            mp = new MediaPlayer();
        }
    }

    public MediaPlayer getMediaPlayer() {
        return mp;
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

    public boolean isPlaying() {
        return mp.isPlaying();
    }

    public int getDuration() {
        return mp.getDuration();
    }

    public static ArrayList<File> getPlayList() {
        return VibraMusicService.playList;
    }

    public void setPlayList(ArrayList<File> musicFiles) {
        playList = musicFiles;
        current_index = 0;
    }

    public int getTrackIndex() {
        return current_index;
    }

    public void preparePlayer(int trackNr) {
        if (playList.size() > 0) {

            current_index = trackNr % playList.size();

            //stop MediaPlayer
            if (mp.isPlaying()) {
                mp.stop();
                mp.release();
            }

            //init MediaPlayer
            try {
                mp.setDataSource(playList.get(current_index).getAbsolutePath());
                mp.prepare();
            } catch (ArrayIndexOutOfBoundsException e) {
                Log.e("Vibra Service Error", "Track index out of bounds");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("playlist error","playlist size is zero");
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
