package integration.tests;

import dino.run.SeleniumDino;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.nio.file.Path;
import java.nio.file.Paths;

public class RunChromeForManualExecution {

    public static void main(String[] args) throws InterruptedException {
        WebDriverManager.firefoxdriver().setup();
        WebDriver webDriver = new FirefoxDriver();
        Path sampleFile = Paths.get("dino_game.html");
        webDriver.get(sampleFile.toUri().toString());
        webDriver.manage().window().setSize(new Dimension(500, 450));
        Thread.sleep(1000);
        System.out.println(new SeleniumDino(webDriver).run());
    }
}