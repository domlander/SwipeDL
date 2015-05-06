package com.dominik.swipedl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static android.view.View.*;

public class HighScores extends ActionBarActivity implements AdapterView.OnItemSelectedListener, OnClickListener {

    // UI controls
    Button game_button;
    Button settings_button;
    TextView SelectGroupText;
    Spinner spinner;
    TextView highScore5s;
    TextView highScore10s;
    TextView highScore30s;
    TextView highScore10d;
    TextView highScore50d;
    TextView highScore100d;
    Button individual_button;
    Button group_button;
    Button world_button;
    Button easy_button;
    Button moderate_button;
    Button hard_button;

    private User thisUser;
    private String regionType;
    private String difficulty;

    // High scores stored in shared preferences
    SharedPreferences sharedPrefs;

    // All possible INDIVIDUAL high scores
    int individualEasy5s, individualEasy10s, individualEasy30s;
    float individualEasy10d, individualEasy50d, individualEasy100d;
    int individualModerate5s, individualModerate10s, individualModerate30s;
    float individualModerate10d, individualModerate50d, individualModerate100d;
    int individualHard5s, individualHard10s, individualHard30s;
    float individualHard10d, individualHard50d, individualHard100d;

    int groupEasy5s, groupEasy10s, groupEasy30s;
    float groupEasy10d, groupEasy50d, groupEasy100d;
    int groupModerate5s, groupModerate10s, groupModerate30s;
    float groupModerate10d, groupModerate50d, groupModerate100d;
    int groupHard5s, groupHard10s, groupHard30s;
    float groupHard10d, groupHard50d, groupHard100d;

    int worldEasy5s, worldEasy10s, worldEasy30s;
    float worldEasy10d, worldEasy50d, worldEasy100d;
    int worldModerate5s, worldModerate10s, worldModerate30s;
    float worldModerate10d, worldModerate50d, worldModerate100d;
    int worldHard5s, worldHard10s, worldHard30s;
    float worldHard10d, worldHard50d, worldHard100d;

    Intent goToGame;
    Intent goToSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);

        // Create user for player
        thisUser = new User("You");

        // test users
        Group group1 = new Group(); // debug
        User bob = new User("bob"); // debug
        User jane = new User("jane"); // debug
        group1.addUserToGroup(bob); // debug
        group1.addUserToGroup(jane); // debug

        String[][] users = group1.getUsers();
        ArrayList<String> userNames = new ArrayList<>();
//        for (String[] user : users) {
//            userNames.add(user[0]);
//        }
        userNames.add("Test Group 1"); // debug
        userNames.add("Test Group 2"); // debug

        game_button = (Button) findViewById(R.id.GameMenuBar);
        game_button.setOnClickListener(this);

        settings_button = (Button) findViewById(R.id.SettingsMenuBar);
        settings_button.setOnClickListener(this);

        SelectGroupText = (TextView) findViewById(R.id.SelectGroupText);
        spinner = (Spinner) findViewById(R.id.spinner);

        SelectGroupText.setVisibility(INVISIBLE);
        spinner.setVisibility(INVISIBLE);

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, userNames);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        individual_button = (Button) findViewById(R.id.Individual);
        individual_button.setOnClickListener(this);

        group_button = (Button) findViewById(R.id.Group);
        group_button.setOnClickListener(this);

        world_button = (Button) findViewById(R.id.World);
        world_button.setOnClickListener(this);

        easy_button = (Button) findViewById(R.id.Easy);
        easy_button.setOnClickListener(this);

        moderate_button = (Button) findViewById(R.id.Moderate);
        moderate_button.setOnClickListener(this);

        hard_button = (Button) findViewById(R.id.Hard);
        hard_button.setOnClickListener(this);

        highScore5s = (TextView) findViewById(R.id.Best5sValue);
        highScore10s = (TextView) findViewById(R.id.Best10sValue);
        highScore30s = (TextView) findViewById(R.id.Best30sValue);
        highScore10d = (TextView) findViewById(R.id.Best10dragsValue);
        highScore50d = (TextView) findViewById(R.id.Best50dragsValue);
        highScore100d = (TextView) findViewById(R.id.Best100dragsValue);

        regionType = "Individual";
        difficulty = "Easy";

        goToGame = new Intent(this, Game.class);
        goToSettings = new Intent(this, Settings.class);

        goToSettings.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        getHighScores();
        individualSelected(); // Default selection
        updateHighScores();
    }

    // Gets high scores from SharedPreferences (Individual), or database (Group, World)
    private void getHighScores() {
        sharedPrefs = getSharedPreferences("High Scores", Context.MODE_PRIVATE);
        individualEasy5s = sharedPrefs.getInt("individualEasy5s", 0);
        individualEasy10s = sharedPrefs.getInt("individualEasy10s", 0);
        individualEasy30s = sharedPrefs.getInt("individualEasy30s", 0);
        individualEasy10d = sharedPrefs.getFloat("individualEasy10d", 0) / 1000;
        individualEasy50d = sharedPrefs.getFloat("individualEasy50d", 0) / 1000;
        individualEasy100d = sharedPrefs.getFloat("individualEasy100d", 0) / 1000;
        individualModerate5s = sharedPrefs.getInt("individualModerate5s", 0);
        individualModerate10s = sharedPrefs.getInt("individualModerate10s", 0);
        individualModerate30s = sharedPrefs.getInt("individualModerate30s", 0);
        individualModerate10d = sharedPrefs.getFloat("individualModerate10d", 0) / 1000;
        individualModerate50d = sharedPrefs.getFloat("individualModerate50d", 0) / 1000;
        individualModerate100d = sharedPrefs.getFloat("individualModerate100d", 0) / 1000;
        individualHard5s = sharedPrefs.getInt("individualHard5s", 0);
        individualHard10s = sharedPrefs.getInt("individualHard10s", 0);
        individualHard30s = sharedPrefs.getInt("individualHard30s", 0);
        individualHard10d = sharedPrefs.getFloat("individualHard10d", 0) / 1000;
        individualHard50d = sharedPrefs.getFloat("individualHard50d", 0) / 1000;
        individualHard100d = sharedPrefs.getFloat("individualHard100d", 0) / 1000;

        // TODO need database to get group high scores.
        groupEasy5s = 0;
        groupEasy10s = 0;
        groupEasy30s = 0;
        groupEasy10d = 0;
        groupEasy50d = 0;
        groupEasy100d = 0;
        groupModerate5s = 0;
        groupModerate10s = 0;
        groupModerate30s = 0;
        groupModerate10d = 0;
        groupModerate50d = 0;
        groupModerate100d = 0;
        groupHard5s = 0;
        groupHard10s = 0;
        groupHard30s = 0;
        groupHard10d = 0;
        groupHard50d = 0;
        groupHard100d = 0;

        // TODO need database to get world high scores.
        worldEasy5s = 0;
        worldEasy10s = 0;
        worldEasy30s = 0;
        worldEasy10d = 0;
        worldEasy50d = 0;
        worldEasy100d = 0;
        worldModerate5s = 0;
        worldModerate10s = 0;
        worldModerate30s = 0;
        worldModerate10d = 0;
        worldModerate50d = 0;
        worldModerate100d = 0;
        worldHard5s = 0;
        worldHard10s = 0;
        worldHard30s = 0;
        worldHard10d = 0;
        worldHard50d = 0;
        worldHard100d = 0;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.GameMenuBar:
                menuBarGame();
                return;

            case R.id.SettingsMenuBar:
                menuBarSettings();
                return;

            case R.id.Individual:
                individualSelected();
                break;

            case R.id.Group:
                groupSelected();
                break;

            case R.id.World:
                worldSelected();
                break;

            case R.id.Easy:
               easySelected();
                break;

            case R.id.Moderate:
                moderateSelected();
                break;

            case R.id.Hard:
                hardSelected();
                break;
        }
        updateHighScores();
    }

    public void menuBarGame() {
        startActivity(goToGame);
    }

    public void menuBarSettings() {
        startActivity(goToSettings);
    }

    // Updates the colour of the buttons and populates the
    // correct high scores for the selected buttons.
    private void updateHighScores() {

        Button locality_button;
        Button difficulty_button;

        switch (regionType) {
            case "Individual":
            default:
                locality_button = individual_button;
                break;
            case "Group":
                locality_button = group_button;
                break;
            case "World":
                locality_button = world_button;
                break;
        }
        switch (difficulty) {
            case "Easy":
            default:
                difficulty_button = easy_button;
                break;
            case "Moderate":
                difficulty_button = moderate_button;
                break;
            case "Hard":
                difficulty_button = hard_button;
                break;
        }

        // Set the background colour of the selected buttons to green and the others to grey.
        individual_button.setBackgroundColor(0xffd9d9f4);
        group_button.setBackgroundColor(0xffd9d9f4);
        world_button.setBackgroundColor(0xffd9d9f4);
        easy_button.setBackgroundColor(0xffd9d9f4);
        moderate_button.setBackgroundColor(0xffd9d9f4);
        hard_button.setBackgroundColor(0xffd9d9f4);

        locality_button.setBackgroundColor(Color.GREEN);
        difficulty_button.setBackgroundColor(Color.GREEN);

        // Set the high scores into the TextView
        switch (regionType) {
            case "Individual":
            default:
                switch (difficulty) {
                    case "Easy":
                    default:
                        highScore5s.setText(Integer.toString(individualEasy5s));
                        highScore10s.setText(Integer.toString(individualEasy10s));
                        highScore30s.setText(Integer.toString(individualEasy30s));
                        highScore10d.setText(Float.toString(individualEasy10d));
                        highScore50d.setText(Float.toString(individualEasy50d));
                        highScore100d.setText(Float.toString(individualEasy100d));
                        break;

                    case "Moderate":
                        highScore5s.setText(Integer.toString(individualModerate5s));
                        highScore10s.setText(Integer.toString(individualModerate10s));
                        highScore30s.setText(Integer.toString(individualModerate30s));
                        highScore10d.setText(Float.toString(individualModerate10d));
                        highScore50d.setText(Float.toString(individualModerate50d));
                        highScore100d.setText(Float.toString(individualModerate100d));
                        break;

                    case "Hard":
                        highScore5s.setText(Integer.toString(individualHard5s));
                        highScore10s.setText(Integer.toString(individualHard10s));
                        highScore30s.setText(Integer.toString(individualHard30s));
                        highScore10d.setText(Float.toString(individualHard10d));
                        highScore50d.setText(Float.toString(individualHard50d));
                        highScore100d.setText(Float.toString(individualHard100d));
                        break;
                }
            break;
            case "Group":
                switch (difficulty) {
                    case "Easy":
                    default:
                        highScore5s.setText(Integer.toString(groupEasy5s));
                        highScore10s.setText(Integer.toString(groupEasy10s));
                        highScore30s.setText(Integer.toString(groupEasy30s));
                        highScore10d.setText(Float.toString(groupEasy10d));
                        highScore50d.setText(Float.toString(groupEasy50d));
                        highScore100d.setText(Float.toString(groupEasy100d));
                        break;

                    case "Moderate":
                        highScore5s.setText(Integer.toString(groupModerate5s));
                        highScore10s.setText(Integer.toString(groupModerate10s));
                        highScore30s.setText(Integer.toString(groupModerate30s));
                        highScore10d.setText(Float.toString(groupModerate10d));
                        highScore50d.setText(Float.toString(groupModerate50d));
                        highScore100d.setText(Float.toString(groupModerate100d));
                        break;

                    case "Hard":
                        highScore5s.setText(Integer.toString(groupHard5s));
                        highScore10s.setText(Integer.toString(groupHard10s));
                        highScore30s.setText(Integer.toString(groupHard30s));
                        highScore10d.setText(Float.toString(groupHard10d));
                        highScore50d.setText(Float.toString(groupHard50d));
                        highScore100d.setText(Float.toString(groupHard100d));
                        break;
                }
            break;

            case "World":
                switch (difficulty) {
                    case "Easy":
                    default:
                        highScore5s.setText(Integer.toString(worldEasy5s));
                        highScore10s.setText(Integer.toString(worldEasy10s));
                        highScore30s.setText(Integer.toString(worldEasy30s));
                        highScore10d.setText(Float.toString(worldEasy10d));
                        highScore50d.setText(Float.toString(worldEasy50d));
                        highScore100d.setText(Float.toString(worldEasy100d));
                        break;

                    case "Moderate":
                        highScore5s.setText(Integer.toString(worldModerate5s));
                        highScore10s.setText(Integer.toString(worldModerate10s));
                        highScore30s.setText(Integer.toString(worldModerate30s));
                        highScore10d.setText(Float.toString(worldModerate10d));
                        highScore50d.setText(Float.toString(worldModerate50d));
                        highScore100d.setText(Float.toString(worldModerate100d));
                        break;

                    case "Hard":
                        highScore5s.setText(Integer.toString(worldHard5s));
                        highScore10s.setText(Integer.toString(worldHard10s));
                        highScore30s.setText(Integer.toString(worldHard30s));
                        highScore10d.setText(Float.toString(worldHard10d));
                        highScore50d.setText(Float.toString(worldHard50d));
                        highScore100d.setText(Float.toString(worldHard100d));
                        break;
                }
            break;
        }
    }

    // User click Individual button
    private void individualSelected() {
        SelectGroupText.setVisibility(INVISIBLE);
        spinner.setVisibility(INVISIBLE);

        regionType = "Individual";
    }

    // User click Group button
    private void groupSelected() {
        if (thisUser.getGroups().size() == 0) {
            SelectGroupText.setVisibility(VISIBLE);
            spinner.setVisibility(VISIBLE);
        }

        regionType = "Group";
    }

    // User click World button
    private void worldSelected() {
        SelectGroupText.setVisibility(INVISIBLE);
        spinner.setVisibility(INVISIBLE);

        regionType = "World";
    }

    // User click Easy button
    private void easySelected() {
        difficulty = "Easy";
    }

    // User click Moderate button
    private void moderateSelected() {
        difficulty = "Moderate";
    }

    // User click Hard button
    private void hardSelected() {
        difficulty = "Hard";
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {  }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_high_scores, menu);
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
