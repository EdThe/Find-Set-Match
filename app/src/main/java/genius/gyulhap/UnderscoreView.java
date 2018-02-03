//This view class is used to show the underscored numbers of chosen cells in-game

package genius.gyulhap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


public class UnderscoreView extends View {
    public boolean p1;
    public int num;

    public UnderscoreView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public UnderscoreView(Context context) {
        super(context);
    }

    public UnderscoreView(UnderscoreView uView){
        super(uView.getContext());
    }


    public void onDraw(Canvas canvas){
        super.onDraw(canvas);

        //This is the underscore
        Rect under = new Rect(15,this.getHeight() - 8,this.getWidth() - 15,this.getHeight());
        Paint p = new Paint(Color.BLACK);
        if(p1)
            p.setColor(getResources().getColor(R.color.colorPrimary));
        else
            p.setColor(getResources().getColor(R.color.colorAccent));
        canvas.drawRect(under, p);

        if(num != 0){
            //This draws the number that is chosen. Since 0 can't be chosen, it is used to specify when not to show the number
            Paint text = new Paint(Color.BLACK);
            if(p1)
                text.setColor(getResources().getColor(R.color.colorPrimary));
            else
                text.setColor(getResources().getColor(R.color.colorAccent));
            text.setTextSize(55);
            canvas.drawText(String.valueOf(num),this.getWidth()/2-14,this.getHeight() - 16,text);
        }
    }


    //Sets the number the text will show
    public void setNum(int num){
        this.num = num;
        invalidate();
        requestLayout();
    }


    //Sets the player the view will show, that specifies which color is used
    public void setP1Turn(boolean p1){
        this.p1 = p1;
        invalidate();
        requestLayout();
    }
}
