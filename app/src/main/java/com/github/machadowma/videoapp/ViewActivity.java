package com.github.machadowma.videoapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;

public class ViewActivity extends AppCompatActivity {
    public SQLiteDatabase bancoDados;
    public Integer idSelecionado;
    public TextView textView;
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        Intent intent = getIntent();
        idSelecionado = intent.getIntExtra("id",0);

        videoView = (VideoView) findViewById(R.id.videoView);
        textView = (TextView) findViewById(R.id.textView);

        carregarDados();
    }

    public void carregarDados(){
        try {
            bancoDados = openOrCreateDatabase("videoapp", MODE_PRIVATE, null);
            Cursor cursor = bancoDados.rawQuery("SELECT id,nome,path FROM video WHERE id = " + idSelecionado, null);
            cursor.moveToFirst();
            textView.setText(cursor.getString(cursor.getColumnIndex("nome")));
            if (cursor.getString(cursor.getColumnIndex("path")) != null) {
                String pathToVideo = cursor.getString(cursor.getColumnIndex("path"));
                File f = new File(pathToVideo);
                if (f.exists()) {
                    videoView.setVideoPath(pathToVideo);
                    MediaController mediaController = new MediaController(this);
                    videoView.setMediaController(mediaController);
                    videoView.start();
                }
            }
            bancoDados.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
