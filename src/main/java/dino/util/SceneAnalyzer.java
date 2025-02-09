package dino.util;

import dino.image.processor.object.Shape;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static dino.Constants.MAX_NEIGHBOUR_PIXELS;
import static dino.Constants.MINIMUM_DISTANCE_FROM_DINO;

public class SceneAnalyzer {
    // The 2D array representing the scene
    private final int[][] scene;
    // A 2D array to keep track of visited cells
    private boolean[][] visited;
    // Variables to store the bounds of the current shape
    private int minX, maxX, minY, maxY;

    // Constructor to initialize the scene
    public SceneAnalyzer(int[][] scene) {
        this.scene = scene;
    }

    // Method to analyze the scene and identify shapes
    public List<Shape> analyzeScene() {
        int id = 0;
        int rows = scene.length;
        int cols = scene[0].length;
        visited = new boolean[rows][cols];
        List<Shape> shapes = new ArrayList<>();
        // Ground is assumed to be at the last row
        int groundRow = rows - 1;

        // Iterate through each cell in the scene
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // If the cell is part of a shape and not visited
                if (scene[i][j] == 1 && !visited[i][j]) {
                    // Initialize the bounds for the new shape
                    minX = maxX = j;
                    minY = maxY = i;
                    // Explore the shape to determine its bounds
                    exploreShape(i, j, rows, cols);
                    int distanceFromDino = minX;

                    // If the shape is far enough from Dino
                    if (distanceFromDino > MINIMUM_DISTANCE_FROM_DINO) {
                        // Calculate the dimensions of the shape
                        int width = maxX - minX + 1;
                        int height = maxY - minY + 1;
                        // Check if the shape touches the ground
                        boolean touchesGround = maxY >= groundRow;
                        // Add the shape to the list
                        shapes.add(new Shape(id++, width, height, touchesGround, distanceFromDino));
                    }
                }
            }
        }

        // Sort shapes by their distance from Dino
        shapes.sort(Comparator.comparingInt(Shape::getxFromDino));
        return shapes;
    }

    // Method to explore a shape using depth-first search
    private void exploreShape(int row, int col, int rows, int cols) {
        // Check if the cell is out of bounds or already visited
        if (row < 0 || row >= rows || col < 0 || col >= cols || visited[row][col]) {
            return;
        }

        // Check if the cell is part of the shape or within neighboring range
        if (scene[row][col] != 1 && !isWithinNeighboringRange(row, col, rows, cols)) {
            return;
        }

        // Mark the cell as visited
        visited[row][col] = true;
        // Update the bounds of the shape
        minX = Math.min(minX, col);
        maxX = Math.max(maxX, col);
        minY = Math.min(minY, row);
        maxY = Math.max(maxY, row);

        // Arrays to explore all 8 possible directions
        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};

        // Recursively explore all 8 directions
        for (int i = 0; i < 8; i++) {
            exploreShape(row + dx[i], col + dy[i], rows, cols);
        }
    }

    // Method to check if a cell is within neighboring range of the shape
    private boolean isWithinNeighboringRange(int row, int col, int rows, int cols) {
        int maxDistanceSquared = MAX_NEIGHBOUR_PIXELS * MAX_NEIGHBOUR_PIXELS;
        // Iterate through the neighboring cells
        for (int i = Math.max(0, row - MAX_NEIGHBOUR_PIXELS); i <= Math.min(rows - 1, row + MAX_NEIGHBOUR_PIXELS); i++) {
            for (int j = Math.max(0, col - MAX_NEIGHBOUR_PIXELS); j <= Math.min(cols - 1, col + MAX_NEIGHBOUR_PIXELS); j++) {
                // If the neighboring cell is part of the shape
                if (scene[i][j] == 1) {
                    // Calculate the squared distance to avoid using sqrt
                    int distanceSquared = (row - i) * (row - i) + (col - j) * (col - j);
                    // Check if the distance is within the allowed range
                    if (distanceSquared <= maxDistanceSquared) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}