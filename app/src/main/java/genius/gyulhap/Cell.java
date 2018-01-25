//This class contains the information on the cells the game needs

package genius.gyulhap;

public class Cell {
    //The enumerations containing all the possible characteristics of a Cell
    public enum Shape {
        CIRCLE, RECTANGLE, TRIANGLE
    }

    public enum Color {
        BLUE, YELLOW, RED
    }

    public enum Background {
        WHITE, GRAY, BLACK
    }

    private Shape shape;
    private Color color;
    private Background background;

    public Cell() {
        shape = Shape.CIRCLE;
        color = Color.BLUE;
        background = Background.WHITE;
    }

    public Cell(Shape shape, Color color, Background background) {
        this.shape = shape;
        this.color = color;
        this.background = background;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Background getBackground() {
        return background;
    }

    public void setBackground(Background background) {
        this.background = background;
    }

    @Override
    public String toString() {
        return "Cell{" + "shape=" + shape + ", color=" + color + ", background=" + background + '}';
    }

    //Checks if this cell is equal to another
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Cell other = (Cell) obj;
        if (this.shape != other.shape) {
            return false;
        }
        if (this.color != other.color) {
            return false;
        }
        if (this.background != other.background) {
            return false;
        }
        return true;
    }

    //Changes all the characteristics
    public void change(Shape shape, Color color, Background background){
        this.shape = shape;
        this.color = color;
        this.background = background;
    }

    //This function was used in the java-only windows version
    public Object[] toArr(){
        return new Object[]{this.shape,this.color,this.background};
    }

    public boolean isHap(Cell second, Cell third){
        boolean setShape = similarOr(this.getShape(),second.getShape(),third.getShape());
        boolean setColor = similarOr(this.getColor(),second.getColor(),third.getColor());
        boolean setBackground = similarOr(this.getBackground(),second.getBackground(),third.getBackground());
        if(setShape && setColor && setBackground)
            return true;
        return false;
    }

    public boolean similarOr(Object first, Object second, Object third){
        if(first instanceof Shape){
            Shape iFirst = (Shape)first;
            Shape iSecond = (Shape)second;
            Shape iThird = (Shape)third;
        }
        else if(first instanceof Color){
            Color iFirst = (Color)first;
            Color iSecond = (Color)second;
            Color iThird = (Color)third;
        }
        else{
            Background iFirst = (Background)first;
            Background iSecond = (Background)second;
            Background iThird = (Background)third;
        }
        if((first == second) && (second == third))
            return true;
        if((first != second) && (first != third) && (second !=third))
            return true;
        return false;
    }
}
