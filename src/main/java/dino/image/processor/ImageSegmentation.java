package dino.image.processor;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageSegmentation {

    public class ObjectInfo {
        public int startX;
        public int width;
        public BufferedImage processedImage;

        public ObjectInfo(int startX, int width, BufferedImage processedImage) {
            this.startX = startX;
            this.width = width;
            this.processedImage = processedImage;
        }
    }

    public ObjectInfo processImage(BufferedImage input, int windowSize, double threshold) {
        int width = input.getWidth();
        int height = input.getHeight();
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int leftmostX = width;
        int rightmostX = -1;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (isPartOfLargeObject(input, x, y, windowSize, threshold)) {
                    output.setRGB(x, y, Color.BLACK.getRGB());
                    leftmostX = Math.min(leftmostX, x);
                    rightmostX = Math.max(rightmostX, x);
                } else {
                    output.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }

        int startX = (leftmostX < width) ? leftmostX : -1;
        int objectWidth = (rightmostX >= 0 && leftmostX < width) ? (rightmostX - leftmostX + 1) : 0;

        return new ObjectInfo(startX, objectWidth, output);
    }

    public ObjectInfo dilateImage(BufferedImage input, int windowSize, double threshold, int dilationSize) {
        int width = input.getWidth();
        int height = input.getHeight();
        BufferedImage binaryImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        BufferedImage dilatedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);

        // Step 1: Create binary image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (isPartOfLargeObject(input, x, y, windowSize, threshold)) {
                    binaryImage.setRGB(x, y, Color.BLACK.getRGB());
                } else {
                    binaryImage.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }

        // Step 2: Perform dilation
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (isDilated(binaryImage, x, y, dilationSize)) {
                    dilatedImage.setRGB(x, y, Color.BLACK.getRGB());
                } else {
                    dilatedImage.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }

        // Step 3: Find object boundaries
        int leftmostX = width;
        int rightmostX = -1;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (dilatedImage.getRGB(x, y) == Color.BLACK.getRGB()) {
                    leftmostX = Math.min(leftmostX, x);
                    rightmostX = Math.max(rightmostX, x);
                }
            }
        }

        int startX = (leftmostX < width) ? leftmostX : -1;
        int objectWidth = (rightmostX >= 0 && leftmostX < width) ? (rightmostX - leftmostX + 1) : 0;

        return new ObjectInfo(startX, objectWidth, dilatedImage);
    }

    private boolean isPartOfLargeObject(BufferedImage image, int centerX, int centerY, int windowSize, double threshold) {
        int halfWindow = windowSize / 2;
        int totalPixels = 0;
        int objectPixels = 0;

        for (int y = centerY - halfWindow; y <= centerY + halfWindow; y++) {
            for (int x = centerX - halfWindow; x <= centerX + halfWindow; x++) {
                if (x >= 0 && x < image.getWidth() && y >= 0 && y < image.getHeight()) {
                    totalPixels++;
                    if (isObjectPixel(image.getRGB(x, y))) {
                        objectPixels++;
                    }
                }
            }
        }

        return (double) objectPixels / totalPixels > threshold;
    }

    private boolean isObjectPixel(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        int grayscale = (r + g + b) / 3;
        return grayscale < 128; // Assuming darker pixels are part of the object
    }

    private boolean isDilated(BufferedImage image, int centerX, int centerY, int dilationSize) {
        int halfDilation = dilationSize / 2;

        for (int y = centerY - halfDilation; y <= centerY + halfDilation; y++) {
            for (int x = centerX - halfDilation; x <= centerX + halfDilation; x++) {
                if (x >= 0 && x < image.getWidth() && y >= 0 && y < image.getHeight()) {
                    if (image.getRGB(x, y) == Color.BLACK.getRGB()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}