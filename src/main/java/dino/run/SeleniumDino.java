package dino.run;

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
            Thread.sleep(3000);
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
            int minY = Integer.MAX_VALUE, maxY = 0, previousMinY = Integer.MAX_VALUE;
            for (int y = inputImageArray.length - 1; y >= 0; y--) {
                int x = 0;
                for (; x < inputImageArray[0].length / 10; x++) {
                    if (inputImageArray[y][x] == 1 && maxY == 0) {
                        maxY = y;
                    } else if (inputImageArray[y][x] == 1 && y < minY) {
                        minY = y;
                    }
                }
                if (minY == previousMinY && minY != Integer.MAX_VALUE) {
                    while (inputImageArray[previousMinY][x] != 0) {
                        x++;
                    }
                    System.out.println(String.format("minY %d maxY %d x %d", minY, maxY, x));
                    break;
                }
                previousMinY = minY;
            }
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