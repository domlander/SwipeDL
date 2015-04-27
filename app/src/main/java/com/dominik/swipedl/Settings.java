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

    SharedPreferences sharedPrefs;

    private int numPlayers;
    private int gameMode;
    private int gameModeOption;
    private int difficulty;

    private Button one_player_button;
    private Button two_player_button;
    private Button time_limit_button;
    private Button drag_limit_button;
    private Button game_mode_option_one_button;
    private Button game_mode_option_two_button;
    private Button game_mode_option_three_button;
    private Button easy_button;
    private Button medium_button;
    private Button hard_button;
    private Button save_button;
    private Button cancel_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPrefs = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        numPlayers = sharedPrefs.getInt("numPlayers", 1);
        gameMode = sharedPrefs.getInt("gameMode", 1);
        gameModeOption = sharedPrefs.getInt("gameModeOption", 3);
        difficulty = sharedPrefs.getInt("difficulty", 1);

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
            case R.id.OnePlayer:
                onePlayerSelected();
                break;

            case R.id.TwoPlayer:
                twoPlayersSelected();
                break;

            case R.id.TimeLimit:
                timeLimitSelected();
                break;

            case R.id.DragLimit:
                dragLimitSelected();
                break;

            case R.id.GameModeOptionOne:
                if (gameMode == 1) {
                    seconds5Selected();
                } else {
                    drags10Selected();
                }
                break;

            case R.id.GameModeOptionTwo:
                if (gameMode == 1) {
                    seconds10Selected();
                } else {
                    drags50Selected();
                }
                break;

            case R.id.GameModeOptionThree:
                if (gameMode == 1) {
                    seconds30Selected();
                } else {
                    drags100Selected();
                }
                break;

            case R.id.Easy:
                easySelected();
                break;

            case R.id.Medium:
                mediumSelected();
                break;

            case R.id.Hard:
                hardSelected();
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

    private void onePlayerSelected() { numPlayers = 1; }
    private void twoPlayersSelected() { numPlayers = 2; }

    private void timeLimitSelected() { gameMode = 1; }
    private void dragLimitSelected() { gameMode = 2; }

    private void seconds5Selected() { gameModeOption = 1; }
    private void seconds10Selected() { gameModeOption = 2; }
    private void seconds30Selected() { gameModeOption = 3; }

    private void drags10Selected() { gameModeOption = 1; }
    private void drags50Selected() { gameModeOption = 2; }
    private void drags100Selected() { gameModeOption = 3; }

    private void easySelected() { difficulty = 1; }
    private void mediumSelected() { difficulty = 2; }
    private void hardSelected() { difficulty = 3; }

    // Button selected ? button colour = green : button colour = grey
    private void updateButtonState() {

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

        one_player_button.setBackgroundColor(Color.LTGRAY);
        two_player_button.setBackgroundColor(Color.LTGRAY);
        time_limit_button.setBackgroundColor(Color.LTGRAY);
        drag_limit_button.setBackgroundColor(Color.LTGRAY);
        game_mode_option_one_button.setBackgroundColor(Color.LTGRAY);
        game_mode_option_two_button.setBackgroundColor(Color.LTGRAY);
        game_mode_option_three_button.setBackgroundColor(Color.LTGRAY);
        easy_button.setBackgroundColor(Color.LTGRAY);
        medium_button.setBackgroundColor(Color.LTGRAY);
        hard_button.setBackgroundColor(Color.LTGRAY);

        selectedNumPlayers.setBackgroundColor(Color.GREEN);
        selectedTimeLimit.setBackgroundColor(Color.GREEN);
        selectedGameModeOption.setBackgroundColor(Color.GREEN);
        selectedDifficulty.setBackgroundColor(Color.GREEN);
    }

    private void saveSelected() {

        sharedPrefs = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        editor.putInt("numPlayers", numPlayers);
        editor.putInt("GameMode", gameMode);
        editor.putInt("gameModeOption", gameModeOption);
        editor.putInt("difficulty", difficulty);

        editor.apply();

        returnHome();
    }

    private void cancelSelected() { returnHome(); }

    private void returnHome() {
        Intent returnHome = new Intent(this, TopMenu.class);
        startActivity(returnHome);
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
