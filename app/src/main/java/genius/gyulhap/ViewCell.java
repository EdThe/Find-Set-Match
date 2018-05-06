//This view class contains the methods that the Cells use

package genius.gyulhap;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;



public class ViewCell extends View {
    private String shape = "rectangle";
    private Paint backgroundColor = new Paint();
    private Paint shapeColor = new Paint();
    //Chosen paint is the paint that surrounds the cell when it is chosen during the game
    private Paint chosenPaint = new Paint();
    private Paint strokePaint = new Paint();
    private int num;
    private boolean hide;
    private boolean chosen = false;
    private boolean p2mode = true;
    private Cell cellVersion;

    public ViewCell(Context context) {
        super(context);
        backgroundColor.setColor(Color.BLACK);
        shapeColor.setColor(Color.BLUE);
        strokePaint.setColor(Color.BLACK);
        strokePaint.setStrokeWidth(5);
        strokePaint.setStyle(Style.STROKE);
        chosenPaint.setColor(Color.rgb(218,165,32));
        chosenPaint.setStyle(Style.STROKE);
        chosenPaint.setStrokeWidth(20);
        cellVersion = new Cell(Cell.Shape.RECTANGLE, Cell.Color.BLUE, Cell.Background.BLACK);
    }

    //This constructor sets the cells number
    public ViewCell(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        backgroundColor.setColor(Color.BLACK);
        shapeColor.setColor(Color.BLUE);
        chosenPaint.setColor(Color.rgb(218,165,32));
        chosenPaint.setStyle(Style.STROKE);
        chosenPaint.setStrokeWidth(20);
        strokePaint.setColor(Color.BLACK);
        strokePaint.setStrokeWidth(5);
        strokePaint.setStyle(Style.STROKE);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ViewCell,
                0, 0);

        try {
            num = a.getInteger(R.styleable.ViewCell_num, 0);
        } finally {
            a.recycle();
        }

    }

    //Tells the object if it is 1 or 2-player, which is needed when drawn
    public void setMode(boolean mode){
        p2mode = mode;
    }

    //Is called when the cell is picked in-game; redraws the cell
    public void setChosen(boolean chosen){
        this.chosen = chosen;
        invalidate();
        requestLayout();
    }

    public boolean getChosen(){
        return this.chosen;
    }

    public int getNum(){
        return num;
    }

    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        //This is the background rectangle
        Rect back = new Rect(15,15,this.getWidth()-15,this.getHeight()-15);

        //Changes the cell to hide the content (when game is paused)
        if(hide){
            Paint c = new Paint();
            c.setColor(Color.GREEN);
            canvas.drawRect(back, c);
            Paint text = new Paint(Color.BLACK);
            text.setTextSize(160);
            canvas.drawText("?", this.getWidth()/2-40,this.getHeight()/2+30, text);
        }
        else {
            Rect cellNum = new Rect(5, 5, 35, 35);
            Paint c = new Paint();
            c.setColor(Color.LTGRAY);
            //Background is drawn
            canvas.drawRect(back, backgroundColor);
            if(chosen) {
                canvas.drawRect(back, chosenPaint);
            }
            else{
                canvas.drawRect(back, strokePaint);
            }

            if(num!=0) {
                //Draws the number of the cell on the top left
                canvas.drawRect(cellNum, c);
                Paint text = new Paint(Color.BLACK);
                text.setTextSize(30);
                canvas.drawText(String.valueOf(num), 10, 32, text);
                cellNum = new Rect(this.getWidth() - 35, this.getHeight() - 35, this.getWidth() - 5, this.getHeight() - 5);
                if(p2mode){
                    //Draws the number of the cell upside-down on the bottom right
                    canvas.drawRect(cellNum, c);
                    canvas.rotate(180, this.getWidth() / 2, this.getHeight() / 2);
                    canvas.drawText(String.valueOf(num), 10, 32, text);
                    canvas.rotate(180, this.getWidth() / 2, this.getHeight() / 2);
                }
            }

            //The specific shape is drawn
            if (shape.equals("triangle")) {
                Path p = new Path();
                p.moveTo(40, this.getHeight() / 2);
                p.lineTo(this.getWidth() - 40, 40);
                p.lineTo(this.getWidth() - 40, this.getHeight() - 40);
                p.lineTo(40, this.getHeight() / 2);
                canvas.drawPath(p, shapeColor);
            }
            if (shape.equals("circle")) {
                canvas.drawCircle(this.getWidth() / 2, this.getHeight() / 2, this.getWidth() / 2 - 40, shapeColor);
            }
            if (shape.equals("rectangle")) {
                Rect rec = new Rect(40, 40, this.getWidth() - 40, this.getHeight() - 40);
                canvas.drawRect(rec, shapeColor);
            }
        }
    }

    //Changes the variables of the cell, used when grid is changed; redraws the cell
    public void change(Cell cell){
        backgroundColor.setColor((cell.getBackground() == Cell.Background.BLACK) ? Color.BLACK : (cell.getBackground() == Cell.Background.GRAY) ? Color.GRAY : Color.WHITE);
        backgroundColor.setShadowLayer(10, 30, 30, Color.CYAN);
        shapeColor.setColor((cell.getColor() == Cell.Color.BLUE) ? Color.BLUE : (cell.getColor() == Cell.Color.YELLOW) ? Color.YELLOW : Color.RED);
        shape = (cell.getShape() == Cell.Shape.CIRCLE) ? "circle" : (cell.getShape() == Cell.Shape.RECTANGLE) ? "rectangle" : "triangle";
        invalidate();
        requestLayout();
    }

    //Called when game is paused; redraws the cell
    public void setHiding(boolean hide){
        this.hide = hide;
        invalidate();
        requestLayout();
    }
}
