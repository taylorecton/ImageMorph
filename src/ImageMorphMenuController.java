/*
 * File:       ImageMorphMenuController.java
 * Author:     Taylor Ecton
 *
 * Purpose:    Listens for events on the menu bar and calls appropriate functions in response.
 *
 */

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ImageMorphMenuController implements ActionListener {

    private ImageMorphWindow window;

    /**
     * Constructor for ImageMorphMenuController.
     * @param window The window for the project.
     */
    public ImageMorphMenuController(ImageMorphWindow window) {
        this.window = window;
    }

    /**
     * Overrides actionPerformed to implement ActionListener.
     * @param e The event that triggers the ActionListener.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        int userResponse;

        switch (action) {
            case "New Project":
                // Get confirmation if edits have been made
                userResponse = confirmWithUser(action, window);

                // Create a new project if no changes or user approves losing unsaved changes
                if (userResponse == JOptionPane.YES_OPTION)
                    newProject();
                break;
            case "Save Project":
                // save current project
                saveProject();
                break;
            case "Open Project":
                // Get confirmation if edits have been made
                userResponse = confirmWithUser(action, window);

                // Open new project if no changes or user approves losing unsaved changes
                if (userResponse == JOptionPane.YES_OPTION)
                    openProject();
                break;
            case "Export":
                // export the project as a series of JPEGs
                export();
                break;
            case "Quit":
                // Get confirmation if edits have been made
                userResponse = confirmWithUser(action, window);

                // Quit if no changes or user approves losing unsaved changes
                if (userResponse == JOptionPane.YES_OPTION)
                    quit();
                break;
            case "Show Editor Settings":
                JCheckBoxMenuItem source = (JCheckBoxMenuItem) e.getSource();
                toggleEditorSettings(source.getState());
                break;
            case "Help":
                // Display help window
                help();
                break;
            default:
                // Something went wrong
                System.err.println("Unexpected action command in actionPerformed method");
                System.err.println("of ImageMorphMenuController: action = " + action);
                System.exit(1);
        }
    }

    /**
     * Opens a new project.
     */
    private void newProject() {
        ImageMorphWindow newWindow;

        window.dispose();

        newWindow = new ImageMorphWindow();
        newWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        newWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (confirmWithUser("Quit", newWindow) == JOptionPane.YES_OPTION)
                    System.exit(0);
            }
        });
    }

    /**
     * Saves the current project.
     */
    private void saveProject() {
        JFileChooser fileChooser = new JFileChooser();
        ImageMorphIO morphIO;
        int userChoice = fileChooser.showSaveDialog(window);
        String fileName;

        if (userChoice == JFileChooser.APPROVE_OPTION) {
            if (fileChooser.getSelectedFile().getAbsolutePath().contains(".morph"))
                fileName = fileChooser.getSelectedFile().getAbsolutePath();
            else
                fileName = fileChooser.getSelectedFile().getAbsolutePath() + ".morph";
            window.setEditsMade(false);
            morphIO = new ImageMorphIO(window);
            morphIO.write(fileName);
        }
    }

    /**
     * Opens a saved project
     */
    private void openProject() {
        JFileChooser fileChooser = new JFileChooser();
        ImageMorphIO morphIO;
        int userChoice = fileChooser.showOpenDialog(window);

        if (userChoice == JFileChooser.APPROVE_OPTION) {
            String absolutePath = fileChooser.getSelectedFile().getAbsolutePath();

            if (!absolutePath.endsWith(".morph")) {
                String message = "File type must be '.morph'";
                String title = "Error Opening File";

                JOptionPane.showConfirmDialog(
                        window, message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE
                );
            } else {
                window.setEditsMade(false);
                morphIO = new ImageMorphIO(window);
                morphIO.read(absolutePath);
            }
        }
    }

    /**
     * Exports a project as a series of JPEG images
     */
    private void export() {
        int userChoice;
        JFileChooser fileChooser = new JFileChooser();
        String message;
        String title = "Error Exporting";
        String directoryPath;

        // make sure that there are start and end images to morph between, display error message if not
        if (window.getImagePanelLeft().getImage() == null || window.getImagePanelRight().getImage() == null) {
            message = "You must set both a start and end image to perform a morph!";

            JOptionPane.showConfirmDialog(
                    window, message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE
            );

            return;
        }

        // make sure that the left panel is not set to the morphed image before exporting
        if (window.getMorphController().getHasMorphed()) {
            message = "Please reset the morph preview before exporting!";

            JOptionPane.showConfirmDialog(
                    window, message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE
            );

            return;
        }

        // supposed to only display directories; my experience has been that this does not work
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        userChoice = fileChooser.showSaveDialog(window);

        if (userChoice == JFileChooser.APPROVE_OPTION) {
            // get the directory name to save to
            if (fileChooser.getSelectedFile().isDirectory()) {
                directoryPath = fileChooser.getSelectedFile().getAbsolutePath();
            }
            else {
                directoryPath = fileChooser.getCurrentDirectory().getAbsolutePath();
            }

            // export the images
            window.getMorphController().setExporting(true);
            window.getMorphController().setExportDirectory(directoryPath);
            window.getMorphController().morph();
        }
    }

    /**
     * Quits the project.
     */
    private void quit() { System.exit(0); }

    private void toggleEditorSettings(boolean state) {
        window.toggleEditorSettings(state);
    }

    /**
     * Opens a Help Window
     */
    private void help() { new ImageMorphHelpWindow(); }

    /**
     * If user has made any edits to current project, get confirmation before taking action.
     * @param action The action being taken. Used as title for JOptionPane.
     * @return YES_OPTION or NO_OPTION
     */
    private int confirmWithUser(String action, ImageMorphWindow window) {
        String message = "Any unsaved changes will be lost. Continue?";

        if (window.getEditsMade())
            return JOptionPane.showConfirmDialog(
                    window, message, action, JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE
            );
        else
            return JOptionPane.YES_OPTION;
    }
}
