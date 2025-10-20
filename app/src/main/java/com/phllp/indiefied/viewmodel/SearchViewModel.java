package com.phllp.indiefied.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.phllp.indiefied.model.Track;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchViewModel extends AndroidViewModel {

    private final FirebaseFirestore db;

    private final MutableLiveData<List<Track>> all = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Track>> filtered = new MutableLiveData<>(new ArrayList<>());
    private String lastQuery = "";

    public SearchViewModel(@NonNull Application app) {
        super(app);

        if (com.google.firebase.FirebaseApp.getApps(app).isEmpty()) {
            com.google.firebase.FirebaseApp.initializeApp(app);
        }
        db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
    }

    public LiveData<List<Track>> getResults() { return filtered; }

    public void loadAllTracks() {
        db.collection("tracks")
                .get()
                .addOnSuccessListener(q -> {
                    List<Track> list = new ArrayList<>();
                    for (QueryDocumentSnapshot d : q) {
                        Track t = d.toObject(Track.class);
                        // garante id = docId caso não tenha no payload
                        if (t.getId() == null || t.getId().isEmpty()) {
                            t.setId(d.getId());
                        }
                        list.add(t);
                    }
                    all.setValue(list);
                    applyFilter(lastQuery); // aplica o filtro atual (inicialmente vazio)
                })
                .addOnFailureListener(e -> {
                    // em produção: post error state
                    filtered.postValue(new ArrayList<>());
                });
    }

    public void applyFilter(String query) {
        lastQuery = query == null ? "" : query.trim();
        List<Track> source = all.getValue();
        if (source == null) source = new ArrayList<>();

        if (lastQuery.isEmpty()) {
            filtered.setValue(new ArrayList<>(source));
            return;
        }

        String q = lastQuery.toLowerCase(Locale.ROOT);
        List<Track> out = new ArrayList<>();
        for (Track t : source) {
            String title = t.getTitle() != null ? t.getTitle().toLowerCase(Locale.ROOT) : "";
            String albumId = t.getAlbum().getTitle() != null ? t.getAlbum().getTitle().toLowerCase(Locale.ROOT) : "";
//            if (title.contains(q) || albumId.contains(q) || (t.getId() != null && t.getId().toLowerCase(Locale.ROOT).contains(q))) {
//                out.add(t);
//            }
            if (title.contains(q) || albumId.contains(q) || (t.getId() != null && t.getId().toLowerCase(Locale.ROOT).contains(q))) {
                out.add(t);
            }
        }
        filtered.setValue(out);
    }
}
