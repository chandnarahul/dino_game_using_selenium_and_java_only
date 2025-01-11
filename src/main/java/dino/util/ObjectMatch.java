package dino.util;

public class ObjectMatch {
    public static boolean findMatches(int[][] image, int[][] template, double threshold) {
        int imgHeight = image.length;
        int imgWidth = image[0].length;
        int tplHeight = template.length;
        int tplWidth = template[0].length;

        // Slide template over image
        for (int y = 0; y <= imgHeight - tplHeight; y++) {
            for (int x = 0; x <= imgWidth - tplWidth; x++) {
                double score = computeScore(image, template, x, y);
                if (score >= threshold) {
                    return true;
                }
            }
        }
        return false;
    }

    private static double computeScore(int[][] image, int[][] template, int offsetX, int offsetY) {
        int matches = 0;
        int total = 0;

        for (int y = 0; y < template.length; y++) {
            for (int x = 0; x < template[0].length; x++) {
                if (template[y][x] == 1) {
                    total++;
                    if (image[offsetY + y][offsetX + x] == 1) {
                        matches++;
                    }
                }
            }
        }
        return total > 0 ? (double) matches / total : 0.0;
    }
}