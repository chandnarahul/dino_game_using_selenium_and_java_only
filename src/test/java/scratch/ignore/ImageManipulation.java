package scratch.ignore;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.io.IOException;

public class ImageManipulation {
    private static BufferedImage gerGreyScaleImageViaGraphics(BufferedImage image) {
        BufferedImage greyScaleImage = new BufferedImage(image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_USHORT_GRAY);
        Graphics g = greyScaleImage.getGraphics();
        g.drawImage(greyScaleImage, 0, 0, null);
        g.dispose();
        return greyScaleImage;
    }

    private static BufferedImage getGreyScaleImageViaFilter(BufferedImage image) {
        ImageFilter filter = new GrayFilter(true, 50);
        ImageProducer producer = new FilteredImageSource(image.getSource(), filter);
        return toBufferedImage(Toolkit.getDefaultToolkit().createImage(producer));
    }

    private static void removeLowerLine(BufferedImage image) {
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.drawLine(0, 132, image.getWidth(), 132);
        g2d.dispose();
    }

    private static void markDinoInImage(BufferedImage image) throws IOException {
        // Draw on the buffered image
        // x = 127
        // y = 335
        // width = 45
        // height = 45
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.red);
        g2d.fill(new Rectangle(20, 92, 45, 47));
        g2d.dispose();
    }

    private static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }
}
