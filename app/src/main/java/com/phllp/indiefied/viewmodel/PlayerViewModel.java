package com.phllp.indiefied.viewmodel;

import android.app.Application;
import android.net.Uri;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

import java.io.File;
import java.util.Arrays;

public class PlayerViewModel extends AndroidViewModel {

    private ExoPlayer player;

    public PlayerViewModel(@NonNull Application app) {
        super(app);
    }

    private void ensurePlayer() {
        if (player == null) {
            player = new ExoPlayer.Builder(getApplication()).build();
        }
    }

    public ExoPlayer getPlayer() {
        ensurePlayer();
        return player;
    }

    /** Toca um arquivo específico pelo nome (ex.: "minha_musica.mp3") na pasta /files/Music */
    public boolean playFromAppMusicDir(String fileName) {
        File dir = getApplication().getExternalFilesDir(Environment.DIRECTORY_MUSIC);
//        System.out.println("");
        if (dir == null) return false;
        File f = new File(dir, fileName);
        if (!f.exists()) return false;
        return playFile(f);
    }

    /** Varre a pasta /files/Music e toca o primeiro arquivo de áudio encontrado */
    public boolean playFirstFromAppMusicDir() {
        File dir = getApplication().getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        if (dir == null || !dir.exists()) return false;

        File[] candidates = dir.listFiles(pathname -> {
            if (pathname == null || !pathname.isFile()) return false;
            String n = pathname.getName().toLowerCase();
            return n.endsWith(".mp3") || n.endsWith(".m4a") || n.endsWith(".wav") || n.endsWith(".ogg");
        });

        if (candidates == null || candidates.length == 0) return false;

        // Opcional: ordenar alfabeticamente para previsibilidade
        Arrays.sort(candidates, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));

        return playFile(candidates[0]);
    }

    /** Toca um File diretamente */
    public boolean playFile(File file) {
        if (file == null || !file.exists()) return false;
        ensurePlayer();
        MediaItem item = MediaItem.fromUri(Uri.fromFile(file));
        player.setMediaItem(item);
        player.prepare();
        player.play();
        return true;
    }

    /** Play/Pause simples (para o botão) */
    public void togglePlayPause() {
        ensurePlayer();
        player.setPlayWhenReady(!player.getPlayWhenReady());
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
