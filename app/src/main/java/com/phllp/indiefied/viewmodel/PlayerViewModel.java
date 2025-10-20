package com.phllp.indiefied.viewmodel;

import android.app.Application;
import android.net.Uri;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

import com.phllp.indiefied.model.Track;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;

public class PlayerViewModel extends AndroidViewModel {

    private ExoPlayer player;
    private final MutableLiveData<Track> currentTrack = new MutableLiveData<>(null);
    private final MutableLiveData<Boolean> isPlaying = new MutableLiveData<>(false);

    public PlayerViewModel(@NonNull Application app) {
        super(app);
    }

    private void ensurePlayer() {
        if (player == null) {
            player = new ExoPlayer.Builder(getApplication()).build();
            player.addListener(new androidx.media3.common.Player.Listener() {
                @Override public void onIsPlayingChanged(boolean playing) {
                    isPlaying.postValue(playing);
                }
            });
        }
    }

    public LiveData<Track> getCurrentTrack() {
        return currentTrack;
    }

    public LiveData<Boolean> getIsPlaying() {
        return isPlaying;
    }

    public ExoPlayer getPlayer() {
        ensurePlayer();
        return player;
    }

    // Inicia reprodução a partir do Track (id = nome base do arquivo salvo em /files/Music/)
    public boolean startPlayback(Track track) {
        if (track == null || track.getId() == null) return false;
        boolean ok = playById(track.getId());
        if (ok) currentTrack.setValue(track);
        return ok;
    }


    /** Toca um arquivo específico pelo nome (ex.: "minha_musica.mp3") na pasta /files/Music */
    public boolean playFromAppMusicDir(String fileName) {
        File dir = getApplication().getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        if (dir == null) return false;
        File f = new File(dir, fileName);
        if (!f.exists()) return false;
        return playFile(f);
    }

    /** Reproduz arquivo procurando por id (com/sem extensão) dentro do app-specific Music */
    public boolean playById(String id) {
        if (id == null) return false;
        id = id.trim();
        File dir = getApplication().getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        if (dir == null || !dir.exists()) return false;

        // tenta id exato e id + “.*”
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) return false;

        String lid = id.toLowerCase(Locale.ROOT);
        File match = null;
        for (File f : files) {
            String ln = f.getName().toLowerCase(Locale.ROOT);
            if (ln.equals(lid) || ln.startsWith(lid + ".")) { match = f; break; }
        }
        if (match == null) return false;

        return playFile(match);
    }

    public boolean playFile(File file) {
        if (file == null || !file.exists()) return false;
        ensurePlayer();
        MediaItem item = MediaItem.fromUri(Uri.fromFile(file));
        player.setMediaItem(item);
        player.prepare();
        player.play();
        isPlaying.setValue(true);
        return true;
    }

    // Alterna play/pause mantendo estado
    public void togglePlayPause() {
        ensurePlayer();
        boolean toPlay = !player.getPlayWhenReady();
        player.setPlayWhenReady(toPlay);
        isPlaying.setValue(toPlay);
    }

    public void pause() {
        ensurePlayer();
        player.setPlayWhenReady(false);
        isPlaying.setValue(false);
    }

    @Override
    protected void onCleared() {
        if (player != null) {
            player.release();
            player = null;
        }
        super.onCleared();
    }
}
