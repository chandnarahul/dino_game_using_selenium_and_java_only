package dino.image.processor;

import dino.image.processor.object.Blob;
import dino.util.ImageUtility;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class MultipleBlobDetector {
    private static int i = 0;

    public List<Blob> countBlobsAfterDilation(BufferedImage originalImage) {
        // First, dilate the image
        BufferedImage dilatedImage = dilateCactus(originalImage);
        // Return the number of blobs
        List<Blob> blobs = detectBlobs(originalImage);
        ImageUtility imageUtility = new ImageUtility(dilatedImage);
        blobs.forEach(imageUtility::markBlobInImage);
        imageUtility.writeImageToFile("image_dialated_" + (i++) + ".png");
        return blobs;
    }

    public BufferedImage dilateCactus(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // Create a new image to store the dilated result
        BufferedImage dilatedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);

        // Dilation kernel (structuring element) - 3x3
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
                boolean isDilationRequired = isDilationRequired(originalImage, x, y, kernel);

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

    private boolean isDilationRequired(BufferedImage image, int centerX, int centerY, int[][] kernel) {
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
                        if (new ImageSegmentation(image).isDarkPixel(newX, newY) && kernel[kernelY][kernelX] == 1) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public List<Blob> detectBlobs(BufferedImage image) {
        // 2D array to track visited pixels
        boolean[][] visited = new boolean[image.getWidth()][image.getHeight()];
        List<Blob> blobs = new ArrayList<>();

        // Scan through entire image
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                // If pixel is black and not visited, start blob detection
                if (new ImageSegmentation(image).isDarkPixel(x, y) && !visited[x][y]) {
                    Blob blob = new Blob();
                    blob.leftmostX = image.getWidth();
                    blob.rightmostX = -1;
                    blob.topY = image.getHeight();
                    blob.bottomY = -1;

                    // Flood fill to mark the entire blob
                    floodFillAndMeasure(image, x, y, visited, blob);

                    blobs.add(blob);
                }
            }
        }

        return blobs;
    }

    private void floodFillAndMeasure(BufferedImage image, int startX, int startY, boolean[][] visited, Blob blob) {
        // Use a simple queue for flood fill
        List<int[]> queue = new ArrayList<>();
        queue.add(new int[]{startX, startY});

        while (!queue.isEmpty()) {
            int[] current = queue.remove(0);
            int x = current[0];
            int y = current[1];

            // Check bounds and if pixel is already visited
            if (x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight() ||
                    visited[x][y] || !new ImageSegmentation(image).isDarkPixel(x, y)) {
                continue;
            }

            // Mark as visited
            visited[x][y] = true;

            // Update blob dimensions
            blob.leftmostX = Math.min(blob.leftmostX, x);
            blob.rightmostX = Math.max(blob.rightmostX, x);
            blob.topY = Math.min(blob.topY, y);
            blob.bottomY = Math.max(blob.bottomY, y);

            // Add neighboring pixels to queue
            queue.add(new int[]{x + 1, y});
            queue.add(new int[]{x - 1, y});
            queue.add(new int[]{x, y + 1});
            queue.add(new int[]{x, y - 1});
        }
    }
}
