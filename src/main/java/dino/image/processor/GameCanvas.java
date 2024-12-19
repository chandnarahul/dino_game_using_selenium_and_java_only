package dino.image.processor;

import dino.image.processor.object.ObstacleAction;
import dino.image.processor.object.ObstacleType;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.util.List;

public class GameCanvas {
    private final BufferedImage image;
    private final ObstacleType obstacleType = ObstacleType.NONE;
    private final List<MultipleBlobDetector.Blob> blobs;

    public GameCanvas(BufferedImage originalImage) {
        ImageSegmentation imageSegmentation = new ImageSegmentation();
        this.image = imageSegmentation.convertToBinary(imageSegmentation.removeDinoFloorAndSkyFromImage(originalImage));
        this.blobs = new MultipleBlobDetector().countBlobsAfterDilation(this.image);
        System.out.println(blobs.size());
    }

    public DataBuffer imageDataBuffer() {
        return image.getRaster().getDataBuffer();
    }

    // Existing methods remain the same
    public ObstacleType obstacleType() {
        return this.obstacleType;
    }

    public ObstacleAction getNextObstacleAction() {

        return ObstacleAction.NONE;
    }
}
