package com.mess.loader.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PluginUtils {

    public static Object invokeMethod(ClassLoader pluginClassLoader, String className, String methodName, Object targetInstance,Class<?>[] parameterTypes,Object... args) {
        try {
            Class<?> targetClass = pluginClassLoader.loadClass(className);
            Method targetMethod = targetClass.getMethod(methodName, parameterTypes);
            if (!targetMethod.isAccessible()) {
                targetMethod.setAccessible(true);
            }
            Object instance = java.lang.reflect.Modifier.isStatic(targetMethod.getModifiers()) ? null : targetInstance;
            return targetMethod.invoke(instance, args);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }


}
