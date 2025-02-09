package dino.run;

import dino.image.processor.ObjectDetector;
import dino.image.processor.object.DinoLocation;
import dino.image.processor.object.Shape;
import dino.util.BinaryImageUtility;
import dino.util.RGBImageUtility;
import dino.util.SceneAnalyzer;
import dino.util.SeleniumAction;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeleniumDino {
    private static final int MAX_STATIONARY_ITERATIONS = 10;
    private final WebDriver webDriver;
    private final long gameStartTime;
    private int screenshot_image_index = 0;
    private Map<Integer, Integer> previousPositions = new HashMap<>();
    private int stationaryCount = 0;

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
            gameLoop(dinoLocation);
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
            RGBImageUtility rgbImageUtility = new RGBImageUtility(takeScreenshot());
            int[][] imageArray = rgbImageUtility.convertToAnArray();
            int[][] inputImageArray = rgbImageUtility.processImage(dinoLocation, imageArray);
            List<Shape> shapes = new SceneAnalyzer(inputImageArray).analyzeScene();
            shapes.forEach(System.out::println);

            if (isGameOver(shapes)) {
                System.out.println("Game Over: Objects are not moving anymore.");
                break;
            }
        }
    }

    private boolean isGameOver(List<Shape> shapes) {
        boolean allStationary = true;
        for (Shape shape : shapes) {
            int id = shape.getId();
            int currentPosition = shape.getxFromDino();
            if (previousPositions.containsKey(id)) {
                if (previousPositions.get(id) != currentPosition) {
                    allStationary = false;
                    stationaryCount = 0;
                }
            }
            previousPositions.put(id, currentPosition);
        }
        if (allStationary) {
            stationaryCount++;
        } else {
            stationaryCount = 0;
        }
        return stationaryCount >= MAX_STATIONARY_ITERATIONS;
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