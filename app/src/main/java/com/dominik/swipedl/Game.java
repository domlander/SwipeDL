package com.dominik.swipedl;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Game extends ActionBarActivity {

    TextView dragCount;
    public TextView timerValue;
    SurfaceView dragArea;
    TextView debugText;
    Button startButton;
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

    int gameTime;
    int dragLimit;

    // Shared Preferences
    SharedPreferences sharedPrefs;
    int numPlayers;
    int gameMode;
    int gameModeOption;
    int difficulty;

    GameTimer countDownTimer;
    long timeRemaining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        dragCount = (TextView) findViewById(R.id.count);
        startButton = (Button) findViewById(R.id.startButton);
        pauseButton = (Button) findViewById(R.id.pauseButton);
        timerValue = (TextView) findViewById(R.id.timerValue);

        // Get settings: difficulty/game mode.
        sharedPrefs = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        numPlayers = sharedPrefs.getInt("numPlayers", 1);
        gameMode = sharedPrefs.getInt("gameMode", 1);
        gameModeOption = sharedPrefs.getInt("gameModeOption", 3);
        difficulty = sharedPrefs.getInt("difficulty", 1);

        // time limit
        if (gameMode == 1) {

            if (gameModeOption == 1) { // 5 seconds
                gameTime = 5000;

            } else if (gameModeOption == 2) { // 10 seconds
                gameTime = 10000;

            } else { // 30 seconds
                gameTime = 30000;
            }

        // drag total
        } else {
            if (gameModeOption == 1) { // 10 drags
                dragLimit = 10;

            } else if (gameModeOption == 2) { // 50 drags
                dragLimit = 50;

            } else { // 100 drags
                dragLimit = 100;

            }
        }

        dragArea = (SurfaceView) findViewById(R.id.dragArea);
        setDragArea(difficulty);
        dragArea.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent swipe) {
                // start button has not been pressed
                if (!gameRunning) { return false; }

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

                            if (swipe.getY() > y_start) {
                                // set the initial direction of the drag.
                                swipeDirection = DOWN;
                                // if the drag is going down and the current y value
                                // is lower than the highest recorded value of y (y_max),
                                // then the direction has changed.
                                if (swipe.getY() <= y_max) {
                                    crossedNS = true;
                                } else {
                                    // y still going down => update y_max with new current value.
                                    y_max = swipe.getY();
                                }

                            } else if (swipe.getY() < y_start) {
                                swipeDirection = UP;
                                // The same as above but with the initial drag direction going up.
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
                dragCount.setText(Integer.toString(numSwipes));
                return true;
            }
        });
    }

    // Start button clicked.
    public void onStartButtonClick(View v) {
        if (!gameRunning) {
            gameRunning = true;
            numSwipes = 0;
            dragCount.setText(Integer.toString(numSwipes));

            if (gameMode == 1) { // Timed game
                countDownTimer = new GameTimer(gameTime, 10);
                countDownTimer.start();
            } else {
                // countUpTimer
            }
            startButton.setVisibility(View.INVISIBLE);
        }
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

    // Pauses the game clock.
    public void onPauseClick(View v) {
        if (gameRunning) {
            countDownTimer.cancel();
            gameRunning = false;
        } else {
            countDownTimer = new GameTimer(timeRemaining, 10);
            countDownTimer.start();
            gameRunning = true;
        }
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

    // Checks to see if the score recorded is a high score.
    // If so, update the high scores page.
    private boolean newHighScore(int score) {
        sharedPrefs = getSharedPreferences("High Scores", Context.MODE_PRIVATE);
        String gameType = "individualEasy30s"; // default

        // Get existing high score from shared preferences
        switch (difficulty) {
            // Easy
            case 1:
                switch (gameMode) {
                    case 1:
                        switch (gameModeOption) {
                            case 1: gameType = "individualEasy5s"; break;
                            case 2: gameType = "individualEasy10s"; break;
                            case 3: gameType = "individualEasy30s"; break;
                        }
                    break;
                    case 2:
                        switch (gameModeOption) {
                            case 1: gameType = "individualEasy10d"; break;
                            case 2: gameType = "individualEasy50d"; break;
                            case 3: gameType = "individualEasy100d"; break;
                        }
                    break;
                }
            break;
            // Moderate
            case 2:
                switch (gameMode) {
                    case 1:
                        switch (gameModeOption) {
                            case 1: gameType = "individualModerate5s"; break;
                            case 2: gameType = "individualModerate10s"; break;
                            case 3: gameType = "individualModerate30s"; break;
                        }
                    break;
                    case 2:
                        switch (gameModeOption) {
                            case 1: gameType = "individualModerate10d"; break;
                            case 2: gameType = "individualModerate50d"; break;
                            case 3: gameType = "individualModerate100d"; break;
                        }
                    break;
                }
            break;
            // Hard
            case 3:
                switch (gameMode) {
                    case 1:
                        switch (gameModeOption) {
                            case 1: gameType = "individualHard5s"; break;
                            case 2: gameType = "individualHard10s"; break;
                            case 3: gameType = "individualModerate30s"; break;
                        }
                    break;
                    case 2:
                        switch (gameModeOption) {
                            case 1: gameType = "individualHard10d"; break;
                            case 2: gameType = "individualHard50d"; break;
                            case 3: gameType = "individualHard100d"; break;
                        }
                    break;
                }
            break;
        }
        // Current Individual high score for this game type.
        int highScore = sharedPrefs.getInt(gameType, 0);

        Toast.makeText(getApplicationContext(), Integer.toString(highScore),
                Toast.LENGTH_LONG).show();

        if (score > highScore) {
            SharedPreferences.Editor editor = sharedPrefs.edit();

            Toast.makeText(getApplicationContext(), gameType, Toast.LENGTH_LONG).show();
            editor.putInt(gameType, score);
            editor.apply();
            return true;
        }
        return false;
    }

    // Inner class. Timer for time-limit games.
    public class GameTimer extends CountDownTimer {

        public GameTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int seconds = (int) (millisUntilFinished / 1000);
            int millis = (int) (millisUntilFinished % 1000);
            timeRemaining = millisUntilFinished;
            timerValue.setText(String.format("%02d:%03d", seconds, millis));
        }

        @Override
        public void onFinish() {
            gameRunning = false;
            timerValue.setText("You made " + numSwipes + " drags in " +
                    gameTime / 1000 + " seconds" );
            if (newHighScore(numSwipes)) {
                Toast.makeText(getApplicationContext(), "New High Score!",
                        Toast.LENGTH_LONG).show();
            }

            startButton.setVisibility(View.VISIBLE);
        }

    }

}