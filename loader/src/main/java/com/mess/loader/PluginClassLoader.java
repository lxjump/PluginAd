package com.mess.loader;

import dalvik.system.DexClassLoader;

public class PluginClassLoader extends DexClassLoader {
    /**
     * 创建插件 ClassLoader
     *
     * @param dexPath      插件 APK 文件路径
     * @param optimizedDir 插件解压路径（存放优化后的 dex 文件）
     * @param libraryPath  插件的 native 库路径
     * @param parent       父加载器（通常是宿主的 ClassLoader）
     */
    public PluginClassLoader(String dexPath, String optimizedDir, String libraryPath, ClassLoader parent) {
        super(dexPath, optimizedDir, libraryPath, parent);
    }
}
