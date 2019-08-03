package com.github.machadowma.videoapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS = 1;
    SQLiteDatabase bancoDados;
    public ListView listView;
    public ArrayList<Integer> arrayIds;
    public Integer idSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadastrar();
            }
        });

        listView = (ListView) findViewById(R.id.listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                abrir(i);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                excluirDado(i);
                return true;
            }
        });



        criarBancoDados();
        requestPermissions();
        listarDados();
    }

    @Override
    protected void onResume() {
        super.onResume();
        listarDados();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void requestPermissions(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }

    public void criarBancoDados(){
        try {
            bancoDados = openOrCreateDatabase("videoapp", MODE_PRIVATE, null);
            //bancoDados.execSQL("DROP TABLE ingrediente");
            //bancoDados.execSQL("DROP TABLE receita");
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS video(" +
                    " id INTEGER PRIMARY KEY AUTOINCREMENT" +
                    " , nome VARCHAR" +
                    " , path VARCHAR)");
            bancoDados.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listarDados(){
        try {
            bancoDados = openOrCreateDatabase("videoapp", MODE_PRIVATE, null);
            Cursor cursor = bancoDados.rawQuery("SELECT id,nome,path FROM video",null);
            ArrayList<String> linhas = new ArrayList<String>();
            ArrayAdapter adapter = new ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    linhas
            );
            listView.setAdapter(adapter);
            cursor.moveToFirst();
            arrayIds = new ArrayList<Integer>();
            while(cursor!=null){
                arrayIds.add(cursor.getInt(cursor.getColumnIndex("id")));
                linhas.add(cursor.getString(cursor.getColumnIndex("nome")));
                cursor.moveToNext();
            }
            bancoDados.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cadastrar(){
        Intent intent = new Intent(this,AddActivity.class);
        startActivity(intent);
    }

    public void abrir(Integer i){
        idSelecionado = arrayIds.get(i);
        Intent intent = new Intent(this,ViewActivity.class);
        intent.putExtra("id",idSelecionado);
        startActivity(intent);
    }

    public void excluirDado(Integer i){
        idSelecionado = arrayIds.get(i);
        try {
            bancoDados = openOrCreateDatabase("videoapp", MODE_PRIVATE, null);
            String sql = "DELETE FROM video WHERE id = ?";
            SQLiteStatement stmt = bancoDados.compileStatement(sql);
            stmt.bindLong(1, idSelecionado);
            stmt.executeUpdateDelete();
            bancoDados.close();
            listarDados();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
