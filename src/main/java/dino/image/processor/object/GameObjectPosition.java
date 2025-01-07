package dino.image.processor.object;

public class GameObjectPosition {
    private final int leftmostX;
    private final int rightmostX;
    private final int topY;
    private final int bottomY;

    public GameObjectPosition(int leftmostX, int rightmostX, int topY, int bottomY) {
        this.leftmostX = leftmostX;
        this.rightmostX = rightmostX;
        this.topY = topY;
        this.bottomY = bottomY;
    }


    public int getWidth() {
        return rightmostX - leftmostX + 1;
    }

    public int getHeight() {
        return bottomY - topY + 1;
    }

    public int getLeftmostX() {
        return leftmostX;
    }

    public int getRightmostX() {
        return rightmostX;
    }

    public int getTopY() {
        return topY;
    }

    public int getBottomY() {
        return bottomY;
    }

    @Override
    public String toString() {
        return "Blob: Width=" + getWidth() + ", Height=" + getHeight() +
                ", X=[" + leftmostX + "," + rightmostX + "]" +
                ", Y=[" + topY + "," + bottomY + "]";
    }
}
