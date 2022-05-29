package com.sohaghlab.wallpaperapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.AbsListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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

    int pageNum=1;
    Boolean isScroll=false;
    int currentItem,totalItem,scrollOutItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView=findViewById(R.id.recyclerview);

        list= new ArrayList<>();
        adapter= new WallpaperAdapter(this,list);

        recyclerView.setAdapter(adapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(gridLayoutManager);


        getWallpaperFromApi();

        //for auto page update
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
    }

    private void getWallpaperFromApi() {

        StringRequest request = new StringRequest(Request.Method.GET, "https://api.pexels.com/v1/curated/?page="+pageNum+"&per_page=80",
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
                                String imageUrl=object2.getString("large");
                                String mediumUrl=object2.getString("large2x");
                                String orginalUrl=object2.getString("original");

                                WallpaperModel model = new WallpaperModel(id,imageUrl,mediumUrl,orginalUrl,alt);
                                list.add(model);
                            }

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
}