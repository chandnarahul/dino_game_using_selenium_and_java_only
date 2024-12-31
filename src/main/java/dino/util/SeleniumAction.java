package dino.util;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;

public class SeleniumAction {

    public static void performJump(WebDriver webDriver, long actionDuration) {
        Actions actions = new Actions(webDriver);
        actions.keyDown(Keys.ARROW_UP)
                .pause(Duration.ofMillis(actionDuration))
                .keyUp(Keys.ARROW_UP)
                .perform();
    }

    public static void performDuck(WebDriver webDriver, long actionDuration) {
        Actions actions = new Actions(webDriver);
        actions.keyDown(Keys.ARROW_DOWN)
                .pause(Duration.ofMillis(actionDuration))
                .keyUp(Keys.ARROW_DOWN)
                .perform();
    }
}
