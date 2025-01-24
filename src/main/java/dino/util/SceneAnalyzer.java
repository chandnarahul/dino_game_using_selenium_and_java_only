package dino.util;

import dino.image.processor.object.Shape;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static dino.Constants.MAX_NEIGHBOUR_PIXELS;
import static dino.Constants.MINIMUM_DISTANCE_FROM_DINO;

public class SceneAnalyzer {
    private boolean[][] visited;
    private int minX, maxX, minY, maxY;
    private final int[][] scene;

    public SceneAnalyzer(int[][] scene) {
        this.scene = scene;
    }

    public List<Shape> analyzeScene() {
        int id = 0;
        int rows = scene.length;
        int cols = scene[0].length;
        visited = new boolean[rows][cols];
        List<Shape> shapes = new ArrayList<>();

        // Ground is assumed to be at the last row
        int groundRow = rows - 1;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (scene[i][j] == 1 && !visited[i][j]) {
                    // Reset bounds for the new shape
                    minX = maxX = j;
                    minY = maxY = i;

                    // Explore the shape
                    exploreShape(scene, i, j, rows, cols);
                    int distanceFromDino = minX;

                    // Add the shape to the list
                    if (distanceFromDino > MINIMUM_DISTANCE_FROM_DINO) {
                        // Calculate dimensions and distance
                        int width = maxX - minX + 1;
                        int height = maxY - minY + 1;
                        // Determine if the shape touches the ground
                        boolean touchesGround = maxY >= groundRow;
                        shapes.add(new Shape(id++, width, height, touchesGround, distanceFromDino));
                    }
                }
            }
        }

        // Sort shapes by distance from Dino
        shapes.sort(Comparator.comparingInt(Shape::getxFromDino));
        return shapes;
    }

    private void exploreShape(int[][] scene, int row, int col, int rows, int cols) {
        if (row < 0 || row >= rows || col < 0 || col >= cols || visited[row][col]) {
            return;
        }

        // Include cells that are part of the shape or within 15 neighbors
        if (scene[row][col] != 1 && !isWithinNeighboringRange(scene, row, col, rows, cols)) {
            return;
        }

        // Mark the cell as visited
        visited[row][col] = true;

        // Update bounds
        minX = Math.min(minX, col);
        maxX = Math.max(maxX, col);
        minY = Math.min(minY, row);
        maxY = Math.max(maxY, row);

        // Explore all 8 directions
        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int i = 0; i < 8; i++) {
            exploreShape(scene, row + dx[i], col + dy[i], rows, cols);
        }
    }

    private boolean isWithinNeighboringRange(int[][] scene, int row, int col, int rows, int cols) {
        for (int i = Math.max(0, row - MAX_NEIGHBOUR_PIXELS); i <= Math.min(rows - 1, row + MAX_NEIGHBOUR_PIXELS); i++) {
            for (int j = Math.max(0, col - MAX_NEIGHBOUR_PIXELS); j <= Math.min(cols - 1, col + MAX_NEIGHBOUR_PIXELS); j++) {
                if (scene[i][j] == 1) {
                    // Check Euclidean distance
                    if (Math.sqrt(Math.pow(row - i, 2) + Math.pow(col - j, 2)) <= MAX_NEIGHBOUR_PIXELS) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
