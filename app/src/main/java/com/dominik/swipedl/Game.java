package com.dominik.swipedl;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Game extends ActionBarActivity {

    TextView countTV;
    SurfaceView dragArea;
    SurfaceView dragAreaBorder;
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

        setDragArea(difficulty);
    }

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

    private void resizeDragArea(int width) {
        dragArea = (SurfaceView) findViewById(R.id.dragArea);
        dragArea.getHolder().setFixedSize(width, width);
    }

    public void onPauseClick(View v) {
        //TODO
    }


    public void onButtonClick(View v) {
        gameRunning = true;
    }

    // "View" is the draggable box.
//    View myView = findViewById(R.id.my_view);
//    myView.setOnTouchListener(new OnTouchListener() {
//        public boolean onTouch(View v, MotionEvent event) {
//            // ... Respond to touch events
//            return true;
//        }
//    });

    /*
     * Called when the user touches the screen.
     */
    public boolean onTouchEvent(MotionEvent swipe){
        // start button has not been pressed
        if (!gameRunning) { return false; }

        countTV = (TextView) findViewById(R.id.count);

        switch (swipe.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x_start = swipe.getX();
                y_start = y_min = y_max = swipe.getY();
                swipeDirection = "";
                break;

            case MotionEvent.ACTION_MOVE:

                // if user has not already changed direction.
                if (!crossedNS) {

                    // set the initial direction of the drag.
                    if (swipe.getY() > y_start) {
                        swipeDirection = DOWN;
                    } else if (swipe.getY() < y_start) {
                        swipeDirection = UP;
                    }

                    // if the drag is going down and the current y value is higher than
                    // the lowest recorded value of y, then the direction has changed.
                    if (swipeDirection.equals(DOWN)) {
                        if (swipe.getY() <= y_max) {
                            crossedNS = true;
                        } else {
                            y_max = swipe.getY(); // Update y_max with current value.
                        }

                    // The same as above but with the initial drag direction going up.
                    } else if (swipeDirection.equals(UP)) {
                        if (swipe.getY() >= y_min) {
                            crossedNS = true;
                        } else {
                            y_min = swipe.getY(); // Update y_min with current value.
                        }
                    }

                    // If the north-south divide has been crossed the drag ends.
                    if (crossedNS = true) {
                        x_end = swipe.getX();
                        y_end = swipe.getY();
                        if (isLegalDrag()) {
                            numSwipes += 1;
                        }
                    }
                }
                debugText(swipe);
                break;

            case MotionEvent.ACTION_UP:
                if (!crossedNS) {
                    x_end = swipe.getX();
                    y_end = swipe.getY();
                    if ((isLegalDrag())) {
                        numSwipes += 1;
                    }
                    crossedNS = false;
                }
                debugText(swipe);
                break;
        }
        countTV.setText(Integer.toString(numSwipes));
        return false;
    }

    // Finds gradient. If angle between drag and vertical line is <15degrees, drag is legal.
    private boolean isLegalDrag() {
        if ((y_start == y_end) || (x_start == x_end)) { return false; }

        return Math.abs((y_end - y_start) / (x_end - x_start)) >= 3.73205; // 15 degrees from vertical
    }

    // Prints stats to screen for debugging purposes
    private void debugText(MotionEvent swipe) {
        debugText = (TextView) findViewById(R.id.debugText);
        debugText.setText(
            "x1: " + String.format("%.2f", x_start) +
            " x2: " + String.format("%.2f", x_end) +
            " y1: " + String.format("%.2f", y_start) +
            " y2: " + String.format("%.2f", y_end) +
            " yMin: " + String.format("%.2f", y_min) +
            " yMax: " + String.format("%.2f", y_max) +
            " grad: " + String.format("%.2f", Math.abs((y_end - y_start) / (x_end - x_start))) +
            " y: " + String.format("%.2f", swipe.getY()) +
            " Swipe Direction: " + swipeDirection +
            " crossedNS: " + Boolean.toString(crossedNS)
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