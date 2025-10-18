package com.phllp.indiefied.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.phllp.indiefied.model.Track;

public class PlayerViewModel extends ViewModel {
    private final MutableLiveData<Track> currentTrack = new MutableLiveData<>();
    private boolean playing = false;
    private boolean shuffle = false;

    public PlayerViewModel() {
        currentTrack.setValue(new Track("1", "Morango do Nordeste", "Karametade", "https://.../capa.jpg"));
    }

    public LiveData<Track> getCurrentTrack() { return currentTrack; }

    public void togglePlay() { playing = !playing; /* ligar UI depois */ }
    public void next() { /* trocar currentTrack */ }
    public void prev() { /* trocar currentTrack */ }
    public void seekTo(int posMs) { /* atualizar player real quando tiver */ }
    public void setShuffle(boolean enabled) { shuffle = enabled; }
}
