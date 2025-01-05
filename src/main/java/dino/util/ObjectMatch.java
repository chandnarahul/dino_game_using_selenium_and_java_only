package dino.util;

import dino.image.processor.DilateObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ObjectMatch {
    public static class Match {
        public final int x, y;
        public final double score;

        public Match(int x, int y, double score) {
            this.x = x;
            this.y = y;
            this.score = score;
        }
    }

    public static int[][] imageToArray(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        int[][] result = new int[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color c = new Color(img.getRGB(x, y));
                result[y][x] = (c.getRed() < 128) ? 1 : 0;
            }
        }
        return result;
    }

    public static List<Match> findMatches(int[][] image, int[][] template, double threshold) {
        List<Match> matches = new ArrayList<>();
        int imgHeight = image.length;
        int imgWidth = image[0].length;
        int tplHeight = template.length;
        int tplWidth = template[0].length;

        // Slide template over image
        for (int y = 0; y <= imgHeight - tplHeight; y++) {
            for (int x = 0; x <= imgWidth - tplWidth; x++) {
                double score = computeScore(image, template, x, y);
                if (score >= threshold) {
                    matches.add(new Match(x, y, score));
                }
            }
        }
        return matches;
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

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        BufferedImage input = ImageIO.read(new File("samples/dilated_image_168.png"));
        int[][] inputImageArray = new DilateObject(new RGBImageUtility(input).convertToAnArray()).dilate();
        for (int y = 0; y < inputImageArray.length; y++) {
            for (int x = 0; x < inputImageArray[0].length; x++) {
                System.out.print(inputImageArray[y][x]);
            }
            System.out.println();
        }
        /*
        List<Match> matches = findMatches(inputImageArray, imageToArray(new RGBImageUtility(ImageIO.read(new File("dino.png"))).convertToBinary()), 0.9);
        for (Match m : matches) {
            System.out.printf("Match found at (%d, %d) with score %.2f%n", m.x, m.y, m.score);
        }
        System.out.println("--------------");
        matches = findMatches(inputImageArray, imageToArray(new RGBImageUtility(ImageIO.read(new File("score.png"))).convertToBinary()), 0.5);
        for (Match m : matches) {
            System.out.printf("Match found at (%d, %d) with score %.2f%n", m.x, m.y, m.score);
        }

         */
        System.out.println(System.currentTimeMillis() - start);
    }
}