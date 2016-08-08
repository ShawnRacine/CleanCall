package com.racine.cleancalls;

import android.app.Application;

/**
 * @author Shawn Racine.
 */
public class IApplication extends Application {
    private static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Application getInstance() {
        return instance;
    }
}
