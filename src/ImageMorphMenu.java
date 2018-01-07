/*
 * File:       ImageMorphMenu.java
 * Author:     Taylor Ecton
 *
 * Purpose:    Menu bar for the Image Morph Program.
 *
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ImageMorphMenu extends JMenuBar {
    private Color textColor = Color.WHITE;
    private Color backgroundColor = Color.BLACK;

    private JCheckBoxMenuItem showEditorSettings;

    /**
     * Constructor for custom menu bar.
     * @param menuListener The listener that will respond to menu items being selected.
     */
    public ImageMorphMenu(ActionListener menuListener) {

        // Menu Items Across Menu Bar
        JMenu fileMenu = new JMenu("File");
        JMenu optionsMenu = new JMenu("Options");
        JMenu helpMenu = new JMenu("Help");

        // File Menu Items
        JMenuItem newProjectMenuItem = new JMenuItem("New Project");
        JMenuItem saveProjectMenuItem = new JMenuItem("Save Project");
        JMenuItem openProjectMenuItem = new JMenuItem("Open Project");
        JMenuItem exportMorphMenuItem = new JMenuItem("Export...");
        JMenuItem quitMenuItem = new JMenuItem("Quit");

        // Options Menu Items
        showEditorSettings = new JCheckBoxMenuItem("Show Editor Settings");
        showEditorSettings.setState(true);

        // Help Menu Items
        JMenuItem helpMenuItem = new JMenuItem("Help");

        // Set Action Commands
        newProjectMenuItem.setActionCommand("New Project");
        saveProjectMenuItem.setActionCommand("Save Project");
        openProjectMenuItem.setActionCommand("Open Project");
        exportMorphMenuItem.setActionCommand("Export");
        quitMenuItem.setActionCommand("Quit");

        showEditorSettings.setActionCommand("Show Editor Settings");

        helpMenuItem.setActionCommand("Help");

        // Add Action Listener
        newProjectMenuItem.addActionListener(menuListener);
        saveProjectMenuItem.addActionListener(menuListener);
        openProjectMenuItem.addActionListener(menuListener);
        exportMorphMenuItem.addActionListener(menuListener);
        quitMenuItem.addActionListener(menuListener);

        showEditorSettings.addActionListener(menuListener);

        helpMenuItem.addActionListener(menuListener);

        // Populate File Menu
        fileMenu.add(newProjectMenuItem);
        fileMenu.add(saveProjectMenuItem);
        fileMenu.add(openProjectMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exportMorphMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(quitMenuItem);

        // Populate Options Menu
        optionsMenu.add(showEditorSettings);

        // Populate Help Menu
        helpMenu.add(helpMenuItem);

        // Populate Image Morph Menu Bar
        this.add(fileMenu);
        this.add(optionsMenu);
        this.add(helpMenu);
    }

    public JCheckBoxMenuItem getShowEditorSettings() { return showEditorSettings; }
}
