package com.phllp.indiefied.repository;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.phllp.indiefied.model.Track;

import java.util.*;

public class TracksRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Task<List<Track>> loadTracksByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return Tasks.forResult(new ArrayList<>());
        }

        // Firestore whereIn aceita até 10 itens -> dividir em lotes
        List<Task<QuerySnapshot>> tasks = new ArrayList<>();
        for (int i = 0; i < ids.size(); i += 10) {
            List<String> chunk = ids.subList(i, Math.min(i + 10, ids.size()));
            tasks.add(db.collection("tracks")
                    .whereIn(FieldPath.documentId(), chunk)
                    .get());
        }

        return Tasks.whenAllSuccess(tasks).continueWith(t -> {
            List<Track> result = new ArrayList<>();
            for (Object obj : t.getResult()) {
                QuerySnapshot qs = (QuerySnapshot) obj;
                for (DocumentSnapshot d : qs.getDocuments()) {
                    Track tr = d.toObject(Track.class);
                    if (tr != null && (tr.getId() == null || tr.getId().isEmpty()))
                        tr.setId(d.getId());
                    if (tr != null) result.add(tr);
                }
            }
            // Ordena conforme a ordem dos ids
            Map<String, Integer> order = new HashMap<>();
            for (int i = 0; i < ids.size(); i++) order.put(ids.get(i), i);
            result.sort(Comparator.comparingInt(a ->
                    order.getOrDefault(a.getId(), Integer.MAX_VALUE)));
            return result;
        });
    }
}
