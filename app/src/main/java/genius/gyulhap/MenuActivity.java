//This class contains the methods for the Menu Activity

package genius.gyulhap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {

    //Starts up 2-player mode
    public void twoPlayerMode(View v){
        Intent intent = new Intent(this, MainActivity.class);
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
        time.setText(String.valueOf(min) + ":" + String.valueOf(sec30) + "0");
        SharedPreferences sharedPreferences = getSharedPreferences("gyulhap_settings", Context.MODE_PRIVATE);
        //The record for the time shown is also shown
        ((TextView)findViewById(R.id.record)).setText(String.valueOf(sharedPreferences.getInt("record_score_"+time.getText(),0)));
    }

    //Quits the application
    public void quitButton(View v){
        finish();
        System.exit(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        SharedPreferences sharedPreferences = getSharedPreferences("gyulhap_settings", Context.MODE_PRIVATE);
        ((TextView)findViewById(R.id.roundNum)).setText(String.valueOf(sharedPreferences.getInt("round_last",3)));
        ((TextView)findViewById(R.id.time)).setText(sharedPreferences.getString("single_time","1:00"));
        ((TextView)findViewById(R.id.record)).setText(String.valueOf(sharedPreferences.getInt("record_score_"+((TextView)findViewById(R.id.time)).getText(),0)));
    }
}
