package dino.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageUtility {

    private final BufferedImage image;

    public ImageUtility(BufferedImage image) {
        this.image = image;
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


    public void writeImageToFile(String filePath) {
        try {
            File outputFile = new File("images" + File.separator + filePath);
            ImageIO.write(image, "png", outputFile);
            System.out.println("Image saved successfully to: " + filePath);
        } catch (IOException e) {
            System.err.println("Error saving image: " + e.getMessage());
        }
    }
}
