package com.vikaskaushik.instagramclone.Utils;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class FileSearch {
    private static final String TAG = "FileSearch";

    /**
     * Search a directory and return list of  directories contained inside
     *
     * @param directory
     * @return
     */
    public static ArrayList<String> getDirectoryPaths(String directory) {
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listFiles = file.listFiles();
        try {
            for (int i = 0; i < listFiles.length; i++) {
                if (listFiles[i].isDirectory()) {
                    pathArray.add(listFiles[i].getAbsolutePath());
                }
            }
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException: " + e.getMessage());
        }

        return pathArray;
    }

    /**
     * Search a file and return list of  files contained inside
     *
     * @param directory
     * @return
     */
    public static ArrayList<String> getFilePaths(String directory) {
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listFiles = file.listFiles();
        for (int i = 0; i < listFiles.length; i++) {
            if (listFiles[i].isFile()) {
                pathArray.add(listFiles[i].getAbsolutePath());
            }
        }
        return pathArray;
    }
}
