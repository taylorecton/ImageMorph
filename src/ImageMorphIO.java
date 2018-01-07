/*
 * File:       ImageMorphIO.java
 * Author:     Taylor Ecton
 *
 * Purpose:    Handles reading and writing of .morph files.
 *
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class ImageMorphIO {
    // the window beind saved or loaded into
    private ImageMorphWindow window;

    // the image panels
    private ImagePanel leftPanel, rightPanel;

    // the settings panel
    private SettingsPanel settingsPanel;

    // the menu
    private ImageMorphMenu menu;

    // the lines for the file to write or read
    private ArrayList<String> fileLines;

    /**
     * Constructor for ImageMorphIO
     * @param window The window being loaded into or saved.
     */
    public ImageMorphIO(ImageMorphWindow window) {
        // initialize references
        this.window = window;
        leftPanel = window.getImagePanelLeft();
        rightPanel = window.getImagePanelRight();
        settingsPanel = window.getSettingsPanel();
        menu = window.getMenu();

        // create array to hold file lines
        fileLines = new ArrayList<>();
    }

    /**
     * Saves project to path specified by absolutePath
     * @param absolutePath The path to save the project to
     */
    public void write(String absolutePath) {
        // get file specified by absolute path
        File file = new File(absolutePath);

        // for writing the files
        OutputStream outputStream;
        OutputStreamWriter outputStreamWriter;
        PrintWriter printWriter;

        // message and title for JOptionPane the opens based on result of save
        String message, title;

        // clear the file if it exists
        if (file.exists())
            file.delete();

        // generate the lines for the file
        generateFileLines();

        // try to write the file
        try {
            outputStream = new FileOutputStream(absolutePath);
            outputStreamWriter = new OutputStreamWriter(outputStream);
            printWriter = new PrintWriter(outputStreamWriter);

            // write the file lines to the file
            for (String line : fileLines)
                printWriter.println(line);

            message = "Project saved.";
            title = "Save";

            // show confirmation of save being completed
            JOptionPane.showConfirmDialog(
                    window, message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE
            );

            printWriter.close();
            outputStreamWriter.close();
            outputStream.close();
        } catch (IOException e) {
            // notify user that save failed and print error to standard error
            message = "Error saving project!";
            title = "ERROR";
            JOptionPane.showConfirmDialog(
                    window, message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE
            );

            System.err.println(e.getMessage());
        }
    }

    /**
     * Reads the project file specified by absolutePath
     * @param absolutePath path of the file to be read in.
     */
    public void read(String absolutePath) {
        // for reading the file
        FileReader fileReader;
        BufferedReader bufferedReader;

        // a line from the file
        String line;

        // message and title for display error if something goes wrong
        String message, title;

        // try to read the file
        try {
            fileReader = new FileReader(absolutePath);
            bufferedReader = new BufferedReader(fileReader);

            // add all the lines to the file lines
            while ((line = bufferedReader.readLine()) != null)
                fileLines.add(line);

            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            // show error message for user and print error to standard error
            message = "Error loading project!";
            title = "ERROR";
            JOptionPane.showConfirmDialog(
                    window, message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE
            );

            System.err.println(e.getMessage());
        }

        // if reading was successful, process what was read
        if (!fileLines.isEmpty())
            processLines();
    }

    /**
     * Creates lines for saving file.
     */
    private void generateFileLines() {
        // holds the coordinates that are currently being written
        int[][] coords;

        // width and height of the panel
        String imagePanelWidth = Integer.toString(window.getImagePanelWidth());
        String imagePanelHeight = Integer.toString(window.getImagePanelHeight());

        // boolean values for settings
        String showEditorSettings = window.getShowEditorSettings() ? "true" : "false";
        String showControlPoints = settingsPanel.getShowControlPoints().isSelected() ? "true" : "false";
        String showLattice = settingsPanel.getShowLattice().isSelected() ? "true" : "false";

        // colors for the lattice and control points
        String primaryCPColor = colorToString(leftPanel.getPrimaryCPColor());
        String highlightCPColor = colorToString(leftPanel.getHighlightedCPColor());
        String primaryLColor = colorToString(leftPanel.getPrimaryLColor());
        String highlightLColor = colorToString(leftPanel.getHighlightedLColor());

        // dimension of control points
        String numberControlPoints = Integer.toString(leftPanel.getNumberControlPoints());

        // add all those strings to the lines for the file
        fileLines.add(imagePanelWidth);
        fileLines.add(imagePanelHeight);
        fileLines.add(showEditorSettings);
        fileLines.add(showControlPoints);
        fileLines.add(showLattice);
        fileLines.add(primaryCPColor);
        fileLines.add(highlightCPColor);
        fileLines.add(primaryLColor);
        fileLines.add(highlightLColor);
        fileLines.add(numberControlPoints);

        // add the image path for the left image to file lines
        fileLines.add(leftPanel.getImagePath());

        // check if the project being saved is currently post-morph-preview
        if (window.getStartImageLabel().getText().contains("Preview"))
            coords = window.getMorphController().getStartXCoords();
        else
            coords = leftPanel.getXCoords();

        // add each x coordinate as a separate line
        for (int i = 0; i < leftPanel.getNumberControlPoints(); i++) {
            for (int j = 0; j < leftPanel.getNumberControlPoints(); j++) {
                fileLines.add(Integer.toString(coords[i][j]));
            }
        }

        // get appropriate coordinates
        if (window.getStartImageLabel().getText().contains("Preview"))
            coords = window.getMorphController().getStartYCoords();
        else
            coords = leftPanel.getYCoords();

        // add each y coordinate for left panel
        for (int i = 0; i < leftPanel.getNumberControlPoints(); i++) {
            for (int j = 0; j < leftPanel.getNumberControlPoints(); j++) {
                fileLines.add(Integer.toString(coords[i][j]));
            }
        }

        // add the path of the right image
        fileLines.add(rightPanel.getImagePath());

        // add all the x coordinates of the right image
        coords = rightPanel.getXCoords();
        for (int i = 0; i < leftPanel.getNumberControlPoints(); i++) {
            for (int j = 0; j < leftPanel.getNumberControlPoints(); j++) {
                fileLines.add(Integer.toString(coords[i][j]));
            }
        }

        // add all the y coordinates of the right image
        coords = rightPanel.getYCoords();
        for (int i = 0; i < leftPanel.getNumberControlPoints(); i++) {
            for (int j = 0; j < leftPanel.getNumberControlPoints(); j++) {
                fileLines.add(Integer.toString(coords[i][j]));
            }
        }

        // get the duration of the morph
        fileLines.add(Integer.toString(window.getMorphController().getMorphDuration()));
    }

    /**
     * Processes lines from a file that has been read in.
     */
    private void processLines() {
        // all the coordinates
        int[][] xCoordsLeft, yCoordsLeft, xCoordsRight, yCoordsRight;

        // integer values in file
        int imagePanelWidth, imagePanelHeight, numberControlPoints, morphDuration;

        // boolean values in file
        boolean showEditorSettings, showControlPoints, showLattice;

        // colors in file
        Color primaryCPColor, highlightCPColor, primaryLColor, highlightLColor;

        // paths of images
        String imagePathLeft, imagePathRight;

        // current index into fileLines
        int currIndex = 0;

        // check each component of the file in order
        imagePanelWidth = Integer.parseInt(fileLines.get(currIndex));
        currIndex++;

        imagePanelHeight = Integer.parseInt(fileLines.get(currIndex));
        currIndex++;

        // set the panel widths after loading the information in
        setPanelWidthAndHeight(imagePanelWidth, imagePanelHeight);

        showEditorSettings = fileLines.get(currIndex).equals("true");
        currIndex++;

        showControlPoints = fileLines.get(currIndex).equals("true");
        currIndex++;

        showLattice = fileLines.get(currIndex).equals("true");
        currIndex++;

        // set all the boolean values after reading them in
        setBooleanValues(showEditorSettings, showControlPoints, showLattice);

        primaryCPColor = stringToColor(fileLines.get(currIndex));
        currIndex++;

        highlightCPColor = stringToColor(fileLines.get(currIndex));
        currIndex++;

        primaryLColor = stringToColor(fileLines.get(currIndex));
        currIndex++;

        highlightLColor = stringToColor(fileLines.get(currIndex));
        currIndex++;

        // set all the colors after reading them in
        setColors(primaryCPColor, highlightCPColor, primaryLColor, highlightLColor);

        numberControlPoints = Integer.parseInt(fileLines.get(currIndex));
        currIndex++;

        // selects the correct index in the settings panel for latticeResolutionSelector
        setLatticeResolutionSelectorIndex(numberControlPoints);

        // set number of control points for panels
        leftPanel.setNumberControlPoints(numberControlPoints);
        rightPanel.setNumberControlPoints(numberControlPoints);

        leftPanel.initializeLattice();
        rightPanel.initializeLattice();

        imagePathLeft = fileLines.get(currIndex);
        currIndex++;

        xCoordsLeft = new int[numberControlPoints][numberControlPoints];
        yCoordsLeft = new int[numberControlPoints][numberControlPoints];

        xCoordsRight = new int[numberControlPoints][numberControlPoints];
        yCoordsRight = new int[numberControlPoints][numberControlPoints];

        // set left panel x coordinates
        for (int i = 0; i < numberControlPoints; i++) {
            for (int j = 0; j < numberControlPoints; j++) {
                xCoordsLeft[i][j] = Integer.parseInt(fileLines.get(currIndex));
                currIndex++;
            }
        }

        // set left panel y coordinates
        for (int i = 0; i < numberControlPoints; i++) {
            for (int j = 0; j < numberControlPoints; j++) {
                yCoordsLeft[i][j] = Integer.parseInt(fileLines.get(currIndex));
                currIndex++;
            }
        }

        imagePathRight = fileLines.get(currIndex);
        currIndex++;

        // set right panel x coordinates
        for (int i = 0; i < numberControlPoints; i++) {
            for (int j = 0; j < numberControlPoints; j++) {
                xCoordsRight[i][j] = Integer.parseInt(fileLines.get(currIndex));
                currIndex++;
            }
        }

        // set right panel y coordinates
        for (int i = 0; i < numberControlPoints; i++) {
            for (int j = 0; j < numberControlPoints; j++) {
                yCoordsRight[i][j] = Integer.parseInt(fileLines.get(currIndex));
                currIndex++;
            }
        }

        // load left image if there is one
        if (!imagePathLeft.equals("none"))
            loadImage(imagePathLeft, leftPanel, true);
        else {
            leftPanel.setImagePath("none");
            leftPanel.setImage(null, true);
        }

        // load right image if there is one
        if (!imagePathRight.equals("none"))
            loadImage(imagePathRight, rightPanel, false);
        else {
            rightPanel.setImagePath("none");
            rightPanel.setImage(null, false);
        }

        // set all the coordinates after setting images; setting images can cause lattice to reset
        leftPanel.setXCoords(xCoordsLeft);
        leftPanel.setYCoords(yCoordsLeft);
        rightPanel.setXCoords(xCoordsRight);
        rightPanel.setYCoords(yCoordsRight);

        morphDuration = Integer.parseInt(fileLines.get(currIndex));

        // set morph duration
        setMorphDuration(morphDuration);

        // repaint the panels
        leftPanel.repaint();
        rightPanel.repaint();
    }

    /**
     * Set the height and width of the panels.
     * @param width width of panels
     * @param height height of panels
     */
    private void setPanelWidthAndHeight(int width, int height) {
        leftPanel.setWidth(width);
        leftPanel.setHeight(height);

        rightPanel.setWidth(width);
        rightPanel.setHeight(height);
    }

    /**
     * Set boolean values
     * @param showEditorSettings Whether the settings panel is visible.
     * @param showControlPoints Whether the control points are visible.
     * @param showLattice Whether the lattice is visible.
     */
    private void setBooleanValues(boolean showEditorSettings, boolean showControlPoints, boolean showLattice) {
        menu.getShowEditorSettings().setState(showEditorSettings);
        window.toggleEditorSettings(showEditorSettings);

        leftPanel.setShowControlPoints(showControlPoints);
        leftPanel.setShowLattice(showLattice);

        rightPanel.setShowControlPoints(showControlPoints);
        rightPanel.setShowLattice(showLattice);

        settingsPanel.getShowControlPoints().setSelected(showControlPoints);
        settingsPanel.getShowLattice().setSelected(showLattice);
    }

    /**
     * Sets the colors for lattice and control points.
     * @param primaryCP Primary color for control points.
     * @param highlightCP Highlight color for control points.
     * @param primaryL Primary color for control points.
     * @param highlightL Highlight color for control points.
     */
    private void setColors(Color primaryCP, Color highlightCP, Color primaryL, Color highlightL) {
        leftPanel.setPrimaryCPColor(primaryCP);
        leftPanel.setHighlightedCPColor(highlightCP);
        leftPanel.setPrimaryLColor(primaryL);
        leftPanel.setHighlightedLColor(highlightL);

        rightPanel.setPrimaryCPColor(primaryCP);
        rightPanel.setHighlightedCPColor(highlightCP);
        rightPanel.setPrimaryLColor(primaryL);
        rightPanel.setHighlightedLColor(highlightL);

        setColorSettingsComboBoxes(primaryCP, highlightL, primaryL, highlightL);
    }

    /**
     * Sets color combo boxes to correct index in settings panel.
     * @param primaryCP Primary color for control points.
     * @param highlightCP Highlight color for control points.
     * @param primaryL Primary color for control points.
     * @param highlightL Highlight color for control points.
     */
    private void setColorSettingsComboBoxes(Color primaryCP, Color highlightCP, Color primaryL, Color highlightL) {
        settingsPanel.getPrimaryCPColorSelector().setSelectedIndex(colorToIndex(primaryCP));
        settingsPanel.getHighlightCPColorSelector().setSelectedIndex(colorToIndex(highlightCP));
        settingsPanel.getPrimaryLColorSelector().setSelectedIndex(colorToIndex(primaryL));
        settingsPanel.getHighlightLColorSelector().setSelectedIndex(colorToIndex(highlightL));
    }

    /**
     * Loads an image and sets it in the specified panel.
     * @param path The path to the image.
     * @param panel The panel to add the image to.
     */
    private void loadImage(String path, ImagePanel panel, boolean isLeftPanel) {
        File file = new File(path);

        BufferedImage image;

        try {
            image = ImageIO.read(file);

            if (isLeftPanel) {
                window.setImagePanelWidth(panel.getAdjustedWidth(image));
                window.setImagePanelHeight(panel.getAdjustedHeight(image));
                window.setDimensions();
            }

            panel.setImage(image, isLeftPanel);
            panel.setImagePath(path);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Sets the duration of the morph.
     * @param duration The duration in seconds.
     */
    private void setMorphDuration(int duration) {
        window.getMorphController().setMorphDuration(duration);
        settingsPanel.getDurationSelector().setSelectedIndex(duration-1);
    }

    /**
     * Sets the latticeResolutionSelector JComboBox to the correct index
     * @param numberControlPoints 5, 10, or 20, corresponding to 5x5, 10x10, 20x20
     */
    private void setLatticeResolutionSelectorIndex(int numberControlPoints) {
        int index;
        switch (numberControlPoints) {
            case 5:
                index = 0;
                break;
            case 20:
                index = 2;
                break;
            default:
                index = 1;
                break;
        }
        settingsPanel.getLatticeResolutionSelector().setSelectedIndex(index);
    }

    /**
     * Converts a Color to a String
     * @param color The Color to convert.
     * @return The string name of the color.
     */
    private String colorToString(Color color) {
        String colorName;

        if (color == Color.ORANGE)
            colorName = "Orange";
        else if (color == Color.GREEN)
            colorName = "Green";
        else if (color == Color.BLUE)
            colorName = "Blue";
        else if (color == Color.RED)
            colorName = "Red";
        else if (color == Color.YELLOW)
            colorName = "Yellow";
        else if (color == Color.BLACK)
            colorName = "Black";
        else
            colorName = "White";

        return colorName;
    }

    /**
     * Converts a String to a Color.
     * @param name The name of the Color.
     * @return The Color indicated by the name.
     */
    private Color stringToColor(String name) {
        Color color = Color.WHITE;

        switch (name) {
            case "Orange":
                color = Color.ORANGE;
                break;
            case "Green":
                color = Color.GREEN;
                break;
            case "Blue":
                color = Color.BLUE;
                break;
            case "Red":
                color = Color.RED;
                break;
            case "Yellow":
                color = Color.YELLOW;
                break;
            case "Black":
                color = Color.BLACK;
                break;
            default:
                break;
        }

        return color;
    }

    /**
     * Index of settings combo box corresponding to a Color.
     * @param color The Color whose index is needed.
     * @return The index of the Color in the combo box.
     */
    private int colorToIndex(Color color) {
        int index;

        if (color == Color.ORANGE)
            index = 0;
        else if (color == Color.GREEN)
            index = 1;
        else if (color == Color.BLUE)
            index = 2;
        else if (color == Color.RED)
            index = 3;
        else if (color == Color.YELLOW)
            index = 4;
        else if (color == Color.BLACK)
            index = 5;
        else
            index = 6;

        return index;
    }
}
