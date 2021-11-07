package com.affable.smartbills.utils;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

public class MultiLanguage extends Application {


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleManager.setLocale(this);
    }


}
