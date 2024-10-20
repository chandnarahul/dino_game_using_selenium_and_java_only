package dino.image.processor;

import dino.image.processor.object.ObstacleAction;
import dino.image.processor.object.ObstacleType;
import dino.util.Constants;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;

public class GameCanvas {
    private final BufferedImage image;
    private ObstacleType obstacleType = ObstacleType.NONE;
    private int objectWidth = 0;
    private int objectXAxisPoint;

    static int i = 0;

    public GameCanvas(BufferedImage image) {
        ImageSegmentation imageSegmentation = new ImageSegmentation();
        ImageSegmentation.ObjectInfo objectInfo1 = imageSegmentation.processImage(removeDinoFloorAndSkyFromImage(image), 10, 0.4);
        ImageSegmentation.ObjectInfo objectInfo = imageSegmentation.dilateImage(objectInfo1.processedImage,10,0.4,3);
        this.image = objectInfo.processedImage;
        System.out.printf("start position %d width %d %n",objectInfo.startX,objectInfo.width);
        writeImageToFile(this.image, "C:\\Users\\rahulchandna\\IdeaProjects\\dino_game_using_fixed_pixels_approach_only_selenium_and_java_used\\images\\image_" + (i++) + ".png");
        //this.findObject();
    }


    private BufferedImage removeDinoFloorAndSkyFromImage(BufferedImage image) {
        int startX = 60;  // adjust to remove the dino
        int startY = 100;  // adjust to set the floor
        int width = image.getWidth() - startX;
        int height = Math.min(image.getHeight() - startY, 130);
        return image.getSubimage(startX, startY, width, height);
    }

    public void writeImageToFile(BufferedImage image, String filePath) {
        try {
            String format = filePath.substring(filePath.lastIndexOf(".") + 1);
            File outputFile = new File(filePath);
            ImageIO.write(image, format, outputFile);
            System.out.println("Image saved successfully to: " + filePath);
        } catch (IOException e) {
            System.err.println("Error saving image: " + e.getMessage());
        }
    }

    public DataBuffer imageDataBuffer() {
        return image.getRaster().getDataBuffer();
    }

    // Existing methods remain the same
    public ObstacleType obstacleType() {
        return this.obstacleType;
    }

    public boolean isLongGroundObject() {
        return objectWidth >= Constants.CLUSTERED_CACTUS_WIDTH;
    }

    public int getObjectWidth() {
        return objectWidth;
    }

    public int distanceFromObject() {
        return isLongGroundObject() ? objectXAxisPoint + objectWidth : objectXAxisPoint;
    }

    private boolean isObstacleClose() {
        return distanceFromObject() <= Constants.JUMP_SAFE_DISTANCE;
    }

    public ObstacleAction getNextObstacleAction() {

        return ObstacleAction.NONE;
    }
}
