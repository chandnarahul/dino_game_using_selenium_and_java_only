package dino.util;

import dino.image.processor.object.GameObjectPosition;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.List;

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

    public BufferedImage convertToBinary() {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage binaryImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (isDarkPixel(x, y)) {
                    binaryImage.setRGB(x, y, Color.BLACK.getRGB());
                } else {
                    binaryImage.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }
        return binaryImage;
    }

    public boolean isDarkPixel(int x, int y) {
        int rgb = image.getRGB(x, y);
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        int grayscale = (r + g + b) / 3;
        return grayscale < 128;
    }

    public boolean isBufferedImageEqualTo(BufferedImage bufferedImage) {
        DataBuffer buffer1 = image.getRaster().getDataBuffer();
        DataBuffer buffer2 = bufferedImage.getRaster().getDataBuffer();
        if (buffer1 == buffer2) return true;
        if (buffer1 == null || buffer2 == null) return false;
        if (buffer1.getClass() != buffer2.getClass()) return false;
        if (buffer1.getSize() != buffer2.getSize()) return false;

        boolean hasGrayscalePixel1 = false;
        boolean hasGrayscalePixel2 = false;

        if (buffer1 instanceof DataBufferByte) {
            byte[] data1 = ((DataBufferByte) buffer1).getData();
            byte[] data2 = ((DataBufferByte) buffer2).getData();
            for (int i = 0; i < data1.length; i++) {
                if ((data1[i] & 0xFF) > 0 && (data1[i] & 0xFF) < 255) {
                    hasGrayscalePixel1 = true;
                }
                if ((data2[i] & 0xFF) > 0 && (data2[i] & 0xFF) < 255) {
                    hasGrayscalePixel2 = true;
                }
                if (data1[i] != data2[i]) return false;
            }
        } else if (buffer1 instanceof DataBufferInt) {
            int[] data1 = ((DataBufferInt) buffer1).getData();
            int[] data2 = ((DataBufferInt) buffer2).getData();
            for (int i = 0; i < data1.length; i++) {
                if (data1[i] > 0 && data1[i] < 0xFFFFFF) {
                    hasGrayscalePixel1 = true;
                }
                if (data2[i] > 0 && data2[i] < 0xFFFFFF) {
                    hasGrayscalePixel2 = true;
                }
                if (data1[i] != data2[i]) return false;
            }
        } else {
            for (int i = 0; i < buffer1.getSize(); i++) {
                int pixel1 = buffer1.getElem(i);
                int pixel2 = buffer2.getElem(i);

                if (pixel1 > 0 && pixel1 < 255) {
                    hasGrayscalePixel1 = true;
                }
                if (pixel2 > 0 && pixel2 < 255) {
                    hasGrayscalePixel2 = true;
                }
                if (pixel1 != pixel2) return false;
            }
        }

        // Ignore comparison if either buffer lacks grayscale pixels
        if (!hasGrayscalePixel1 || !hasGrayscalePixel2) {
            return false;
        }

        return true;
    }

}
