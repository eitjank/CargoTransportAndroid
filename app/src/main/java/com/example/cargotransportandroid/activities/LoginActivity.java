package com.example.cargotransportandroid.activities;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.cargotransportandroid.LoginCredentials;
import com.example.cargotransportandroid.R;
import com.example.cargotransportandroid.Rest;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.example.cargotransportandroid.Constants.URL_VALIDATE;

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void validate(View view) {

        EditText loginField = findViewById(R.id.usernameField);
        EditText passwordField = findViewById(R.id.passwordField);

        Gson gson = new Gson();
        LoginCredentials loginCredentials = new LoginCredentials(loginField.getText().toString(), passwordField.getText().toString());
        String loginData = gson.toJson(loginCredentials);
        //String loginData = "{\"login\":\"" + loginField.getText().toString() + "\",\"password\":\"" + passwordField.getText().toString() + "\"}";

        Executor executor = Executors.newSingleThreadExecutor();

        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String response = Rest.sendRequestWithBody(URL_VALIDATE, loginData, "POST");
                handler.post(() -> {
                    try {
                        if (!response.equals("Error") && !response.equals("")) {
                            Intent intent = new Intent(LoginActivity.this, MainPage.class);
                            intent.putExtra("USER_JSON", response);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, "Errors during authentication", Toast.LENGTH_SHORT).show();
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