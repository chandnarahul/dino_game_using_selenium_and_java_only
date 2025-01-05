package dino.util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RGBImageUtility {

    private final BufferedImage image;

    public RGBImageUtility(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage convertToBinary() {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage binaryImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (isRGBDarkPixel(x, y)) {
                    binaryImage.setRGB(x, y, Color.BLACK.getRGB());
                } else {
                    binaryImage.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }
        return binaryImage;
    }

    public int[][] convertToAnArray() {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] imageArray = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (isRGBDarkPixel(x, y) && hasMinimumDarkNeighbors(x, y, 5)) {
                    imageArray[y][x] = 1;
                }
            }
        }
        return imageArray;
    }

    /*
        (-1,-1) | (-1,0)  | (-1,1)
        --------|---------|--------
        (0,-1)  | (x,y)   | (0,1)
        --------|---------|--------
        (1,-1)  | (1,0)   | (1,1)
    */
    private boolean hasMinimumDarkNeighbors(int x, int y, int neighbours) {
        int width = image.getWidth();
        int height = image.getHeight();
        int darkCount = 0;
        // Check all 8 surrounding pixels
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                // Skip the center pixel
                if (dx == 0 && dy == 0) {
                    continue;
                }
                int newX = x + dx;
                int newY = y + dy;
                // Check if the neighboring pixel is within image bounds
                if (newX >= 0 && newX < width && newY >= 0 && newY < height) {
                    if (isRGBDarkPixel(newX, newY)) {
                        darkCount++;
                        // Early return if we've found enough dark neighbors
                        if (darkCount >= neighbours) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean isRGBDarkPixel(int x, int y) {
        int rgb = image.getRGB(x, y);
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        int grayscale = (r + g + b) / 3;
        return grayscale < 128;
    }

}
