package integration.tests;

import dino.run.SeleniumDino;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.nio.file.Path;
import java.nio.file.Paths;

public class RunChromeForManualExecution {

    public static void main(String[] args) throws InterruptedException {
        //WebDriver webDriver=chrome();
        WebDriver webDriver=firefox();
        Path sampleFile = Paths.get("dino_game.html");
        webDriver.get(sampleFile.toUri().toString());
        webDriver.manage().window().setSize(new Dimension(500, 450));
        Thread.sleep(1000);
        System.out.println(new SeleniumDino(webDriver).run());
    }

    private static WebDriver firefox() {
        WebDriverManager.firefoxdriver().setup();
        return new FirefoxDriver();
    }

    public static WebDriver chrome() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-gpu"); // Disable GPU rendering
        options.addArguments("--disable-extensions"); // Disable extensions
        options.addArguments("--disable-blink-features=AutomationControlled"); // Avoid detection as a bot
        options.addArguments("--disable-infobars"); // Disable infobars
        //options.addArguments("--headless"); // Optional: Run in headless mode for better performance
        return new ChromeDriver(options);
    }
}