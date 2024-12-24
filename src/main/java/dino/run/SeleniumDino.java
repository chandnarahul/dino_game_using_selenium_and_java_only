package dino.run;

import dino.image.processor.GameImageProcessor;
import dino.image.processor.GameImageTracker;
import dino.image.processor.object.ObstacleAction;
import dino.util.ImageUtility;
import org.openqa.selenium.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;

public class SeleniumDino {
    private final WebDriver webDriver;
    private final Date gameStartTime;
    private final GameImageTracker gameImageTracker;

    public SeleniumDino(WebDriver webDriver) {
        this.webDriver = webDriver;
        this.gameImageTracker = new GameImageTracker();
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

    private void startGame() throws InterruptedException {
        performJump();
        Thread.sleep(2000); // Consider replacing with explicit waits to avoid hardcoded sleep
    }

    private void gameLoop() throws Exception {
        while (true) {
            processImageAndTakeAction();
        }
    }

    private void processImageAndTakeAction() throws Exception {
        BufferedImage screenshot = takeScreenshot();
        gameImageTracker.stopExecutionIfNoNewImageIsReceived(screenshot);
        ObstacleAction nextAction = getNextAction(screenshot);
        if (nextAction == ObstacleAction.JUMP) {
            performJump();
        } else if (nextAction == ObstacleAction.LOWER_THE_HEAD) {
            performDuck();
        }
    }

    private ObstacleAction getNextAction(BufferedImage screenshot) {
        GameImageProcessor gameImageProcessor = new GameImageProcessor(screenshot);
        saveScreenshotForDebug(gameImageProcessor);
        return gameImageProcessor.getNextAction();
    }

    private int i = 0;

    private void saveScreenshotForDebug(GameImageProcessor gameImageProcessor) {
        ImageUtility imageUtility = new ImageUtility(gameImageProcessor.getProcessedImage());
        gameImageProcessor.getBlobs().forEach(imageUtility::markBlobInImage);
        imageUtility.writeImageToFile("image_dialated_" + (i++) + ".png");
    }

    private BufferedImage takeScreenshot() throws IOException {
        WebElement gameCanvas = webDriver.findElement(By.className("runner-canvas"));
        Rectangle rect = gameCanvas.getRect();
        BufferedImage fullImage = ImageIO.read(((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE));
        return fullImage.getSubimage(rect.x, rect.y, rect.width, rect.height);
    }

    private void performJump() {
        webDriver.findElement(By.tagName("body")).sendKeys(Keys.UP);
    }

    private void performDuck() {
        webDriver.findElement(By.tagName("body")).sendKeys(Keys.DOWN);
    }

    private int calculateGameDuration() {
        return (int) (new Date().getTime() - gameStartTime.getTime());
    }
}