/*
 * File:       ImageMorphHelpWindow.java
 * Author:     Taylor Ecton
 *
 * Purpose:    A window displaying help information for the user about the application.
 *
 */

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ImageMorphHelpWindow extends JFrame {
    /**
     * Constructor for ImageMorphHelpWindow.
     */
    public ImageMorphHelpWindow() {
        // sets the title for the window
        super("Help");

        // the text contained in the help window
        JTextArea helpTextArea = new JTextArea(
                "Weclome to ImageMorph!\n\n" +
                "Move control points on the start image and end image" +
                " to correspond to similar features in the photos.\n\n" +
                "Morph preview will animate from start image to end " +
                "image; both must be set in order to preview.\n\n" +
                "Export a morph as a series of JPEGs using the 'Export...' " +
                "option under the 'File' menu and select the directory where " +
                "you want to save the images.\n\n" +
                "Can hide or show settings panel from Options in menu bar.\n\n" +
                "More features to come!"
        );

        // creates a border around the text, has the text wrap so it isn't one long
        // line, and makes the area un-editable
        helpTextArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        helpTextArea.setLineWrap(true);
        helpTextArea.setWrapStyleWord(true);
        helpTextArea.setEditable(false);

        // a scroll pane to contain the text
        JScrollPane scrollPane = new JScrollPane(helpTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(550, 350));

        // add the scroll pane to the window and make the window visible
        this.add(scrollPane, BorderLayout.CENTER);
        this.setSize(600, 400);
        this.setVisible(true);
        this.setResizable(false);
    }
}
