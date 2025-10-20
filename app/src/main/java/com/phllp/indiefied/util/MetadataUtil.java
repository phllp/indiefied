package com.phllp.indiefied.util;

import android.media.MediaMetadataRetriever;

import com.phllp.indiefied.model.Track;

public class MetadataUtil {

//    public static Track buildTrackFromFilePath(String filePath) {
//        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//        try {
//            mmr.setDataSource(filePath);
//
//            String title  = safe(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE), "Unknown Title");
//            String artist = safe(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST), "Unknown Artist");
//            String album  = safe(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM), "Unknown Album");
//            String durStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//            int durationMs = 0;
//            if (durStr != null) {
//                try { durationMs = Integer.parseInt(durStr); } catch (NumberFormatException ignored) {}
//            }
//
//            // coverUri opcional: você pode extrair embedded picture e salvar como arquivo; por ora, null
//            return new Track(title, artist, album, null, filePath, durationMs);
//        } finally {
//            mmr.release();
//        }
//    }
//
//    private static String safe(String v, String def) {
//        return (v == null || v.trim().isEmpty()) ? def : v.trim();
//    }
}
