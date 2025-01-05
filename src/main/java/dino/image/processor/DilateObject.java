package dino.image.processor;

public class DilateObject {
    private final int[][] image;
    private final int height;
    private final int width;

    public DilateObject(int[][] image) {
        this.image = image;
        this.height = image.length;
        this.width = image[0].length;
    }

    public int[][] dilate() {
        // Create a new image to store the dilated result
        int[][] dilatedImage = new int[height][width];

        // Dilation kernel (structuring element)
        int kernelSize = 10;
        int[][] kernel = new int[kernelSize][kernelSize];
        for(int i=0;i<kernel.length;i++){
            for(int j=0;j<kernel[0].length;j++){
                kernel[i][j]=1;
            }
        }

        // Iterate through each pixel
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Check if current pixel needs dilation
                boolean isDilationRequired = isDilationRequired(x, y, kernel);
                // Set the pixel color
                if (isDilationRequired) {
                    dilatedImage[y][x] = 1;
                }
            }
        }

        return dilatedImage;
    }

    private boolean isDilationRequired(int centerX, int centerY, int[][] kernel) {
        int kernelSize = kernel.length;
        int kernelOffset = kernelSize / 2;

        // Check neighboring pixels based on kernel
        for (int dy = -kernelOffset; dy < kernelOffset; dy++) {
            for (int dx = -kernelOffset; dx < kernelOffset; dx++) {
                int newX = centerX + dx;
                int newY = centerY + dy;

                // Adjust kernel indexing to avoid out of bounds
                int kernelY = dy + kernelOffset;
                int kernelX = dx + kernelOffset;

                // Ensure kernel indices are within bounds
                if (kernelY >= 0 && kernelY < kernelSize &&
                        kernelX >= 0 && kernelX < kernelSize) {

                    // Check if neighborhood pixel is within image bounds
                    if (newX >= 0 && newX < width &&
                            newY >= 0 && newY < height) {

                        // If any neighboring pixel is black and kernel supports it
                        if (image[newY][newX] == 1 && kernel[kernelY][kernelX] == 1) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
}
