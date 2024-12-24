package dino.image.processor;

import dino.image.processor.object.Blob;
import dino.image.processor.object.ObstacleAction;
import dino.util.ImageUtility;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.util.List;

public class GameImageProcessor {
    private final List<Blob> blobs;
    private final BufferedImage processedImage;

    public GameImageProcessor(BufferedImage originalImage) {
        BufferedImage imageWithoutDinoFloorAndSky = new ImageSegmentation(originalImage).removeDinoFloorAndSkyFromImage();
        BufferedImage binaryImage = new ImageUtility(imageWithoutDinoFloorAndSky).convertToBinary();
        this.processedImage = new DilateObject(binaryImage).dilate();
        this.blobs = new ObjectDetector(processedImage).detect();
        System.out.println(blobs.size() + " " + blobs);
    }

    public List<Blob> getBlobs() {
        return blobs;
    }

    public BufferedImage getProcessedImage() {
        return processedImage;
    }

    public ObstacleAction getNextAction() {
        return ObstacleAction.NONE;
    }
}
