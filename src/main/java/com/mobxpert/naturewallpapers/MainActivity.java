package com.mobxpert.naturewallpapers;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mobxpert.naturewallpapers.interfaces.OnImageClickListner;
import com.mobxpert.naturewallpapers.utils.Utility;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {


    SwipeRefreshLayout mSwipeRefresh;
    AdView mAdView;
    AllWallpapersAdapter adapter;
    RecyclerView mRecyclerView1;
    List<String> myGallery1 = new ArrayList<>();
    StorageReference mStorageRef;
    InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


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

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial));
        AdRequest adRequest1 = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest1);


        mRecyclerView1 = findViewById(R.id.recyclerView1);
        mSwipeRefresh = findViewById(R.id.mSwipeRefresh);
        mSwipeRefresh.setOnRefreshListener(this);

        mSwipeRefresh.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefresh.setRefreshing(true);
                getPicturesFromFirebase();
            }
        });
        mRecyclerView1.setLayoutManager(new GridLayoutManager(this, 3));

    }


    private void getPicturesFromFirebase() {
        if (Utility.isConnectingToInternet(this)) {
            //showLoadingDialog();
            final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference messagesRef = rootRef.child("Wallpaers");
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String key = ds.getKey();

                        DatabaseReference keyRef = rootRef.child("Wallpaers").child("NatureWallpapers");

                        ValueEventListener valueEventListener = new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                mSwipeRefresh.setRefreshing(false);
                                myGallery1.clear();
                                for (DataSnapshot ds1 : dataSnapshot.getChildren()) {
                                    myGallery1.add(ds1.getValue(String.class));
                                }
                                adapter = new AllWallpapersAdapter(MainActivity.this, myGallery1, new OnImageClickListner() {
                                    @Override
                                    public void OnImageClick(final String image) {

                                        if (interstitialAd.isLoaded() && interstitialAd != null) {
                                            interstitialAd.show();
                                        } else {
                                            Intent intent = new Intent(MainActivity.this, FullViewImage.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putSerializable("image_Object", image);
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                        }
                                        if (interstitialAd != null) {
                                            interstitialAd.setAdListener(new AdListener() {
                                                @Override
                                                public void onAdClosed() {
                                                    Intent intent = new Intent(MainActivity.this, FullViewImage.class);
                                                    Bundle bundle = new Bundle();
                                                    bundle.putSerializable("image_Object", image);
                                                    intent.putExtras(bundle);
                                                    startActivity(intent);
                                                    AdRequest adInterstitialRequest = new AdRequest.Builder().build();
                                                    interstitialAd.loadAd(adInterstitialRequest);
                                                }
                                            });
                                        }


                                    }
                                });
                                mRecyclerView1.setAdapter(adapter);
                            }


                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                mSwipeRefresh.setRefreshing(false);
                            }
                        };
                        keyRef.addListenerForSingleValueEvent(valueEventListener);
                        myGallery1.clear();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    mSwipeRefresh.setRefreshing(false);
                }
            };
            messagesRef.addListenerForSingleValueEvent(eventListener);
        } else {
            Toast.makeText(this, "No internet available", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRefresh() {


        if (interstitialAd.isLoaded() && interstitialAd != null) {
            interstitialAd.show();
        } else {
            getPicturesFromFirebase();
        }
        if (interstitialAd != null) {
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    getPicturesFromFirebase();
                    AdRequest adInterstitialRequest = new AdRequest.Builder().build();
                    interstitialAd.loadAd(adInterstitialRequest);
                }
            });
        }


    }
}
