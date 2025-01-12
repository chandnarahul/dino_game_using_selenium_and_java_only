package dino.util;

import dino.Constants;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static dino.util.BinaryImageUtility.printArray;

public class SceneAnalyzer {
    static class Shape {
        int width;
        int height;
        boolean isCloseToTheGround; // True if it touches the ground
        int xFromDino;    // Horizontal distance from Dino at X=0

        Shape(int width, int height, boolean isCloseToTheGround, int xFromDino) {
            this.width = width;
            this.height = height;
            this.isCloseToTheGround = isCloseToTheGround;
            this.xFromDino = xFromDino;
        }

        @Override
        public String toString() {
            return (isCloseToTheGround ? "Close To The Ground" : "Above Ground") +
                    " - Width: " + width +
                    ", Height: " + height +
                    ", Distance from Dino: X=" + xFromDino;
        }
    }

    private static boolean[][] visited;
    private static int minX, maxX, minY, maxY;
    private final BlockingQueue<int[][]> sceneQueue;
    private final BlockingQueue<List<Shape>> resultQueue;
    private volatile boolean isRunning;
    private Thread processingThread;

    public SceneAnalyzer() {
        this.sceneQueue = new LinkedBlockingQueue<>();
        this.resultQueue = new LinkedBlockingQueue<>();
        this.isRunning = true;
        startProcessingThread();
    }

    private void startProcessingThread() {
        processingThread = new Thread(() -> {
            while (isRunning) {
                try {
                    int[][] scene = sceneQueue.poll(10, TimeUnit.MILLISECONDS);
                    if (scene != null) {
                        List<Shape> shapes = analyzeScene(scene);
                        resultQueue.put(shapes);
                    }
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        processingThread.start();
    }

    public void submitScene(int[][] scene) {
        try {
            sceneQueue.put(scene);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    public List<Shape> getLatestResults() {
        try {
            return resultQueue.poll(10, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            return Collections.EMPTY_LIST;
        }
    }

    public void shutdown() {
        isRunning = false;
        processingThread.interrupt();
        try {
            processingThread.join(1000);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    public static List<Shape> analyzeScene(int[][] scene) {
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

                    // Calculate dimensions and distance
                    int width = maxX - minX + 1;
                    int height = maxY - minY + 1;
                    int distanceFromDino = minX;

                    // Determine if the shape touches the ground
                    boolean touchesGround = maxY >= groundRow;

                    // Add the shape to the list
                    shapes.add(new Shape(width, height, touchesGround, distanceFromDino));
                }
            }
        }

        // Sort shapes by distance from Dino
        shapes.sort((a, b) -> Integer.compare(a.xFromDino, b.xFromDino));
        return shapes;
    }

    private static void exploreShape(int[][] scene, int row, int col, int rows, int cols) {
        if (row < 0 || row >= rows || col < 0 || col >= cols || visited[row][col]) {
            return;
        }

        // Include cells that are part of the shape or within 15 neighbors
        if (scene[row][col] != 1 && !isWithinNeighboringRange(scene, row, col, rows, cols, 15)) {
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

    private static boolean isWithinNeighboringRange(int[][] scene, int row, int col, int rows, int cols, int range) {
        for (int i = Math.max(0, row - range); i <= Math.min(rows - 1, row + range); i++) {
            for (int j = Math.max(0, col - range); j <= Math.min(cols - 1, col + range); j++) {
                if (scene[i][j] == 1) {
                    // Check Euclidean distance
                    if (Math.sqrt(Math.pow(row - i, 2) + Math.pow(col - j, 2)) <= range) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void main(String[] args) throws IOException {
        SceneAnalyzer analyzer = new SceneAnalyzer();
        long baseStart = System.currentTimeMillis();
        try {
            for (int i = 332; i < 333; i++) {
                long start = System.currentTimeMillis();
                BufferedImage input = ImageIO.read(new File(String.format("samples/binary_image_%d.png", i)));
                start = printAndResetTime(start, "time to read file from disk");

                int[][] inputImageArray = new RGBImageUtility(input).convertGameImageToAnArray();
                start = printAndResetTime(start, "time to convert image to 2d array");

                analyzer.submitScene(inputImageArray);
                List<Shape> shapes = analyzer.getLatestResults();

                start = printAndResetTime(start, "time to find objects");
                if(shapes!=null)
                for (Shape shape : shapes) {
                    System.out.println(shape);
                }

                start = printAndResetTime(start, "time to print shapes");

                System.out.println(ObjectMatch.findMatches(inputImageArray, Constants.GAME_OVER_TEMPLATE, 0.9));

                start = printAndResetTime(start, "time to find game over");

                printArray(inputImageArray);

                start = printAndResetTime(start, "time to print array");
            }
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            analyzer.shutdown();
            printAndResetTime(baseStart, "Total time to run everything");
        }
    }

    public static long printAndResetTime(long start, String message) {
        System.out.println(String.format("%s %d", message, (System.currentTimeMillis() - start)));
        return System.currentTimeMillis();
    }
}
