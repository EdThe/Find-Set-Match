//This class contains the methods for the Menu Activity

package genius.gyulhap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {

    ViewCell comboP1;
    ViewCell comboP2;
    ViewCell comboP3;
    Cell p1 = new Cell();
    Cell p2 = new Cell();
    Cell p3 = new Cell();
    Pair<ViewCell, Cell>[] comboPieces;
    int comboAt = 0;
    ImageView comboAnswer;
    MediaPlayer wrongSound;
    MediaPlayer rightSound;

    Animation shake_anim;
    Handler casinoSystem;
    Runnable casinoing = new Runnable() {
        @Override
        public void run() {
            //Same as if statement, just wanted to try
            /*switch(comboAt){
                case 2:
                case 1:
                case 0:
                    comboPieces[comboAt].second.randomize();
                    comboPieces[comboAt].first.change(comboPieces[comboAt].second);
                    animateCell(comboPieces[comboAt].first);
                    break;
                case 3:
                    if(p1.isHap(p2,p3)){
                        comboAnswer.setImageResource(R.drawable.correcttransparent);
                        rightSound.start();
                    }
                    else{
                        comboAnswer.setImageResource(R.drawable.incorrecttransparentp1);
                        wrongSound.start();
                    }
                    comboAnswer.setAlpha(1f);
                    casinoSystem.postDelayed(casinoing,1500);
                    break;
                case 4:
                    animateCell(comboAnswer);
                case 5:
                case 6:
                    animateCell(comboPieces[comboAt-4].first);
                    casinoSystem.postDelayed(casinoing,500);
                    break;
            }*/
            if(comboAt < 3){
                if(comboAt == 2 && (int)(Math.random()*2) == 0)
                    comboPieces[0].second.remainingHap(comboPieces[1].second, comboPieces[2].second);
                else
                    comboPieces[comboAt].second.randomize();
                comboPieces[comboAt].first.change(comboPieces[comboAt].second);
                animateCell(comboPieces[comboAt].first);
                casinoSystem.postDelayed(casinoing,500);
            }
            else if(comboAt == 3){
                if(p1.isHap(p2,p3)){
                    comboAnswer.setImageResource(R.drawable.correcttransparent);
                    rightSound.start();
                }
                else{
                    comboAnswer.setImageResource(R.drawable.incorrecttransparentp1);
                    wrongSound.start();
                }
                comboAnswer.setAlpha(1f);
                comboAnswer.startAnimation(shake_anim);
                casinoSystem.postDelayed(casinoing,1500);
            }
            else if(comboAt == 4){
                animateCell(comboAnswer);
                animateCell(comboPieces[comboAt-4].first);
                casinoSystem.postDelayed(casinoing,500);
            }
            else{
                animateCell(comboPieces[comboAt-4].first);
                casinoSystem.postDelayed(casinoing,500);
            }
            comboAt = ++comboAt%7;
        }
    };

    //Starts up 2-player mode
    public void twoPlayerMode(View v){
        Intent intent = new Intent(this, TwoPlayers.class);
        startActivity(intent);
    }

    //Takes the time chosen and starts up 1-player mode
    public void onePlayerMode(View v){
        SharedPreferences sharedPreferences = getSharedPreferences("gyulhap_settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("single_time", ((TextView)findViewById(R.id.time)).getText().toString());
        editor.commit();
        Intent intent = new Intent(this, OnePlayer.class);
        startActivity(intent);
    }

    //Opens the help activity
    public void help(View v){
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    //Changes the amount of rounds for 2-player, then keeps it in memory
    public void changeRound(View v){
        TextView round = (TextView)findViewById(R.id.roundNum);
        int roundNum = Integer.valueOf(round.getText().toString());
        //The upper limits of round is 10, the lower limit is 1
        if(v.getId() == R.id.upRound){
            if(roundNum != 10)
                roundNum++;
        }
        else{
            if(roundNum != 1) {
                roundNum--;
            }
        }
        round.setText(String.valueOf(roundNum));

        //The change is recorded for use in the 2-player activity
        SharedPreferences sharedPreferences = getSharedPreferences("gyulhap_settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("round_last", Integer.valueOf(String.valueOf(roundNum)));
        editor.commit();
    }

    //Changes the time allowed in 1-player and shows the record
    public void changeTime(View v){
        TextView time = (TextView)findViewById(R.id.time);
        int min = Integer.valueOf(time.getText().toString().substring(0,1));
        int sec30 = Integer.valueOf(time.getText().toString().substring(2,3));

        //The upper limit of time is 5:00, the lower limit is 0:30
        if(v.getId() == R.id.upTime){
            if(min != 5)
                if(sec30 == 0)
                    sec30 = 3;
                else {
                    min++;
                    sec30 = 0;
                }
        }
        else{
            if(min != 0) {
                if(sec30 == 3)
                    sec30 = 0;
                else {
                    sec30 = 3;
                    min--;
                }
            }
        }

        //The new time and the record associated to it is shown
        time.setText(String.valueOf(min) + ":" + String.valueOf(sec30) + "0");
        SharedPreferences sharedPreferences = getSharedPreferences("gyulhap_settings", Context.MODE_PRIVATE);
        ((TextView)findViewById(R.id.record)).setText(String.valueOf(sharedPreferences.getInt("record_score_"+time.getText(),0)));
    }


    //Quits the application
    public void quitButton(View v){
        finish();
        System.exit(0);
    }


    private void animateCell(View animated){
        animated.animate()
                .alpha(1f-animated.getAlpha())
                .setDuration(1000)
                .setListener(null);
    }

    @Override
    protected void onPause() {
        super.onPause();

        casinoSystem.removeCallbacks(casinoing);
    }

    @Override
    protected void onResume() {
        super.onResume();

        casinoSystem.postDelayed(casinoing, 500);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        shake_anim = AnimationUtils.loadAnimation(this, R.anim.anim_shake);
        //Gets the most recent amount of rounds and time chosen and displays them
        SharedPreferences sharedPreferences = getSharedPreferences("gyulhap_settings", Context.MODE_PRIVATE);
        ((TextView)findViewById(R.id.roundNum)).setText(String.valueOf(sharedPreferences.getInt("round_last",3)));
        ((TextView)findViewById(R.id.time)).setText(sharedPreferences.getString("single_time","1:00"));
        ((TextView)findViewById(R.id.record)).setText(String.valueOf(sharedPreferences.getInt("record_score_"+((TextView)findViewById(R.id.time)).getText(),0)));
        comboP1 = (ViewCell)findViewById(R.id.comboPart1);
        comboP2 = (ViewCell)findViewById(R.id.comboPart2);
        comboP3 = (ViewCell)findViewById(R.id.comboPart3);
        comboAnswer = (ImageView)findViewById(R.id.comboResult);

        comboPieces = new Pair[3];
        comboPieces[0] = new Pair<>(comboP1,p1);
        comboPieces[1] = new Pair<>(comboP2,p2);
        comboPieces[2] = new Pair<>(comboP3,p3);

        wrongSound = MediaPlayer.create(this, R.raw.bad);
        rightSound = MediaPlayer.create(this, R.raw.good);

        casinoSystem = new Handler();
    }
}
