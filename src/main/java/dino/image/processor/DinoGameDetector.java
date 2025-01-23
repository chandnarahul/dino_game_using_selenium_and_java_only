package dino.image.processor;

import dino.image.processor.object.DinoGameObject;
import dino.image.processor.object.DinoGameObjects;
import dino.image.processor.object.DinoPoint;

import java.util.ArrayList;
import java.util.List;

import static dino.Constants.*;

public class DinoGameDetector {// Ignore score at top

    public static DinoGameObjects detectObjects(int[][] screen) {
        // Constants
        final int DINO_REGION_X = screen[0].length; // Dino appears in first 1/20 of screen

        List<DinoGameObject> obstacles = new ArrayList<>();

        // Find all objects using simple connected component analysis
        boolean[][] visited = new boolean[screen.length][screen[0].length];

        for (int y = 0; y < screen.length; y++) {
            for (int x = 0; x < screen[0].length; x++) {
                if (screen[y][x] == 1 && !visited[y][x]) {
                    DinoPoint bounds = new DinoPoint(x, y, 0, 0); // Initial bounds
                    floodFill(screen, visited, y, x, DINO_REGION_X, bounds);
                    if (bounds.x >= DINO_REGION_X) {
                        obstacles.add(new DinoGameObject("obstacle", bounds));
                    }
                }
            }
        }

        return new DinoGameObjects(obstacles);
    }

    private static void floodFill(int[][] screen, boolean[][] visited, int y, int x, int dinoRegionX, DinoPoint bounds) {
        // Base case: out of bounds, already visited, or not part of object
        if (y < 0 || y >= screen.length || x < 0 || x >= screen[0].length ||
                visited[y][x] || screen[y][x] != 1) {
            return;
        }

        // Mark current pixel as visited
        visited[y][x] = true;

        // If not in dino region, update bounds
        if (x >= dinoRegionX) {
            // Update bounds
            bounds.x = Math.min(bounds.x, x);
            bounds.y = Math.min(bounds.y, y);
            bounds.width = Math.max(bounds.width, x - bounds.x + 1);
            bounds.height = Math.max(bounds.height, y - bounds.y + 1);
        }

        // Recursively check 4 adjacent pixels
        floodFill(screen, visited, y - 1, x, dinoRegionX, bounds); // up
        floodFill(screen, visited, y + 1, x, dinoRegionX, bounds); // down
        floodFill(screen, visited, y, x - 1, dinoRegionX, bounds); // left
        floodFill(screen, visited, y, x + 1, dinoRegionX, bounds); // right
    }
}