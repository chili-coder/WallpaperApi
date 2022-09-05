package com.sohaghlab.wallpaperapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    List<WallpaperModel>list;
    WallpaperAdapter adapter;
    private ProgressBar progressBar;

    int pageNum=1;
    Boolean isScroll=false;
    int currentItem,totalItem,scrollOutItem;
    SwipeRefreshLayout swipeRefreshLayout;

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private AdView mAdView;


 String api_uri="https://api.pexels.com/v1/curated/?page="+pageNum+"&per_page=80";
 //String api_uri="https://api.pexels.com/v1/curated/?page=1&per_page=80";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolBar);
        navigationView = findViewById(R.id.navigationView);
        drawer = findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);


        toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.open,R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        recyclerView=findViewById(R.id.recyclerview);
        progressBar=findViewById(R.id.progressBar);
      //  swipeRefreshLayout=findViewById(R.id.swipeRefreshLayout);

        list= new ArrayList<>();
        adapter= new WallpaperAdapter(this,list);

        recyclerView.setAdapter(adapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(gridLayoutManager);


        getWallpaperFromApi();


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawer.closeDrawer(GravityCompat.START);
                switch (item.getItemId()){
                    case R.id.home_nav:
                        Intent intent = new Intent(getApplication(), MainActivity.class);
                        startActivity(intent);
                        finish();

                        break;
                    case R.id.about_nav:
//                        Intent intent = new Intent(getApplication(), AbroadActivity.class);
//                        startActivity(intent);
//                        finish();
                        break;
                    case R.id.share_nav:
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String shareBody = " দেশী ভ্রমণ (Deshi Tour) \n Application Download Link: ";
                        String downloadText="ApplicationDownload Link";
                        String shareUrl= "https://play.google.com/store/apps/details?id=";
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareBody);
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareUrl + getPackageName());
                        startActivity(Intent.createChooser(sharingIntent, "Sharevia"));

                        break;

                    case R.id.rate_nav:
                        try {
                            Uri uri = Uri.parse("market://details?id=" +getPackageName());
                            Intent in = new Intent(Intent.ACTION_VIEW,uri);
                            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(in);
                        } catch (ActivityNotFoundException e){
                            Uri uri =Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName());
                            Intent in =new Intent(Intent.ACTION_VIEW,uri);
                            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(in);
                        }
                        break;

                    case R.id.privacy_nav:
//                        Intent chromeIntent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://sites.google.com/view/deshitour/home"));
//                        startActivity(chromeIntent);
                        break;




                }
                return true;
            }
        });





        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScroll=true;

                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                currentItem=gridLayoutManager.getChildCount();
                totalItem=gridLayoutManager.getItemCount();
                scrollOutItem=gridLayoutManager.findFirstVisibleItemPosition();

                if (isScroll && (currentItem+scrollOutItem==totalItem)){
                    isScroll=false;
                    getWallpaperFromApi();
                }

            }
        });

        //banner ads
        mAdView=findViewById(R.id.adView2);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        /////end baneer

        ///no internet
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();


        if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {

            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.no_internet);
            dialog.setCancelable(false);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().getAttributes().windowAnimations =
                    android.R.style.Animation_Dialog;

            Button retry = dialog.findViewById(R.id.retry);

            retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recreate();
                }
            });
            dialog.show();

        } else {

        } //end retry

    }

    private void getWallpaperFromApi() {

        StringRequest request = new StringRequest(Request.Method.GET,"https://api.pexels.com/v1/curated/?page="+pageNum+"&per_page=80",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("photos");
                            int lenght =jsonArray.length();
                            for (int i=0; i<lenght;i++){

                                //for id
                                JSONObject object = jsonArray.getJSONObject(i);
                                int id =object.getInt("id");
                                String alt=object.getString("alt");


                                ///image scr
                                JSONObject object2 =object.getJSONObject("src");
                                String imageUrl=object2.getString("medium");
                                String mediumUrl=object2.getString("large2x");
                                String orginalUrl=object2.getString("original");

                                WallpaperModel model = new WallpaperModel(id,imageUrl,mediumUrl,orginalUrl,alt);
                                list.add(model);
                            }
                            progressBar.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                            pageNum++;

                        }catch (JSONException e){

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String>paramitars= new HashMap<>();
                paramitars.put("Authorization","563492ad6f91700001000001712ff72aa6cc492ea61441d447a48f92");
                return paramitars;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            getMenuInflater().inflate(R.menu.search,menu);
            MenuItem item=menu.findItem(R.id.search);
            SearchView searchView =(SearchView)item.getActionView();
            searchView.setQueryHint("Search Wallpaper");



            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText)
                {

                    api_uri = "https://api.pexels.com/v1/search/?page="+pageNum+"&per_page=80&query="+newText;
                    list.clear();
                    getWallpaperFromApi();


                    return false;
                }
            });



        }catch (Exception e){

        }


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage("Are you sure for exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.super.onBackPressed();
                        finishAffinity();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

}