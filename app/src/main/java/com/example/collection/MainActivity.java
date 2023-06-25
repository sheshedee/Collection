package com.example.collection;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String URL = "https://api.unsplash.com/photos/?client_id=BynWB-cZ3gnxyOiLK8FV2ojXYFfVc9gpwIFfgLeSGX8";
    private RecyclerViewAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.idCourseRV);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        fetchDataFromUrl().thenAccept(dataObjects -> {
            // Data fetched successfully
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter = new RecyclerViewAdapter(dataObjects, MainActivity.this);
                    recyclerView.setAdapter(adapter);
                    for (RecyclerData dataObject : dataObjects) {
                        Log.d(TAG, "RecyclerData: " + dataObject.getUrls().get("small"));
                    }
                }
            });

            // Perform further operations with the data
            // ...
        }).exceptionally(throwable -> {
            // Handle any exceptions that occurred during data fetching
            Log.e(TAG, "Failed to fetch data: " + throwable.getMessage());
            adapter = new RecyclerViewAdapter(new ArrayList<>(), MainActivity.this);
            recyclerView.setAdapter(adapter);
            return null;
        });

    }

    private CompletableFuture<ArrayList<RecyclerData>> fetchDataFromUrl() {
        CompletableFuture<ArrayList<RecyclerData>> completableFuture = new CompletableFuture<>();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                completableFuture.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonString = response.body().string();

                    Gson gson = new Gson();
                    Type dataType = new TypeToken<ArrayList<RecyclerData>>(){}.getType();
                    ArrayList<RecyclerData> dataObjects = gson.fromJson(jsonString, dataType);

                    completableFuture.complete(dataObjects);
                } else {
                    completableFuture.completeExceptionally(new IOException("Request failed with code: " + response.code()));
                }
            }
        });

        return completableFuture;
    }

//    private void fetchDataFromUrl() {
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url(URL)
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.e(TAG, "Failed to fetch data: " + e.getMessage());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    String jsonString = response.body().string();
//                    Log.d(TAG, "jsonString" + jsonString);
//
//                    Gson gson = new Gson();
//                    Type dataType = new TypeToken<ArrayList<RecyclerData>>() {}.getType();
//                    ArrayList<RecyclerData> dataObjects = gson.fromJson(jsonString, dataType);
//
//                        runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            adapter = new RecyclerViewAdapter(new ArrayList<>(), MainActivity.this);
//                            adapter.recyclerData(dataObjects);
//                            recyclerView.setAdapter(adapter);
//
//
//                            // Data fetched successfully
//                            for (RecyclerData dataObject : dataObjects) {
//                                Log.d(TAG, "RecyclerData: " + dataObject.getUrls().get("small"));
//                            }
//
//
//                            // Perform further operations with the data
//
//                            // ...
//                        }
//                    });
//                } else {
//                    Log.e(TAG, "Request failed with code: " + response.code());
//                }
//            }
//        });
//    }
}