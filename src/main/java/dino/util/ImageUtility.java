package dino.util;

import dino.image.processor.object.Blob;

import javax.imageio.ImageIO;
import java.awt.*;
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

    public void markBlobInImage(Blob blob) {
        Graphics2D g2d = this.image.createGraphics();

        // Enable antialiasing for smoother lines
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set thick stroke (5 pixels wide)
        g2d.setStroke(new BasicStroke(5.0f));
        g2d.setColor(new Color(255, 0, 0));

        // Draw thick rectangle outline around blob
        // Adding small padding (5 pixels) around the blob for better visibility
        g2d.drawRect(
                blob.leftmostX - 5,
                blob.topY - 5,
                blob.getWidth() + 9,  // Add 9 to compensate for padding on both sides
                blob.getHeight() + 9
        );

        // Draw thick crosshairs at center point
        int centerX = blob.leftmostX + (blob.getWidth() / 2);
        int centerY = blob.topY + (blob.getHeight() / 2);

        // Longer crosshairs for better visibility
        g2d.drawLine(
                centerX - 10, centerY,
                centerX + 10, centerY
        );

        g2d.drawLine(
                centerX, centerY - 10,
                centerX, centerY + 10
        );

        g2d.dispose();
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
