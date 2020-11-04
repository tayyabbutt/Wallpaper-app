package com.mobxpert.naturewallpapers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.storage.StorageReference;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.mobxpert.naturewallpapers.utils.Utility;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FullViewImage extends AppCompatActivity implements Serializable {
      AdView mAdView;
    ImageView mImageView;

    Button mWallpaperBtn;
    Context mContext;
    WallpaperManager myWallpaperManager;

    ImageView mShareBtn, mBackArrow;
    LinearLayout mBtns;
    Button mDownloadButton;
    StorageReference storageRef;
    String myImage;
    //  InterstitialAd interstitialAd = null;
    private AsyncTask mMyTask;
    private int PERMISSION_ALL = 1;
    private ProgressDialog mProgressDialog;
    public List<String> mGalleryList1 = new ArrayList<String>();
    KProgressHUD progress;
      InterstitialAd interstitialAd;


    Dialog dialog;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        dialog = new Dialog(FullViewImage.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_full__image);


        mWallpaperBtn = findViewById(R.id.mSetBtn);
        mImageView = findViewById(R.id.fullImage);
        mShareBtn = findViewById(R.id.shareTo);
        mBackArrow = findViewById(R.id.backArrow);


        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial));
        AdRequest adRequest1 = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest1);

    /*    mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial));
        AdRequest adInterstitialRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adInterstitialRequest);*/

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        mDownloadButton = findViewById(R.id.mDownBtn);

          mAdView = findViewById(R.id.adView);

        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        MobileAds.initialize(this, getString(R.string.bannerappid));
//        MobileAds.initialize(this, getString(R.string.bannerappid));
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(id)
                .build();
        adRequest.isTestDevice(this);
        boolean istestdeviice = adRequest.isTestDevice(this);
        mAdView.loadAd(adRequest);
        boolean shown = mAdView.isShown();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        String[] PERMISSIONS = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        };
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }


        myWallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        final Bundle bundle = getIntent().getExtras();
//Extract the data…
        myImage = (String) bundle.getSerializable("image_Object");
//        mGalleryList1=  bundle.getStringArrayList("key");
//        Log.d("value of list",mGalleryList1.size()+"");

        Glide.with(getApplicationContext()).load(myImage).into(mImageView);
        /*storageRef = FirebaseStorage.getInstance().getReference();*/

        /*mProgressDialog = new ProgressDialog(FullViewImage.this);
        mProgressDialog.setIndeterminate(true);
        // Progress dialog horizontal style
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // Progress dialog title
        mProgressDialog.setTitle("AsyncTask");
        // Progress dialog message
        mProgressDialog.setMessage("Please wait, we are downloading your image file...");
*/

        mWallpaperBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interstitialAd.isLoaded() && interstitialAd != null) {
                    interstitialAd.show();
                } else {
                    showProgress(FullViewImage.this);
                    showDialog(FullViewImage.this);
                }
                if (interstitialAd != null) {
                    interstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            showProgress(FullViewImage.this);
                            showDialog(FullViewImage.this);
                            AdRequest adInterstitialRequest = new AdRequest.Builder().build();
                            interstitialAd.loadAd(adInterstitialRequest);
                        }
                    });
                }

                /* setBackgroundImage();*/
            }
        });

        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interstitialAd.isLoaded() && interstitialAd != null) {
                    interstitialAd.show();
                } else {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    // If you want to share a png image only, you can do:
                    // setType("image/png"); OR for jpeg: setType("image/jpeg");
                    Uri uri = Uri.parse(myImage);
                    share.setType("image/jpeg");
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(share, "Share Image!"));
                }
                if (interstitialAd != null) {
                    interstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            Intent share = new Intent(Intent.ACTION_SEND);
                            // If you want to share a png image only, you can do:
                            // setType("image/png"); OR for jpeg: setType("image/jpeg");
                            Uri uri = Uri.parse(myImage);
                            share.setType("image/jpeg");
                            share.putExtra(Intent.EXTRA_STREAM, uri);
                            startActivity(Intent.createChooser(share, "Share Image!"));
                            AdRequest adInterstitialRequest = new AdRequest.Builder().build();
                            interstitialAd.loadAd(adInterstitialRequest);
                        }
                    });
                }
              /*  Intent share = new Intent(Intent.ACTION_SEND);
                // If you want to share a png image only, you can do:
                // setType("image/png"); OR for jpeg: setType("image/jpeg");
                Uri uri = Uri.parse(myImage);
                share.setType("image/jpeg");
                share.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(share, "Share Image!"));*/

            }
        });


        mDownloadButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (interstitialAd.isLoaded() && interstitialAd != null) {
                    interstitialAd.show();
                } else {
                    if (Utility.isConnectingToInternet(FullViewImage.this)) {
                        new DownloadTask(FullViewImage.this, mDownloadButton, myImage);
                    } else {
                        Toast.makeText(mContext, "No internet available", Toast.LENGTH_SHORT).show();
                    }
                }
                if (interstitialAd != null) {
                    interstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            if (Utility.isConnectingToInternet(FullViewImage.this)) {
                                new DownloadTask(FullViewImage.this, mDownloadButton, myImage);
                            } else {
                                Toast.makeText(mContext, "No internet available", Toast.LENGTH_SHORT).show();
                            }
                            AdRequest adInterstitialRequest = new AdRequest.Builder().build();
                            interstitialAd.loadAd(adInterstitialRequest);
                        }
                    });
                }

             /*   if (Utility.isConnectingToInternet(FullViewImage.this)) {
                    new DownloadTask(FullViewImage.this, mDownloadButton, myImage);
                } else {
                    Toast.makeText(mContext, "No internet available", Toast.LENGTH_SHORT).show();
                }*/

            }
        });

    }




    private void setHomeScreenWallpaper() {
        Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
        WallpaperManager manager = WallpaperManager.getInstance(getApplicationContext());
        try {
            hideProgress();
            manager.setBitmap(bitmap);
            dialog.dismiss();
            Toast.makeText(getApplicationContext(), "Wallpaper set on home screen ", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            hideProgress();
            Toast.makeText(getApplicationContext(), "Wallpaper not load yet!", Toast.LENGTH_SHORT).show();
        }
    }


    private void setBackgroundImage() {

        Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
        WallpaperManager manager = WallpaperManager.getInstance(getApplicationContext());
        try {
            hideProgress();
            manager.setBitmap(bitmap);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);

            }
            Toast.makeText(getApplicationContext(), "Wallpaper set on both home and lock screen ", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        } catch (IOException e) {
            hideProgress();
            Toast.makeText(getApplicationContext(), "Wallpaper not load yet!", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("NewApi")
    private void setLockScreenWallpaper() {
        Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
        WallpaperManager manager = WallpaperManager.getInstance(getApplicationContext());
        try {
            hideProgress();
            // manager.setBitmap(bitmap);
            manager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);

            Toast.makeText(getApplicationContext(), "Wallpaper set on lock screen ", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        } catch (IOException e) {
            hideProgress();
            Toast.makeText(getApplicationContext(), "Wallpaper not load yet!", Toast.LENGTH_SHORT).show();
        }
    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void showProgress(Context context) {

        progress = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setAnimationSpeed(2)
                .show();
    }

    public void hideProgress() {
        progress.dismiss();
    }


    public void showDialog(Context context) {


        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = (int) (Utility.getScreenWidth(context) - Utility.convertDpToPixel(Utility.DIALOG_WIDTH_MARGIN, context));
        //  lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        Button homeWallpaper = dialog.findViewById(R.id.homeWallpaper);
        Button lockSCreenWallpaper = dialog.findViewById(R.id.lockScreenWallpaper);
        Button both = dialog.findViewById(R.id.bothWallpaper);
        homeWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHomeScreenWallpaper();
            }
        });
        lockSCreenWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLockScreenWallpaper();
            }
        });
        both.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackgroundImage();
            }
        });

        dialog.show();
    }


}


