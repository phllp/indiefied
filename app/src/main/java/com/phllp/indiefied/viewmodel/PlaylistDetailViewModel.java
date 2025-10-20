package com.phllp.indiefied.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.ListenerRegistration;
import com.phllp.indiefied.model.PlaylistDoc;
import com.phllp.indiefied.model.Track;
import com.phllp.indiefied.model.UserDoc;
import com.phllp.indiefied.repository.TracksRepository;
import com.phllp.indiefied.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class PlaylistDetailViewModel extends AndroidViewModel {

    private static final String USER_ID = "admin";

    private final UserRepository userRepo = new UserRepository();
    private final TracksRepository tracksRepo = new TracksRepository();

    private final MutableLiveData<String> title = new MutableLiveData<>("Playlist");
    private final MutableLiveData<List<Track>> tracks = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);

    private ListenerRegistration userReg;

    public PlaylistDetailViewModel(@NonNull Application app) {
        super(app);
        FirebaseApp.initializeApp(app);
    }

    public LiveData<String> getTitle() { return title; }
    public LiveData<List<Track>> getTracks() { return tracks; }
    public LiveData<Boolean> isLoading() { return loading; }

    public void start(String playlistName) {
        title.setValue(playlistName != null ? playlistName : "Playlist");
        // Escuta o usuário e atualiza a lista quando mudar
        if (userReg != null) userReg.remove();
        userReg = userRepo.listenUser(USER_ID, new MutableLiveData<UserDoc>() {{
            observeForever(doc -> {
                if (doc == null || doc.getPlaylists() == null) {
                    tracks.postValue(new ArrayList<>());
                    return;
                }
                List<String> ids = null;
                for (PlaylistDoc p : doc.getPlaylists()) {
                    if (p != null && playlistName.equals(p.getName())) {
                        ids = p.getTracks();
                        break;
                    }
                }
                loadTracks(ids);
            });
        }});
    }

    private void loadTracks(List<String> ids) {
        loading.postValue(true);
        Task<List<Track>> task = tracksRepo.loadTracksByIds(ids != null ? ids : new ArrayList<>());
        task.addOnSuccessListener(list -> {
            tracks.postValue(list);
            loading.postValue(false);
        }).addOnFailureListener(e -> {
            tracks.postValue(new ArrayList<>());
            loading.postValue(false);
        });
    }

    @Override
    protected void onCleared() {
        if (userReg != null) userReg.remove();
        super.onCleared();
    }
}
