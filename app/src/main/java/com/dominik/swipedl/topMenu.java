package com.dominik.swipedl;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class topMenu extends ActionBarActivity implements View.OnClickListener {

    Button start_game_button;
    Button settings_button;
    Button how_to_play_button;
    Object[] settings;
    TextView debug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_menu);

        // Default settings: single player; time limit; 60s; easy
        settings = new Object[] {1, true, 3, 1};

        start_game_button = (Button) findViewById(R.id.start_game_button);
        start_game_button.setOnClickListener(this);

        settings_button = (Button) findViewById(R.id.settings_button);
        settings_button.setOnClickListener(this);

        how_to_play_button = (Button) findViewById(R.id.how_to_play_button);
        how_to_play_button.setOnClickListener(this);
    }

    private void startGameButtonClicked() {
        // Get current setting values and send through
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        settings[0] = Integer.parseInt(extras.getString("NUM_PLAYERS"));
        settings[1] = Boolean.parseBoolean(extras.getString("TIME_LIMIT"));
        settings[2] = Integer.parseInt(extras.getString("GAME_MODE_OPTION"));
        settings[3] = Integer.parseInt(extras.getString("DIFFICULTY"));

        Intent goToGame = new Intent(this, MainActivity.class);

        Bundle extras2 = new Bundle();
        extras2.putString("NUM_PLAYERS", settings[0].toString());
        extras2.putString("TIME_LIMIT", settings[1].toString());
        extras2.putString("GAME_MODE_OPTION", settings[2].toString());
        extras2.putString("DIFFICULTY", settings[3].toString());
        goToGame.putExtras(extras2);

        startActivity(goToGame);
    }

    private void settingsButtonClicked() {
        Intent goToSettings = new Intent(this, Settings.class);
        Bundle extras = new Bundle();
        extras.putString("NUM_PLAYERS", settings[0].toString());
        extras.putString("TIME_LIMIT", settings[1].toString());
        extras.putString("GAME_MODE_OPTION", settings[2].toString());
        extras.putString("DIFFICULTY", settings[3].toString());
        goToSettings.putExtras(extras);
        startActivity(goToSettings);
    }

    private void highScoresButtonClicked() {
        // TODO Create High Scores activity
        // Intent goToHighScores = new Intent(this, MainActivity.class);
        // startActivity(goToHighScores);
    }

    private void howToPlayButtonClicked() {
        // TODO Create Instructions activity
        // Intent goToInstructions = new Intent(this, MainActivity.class);
        // startActivity(goToInstructions);
    }

    /*
     * This will check whether a button has been pressed and if true call
     * the appropriate method.
     */
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_game_button:
                startGameButtonClicked();
                break;

            case R.id.settings_button:
                settingsButtonClicked();
                break;

            case R.id.how_to_play_button:
                howToPlayButtonClicked();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_top_menu, menu);
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
