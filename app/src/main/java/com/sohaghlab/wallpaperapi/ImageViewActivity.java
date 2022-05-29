package com.sohaghlab.wallpaperapi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

public class ImageViewActivity extends AppCompatActivity {
    private PhotoView photoView;
    String mediumUrl="";
    private ProgressBar progressBar;
    private FloatingActionButton downloadButton;
    String originalUrl="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        getSupportActionBar().hide();

        photoView=findViewById(R.id.fullImageView);
        downloadButton=findViewById(R.id.floatingActionButton);



        Intent intent =getIntent();
        mediumUrl = intent.getStringExtra("mediumUrl");
        Glide.with(this).load(mediumUrl).into(photoView);


        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(ImageViewActivity.this);
                builder.setIcon(R.drawable.ic_download);
                builder.setTitle("Choose one");
                builder.setMessage("What do you want?");

                builder.setPositiveButton("Original", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent getimg = getIntent();
                                originalUrl = getimg.getStringExtra("originalUrl");
                                downloadImageNew("Original_Wallpaper", originalUrl);

                            }
                        });

                        builder.setNegativeButton("Medium", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent getimg = getIntent();
                                mediumUrl = getimg.getStringExtra("mediumUrl");
                                downloadImageNew("Medium_Wallpaper", mediumUrl);


                            }
                        });
                        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.create();
                        builder.show();

                    }
                });


    }


    private void downloadImageNew(String filename, String imageUrl){



        if (ActivityCompat.checkSelfPermission(ImageViewActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            try{
                DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                Uri downloadUri = Uri.parse(imageUrl);
                DownloadManager.Request request = new DownloadManager.Request(downloadUri);
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                        .setAllowedOverRoaming(false)
                        .setTitle(filename)
                        .setMimeType("image/jpeg") // Your file type. You can use this code to download other file types also.
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,File.separator + filename + ".jpg");
                dm.enqueue(request);
                Toast.makeText(this, "Downloading ....", Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                Toast.makeText(this, "Download failed."+e, Toast.LENGTH_SHORT).show();
            }

        }else {

            ActivityCompat.requestPermissions(ImageViewActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 44);
        }

    }

}