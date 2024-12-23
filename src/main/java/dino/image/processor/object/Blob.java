package dino.image.processor.object;

public class Blob {
    public int leftmostX;
    public int rightmostX;
    public int topY;
    public int bottomY;

    public int getWidth() {
        return rightmostX - leftmostX + 1;
    }

    public int getHeight() {
        return bottomY - topY + 1;
    }

    @Override
    public String toString() {
        return "Blob: Width=" + getWidth() + ", Height=" + getHeight() +
                ", X=[" + leftmostX + "," + rightmostX + "]" +
                ", Y=[" + topY + "," + bottomY + "]";
    }
}