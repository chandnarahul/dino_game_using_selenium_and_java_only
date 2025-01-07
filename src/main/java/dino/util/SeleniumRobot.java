package dino.util;

import java.awt.*;
import java.awt.event.KeyEvent;

public class SeleniumRobot {

    public void openDinoGameUsingRobotKeys() throws Exception {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_F6);
        robot.keyRelease(KeyEvent.VK_F6);
        Thread.sleep(500);
        String url = "chrome://dino";
        for (char c : url.toLowerCase().toCharArray()) {
            typeCharacter(robot, c);
            Thread.sleep(100);
        }
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
    }

    private void typeCharacter(Robot robot, char c) {
        if (c >= 'a' && c <= 'z') {
            robot.keyPress(KeyEvent.VK_A + (c - 'a'));
            robot.keyRelease(KeyEvent.VK_A + (c - 'a'));
        } else {
            switch (c) {
                case ':':
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.VK_SEMICOLON);
                    robot.keyRelease(KeyEvent.VK_SEMICOLON);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                    break;
                case '/':
                    robot.keyPress(KeyEvent.VK_SLASH);
                    robot.keyRelease(KeyEvent.VK_SLASH);
                    break;
                default:
                    System.out.println("Unsupported character: " + c);
                    break;
            }
        }
    }
}
