package com.phllp.indiefied.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.phllp.indiefied.model.PlaylistDoc;
import com.phllp.indiefied.model.UserDoc;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private static final String FAVORITES = "Favorites";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public DocumentReference userRef(String userId) {
        return db.collection("users").document(userId);
    }


    /** Garante user + playlist Favorites */
    public void ensureFavorites(String userId, String userName, Runnable onDone, java.util.function.Consumer<Exception> onError) {
        DocumentReference ref = userRef(userId);
        db.runTransaction(tx -> {
                    DocumentSnapshot snap = tx.get(ref);
                    UserDoc user;

                    if (!snap.exists()) {
                        user = new UserDoc(userName);
                        List<PlaylistDoc> pls = new ArrayList<>();
                        pls.add(new PlaylistDoc(FAVORITES));
                        user.setPlaylists(pls);
                        tx.set(ref, user);
                        return null;
                    }

                    user = snap.toObject(UserDoc.class);
                    if (user.getPlaylists() == null) user.setPlaylists(new ArrayList<>());

                    boolean hasFav = false;
                    for (PlaylistDoc p : user.getPlaylists()) {
                        if (FAVORITES.equals(p.getName())) { hasFav = true; break; }
                    }
                    if (!hasFav) {
                        user.getPlaylists().add(new PlaylistDoc(FAVORITES));
                        tx.set(ref, user); // sobrescreve com a nova lista (merge simples)
                    }
                    return null;
                }).addOnSuccessListener(v -> { if (onDone != null) onDone.run(); })
                .addOnFailureListener(e -> { if (onError != null) onError.accept(e); });
    }

    /** LiveData<Boolean> que responde se trackId está nos favoritos */
    public ListenerRegistration isFavoriteLive(String userId, String trackId, MutableLiveData<Boolean> liveOut) {
        DocumentReference ref = userRef(userId);
        return ref.addSnapshotListener((snap, e) -> {
            if (e != null || snap == null || !snap.exists()) {
                liveOut.postValue(false); return;
            }
            UserDoc user = snap.toObject(UserDoc.class);
            boolean fav = false;
            if (user != null && user.getPlaylists() != null) {
                for (PlaylistDoc p : user.getPlaylists()) {
                    if (FAVORITES.equals(p.getName())) {
                        List<String> t = p.getTracks();
                        fav = (t != null && trackId != null && t.contains(trackId));
                        break;
                    }
                }
            }
            liveOut.postValue(fav);
        });
    }

    /** Alterna: adiciona/remove a track em Favorites (transaction, reescreve playlists) */
    public void toggleFavorite(String userId, String trackId, Runnable onDone, java.util.function.Consumer<Exception> onError) {
        DocumentReference ref = userRef(userId);
        db.runTransaction(tx -> {
                    DocumentSnapshot snap = tx.get(ref);
                    UserDoc user;
                    if (!snap.exists()) return null; // chame ensureFavorites antes

                    user = snap.toObject(UserDoc.class);
                    if (user.getPlaylists() == null) user.setPlaylists(new ArrayList<>());

                    PlaylistDoc fav = null;
                    for (PlaylistDoc p : user.getPlaylists()) {
                        if (FAVORITES.equals(p.getName())) { fav = p; break; }
                    }
                    if (fav == null) {
                        fav = new PlaylistDoc(FAVORITES);
                        user.getPlaylists().add(fav);
                    }
                    if (fav.getTracks() == null) fav.setTracks(new ArrayList<>());

                    if (trackId != null) {
                        if (fav.getTracks().contains(trackId)) {
                            fav.getTracks().remove(trackId);
                        } else {
                            fav.getTracks().add(trackId);
                        }
                    }
                    tx.set(ref, user);
                    return null;
                }).addOnSuccessListener(v -> { if (onDone != null) onDone.run(); })
                .addOnFailureListener(e -> { if (onError != null) onError.accept(e); });
    }



    /** Escuta o documento do usuário em tempo real */
    public ListenerRegistration listenUser(String userId, MutableLiveData<UserDoc> liveOut) {
        return userRef(userId).addSnapshotListener((snap, e) -> {
            if (e != null || snap == null || !snap.exists()) {
                liveOut.postValue(null);
            } else {
                UserDoc u = snap.toObject(UserDoc.class);
                if (u != null && u.getPlaylists() == null) u.setPlaylists(new ArrayList<>());
                liveOut.postValue(u);
            }
        });
    }

    /** Cria uma playlist (ignora se já existir com mesmo nome, case-sensitive simples) */
    public void addPlaylist(String userId, String name, Runnable onOk, java.util.function.Consumer<Exception> onErr) {
        DocumentReference ref = userRef(userId);
        db.runTransaction(tx -> {
                    DocumentSnapshot snap = tx.get(ref);
                    if (!snap.exists()) return null; // crie o user antes (ex: ensureFavorites)
                    UserDoc user = snap.toObject(UserDoc.class);
                    if (user.getPlaylists() == null) user.setPlaylists(new ArrayList<>());
                    for (PlaylistDoc p : user.getPlaylists()) {
                        if (name.equals(p.getName())) {
                            return null; // já existe; no-op
                        }
                    }
                    PlaylistDoc novo = new PlaylistDoc(name);
                    user.getPlaylists().add(novo);
                    tx.set(ref, user);
                    return null;
                }).addOnSuccessListener(v -> { if (onOk != null) onOk.run(); })
                .addOnFailureListener(e -> { if (onErr != null) onErr.accept(e); });
    }

    // com.phllp.indiefied.repository.UserRepository (adicione)
    public void addTrackToPlaylist(String userId, String playlistName, String trackId,
                                   Runnable onOk, java.util.function.Consumer<Exception> onErr) {
        DocumentReference ref = userRef(userId);
        db.runTransaction(tx -> {
                    DocumentSnapshot snap = tx.get(ref);
                    if (!snap.exists()) return null;
                    UserDoc user = snap.toObject(UserDoc.class);
                    if (user.getPlaylists() == null) user.setPlaylists(new ArrayList<>());

                    PlaylistDoc target = null;
                    for (PlaylistDoc p : user.getPlaylists()) {
                        if (playlistName.equals(p.getName())) { target = p; break; }
                    }
                    if (target == null) {
                        target = new PlaylistDoc(playlistName);
                        user.getPlaylists().add(target);
                    }
                    if (target.getTracks() == null) target.setTracks(new ArrayList<>());
                    if (trackId != null && !target.getTracks().contains(trackId)) {
                        target.getTracks().add(trackId);
                    }
                    tx.set(ref, user);
                    return null;
                }).addOnSuccessListener(v -> { if (onOk != null) onOk.run(); })
                .addOnFailureListener(e -> { if (onErr != null) onErr.accept(e); });
    }

}
