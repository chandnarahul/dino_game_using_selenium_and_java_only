package dino.run;

import dino.image.processor.ImageSegmentation;
import dino.image.processor.object.GameObjectPosition;
import dino.util.BinaryImageUtility;
import dino.util.SeleniumAction;
import org.openqa.selenium.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static dino.Constants.MAX_COMMON_OBJECTS;

public class SeleniumDino {
    private final WebDriver webDriver;
    private final long gameStartTime;
    private int screenshot_image_index = 0;

    public SeleniumDino(WebDriver webDriver) {
        this.webDriver = webDriver;
        this.gameStartTime = System.currentTimeMillis();
    }

    public long run() {
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
        SeleniumAction.jump(webDriver, 200);
    }

    private void gameLoop() throws Exception {
        while (true) {
            BufferedImage screenshot = takeScreenshot();
            BufferedImage imageWithoutDinoFloorAndSky = new ImageSegmentation(screenshot).removeDinoFloorAndSkyFromImage();
            //BufferedImage binaryImage = new RGBImageUtility(imageWithoutDinoFloorAndSky).convertToBinary();
            //BufferedImage dilatedImage = new DilateObject(binaryImage).dilate();

            //ObjectDetector detector = new ObjectDetector(dilatedImage);
            //List<GameObjectPosition> gameObjectPositions = detector.detect();
            List<GameObjectPosition> gameObjectPositions = Collections.EMPTY_LIST;
            //saveScreenshotForDebug(binaryImage, gameObjectPositions);
            // If no objects are detected, skip the rest of the loop
            /*if (gameObjectPositions.isEmpty()) {
                continue;
            }
            if (hasConsecutiveObjectsWithSameYPosition(gameObjectPositions)) {
                break;
            }
            System.out.println("decision"+gameObjectPositions.get(0));*/
            //System.out.println(ContourDetector.detectGameElements(binaryImage));
        }
    }

    private boolean hasConsecutiveObjectsWithSameYPosition(List<GameObjectPosition> gameObjectPositions) {
        int similarObjectCount = 0;
        int topYPosition = -1;  // Initialize to a value that cannot match initially
        for (GameObjectPosition gameObjectPosition : gameObjectPositions) {
            if (topYPosition == gameObjectPosition.getTopY()) {
                similarObjectCount++;
            } else {
                similarObjectCount = 0;
                topYPosition = gameObjectPosition.getTopY();
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
        imageUtility.writeImageToFile("binary_image_" + (screenshot_image_index++) + ".png");
    }

    private BufferedImage takeScreenshot() throws IOException {
        WebElement gameCanvas = webDriver.findElement(By.className("runner-canvas"));
        Rectangle rect = gameCanvas.getRect();
        BufferedImage fullImage = ImageIO.read(((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE));
        return fullImage.getSubimage(rect.x, rect.y, rect.width, rect.height);
    }

    private long calculateGameDuration() {
        return (System.currentTimeMillis() - gameStartTime);
    }
}