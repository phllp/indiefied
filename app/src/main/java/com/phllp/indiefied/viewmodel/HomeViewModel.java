package com.phllp.indiefied.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.*;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.phllp.indiefied.model.AlbumItem;
import com.phllp.indiefied.repository.AlbumsRepository;

import java.util.*;

public class HomeViewModel extends AndroidViewModel {
    private final AlbumsRepository repo = new AlbumsRepository();
    private final MutableLiveData<List<AlbumItem>> albums = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);

    public HomeViewModel(@NonNull Application app) {
        super(app);
        FirebaseApp.initializeApp(app);
    }

    public LiveData<List<AlbumItem>> getAlbums() { return albums; }
    public LiveData<Boolean> isLoading() { return loading; }

    public void load() {
        loading.setValue(true);
        Task<List<AlbumItem>> task = repo.loadAllAlbums();
        task.addOnSuccessListener(list -> {
            albums.setValue(list);
            loading.setValue(false);
        }).addOnFailureListener(e -> {
            albums.setValue(new ArrayList<>());
            loading.setValue(false);
        });
    }
}
