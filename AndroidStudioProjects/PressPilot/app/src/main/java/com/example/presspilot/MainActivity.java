package com.example.presspilot;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private MaterialButton btnGetStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Show activity_main.xml
        setContentView(R.layout.activity_main);

        // Connect button
        btnGetStarted = findViewById(R.id.btnGetStarted);

        // Open Login screen
        btnGetStarted.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    LoginActivity.class
            );

            startActivity(intent);
            finish();
        });
    }
}