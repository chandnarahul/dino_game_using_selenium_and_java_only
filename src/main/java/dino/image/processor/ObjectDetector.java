package dino.image.processor;

import dino.image.processor.object.DinoLocation;

import static dino.Constants.SKIP_DINO_EYE_PIXELS;

public class ObjectDetector {
    private final int[][] image;
    public ObjectDetector(int[][] image) {
        this.image = image;
    }

    public DinoLocation identifyDinoLocation() {
        int minY = Integer.MAX_VALUE, maxY = 0, previousMinY = Integer.MAX_VALUE;
        for (int y = image.length - 1; y >= 0; y--) {
            int x = 0;
            for (; x < image[0].length / 10; x++) {
                if (image[y][x] == 1 && maxY == 0) {
                    maxY = y;
                } else if (image[y][x] == 1 && y < minY) {
                    minY = y;
                }
            }
            if (minY == previousMinY && minY != Integer.MAX_VALUE) {
                int dinoFaceWidth = x + getDinoFaceWidth(minY, x);
                return new DinoLocation(minY, maxY, dinoFaceWidth);
            }
            previousMinY = minY;
        }
        throw new RuntimeException("Unable to Identify Dino x and y values");
    }

    private int getDinoFaceWidth(final int minY, final int minX) {
        int maxX = 0;
        int y = minY;
        while ((image[y][minX] != 0 || image[y][minX + SKIP_DINO_EYE_PIXELS] != 0)) {
            int count = 0;
            int x = minX;
            while (image[y][x] != 0) {
                count++;
                x++;
            }
            if (count > maxX) {
                maxX = count;
            }
            y++;
        }
        return maxX;
    }

}
