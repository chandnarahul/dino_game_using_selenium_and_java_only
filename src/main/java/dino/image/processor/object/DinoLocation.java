package dino.image.processor.object;

public class DinoLocation {
    private final int maxY;
    private final int minY;
    private final int dinoPixels;

    public DinoLocation(int minY,int maxY, int dinoPixels) {
        this.minY = minY;
        this.maxY = maxY;
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

    @Override
    public String toString() {
        return "DinoLocation{" +
                "maxY=" + maxY +
                ", minY=" + minY +
                ", dinoPixels=" + dinoPixels +
                '}';
    }
}
