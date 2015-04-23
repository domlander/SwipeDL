package com.dominik.swipedl;

import android.graphics.Color;
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
import java.util.ArrayList;

import static android.view.View.*;

public class HighScores extends ActionBarActivity implements AdapterView.OnItemSelectedListener, OnClickListener {

    private TextView SelectGroupText;
    private Spinner spinner;
    private User thisUser;

    private Button individual_button;
    private Button group_button;
    private Button world_button;

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
        userNames.add("aaa"); // debug
        userNames.add("bbb"); // debug

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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Individual:
                individualSelected();
                break;

            case R.id.Group:
                groupSelected();
                break;

            case R.id.World:
                worldSelected();
                break;
        }
    }

    private void individualSelected() {
        SelectGroupText.setVisibility(INVISIBLE);
        spinner.setVisibility(INVISIBLE);

        individual_button.setBackgroundColor(Color.GREEN);
        group_button.setBackgroundColor(Color.LTGRAY);
        world_button.setBackgroundColor(Color.LTGRAY);
    }

    private void groupSelected() {
        if (thisUser.getGroups().size() == 0) {
            SelectGroupText.setVisibility(VISIBLE);
            spinner.setVisibility(VISIBLE);
        }
        individual_button.setBackgroundColor(Color.LTGRAY);
        group_button.setBackgroundColor(Color.GREEN);
        world_button.setBackgroundColor(Color.LTGRAY);
    }

    private void worldSelected() {
        SelectGroupText.setVisibility(INVISIBLE);
        spinner.setVisibility(INVISIBLE);

        individual_button.setBackgroundColor(Color.LTGRAY);
        group_button.setBackgroundColor(Color.LTGRAY);
        world_button.setBackgroundColor(Color.GREEN);
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
