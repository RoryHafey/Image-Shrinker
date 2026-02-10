/* Apave Image Shrinker
 * Rory Hafey (roryngh@hotmail.com)
 * Apave Newcastle
 * 2025
 */
package imageShrink;

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
import java.util.Scanner;

public class ApaveImageShrinker 
{    

    // GUI components
    static JPanel displayDirectory = new JPanel(new GridLayout(0, 2));
    static JMenuBar menuBar;
    static JMenu fileMenu, helpMenu;
    static JMenuItem openFolder, exitProgram, aboutProgram;
    static String imagePath = "null";
    static ArrayList<String> filesToShrink = new ArrayList<String>();
    static String lastDirectory;
    static int numDone = 0;

    public static void main(String[] args)
    {

        Counter counter = new Counter();

        // Get the last directory opened
        try{
            File configFile = new File("config.txt");
            Scanner configReader = new Scanner(configFile);
            String nextLine =  configReader.nextLine();
            lastDirectory = nextLine.split(">")[1];
        }
        catch(IOException e){
            System.out.println("Error reading config file");
        }

        // Create the GUI frame, set up menu items
        JFrame frame = new JFrame("Apave Image Shrinker");
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        helpMenu = new JMenu("Help");
        openFolder = new JMenuItem("Open Folder");
        exitProgram = new JMenuItem("Exit");
        aboutProgram = new JMenuItem("About");
        BufferedImage iconPic = new BufferedImage(1,1,2);
        java.net.URL imageURL = ApaveImageShrinker.class.getResource("loading.gif");
        ImageIcon loadingPic = new ImageIcon(imageURL);

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
        JLabel loading = new JLabel(loadingPic);

        // create file opening item and scrolling window
        JFileChooser selectDirectory = new JFileChooser(lastDirectory);
        JScrollPane scrollPane = new JScrollPane(displayDirectory, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();

        // set up the dimensions (x axis, y axis, width, height)
        shrinkImages.setBounds(250, 250, 175, 50);
        selectImages.setBounds(10, 250, 175, 50);
        scrollPane.setBounds(10, 30, 400, 200);
        preview.setBounds(10, 10, 200, 20);
        loading.setBounds(185, 250, 50, 50);

        // adding components to frame
        frame.add(selectImages);
        frame.add(shrinkImages);
        frame.add(scrollPane);
        frame.add(preview);
        frame.add(loading);
        frame.setJMenuBar(menuBar);
        
        // set the icon pics for the frame
        try {
            iconPic = ImageIO.read(new File("Logo.jpg"));
        }
        catch (IOException errors){
            System.out.println("Icon file not found");
        }

        frame.setIconImage(iconPic);

        // set size and tell it to close when x is pressed
        frame.setSize(450, 375);
        frame.setLocationRelativeTo(null);
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
                if (!imagePath.equals("null")){
                    counter.resetNumDone();
                    //make output directory if it doesnt exist
                    File smallFolder = new File(imagePath.replace("\\","/") + "/small");
                    if (!smallFolder.exists()){
                        smallFolder.mkdirs();
                    }
                    
                    // resize the images in the list
                    int numToShrink = 0;
                    for (String image: filesToShrink){
                        Resizer newResizer = new Resizer(image, imagePath.replace("\\","/") + "/small/small_" + cutUp(image), reqSize, counter);
                        Thread newThread = new Thread(newResizer);
                        newThread.setDaemon(true);
                        newThread.start();
                        numToShrink++;
                    }

                    // timer for timeout notifications
                    int timer = 0;
                    while (counter.getNumDone() < numToShrink){
                        try{
                            Thread.sleep(5);
                            timer+=5;
                        }
                        catch(InterruptedException ioerror){break;}
                        if (timer >= 15000){
                            break;
                        }
                    }
                    if (timer >= 15000){
                        JOptionPane.showMessageDialog(null, "Timed Out. Too many pictures", "OK", JOptionPane.INFORMATION_MESSAGE);
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "Finished shrinking " + counter.getNumDone() + " pictures.", "Done", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                else {
                    JOptionPane.showMessageDialog(null, "No images selected. Open a folder", "Error", JOptionPane.INFORMATION_MESSAGE);
                }
            }            
        });

        selectImages.addActionListener(new ActionListener() {   
            public void actionPerformed(ActionEvent e) {
                openDirectory(selectDirectory);
            }
        });

    }


    // method to open a directory and save the image files there to a list
    public static void openDirectory(JFileChooser selectDirectory){

        selectDirectory.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); //rather than picking a specific file
        int r = selectDirectory.showOpenDialog(null);
        if (r == JFileChooser.APPROVE_OPTION) {
            imagePath = selectDirectory.getSelectedFile().getAbsolutePath();

            try{
                // Update the lastDirectory
                lastDirectory = imagePath;
                FileWriter setLast = new FileWriter("config.txt");
                setLast.write("Last used directory >" + lastDirectory);
                setLast.close();
                System.out.println(lastDirectory);
            }
            catch(IOException error){
                System.out.println("Error writing last used directory");
            }


            // wipe the current display so it doesnt double up
            displayDirectory.removeAll();
            filesToShrink.clear();
            File folder = new File(imagePath);
            File[] listOfFiles = folder.listFiles();
            // check its an image, then add it to the display as a label with an icon, max 
            for (File file : listOfFiles) {
                if (file.isFile() && (file.getName().toLowerCase().contains(".jpg") || file.getName().toLowerCase().contains(".jpeg")
                || file.getName().toLowerCase().contains(".png") || file.getName().toLowerCase().contains(".heic"))) {
                        Thumbnail newThumbnail = new Thumbnail(imagePath + "\\" + file.getName(), displayDirectory);
                        Thread newThread = new Thread(newThumbnail);
                        newThread.setDaemon(true);
                        newThread.start();
                    // add the files to a list for later processing
                    filesToShrink.add(imagePath + "\\" + file.getName());
                }
            }
        }
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


