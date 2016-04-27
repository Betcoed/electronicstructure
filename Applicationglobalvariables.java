package com.sciencehighgames.electronicstructure;

import android.app.Application;

/**
 * Created by sarahhinsley on 24/01/2015.
 */
public class Applicationglobalvariables extends Application {

    private static int StartValue = 0;

    private static Applicationglobalvariables singleton;
    public static int result;

    public static Applicationglobalvariables getInstance() {
        
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
    }
}
