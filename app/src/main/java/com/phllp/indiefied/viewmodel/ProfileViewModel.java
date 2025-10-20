package com.phllp.indiefied.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.ListenerRegistration;
import com.phllp.indiefied.model.PlaylistDoc;
import com.phllp.indiefied.model.UserDoc;
import com.phllp.indiefied.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class ProfileViewModel extends AndroidViewModel {

    private static final String USER_ID = "admin";

    private final UserRepository repo = new UserRepository();

    private final MutableLiveData<String> userName = new MutableLiveData<>("Usuário");
    private final MutableLiveData<List<PlaylistDoc>> playlists = new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<UserDoc> userDocLive = new MutableLiveData<>();
    private ListenerRegistration reg;
    private final Observer<UserDoc> userObserver = doc -> {
        // NÃO usamos mais doc.getName() aqui; apenas playlists
        if (doc == null || doc.getPlaylists() == null) {
            playlists.postValue(new ArrayList<>());
        } else {
            playlists.postValue(doc.getPlaylists());
        }
    };

    public ProfileViewModel(@NonNull Application app) {
        super(app);
        FirebaseApp.initializeApp(app);

        // 1) Nome direto do UserSession
        String sessionName = com.phllp.indiefied.session.UserSession.getUserName(app);
        userName.setValue(sessionName != null ? sessionName : "Usuário");

        // 2) Ouvir playlists do usuário "admin"
        reg = repo.listenUser(USER_ID, userDocLive);
        userDocLive.observeForever(userObserver);
    }

    public LiveData<String> getUserName() { return userName; }
    public LiveData<List<PlaylistDoc>> getPlaylists() { return playlists; }

    public void createPlaylist(String name) {
        if (name == null) return;
        String trimmed = name.trim();
        if (trimmed.isEmpty()) return;
        repo.addPlaylist(USER_ID, trimmed, null, e -> e.printStackTrace());
    }

    @Override protected void onCleared() {
        if (reg != null) reg.remove();
        userDocLive.removeObserver(userObserver);
        super.onCleared();
    }
}
