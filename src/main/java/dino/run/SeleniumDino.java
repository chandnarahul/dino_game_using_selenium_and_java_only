package dino.run;

import dino.image.processor.ObjectDetector;
import dino.image.processor.object.DinoLocation;
import dino.util.BinaryImageUtility;
import dino.util.RGBImageUtility;
import dino.util.SeleniumAction;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

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
            Thread.sleep(4000);
            RGBImageUtility rgbImageUtility = new RGBImageUtility(takeScreenshot());
            DinoLocation dinoLocation = new ObjectDetector(rgbImageUtility.convertToAnArray()).identifyDinoLocation();
            gameLoop(dinoLocation);  // Extracted game loop to its own method for clarity
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

    private void gameLoop(DinoLocation dinoLocation) throws Exception {
        while (true) {
            int[][] inputImageArray = new RGBImageUtility(takeScreenshot()).convertGameImageToAnArray(dinoLocation);
            BinaryImageUtility.printArray(inputImageArray);
            break;
        }
    }

    private void saveScreenshotForDebug(BufferedImage bufferedImage) {
        BinaryImageUtility imageUtility = new BinaryImageUtility(bufferedImage);
        imageUtility.writeImageToFile("binary_image_" + (screenshot_image_index++) + ".png");
    }

    private BufferedImage takeScreenshot() throws IOException {
        return ImageIO.read(((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE));
    }

    private long calculateGameDuration() {
        return (System.currentTimeMillis() - gameStartTime);
    }
}