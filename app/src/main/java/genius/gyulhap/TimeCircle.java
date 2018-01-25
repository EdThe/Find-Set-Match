//This is the View class of the arc that shows the players how much time passed/has yet to pass

package genius.gyulhap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


public class TimeCircle extends View {
    double progress = 0.0;
    int startPos = 270;

    public TimeCircle(Context context) {
        super(context);
    }

    public TimeCircle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    //Draws the arc of the circle, from the angle required to where it has progressed to
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawArc(10,10,120,120,startPos,(int)(360*progress),true,new Paint(Color.CYAN));
        }
    }

    //Sets how far the circle should have progressed to
    public void setProgress(double x){
        progress = x;
        invalidate();
        requestLayout();
    }

    //Knows how far the circle has progressed
    public double getProgress(){
        return progress;
    }

    //Sets which player's turn it is, to know how the circle should be rotated
    public void setTurn(boolean p1Turn){
        if(p1Turn){
            startPos = 270;
        }
        else {
            startPos = 90;
        }
    }
}
