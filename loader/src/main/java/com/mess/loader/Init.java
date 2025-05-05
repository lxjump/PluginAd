package com.mess.loader;

import android.app.Application;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.mess.loader.utils.LogUtils;


public class Init extends ContentProvider {

    @Override
    public boolean onCreate() {
        LogUtils.d("Init::onCreate");
        Application app = (Application) this.getContext().getApplicationContext();
        Loader.getLoaderInstance().loadMod(app.getBaseContext(), app);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
