//Non-view class that creates a grid and contains a 3x3 grid of cells

package genius.gyulhap;

import java.util.ArrayList;

public class Grid {
    Cell[] grid;
    ArrayList<String> hapAnswers = new ArrayList();
    ArrayList<String> foundHap = new ArrayList();

    //Sets the grid and gives every cell attributes with changeAll()
    public Grid(){
        grid = new Cell[9];
        for(int i = 0;i<grid.length;i++)
            grid[i] = new Cell();
        changeAll();
    }

    public ArrayList<String> getFoundHap() {
        return foundHap;
    }

    public Cell[] getCells(){
        return grid;
    }

    //Sees if the combination of cell is an hap, by seeing if it is in the hapAnswers
    public boolean hap(String cells){
        if(hapAnswers.contains(cells)){
            if(foundHap.contains(cells))
                return false;
            foundHap.add(cells);
            return true;
        }
        return false;
    }

    //Finds all possible haps and adds them to hapAnswers
    public void findHap(){
        for(int i = 0; i<grid.length - 2 ;i++)
            for(int j = i + 1;j<grid.length - 1;j++)
                for(int k = j + 1;k<grid.length;k++)
                    if(grid[i].isHap(grid[j],grid[k]))
                        hapAnswers.add((i+1)+""+(j+1)+""+(k+1));
    }

    //Returns true if all the haps were found
    public boolean gyul(){
        return foundHap.containsAll(hapAnswers);
    }

    //Gives random characteristics to all the cells, then finds the new haps
    public void changeAll(){
        Cell.Shape[] shapes = {Cell.Shape.CIRCLE,Cell.Shape.RECTANGLE,Cell.Shape.TRIANGLE};
        Cell.Color[] colors = {Cell.Color.BLUE,Cell.Color.YELLOW,Cell.Color.RED};
        Cell.Background[] backgrounds = {Cell.Background.BLACK,Cell.Background.GRAY,Cell.Background.WHITE};
        for(int i = 0;i<grid.length;i++)
            grid[i].change(shapes[(int)(Math.random()*3)], colors[(int)(Math.random()*3)], backgrounds[(int)(Math.random()*3)]);
        foundHap.clear();
        hapAnswers.clear();
        findHap();
    }

}