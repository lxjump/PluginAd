package com.mess.loader.abi;


import android.content.Context;


import com.mess.loader.utils.ByteUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ProcessAbiDetector {

    private static NativeAbi abi;

    public static NativeAbi getAbi(Context context) {
        if (abi == null) {
            abi = detect(context);
        }
        return abi;
    }

    private static NativeAbi detect(Context context) {
        NativeAbi result = detectPrivateLibsAbi(context);
        if (result == NativeAbi.UNKNOWN) {
            result = detectProcessAbi(context);
        }
        return result;
    }

    private static NativeAbi detectPrivateLibsAbi(Context context) {
        File file = queryAppLargestPrivateLib(context);
        if (file == null) {
            file = queryProcessLibcFile(context);
        }
        return file != null ? getAbi(file) : NativeAbi.UNKNOWN;
    }

    private static NativeAbi detectProcessAbi(Context context) {
        File file = queryProcessLibcFile(context);
        return file != null ? getAbi(file) : NativeAbi.UNKNOWN;
    }

    private static File queryAppLargestPrivateLib(Context context) {
        File dir = new File(context.getApplicationInfo().nativeLibraryDir);
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null && files.length > 0) {
                File largestFile = null;
                long maxSize = 0;
                for (File file : files) {
                    if (file.isFile() && file.length() > maxSize) {
                        largestFile = file;
                        maxSize = file.length();
                    }
                }
                return largestFile;
            }
        }
        return null;
    }

    private static File queryProcessLibcFile(Context context) {
        try {
            ClassLoader classLoader = context.getClassLoader();
            java.lang.reflect.Method method = ClassLoader.class.getDeclaredMethod("findLibrary", String.class);
            String path = (String) method.invoke(classLoader, "c");
            if (path != null) {
                return new File(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static NativeAbi getAbi(File file) {
        if (file.exists() && file.isFile()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[32];
                int count = fis.read(buffer, 0, 32);
                if (count == 32) {
                    if (compareEqual(buffer[0], (byte) 0x7f) &&
                            compareEqual(buffer[1], (byte) 0x45) &&
                            compareEqual(buffer[2], (byte) 0x4c) &&
                            compareEqual(buffer[3], (byte) 0x46)) { // .elf magic number
                        int magic = ByteUtils.byteArrayToIntLittleEndian(new byte[]{buffer[18], buffer[19], 0x00, 0x00});
                        return NativeAbi.create(magic);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return NativeAbi.UNKNOWN;
    }

    private static boolean compareEqual(byte b, byte other) {
        return b == other;
    }
}
