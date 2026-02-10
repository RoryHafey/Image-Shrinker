/* Apave Image Shrinker
 * Rory Hafey (roryngh@hotmail.com)
 * Apave Newcastle
 * 2025
 */

 // Import relevant libraries
import java.io.*;
import java.util.regex.*;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;

public class ApaveImageShrinker 
{    

    // GUI components
    static JPanel displayDirectory = new JPanel(new GridLayout(0, 2));
    static JMenuBar menuBar;
    static JMenu fileMenu, helpMenu;
    static JMenuItem openFolder, exitProgram, aboutProgram;
    static String imagePath = "null";
    static ArrayList<String> filesToShrink = new ArrayList<String>();

    public static void main(String[] args)
    {

        // Create the GUI frame, set up menu items
        JFrame frame = new JFrame("Apave Image Shrinker");
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        helpMenu = new JMenu("Help");
        openFolder = new JMenuItem("Open Folder");
        exitProgram = new JMenuItem("Exit");
        aboutProgram = new JMenuItem("About");
        BufferedImage iconPic = new BufferedImage(1,1,2);

        // Max image size
        double reqSize = 800.0;

        //Add menu items to gui
        fileMenu.add(openFolder);
        fileMenu.add(exitProgram);
        helpMenu.add(aboutProgram);
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        // Create buttons and label
        JButton shrinkImages = new JButton("Shrink All Images");
        JButton selectImages = new JButton("Open Folder");
        JLabel preview = new JLabel("Image Preview");

        // create file opening item and scrolling window
        JFileChooser selectDirectory = new JFileChooser();
        JScrollPane scrollPane = new JScrollPane(displayDirectory, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();

        // set up the dimensions (x axis, y axis, width, height)
        shrinkImages.setBounds(250, 250, 175, 50);
        selectImages.setBounds(10, 250, 175, 50);
        scrollPane.setBounds(10, 30, 400, 200);
        preview.setBounds(10, 10, 200, 20);

        // adding components to frame
        frame.add(selectImages);
        frame.add(shrinkImages);
        frame.add(scrollPane);
        frame.add(preview);
        frame.setJMenuBar(menuBar);
        
        // set the icon for the frame
        try {
            iconPic = ImageIO.read(new File("icon.png"));
        }
        catch (IOException errors){
            System.out.println(errors);
        }
        frame.setIconImage(iconPic);

        // set size and tell it to close when x is pressed
        frame.setSize(450, 375);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // using no layout managers for frame
        frame.setLayout(null);

        // making the frame visible
        frame.setVisible(true);

        // action listeners for each component that needs it
        openFolder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openDirectory(selectDirectory);
            }
        });

        exitProgram.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        aboutProgram.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Developed by Rory Hafey\nApave Australia\n2025", "About this program", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        shrinkImages.addActionListener(new ActionListener() {   
            public void actionPerformed(ActionEvent e) {
                try {
                    if (!imagePath.equals("null")){
                        //make output directory if it doesnt exist
                        File smallFolder = new File(imagePath.replace("\\","/") + "/small");
                        if (!smallFolder.exists()){
                            smallFolder.mkdirs();
                        }
                        
                        // resize the images in the list
                        for (String image: filesToShrink){
                            resize(image, imagePath.replace("\\","/") + "/small/small_" + cutUp(image), reqSize);
                        }
                        JOptionPane.showMessageDialog(null, "Image Shrinking Complete", "Done", JOptionPane.INFORMATION_MESSAGE);

                    }
                    else {
                        JOptionPane.showMessageDialog(null, "No images selected. Open a folder", "Error", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                catch (IOException errors){
                    System.out.println(errors);
                }
            }          
        });

        selectImages.addActionListener(new ActionListener() {   
            public void actionPerformed(ActionEvent e) {
                openDirectory(selectDirectory);
            }
        });

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

    // resize the images according to the scaling required
    public static void resize(String inputImagePath, String outputImagePath, double reqSize) throws IOException {
        int scaledWidth; 
        int scaledHeight;
        int oldWidth;
        int oldHeight;
        double scaler = 1.0;

        // reads input image
        File inputFile = new File(inputImagePath);
        BufferedImage inputImage = ImageIO.read(inputFile);

        //gets the dimensions
        oldWidth = inputImage.getWidth();
        oldHeight = inputImage.getHeight();
                             
        // dont resize if its too small, also determine the orientation and scaling factor for the image
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
        }
    }



    // methpod to open a directory and save the image files there to a list
    public static void openDirectory(JFileChooser selectDirectory){
        selectDirectory.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); //rather than picking a specific file
        int r = selectDirectory.showOpenDialog(null);
        if (r == JFileChooser.APPROVE_OPTION) {
            imagePath = selectDirectory.getSelectedFile().getAbsolutePath();
            // wipe the current display so it doesnt double up
            displayDirectory.removeAll();
            filesToShrink.clear();
            File folder = new File(imagePath);
            File[] listOfFiles = folder.listFiles();
            // check its an image, then add it to the display as a label with an icon
            for (File file : listOfFiles) {
                if (file.isFile() && (file.getName().toLowerCase().contains(".jpg") || file.getName().toLowerCase().contains(".jpeg")
                || file.getName().toLowerCase().contains(".png") || file.getName().toLowerCase().contains(".heic"))) {
                    try{
                        ImageIcon icon = new ImageIcon(thumbnail(imagePath + "\\" + file.getName()));  //shrink to appropriate size
                        JLabel tempLabel = new JLabel(icon);
                        tempLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 5));                    
                        displayDirectory.add(tempLabel);
                        displayDirectory.setVisible(false); //refresh the display
                        displayDirectory.setVisible(true);
                    }
                    catch (IOException errors){
                        System.out.println(errors);
                    }
                    // add the files to a list for later processing
                    filesToShrink.add(imagePath + "\\" + file.getName());
                }
            }
        }
    }


    // method to make thumbnail images for the display. see above for details, its the same except smaller and no file output
    public static BufferedImage thumbnail(String inputImagePath) throws IOException {
        int scaledWidth; 
        int scaledHeight;
        int oldWidth;
        int oldHeight;
        double scaler = 1.0;
        BufferedImage outputImage = new BufferedImage(1, 1, 5);

        // reads input image
        File inputFile = new File(inputImagePath);
        BufferedImage inputImage = ImageIO.read(inputFile);

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
            System.out.println(e);
        }
        return outputImage;
    }

}


