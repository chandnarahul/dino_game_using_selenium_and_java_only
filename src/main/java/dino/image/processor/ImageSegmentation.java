package dino.image.processor;

import java.awt.image.BufferedImage;

public class ImageSegmentation {
    private final BufferedImage image;

    public ImageSegmentation(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage removeDinoFloorAndSkyFromImage() {
        int startX = 60;  // adjust to remove the dino
        int width = image.getWidth() - startX;
        int height = image.getHeight() - 65;
        return image.getSubimage(startX, 36, width, height);
    }
}