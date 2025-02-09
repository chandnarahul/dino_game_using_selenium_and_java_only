package dino.image.processor.object;

public class Shape {

    private final int id;
    private final int width;
    private final int height;
    private final boolean isCloseToTheGround; // True if it touches the ground
    private final int xFromDino;    // Horizontal distance from Dino at X=0

    public Shape(int id, int width, int height, boolean isCloseToTheGround, int xFromDino) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.isCloseToTheGround = isCloseToTheGround;
        this.xFromDino = xFromDino;
    }

    @Override
    public String toString() {
        return (isCloseToTheGround ? "Close To The Ground" : "Above Ground") +
                " - Id: " + id +
                " - Width: " + width +
                ", Height: " + height +
                ", Distance from Dino: X=" + xFromDino;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isCloseToTheGround() {
        return isCloseToTheGround;
    }

    public int getxFromDino() {
        return xFromDino;
    }

    public int getId() {
        return id;
    }
}

