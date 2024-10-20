package dino.util;

import java.awt.image.BufferedImage;

public class ImageUtility {

    private final BufferedImage image;

    public ImageUtility(BufferedImage image) {
        this.image = image;
    }

    public boolean isAnyPixelFoundAtTop(int i) {
        return isGrayPixel(i, 0);
    }

    public boolean isGrayPixel(int xAxis, int yAxix) {
        int rgb = image.getRGB(xAxis, yAxix);

        // Extract Red, Green, and Blue components from the RGB value
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;

        // Calculate the luminance (grayscale) value using the luminance formula
        int grayscale = (int) (0.299 * red + 0.587 * green + 0.114 * blue);

        return grayscale < Constants.GRAY_SCALE_PIXEL_COLOR;
    }
}
