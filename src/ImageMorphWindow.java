/*
 * File:       ImageMorphWindow.java
 * Author:     Taylor Ecton
 *
 * Purpose:    This is the frame that contains all the other components.
 *
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageMorphWindow extends JFrame implements MouseListener, ActionListener {
    // the two image panels
    private ImagePanel imagePanelLeft, imagePanelRight;

    // the menu for the window
    private ImageMorphMenu menu;

    // controller that controls the lattice
    private MorphController morphController;

    // buttons for loading images and previewing morph
    private JButton loadImageButtonLeft, loadImageButtonRight, previewButton;

    // panel for preview button and contentPanel for holding image panels
    private JPanel previewButtonPanel, contentPanel, outerPanelLeft, outerPanelRight;

    // Panel for settings
    private SettingsPanel settingsPanel;

    // Separator that is added/removed with settings panel for clarity
    private JSeparator settingsSeparator;

    // the content pane
    private Container container;

    // width and height for the panel
    private int imagePanelWidth, imagePanelHeight;

    // booleans indicating if the settings should be shown and if any edits have been made since saving
    private boolean showEditorSettings, editsMade;

    // color for the frame; may make editor theme customizable at later date
    private Color frameColor = Color.BLACK;

    // start image label; need reference to change it during preview animation
    private JLabel startImageLabel;

    /**
     * Constructor for ImageMorphWindow class.
     */
    public ImageMorphWindow() {
        // set the window title
        super("Image Morph");

        // get the container and set it black
        container = this.getContentPane();
        container.setBackground(Color.BLACK);

        // initialize panel width and height
        imagePanelWidth = 600;
        imagePanelHeight = 400;

        // initialize boolean values
        editsMade = false;
        showEditorSettings = true;

        // create morphController
        morphController = new MorphController(this);

        // set up load image buttons, image panels, settings panel, and preview button
        initializeLoadImageButtons();
        createImagePanels();
        createSettingsPanel();
        createPreviewButtonPanel();

        // setup menu controller and menu
        ImageMorphMenuController menuController = new ImageMorphMenuController(this);
        menu = new ImageMorphMenu(menuController);

        // add the menu to the window
        this.setJMenuBar(menu);

        // set the window size and set visible
        this.setSize(imagePanelWidth*2 + 285, imagePanelHeight + 175);
        this.setResizable(false);
        this.setVisible(true);
    }

    /**
     * @return int imagePanelWidth
     */
    public int getImagePanelWidth() { return imagePanelWidth; }

    /**
     * @return int imagePanelHeight
     */
    public int getImagePanelHeight() { return imagePanelHeight; }

    /**
     * @return boolean showEditorSettings
     */
    public boolean getShowEditorSettings() { return showEditorSettings; }

    /**
     * @return left ImagePanel
     */
    public ImagePanel getImagePanelLeft() { return imagePanelLeft; }

    /**
     * @return right ImagePanel
     */
    public ImagePanel getImagePanelRight() { return imagePanelRight; }

    /**
     * @return the SettingsPanel
     */
    public SettingsPanel getSettingsPanel() { return settingsPanel; }

    /**
     * @return The menu; used for setting showEditorSettings CheckBoxItem when loading project
     */
    public ImageMorphMenu getMenu() { return menu; }

    /**
     * @return boolean editsMade
     */
    public boolean getEditsMade() { return editsMade; }

    /**
     * @return JLabel startImageLabel
     */
    public JLabel getStartImageLabel() { return startImageLabel; }

    /**
     * @return JButton previewButton
     */
    public JButton getPreviewButton() { return previewButton; }

    /**
     * @return the MorphController
     */
    public MorphController getMorphController() { return morphController; }

    /**
     * Set whether edits have been made to project since last save.
     * @param value Whether edits have been made or not.
     */
    public void setEditsMade(boolean value) { editsMade = value; }

    /**
     * Allows other classes to set width of ImagePanels
     * @param width
     */
    public void setImagePanelWidth(int width) { this.imagePanelWidth = width; }

    /**
     * Allows other classes to set height of ImagePanels
     * @param height
     */
    public void setImagePanelHeight(int height) { this.imagePanelHeight = height; }

    /**
     * Method for changing window color. Currently not in use.
     * @param newWindowColor The new color for the window.
     */
    public void setWindowColor(Color newWindowColor) {
        frameColor = newWindowColor;
        contentPanel.setBackground(newWindowColor);
    }

    /**
     * Toggles the settings panel showing or hidden.
     * @param visible Is the panel visible?
     */
    public void toggleEditorSettings(boolean visible) {
        if (visible) {
            // update boolean value
            showEditorSettings = true;

            // take the preview button off of the main container
            container.remove(previewButtonPanel);

            // and return it to the settings panel
            settingsPanel.getMorphSettingsPanel().add(previewButtonPanel);

            // add the settings panel to the container
            container.add(settingsSeparator);
            container.add(settingsPanel, BorderLayout.EAST);

            // and resize the container
            this.setSize(imagePanelWidth*2 + 285, imagePanelHeight + 175);
        } else {
            // update the boolean
            showEditorSettings = false;

            // remove the settings panel from the window
            container.remove(settingsPanel);
            container.remove(settingsSeparator);

            // take the preview button off of the settings panel
            settingsPanel.getMorphSettingsPanel().remove(previewButtonPanel);

            // and add it to the bottom of the main window
            container.add(previewButtonPanel, BorderLayout.SOUTH);

            // resize the window
            this.setSize(imagePanelWidth*2 + 85, imagePanelHeight + 175);
        }
    }

    /**
     * Create the image panels
     */
    private void createImagePanels() {
        // Panels to organize components
        outerPanelLeft = new JPanel();
        outerPanelRight = new JPanel();

        // label for the end image
        JLabel endImageLabel = new JLabel("End Image");

        // initialize start image label
        startImageLabel = new JLabel("Start Image");

        // set up the fonts for the labels
        startImageLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
        startImageLabel.setForeground(Color.WHITE);

        endImageLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
        endImageLabel.setForeground(Color.WHITE);

        // set sizes and color for the subpanels
        outerPanelLeft.setPreferredSize(new Dimension(imagePanelWidth, imagePanelHeight+175));
        outerPanelLeft.setBackground(Color.BLACK);
        outerPanelLeft.setOpaque(true);

        outerPanelRight.setPreferredSize(new Dimension(imagePanelWidth, imagePanelHeight+175));
        outerPanelRight.setBackground(Color.BLACK);
        outerPanelRight.setOpaque(true);

        // panel to contain the other panels
        contentPanel = new JPanel();
        contentPanel.setBackground(frameColor);
        contentPanel.setOpaque(true);

        // initialize the image panels and add listeners
        imagePanelLeft = new ImagePanel(imagePanelWidth, imagePanelHeight);
        imagePanelRight = new ImagePanel(imagePanelWidth, imagePanelHeight);

        imagePanelLeft.addMouseListener(morphController);
        imagePanelLeft.addMouseMotionListener(morphController);
        imagePanelRight.addMouseListener(morphController);
        imagePanelRight.addMouseMotionListener(morphController);

        // give the lattice controller references to panels
        morphController.setLeftPanel(imagePanelLeft);
        morphController.setRightPanel(imagePanelRight);

        // add the components to the organizing panels
        outerPanelLeft.add(startImageLabel, BorderLayout.NORTH);
        outerPanelLeft.add(imagePanelLeft, BorderLayout.CENTER);
        outerPanelLeft.add(loadImageButtonLeft, BorderLayout.SOUTH);

        outerPanelRight.add(endImageLabel, BorderLayout.NORTH);
        outerPanelRight.add(imagePanelRight, BorderLayout.CENTER);
        outerPanelRight.add(loadImageButtonRight, BorderLayout.SOUTH);

        // add the organizing panels to the main window separated by a strut
        contentPanel.add(outerPanelLeft, BorderLayout.WEST);
        contentPanel.add(Box.createHorizontalStrut(50));
        contentPanel.add(outerPanelRight, BorderLayout.EAST);

        // add the panel to the window
        container.add(contentPanel, BorderLayout.WEST);
    }

    /**
     * Create preview button panel that will alternate between being on the main window or on the settings panel,
     * dependent upon whether the settings panel is visible.
     */
    private void createPreviewButtonPanel() {
        // initialize the preview button and add a mouse listener for highlighting button
        previewButton = new JButton("   Preview Morph   ");
        previewButton.setActionCommand("preview");
        previewButton.setName("previewButton");
        previewButton.setBackground(Color.BLACK);
        previewButton.setOpaque(true);
        previewButton.setForeground(Color.WHITE);
        previewButton.setBorder(new LineBorder(Color.WHITE));
        previewButton.addActionListener(morphController);
        previewButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {
                JButton source = (JButton) e.getSource();

                if (!morphController.getIsMorphing()) {
                    source.setBorder(new LineBorder(Color.ORANGE));
                    source.setBackground(Color.DARK_GRAY);
                    source.setForeground(Color.ORANGE);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                JButton source = (JButton) e.getSource();

                if (!morphController.getIsMorphing()) {
                    source.setBorder(new LineBorder(Color.WHITE));
                    source.setBackground(Color.BLACK);
                    source.setForeground(Color.WHITE);
                }
            }
        });

        // create the panel, set its color, and add the button
        previewButtonPanel = new JPanel();
        previewButtonPanel.setBackground(Color.BLACK);
        previewButtonPanel.setOpaque(true);
        previewButtonPanel.add(previewButton);

        // add to the settings panel initially
        settingsPanel.getMorphSettingsPanel().add(previewButtonPanel);
    }

    /**
     * Set up load image buttons.
     */
    private void initializeLoadImageButtons() {
        loadImageButtonLeft = new JButton("   Load Image   ");
        loadImageButtonLeft.setActionCommand("left");

        loadImageButtonLeft.setBackground(Color.BLACK);
        loadImageButtonLeft.setBorder(new LineBorder(Color.WHITE));
        loadImageButtonLeft.setOpaque(true);
        loadImageButtonLeft.setForeground(Color.WHITE);

        loadImageButtonLeft.addMouseListener(this);
        loadImageButtonLeft.addActionListener(this);

        loadImageButtonRight = new JButton("   Load Image   ");
        loadImageButtonRight.setActionCommand("right");

        loadImageButtonRight.setBackground(Color.BLACK);
        loadImageButtonRight.setBorder(new LineBorder(Color.WHITE));
        loadImageButtonRight.setOpaque(true);
        loadImageButtonRight.setForeground(Color.WHITE);

        loadImageButtonRight.addMouseListener(this);
        loadImageButtonRight.addActionListener(this);
    }

    /**
     * Create the settings panel and add it to the window.
     */
    private void createSettingsPanel() {
        settingsPanel = new SettingsPanel(180, imagePanelHeight+175, morphController);
        settingsSeparator = new JSeparator(SwingConstants.VERTICAL);

        container.add(settingsSeparator);
        container.add(settingsPanel, BorderLayout.EAST);
    }

    /**
     * Action handlers for the load image buttons
     * @param e the ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        boolean isLeftPanel = false;

        // file chooser for image
        JFileChooser fileChooser = new JFileChooser(".");

        // panel to add the image to
        ImagePanel panel;

        // the image that will be added
        BufferedImage image;

        // which button was pressed
        JButton source = (JButton) e.getSource();

        // do nothing if the lattice is currently morphing
        if (morphController.getIsMorphing())
            return;

        // which panel is being set
        if (source.getActionCommand().equals("left")) {
            panel = imagePanelLeft;
            isLeftPanel = true;
        } else {
            if (imagePanelLeft.getImage() == null) {
                String message = "Please set the Start Image first!";
                String title = "Error";

                JOptionPane.showConfirmDialog(
                        this, message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE
                );

                return;
            }

            panel = imagePanelRight;
        }

        // get the user response
        int userChoice = fileChooser.showOpenDialog(panel);
        if (userChoice == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String filePath = file.getAbsolutePath();

            editsMade = true;

            // try to load the image
            try {
                image = ImageIO.read(file);

                // resizes everything based on dimensions of image loaded in
                if (panel == imagePanelLeft) {
                    imagePanelWidth = panel.getAdjustedWidth(image);
                    imagePanelHeight = panel.getAdjustedHeight(image);
                    setDimensions();
                }

                panel.setImage(image, isLeftPanel);
                panel.setImagePath(filePath);
                panel.repaint();
            } catch (IOException exception) {
                System.err.println(exception.getMessage());
            }
        }
    }

    /**
     * Resizes everything. Used when setting the Left Image
     */
    public void setDimensions() {
        container.removeAll();

        imagePanelLeft.setWidth(imagePanelWidth);
        imagePanelLeft.setHeight(imagePanelHeight);
        imagePanelLeft.setPreferredSize(new Dimension(imagePanelWidth, imagePanelHeight));
        imagePanelLeft.initializeLattice();

        imagePanelRight.setWidth(imagePanelWidth);
        imagePanelRight.setHeight(imagePanelHeight);
        imagePanelRight.setPreferredSize(new Dimension(imagePanelWidth, imagePanelHeight));
        imagePanelRight.initializeLattice();

        outerPanelLeft.setPreferredSize(new Dimension(imagePanelWidth, imagePanelHeight + 175));

        outerPanelRight.setPreferredSize(new Dimension(imagePanelWidth, imagePanelHeight + 175));

        contentPanel.setPreferredSize(new Dimension(imagePanelWidth * 2 + 100, imagePanelHeight + 175));

        settingsPanel.setPreferredSize(new Dimension(180, imagePanelHeight + 175));
        settingsPanel.getMorphSettingsPanel().setPreferredSize(new Dimension(180, imagePanelHeight + 175));
        settingsPanel.getImageSettingsPanel().setPreferredSize(new Dimension(180, imagePanelHeight + 175));

        container.add(contentPanel, BorderLayout.WEST);
        container.add(settingsSeparator);
        container.add(settingsPanel, BorderLayout.EAST);

        this.setSize(new Dimension(imagePanelWidth*2 + 285, imagePanelHeight + 175));

        imagePanelLeft.repaint();
        imagePanelRight.repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    /**
     * Highlights load image buttons on mouse over
     * @param e the mouse over event
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        JButton source = (JButton) e.getSource();

        if (!morphController.getIsMorphing()) {
            source.setBorder(new LineBorder(Color.ORANGE));
            source.setBackground(Color.DARK_GRAY);
            source.setForeground(Color.ORANGE);
        }
    }

    /**
     * De-highlights image buttons when mouse away from button
     * @param e
     */
    @Override
    public void mouseExited(MouseEvent e) {
        JButton source = (JButton) e.getSource();

        if (!morphController.getIsMorphing()) {
            source.setBorder(new LineBorder(Color.WHITE));
            source.setBackground(Color.BLACK);
            source.setForeground(Color.WHITE);
        }
    }
}
