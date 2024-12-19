package dino.image.processor;

import dino.image.processor.exception.GameOverException;
import dino.image.processor.object.ObstacleAction;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
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

        boolean hasGrayscalePixel1 = false;
        boolean hasGrayscalePixel2 = false;

        if (buffer1 instanceof DataBufferByte && buffer2 instanceof DataBufferByte) {
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
        } else if (buffer1 instanceof DataBufferInt && buffer2 instanceof DataBufferInt) {
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
