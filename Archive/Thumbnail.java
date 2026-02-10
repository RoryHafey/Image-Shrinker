/* Apave Image Shrinker - Thumbnail
 * Rory Hafey (roryngh@hotmail.com)
 * Apave Newcastle
 * 2025
 */

 // Import relevant libraries

package imageShrink;

import java.io.*;
import java.util.regex.*;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;

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

    // Constructor 
    public Thumbnail(String inputImagePath, JPanel displayDirectory) {
        this.inputImagePath = inputImagePath;
        this.displayDirectory = displayDirectory;
    }
     
    
    @Override
    public void run() {
        try{
            // reads input image
            File inputFile = new File(this.inputImagePath);
            this.inputImage = ImageIO.read(inputFile);
        }
        catch(IOException e){
            System.out.println("Error opening file for image thumbnail");
        }

        oldWidth = inputImage.getWidth();
        oldHeight = inputImage.getHeight();

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
        catch(IllegalArgumentException e){
            System.out.println("Error making image thumbnail");
        }
        
        ImageIcon icon = new ImageIcon(outputImage);  //shrink to appropriate size
        JLabel tempLabel = new JLabel(icon);
        tempLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 5));                    
        displayDirectory.add(tempLabel);
        displayDirectory.setVisible(false); //refresh the display
        displayDirectory.setVisible(true);
    }
}