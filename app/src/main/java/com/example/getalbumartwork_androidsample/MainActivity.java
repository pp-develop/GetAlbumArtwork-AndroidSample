package com.example.getalbumartwork_androidsample;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.michaelevans.colorart.library.ColorArt;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static int CHOSE_FILE_CODE = 0000;
    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            switch (v.getId()) {
                case R.id.button:
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("audio/*");
                    startActivityForResult(intent, CHOSE_FILE_CODE);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOSE_FILE_CODE && resultCode == RESULT_OK) {
            mUri = data.getData();
            //ファイル名取得
            String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
            Cursor cursor = this.getContentResolver().query(mUri, projection, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    //アルバムアートの取得
                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    mmr.setDataSource(this, mUri);
                    byte[] artByte = mmr.getEmbeddedPicture();
                    if (artByte != null) {
                        InputStream artByteArray = new ByteArrayInputStream(mmr.getEmbeddedPicture());
                        Bitmap bitmap = BitmapFactory.decodeStream(artByteArray);
                        ((ImageView) findViewById(R.id.imageView)).setImageBitmap(bitmap);

                        ColorArt colorArt = new ColorArt(bitmap);
                        int backgroundColor = colorArt.getBackgroundColor();
                        int primaryColor = colorArt.getPrimaryColor();
                        int secondaryColor = colorArt.getSecondaryColor();
                        int detailColor = colorArt.getDetailColor();
                        setTextColor(backgroundColor, primaryColor, secondaryColor, detailColor);
                    } else {
                        ((ImageView) findViewById(R.id.imageView)).setImageResource(R.drawable.no_image);
                        int defaultTextColor = getColor(android.R.color.secondary_text_light);
                        setTextColor(defaultTextColor, defaultTextColor, defaultTextColor, defaultTextColor);
                    }
                }
            }
            cursor.close();
        }
    }

    public void setTextColor(int backgroundColor, int primaryColor, int secondaryColor, int detailColor) {
        ((TextView) findViewById(R.id.textBackgroundColor)).setTextColor(backgroundColor);
        ((TextView) findViewById(R.id.textPrimaryColor)).setTextColor(primaryColor);
        ((TextView) findViewById(R.id.textSecondaryColor)).setTextColor(secondaryColor);
        ((TextView) findViewById(R.id.textDetailColor)).setTextColor(detailColor);
    }
}

