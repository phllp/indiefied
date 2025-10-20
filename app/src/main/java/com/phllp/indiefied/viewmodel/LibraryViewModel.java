package com.phllp.indiefied.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.LiveData;
import com.phllp.indiefied.model.Track;
import com.phllp.indiefied.repository.FirebaseRepository;
import java.util.List;

public class LibraryViewModel extends AndroidViewModel {
    private final FirebaseRepository repo;
    private final MutableLiveData<List<Track>> tracks = new MutableLiveData<>();

    public LibraryViewModel(@NonNull Application app) {
        super(app);
        repo = new FirebaseRepository();
    }

    public LiveData<List<Track>> getTracks() {
        return tracks;
    }

    public void loadAllTracks() {
        repo.getAllTracks(tracks::postValue, e -> e.printStackTrace());
    }

    public void loadTracksByAlbum(String albumId) {
        repo.getTracksByAlbum(albumId, tracks::postValue, e -> e.printStackTrace());
    }
}
