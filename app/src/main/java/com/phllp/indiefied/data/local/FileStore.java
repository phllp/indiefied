package com.phllp.indiefied.data.local;

import android.content.Context;
import android.os.Environment;
import java.io.*;

public class FileStore {

    public static File getAppMusicDir(Context ctx) {
        return ctx.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
    }

    public static File saveMusic(Context ctx, String fileName, InputStream in) throws IOException {
        File dir = getAppMusicDir(ctx);
        if (dir != null && !dir.exists()) dir.mkdirs();
        File outFile = new File(dir, fileName);
        try (OutputStream out = new FileOutputStream(outFile)) {
            byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) != -1) out.write(buf, 0, len);
        }
        return outFile;
    }
}
