package com.github.machadowma.videoapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.VideoView;

public class AddActivity extends AppCompatActivity {
    private static final int GALLERY_REQUEST_CODE = 1;
    SQLiteDatabase bancoDados;
    public Button buttonAdd,buttonBuscarVideo;
    public EditText editTextNome;
    public VideoView videoViewAdd;
    String pathToVideo = null;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        editTextNome = (EditText) findViewById(R.id.editTextNome);
        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonBuscarVideo = (Button) findViewById(R.id.buttonBuscarVideo);
        videoViewAdd = (VideoView) findViewById(R.id.videoViewAdd);


        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadastrar();
            }
        });

        buttonBuscarVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickFromGallery();
            }
        });
    }

    public void cadastrar(){
        String valueNome = null;

        if(TextUtils.isEmpty(editTextNome.getText().toString())){
            editTextNome.setError("Este campo é obrigatório");
            return;
        } else {
            valueNome = editTextNome.getText().toString();
        }

        try {
            bancoDados = openOrCreateDatabase("videoapp", MODE_PRIVATE, null);
            String sql = "INSERT INTO video (nome,path) VALUES (?,?)";
            SQLiteStatement stmt = bancoDados.compileStatement(sql);

            stmt.bindString(1, valueNome);
            if(pathToVideo==null){
                stmt.bindNull(2);
            } else {
                stmt.bindString(2, pathToVideo);
            }

            stmt.executeInsert();
            bancoDados.close();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pickFromGallery(){
        //Create an Intent with action as ACTION_PICK
        Intent intent=new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("video/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        //String[] mimeTypes = {"image/jpeg", "image/png"};
        //intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        // Launching the Intent
        startActivityForResult(intent,GALLERY_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode,int resultCode,Intent data){
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == AddActivity.RESULT_OK)
            switch (requestCode){
                case GALLERY_REQUEST_CODE:
                    //data.getData return the content URI for the selected Image
                    Uri selectedVideo = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    // Get the cursor
                    Cursor cursor = getContentResolver().query(selectedVideo, filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();
                    //Get the column index of MediaStore.Images.Media.DATA
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    //Gets the String value in the column
                    pathToVideo = cursor.getString(columnIndex);
                    cursor.close();
                    // Set the Image in ImageView after decoding the String
                    videoViewAdd.setVideoPath(pathToVideo);
                    MediaController mediaController = new MediaController(this);
                    videoViewAdd.setMediaController(mediaController);
                    videoViewAdd.start();
                    break;

            }
    }
}
