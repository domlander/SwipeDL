package com.dominik.swipedl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

//TODO Change gameMode and gameOption to one gameMode = {t5, t10, t30, d10, d50, d100}

public class Game extends ActionBarActivity {

    Button playButton;
    Button settingsButton;
    Button highScoresButton;
    TextView dragCount;
    TextView timerValue;
    TextView completionMessage;
    TextView newHighScoreMessage;
    SurfaceView dragArea;
    Button startButton;
    Button pauseButton;
    TextView countDownNumberTextView;
    TextView pausedText1;
    TextView pausedText2;

    private GameState gameState;
    private int numSwipes = 0;
    private final String UP = "UP";
    private final String DOWN = "DOWN";

    // for debug messages
    private static final String TAG = "Game";

    private String swipeDirection;
    private boolean crossedNS; // if true, the user has changed swipe directions.

    // Registers the co-ordinates of the user touching the screen and releasing.
    private double x_start, x_end;
    private double y_start, y_end;
    private double y_min, y_max;

    private int gameTime;
    private int dragLimit;

    // Shared Preferences
    SharedPreferences sharedPrefs;
    int numPlayers;
    int gameMode;
    int gameModeOption;
    int difficulty;

    private long timeRemaining;

    GameCountDownTimer countDownTimer;
    CountDownTimer delayTimer;
    CountDownTimer displayResultCountDownTimer;
    CountDownTimer preGameCountDownTimer;
    int preGameCountDownNumber;

    Intent goToSettings;
    Intent goToHighScores;

    // For standard timer in drag limit games.
    long startTime = 0;
    long millisSinceStart;
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            millisSinceStart = System.currentTimeMillis() - startTime;
            int minutes = (int) (millisSinceStart / 60000);
            int seconds = (int) ((millisSinceStart / 1000) % 60);
            int millis = (int) millisSinceStart % 1000;

            if (minutes > 0) {
                timerValue.setText(String.format("%2d:%2d.%03d", minutes, seconds, millis));
            } else {
                timerValue.setText(String.format("%2d.%03ds", seconds, millis));
            }
            timerHandler.postDelayed(this, 0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        // UI
        playButton = (Button) findViewById(R.id.GameMenuBar);
        settingsButton = (Button) findViewById(R.id.SettingsMenuBar);
        highScoresButton = (Button) findViewById(R.id.HighScoresMenuBar);
        dragCount = (TextView) findViewById(R.id.count);
        startButton = (Button) findViewById(R.id.startButton);
        pauseButton = (Button) findViewById(R.id.pauseButton);
        timerValue = (TextView) findViewById(R.id.timerValue);
        completionMessage = (TextView) findViewById(R.id.result);
        newHighScoreMessage = (TextView) findViewById(R.id.newHighScore);
        countDownNumberTextView = (TextView) findViewById(R.id.countdownNumber);
        pausedText1 = (TextView) findViewById(R.id.pauseText1);
        pausedText2 = (TextView) findViewById(R.id.pauseText2);

        goToSettings = new Intent(this, Settings.class);
        goToHighScores = new Intent(this, HighScores.class);

        goToSettings.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        goToHighScores.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

    } // onCreate end

    @Override
    protected void onResume() {
        super.onResume();

        completionMessage.setVisibility(View.INVISIBLE);
        newHighScoreMessage.setVisibility(View.INVISIBLE);
        pausedText1.setVisibility(View.INVISIBLE);
        pausedText2.setVisibility(View.INVISIBLE);

        // Get settings: difficulty/game mode.
        sharedPrefs = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        numPlayers = sharedPrefs.getInt("numPlayers", 1);
        gameMode = sharedPrefs.getInt("gameMode", 1);
        gameModeOption = sharedPrefs.getInt("gameModeOption", 3);
        difficulty = sharedPrefs.getInt("difficulty", 1);

        // Set game type variable
        switch (gameMode) {
            case 1: // time limit
                switch (gameModeOption) {
                    case 1: // 5 seconds
                        gameTime = 5000;
                        break;
                    case 2: // 10 seconds
                        gameTime = 10000;
                        break;
                    case 3: // 30 seconds
                        gameTime = 30000;
                        break;
                }
                break;

            case 2: // drag total
                switch (gameModeOption) {
                    case 1: // 10 drags
                        dragLimit = 10;
                        break;
                    case 2: // 50 drags
                        dragLimit = 50;
                        break;
                    case 3: // 100 drags
                        dragLimit = 100;
                        break;
                }
                break;
        }

        gameState = GameState.OFF;

        dragArea = (SurfaceView) findViewById(R.id.dragArea);
        setDragArea(difficulty);
        dragArea.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent swipe) {
                // Game is not running or is paused.
                if (!gameState.equals(GameState.ON)) {
                    return false;
                }

                switch (swipe.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Set initial values when user first touches drag area.
                        x_start = swipe.getX();
                        y_start = y_min = y_max = swipe.getY();
                        swipeDirection = "";
                        debugInfo(swipe,"down");
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
                        break;

                    case MotionEvent.ACTION_UP:
                        // If the north-south divide has been crossed, the isLegalDrag()
                        // calculation has already been covered in MotionEvent.ACTION_MOVE.
                        if ((!crossedNS) && (isLegalDrag(swipe))) {
                            numSwipes += 1;
                        }
                        crossedNS = false;
                        debugInfo(swipe,"up");
                        break;
                }
                dragCount.setText(Integer.toString(numSwipes));

                // if drag limit met
                if (gameMode == 2) {
                    if (((gameModeOption == 1) && (numSwipes >= 10)) ||
                            ((gameModeOption == 2) && (numSwipes >= 50)) ||
                            ((gameModeOption == 3) && (numSwipes >= 100))) {
                        dragLimitReached();
                    }
                }
                return true;
            }
        });

        preGameCountDownNumber = 3;
        // Countdown that runs before each game.
        preGameCountDownTimer = new CountDownTimer(800, 1) {
            int textSize = 40;
            public void onTick(long millisUntilFinished) {
                if (textSize < 90) {
                    countDownNumberTextView.setTextSize(textSize++);
                }
            }

            public void onFinish() {
                textSize = 40;
                preGameCountDownNumber -= 1;
                countDownNumberTextView.setTextSize(textSize);
                preGameCountDown();
            }
        };

        // Delay timer countdown. Gives activity time to load before countdown starts.
        delayTimer = new CountDownTimer(300, 300) {
            public void onTick(long millisUntilFinished) { }
            public void onFinish() { preGameCountDown(); }
        };
        delayTimer.start();

        // Hides result completion message after a period of time.
        displayResultCountDownTimer = new CountDownTimer(4000, 4000) {
            public void onTick(long millisUntilFinished) { }
            public void onFinish() {
                newHighScoreMessage.setVisibility(View.INVISIBLE);
                completionMessage.setVisibility(View.INVISIBLE);
            }
        };

    } // onStart() end

    // Starts the countdown to the game.
    public void onStartButtonClick(View v) {
        toggleButtonVisibility(true);
        timerValue.setVisibility(View.INVISIBLE);
        preGameCountDown();
    }

    // pause game
    private void pauseGame() {
        gameState = GameState.PAUSED;
        highScoresButton.setVisibility(View.VISIBLE);
        pausedText1.setVisibility(View.VISIBLE);
        pausedText2.setVisibility(View.VISIBLE);
        playButton.setVisibility(View.VISIBLE);
        settingsButton.setVisibility(View.VISIBLE);
    }

    // Pauses the game clock.
    public void onPauseClick(View v) {
        switch (gameState) {
            case COUNTDOWN:
                preGameCountDownTimer.cancel();
                pauseGame();
                break;

            case ON:
                if (gameMode == 1) {
                    countDownTimer.cancel();
                } else {
                    timerHandler.removeCallbacks(timerRunnable);
                }
                pauseGame();
                break;

            case PAUSED:
                // game paused during pre-game countdown.
                if (countDownNumberTextView.getVisibility() == View.VISIBLE) {
                    preGameCountDownTimer.start();
                    gameState = GameState.COUNTDOWN;

                    // game paused in game.
                } else {
                    if (gameMode == 1) {
                        countDownTimer = new GameCountDownTimer(timeRemaining, 10);
                        countDownTimer.start();
                    } else {
                        startTime = System.currentTimeMillis() - millisSinceStart;
                        timerHandler.postDelayed(timerRunnable, 0);
                    }
                    gameState = GameState.ON;
                }
                pausedText1.setVisibility(View.INVISIBLE);
                pausedText2.setVisibility(View.INVISIBLE);
                playButton.setVisibility(View.INVISIBLE);
                settingsButton.setVisibility(View.INVISIBLE);
                highScoresButton.setVisibility(View.INVISIBLE);
                break;
        }
    }

    public void onSettingsButtonClick(View view) {
        startActivity(goToSettings);
    }

    public void onHighScoresButtonClick(View view) {
        startActivity(goToHighScores);
    }

    // Called for each number in the countdown.
    private void preGameCountDown() {
        gameState = GameState.COUNTDOWN;
        countDownNumberTextView.setText(Integer.toString(preGameCountDownNumber));
        countDownNumberTextView.setVisibility(View.VISIBLE);
        if (preGameCountDownNumber > 0) {
            preGameCountDownTimer.start();

        } else {
            preGameCountDownNumber = 3;
            countDownNumberTextView.setVisibility(View.INVISIBLE);
            startGame();
        }
    }

    // Start the game timer - drags are counted from now.
    public void startGame() {
        gameState = GameState.ON;
        numSwipes = 0;
        dragCount.setText(Integer.toString(numSwipes));
        timerValue.setVisibility(View.VISIBLE);

        if (gameMode == 1) { // Timed game
            countDownTimer = new GameCountDownTimer(gameTime, 10);
            countDownTimer.start();

        } else { // Drag limit game
            startTime = System.currentTimeMillis();
            timerHandler.postDelayed(timerRunnable, 0);
        }
        toggleButtonVisibility(true);
    }

    // When game start hide top menu & start button; show pause button.
    // When game ends show top menu & start button; hide pause button.
    private void toggleButtonVisibility(boolean visible) {
        if (visible) {
            pauseButton.setVisibility(View.VISIBLE);
            highScoresButton.setVisibility(View.INVISIBLE);
            playButton.setVisibility(View.INVISIBLE);
            settingsButton.setVisibility(View.INVISIBLE);
            startButton.setVisibility(View.INVISIBLE);

        } else {
            pauseButton.setVisibility(View.INVISIBLE);
            highScoresButton.setVisibility(View.VISIBLE);
            playButton.setVisibility(View.VISIBLE);
            settingsButton.setVisibility(View.VISIBLE);
            startButton.setVisibility(View.VISIBLE);
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

    // The player has reached the total amount of drags required.
    // DRAG limit only
    private void dragLimitReached() {
        timerHandler.removeCallbacks(timerRunnable);
        gameState = GameState.OFF;

        int minutes = (int) (millisSinceStart / 60000);
        int seconds = (int) ((millisSinceStart / 1000) % 60);
        int millis = (int) millisSinceStart % 1000;

        String result;
        boolean isHighScore = false;
        if (minutes > 0) {
            result = String.format("You made %d drags in %2d:%2d.%03ds", numSwipes, minutes, seconds, millis);
        } else {
            result = String.format("You made %d drags in %2d.%03ds", numSwipes, seconds, millis);
        }

        if (newHighScore(millisSinceStart)) {
            isHighScore = true;
        }

        displayResult(result, isHighScore);
        toggleButtonVisibility(false);
    }

    // If the score recorded is a high score, update the high scores page.
    // TIME Limit games only.
    private boolean newHighScore(int score) {
        sharedPrefs = getSharedPreferences("High Scores", Context.MODE_PRIVATE);
        String gameType = "individualEasy30s"; // default

        // Get existing high score from shared preferences
        switch (difficulty) {
            case 1:  // Easy
                switch (gameModeOption) {
                    case 1: gameType = "individualEasy5s"; break;
                    case 2: gameType = "individualEasy10s"; break;
                    case 3: gameType = "individualEasy30s"; break;
                }
            break;

            case 2: // Moderate
                switch (gameModeOption) {
                    case 1: gameType = "individualModerate5s"; break;
                    case 2: gameType = "individualModerate10s"; break;
                    case 3: gameType = "individualModerate30s"; break;
                }
            break;

            case 3: // Hard
                switch (gameModeOption) {
                    case 1: gameType = "individualHard5s"; break;
                    case 2: gameType = "individualHard10s"; break;
                    case 3: gameType = "individualModerate30s"; break;
                }
            break;
        }

        // Current Individual high score for this game type.
        int highScore = sharedPrefs.getInt(gameType, 0);

        if (score > highScore) {
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putInt(gameType, score);
            editor.apply();
            return true;
        }
        return false;
    }

    // If the score recorded is a high score, update the high scores page.
    // DRAG Limit games only.
    // TODO group/world high score check.
    private boolean newHighScore(float millisSinceStart) {
        sharedPrefs = getSharedPreferences("High Scores", Context.MODE_PRIVATE);
        String gameType = "individualEasy30s"; // default

        // Get existing high score from shared preferences
        switch (difficulty) {
            case 1:  // Easy
                switch (gameModeOption) {
                    case 1: gameType = "individualEasy10d"; break;
                    case 2: gameType = "individualEasy50d"; break;
                    case 3: gameType = "individualEasy100d"; break;
                }
                break;

            case 2: // Moderate
                switch (gameModeOption) {
                    case 1: gameType = "individualModerate10d"; break;
                    case 2: gameType = "individualModerate50d"; break;
                    case 3: gameType = "individualModerate100d"; break;
                }
                break;

            case 3: // Hard
                switch (gameModeOption) {
                    case 1: gameType = "individualHard10d"; break;
                    case 2: gameType = "individualHard50d"; break;
                    case 3: gameType = "individualHard100d"; break;
                }
                break;
        }

        // Current Individual high score for this game type.
        float highScore = sharedPrefs.getFloat(gameType, 0);

        if ((millisSinceStart < (long) highScore) || (highScore == 0)) {
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putFloat(gameType, millisSinceStart);
            editor.apply();
            return true;
        }
        return false;
    }

    // Displays final result.
    private void displayResult(String result, boolean isHighScore) {

        if (isHighScore) {
            newHighScoreMessage.setVisibility(View.VISIBLE);
        }

        completionMessage.setText(result);
        completionMessage.setVisibility(View.VISIBLE);
        displayResultCountDownTimer.start();
    }

    // Prints stats to screen for debugging purposes
    public void debugInfo(MotionEvent swipe, String action) {
        String debugMessage = action +
                "\nx_start: " + String.format("%.2f", x_start) +
                "\nx_end: " + String.format("%.2f", x_end) +
                "\ny_start: " + String.format("%.2f", y_start) +
                "\ny_end: " + String.format("%.2f", y_end) +
                "\nyMin: " + String.format
                ("%.2f", y_min) +
                "\nyMax: " + String.format("%.2f", y_max) +
                "\ngrad: " + String.format("%.2f", Math.abs((y_end - y_start) / (x_end - x_start))) +
                "\ny: " + String.format("%.2f", swipe.getY()) +
                "\nSwipe Direction: " + swipeDirection +
                "\ncrossedNS: " + Boolean.toString(crossedNS);

        Log.d(TAG, debugMessage);
    }

    // Inner class. Timer for time-limit games.
    public class GameCountDownTimer extends CountDownTimer {

        public GameCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int seconds = (int) (millisUntilFinished / 1000);
            int millis = (int) (millisUntilFinished % 1000);

            timeRemaining = millisUntilFinished;
            timerValue.setText(String.format("%02d.%03ds", seconds, millis));
        }

        @Override
        public void onFinish() {
            gameState = GameState.OFF;
            timerValue.setText("0.000s");

            String result = "You made " + numSwipes + " drags in " + gameTime / 1000 + " seconds";
            boolean isHighScore = false;

            if (newHighScore(numSwipes)) {
                isHighScore = true;
            }

            displayResult(result, isHighScore);
            toggleButtonVisibility(false);
        }
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