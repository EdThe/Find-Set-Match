//This class contains all the methods for 2-player mode

package genius.gyulhap;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnClickListener;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    int ansCount;
    String next = null;
    int timePassed = 0;
    int untilTime = 0;
    CountDownTimer cdt = null;
    int roundNumber = 1;
    int endRound = 3;
    AlertDialog.Builder builder;
    AlertDialog.Builder pause;
    AlertDialog alertDialog;
    ArrayList<Integer> hapTry = new ArrayList<>();
    Grid g;
    boolean hapTrying = false;
    GridLayout answers;
    GridLayout answers2;
    boolean player1Turn = false;
    Handler timerSystem;
    TimeCircle timer;
    boolean allowed = true;
    boolean gyuling = false;
    Handler circling;
    long duration = 5000;
    MediaPlayer turnSound;
    MediaPlayer wrongSound;
    MediaPlayer rightSound;
    MediaPlayer music;

    //The runnable that changes the progress circle of the timer
    Runnable circleStyle = new Runnable() {
        @Override
        public void run() {
            double prog = 0.011;
            timer.setProgress(timer.getProgress() + prog);
            circling.postDelayed(circleStyle, 50);
        }
    };

    //The runnable that changes turns
    Runnable turnChange = new Runnable() {
        public void run() {
            player1Turn = !player1Turn;
            //Makes all possibly visible UnderscoreViews and Images invisible
            (findViewById(R.id.hapChoicesP1)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.hapChoicesP2)).setVisibility(View.INVISIBLE);
            findViewById(R.id.gyulGoodP1).setVisibility(View.INVISIBLE);
            findViewById(R.id.gyulGoodP2).setVisibility(View.INVISIBLE);
            circling.removeCallbacks(circleStyle);
            timer.setProgress(0);
            circling.post(circleStyle);
            GridLayout grid = (GridLayout) findViewById(R.id.hapGrid);
            //Sets the chosen variable of all cells to false, in case a "Combo" was attempted
            for (int i = 0; i < 9; i++) {
                if (((ViewCell) grid.getChildAt(i)).getChosen())
                    ((ViewCell) grid.getChildAt(i)).setChosen(false);
            }
            //Changes attributes of Views depending on whose turn it is
            if (player1Turn) {
                timer = (TimeCircle) findViewById(R.id.timer2);
                findViewById(R.id.turnP2).setAlpha((float) 0.3);
                findViewById(R.id.turnP1).setAlpha((float) 1);
                findViewById(R.id.buttonP2).setAlpha((float) 0.3);
                findViewById(R.id.buttonP1).setAlpha((float) 1);
                findViewById(R.id.gyulP2).setAlpha((float) 0.3);
                findViewById(R.id.gyulP1).setAlpha((float) 1);
                findViewById(R.id.gyulP2).setClickable(false);
                findViewById(R.id.gyulP1).setClickable(true);
                findViewById(R.id.buttonP2).setClickable(false);
                findViewById(R.id.buttonP1).setClickable(true);
            } else {
                timer = (TimeCircle) findViewById(R.id.timer);
                findViewById(R.id.turnP1).setAlpha((float) 0.3);
                findViewById(R.id.turnP2).setAlpha((float) 1);
                findViewById(R.id.buttonP1).setAlpha((float) 0.3);
                findViewById(R.id.buttonP2).setAlpha((float) 1);
                findViewById(R.id.gyulP1).setAlpha((float) 0.3);
                findViewById(R.id.gyulP2).setAlpha((float) 1);
                findViewById(R.id.gyulP1).setClickable(false);
                findViewById(R.id.gyulP2).setClickable(true);
                findViewById(R.id.buttonP1).setClickable(false);
                findViewById(R.id.buttonP2).setClickable(true);
            }
            findViewById(R.id.correctOrNotP1).setVisibility(View.INVISIBLE);
            findViewById(R.id.correctOrNotP2).setVisibility(View.INVISIBLE);
            gyuling = false;
            allowed = true;
            hapTrying = false;
            next = "turnChange";
            timer.setTurn(player1Turn);

            //Starts turn timer again
            timerSystem.postDelayed(this, duration);
            timePassed = 0;
            untilTime = (int) duration;
            try {
                cdt.cancel();
            } catch (Exception ex) {
            }
            cdt = new CountDownTimer(untilTime, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timePassed += 100;
                }

                @Override
                public void onFinish() {
                    timePassed = 0;
                }
            };
            cdt.start();
            turnSound.start();
        }
    };

    //The runnable that  handles changing the grid
    Runnable gridChange = new Runnable() {
        @Override
        public void run() {
            ansCount = 0;
            for (int i = 0; i < answers.getRowCount(); i++) {
                ((TextView) answers.getChildAt(i)).setText("");
                ((TextView) answers2.getChildAt(i)).setText("");
            }
            timerSystem.post(turnChange);
            changingGrid();
        }
    };

    //The runnable that handles when a player fails to do a hap
    Runnable hapGuessingGame = new Runnable() {
        public void run() {
            circling.removeCallbacks(circleStyle);
            TextView t = (player1Turn) ? ((TextView) findViewById(R.id.scoreP1)) : ((TextView) findViewById(R.id.scoreP2));
            int s = Integer.parseInt(t.getText().toString());
            s--;
            wrongSound.start();
            ImageView img = (player1Turn) ? (ImageView) findViewById(R.id.correctOrNotP1) : (ImageView) findViewById(R.id.correctOrNotP2);
            img.setImageResource(R.drawable.incorrecttransparentp1);
            img.setVisibility(View.VISIBLE);
            t.setText(String.valueOf(s));
            hapTry.clear();
            timerSystem.removeCallbacks(turnChange);
            allowed = false;
            next = "turnChange";
            timerSystem.postDelayed(turnChange, 1500);
            timePassed = 0;
            untilTime = 1500;
            cdt.cancel();
            cdt = new CountDownTimer(untilTime, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timePassed += 100;
                }

                @Override
                public void onFinish() {
                    timePassed = 0;
                }
            };
            cdt.start();
        }
    };

    private GridLayout hapGrid;

    //Method for when "Combo" is pressed, starts countdown for choosing cells
    public void clickIt(View v) {
        if (!hapTrying && !gyuling) {
            hapTrying = true;
            allowed = true;
            GridOrder choices = (GridOrder) findViewById(R.id.hapChoicesP1);
            choices.resetOrder(player1Turn);
            choices.setVisibility(View.VISIBLE);
            choices = (GridOrder) findViewById(R.id.hapChoicesP2);
            choices.resetOrder(player1Turn);
            choices.setVisibility(View.VISIBLE);
            timerSystem.removeCallbacks(turnChange);
            timer.setProgress(0);
            next = "hapGuessingGame";
            timerSystem.postDelayed(hapGuessingGame, duration);
            timePassed = 0;
            untilTime = (int) duration;
            cdt.cancel();
            cdt = new CountDownTimer(untilTime, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timePassed += 100;
                }

                @Override
                public void onFinish() {
                    timePassed = 0;
                }
            };
            cdt.start();
        }
    }

    //Is called when someone presses "Complete"
    public void gyulThatShit(View v) {
        if (!hapTrying && allowed && !gyuling) {
            circling.removeCallbacks(circleStyle);
            timerSystem.removeCallbacks(turnChange);
            gyuling = true;
            TextView t = (player1Turn) ? ((TextView) findViewById(R.id.scoreP1)) : ((TextView) findViewById(R.id.scoreP2));
            TextView gyulText1 = (TextView) findViewById(R.id.gyulGoodP1);
            TextView gyulText2 = (TextView) findViewById(R.id.gyulGoodP2);
            int s = Integer.parseInt(t.getText().toString());
            //Happens if there are no more combos to find
            if (g.gyul()) {
                s += 3;
                rightSound.start();
                gyulText1.setText("Complete!");
                gyulText2.setText("Complete!");
                ImageView img = (player1Turn) ? (ImageView) findViewById(R.id.correctOrNotP1) : (ImageView) findViewById(R.id.correctOrNotP2);
                img.setImageResource(R.drawable.correcttransparent);
                img.setVisibility(View.VISIBLE);
                //Changes the grid
                next = "gridChange";
                timerSystem.postDelayed(gridChange, 1500);
            } else {
                s -= 1;
                gyulText1.setText("Not Complete!");
                gyulText2.setText("Not Complete!");
                wrongSound.start();
                ImageView img = (player1Turn) ? (ImageView) findViewById(R.id.correctOrNotP1) : (ImageView) findViewById(R.id.correctOrNotP2);
                img.setImageResource(R.drawable.incorrecttransparentp1);
                img.setVisibility(View.VISIBLE);
                next = "turnChange";
                timerSystem.postDelayed(turnChange, 1500);
            }
            //Shows in the player's color if they are correct
            gyulText1.setTextColor(getResources().getColor((player1Turn) ? R.color.colorPrimary : R.color.colorAccent));
            gyulText2.setTextColor(getResources().getColor((player1Turn) ? R.color.colorPrimary : R.color.colorAccent));
            gyulText1.setVisibility(View.VISIBLE);
            gyulText2.setVisibility(View.VISIBLE);
            t.setText(String.valueOf(s));
            timePassed = 0;
            untilTime = 1500;
            cdt.cancel();
            cdt = new CountDownTimer(untilTime, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timePassed += 100;
                }

                @Override
                public void onFinish() {
                    timePassed = 0;
                }
            };
            cdt.start();
        }
    }

    //Changes the cells of the grid using the grid object
    public void changingGrid() {
        if (roundNumber >= endRound) {
            endGame();
            timerSystem.removeCallbacks(turnChange);
        } else {
            roundNumber++;
            ((TextView) findViewById(R.id.roundCounterP1)).setText("Round " + roundNumber + " of " + endRound);
            ((TextView) findViewById(R.id.roundCounterP2)).setText("Round " + roundNumber + " of " + endRound);
            g.changeAll();
            Cell[] cells = g.getCells();
            for (int i = 0; i < cells.length; i++) {
                ((ViewCell) hapGrid.getChildAt(i)).change(cells[6 - (6 * (i / 3)) + i]);
                hapGrid.getChildAt(i).setOnClickListener(cellTouch);
            }
        }
    }

    //Is called when the timer counts down to 0 and ends the game
    public void endGame() {
        int p1Score = Integer.parseInt(((TextView) findViewById(R.id.scoreP1)).getText().toString());
        int p2Score = Integer.parseInt(((TextView) findViewById(R.id.scoreP2)).getText().toString());
        AlertDialog.Builder winScreen = new AlertDialog.Builder(this);
        //AlertDialog content depends on who wins
        if (p1Score > p2Score) {
            winScreen.setMessage("Player 1 has won the game with a score of " + p1Score + " to " + p2Score + " after round " + roundNumber + ".")
                    .setPositiveButton("Play Again", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            music.stop();
                            matchStart();
                        }
                    })
                    .setNegativeButton("Go Back to Menu", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            music.stop();
                            finish();
                        }
                    });
        } else if (p2Score > p1Score) {
            winScreen.setMessage("Player 2 has won the game with a score of " + p2Score + " to " + p1Score + " after round " + roundNumber + ".")
                    .setPositiveButton("Play Again", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            music.stop();
                            matchStart();
                        }
                    })
                    .setNegativeButton("Go Back to Menu", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            music.stop();
                            finish();
                        }
                    });
        }
        //AlertDialog if there is a tie. Music change, and possibility of playing additionnal round
        else {
            if (roundNumber == endRound) {
                music.stop();
                music.reset();
                try {
                    AssetFileDescriptor overtimeafd = getAssets().openFd("overtimemusic.mp3");
                    music.setDataSource(overtimeafd.getFileDescriptor());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                music.start();
            }
            winScreen.setMessage("Both players have tied at " + p2Score + " points after round " + roundNumber + ".")
                    .setPositiveButton("Go into Overtime", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            roundNumber++;
                            ((TextView) findViewById(R.id.roundCounterP1)).setText("Round " + roundNumber + " of " + endRound);
                            ((TextView) findViewById(R.id.roundCounterP2)).setText("Round " + roundNumber + " of " + endRound);
                            g.changeAll();
                            Cell[] cells = g.getCells();
                            for (int i = 0; i < cells.length; i++) {
                                ((ViewCell) hapGrid.getChildAt(i)).change(cells[6 - (6 * (i / 3)) + i]);
                                hapGrid.getChildAt(i).setOnClickListener(cellTouch);
                            }
                            timerSystem.post(turnChange);
                        }
                    })
                    .setNeutralButton("Play Again", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            music.stop();
                            music.reset();
                            try {
                                AssetFileDescriptor multiafd = getAssets().openFd("multimusic.mp3");
                                music.setDataSource(multiafd.getFileDescriptor());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            matchStart();
                        }
                    })
                    .setNegativeButton("Go Back to Menu", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            music.stop();
                            finish();
                        }
                    });
        }
        winScreen.setCancelable(false);
        alertDialog = winScreen.create();
        alertDialog.show();
    }

    //Is called when a cell is touched
    private OnClickListener cellTouch = new OnClickListener() {
        @Override
        public void onClick(View v) {
            //Checks if the user is trying to do a combo
            if (hapTrying && allowed) {
                ViewCell vx = (ViewCell) v;
                //Adds the number to the combo
                if (!hapTry.contains(vx.getNum())) {
                    hapTry.add(vx.getNum());
                    GridOrder choices = (GridOrder) findViewById(R.id.hapChoicesP1);
                    ((UnderscoreView) choices.getChildAt(choices.getOrderAt())).setNum(vx.getNum());
                    choices = (GridOrder) findViewById(R.id.hapChoicesP2);
                    ((UnderscoreView) choices.getChildAt(choices.getOrderAt())).setNum(vx.getNum());
                    vx.setChosen(true);
                }
                //If this makes it the third cell of a combo, chekcs to see if it is correct
                if (hapTry.size() == 3) {
                    circling.removeCallbacks(circleStyle);
                    java.util.Collections.sort(hapTry);
                    String gottenHap = hapTry.get(0).toString() + hapTry.get(1).toString() + hapTry.get(2).toString();
                    TextView t = (player1Turn) ? ((TextView) findViewById(R.id.scoreP1)) : ((TextView) findViewById(R.id.scoreP2));
                    int s = Integer.parseInt(t.getText().toString());
                    if (g.hap(gottenHap)) {
                        s++;
                        rightSound.start();
                        ImageView img = (player1Turn) ? (ImageView) findViewById(R.id.correctOrNotP1) : (ImageView) findViewById(R.id.correctOrNotP2);
                        img.setImageResource(R.drawable.correcttransparent);
                        img.setVisibility(View.VISIBLE);
                        ((TextView) answers.getChildAt((answers.getRowCount() - ansCount - 1))).setText(gottenHap);
                        ((TextView) answers2.getChildAt((answers2.getRowCount() - ansCount - 1))).setText(gottenHap);
                        ansCount++;
                    } else {
                        s--;
                        wrongSound.start();
                        ImageView img = (player1Turn) ? (ImageView) findViewById(R.id.correctOrNotP1) : (ImageView) findViewById(R.id.correctOrNotP2);
                        img.setImageResource(R.drawable.incorrecttransparentp1);
                        img.setVisibility(View.VISIBLE);
                    }
                    t.setText(String.valueOf(s));
                    hapTry.clear();
                    timerSystem.removeCallbacks(hapGuessingGame);
                    allowed = false;
                    next = "turnChange";
                    timerSystem.postDelayed(turnChange, 1500);
                    timePassed = 0;
                    untilTime = 1500;
                    cdt.cancel();
                    cdt = new CountDownTimer(untilTime, 100) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            timePassed += 100;
                        }

                        @Override
                        public void onFinish() {
                            timePassed = 0;
                        }
                    };
                    cdt.start();
                }
            }
        }
    };

    //Called at the start of the game, when player presses "Ready" button. Sets the grid and starts the timer
    public void readyNow() {
        music.start();
        timerSystem.post(turnChange);
        g = new Grid();
        GridLayout grid = (GridLayout) findViewById(R.id.hapGrid);
        int specialI;
        Cell[] cells = g.getCells();
        for (int i = 0; i < cells.length; i++) {
            specialI = 6 - (3 * (i / 3)) + (i % 3);
            ((ViewCell) grid.getChildAt(i)).change(cells[specialI]);
            grid.getChildAt(i).setOnClickListener(cellTouch);
        }
    }

    //Sets the game when the activity is created. Asks the user to press Ready when they are
    public void matchStart() {
        roundNumber = 1;
        ((TextView) findViewById(R.id.roundCounterP1)).setText("Round 1 of " + endRound);
        ((TextView) findViewById(R.id.roundCounterP2)).setText("Round 1 of " + endRound);
        GridLayout grid = (GridLayout) findViewById(R.id.hapGrid);
        for (int i = 0; i < 9; i++) {
            ((ViewCell) grid.getChildAt(i)).change(new Cell(Cell.Shape.RECTANGLE, Cell.Color.BLUE, Cell.Background.BLACK));
            grid.getChildAt(i).setOnClickListener(cellTouch);
        }
        ((TextView) findViewById(R.id.scoreP1)).setText("0");
        ((TextView) findViewById(R.id.scoreP2)).setText("0");
        alertDialog = builder.create();
        alertDialog.show();
    }

    //Override of the onPause() method. Pauses the game and shows custom AlertDialog
    public void onPause() {
        super.onPause();
        music.pause();
        if (!alertDialog.isShowing()) {
            timerSystem.removeCallbacks(turnChange);
            timerSystem.removeCallbacks(hapGuessingGame);
            timerSystem.removeCallbacks(gridChange);
            try {
                cdt.cancel();
            } catch (Exception ex) {
            }
            circling.removeCallbacks(circleStyle);
            for (int i = 0; i < 9; i++) {
                ((ViewCell) hapGrid.getChildAt(i)).setHiding(true);
            }
            alertDialog = pause.create();
            alertDialog.show();
        }
    }

    //Similar to onPause(), for when the back button is pressed on the user's phone
    @Override
    public void onBackPressed() {
        if (this.hasWindowFocus() && !alertDialog.isShowing()) {
            music.pause();
            timerSystem.removeCallbacks(turnChange);
            timerSystem.removeCallbacks(hapGuessingGame);
            timerSystem.removeCallbacks(gridChange);
            cdt.cancel();
            circling.removeCallbacks(circleStyle);
            for (int i = 0; i < 9; i++) {
                ((ViewCell) hapGrid.getChildAt(i)).setHiding(true);
            }
            alertDialog = pause.create();
            alertDialog.show();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getActionBar();
        try {
            actionBar.hide();
        } catch (NullPointerException e) {
        }
        //The AlertDialog used when paused
        pause = new AlertDialog.Builder(this);
        pause.setMessage("Pause menu")
                .setPositiveButton("Resume", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (next == "turnChange") {
                            timerSystem.postDelayed(turnChange, untilTime - timePassed);
                        } else if (next == "hapGuessingGame") {
                            timerSystem.postDelayed(hapGuessingGame, untilTime - timePassed);
                        } else if (next == "gridChange") {
                            timerSystem.postDelayed(gridChange, untilTime - timePassed);
                        }
                        for (int i = 0; i < 9; i++) {
                            ((ViewCell) hapGrid.getChildAt(i)).setHiding(false);
                        }
                        circling.post(circleStyle);
                        cdt = new CountDownTimer(untilTime - timePassed, 100) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                timePassed += 100;
                            }

                            @Override
                            public void onFinish() {
                                timePassed = 0;
                            }
                        };
                        music.start();
                        cdt.start();
                    }
                })
                .setNegativeButton("Go Back to Menu", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        music.stop();
                        finish();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        if (next == "turnChange") {
                            timerSystem.postDelayed(turnChange, untilTime - timePassed);
                        } else if (next == "hapGuessingGame") {
                            timerSystem.postDelayed(hapGuessingGame, untilTime - timePassed);
                        } else if (next == "gridChange") {
                            timerSystem.postDelayed(gridChange, untilTime - timePassed);
                        }
                        for (int i = 0; i < 9; i++) {
                            ((ViewCell) hapGrid.getChildAt(i)).setHiding(false);
                        }
                        circling.postDelayed(circleStyle, 50);
                        cdt = new CountDownTimer(untilTime - timePassed, 100) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                timePassed += 100;
                            }

                            @Override
                            public void onFinish() {
                                timePassed = 0;
                            }
                        };
                        music.start();
                        cdt.start();
                    }
                });
        //The AlertDialog used when matchStart() is called
        builder = new AlertDialog.Builder(this);
        builder.setMessage("Press when ready")
                .setPositiveButton("Ready", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        readyNow();
                    }
                })
                .setNegativeButton("Go Back to Menu", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                });
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        SharedPreferences sharedPreferences = getSharedPreferences("gyulhap_settings", Context.MODE_PRIVATE);
        if (!sharedPreferences.contains("round_last")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("round_last", 3);
            editor.commit();
        }
        ansCount = 0;

        endRound = sharedPreferences.getInt("round_last", 3);
        hapGrid = (GridLayout) findViewById(R.id.hapGrid);
        timer = ((TimeCircle) findViewById(R.id.timer));
        timerSystem = new Handler();
        circling = new Handler();
        answers = (GridLayout) findViewById(R.id.answers);
        answers2 = (GridLayout) findViewById(R.id.answers2);
        //Sets up the answer GridLayouts in order to show them from the bottom
        for (int i = 0; i < answers.getRowCount(); i++) {
            answers.addView(new TextView(MainActivity.this));
            answers2.addView(new TextView(MainActivity.this));
            answers.getChildAt(i).setMinimumHeight(20);
            answers2.getChildAt(i).setMinimumHeight(20);
        }
        turnSound = MediaPlayer.create(this, R.raw.turnswitch);
        wrongSound = MediaPlayer.create(this, R.raw.bad);
        rightSound = MediaPlayer.create(this, R.raw.good);
        music = MediaPlayer.create(this, R.raw.multimusic);
        music.setLooping(true);
        music.setVolume(40, 40);
        matchStart();
    }

}
