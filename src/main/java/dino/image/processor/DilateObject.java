package dino.image.processor;

import dino.util.ImageUtility;

import java.awt.*;
import java.awt.image.BufferedImage;

public class DilateObject {
    private final BufferedImage image;

    public DilateObject(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage dilate() {
        int width = image.getWidth();
        int height = image.getHeight();

        // Create a new image to store the dilated result
        BufferedImage dilatedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);

        // Dilation kernel (structuring element) - 4x4
        int[][] kernel = {
                {1, 1, 1, 1},
                {1, 1, 1, 1},
                {1, 1, 1, 1},
                {1, 1, 1, 1},
        };

        // Iterate through each pixel
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Check if current pixel needs dilation
                boolean isDilationRequired = isDilationRequired(x, y, kernel);

                // Set the pixel color
                if (isDilationRequired) {
                    dilatedImage.setRGB(x, y, Color.BLACK.getRGB());
                } else {
                    dilatedImage.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }

        return dilatedImage;
    }

    private boolean isDilationRequired(int centerX, int centerY, int[][] kernel) {
        int kernelSize = kernel.length;
        int kernelOffset = kernelSize / 2;

        // Check neighboring pixels based on kernel
        for (int dy = -kernelOffset; dy < kernelOffset; dy++) {
            for (int dx = -kernelOffset; dx < kernelOffset; dx++) {
                int newX = centerX + dx;
                int newY = centerY + dy;

                // Adjust kernel indexing to avoid out of bounds
                int kernelY = dy + kernelOffset;
                int kernelX = dx + kernelOffset;

                // Ensure kernel indices are within bounds
                if (kernelY >= 0 && kernelY < kernelSize &&
                        kernelX >= 0 && kernelX < kernelSize) {

                    // Check if neighborhood pixel is within image bounds
                    if (newX >= 0 && newX < image.getWidth() &&
                            newY >= 0 && newY < image.getHeight()) {

                        // If any neighboring pixel is black and kernel supports it
                        if (new ImageUtility(image).isDarkPixel(newX, newY) && kernel[kernelY][kernelX] == 1) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
}
