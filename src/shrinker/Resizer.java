/* Image Shrinker - Resizer
 * Rory Hafey (roryngh@hotmail.com)
 * 2025
 */


// Import relevant libraries

package shrinker;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.*;


// Class to make resized images for the display. Outputs to file
public class Resizer implements Runnable
{
    // declare variables
    int scaledWidth;
    int scaledHeight;
    int oldWidth;
    int oldHeight;
    double scaler = 1.0;
    String inputImagePath;
    String outputImagePath;
    double reqSize;
    BufferedImage inputImage;
    Counter counter;

    // resize the images according to the scaling required
    public Resizer(String inputImagePath, String outputImagePath, double reqSize, Counter counter){
        this.inputImagePath = inputImagePath;
        this.outputImagePath = outputImagePath;
        this.reqSize = reqSize;
        this.counter = counter;
    }

    @Override
    public void run() {
        try{
            // reads input image
            File inputFile = new File(inputImagePath);
            inputImage = ImageIO.read(inputFile);

            //gets the dimensions
            oldWidth = inputImage.getWidth();
            oldHeight = inputImage.getHeight();

            // Don't resize if it's too small, also determine the orientation and scaling factor for the image
            if (oldHeight >= reqSize || oldWidth >= reqSize){
                if (oldHeight < oldWidth){
                    scaler = reqSize / oldWidth;
                }
                else{
                    scaler = reqSize / oldHeight;
                }

                // scale the output sizes
                scaledHeight = (int)(scaler * oldHeight);
                scaledWidth = (int)(scaler * oldWidth);

                // get the type of image, set it as a jpg if this goes wrong
                int imgType = inputImage.getType();
                if (imgType == 0){
                    imgType = 5;
                }

                try{
                    // creates output image
                    BufferedImage outputImage = new BufferedImage(scaledWidth, scaledHeight, imgType);

                    // scales the input image to the output image
                    Graphics2D g2d = outputImage.createGraphics();
                    g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
                    g2d.dispose();

                    // extracts extension of output file
                    String formatName = outputImagePath.substring(outputImagePath
                            .lastIndexOf(".") + 1);

                    // writes to output file
                    ImageIO.write(outputImage, formatName, new File(outputImagePath));
                }
                catch(IllegalArgumentException e){
                    System.out.println(e);
                    JOptionPane.showMessageDialog(null, "There was an error shrinking image" + cutUp(inputImagePath) + "\nShrinking the rest of the images", "Error", JOptionPane.INFORMATION_MESSAGE);
                }
                catch (OutOfMemoryError memoryError){
                    System.out.println(memoryError);
                }
            }
            else{
                // get the type of image, set it as a jpg if this goes wrong
                int imgType = inputImage.getType();
                if (imgType == 0){
                    imgType = 5;
                }
                // creates output image
                BufferedImage outputImage = new BufferedImage(oldWidth, oldHeight, imgType);
                // extracts extension of output file
                String formatName = outputImagePath.substring(outputImagePath
                        .lastIndexOf(".") + 1);

                // writes to output file
                ImageIO.write(outputImage, formatName, new File(outputImagePath));
            }

        }
        catch(IOException | OutOfMemoryError e){
            System.out.println(e);
        }
        counter.incDone();
    }

    // Method to grab just the file name and extension from the address (file.txt)
    public static String cutUp(String input){
        String output = "";
        String[] tempString = input.split(Pattern.quote(File.separator));
        int i=0;
        for (String counter:tempString){
            i++; // just to get the length of the array
        }
        output = tempString[i-1];
        return output;
    }
}