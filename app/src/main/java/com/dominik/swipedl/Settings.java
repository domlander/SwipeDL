package com.dominik.swipedl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.graphics.Color;

public class Settings extends ActionBarActivity implements View.OnClickListener {
    // TODO image and audio

    Button game_button;
    Button high_scores_button;
    Button one_player_button;
    Button two_player_button;
    Button time_limit_button;
    Button drag_limit_button;
    Button game_mode_option_one_button;
    Button game_mode_option_two_button;
    Button game_mode_option_three_button;
    Button easy_button;
    Button medium_button;
    Button hard_button;
    Button save_button;
    Button cancel_button;

    SharedPreferences sharedPrefs;

    private int numPlayers;
    private int gameMode;
    private int gameModeOption;
    private int difficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPrefs = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        numPlayers = sharedPrefs.getInt("numPlayers", 1);
        gameMode = sharedPrefs.getInt("gameMode", 1);
        gameModeOption = sharedPrefs.getInt("gameModeOption", 3);
        difficulty = sharedPrefs.getInt("difficulty", 1);

        game_button = (Button) findViewById(R.id.GameMenuBar);
        game_button.setOnClickListener(this);

        high_scores_button = (Button) findViewById(R.id.HighScoresMenuBar);
        high_scores_button.setOnClickListener(this);

        one_player_button = (Button) findViewById(R.id.OnePlayer);
        one_player_button.setOnClickListener(this);

        two_player_button = (Button) findViewById(R.id.TwoPlayer);
        two_player_button.setOnClickListener(this);

        time_limit_button = (Button) findViewById(R.id.TimeLimit);
        time_limit_button.setOnClickListener(this);

        drag_limit_button = (Button) findViewById(R.id.DragLimit);
        drag_limit_button.setOnClickListener(this);

        game_mode_option_one_button = (Button) findViewById(R.id.GameModeOptionOne);
        game_mode_option_one_button.setOnClickListener(this);

        game_mode_option_two_button = (Button) findViewById(R.id.GameModeOptionTwo);
        game_mode_option_two_button.setOnClickListener(this);

        game_mode_option_three_button = (Button) findViewById(R.id.GameModeOptionThree);
        game_mode_option_three_button.setOnClickListener(this);

        easy_button = (Button) findViewById(R.id.Easy);
        easy_button.setOnClickListener(this);

        medium_button = (Button) findViewById(R.id.Medium);
        medium_button.setOnClickListener(this);

        hard_button = (Button) findViewById(R.id.Hard);
        hard_button.setOnClickListener(this);

        save_button = (Button) findViewById(R.id.Save);
        save_button.setOnClickListener(this);

        cancel_button = (Button) findViewById(R.id.Cancel);
        cancel_button.setOnClickListener(this);

        updateButtonState();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.GameMenuBar:
                menuBarGame();
                return;

            case R.id.HighScoresMenuBar:
                menuBarHighScores();
                return;

            case R.id.OnePlayer:
                numPlayers = 1;
                break;

            case R.id.TwoPlayer:
                numPlayers = 2;
                break;

            case R.id.TimeLimit:
                gameMode = 1;
                break;

            case R.id.DragLimit:
                gameMode = 2;
                break;

            case R.id.GameModeOptionOne:
                if (gameMode == 1) {
                    gameModeOption = 1;
                } else {
                    gameModeOption = 1;
                }
                break;

            case R.id.GameModeOptionTwo:
                if (gameMode == 1) {
                    gameModeOption = 2;
                } else {
                    gameModeOption = 2;
                }
                break;

            case R.id.GameModeOptionThree:
                if (gameMode == 1) {
                    gameModeOption = 3;
                } else {
                    gameModeOption = 3;
                }
                break;

            case R.id.Easy:
                difficulty = 1;
                break;

            case R.id.Medium:
                difficulty = 2;
                break;

            case R.id.Hard:
                difficulty = 3;
                break;

            case R.id.Save:
                saveSelected();
                break;

            case R.id.Cancel:
                cancelSelected();
                break;
        }

        updateButtonState();
    }

    public void menuBarGame() {
        Intent goToGame = new Intent(this, Game.class);
        startActivity(goToGame);
    }

    public void menuBarHighScores() {
        Intent goToHighScores = new Intent(this, HighScores.class);
        startActivity(goToHighScores);
    }

    // Button selected ? button colour = green : button colour = grey
    private void updateButtonState() {

        // The following buttons are aliased to the currently selected buttons.
        // These buttons are given a different background colour to indicate selection.
        Button selectedNumPlayers;
        Button selectedTimeLimit;
        Button selectedGameModeOption;
        Button selectedDifficulty;

        if (numPlayers == 1) {
            selectedNumPlayers= one_player_button;
        } else {
            selectedNumPlayers = two_player_button;
        }

        if (gameMode == 1) {
            selectedTimeLimit = time_limit_button;
            game_mode_option_one_button.setText(getString(R.string.five_seconds));
            game_mode_option_two_button.setText(getString(R.string.ten_seconds));
            game_mode_option_three_button.setText(getString(R.string.thirty_seconds));

        } else {
            selectedTimeLimit = drag_limit_button;
            game_mode_option_one_button.setText(getString(R.string.ten_drags));
            game_mode_option_two_button.setText(getString(R.string.fifty_drags));
            game_mode_option_three_button.setText(getString(R.string.hundred_drags));
        }

        if (gameModeOption == 1) {
            selectedGameModeOption = game_mode_option_one_button;

        } else if (gameModeOption == 2) {
            selectedGameModeOption = game_mode_option_two_button;

        } else {
            selectedGameModeOption = game_mode_option_three_button;
        }

        if (difficulty == 1) {
            selectedDifficulty = easy_button;

        } else if (difficulty == 2) {
            selectedDifficulty = medium_button;

        } else {
            selectedDifficulty = hard_button;
        }

        one_player_button.setBackgroundColor(0xffd9d9f4);
        two_player_button.setBackgroundColor(0xffd9d9f4);
        time_limit_button.setBackgroundColor(0xffd9d9f4);
        drag_limit_button.setBackgroundColor(0xffd9d9f4);
        game_mode_option_one_button.setBackgroundColor(0xffd9d9f4);
        game_mode_option_two_button.setBackgroundColor(0xffd9d9f4);
        game_mode_option_three_button.setBackgroundColor(0xffd9d9f4);
        easy_button.setBackgroundColor(0xffd9d9f4);
        medium_button.setBackgroundColor(0xffd9d9f4);
        hard_button.setBackgroundColor(0xffd9d9f4);

        selectedNumPlayers.setBackgroundColor(Color.GREEN);
        selectedTimeLimit.setBackgroundColor(Color.GREEN);
        selectedGameModeOption.setBackgroundColor(Color.GREEN);
        selectedDifficulty.setBackgroundColor(Color.GREEN);
    }

    // Save to SharedPreferences. The selected settings will be used for the next game.
    private void saveSelected() {

        sharedPrefs = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        editor.putInt("numPlayers", numPlayers);
        editor.putInt("gameMode", gameMode);
        editor.putInt("gameModeOption", gameModeOption);
        editor.putInt("difficulty", difficulty);

        editor.apply();

        Intent goToGame = new Intent(this, Game.class);
        startActivity(goToGame);
    }

    private void cancelSelected() {
        Intent goToTopMenu = new Intent(this, TopMenu.class);
        startActivity(goToTopMenu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
