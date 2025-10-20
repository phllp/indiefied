package com.phllp.indiefied.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.ListenerRegistration;
import com.phllp.indiefied.model.Track;
import com.phllp.indiefied.repository.UserRepository;
import com.phllp.indiefied.session.UserSession;

public class FavoritesViewModel extends AndroidViewModel {

    private final UserRepository repo = new UserRepository();
    private final MutableLiveData<Boolean> isFavorite = new MutableLiveData<>(false);
    private ListenerRegistration favListener;
    private String userId;
    private String userName;

    public FavoritesViewModel(@NonNull Application app) {
        super(app);
        FirebaseApp.initializeApp(app);
        userId = UserSession.getUserId(app);
        userName = UserSession.getUserName(app);

        // garante usuário + playlist Favorites
        repo.ensureFavorites(userId, userName, null, e -> e.printStackTrace());
    }

    public LiveData<Boolean> isFavorite() { return isFavorite; }

    /** Chamar toda vez que a currentTrack mudar */
    public void watchTrack(Track t) {
        if (favListener != null) { favListener.remove(); favListener = null; }
        if (t == null || t.getId() == null || t.getId().isEmpty()) {
            isFavorite.setValue(false);
            return;
        }
        favListener = repo.isFavoriteLive(userId, t.getId(), isFavorite);
    }

    public void toggleFavorite(Track t) {
        if (t == null || t.getId() == null) return;
        repo.toggleFavorite(userId, t.getId(), null, e -> e.printStackTrace());
    }

    @Override
    protected void onCleared() {
        if (favListener != null) favListener.remove();
        super.onCleared();
    }
}
