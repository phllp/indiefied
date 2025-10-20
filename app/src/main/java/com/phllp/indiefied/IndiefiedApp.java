package com.phllp.indiefied;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class IndiefiedApp extends Application {
    @Override public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}
