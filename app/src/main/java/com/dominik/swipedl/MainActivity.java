package com.dominik.swipedl;

        import android.os.CountDownTimer;
        import android.support.v7.app.ActionBarActivity;
        import android.os.Bundle;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.MotionEvent;
        import android.view.View;
        import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

    // For a swipe to count, the player must be within ALLOWED_ANGLE
    // degrees from vertical.
    private final double ALLOWED_ANGLE_DEGREES = 15;

    private final double ALLOWED_ANGLE_RADS = ALLOWED_ANGLE_DEGREES * 0.0174532925;
    private boolean gameRunning = false;
    private int numSwipes = 0;


    /*
     * Timer that is called after every swipe. After 1 second the total
     * number of swipes is displayed.
     */
    private CountDownTimer timer = new CountDownTimer(1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {  }

        @Override
        public void onFinish() {
            TextView countTV = (TextView) findViewById(R.id.count);
            countTV.setText(Integer.toString(numSwipes));
        }
    };

    /*
     * Calculates the angle between the line between the two points:
     * (x1,x2) and (y1,y2), and vertical line that intersects (y1,y2)
     */
    public double getAngle(double x1, double x2, double y1, double y2) {
        if (y1 == y2) { return 0; }

        double adjacent = y2 - y1;
        double hypotenuse = Math.pow(Math.pow((y2 - y1), 2) + Math.pow((x2 - x1), 2), 0.5);
        double angle = Math.acos(adjacent / hypotenuse);

        return angle;
    }

    public void onButtonClick(View v) {
        gameRunning = true;
        timer.start();
    }

    // Registers the co-ordinates of the user touching the screen and releasing.
    double x_start, x_end;
    double y_start, y_end;

    double y_current; // current y value of the drag
    boolean crossNS = false;
    /*
     * Called when the user touches the screen.
     */
    public boolean onTouchEvent(MotionEvent swipe){
        // start button has not been pressed
        if (!gameRunning) { return false; }

        TextView countTV = (TextView) findViewById(R.id.count);
        TextView debugText = (TextView) findViewById(R.id.debugText);

        // cancel the timer and hide the current score.
        timer.cancel();
        countTV.setText("");

        switch (swipe.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x_start = swipe.getX();
                y_start = y_current = swipe.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                if (!crossNS) {
                    // if user starts dragging upwards the drag will not be valid
                    if (swipe.getY() < y_start) {
                        crossNS = true;
                    }
                    // When the user stops dragging down, the north-south
                    // divide has been crossed and the drag ends.
                    if (swipe.getY() >= y_current) {
                        y_current = swipe.getY();
                    } else {
                        crossNS = true;
                        if (isLegalDrag(swipe)) {
                            numSwipes += 1;
                        }
                    }
                }
                debugText.setText(
                        String.format("%.2f", x_start) + " " +
                                String.format("%.2f", x_end) + " " +
                                String.format("%.2f", y_start) + " " +
                                String.format("%.2f", y_end) + " " +
                                String.format("%.2f", getAngle(x_start, x_end, y_start, y_end)) + " " +
                                String.format("%.2f", swipe.getY()) + " " +
                                String.format("%.2f", y_current) + " " +
                                Boolean.toString(crossNS));
                break;

            case MotionEvent.ACTION_UP:
                x_end = swipe.getX();
                y_end = swipe.getY();

                if ((!crossNS) && (isLegalDrag(swipe))) {
                    numSwipes += 1;
                }
                crossNS = false;

                break;
        }
        timer.start();
        return false;
    }

    private boolean isLegalDrag(MotionEvent swipe) {
        x_end = swipe.getX();
        y_end = swipe.getY();
        if ((y_end > y_start) &&
                (getAngle(x_start, x_end, y_start, y_end) <= ALLOWED_ANGLE_RADS)) {
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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