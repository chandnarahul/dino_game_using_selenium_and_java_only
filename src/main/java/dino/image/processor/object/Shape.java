package dino.image.processor.object;

public class Shape {
    private final int width;
    private final int height;
    private final boolean isCloseToTheGround; // True if it touches the ground
    private final int xFromDino;    // Horizontal distance from Dino at X=0

    public Shape(int width, int height, boolean isCloseToTheGround, int xFromDino) {
        this.width = width;
        this.height = height;
        this.isCloseToTheGround = isCloseToTheGround;
        this.xFromDino = xFromDino;
    }

    @Override
    public String toString() {
        return (isCloseToTheGround ? "Close To The Ground" : "Above Ground") +
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
}

