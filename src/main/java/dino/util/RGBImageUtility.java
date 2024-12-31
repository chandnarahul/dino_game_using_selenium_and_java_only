package dino.util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RGBImageUtility {

    private final BufferedImage image;

    public RGBImageUtility(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage convertToBinary() {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage binaryImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (isRGBDarkPixel(x, y)) {
                    binaryImage.setRGB(x, y, Color.BLACK.getRGB());
                } else {
                    binaryImage.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }
        return binaryImage;
    }

    public boolean isRGBDarkPixel(int x, int y) {
        int rgb = image.getRGB(x, y);
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        int grayscale = (r + g + b) / 3;
        return grayscale < 128;
    }

}
