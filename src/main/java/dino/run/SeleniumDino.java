package dino.run;

import dino.image.processor.DilateObject;
import dino.image.processor.GameImageTracker;
import dino.image.processor.ImageSegmentation;
import dino.image.processor.ObjectDetector;
import dino.image.processor.action.Action;
import dino.image.processor.action.ActionType;
import dino.image.processor.object.GameObjectPosition;
import dino.util.ImageUtility;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.List;

public class SeleniumDino {
    private final WebDriver webDriver;
    private final Date gameStartTime;
    private final GameImageTracker gameImageTracker;
    private int screenshot_image_index = 0;

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

    private void startGame() {
        performJump(200);
    }

    private void gameLoop() throws Exception {
        BufferedImage screenshot = takeScreenshot();
        while (gameImageTracker.shouldContinueWithGameExecution(screenshot)) {
            processImageAndTakeAction(screenshot);
        }
    }

    private void processImageAndTakeAction(BufferedImage screenshot) {
        Action nextAction = getNextAction(screenshot);
        if (nextAction.getActionType() == ActionType.JUMP) {
            performJump(nextAction.getActionDuration());
        } else if (nextAction.getActionType() == ActionType.LOWER_THE_HEAD) {
            performDuck(nextAction.getActionDuration());
        }
    }

    private Action getNextAction(BufferedImage screenshot) {
        BufferedImage imageWithoutDinoFloorAndSky = new ImageSegmentation(screenshot).removeDinoFloorAndSkyFromImage();
        BufferedImage binaryImage = new ImageUtility(imageWithoutDinoFloorAndSky).convertToBinary();
        BufferedImage dilatedImage = new DilateObject(binaryImage).dilate();
        List<GameObjectPosition> gameObjectPositions = new ObjectDetector(dilatedImage).detect();
        saveScreenshotForDebug(dilatedImage, gameObjectPositions);
        return new Action(ActionType.NONE, 0);
    }


    private void saveScreenshotForDebug(BufferedImage bufferedImage, List<GameObjectPosition> gameObjectPositions) {
        ImageUtility imageUtility = new ImageUtility(bufferedImage);
        imageUtility.addObjectDimensions(gameObjectPositions);
        imageUtility.writeImageToFile("dilated_image_" + (screenshot_image_index++) + ".png");
    }

    private BufferedImage takeScreenshot() throws IOException {
        WebElement gameCanvas = webDriver.findElement(By.className("runner-canvas"));
        Rectangle rect = gameCanvas.getRect();
        BufferedImage fullImage = ImageIO.read(((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE));
        return fullImage.getSubimage(rect.x, rect.y, rect.width, rect.height);
    }

    private void performJump(long actionDuration) {
        Actions actions = new Actions(webDriver);
        actions.keyDown(Keys.ARROW_UP)
                .pause(Duration.ofMillis(actionDuration))
                .keyUp(Keys.ARROW_UP)
                .perform();
    }

    private void performDuck(long actionDuration) {
        Actions actions = new Actions(webDriver);
        actions.keyDown(Keys.ARROW_DOWN)
                .pause(Duration.ofMillis(actionDuration))
                .keyUp(Keys.ARROW_DOWN)
                .perform();
    }

    private int calculateGameDuration() {
        return (int) (new Date().getTime() - gameStartTime.getTime());
    }
}