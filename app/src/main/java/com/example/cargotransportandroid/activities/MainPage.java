package com.example.cargotransportandroid.activities;

import android.annotation.SuppressLint;
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
import com.example.cargotransportandroid.model.Route;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.example.cargotransportandroid.Constants.*;


public class MainPage extends AppCompatActivity {

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        Intent currentIntent = getIntent();
        String userDataJson = currentIntent.getStringExtra("USER_JSON");

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getText().equals(getResources().getString(R.string.trucks))){
                    Intent intent = new Intent(MainPage.this, TruckActivity.class);
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

        GsonBuilder gsonBuilder = new GsonBuilder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());
        }
        Gson gson = gsonBuilder.create();

        //Pasitikrinti, ar json struktura pilnai sutampa su klase, kitaip nemapins
//        Driver user = gson.fromJson(userDataJson, Driver.class);

        //Visada parsinimas su properties galimas
        Properties properties = gson.fromJson(userDataJson, Properties.class);

        int userId = Integer.parseInt(properties.getProperty("id"));
        String isAdminFieldPresent = properties.getProperty("admin");

        boolean managerLoggedIn = isAdminFieldPresent != null;

        if (managerLoggedIn) {
            //sioje vietoje padarau, kad tam tikras funkcionalumas butu deaktyvuotas
            //Cia pildau visus route
            executor.execute(() -> {
                try {
                    String response = Rest.sendRequest(URL_GET_ALL_ROUTES,"GET");
                    handler.post(() -> {
                        try {
                            if (!response.equals("Error")) {

                                //Jei as turesiu data, tiketina reikes ta data deserializuoti

                                //Reikia dar gson builderio
                                //Jei turiu kazkokius deserializatorius, reikia paregistruoti. Bus po GsonBuilder(). ... cia visi eina ...
                                Gson builder = null;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    builder = new GsonBuilder()
                                            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                                            .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                                            .create();
                                }

                                //Sitoje vietoje pasakau kokio tipo duomenys bus tam json, apsibreziu tipa pagal kuri mappins
                                Type routeType = new TypeToken<List<Route>>() {
                                }.getType();
                                //Sioje eiluteje gauta response parsinu pagal ta tipa =, kuri apsibreziau auksciau. Jis zinos, kad ten turi but List<Route>
                                final List<Route> routeListFromJson = builder.fromJson(response, routeType);

                                //By default ListView neturi grafinio vaizdo, jis yra toks kaip konteineris, kuriam reik ir duomenu ir layout nurodyti
                                ArrayAdapter<Route> arrayAdapter = new ArrayAdapter<>(MainPage.this, android.R.layout.simple_list_item_1, routeListFromJson);

                                //Dabar noriu supildyti duomenis i savo ListView t.y. atvaizduoti GUI
                                ListView routesListView = findViewById(R.id.allRoutesList); //cia grafini elementa pasiimu
                                routesListView.setAdapter(arrayAdapter);

                                //Bet as noriu tureti list view, kuri galima paspausti ir man uzkraus duomenis kitam lange
                                routesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        //Cia bus custom logika visiems routes, nepriklausomai ar priskirtas ar ne
                                        Toast.makeText(MainPage.this, "Selected route: " + routeListFromJson.get(i).getId(), Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(MainPage.this, DetailedRoute.class);
                                        //I kita langa paduodu pasirinkto route id, kad issitraukciau tiksliai naujausius jo duomenis
                                        intent.putExtra("SELECTED_ROUTE_ID", routeListFromJson.get(i).getId());
                                        intent.putExtra("USER_JSON", userDataJson);
                                        startActivity(intent);
                                    }
                                });
                            } else {
                                Toast.makeText(MainPage.this, "Errors getting all routes", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else {
            //Reiktu isvis nerodyti elemento, jei tai vairuotojas
            ListView listViewAllRoutes = findViewById(R.id.allRoutesList);
            listViewAllRoutes.setVisibility(View.INVISIBLE);
            TextView allRoutesText = findViewById(R.id.allRoutesTextView);
            allRoutesText.setVisibility(View.INVISIBLE);
        }

        //Pildau ListView, kur atrinkti Routes pagal user Id(priskirta, nepriskirta)
        executor.execute(() -> {
            try {
                String response;
                if(managerLoggedIn) {
                    response = Rest.sendRequest(URL_GET_MANAGER_ROUTES + userId, "GET");
                }else {
                    response = Rest.sendRequest(URL_GET_DRIVER_ROUTES + userId, "GET");
                }
                handler.post(() -> {
                    try {
                        if (!response.equals("Error")) {
                            //Jei as turesiu data, tiketina reikes ta data deserializuoti
                            //Reikia dar gson builderio
                            //Jei turiu kazkokius deserializatorius, reikia paregistruoti. Bus po GsonBuilder(). ... cia visi eina ...
                            Gson builder = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                builder = new GsonBuilder()
                                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                                        .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                                        .create();
                            }

                            //Sitoje vietoje pasakau kokio tipo duomenys bus tam json, apsibreziu tipa pagal kuri mappins
                            Type routeType = new TypeToken<List<Route>>() {
                            }.getType();
                            //Sioje eiluteje gauta response parsinu pagal ta tipa =, kuri apsibreziau auksciau. Jis zinos, kad ten turi but List<Route>
                            final List<Route> routeListFromJson = builder.fromJson(response, routeType);

                            //By default ListView neturi grafinio vaizdo, jis yra toks kaip konteineris, kuriam reik ir duomenu ir layout nurodyti
                            ArrayAdapter<Route> arrayAdapter = new ArrayAdapter<>(MainPage.this, android.R.layout.simple_list_item_1, routeListFromJson);

                            //Dabar noriu supildyti duomenis i savo ListView t.y. atvaizduoti GUI
                            ListView routesListView = findViewById(R.id.routeList); //cia grafini elementa pasiimu
                            routesListView.setAdapter(arrayAdapter);

                            //Bet as noriu tureti list view, kuri galima paspausti ir man uzkraus duomenis kitam lange
                            routesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Toast.makeText(MainPage.this, "Selected route: " + routeListFromJson.get(i).getId(), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainPage.this, DetailedRoute.class);
                                    //I kita langa paduodu pasirinkto route id, kad issitraukciau tiksliai naujausius jo duomenis
                                    intent.putExtra("SELECTED_ROUTE_ID", routeListFromJson.get(i).getId());
                                    intent.putExtra("USER_JSON", userDataJson);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            Toast.makeText(MainPage.this, "Errors during authentication", Toast.LENGTH_SHORT).show();
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