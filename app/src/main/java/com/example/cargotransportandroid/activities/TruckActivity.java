package com.example.cargotransportandroid.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.cargotransportandroid.LocalDateDeserializer;
import com.example.cargotransportandroid.LocalDateTimeDeserializer;
import com.example.cargotransportandroid.R;
import com.example.cargotransportandroid.Rest;
import com.example.cargotransportandroid.model.Truck;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.example.cargotransportandroid.Constants.URL_GET_ALL_TRUCKS;

public class TruckActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truck);

        Intent currentIntent = getIntent();
        String userDataJson = currentIntent.getStringExtra("USER_JSON");

        TabLayout tabLayout = findViewById(R.id.truckTabLayout);

        tabLayout.getTabAt(1).select();

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().equals(getResources().getString(R.string.routes))) {
                    Intent intent = new Intent(TruckActivity.this, MainPage.class);
                    intent.putExtra("USER_JSON", userDataJson);
                    startActivity(intent);
                }

            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String response = Rest.sendRequest(URL_GET_ALL_TRUCKS, "GET");
                handler.post(() -> {
                    try {
                        if (!response.equals("Error")) {
                            Gson gson = null;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                gson = new GsonBuilder()
                                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                                        .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                                        .create();
                            }
                            Type truckType = new TypeToken<List<Truck>>() {
                            }.getType();
                            assert gson != null;
                            final List<Truck> truckListFromJson = gson.fromJson(response, truckType);
                            ArrayAdapter<Truck> arrayAdapter = new ArrayAdapter<>(TruckActivity.this, android.R.layout.simple_list_item_1, truckListFromJson);
                            ListView trucksListView = findViewById(R.id.allTrucksList);
                            trucksListView.setAdapter(arrayAdapter);

                            trucksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Toast.makeText(TruckActivity.this, "Selected truck: " + truckListFromJson.get(i).getId(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(TruckActivity.this, "Errors getting all trucks", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }
}