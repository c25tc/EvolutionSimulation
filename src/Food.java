import processing.core.PVector;
//0: leaf
//1: meat
public class Food {
    int key;
    PVector pos;
    int size;
    int type;
    public Food(int x, int y, int size, int type, int key) {
        this.pos = new PVector(x, y);
        this.size = size;
        this.type = type;
        this.key = key;
    }
}
