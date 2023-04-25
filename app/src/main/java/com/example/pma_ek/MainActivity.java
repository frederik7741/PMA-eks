package com.example.pma_ek;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button button;
    private Button easyButton, mediumButton, hardButton;
    private int Difficulty = 0;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the buttons and set click listeners
        easyButton = findViewById(R.id.easy_button);
        mediumButton = findViewById(R.id.medium_button);
        hardButton = findViewById(R.id.hard_button);

        easyButton.setOnClickListener(this);
        mediumButton.setOnClickListener(this);
        hardButton.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        // Determine which button was clicked
        int buttonId = v.getId();
        if (buttonId == R.id.easy_button) {
            setActiveDifficulty(1);
        } else if (buttonId == R.id.medium_button) {
            setActiveDifficulty(2);
        } else if (buttonId == R.id.hard_button) {
            setActiveDifficulty(3);
        }
    }

    private void setActiveDifficulty(int difficulty) {
        Difficulty = difficulty;
        // Deactivate the previously active button
        if (Difficulty == 1) {
            easyButton.setActivated(true);
            mediumButton.setActivated(false);
            hardButton.setActivated(false);
        } else if (Difficulty == 2) {
            easyButton.setActivated(false);
            mediumButton.setActivated(true);
            hardButton.setActivated(false);
        } else if (Difficulty == 3) {
            easyButton.setActivated(false);
            mediumButton.setActivated(false);
            hardButton.setActivated(true);
        }
        Intent intent = new Intent(MainActivity.this, Run.class);
        intent.putExtra("DIFFICULTY_LEVEL", Difficulty); // Add the difficulty level as an extra
        startActivity(intent);
    }



}
