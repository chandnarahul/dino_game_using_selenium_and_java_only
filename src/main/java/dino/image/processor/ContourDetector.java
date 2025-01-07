package dino.image.processor;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ContourDetector {
    private static final int[] DX = {1, 1, 0, -1, -1, -1, 0, 1};
    private static final int[] DY = {0, 1, 1, 1, 0, -1, -1, -1};

    public static class Point {
        int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static class Contour {
        List<Point> points;
        int minX, maxX, minY, maxY;

        Contour(List<Point> points) {
            this.points = points;
            calculateBounds();
        }

        private void calculateBounds() {
            minX = minY = Integer.MAX_VALUE;
            maxX = maxY = Integer.MIN_VALUE;
            for (Point p : points) {
                minX = Math.min(minX, p.x);
                maxX = Math.max(maxX, p.x);
                minY = Math.min(minY, p.y);
                maxY = Math.max(maxY, p.y);
            }
        }

        public int width() {
            return maxX - minX + 1;
        }

        public int height() {
            return maxY - minY + 1;
        }

        public double aspectRatio() {
            return (double) width() / height();
        }

        public boolean isHorizontalLine() {
            return height() <= 2;
        }

        public boolean isTopThird(int imageHeight) {
            return maxY < imageHeight / 3;
        }

        public boolean isBottomThird(int imageHeight) {
            return minY > imageHeight * 2 / 3;
        }
    }

    private static List<Contour> findContours(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        boolean[][] visited = new boolean[height][width];
        List<Contour> contours = new ArrayList<>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (!visited[y][x] && image.getRGB(x, y) == Color.BLACK.getRGB()) {
                    List<Point> contourPoints = traceContour(image, x, y, visited);
                    if (!contourPoints.isEmpty()) {
                        contours.add(new Contour(contourPoints));
                    }
                }
            }
        }

        return contours;
    }

    private static List<Point> traceContour(BufferedImage image, int startX, int startY, boolean[][] visited) {
        List<Point> contour = new ArrayList<>();
        Stack<Point> stack = new Stack<>();
        stack.push(new Point(startX, startY));

        while (!stack.isEmpty()) {
            Point p = stack.pop();
            if (p.y < 0 || p.y >= visited.length || p.x < 0 || p.x >= visited[0].length
                    || visited[p.y][p.x] || image.getRGB(p.x, p.y) != Color.BLACK.getRGB()) {
                continue;
            }

            visited[p.y][p.x] = true;
            contour.add(p);

            for (int i = 0; i < 8; i++) {
                stack.push(new Point(p.x + DX[i], p.y + DY[i]));
            }
        }

        return contour;
    }

    public static class GameElements {
        Point dinoPosition;
        int roadY;
        Point scorePosition;  // Added to track score location
        List<Contour> scoreContours = new ArrayList<>();
        int score;

        @Override
        public String toString() {
            return String.format("GameElements{dino=(%d,%d), roadY=%d, score_pos=(%d,%d)}",
                    dinoPosition != null ? dinoPosition.x : -1,
                    dinoPosition != null ? dinoPosition.y : -1,
                    roadY,
                    scorePosition != null ? scorePosition.x : -1,
                    scorePosition != null ? scorePosition.y : -1
            );
        }
    }

    public static GameElements detectGameElements(BufferedImage image) {
        List<Contour> contours = findContours(image);
        GameElements elements = new GameElements();

        // Find the largest contour in the middle section - likely the dino
        Contour dinoContour = null;
        int maxDinoSize = 0;

        // Find the longest horizontal line near the bottom - the road
        Contour roadContour = null;
        int maxRoadWidth = 0;

        // Track the leftmost score digit position
        int minScoreX = Integer.MAX_VALUE;
        int scoreY = -1;

        for (Contour contour : contours) {
            // Skip very small contours (noise)
            if (contour.points.size() < 10) continue;

            if (contour.isTopThird(image.getHeight()) &&
                    contour.height() > 5 && contour.height() < 30) {
                // Likely a score digit
                elements.scoreContours.add(contour);
                if (contour.minX < minScoreX) {
                    minScoreX = contour.minX;
                    scoreY = contour.minY;
                }
            } else if (contour.isHorizontalLine() &&
                    contour.isBottomThird(image.getHeight()) &&
                    contour.width() > maxRoadWidth) {
                roadContour = contour;
                maxRoadWidth = contour.width();
            } else if (contour.points.size() > maxDinoSize &&
                    !contour.isTopThird(image.getHeight()) &&
                    !contour.isBottomThird(image.getHeight())) {
                dinoContour = contour;
                maxDinoSize = contour.points.size();
            }
        }

        if (dinoContour != null) {
            elements.dinoPosition = new Point(dinoContour.minX, dinoContour.minY);
        }

        if (roadContour != null) {
            elements.roadY = roadContour.minY;
        }

        if (!elements.scoreContours.isEmpty()) {
            elements.scorePosition = new Point(minScoreX, scoreY);
        }

        return elements;
    }
}