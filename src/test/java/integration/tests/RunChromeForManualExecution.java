package integration.tests;

import dino.run.SeleniumDino;
import dino.util.SeleniumRobot;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class RunChromeForManualExecution {

    public static void main(String[] args) throws Exception {
        WebDriverManager.chromedriver().setup();
        WebDriver webDriver = new ChromeDriver();
        webDriver.get("data:text/html,<html></html>");
        Thread.sleep(5000);
        new SeleniumRobot().openDinoGameUsingRobotKeys();
        webDriver.manage().window().setSize(new Dimension(500, 450));
        new SeleniumDino(webDriver).run();
    }
}