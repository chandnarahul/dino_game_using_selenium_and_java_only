package dino.util;

import dino.image.processor.object.GameObjectPosition;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class BinaryImageUtility {

    private final BufferedImage image;

    public BinaryImageUtility(BufferedImage image) {
        this.image = image;
    }


    public void addObjectDimensions(List<GameObjectPosition> gameObjectPositions) {
        Graphics2D g2d = this.image.createGraphics();
        // Enable antialiasing for smoother lines
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Set thick stroke (5 pixels wide)
        g2d.setStroke(new BasicStroke(5.0f));
        g2d.setColor(new Color(0, 0, 0));

        gameObjectPositions.forEach(gameObjectPosition -> {
            g2d.drawRect(
                    gameObjectPosition.getLeftmostX() - 5,
                    gameObjectPosition.getTopY() - 5,
                    gameObjectPosition.getWidth() + 9,  // Add 9 to compensate for padding on both sides
                    gameObjectPosition.getHeight() + 9
            );

            // Draw thick crosshairs at center point
            int centerX = gameObjectPosition.getLeftmostX() + (gameObjectPosition.getWidth() / 2);
            int centerY = gameObjectPosition.getTopY() + (gameObjectPosition.getHeight() / 2);

            // Longer lines for better visibility
            g2d.drawLine(
                    centerX - 10, centerY,
                    centerX + 10, centerY
            );

            g2d.drawLine(
                    centerX, centerY - 10,
                    centerX, centerY + 10
            );
        });
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


    public boolean isDarkPixel(int x, int y) {
        DataBufferByte buffer = (DataBufferByte) image.getRaster().getDataBuffer();
        byte[] data = buffer.getData();  // The byte array that holds pixel data
        int width = image.getWidth();
        // Calculate the index of the pixel in the byte array
        int pixelIndex = y * width + x;
        // Calculate the byte index and bit position for the pixel
        int byteIndex = pixelIndex / 8;
        int bitPosition = pixelIndex % 8;
        // Get the byte that holds the pixel's bit
        byte pixelByte = data[byteIndex];
        // Create a mask to isolate the specific bit for the pixel
        byte mask = (byte) (1 << (7 - bitPosition));
        // Check if the bit is 0 (black) or 1 (white)
        return (pixelByte & mask) == 0;  // If the bit is 0, it's black; if 1, it's white
    }

}
