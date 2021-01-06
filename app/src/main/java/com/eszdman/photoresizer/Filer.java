package com.eszdman.photoresizer;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Filer {
    public static File[] getPhotoList(int days) {
        long timediff = days * 24 * 60 * 60 * 1000;
        long timecur = System.currentTimeMillis();
        File dir = new File(Environment.getExternalStorageDirectory() + "//DCIM//Camera//");
        Log.e("MyApp", "DIR DATA:" + dir.getAbsolutePath());
        File[] photos = dir.listFiles();
        Log.e("MyApp", "FILE DATA:" + photos);
        ArrayList<File> photodated = new ArrayList<>();
        if (photos == null) return null;
        for (int i = 0; i < photos.length; i++) {
            //System.err.println("FILE DATA:" + photos[i].lastModified() +" CURRENT DATA:"+timecur);
            Log.e("MyApp", "FILE DATA:" + photos[i].lastModified() + " CURRENT DATA:" + timecur);
            if (timecur - photos[i].lastModified() <= timediff) photodated.add(photos[i]);
        }
        return photodated.toArray(new File[photodated.size()]);
    }
}
