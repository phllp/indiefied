package com.phllp.indiefied.session;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

public class UserSession {
    private static final String PREF = "indiefied_prefs";
    private static final String KEY_USER_ID = "admin";
    private static final String KEY_USER_NAME = "Felipe Beiger";

    // @Todo por hora só temos um usuário, futuramente será o usuário logado
    public static String getUserId(Context ctx) {
        return KEY_USER_ID;
    }

    public static String getUserName(Context ctx) {
          return KEY_USER_NAME;
    }
}
