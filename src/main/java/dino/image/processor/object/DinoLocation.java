package dino.image.processor.object;

public class DinoLocation {
    private final int maxY;
    private final int minY;
    private final int dinoPixels;

    public DinoLocation(int maxY, int minY, int dinoPixels) {
        this.maxY = maxY;
        this.minY = minY;
        this.dinoPixels = dinoPixels;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMinY() {
        return minY;
    }

    public int getDinoPixels() {
        return dinoPixels;
    }
}
