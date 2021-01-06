package com.eszdman.photoresizer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.ScriptIntrinsicResize;
import android.renderscript.Type;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    SharedPreferences sPref;
    final String fromprog = "fromvalue";
    final String toprog = "tovalueset";
    private SeekBar fromslider;
    private SeekBar toslider;
    private static final int million = 1000000;
    public static MainActivity instance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_main);
        ProgressBar prog = findViewById(R.id.progressBar);
        prog.setVisibility(View.INVISIBLE);
        fromslider = findViewById(R.id.fromslider);
        toslider = findViewById(R.id.toslider);
        Permissions.RequestPermissions(MainActivity.this, 2,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE});
        loadText();
        fromslider.setMin(1);
        toslider.setMin(1);
        final TextView fromslidertext = findViewById(R.id.fromslidertext);
        final TextView toslidertext = findViewById(R.id.toslidertext);
        fromslidertext.setText(toMP(fromslider.getProgress()));
        toslidertext.setText(toMP(toslider.getProgress()));
        fromslider.getProgress();
        fromslider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                fromslidertext.setText(toMP(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        toslider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                toslidertext.setText(toMP(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        Button resizebyd = findViewById(R.id.ResizeDay);
        resizebyd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Process(1);
            }
        });
        Button resizebym = findViewById(R.id.ResizeMouth);
        resizebym.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Process(30);
            }
        });
    }
    private static String toMP(int in){
        return ((double)(in*5))/10+"mp";
    }
    public void Process(int days){
        File[] photos = Filer.getPhotoList(days);
        ProgressBar prog = findViewById(R.id.progressBar);
        Toast.makeText(MainActivity.this, "Processing...", Toast.LENGTH_SHORT).show();
        if(photos == null) {Toast.makeText(MainActivity.this, "Null Photos!", Toast.LENGTH_SHORT).show(); return;}
        int from = (int)(((double)(fromslider.getProgress()*5))*million/10);
        int to = (int)(((double)(toslider.getProgress()*5))*million/10);
        prog.setMax(photos.length-1);
        prog.setVisibility(View.VISIBLE);
        for(int i =0; i<photos.length; i++) {
            prog.setProgress(i);
            Render.ReSizeFile(photos[i],from,to);
        }
        prog.setVisibility(View.INVISIBLE);
        Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onPause(){
        super.onPause();
        saveText();
    }
    public void onClick(View v){

    }
    void saveText() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(fromprog, String.valueOf(fromslider.getProgress()));
        ed.putString(toprog, String.valueOf(toslider.getProgress()));
        ed.commit();
    }

    void loadText() {
        sPref = getPreferences(MODE_PRIVATE);
        String fromvalue = sPref.getString(fromprog, "50");
        String tovalue = sPref.getString(toprog, "50");
        fromslider.setProgress(Integer.parseInt(fromvalue));
        toslider.setProgress(Integer.parseInt(tovalue));
    }
}
