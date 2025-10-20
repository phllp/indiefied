package com.phllp.indiefied.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.*;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.phllp.indiefied.model.Track;
import com.phllp.indiefied.repository.AlbumsRepository;

import java.util.*;

public class AlbumDetailViewModel extends AndroidViewModel {
    private final AlbumsRepository repo = new AlbumsRepository();
    private final MutableLiveData<List<Track>> tracks = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);

    public AlbumDetailViewModel(@NonNull Application app) {
        super(app);
        FirebaseApp.initializeApp(app);
    }

    public LiveData<List<Track>> getTracks() { return tracks; }
    public LiveData<Boolean> isLoading() { return loading; }

    public void load(String albumTitle) {
        loading.setValue(true);
        Task<List<Track>> task = repo.loadTracksByAlbumTitle(albumTitle);
        task.addOnSuccessListener(list -> {
            tracks.setValue(list);
            loading.setValue(false);
        }).addOnFailureListener(e -> {
            tracks.setValue(new ArrayList<>());
            loading.setValue(false);
        });
    }
}
