package dino.image.processor;

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

    public Boolean shouldContinueWithGameExecution(BufferedImage currentBufferedImage) {
        if (previousBufferedImages.size() >= MAX_COMMON_IMAGES) {
            return Boolean.FALSE;
        }
        if (ifUniqueImage(currentBufferedImage)) {
            previousBufferedImages.clear();
        }
        previousBufferedImages.add(currentBufferedImage);
        return Boolean.TRUE;
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
