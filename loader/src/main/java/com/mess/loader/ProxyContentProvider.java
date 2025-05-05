package com.mess.loader;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

public class ProxyContentProvider extends ContentProvider {

    private static final String TAG = "ProxyContentProvider";
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    private static final Map<String, ContentProvider> PLUGIN_PROVIDERS = new HashMap<>();

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        ContentProvider pluginProvider = getPluginProvider(uri);
        if (pluginProvider != null) {
            return pluginProvider.query(uri, projection, selection, selectionArgs, sortOrder);
        }
        return null;
    }

    @Override
    public String getType(Uri uri) {
        ContentProvider pluginProvider = getPluginProvider(uri);
        if (pluginProvider != null) {
            return pluginProvider.getType(uri);
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        ContentProvider pluginProvider = getPluginProvider(uri);
        if (pluginProvider != null) {
            return pluginProvider.insert(uri, values);
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        ContentProvider pluginProvider = getPluginProvider(uri);
        if (pluginProvider != null) {
            return pluginProvider.delete(uri, selection, selectionArgs);
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        ContentProvider pluginProvider = getPluginProvider(uri);
        if (pluginProvider != null) {
            return pluginProvider.update(uri, values, selection, selectionArgs);
        }
        return 0;
    }

    private ContentProvider getPluginProvider(Uri uri) {
        String authority = uri.getAuthority();
        return PLUGIN_PROVIDERS.get(authority);
    }

    public static void registerPluginProvider(String authority, ContentProvider provider) {
        PLUGIN_PROVIDERS.put(authority, provider);
    }
}
