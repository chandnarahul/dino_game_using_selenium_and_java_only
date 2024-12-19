package dino.image.processor;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageSegmentation {

    public BufferedImage removeDinoFloorAndSkyFromImage(BufferedImage image) {
        int startX = 60;  // adjust to remove the dino
        int width = image.getWidth() - startX;
        int height = image.getHeight() - 65;
        return image.getSubimage(startX, 36, width, height);
    }

    public BufferedImage convertToBinary(BufferedImage input) {
        int width = input.getWidth();
        int height = input.getHeight();
        BufferedImage binaryImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (isDarkPixel(input, x, y)) {
                    binaryImage.setRGB(x, y, Color.BLACK.getRGB());
                } else {
                    binaryImage.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }
        return binaryImage;
    }

    public boolean isDarkPixel(BufferedImage input, int x, int y) {
        int rgb = input.getRGB(x, y);
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        int grayscale = (r + g + b) / 3;
        return grayscale < 128;
    }
}