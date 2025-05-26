package com.mess.loader;

import android.util.Log;

import com.mess.loader.utils.LogUtils;

/**
 *
 */
public class BaseClassLoader extends ClassLoader{

    private final static String TAG = BaseClassLoader.class.getSimpleName();
    private final ClassLoader baseClassLoader;

    public BaseClassLoader(ClassLoader hostClassLoader, ClassLoader pluginClassLoader) {
        super(hostClassLoader);
        this.baseClassLoader = pluginClassLoader;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // 优先从插件加载
        try {
            Log.d("BaseClassLoader", "find class name :" + name);
            if (name.contains("RewardVideoActivity")) {
                Log.d(TAG, "class Name = " + name);
                Log.d("BaseClassLoader", "find RewardVideoActivity");
            }
            return baseClassLoader.loadClass(name);
        } catch (ClassNotFoundException e) {
            // 如果插件中没有，再从宿主加载
            LogUtils.d("plugin not found class " + name);
            return super.loadClass(name, resolve);
        }
    }
}
