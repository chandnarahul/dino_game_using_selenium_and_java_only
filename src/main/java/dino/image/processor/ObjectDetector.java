package dino.image.processor;

import dino.image.processor.object.GameObjectPosition;
import dino.util.ImageUtility;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ObjectDetector {
    private final BufferedImage image;

    public ObjectDetector(BufferedImage image) {
        this.image = image;
    }

    public List<GameObjectPosition> detect() {
        // 2D array to track visited pixels
        boolean[][] visited = new boolean[image.getWidth()][image.getHeight()];
        List<GameObjectPosition> blobs = new ArrayList<>();

        // Scan through entire image
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                // If pixel is black and not visited, start blob detection
                if (new ImageUtility(image).isDarkPixel(x, y) && !visited[x][y]) {
                    // Flood fill to mark the entire nextBlob
                    GameObjectPosition initialBlob = new GameObjectPosition(image.getWidth(), -1, image.getHeight(), -1);
                    GameObjectPosition floodFillAndMeasureBlob = floodFillAndMeasure(x, y, visited, initialBlob);
                    blobs.add(floodFillAndMeasureBlob);
                }
            }
        }

        return blobs;
    }

    private GameObjectPosition floodFillAndMeasure(int startX, int startY, boolean[][] visited, GameObjectPosition blob) {
        // Use a simple queue for flood fill
        List<int[]> queue = new ArrayList<>();
        queue.add(new int[]{startX, startY});

        GameObjectPosition tempBlob = blob;
        while (!queue.isEmpty()) {
            int[] current = queue.remove(0);
            int x = current[0];
            int y = current[1];

            // Check bounds and if pixel is already visited
            if (x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight() ||
                    visited[x][y] || !new ImageUtility(image).isDarkPixel(x, y)) {
                continue;
            }

            // Mark as visited
            visited[x][y] = true;

            // Update blob dimensions
            tempBlob = new GameObjectPosition(Math.min(tempBlob.getLeftmostX(), x), Math.max(tempBlob.getRightmostX(), x), Math.min(tempBlob.getTopY(), y), Math.max(tempBlob.getBottomY(), y));

            // Add neighboring pixels to queue
            queue.add(new int[]{x + 1, y});
            queue.add(new int[]{x - 1, y});
            queue.add(new int[]{x, y + 1});
            queue.add(new int[]{x, y - 1});
        }

        return tempBlob;
    }
}
