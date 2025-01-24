package dino.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BinaryImageUtility {

    private final BufferedImage image;

    public BinaryImageUtility(BufferedImage image) {
        this.image = image;
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
