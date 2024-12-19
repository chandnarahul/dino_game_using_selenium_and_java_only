package dino.run;

import dino.image.processor.GameCanvas;
import dino.image.processor.GameImageProcessor;
import dino.image.processor.object.ObstacleAction;
import dino.util.Constants;
import org.openqa.selenium.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SeleniumDino {
    private final WebDriver webDriver;
    private final List<GameCanvas> imageBuffers;
    private Date gameStartTime;

    public SeleniumDino(WebDriver webDriver) {
        this.webDriver = webDriver;
        this.imageBuffers = new ArrayList<>(Constants.MAX_COMMON_IMAGES); // Initialized in constructor
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
        gameStartTime = new Date();
    }

    private void gameLoop() throws Exception {
        while (true) {
            processImageAndTakeAction();
        }
    }

    private void processImageAndTakeAction() throws Exception {
        ObstacleAction nextAction = getNextAction();
        if (nextAction == ObstacleAction.JUMP) {
            performJump();
        } else if (nextAction == ObstacleAction.LOWER_THE_HEAD) {
            performDuck();
        }
    }

    private ObstacleAction getNextAction() throws IOException {
        BufferedImage screenshot = takeScreenshot();
        return new GameImageProcessor(screenshot, imageBuffers).getNextAction();
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