//This is a special GridLayout class that is used to show chosen cells, and know how many of its views were changed

package genius.gyulhap;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.GridLayout;

public class GridOrder extends GridLayout {
    private int number;
    public GridOrder(Context context, AttributeSet attrs) {
        super(context, attrs);
        number = 0;
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public int getOrderAt(){
        return number++;
    }

    public void resetOrder(boolean p1) {
        number = 0;
        for (int i = 0; i <= 2; i++) {
            UnderscoreView underscoreView = ((UnderscoreView) this.getChildAt(i));
            underscoreView.setNum(0);
            underscoreView.setP1Turn(p1);
        }
    }
}
