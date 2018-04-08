package com.houwei.guaishang.tools;

import android.app.Application;

/**
 * Created by ** on 2018/4/6.
 */

public class ApplicationProvider {

    public static Application application;

    public static Application privode() {
        return application;
    }

    public static void init(Application app) {
        application = app;
    }
}
