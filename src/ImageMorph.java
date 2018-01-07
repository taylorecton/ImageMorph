/*
 * File:       ImageMorph.java
 * Author:     Taylor Ecton
 *
 * Purpose:    Driver for program.
 *
 */

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ImageMorph {
    // Main function
    public static void main(String[] args) {
        // create a new ImageMorphWindow
        ImageMorphWindow window = new ImageMorphWindow();

        // Set window to not automatically close when close button is pressed
        window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        // Close program after validating with user if edits have been made
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (confirmWithUser(window) == JOptionPane.YES_OPTION)
                    System.exit(0);
            }
        });
    }

    /**
     * Gets user confirmation if window is being closed after edits have been made
     * @param window The image morph window.
     * @return An int indicating yes or no.
     */
    private static int confirmWithUser(ImageMorphWindow window) {
        String message = "Any unsaved changes will be lost. Continue?";

        if (window.getEditsMade())
            return JOptionPane.showConfirmDialog(
                    window, message, "Quit", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE
            );
        else
            return JOptionPane.YES_OPTION;
    }
}
