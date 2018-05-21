package com.akshaykale.objecttranslator;

import android.app.Application;
import android.content.Context;

import com.akshaykale.objecttranslator.login.GoogleApiHelper;

public class OTApplication extends Application{
    static OTApplication sInstance;

    private GoogleApiHelper googleApiHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        googleApiHelper = new GoogleApiHelper(getAppContext());

    }

    public static synchronized OTApplication getsInstance(){
        return sInstance;
    }

    public static Context getAppContext(){
        return sInstance.getApplicationContext();
    }

    public GoogleApiHelper getGoogleApiHelperInstance() {
        return this.googleApiHelper;
    }

    public static GoogleApiHelper getGoogleApiHelper() {
        return getsInstance().getGoogleApiHelperInstance();
    }
}
