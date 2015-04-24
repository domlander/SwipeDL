package com.dominik.swipedl;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Game extends ActionBarActivity {

    TextView countTV;
    SurfaceView dragArea;
    TextView debugText;
    Button pauseButton;

    private boolean gameRunning = false;
    private int numSwipes = 0;
    private final String UP = "UP";
    private final String DOWN = "DOWN";

    String swipeDirection;
    boolean crossedNS; // if true, the user has changed swipe directions.

    // Registers the co-ordinates of the user touching the screen and releasing.
    double x_start, x_end;
    double y_start, y_end;
    double y_min, y_max;

    SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        // Get settings: difficulty/game mode.
        sharedPrefs = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        int numPlayers = sharedPrefs.getInt("numPlayers", 1);
        boolean timeLimit = sharedPrefs.getBoolean("timeLimit", true);
        int gameModeOption = sharedPrefs.getInt("gameModeOption", 3);
        int difficulty = sharedPrefs.getInt("difficulty", 1);

        dragArea = (SurfaceView) findViewById(R.id.dragArea);
        setDragArea(difficulty);

        dragArea.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent swipe) {
                // start button has not been pressed
                if (!gameRunning) { return false; }

                countTV = (TextView) findViewById(R.id.count);

                switch (swipe.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Set initial values when user first touches drag area.
                        x_start = swipe.getX();
                        y_start = y_min = y_max = swipe.getY();
                        swipeDirection = "";
//                      debugText(swipe,"down");
                        break;

                    case MotionEvent.ACTION_MOVE:

                        // If user has already changed direction, the isLegalDrag()
                        // calculation will have already been covered in this case.
                        if (!crossedNS) {

                            // set the initial direction of the drag.
                            if (swipe.getY() > y_start) {
                                swipeDirection = DOWN;
                            } else if (swipe.getY() < y_start) {
                                swipeDirection = UP;
                            }

                            // if the drag is going down and the current y value
                            // is lower than the highest recorded value of y (y_max),
                            // then the direction has changed.
                            if (swipeDirection.equals(DOWN)) {
                                if (swipe.getY() <= y_max) {
                                    crossedNS = true;
                                } else {
                                    // y still going down => update y_max with new current value.
                                    y_max = swipe.getY();
                                }

                            // The same as above but with the initial drag direction going up.
                            } else if (swipeDirection.equals(UP)) {
                                if (swipe.getY() >= y_min) {
                                    crossedNS = true;
                                } else {
                                    y_min = swipe.getY();
                                }
                            }

                            // If the north-south divide has been crossed the drag ends.
                            if ((crossedNS) && (isLegalDrag(swipe))) {
                                numSwipes += 1;
                            }
                        }
//                      debugText(swipe,"move");
                        break;

                    case MotionEvent.ACTION_UP:
                        // If the north-south divide has been crossed, the isLegalDrag()
                        // calculation has already been covered in MotionEvent.ACTION_MOVE.
                        if ((!crossedNS) && (isLegalDrag(swipe))) {
                            numSwipes += 1;
                        }
                        crossedNS = false;
//                      debugText(swipe,"up");
                        break;
                }
                countTV.setText(Integer.toString(numSwipes));
                return true;
            }
        });
    }

    // Finds gradient. If angle between drag and vertical line is <15degrees, drag is legal.
    private boolean isLegalDrag(MotionEvent swipe) {
        x_end = swipe.getX();
        y_end = swipe.getY();

        // In case x_start == x_end -> avoid dividing by zero
        if (x_start == x_end) {
            return (y_start != y_end);
        }

        return ((y_start != y_end) &&
                (Math.abs((y_end - y_start) / (x_end - x_start)) >= 3.73205)); // 15 degrees from vertical
    }

    // Sets the size of the drag area dependant on the difficulty level.
    private void setDragArea(int difficulty) {
        switch (difficulty) {
            case 1: // easy
            default:
                resizeDragArea(600);
                break;

            case 2:
                resizeDragArea(400);
                break;

            case 3: // hard
                resizeDragArea(200);
                break;
        }
    }

    // Sets the size of the drag area that the user swipes in
    private void resizeDragArea(int width) {
        dragArea.getHolder().setFixedSize(width, width);
    }

    public void onPauseClick(View v) {
        //TODO
    }

    // When START is clicked, start game.
    public void onButtonClick(View v) {
        gameRunning = true;
    }

    // Prints stats to screen for debugging purposes
    private void debugText(MotionEvent swipe, String action) {
        debugText = (TextView) findViewById(R.id.debugText);
        debugText.setText(
            action +
            "\nx_start: " + String.format("%.2f", x_start) +
            "\nx_end: " + String.format("%.2f", x_end) +
            "\ny_start: " + String.format("%.2f", y_start) +
            "\ny_end: " + String.format("%.2f", y_end) +
            "\nyMin: " + String.format("%.2f", y_min) +
            "\nyMax: " + String.format("%.2f", y_max) +
            "\ngrad: " + String.format("%.2f", Math.abs((y_end - y_start) / (x_end - x_start))) +
            "\ny: " + String.format("%.2f", swipe.getY()) +
            "\nSwipe Direction: " + swipeDirection +
            "\ncrossedNS: " + Boolean.toString(crossedNS)
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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