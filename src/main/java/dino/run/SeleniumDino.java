package dino.run;

import dino.Constants;
import dino.image.processor.object.Shape;
import dino.util.*;
import org.openqa.selenium.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

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
            int[][] inputImageArray = new RGBImageUtility(screenshot).convertGameImageToAnArray();
            List<Shape> shapes = new SceneAnalyzer(inputImageArray).analyzeScene();
            if (!shapes.isEmpty()) {
                System.out.println(shapes);
                saveScreenshotForDebug(screenshot);
            }
            if (ObjectMatch.findMatches(inputImageArray, Constants.GAME_OVER_TEMPLATE, 0.9)) {
                break;
            }
        }
    }

    private void saveScreenshotForDebug(BufferedImage bufferedImage) {
        BinaryImageUtility imageUtility = new BinaryImageUtility(bufferedImage);
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