/* Apave Image Shrinker
 * Rory Hafey (roryngh@hotmail.com)
 * Apave Newcastle
 * 2025
 */

package shrinker;

// Import relevant libraries
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main
{

    // GUI components
    static JPanel displayDirectory = new JPanel(new GridLayout(0, 2));
    static JMenuBar menuBar;
    static JMenu fileMenu, helpMenu;
    static JFrame frame = new JFrame("Apave Image Shrinker");
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
        JLabel preview = new JLabel("Low-Resolution Image Preview");

        // create file opening item and scrolling window
        JFileChooser selectDirectory = new JFileChooser(lastDirectory);
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

        // set the icon pics for the frame
        try {
            InputStream is = shrinker.Main.class.getResourceAsStream("/images/Logo.jpg");
            iconPic = ImageIO.read(is);
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
                selectImages.setText("Retrieving Images");
                selectImages.setEnabled(false);
                shrinkImages.setEnabled(false);
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        openDirectory(selectDirectory, counter);
                        return null;
                    }
                    @Override
                    protected void done() {
                        selectImages.setText("Open Folder");
                        shrinkImages.setEnabled(true);
                        selectImages.setEnabled(true);
                    }
                }.execute();
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
                shrinkImages.setText("Processing Images...");
                shrinkImages.setEnabled(false);
                selectImages.setEnabled(false);

                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        if (!imagePath.equals("null")) {

                            boolean stalledFlag = false;
                            File smallFolder = new File(imagePath.replace("\\", "/") + "/small");
                            if (!smallFolder.exists()) {
                                smallFolder.mkdirs();
                            }
                            System.out.println(filesToShrink.size() + " files to shrink");
                            for (int i = 0; i < filesToShrink.size(); i = i+50) {
                                System.out.println("Looping with i " + i);
                                int numToShrink = 0;
                                counter.resetNumDone();


                                if ((i + 50) < filesToShrink.size()) {
                                    numToShrink = 50;
                                    String[] subArray = new String[50];
                                    for (int j = 0; j < 50; j++) {
                                        subArray[j] = filesToShrink.get(i + j);
                                    }
                                    for (String image : subArray) {
                                        Resizer newResizer = new Resizer(image, imagePath.replace("\\", "/") + "/small/small_" + cutUp(image), reqSize, counter);
                                        Thread newThread = new Thread(newResizer);
                                        newThread.setDaemon(true);
                                        newThread.start();
                                    }
                                } else {
                                    String[] subArray = new String[filesToShrink.size() - i];
                                    numToShrink = filesToShrink.size() - i;
                                    for (int j = 0; j < filesToShrink.size() - i; j++) {
                                        subArray[j] = filesToShrink.get(i + j);
                                    }
                                    for (String image : subArray) {
                                        Resizer newResizer = new Resizer(image, imagePath.replace("\\", "/") + "/small/small_" + cutUp(image), reqSize, counter);
                                        Thread newThread = new Thread(newResizer);
                                        newThread.setDaemon(true);
                                        newThread.start();
                                    }
                                }

                                int timer = 0;
                                while (counter.getNumDone() < numToShrink) {
                                    Thread.sleep(250);
                                    timer += 250;
                                    if (timer >= 15000) {
                                        stalledFlag = true;
                                        break;
                                    }
                                }
                            }
                            if (stalledFlag) {
                                JOptionPane.showMessageDialog(null,
                                        "Timed Out. Too many pictures", "OK", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(null,
                                        "Finished shrinking " + filesToShrink.size() + " pictures.", "Done", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                        else {
                                JOptionPane.showMessageDialog(null,
                                        "No images selected. Open a folder", "Error", JOptionPane.INFORMATION_MESSAGE);
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        shrinkImages.setText("Shrink All Images");
                        shrinkImages.setEnabled(true);
                        selectImages.setEnabled(true);
                    }
                }.execute();
            }
        });


        selectImages.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectImages.setText("Retrieving Images");
                selectImages.setEnabled(false);
                shrinkImages.setEnabled(false);
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        openDirectory(selectDirectory, counter);
                        return null;
                    }
                    @Override
                    protected void done() {
                        selectImages.setText("Open Folder");
                        selectImages.setEnabled(true);
                        shrinkImages.setEnabled(true);
                    }
                }.execute();
            }
        });

    }


    // method to open a directory and save the image files there to a list
    public static void openDirectory(JFileChooser selectDirectory, Counter counter){

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


            // wipe the current display so it doesn't double up
            displayDirectory.removeAll();
            filesToShrink.clear();
            File folder = new File(imagePath);
            File[] listOfFiles = folder.listFiles();
            boolean heicFlag = false; // flag to ensure only one error message is shown about HEIC files
            boolean stalledFlag = false;
            for (int i = 0; i < listOfFiles.length-1; i = i+50){
                File[] subArray;
                counter.resetNumDone();
                if ((i + 50) < listOfFiles.length) {
                    subArray = Arrays.copyOfRange(listOfFiles, i, i + 50);
                }
                else {
                    subArray = Arrays.copyOfRange(listOfFiles, i, listOfFiles.length);
                }


                // check it's an image, then add it to the display as a label with an icon, max
                for (File file : subArray) {
                    if (file.isFile() && (file.getName().toLowerCase().contains(".heic")) && !heicFlag){
                        JOptionPane.showMessageDialog(null, "This program does not support HEIC files", "Warning", JOptionPane.INFORMATION_MESSAGE);
                        heicFlag = true;
                    }
                    if (file.isFile() && (file.getName().toLowerCase().contains(".jpg") || file.getName().toLowerCase().contains(".jpeg")
                            || file.getName().toLowerCase().contains(".png"))) {
                        Thumbnail newThumbnail = new Thumbnail(imagePath + "\\" + file.getName(), displayDirectory, counter);
                        Thread newThread = new Thread(newThumbnail);
                        newThread.setDaemon(true);
                        newThread.start();
                        // add the files to a list for later processing
                        filesToShrink.add(imagePath + "\\" + file.getName());
                    }
                }

                try {
                    int timer = 0;
                    while (counter.getNumDone() < subArray.length-1) {
                        Thread.sleep(100);
                        timer += 100;
                        System.out.println(counter.getNumDone());
                        if (timer >= 15000) {
                            System.out.println("Thumbnails took too long");
                            stalledFlag = true;
                            break;
                        }
                    }
                }
                catch (InterruptedException error){
                    System.out.println("Error waiting for thumbnails");
                }

                System.out.println("StalledFlag is " + stalledFlag);
                if (stalledFlag) {
                    System.out.println("Breaking");
                    break;
                }
                System.out.println("End of loop");
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


