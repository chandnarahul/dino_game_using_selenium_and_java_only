# Google Chrome Offline Mode Dino Game

## Overview
This code plays the Google Chrome offline mode Dino game.

## Current Limitations
### Identified Issues:
1. **Screenshot Accuracy:** The code works on Debian 10, but the screenshot output is incorrect on my MacBook Pro. This appears to be a Selenium driver issue, though I'm not entirely certain.
2. **Day Mode Only:** Currently, the game operates solely in day mode. This can be easily fixed by averaging half the pixels in a row to determine whether it is morning or night; I plan to address this in my next iteration.
3. **Performance Delay:** Taking screenshots takes approximately 40 to 90 milliseconds, while image processing takes under 2 milliseconds. If I implement the night mode fix, the game may still fail due to the Dino's increased speed. By the time a decision is made, the Dino may have already hit an obstacle due to the screenshot delay.

## Algorithm
1. The code uses Selenium to capture a screenshot.
2. Java's image buffer processes the screenshot to identify objects on the ground and in the air.
3. It determines the distance to the identified objects.
4. It measures the width of these objects.
5. The code then decides when to jump or duck based on the object's position.

## Getting Started
To see the code in action using the Selenium driver, follow these steps:

1. **Install Google Chrome:** Ensure that Google Chrome is installed on your machine.
2. **Run `RunChromeForManualExecution.java`:** Locate the file in your integration tests. With WebDriverManager, there is no need to manually set the driver path.
3. **Run the Program:**
    - If you're using IntelliJ, simply click the green pointer next to the `public static void main` method to run the program.

## Test Cases
All image processing test cases are written using JUnit and can be found in the `dino.image.processor` package.

## Configuration
Game configurations, such as conditions for jumping, can be updated in the `Constants.java` class.