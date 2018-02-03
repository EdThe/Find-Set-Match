//This class contains the methods that the Help Activity uses

package genius.gyulhap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        //Creation of the cells that the Help page use
        Cell c1p1 = new Cell(Cell.Shape.CIRCLE, Cell.Color.YELLOW, Cell.Background.GRAY);
        Cell c1p2 = new Cell(Cell.Shape.RECTANGLE, Cell.Color.YELLOW, Cell.Background.GRAY);
        Cell c1p3 = new Cell(Cell.Shape.TRIANGLE, Cell.Color.YELLOW, Cell.Background.GRAY);

        Cell c2p1 = new Cell(Cell.Shape.RECTANGLE, Cell.Color.YELLOW, Cell.Background.WHITE);
        Cell c2p2 = new Cell(Cell.Shape.RECTANGLE, Cell.Color.RED, Cell.Background.BLACK);
        Cell c2p3 = new Cell(Cell.Shape.RECTANGLE, Cell.Color.BLUE, Cell.Background.GRAY);

        Cell c3p1 = new Cell(Cell.Shape.CIRCLE, Cell.Color.RED, Cell.Background.WHITE);
        Cell c3p2 = new Cell(Cell.Shape.CIRCLE, Cell.Color.RED, Cell.Background.WHITE);
        Cell c3p3 = new Cell(Cell.Shape.CIRCLE, Cell.Color.RED, Cell.Background.WHITE);

        Cell c4p1 = new Cell(Cell.Shape.CIRCLE, Cell.Color.BLUE, Cell.Background.GRAY);
        Cell c4p2 = new Cell(Cell.Shape.TRIANGLE, Cell.Color.BLUE, Cell.Background.GRAY);
        Cell c4p3 = new Cell(Cell.Shape.CIRCLE, Cell.Color.BLUE, Cell.Background.GRAY);


        //Gives the correct attributes to each ViewCells
        ((ViewCell)findViewById(R.id.combo1Part1)).change(c1p1);
        ((ViewCell)findViewById(R.id.combo1Part2)).change(c1p2);
        ((ViewCell)findViewById(R.id.combo1Part3)).change(c1p3);

        ((ViewCell)findViewById(R.id.combo2Part1)).change(c2p1);
        ((ViewCell)findViewById(R.id.combo2Part2)).change(c2p2);
        ((ViewCell)findViewById(R.id.combo2Part3)).change(c2p3);

        ((ViewCell)findViewById(R.id.combo3Part1)).change(c3p1);
        ((ViewCell)findViewById(R.id.combo3Part2)).change(c3p2);
        ((ViewCell)findViewById(R.id.combo3Part3)).change(c3p3);

        ((ViewCell)findViewById(R.id.combo4Part1)).change(c4p1);
        ((ViewCell)findViewById(R.id.combo4Part2)).change(c4p2);
        ((ViewCell)findViewById(R.id.combo4Part3)).change(c4p3);
    }


    //Method that the back button uses to go back to the menu
    public void backButton(View v){
        finish();
    }
}
