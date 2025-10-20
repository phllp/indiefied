package com.phllp.indiefied.repository;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.*;
import com.phllp.indiefied.model.AlbumItem;
import com.phllp.indiefied.model.Track;

import java.util.*;

public class AlbumsRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /** Carrega todos os álbuns distintos a partir das faixas */
    public Task<List<AlbumItem>> loadAllAlbums() {
        return db.collection("tracks").get().continueWith(t -> {
            QuerySnapshot qs = t.getResult();
            Map<String, AlbumItem> map = new LinkedHashMap<>();
            if (qs != null) {
                for (DocumentSnapshot d : qs.getDocuments()) {
                    Track tr = d.toObject(Track.class);
                    if (tr == null || tr.getAlbum() == null) continue;
                    String albTitle = tr.getAlbum().getTitle();
                    if (albTitle == null || albTitle.isEmpty()) continue;

                    AlbumItem item = map.get(albTitle);
                    if (item == null) {
                        String cover = tr.getAlbum().getCover();
                        String artist = (tr.getArtist() != null) ? tr.getArtist().getName() : null;
                        item = new AlbumItem(albTitle, cover, artist);
                        map.put(albTitle, item);
                    }
                }
            }
            return new ArrayList<>(map.values());
        });
    }

    /** Carrega faixas de um álbum (igualdade simples em album.title) */
    public Task<List<Track>> loadTracksByAlbumTitle(String albumTitle) {
        if (albumTitle == null || albumTitle.isEmpty()) {
            return Tasks.forResult(new ArrayList<>());
        }
        return db.collection("tracks")
                .whereEqualTo("album.title", albumTitle)
                .get()
                .continueWith(t -> {
                    QuerySnapshot qs = t.getResult();
                    List<Track> out = new ArrayList<>();
                    if (qs != null) {
                        for (DocumentSnapshot d : qs.getDocuments()) {
                            Track tr = d.toObject(Track.class);
                            if (tr != null && (tr.getId() == null || tr.getId().isEmpty()))
                                tr.setId(d.getId());
                            if (tr != null) out.add(tr);
                        }
                    }
                    // Ordene como preferir (alfabética):
                    out.sort(Comparator.comparing(Track::getTitle, String.CASE_INSENSITIVE_ORDER));
                    return out;
                });
    }
}
