package dino.image.processor;

import dino.image.processor.exception.GameOverException;
import dino.util.Constants;
import dino.util.ImageUtility;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.util.ArrayList;
import java.util.List;

import static dino.util.Constants.MAX_COMMON_IMAGES;

public class GameImageTracker {
    private final List<BufferedImage> previousBufferedImages;

    public GameImageTracker() {
        this.previousBufferedImages = new ArrayList<>(Constants.MAX_COMMON_IMAGES); // Initialized in constructor
    }

    public void stopExecutionIfNoNewImageIsReceived(BufferedImage currentBufferedImage) {
        if (previousBufferedImages.size() >= MAX_COMMON_IMAGES) {
            throw new GameOverException("game over");
        }
        if (ifUniqueImage(currentBufferedImage)) {
            previousBufferedImages.clear();
        }
        previousBufferedImages.add(currentBufferedImage);
    }

    private boolean ifUniqueImage(BufferedImage currentBufferedImage) {
        for (BufferedImage previous : previousBufferedImages) {
            DataBuffer dataBuffer = previous.getRaster().getDataBuffer();
            if (dataBuffer.getNumBanks() > 0) {
                return !new ImageUtility(currentBufferedImage).isBufferedImageEqualTo(previous);
            }
        }
        return Boolean.TRUE;
    }

}
