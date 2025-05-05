package com.mess.loader.utils;

import android.content.Context;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class FileUtils {
    private static final int BUFFER_SIZE = 8192;


    public static boolean deleteFile(final File file) {
        return file != null && (!file.exists() || file.isFile() && file.delete());
    }


    public static boolean copyOrMoveFile(final File srcFile,
                                         final File destFile,
                                         final OnReplaceListener listener,
                                         final boolean isMove) {
        if (srcFile == null || destFile == null) return false;
        // srcFile equals destFile then return false
        if (srcFile.equals(destFile)) return false;
        // srcFile doesn't exist or isn't a file then return false
        if (!srcFile.exists() || !srcFile.isFile()) return false;
        if (destFile.exists()) {
            if (listener == null || listener.onReplace()) {// require delete the old file
                if (!destFile.delete()) {// unsuccessfully delete then return false
                    return false;
                }
            } else {
                return true;
            }
        }
        if (!createOrExistsDir(destFile.getParentFile())) return false;
        try {
            return writeFileFromIS(destFile, new FileInputStream(srcFile), false)
                    && !(isMove && !deleteFile(srcFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean createOrExistsDir(final File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }


    public static boolean copyFileFromAssets(final Context context, final String assetsFilePath, final String destFilePath) {
        boolean res = true;
        try {
            final String[] assets = context.getAssets().list(assetsFilePath);
            if (assets != null && assets.length > 0) {
                for (String asset : assets) {
                    res &= copyFileFromAssets(context, assetsFilePath + "/" + asset, destFilePath + "/" + asset);
                }
            } else {
                res = writeFileFromIS(
                        destFilePath,
                        context.getAssets().open(assetsFilePath),
                        false
                );
            }
        } catch (IOException e) {
            LogUtils.e(e.toString());
            res = false;
        }
        return res;
    }


    /**
     * Return the name of file.
     *
     * @param file The file.
     * @return the name of file
     */
    public static String getFileName(final File file) {
        if (file == null) return "";
        return getFileName(file.getAbsolutePath());
    }


    /**
     * Return the name of file.
     *
     * @param filePath The path of file.
     * @return the name of file
     */
    public static String getFileName(final String filePath) {
        if (isSpace(filePath)) return "";
        int lastSep = filePath.lastIndexOf(File.separator);
        return lastSep == -1 ? filePath : filePath.substring(lastSep + 1);
    }


    /**
     * Return the name of file without extension.
     *
     * @param file The file.
     * @return the name of file without extension
     */
    public static String getFileNameNoExtension(final File file) {
        if (file == null) return "";
        return getFileNameNoExtension(file.getPath());
    }


    /**
     * Return the name of file without extension.
     *
     * @param filePath The path of file.
     * @return the name of file without extension
     */
    public static String getFileNameNoExtension(final String filePath) {
        if (isSpace(filePath)) return "";
        int lastPoi = filePath.lastIndexOf('.');
        int lastSep = filePath.lastIndexOf(File.separator);
        if (lastSep == -1) {
            return (lastPoi == -1 ? filePath : filePath.substring(0, lastPoi));
        }
        if (lastPoi == -1 || lastSep > lastPoi) {
            return filePath.substring(lastSep + 1);
        }
        return filePath.substring(lastSep + 1, lastPoi);
    }


    public static long getAssetsFileLength(final Context context, final String assetsFile) {
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(assetsFile);
            return inputStream.available();
        } catch (IOException e) {
            LogUtils.e(e.toString());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LogUtils.e(e.toString());
                }
            }
        }
        return 0;
    }


    public static boolean isFileExists(final File file) {
        return file.exists() && file.isFile();
    }


    public static boolean isFileNotExists(final File file) {
        return !isFileExists(file);
    }


    public static boolean isDirExists(final File file) {
        return file.exists() && file.isDirectory();
    }


    public static boolean isDirNotExists(final File file) {
        return !isDirExists(file);
    }


    public static boolean checkFileExists(final File file) {
        return file.exists() && file.isFile();
    }


    public static boolean checkSameFile(final File file1, final File file2) {
        return file1.lastModified() == file2.lastModified()
                && file1.length() == file2.length();
    }


    public static boolean writeFileFromIS(final String filePath,
                                          final InputStream is,
                                          final boolean append) {
        return writeFileFromIS(getFileByPath(filePath), is, append);
    }


    public static boolean writeFileFromIS(final File file,
                                          final InputStream is,
                                          final boolean append) {
        if (!createOrExistsFile(file) || is == null) return false;
        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file, append));
            byte[] data = new byte[BUFFER_SIZE];
            int len;
            while ((len = is.read(data, 0, BUFFER_SIZE)) != -1) {
                os.write(data, 0, len);
            }
            return true;
        } catch (IOException e) {
            LogUtils.e(e.toString());
            return false;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                LogUtils.e(e.toString());
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                LogUtils.e(e.toString());
            }
        }
    }


    public static File getFileByPath(final String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }


    public static boolean createOrExistsFile(final File file) {
        if (file == null) return false;
        if (file.exists()) return file.isFile();
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            LogUtils.e(e.toString());
            return false;
        }
    }

    public static boolean deleteDir(final File dir) {
        if (dir == null) return false;
        // dir doesn't exist then return true
        if (!dir.exists()) return true;
        // dir isn't a directory then return false
        if (!dir.isDirectory()) return false;
        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.isFile()) {
                    if (!file.delete()) return false;
                } else if (file.isDirectory()) {
                    if (!deleteDir(file)) return false;
                }
            }
        }
        return dir.delete();
    }


    public static boolean deleteFilesInDir(final File dir) {
        if (dir == null) return false;
        // dir doesn't exist then return true
        if (!dir.exists()) return true;
        // dir isn't a directory then return false
        if (!dir.isDirectory()) return false;
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {

                if (file.isFile()) {
                    if (!file.delete()) return false;
                } else if (file.isDirectory()) {
                    if (!deleteDir(file)) return false;
                }

            }
        }
        return true;
    }

    public static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }


    public interface OnReplaceListener {
        boolean onReplace();
    }

}
