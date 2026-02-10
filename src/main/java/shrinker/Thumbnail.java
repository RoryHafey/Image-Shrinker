/* Apave Image Shrinker - Thumbnail
 * Rory Hafey (roryngh@hotmail.com)
 * Apave Newcastle
 * 2025
 */

// Import relevant libraries

package shrinker;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.lang.OutOfMemoryError;
import java.lang.NullPointerException;

// Class to make thumbnail images for the display. No file output, outputs to the display instead
public class Thumbnail implements Runnable
{
    // Declare variables
    int scaledWidth;
    int scaledHeight;
    int oldWidth;
    int oldHeight;
    double scaler = 1.0;
    BufferedImage outputImage = new BufferedImage(1, 1, 5);
    String inputImagePath;
    JPanel displayDirectory;
    BufferedImage inputImage;
    Counter counter;

    // Constructor 
    public Thumbnail(String inputImagePath, JPanel displayDirectory, Counter counter) {
        this.inputImagePath = inputImagePath;
        this.displayDirectory = displayDirectory;
        this.counter = counter;
    }


    @Override
    public void run() {
        try{
            // reads input image
            File inputFile = new File(this.inputImagePath);
            this.inputImage = ImageIO.read(inputFile);

            oldWidth = inputImage.getWidth();
            oldHeight = inputImage.getHeight();
        }
        catch(IOException | OutOfMemoryError | NullPointerException e){
            System.out.println("Error making image thumbnail for image" +  inputImagePath);
        }

        if (oldHeight < oldWidth){
            scaler = 150.0 / oldWidth;
        }
        else{
            scaler = 150.0 / oldHeight;
        }

        scaledHeight = (int)(scaler * oldHeight);
        scaledWidth = (int)(scaler * oldWidth);

        int imgType = inputImage.getType();

        if (imgType == 0){
            imgType = 5;
        }

        try{
            // creates output image
            outputImage = new BufferedImage(scaledWidth, scaledHeight, imgType);

            // scales the input image to the output image
            Graphics2D g2d = outputImage.createGraphics();
            g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
            g2d.dispose();
        }
        catch(IllegalArgumentException|NullPointerException e){
            System.out.println("Error making image thumbnail for image" +  inputImagePath);
        }
        catch (OutOfMemoryError memoryError){
            System.out.println(memoryError);
        }

        ImageIcon icon = new ImageIcon(outputImage);  //shrink to appropriate size
        JLabel tempLabel = new JLabel(icon);
        tempLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 5));
        displayDirectory.add(tempLabel);
        displayDirectory.setVisible(false); //refresh the display
        displayDirectory.setVisible(true);

        counter.incDone();
    }



}