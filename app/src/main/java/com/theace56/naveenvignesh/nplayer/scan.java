package com.theace56.naveenvignesh.nplayer;

import android.os.Environment;

import java.io.File;
import java.util.HashMap;

/**
 * Created by Naveen Vignesh on 15-07-2016.
 */
public class scan {
    final String MEDIA_PATH = "/"+Environment.getExternalStorageDirectory()
            .getPath();
    private HashMap<String, String> songsList = new HashMap<String, String>();
    private String mp3Pattern = ".mp3";

    /**
     * Function to read all mp3 files and store the details in
     * ArrayList
     * */
    public HashMap<String, String> getPlayList() {
        System.out.println(MEDIA_PATH);
        if (MEDIA_PATH != null) {
            File home = new File(MEDIA_PATH);
            File[] listFiles = home.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File file : listFiles) {
                    if (file.isDirectory()) {
                        scanDirectory(file);
                    } else {
                        addSongToList(file,songsList);
                    }
                }
            }
        }
        // return songs list array
        return songsList;
    }

    private void scanDirectory(File directory) {
        if (directory != null) {
            File[] listFiles = directory.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File file : listFiles) {
                    if (file.isDirectory()) {
                        scanDirectory(file); //recursively calling function
                    } else {
                        addSongToList(file,songsList);
                    }
                }
            }
        }
    }

    private void addSongToList(File song,HashMap<String,String> h) {
        if (song.getName().endsWith(mp3Pattern)) {
            h.put(song.getName().substring(0, (song.getName().length() - 4)),song.getPath());
            // Adding each song to SongList
        }
    }
}
