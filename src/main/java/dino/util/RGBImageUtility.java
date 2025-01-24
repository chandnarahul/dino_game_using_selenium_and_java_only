package dino.util;

import dino.image.processor.object.DinoLocation;

import java.awt.image.BufferedImage;

import static dino.Constants.MINIMUM_NEIGHBOURS;

public class RGBImageUtility {
    private final int width;
    private final int height;
    private final int[] pixels;

    public RGBImageUtility(BufferedImage image) {
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.pixels = image.getRGB(0, 0, width, height, null, 0, width);
    }

    public int[][] convertToAnArray() {
        int[][] imageArray = new int[height][width];
        boolean[] isDark = new boolean[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            isDark[i] = isRGBDarkPixel(pixels[i]);
        }
        for (int y = 0; y < height; y++) {
            int baseIndex = y * width;
            for (int x = 0; x < width; x++) {
                if (isDark[baseIndex + x] && countDarkNeighbors(x, y, isDark) >= MINIMUM_NEIGHBOURS) {
                    imageArray[y][x] = 1;
                }
            }
        }
        return imageArray;
    }

    public int[][] processImage(DinoLocation dinoLocation, int[][] imageArray) {
        int minY = dinoLocation.getMinY();
        int maxY = dinoLocation.getMaxY();
        int startX = dinoLocation.getDinoPixels();

        // Calculate new dimensions
        int newHeight = maxY - minY + 1;
        int newWidth = width - startX;

        // Create smaller array
        int[][] subImageArray = new int[newHeight][newWidth];
        // Copy values with adjusted coordinates
        for (int y = minY; y <= maxY; y++) {
            if (width - startX >= 0) {
                System.arraycopy(imageArray[y], startX, subImageArray[y - minY], startX - startX, width - startX);
            }
        }

        return subImageArray;
    }

    private int countDarkNeighbors(int x, int y, boolean[] isDark) {
        int count = 0;
        int baseIndex = y * width;

        if (x > 0) {
            if (y > 0 && isDark[baseIndex - width + x - 1]) count++;
            if (isDark[baseIndex + x - 1]) count++;
            if (y < height - 1 && isDark[baseIndex + width + x - 1]) count++;
        }

        if (x < width - 1) {
            if (y > 0 && isDark[baseIndex - width + x + 1]) count++;
            if (isDark[baseIndex + x + 1]) count++;
            if (y < height - 1 && isDark[baseIndex + width + x + 1]) count++;
        }

        if (y > 0 && isDark[baseIndex - width + x]) count++;
        if (y < height - 1 && isDark[baseIndex + width + x]) count++;

        return count;
    }


    public boolean isRGBDarkPixel(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        int grayscale = (r + g + b) / 3;
        return grayscale < 128;
    }

}
