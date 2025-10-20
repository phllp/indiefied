package com.phllp.indiefied.repository;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.CollectionReference;
import com.phllp.indiefied.model.Track;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FirebaseRepository {
    private final FirebaseFirestore db;

    public FirebaseRepository() {
        db = FirebaseFirestore.getInstance();
    }

    // Buscar todas as faixas
    public void getAllTracks(Consumer<List<Track>> onResult, Consumer<Exception> onError) {
        db.collection("tracks")
                .get()
                .addOnSuccessListener(query -> {
                    List<Track> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : query) {
                        Track track = doc.toObject(Track.class);
                        list.add(track);
                    }
                    onResult.accept(list);
                })
                .addOnFailureListener((OnFailureListener) onError);
    }

    // Buscar faixas de um álbum específico
    public void getTracksByAlbum(String albumId, Consumer<List<Track>> onResult, Consumer<Exception> onError) {
        db.collection("tracks")
                .whereEqualTo("albumId", albumId)
                .get()
                .addOnSuccessListener(query -> {
                    List<Track> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : query) {
                        list.add(doc.toObject(Track.class));
                    }
                    onResult.accept(list);
                })
                .addOnFailureListener((OnFailureListener) onError);
    }

    // Buscar faixa específica por id
    public void getTrackById(String trackId, Consumer<Track> onResult, Consumer<Exception> onError) {
        db.collection("tracks")
                .document(trackId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        onResult.accept(doc.toObject(Track.class));
                    } else {
                        onResult.accept(null);
                    }
                })
                .addOnFailureListener((OnFailureListener) onError);
    }
}
