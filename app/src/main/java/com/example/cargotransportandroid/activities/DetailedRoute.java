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
import com.example.cargotransportandroid.*;
import com.example.cargotransportandroid.model.Driver;
import com.example.cargotransportandroid.model.Manager;
import com.example.cargotransportandroid.model.Route;
import com.example.cargotransportandroid.model.Truck;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.example.cargotransportandroid.Constants.*;

public class DetailedRoute extends AppCompatActivity {
    EditText idField;
    EditText startLocationField;
    EditText endLocationField;
    EditText deliveryDeadlineField;
    EditText assignedTruckField;
    EditText assignedDriverField;
    EditText assignedManagerField;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_route);

        idField = findViewById(R.id.idTextField);
        startLocationField = findViewById(R.id.startLocationField);
        endLocationField = findViewById(R.id.endLocationField);
        deliveryDeadlineField = findViewById(R.id.deliveryDeadlineField);
        assignedTruckField = findViewById(R.id.assignedTruckField);
        assignedDriverField = findViewById(R.id.assignedDriverField);
        assignedManagerField = findViewById(R.id.assignedManagerField);

        Intent currentIntent = getIntent();
        Long routeId = currentIntent.getLongExtra("SELECTED_ROUTE_ID", -1);

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String response = Rest.sendRequest(URL_GET_ROUTE_BY_ID + routeId, "GET");
                handler.post(() -> {
                    try {
                        if (!response.equals("Error")) {
                            Gson gson = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                gson = new GsonBuilder()
                                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                                        .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                                        .create();
                            }

                            Route route = gson.fromJson(response, Route.class);
                            idField.setText(String.valueOf(route.getId()));
                            startLocationField.setText(route.getStartLocation());
                            endLocationField.setText(route.getEndLocation());
                            deliveryDeadlineField.setText(route.getDeliveryDeadline() != null ? route.getDeliveryDeadline().toString() : "");
                            Truck truck = route.getAssignedTruck();
                            if(truck != null) {
                                assignedTruckField.setText(truck.getId() + ": " + truck.getCarBrand() + " " + truck.getModel() + " " + truck.getManufactureYear());
                            }
                            Driver driver = route.getDriver();
                            if(driver != null) {
                                assignedDriverField.setText(driver.getId() + ": " + driver.getName() + " " + driver.getSurname());
                            }
                            Manager manager = route.getManager();
                            if(manager != null) {
                                assignedManagerField.setText(manager.getId() + ": " + manager.getName() + " " + manager.getSurname());
                            }


                        } else {
                            Toast.makeText(DetailedRoute.this, "Errors getting selected route", Toast.LENGTH_SHORT)
                                    .show();
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

    public void updateRoute(View view) throws IOException {
        Intent currentIntent = getIntent();
        Long routeId = currentIntent.getLongExtra("SELECTED_ROUTE_ID", -1);

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String response = Rest.sendRequest(URL_GET_ROUTE_BY_ID + routeId, "GET");
                handler.post(() -> {
                    try {
                        if (!response.equals("Error")) {
                            Gson gson = null;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                gson = new GsonBuilder()
                                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                                        .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
                                        .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                                        .create();
                            }
                            Route route = gson.fromJson(response, Route.class);

                            route.setStartLocation(startLocationField.getText().toString());
                            route.setEndLocation(endLocationField.getText().toString());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                route.setDeliveryDeadline(deliveryDeadlineField.getText().toString().equals("") ? null :
                                        LocalDateTime.parse(deliveryDeadlineField.getText().toString()));
                            }

                            String data = gson.toJson(route);

                            Executor executor2 = Executors.newSingleThreadExecutor();
                            Handler handler2 = new Handler(Looper.getMainLooper());
                            executor2.execute(() -> {
                                try {
                                    String response2 = Rest.sendRequestWithBody(URL_UPDATE_ROUTE + routeId, data, "PUT");
                                    handler2.post(() -> {
                                        try {
                                            if (!response2.equals("Error")) {
                                                Toast.makeText(DetailedRoute.this, "Route successfully updated",
                                                        Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(DetailedRoute.this, MainPage.class);
                                                intent.putExtra("USER_JSON", getIntent().getStringExtra("USER_JSON"));
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(DetailedRoute.this, "Errors updating selected route",
                                                        Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(DetailedRoute.this, "Errors updating selected route",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(DetailedRoute.this, "Errors updating selected route",
                                Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }

    public void deleteRoute(View view) throws IOException {
        Intent currentIntent = getIntent();
        long routeId = currentIntent.getLongExtra("SELECTED_ROUTE_ID", -1);

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String response = Rest.sendRequest(URL_DELETE_ROUTE + routeId, "DELETE");
                handler.post(() -> {
                    try {
                        if (!response.equals("Error")) {
                            Toast.makeText(DetailedRoute.this, "Route successfully deleted",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(DetailedRoute.this, MainPage.class);
                            intent.putExtra("USER_JSON", getIntent().getStringExtra("USER_JSON"));
                            startActivity(intent);

                        } else {
                            Toast.makeText(DetailedRoute.this, "Errors deleting selected route",
                                    Toast.LENGTH_SHORT).show();
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