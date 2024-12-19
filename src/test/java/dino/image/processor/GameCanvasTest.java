package dino.image.processor;

import dino.image.processor.object.ObstacleType;
import org.junit.Ignore;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GameCanvasTest {

    @Test
    public void should_calculate_ground_object_width() throws IOException {
        GameCanvas gameCanvas = new GameCanvas(ImageIO.read(new File("src/test/resources/cluster.png")));
        //assertTrue(gameCanvas.isLongGroundObject());
    }

    @Test
    public void should_calculate_ground_object_at_the_end_width() throws IOException {
        GameCanvas gameCanvas = new GameCanvas(ImageIO.read(new File("src/test/resources/cluster_at_end.png")));
        //assertTrue(gameCanvas.isLongGroundObject());
    }


    @Test(expected = AssertionError.class)
    public void should_not_calculate_flying_object_width() throws IOException {
        GameCanvas gameCanvas = new GameCanvas(ImageIO.read(new File("src/test/resources/flying.png")));
        //assertTrue(gameCanvas.isLongGroundObject());
    }

    @Test
    public void should_identify_flyingObject() throws IOException {
        GameCanvas gameCanvas = new GameCanvas(ImageIO.read(new File("src/test/resources/flying.png")));
        assertEquals(ObstacleType.BIRD, gameCanvas.obstacleType());
    }

    @Test
    @Ignore
    public void should_return_flyingObject_distance() throws IOException {
        GameCanvas gameCanvas = new GameCanvas(ImageIO.read(new File("src/test/resources/flying.png")));
        //assertTrue(gameCanvas.distanceFromObject() > 0);
    }

    @Test(expected = AssertionError.class)
    public void should_fail_since_this_is_not_a_flyingObject() throws IOException {
        GameCanvas gameCanvas = new GameCanvas(ImageIO.read(new File("src/test/resources/duck_bug.png")));
        assertEquals(ObstacleType.BIRD, gameCanvas.obstacleType());
    }

    @Test
    public void should_identify_mini_object_closer_to_the_ground_1() throws IOException {
        GameCanvas gameCanvas = new GameCanvas(ImageIO.read(new File("src/test/resources/mini_objects.png")));
        assertEquals(ObstacleType.CACTUS, gameCanvas.obstacleType());
    }

    @Test
    public void should_identify_mini_object_closer_to_the_ground_2() throws IOException {
        GameCanvas gameCanvas = new GameCanvas(ImageIO.read(new File("src/test/resources/game67.png")));
        assertEquals(ObstacleType.CACTUS, gameCanvas.obstacleType());
    }

    @Test
    @Ignore
    public void sensor_distance_should_reduce_as_object_moves_closer_to_dino() throws IOException {
        int distance = Integer.MAX_VALUE;
        for (int i = 65; i <= 74; i++) {
            String pathname = "src/test/resources/movingobject/game" + i + ".png";
            //GameCanvas gameCanvas = new GameCanvas(ImageIO.read(new File(pathname)));
            //assertTrue(gameCanvas.distanceFromObject() < distance);
            //distance = gameCanvas.distanceFromObject();
        }
    }

    @Test
    @Ignore
    public void should_return_groundObject_distance() throws IOException {
        GameCanvas gameCanvas = new GameCanvas(ImageIO.read(new File("src/test/resources/ground_object.png")));
        //assertTrue(gameCanvas.distanceFromObject() > 0);
    }
}