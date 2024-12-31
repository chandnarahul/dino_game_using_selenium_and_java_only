package dino.run;

import dino.image.processor.DilateObject;
import dino.image.processor.ImageSegmentation;
import dino.image.processor.ObjectDetector;
import dino.image.processor.object.GameObjectPosition;
import dino.util.BinaryImageUtility;
import dino.util.RGBImageUtility;
import dino.util.SeleniumAction;
import org.openqa.selenium.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static dino.util.Constants.MAX_COMMON_OBJECTS;

public class SeleniumDino {
    private final WebDriver webDriver;
    private final Date gameStartTime;
    private int screenshot_image_index = 0;

    public SeleniumDino(WebDriver webDriver) {
        this.webDriver = webDriver;
        this.gameStartTime = new Date();
    }

    public int run() {
        try {
            startGame();
            gameLoop();  // Extracted game loop to its own method for clarity
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            webDriver.quit();
        }
        return calculateGameDuration();
    }

    private void startGame() {
        SeleniumAction.performJump(webDriver, 200);
    }

    private void gameLoop() throws Exception {
        while (true) {
            BufferedImage screenshot = takeScreenshot();
            BufferedImage imageWithoutDinoFloorAndSky = new ImageSegmentation(screenshot).removeDinoFloorAndSkyFromImage();
            BufferedImage binaryImage = new RGBImageUtility(imageWithoutDinoFloorAndSky).convertToBinary();
            BufferedImage dilatedImage = new DilateObject(binaryImage).dilate();
            ObjectDetector detector = new ObjectDetector(dilatedImage);
            List<GameObjectPosition> gameObjectPositions = detector.detect();
            saveScreenshotForDebug(dilatedImage, gameObjectPositions);
            // If no objects are detected, skip the rest of the loop
            if (gameObjectPositions.isEmpty()) {
                continue;
            }
            if (hasConsecutiveObjectsWithSameYPosition(gameObjectPositions)) {
                break;
            }
        }
    }

    private boolean hasConsecutiveObjectsWithSameYPosition(List<GameObjectPosition> gameObjectPositions) {
        int similarObjectCount = 0;
        int topYPosition = -1;  // Initialize to a value that cannot match initially
        for (GameObjectPosition object : gameObjectPositions) {
            if (topYPosition == object.getTopY()) {
                similarObjectCount++;
            } else {
                similarObjectCount = 0;
                topYPosition = object.getTopY();
            }
            if (similarObjectCount > MAX_COMMON_OBJECTS) {
                return true;
            }
        }
        return false;
    }

    private void saveScreenshotForDebug(BufferedImage bufferedImage, List<GameObjectPosition> gameObjectPositions) {
        BinaryImageUtility imageUtility = new BinaryImageUtility(bufferedImage);
        imageUtility.addObjectDimensions(gameObjectPositions);
        imageUtility.writeImageToFile("dilated_image_" + (screenshot_image_index++) + ".png");
    }

    private BufferedImage takeScreenshot() throws IOException {
        WebElement gameCanvas = webDriver.findElement(By.className("runner-canvas"));
        Rectangle rect = gameCanvas.getRect();
        BufferedImage fullImage = ImageIO.read(((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE));
        return fullImage.getSubimage(rect.x, rect.y, rect.width, rect.height);
    }

    private int calculateGameDuration() {
        return (int) (new Date().getTime() - gameStartTime.getTime());
    }
}