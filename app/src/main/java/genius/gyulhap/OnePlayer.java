//This class contains the methods for the 1-player mode Activity

package genius.gyulhap;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnClickListener;

import java.util.ArrayList;

public class OnePlayer extends AppCompatActivity {

    Handler timeLeft;
    TextView left;

    //The runnable that keeps the time and counts it down
    Runnable timeTask = new Runnable() {
        @Override
        public void run() {
            String actual = ((TextView) findViewById(R.id.timeLeft)).getText().toString();
            int sec = Integer.parseInt(actual.split(":")[1]);
            int min = Integer.parseInt(String.valueOf(actual.charAt(0)));
            sec--;
            if (sec == 0 && min == 0) {
                left.setText(min + ":0" + sec);
                endGame();
            } else if (sec < 0) {
                sec = 59;
                min--;
                left.setText(min + ":" + sec);
                timeLeft.postDelayed(timeTask,1000);
            } else if (sec < 10) {
                left.setText(min + ":0" + sec);
                timeLeft.postDelayed(timeTask,1000);
            } else {
                left.setText(min + ":" + sec);
                timeLeft.postDelayed(timeTask, 1000);
            }
        }
    };

    String next = null;
    int timePassed = 0;
    int untilTime = 0;
    CountDownTimer cdt = null;
    int roundNumber = 1;
    Thread systemCheck;
    android.app.AlertDialog.Builder builder;
    android.app.AlertDialog.Builder pause;
    ArrayList<Integer> hapTry = new ArrayList<>();
    Grid g;
    boolean hapTrying = false;
    GridLayout answers;
    Handler timerSystem;
    TimeCircle timer;
    boolean allowed = true;
    boolean gyuling = false;
    MediaPlayer wrongSound;
    MediaPlayer rightSound;
    MediaPlayer music;
    AlertDialog alertDialog;
    Handler circling;
    long duration = 5000;

    //The runnable that changes the progress circle of the timer
    Runnable circleStyle = new Runnable() {
        @Override
        public void run() {
            timer.setProgress(timer.getProgress()+0.02);
            circling.postDelayed(circleStyle,100);
        }
    };

    //The runnable that activates when a hap is finished
    Runnable turnChange = new Runnable(){
        public void run(){
            (findViewById(R.id.hapChoices)).setVisibility(View.INVISIBLE);
            findViewById(R.id.gyulGood).setVisibility(View.INVISIBLE);
            circling.removeCallbacks(circleStyle);
            timer=(TimeCircle)findViewById(R.id.timer);
            timer.setProgress(0);
            GridLayout grid = (GridLayout)findViewById(R.id.hapGrid);
            for(int i = 0;i<9;i++){
                if(((ViewCell)grid.getChildAt(i)).getChosen())
                    ((ViewCell)grid.getChildAt(i)).setChosen(false);
            }
            findViewById(R.id.correctOrNot).setVisibility(View.INVISIBLE);
            gyuling = false;
            allowed = true;
            hapTrying = false;
            next = "none";
        }
    };

    //The runnable that  handles changing the grid
    Runnable gridChange = new Runnable() {
        @Override
        public void run() {
            answers.removeAllViews();
            timerSystem.post(turnChange);
            changingGrid();
        }
    };

    //The runnable that handles when a player fails to do a hap
    Runnable hapGuessingGame = new Runnable(){
        public void run(){
            circling.removeCallbacks(circleStyle);
            TextView t = ((TextView) findViewById(R.id.score));
            int s = Integer.parseInt(t.getText().toString().split(" ")[1]);
            s--;
            wrongSound.start();
            ImageView img = (ImageView) findViewById(R.id.correctOrNot);
            img.setImageResource(R.drawable.incorrecttransparentp1);
            img.setVisibility(View.VISIBLE);
            t.setText("Score: "+String.valueOf(s));
            hapTry.clear();
            timerSystem.removeCallbacks(turnChange);
            allowed = false;
            next = "turnChange";
            timerSystem.postDelayed(turnChange, 1000);
            timePassed = 0;
            untilTime = 1500;
            cdt.cancel();
            cdt = new CountDownTimer(untilTime, 100){
                @Override
                public void onTick(long millisUntilFinished) {
                    timePassed+=100;
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
    public void clickIt(View v){
        if(!hapTrying && !gyuling) {
            hapTrying = true;
            allowed = true;
            GridOrder choices = (GridOrder)findViewById(R.id.hapChoices);
            choices.resetOrder(false);
            choices.setVisibility(View.VISIBLE);
            timerSystem.removeCallbacks(turnChange);
            timer.setProgress(0);
            next = "hapGuessingGame";
            timerSystem.postDelayed(hapGuessingGame, duration);
            timePassed = 0;
            circling.post(circleStyle);
            untilTime = (int)duration;
            cdt.cancel();
            cdt = new CountDownTimer(untilTime, 100){
                @Override
                public void onTick(long millisUntilFinished) {
                    timePassed+=100;
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
            TextView t = ((TextView) findViewById(R.id.score));
            TextView gyulText = (TextView)findViewById(R.id.gyulGood);
            int s = Integer.parseInt(t.getText().toString().split(" ")[1]);
            //Happens if there is no more combos to find
            if (g.gyul()) {
                s += 3;
                rightSound.start();
                gyulText.setText("Complete!");
                ImageView img = (ImageView) findViewById(R.id.correctOrNot);
                img.setImageResource(R.drawable.correcttransparent);
                img.setVisibility(View.VISIBLE);
                //Changes the grid
                next = "gridChange";
                timerSystem.postDelayed(gridChange, 1000);
            } else {
                s -= 1;
                wrongSound.start();
                gyulText.setText("Not Complete!");
                ImageView img = (ImageView) findViewById(R.id.correctOrNot);
                img.setImageResource(R.drawable.incorrecttransparentp1);
                img.setVisibility(View.VISIBLE);
                next = "turnChange";
                timerSystem.postDelayed(turnChange, 1000);
            }
            gyulText.setVisibility(View.VISIBLE);
            t.setText("Score: "+String.valueOf(s));
            timePassed = 0;
            untilTime = 1500;
            cdt.cancel();
            cdt = new CountDownTimer(untilTime, 100){
                @Override
                public void onTick(long millisUntilFinished) {
                    timePassed+=100;
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
    public void changingGrid(){
            roundNumber++;
            g.changeAll();
            Cell[] cells = g.getCells();
            for (int i = 0; i < cells.length; i++) {
                ((ViewCell) hapGrid.getChildAt(i)).change(cells[6 - (6 * (i / 3)) + i]);
                hapGrid.getChildAt(i).setOnClickListener(cellTouch);
        }
    }

    //Is called when the timer counts down to 0 and ends the game
    public void endGame(){
        timerSystem.removeCallbacks(turnChange);
        timerSystem.removeCallbacks(hapGuessingGame);
        timerSystem.removeCallbacks(gridChange);
        circling.removeCallbacks(circleStyle);
        //Finds the old record and changes it if necessary
        SharedPreferences sharedPreferences = getSharedPreferences("gyulhap_settings",Context.MODE_PRIVATE);
        String record = ((TextView)findViewById(R.id.record)).getText().toString().split(" ")[1];
        String endScore = ((TextView)findViewById(R.id.score)).getText().toString().split(" ")[1];
        String endText = "You have gotten a score of " + endScore + " points!";
        if(Integer.valueOf(endScore) > Integer.valueOf(record)){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("record_score_"+sharedPreferences.getString("single_time","1:00"), Integer.valueOf(endScore));
            editor.commit();
            endText += " You have beaten your record of " + record + " for " +sharedPreferences.getString("single_time","1:00")+ "!";
        }
        //AlretDialog that lets the player play again
        android.app.AlertDialog.Builder winScreen = new android.app.AlertDialog.Builder(this);
        winScreen.setMessage(endText)
                .setPositiveButton("Play Again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        matchStart();
                    }
                })
                .setNegativeButton("Go Back to Menu", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        music.stop();
                        finish();
                    }
                });
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
                    GridOrder choices = (GridOrder) findViewById(R.id.hapChoices);
                    ((UnderscoreView)choices.getChildAt(choices.getOrderAt())).setNum(vx.getNum());
                    vx.setChosen(true);
                }
                //If there are 3 cells in the combo, checks to see if it is an hap
                if (hapTry.size() == 3) {
                    circling.removeCallbacks(circleStyle);
                    java.util.Collections.sort(hapTry);
                    String gottenHap = hapTry.get(0).toString() + hapTry.get(1).toString() + hapTry.get(2).toString();
                    TextView t = (TextView) findViewById(R.id.score);
                    int s = Integer.parseInt(t.getText().toString().split(" ")[1]);
                    if (g.hap(gottenHap)) {
                        s++;
                        rightSound.start();
                        ImageView img = (ImageView) findViewById(R.id.correctOrNot);
                        img.setImageResource(R.drawable.correcttransparent);
                        img.setVisibility(View.VISIBLE);
                        TextView nA = new TextView(OnePlayer.this);
                        if(answers.getChildAt(0) == null)
                            nA.setText(gottenHap);
                        else {
                            if (answers.getChildAt(5) != null && answers.getChildAt(6) == null)
                                nA.setText(gottenHap);
                            else
                                nA.setText(" - " + gottenHap);
                        }
                        answers.addView(nA);
                    }
                    else {
                        s--;
                        wrongSound.start();
                        ImageView img = (ImageView) findViewById(R.id.correctOrNot);
                        img.setImageResource(R.drawable.incorrecttransparentp1);
                        img.setVisibility(View.VISIBLE);
                    }

                    //Score is changed accordingly
                    t.setText("Score: "+String.valueOf(s));
                    hapTry.clear();
                    timerSystem.removeCallbacks(hapGuessingGame);
                    allowed = false;
                    next = "turnChange";
                    timerSystem.postDelayed(turnChange, 1500);
                    timePassed = 0;
                    untilTime = 1500;
                    cdt.cancel();
                    cdt = new CountDownTimer(untilTime, 100){
                        @Override
                        public void onTick(long millisUntilFinished) {
                            timePassed+=100;
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
    public void readyNow(){
        timerSystem.post(turnChange);
        g = new Grid();
        GridLayout grid = (GridLayout)findViewById(R.id.hapGrid);
        int specialI;
        Cell[] cells = g.getCells();
        for(int i = 0;i<cells.length;i++){
            specialI = 6-(3*(i/3))+(i%3);
            ((ViewCell)grid.getChildAt(i)).change(cells[specialI]);
            grid.getChildAt(i).setOnClickListener(cellTouch);
        }
        timeLeft.postDelayed(timeTask,1000);
        music.start();
    }

    //Override of the onPause() method. Pauses the game and shows custom AlertDialog
    public void onPause(){
        super.onPause();
        music.pause();
        if(!alertDialog.isShowing()) {
            timerSystem.removeCallbacks(turnChange);
            timerSystem.removeCallbacks(hapGuessingGame);
            timerSystem.removeCallbacks(gridChange);
            try{cdt.cancel();}catch(Exception ex){}
            timeLeft.removeCallbacks(timeTask);
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
    public void onBackPressed(){
        if(this.hasWindowFocus() && !alertDialog.isShowing()){
            music.pause();
            timerSystem.removeCallbacks(turnChange);
            timerSystem.removeCallbacks(hapGuessingGame);
            timerSystem.removeCallbacks(gridChange);
            cdt.cancel();
            timeLeft.removeCallbacks(timeTask);
            circling.removeCallbacks(circleStyle);
            for(int i = 0; i<9 ; i++){
                ((ViewCell)hapGrid.getChildAt(i)).setHiding(true);
            }
        alertDialog = pause.create();
            alertDialog.show();
        }

    }

    //Sets the game when the activity is created. Asks the user to press Ready when they are
    public void matchStart(){
        SharedPreferences sharedPreferences = getSharedPreferences("gyulhap_settings",Context.MODE_PRIVATE);
        ((TextView)findViewById(R.id.timeLeft)).setText(sharedPreferences.getString("single_time", "1:00"));
        String recordTimeFromSettings = "record_score_"+sharedPreferences.getString("single_time", "1:00");
        if(!sharedPreferences.contains(recordTimeFromSettings)){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(recordTimeFromSettings, 0);
            editor.commit();
        }
        ((TextView)findViewById(R.id.record)).setText("Record: " + String.valueOf(sharedPreferences.getInt(recordTimeFromSettings, 0)));
        ((TextView) findViewById(R.id.score)).setText("Score: 0");
        GridLayout grid = (GridLayout)findViewById(R.id.hapGrid);
        for(int i = 0;i<9;i++){
            ((ViewCell)grid.getChildAt(i)).change(new Cell(Cell.Shape.RECTANGLE,Cell.Color.BLUE,Cell.Background.BLACK));
            grid.getChildAt(i).setOnClickListener(cellTouch);
        }
        answers.removeAllViews();
        left.setText(sharedPreferences.getString("single_time", "1:00"));
        roundNumber = 1;
        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_player);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getActionBar();
        try{actionBar.hide();}catch(NullPointerException e){}
        //The AlertDialog used when paused
        pause = new android.app.AlertDialog.Builder(this);
        pause.setMessage("Pause menu")
                .setPositiveButton("Resume", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(next == "none"){
                        }
                        else if(next == "turnChange"){
                            circling.post(circleStyle);
                            timerSystem.postDelayed(turnChange,untilTime - timePassed);
                        }
                        else if(next == "hapGuessingGame"){
                            circling.post(circleStyle);
                            timerSystem.postDelayed(hapGuessingGame,untilTime - timePassed);
                        }
                        else if(next == "gridChange"){
                            circling.post(circleStyle);
                            timerSystem.postDelayed(gridChange,untilTime - timePassed);
                        }
                        for(int i = 0; i<9 ; i++){
                            ((ViewCell)hapGrid.getChildAt(i)).setHiding(false);
                        }
                        cdt = new CountDownTimer(untilTime - timePassed, 100){
                            @Override
                            public void onTick(long millisUntilFinished) {
                                timePassed+=100;
                            }

                            @Override
                            public void onFinish() {
                                timePassed = 0;
                            }
                        };
                        music.start();
                        cdt.start();
                        timeLeft.postDelayed(timeTask,1000);
                    }
                })
                .setNegativeButton("Go Back to Menu", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        music.stop();
                        finish();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog){
                        if(next == "none"){
                        }
                        else if(next == "turnChange"){
                            timerSystem.postDelayed(turnChange,untilTime - timePassed);
                        }
                        else if(next == "hapGuessingGame"){
                            timerSystem.postDelayed(hapGuessingGame,untilTime - timePassed);
                        }
                        else if(next == "gridChange"){
                            timerSystem.postDelayed(gridChange,untilTime - timePassed);
                        }
                        for(int i = 0; i<9 ; i++){
                            ((ViewCell)hapGrid.getChildAt(i)).setHiding(false);
                        }
                        circling.postDelayed(circleStyle,50);
                        cdt = new CountDownTimer(untilTime - timePassed, 100){
                            @Override
                            public void onTick(long millisUntilFinished) {
                                timePassed+=100;
                            }

                            @Override
                            public void onFinish() {
                                timePassed = 0;
                            }
                        };
                        music.start();
                        cdt.start();
                        timeLeft.postDelayed(timeTask,1000);
                    }
                });
        //AlertDialog used in matchStart() that asks the user when they are ready
        builder = new android.app.AlertDialog.Builder(this);
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
                });
        builder.setCancelable(false);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Setting up the game's timer
        cdt = new CountDownTimer(untilTime - timePassed, 100){
            @Override
            public void onTick(long millisUntilFinished) {
                timePassed+=100;
            }

            @Override
            public void onFinish() {
                timePassed = 0;
            }
        };
        systemCheck = new Thread();
        timeLeft = new Handler();
        hapGrid = (GridLayout)findViewById(R.id.hapGrid);
        //Sets the cells to 1-player mode
        for(int i = 0; i<9 ; i++){
            ((ViewCell)hapGrid.getChildAt(i)).setMode(false);
        }
        left = (TextView) findViewById(R.id.timeLeft);
        timer = ((TimeCircle)findViewById(R.id.timer));
        timerSystem = new Handler();
        circling = new Handler();
        answers = (GridLayout)findViewById(R.id.answers);
        wrongSound = MediaPlayer.create(this, R.raw.bad);
        rightSound = MediaPlayer.create(this, R.raw.good);
        music = MediaPlayer.create(this, R.raw.singlemusic);
        music.setLooping(true);
        music.setVolume(40, 40);
        matchStart();
    }

}
