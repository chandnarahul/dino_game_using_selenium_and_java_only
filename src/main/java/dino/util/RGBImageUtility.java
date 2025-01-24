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
        int effectiveHeight = height;
        int[][] imageArray = new int[effectiveHeight][width];
        boolean[] isDark = new boolean[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            isDark[i] = isRGBDarkPixel(pixels[i]);
        }
        for (int y = 0; y < height; y++) {
            int baseIndex = y * width;
            int targetY = y;

            for (int x = 0; x < width; x++) {
                if (isDark[baseIndex + x] && countDarkNeighbors(x, y, isDark) >= 5) {
                    imageArray[targetY][x] = 1;
                }
            }
        }

        return imageArray;
    }

    public int[][] convertGameImageToAnArray(DinoLocation dinoLocation) {
        int effectiveHeight = height;
        int[][] imageArray = new int[effectiveHeight][width - dinoLocation.getDinoPixels()];
        boolean[] isDark = new boolean[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            isDark[i] = isRGBDarkPixel(pixels[i]);
        }
        for (int y = dinoLocation.getMinY(); y <= dinoLocation.getMaxY(); y++) {
            int baseIndex = y * width;
            for (int x = dinoLocation.getDinoPixels(); x < width; x++) {
                if (isDark[baseIndex + x] && countDarkNeighbors(x, y, isDark) >= MINIMUM_NEIGHBOURS) {
                    int targetX = x - dinoLocation.getDinoPixels();
                    imageArray[y][targetX] = 1;
                }
            }
        }

        return imageArray;
    }

    private int countDarkNeighbors(int x, int y, boolean[] isDark) {
        int count = 0;
        int baseIndex = y * width;
        if (x > 0) {
            if (y > 0 && isDark[baseIndex - width + x - 1]) count++;
            if (isDark[baseIndex + x - 1]) count++;
            if (y < height - 1 && isDark[baseIndex + width + x - 1]) count++;
        }

        if (y > 0 && isDark[baseIndex - width + x]) count++;
        if (y < height - 1 && isDark[baseIndex + width + x]) count++;

        if (x < width - 1) {
            if (y > 0 && isDark[baseIndex - width + x + 1]) count++;
            if (isDark[baseIndex + x + 1]) count++;
            if (y < height - 1 && isDark[baseIndex + width + x + 1]) count++;
        }
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
