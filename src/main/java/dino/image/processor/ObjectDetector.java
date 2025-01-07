package dino.image.processor;

import dino.image.processor.object.GameObjectPosition;

import java.util.ArrayList;
import java.util.List;

public class ObjectDetector {
    private final int[][] image;
    private final int height;
    private final int width;

    public ObjectDetector(int[][] image) {
        this.image = image;
        this.height = image.length;
        this.width = image[0].length;
    }

    public List<GameObjectPosition> detect() {
        // 2D array to track visited pixels
        boolean[][] visited = new boolean[width][height];
        List<GameObjectPosition> blobs = new ArrayList<>();

        // Scan through entire image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // If pixel is black and not visited, start blob detection
                if (image[x][y] == 1 && !visited[x][y]) {
                    // Flood fill to mark the entire nextBlob
                    GameObjectPosition initialBlob = new GameObjectPosition(width, -1, height, -1);
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
            if (x < 0 || x >= width || y < 0 || y >= height ||
                    visited[x][y] || !(image[x][y] == 1)) {
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
