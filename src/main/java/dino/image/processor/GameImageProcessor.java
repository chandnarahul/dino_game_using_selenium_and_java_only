package dino.image.processor;

import dino.image.processor.exception.GameOverException;
import dino.image.processor.object.ObstacleAction;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import java.util.List;

import static dino.util.Constants.MAX_COMMON_IMAGES;

public class GameImageProcessor {

    private final GameCanvas gameCanvas;
    private final List<GameCanvas> imageBuffers;

    public GameImageProcessor(BufferedImage image, List<GameCanvas> imageBuffers) {
        this.gameCanvas = new GameCanvas(image);
        this.imageBuffers = imageBuffers;
        imageBuffers.add(gameCanvas);
        stopExecutionIfNoNewImageIsReceived();
    }

    public ObstacleAction getNextAction() {
        return gameCanvas.getNextObstacleAction();
    }

    private void stopExecutionIfNoNewImageIsReceived() {
        if (imageBuffers.size() == MAX_COMMON_IMAGES) {
            throw new GameOverException("game over");
        } else if (ifUniqueImage()) {
            imageBuffers.clear();
        }
    }

    private boolean ifUniqueImage() {
        for (GameCanvas previous : imageBuffers) {
            DataBuffer dataBuffer = previous.imageDataBuffer();
            if (dataBuffer.getNumBanks() > 0) {
                return !areDataBuffersEqual(gameCanvas.imageDataBuffer(), dataBuffer);
            }
        }
        return Boolean.TRUE;
    }

    public static boolean areDataBuffersEqual(DataBuffer buffer1, DataBuffer buffer2) {
        if (buffer1 == buffer2) return true;
        if (buffer1 == null || buffer2 == null) return false;
        if (buffer1.getClass() != buffer2.getClass()) return false;
        if (buffer1.getSize() != buffer2.getSize()) return false;

        if (buffer1 instanceof DataBufferByte) {
            return Arrays.equals(((DataBufferByte) buffer1).getData(), ((DataBufferByte) buffer2).getData());
        } else if (buffer1 instanceof DataBufferInt) {
            return Arrays.equals(((DataBufferInt) buffer1).getData(), ((DataBufferInt) buffer2).getData());
        } else {
            for (int i = 0; i < buffer1.getSize(); i++) {
                if (buffer1.getElem(i) != buffer2.getElem(i)) {
                    return false;
                }
            }
            return true;
        }
    }
}
