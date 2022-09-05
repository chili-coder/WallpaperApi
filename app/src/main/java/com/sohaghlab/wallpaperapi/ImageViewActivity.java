package com.sohaghlab.wallpaperapi;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;

public class ImageViewActivity extends AppCompatActivity {
    private PhotoView photoView;
    String mediumUrl="";
    private ProgressBar progressBar;

    String originalUrl="";
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private com.getbase.floatingactionbutton.FloatingActionButton downloadButton;
    private com.getbase.floatingactionbutton.FloatingActionButton setwalpaperButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        //getSupportActionBar().hide();

        photoView=findViewById(R.id.fullImageView);
        downloadButton=findViewById(R.id.FloatingActionDownload);
        setwalpaperButton=findViewById(R.id.FloatingActionSetWallpaper);


        //banner ads
        mAdView=findViewById(R.id.adView3);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView3);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        /////end baneer
        setInstaAds();

        Intent intent =getIntent();
        mediumUrl = intent.getStringExtra("mediumUrl");
        Glide.with(this).load(mediumUrl).into(photoView);



        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(ImageViewActivity.this);
                builder.setIcon(R.drawable.ic_download);
                builder.setTitle("Download");
                builder.setMessage("What do you want wallpaper size?");

                builder.setPositiveButton("Original", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent getimg = getIntent();
                                originalUrl = getimg.getStringExtra("originalUrl");
                                downloadImageNew("Original_Wallpaper", originalUrl);
                                if (mInterstitialAd!=null){
                                    mInterstitialAd.show(ImageViewActivity.this);
                                }

                            }
                        });

                        builder.setNegativeButton("Medium", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent getimg = getIntent();
                                mediumUrl = getimg.getStringExtra("mediumUrl");
                                downloadImageNew("Medium_Wallpaper", mediumUrl);
                                if (mInterstitialAd!=null){
                                    mInterstitialAd.show(ImageViewActivity.this);
                                }

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

        setwalpaperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(ImageViewActivity.this);
                Bitmap bitmap  = ((BitmapDrawable)photoView.getDrawable()).getBitmap();
                try {
                    wallpaperManager.setBitmap(bitmap);
                    if (mInterstitialAd!=null){
                        mInterstitialAd.show(ImageViewActivity.this);
                    }
                    Toast.makeText(ImageViewActivity.this, "Wallpaper Set", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void setInstaAds() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,getString(R.string.admob_insta_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        mInterstitialAd = null;
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