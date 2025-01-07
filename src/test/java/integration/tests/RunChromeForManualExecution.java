package integration.tests;

import dino.run.SeleniumDino;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.nio.file.Path;
import java.nio.file.Paths;

public class RunChromeForManualExecution {

    public static void main(String[] args) throws InterruptedException {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-gpu"); // Disable GPU rendering
        options.addArguments("--disable-extensions"); // Disable extensions
        options.addArguments("--disable-blink-features=AutomationControlled"); // Avoid detection as a bot
        options.addArguments("--disable-infobars"); // Disable infobars
        //options.addArguments("--headless"); // Optional: Run in headless mode for better performance

        Path sampleFile = Paths.get("dino_game.html");
        WebDriver webDriver = new ChromeDriver(options);
        webDriver.get(sampleFile.toUri().toString());
        webDriver.manage().window().setSize(new Dimension(500, 450));
        Thread.sleep(1000);
        System.out.println(new SeleniumDino(webDriver).run());
    }
}