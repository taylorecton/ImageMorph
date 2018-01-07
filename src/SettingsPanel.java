import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JTabbedPane {
    // check boxes allowing user to specify whether to show or hide control points and lattice
    private JCheckBox showControlPoints, showLattice;

    private JPanel morphSettingsPanel, imageSettingsPanel;

    // labels for the combo boxes
    private JLabel primaryCPColor, highlightCPColor,
                   primaryLColor,  highlightLColor;

    // selectors for colors and the morph duration
    private JComboBox<String> primaryCPColorSelector, highlightCPColorSelector,
                              primaryLColorSelector,  highlightLColorSelector,
                              latticeResolutionSelector, durationSelector;

    // colors the user can choose from
    private String[] colors = { "Orange", "Green", "Blue", "Red", "Yellow", "Black", "White" };

    // lattice resolutions
    private String[] resolutions = { "5x5", "10x10", "20x20" };

    // fonts used in the panel
    private Font sectionLabelFont = new Font(Font.SANS_SERIF, Font.BOLD, 16);
    private Font subSectionLabelFont = new Font(Font.SANS_SERIF, Font.PLAIN, 11);

    private Dimension comboBoxDimension = new Dimension(125, 20);

    /**
     * Constructor
     * @param width The width of the panel.
     * @param height The height of the panel.
     * @param listener The listener that responds to events on the panel.
     */
    public SettingsPanel(int width, int height, MorphController listener) {
        // set background and size
        this.setBackground(Color.BLACK);
        this.setOpaque(true);
        this.setPreferredSize(new Dimension(width, height));

        // setup sub-panels for control points
        setupPanels(width, height);
        setupShowControlPointsPanel(listener);
        setupPrimaryCPColorPanel(listener);
        setupHighlightCPColorPanel(listener);

        // add a separator for organization
        morphSettingsPanel.add(new JSeparator(JSeparator.HORIZONTAL));

        // setup sub-panels for lattice
        setupShowLatticePanel(listener);
        setupPrimaryLColorPanel(listener);
        setupHighlightLColorPanel(listener);
        setupLatticeResolutionController(listener);

        // separator for organization
        morphSettingsPanel.add(new JSeparator(JSeparator.HORIZONTAL));

        setupImage1SettingsPanels(listener);
        imageSettingsPanel.add(new JSeparator(JSeparator.HORIZONTAL));
        setupImage2SettingsPanels(listener);

        this.addTab("Morph", null, morphSettingsPanel, "Settings for the morph and lattice");
        this.addTab("Images", null, imageSettingsPanel, "Settings for the images");

        // morph sub-panel setup
        setupMorphPanel(listener);
    }

    /**
     * @return showControlPoints JCheckBox
     */
    public JCheckBox getShowControlPoints() { return showControlPoints; }

    /**
     * @return showLattice JCheckBox
     */
    public JCheckBox getShowLattice() { return showLattice; }

    /**
     * @return highlightCPColorSelector JComboBox
     */
    public JComboBox<String> getHighlightCPColorSelector() { return highlightCPColorSelector; }

    /**
     * @return primaryCPColorSelector JComboBox
     */
    public JComboBox<String> getPrimaryCPColorSelector() { return primaryCPColorSelector; }

    /**
     * @return highlightLColorSelector JComboBox
     */
    public JComboBox<String> getHighlightLColorSelector() { return highlightLColorSelector; }

    /**
     * @return primaryLColorSelector JComboBox
     */
    public JComboBox<String> getPrimaryLColorSelector() { return primaryLColorSelector; }

    public JComboBox<String> getLatticeResolutionSelector() {
        return latticeResolutionSelector;
    }

    /**
     * @return durationSelector JComboBox
     */
    public JComboBox<String> getDurationSelector() { return durationSelector; }

    /**
     * @return the morphSettingsPanel
     */
    public JPanel getMorphSettingsPanel() {
        return morphSettingsPanel;
    }

    /**
     * @return the imageSettingsPanel
     */
    public JPanel getImageSettingsPanel() {
        return imageSettingsPanel;
    }

    private void setupPanels(int width, int height) {
        morphSettingsPanel = new JPanel();
        morphSettingsPanel.setBackground(Color.BLACK);
        morphSettingsPanel.setOpaque(true);
        morphSettingsPanel.setPreferredSize(new Dimension(width, height));
        morphSettingsPanel.setLayout(new GridLayout(21, 1, 0, 0));

        imageSettingsPanel = new JPanel();
        imageSettingsPanel.setBackground(Color.BLACK);
        imageSettingsPanel.setOpaque(true);
        imageSettingsPanel.setPreferredSize(new Dimension(width, height));
        imageSettingsPanel.setLayout(new GridLayout(7, 1, 0, 0));
    }

    /**
     * Sets up top portion of Control Points portion of settings panel
     * @param listener The listener
     */
    private void setupShowControlPointsPanel(MorphController listener) {
        // panels to contain components; prevents GridLayout from stretching components
        JPanel controlPointsPanel = new JPanel();
        JPanel showControlPointsPanel = new JPanel();

        // Label for section of settings panel
        JLabel controlPointLabel = new JLabel("Control Points");

        // set color for panels
        controlPointsPanel.setBackground(Color.BLACK);
        controlPointsPanel.setOpaque(true);

        showControlPointsPanel.setBackground(Color.BLACK);
        showControlPointsPanel.setOpaque(true);

        // setup font
        controlPointLabel.setFont(sectionLabelFont);
        controlPointLabel.setForeground(Color.WHITE);

        controlPointsPanel.add(controlPointLabel);

        // setup check box
        showControlPoints = new JCheckBox("Show Control Points");
        showControlPoints.setSelected(true);
        showControlPoints.setForeground(Color.WHITE);
        showControlPoints.setBackground(Color.BLACK);
        showControlPoints.addActionListener(listener);
        showControlPoints.setName("showControlPoints");
        showControlPointsPanel.add(showControlPoints, BorderLayout.SOUTH);

        // add panels to settings
        morphSettingsPanel.add(controlPointsPanel);
        morphSettingsPanel.add(showControlPointsPanel);
    }

    /**
     * Setup the top portion of the lattice portion of settings panel
     * @param listener listener for events
     */
    private void setupShowLatticePanel(MorphController listener) {
        // panels to add components to; prevents stretching when adding to gridlayout
        JPanel showLatticePanel = new JPanel();
        JPanel latticeLabelPanel = new JPanel();

        // label for lattice portion of settings
        JLabel latticeLabel = new JLabel("Lattice");

        // set color for panels
        showLatticePanel.setBackground(Color.BLACK);
        showLatticePanel.setOpaque(true);

        latticeLabelPanel.setBackground(Color.BLACK);
        latticeLabelPanel.setOpaque(true);

        // set font
        latticeLabel.setFont(sectionLabelFont);
        latticeLabel.setForeground(Color.WHITE);

        latticeLabelPanel.add(latticeLabel);

        // setup check box
        showLattice = new JCheckBox("Show Lattice");
        showLattice.setSelected(true);
        showLattice.setForeground(Color.WHITE);
        showLattice.setBackground(Color.BLACK);
        showLattice.addActionListener(listener);
        showLattice.setName("showLattice");
        showLatticePanel.add(showLattice, BorderLayout.SOUTH);

        // add components to settings panel
        morphSettingsPanel.add(latticeLabelPanel);
        morphSettingsPanel.add(showLatticePanel);
    }

    /**
     * Sets up primary control point color selector
     * @param listener The action listener
     */
    private void setupPrimaryCPColorPanel(MorphController listener) {
        // panels to hold components
        JPanel primaryCPColorPanel = new JPanel();
        JPanel primaryCPColorLabelPanel = new JPanel();

        // set color for panels
        primaryCPColorPanel.setBackground(Color.BLACK);
        primaryCPColorPanel.setOpaque(true);

        primaryCPColorLabelPanel.setBackground(Color.BLACK);
        primaryCPColorLabelPanel.setOpaque(true);

        // add label to selector
        primaryCPColor = new JLabel("Primary Color");
        primaryCPColor.setForeground(Color.WHITE);
        primaryCPColor.setFont(subSectionLabelFont);
        primaryCPColorLabelPanel.add(primaryCPColor, BorderLayout.SOUTH);

        // set up combo box
        primaryCPColorSelector = new JComboBox<>(colors);
        primaryCPColorSelector.setSelectedIndex(6);
        primaryCPColorSelector.setName("primaryCP");
        primaryCPColorSelector.setPreferredSize(comboBoxDimension);
        primaryCPColorSelector.addActionListener(listener);
        primaryCPColorPanel.add(primaryCPColorSelector, BorderLayout.SOUTH);

        // add components to settings panel
        morphSettingsPanel.add(primaryCPColorPanel);
        morphSettingsPanel.add(primaryCPColorLabelPanel);
    }

    /**
     * Sets up control point highlight color selector
     * @param listener The action listener
     */
    private void setupHighlightCPColorPanel(MorphController listener) {
        // panels to contain components
        JPanel highlightCPColorPanel = new JPanel();
        JPanel highlightCPColorLablePanel = new JPanel();

        // set color on panels
        highlightCPColorPanel.setBackground(Color.BLACK);
        highlightCPColorPanel.setOpaque(true);

        highlightCPColorLablePanel.setBackground(Color.BLACK);
        highlightCPColorLablePanel.setOpaque(true);

        // label for selector
        highlightCPColor = new JLabel("Highlighted Color");
        highlightCPColor.setForeground(Color.WHITE);
        highlightCPColor.setFont(subSectionLabelFont);
        highlightCPColorLablePanel.add(highlightCPColor);

        // combo box selector
        highlightCPColorSelector = new JComboBox<>(colors);
        highlightCPColorSelector.setSelectedIndex(1);
        highlightCPColorSelector.setPreferredSize(comboBoxDimension);
        highlightCPColorSelector.setName("highlightCP");
        highlightCPColorSelector.addActionListener(listener);
        highlightCPColorPanel.add(highlightCPColorSelector, BorderLayout.SOUTH);

        // add components to settings panel
        morphSettingsPanel.add(highlightCPColorPanel);
        morphSettingsPanel.add(highlightCPColorLablePanel);
    }

    /**
     * Sets up primary color selector for lattice
     * @param listener The listener
     */
    private void setupPrimaryLColorPanel(MorphController listener) {
        // panels to contain components
        JPanel primaryLColorPanel = new JPanel();
        JPanel primaryLColorLabelPanel = new JPanel();

        // set color for components
        primaryLColorPanel.setBackground(Color.BLACK);
        primaryLColorPanel.setOpaque(true);

        primaryLColorLabelPanel.setBackground(Color.BLACK);
        primaryLColorLabelPanel.setOpaque(true);

        // label for selector
        primaryLColor = new JLabel("Primary Color");
        primaryLColor.setForeground(Color.WHITE);
        primaryLColor.setFont(subSectionLabelFont);
        primaryLColorLabelPanel.add(primaryLColor);

        // combo box selector
        primaryLColorSelector = new JComboBox<>(colors);
        primaryLColorSelector.setSelectedIndex(3);
        primaryLColorSelector.setPreferredSize(comboBoxDimension);
        primaryLColorSelector.setName("primaryL");
        primaryLColorSelector.addActionListener(listener);
        primaryLColorPanel.add(primaryLColorSelector, BorderLayout.SOUTH);

        // add components to settings
        morphSettingsPanel.add(primaryLColorPanel);
        morphSettingsPanel.add(primaryLColorLabelPanel);
    }

    /**
     * Sets up selector for highlight color for lattice
     * @param listener The listener
     */
    private void setupHighlightLColorPanel(MorphController listener) {
        // panels to contain the components
        JPanel highlightLColorPanel = new JPanel();
        JPanel highlightLColorLabelPanel = new JPanel();

        // set the panel color
        highlightLColorPanel.setBackground(Color.BLACK);
        highlightLColorPanel.setOpaque(true);

        highlightLColorLabelPanel.setBackground(Color.BLACK);
        highlightLColorLabelPanel.setOpaque(true);

        // label for selector
        highlightLColor = new JLabel("Highlighted Color");
        highlightLColor.setForeground(Color.WHITE);
        highlightLColor.setFont(subSectionLabelFont);
        highlightLColorLabelPanel.add(highlightLColor);

        // combo box selector
        highlightLColorSelector = new JComboBox<>(colors);
        highlightLColorSelector.setSelectedIndex(1);
        highlightLColorSelector.setPreferredSize(comboBoxDimension);
        highlightLColorSelector.setName("highlightL");
        highlightLColorSelector.addActionListener(listener);
        highlightLColorPanel.add(highlightLColorSelector, BorderLayout.SOUTH);

        // add components to settings panel
        morphSettingsPanel.add(highlightLColorPanel);
        morphSettingsPanel.add(highlightLColorLabelPanel);
    }

    private void setupLatticeResolutionController(MorphController listener) {
        JPanel latticeResolutionPanel = new JPanel();
        JPanel latticeResolutionLabelPanel = new JPanel();

        JLabel latticeResolutionLabel = new JLabel("Lattice Resolution");

        latticeResolutionPanel.setBackground(Color.BLACK);
        latticeResolutionPanel.setOpaque(true);

        latticeResolutionLabelPanel.setBackground(Color.BLACK);
        latticeResolutionLabelPanel.setOpaque(true);

        latticeResolutionLabel.setForeground(Color.WHITE);
        latticeResolutionLabel.setFont(subSectionLabelFont);
        latticeResolutionLabelPanel.add(latticeResolutionLabel);

        latticeResolutionSelector = new JComboBox<>(resolutions);
        latticeResolutionSelector.setSelectedIndex(1);
        latticeResolutionSelector.setPreferredSize(comboBoxDimension);
        latticeResolutionSelector.setName("latticeResolution");
        latticeResolutionSelector.addActionListener(listener);
        latticeResolutionPanel.add(latticeResolutionSelector, BorderLayout.SOUTH);
        latticeResolutionPanel.add(new JSeparator(JSeparator.HORIZONTAL));

        morphSettingsPanel.add(latticeResolutionPanel);
        morphSettingsPanel.add(latticeResolutionLabelPanel);
    }

    /**
     * Sets up morph section of panel
     * @param listener listener
     */
    private void setupMorphPanel(MorphController listener) {
        // panels to contain components
        JPanel morphLabelPanel = new JPanel();
        JPanel morphDurationLabelPanel = new JPanel();
        JPanel morphDurationPanel = new JPanel();

        // label for morph section of settings
        JLabel morphLabel = new JLabel("Morph");

        // label for morph duration selector
        JLabel morphDurationLabel = new JLabel("Morph Duration");

        // durations available for morph
        String[] durations = {"1 second", "2 seconds", "3 seconds", "4 seconds",
                              "5 seconds", "6 seconds", "7 seconds", "8 seconds",
                              "9 seconds", "10 seconds", "11 seconds", "12 seconds",
                              "13 seconds", "14 seconds", "15 seconds"};

        // combo box for duration selection
        durationSelector = new JComboBox<>(durations);

        // set panels to black
        morphLabelPanel.setBackground(Color.BLACK);
        morphLabelPanel.setOpaque(true);

        morphDurationLabelPanel.setBackground(Color.BLACK);
        morphDurationLabelPanel.setOpaque(true);

        morphDurationPanel.setBackground(Color.BLACK);
        morphDurationPanel.setOpaque(true);

        // set fonts
        morphLabel.setFont(sectionLabelFont);
        morphLabel.setForeground(Color.WHITE);

        morphDurationLabel.setForeground(Color.WHITE);
        morphDurationLabel.setFont(subSectionLabelFont);

        // initialize durationSelector to default duration (5)
        durationSelector.setSelectedIndex(4);
        durationSelector.setPreferredSize(comboBoxDimension);
        durationSelector.setName("duration");
        durationSelector.addActionListener(listener);

        // add components to panels
        morphLabelPanel.add(morphLabel);
        morphDurationPanel.add(durationSelector);
        morphDurationLabelPanel.add(morphDurationLabel);

        // add panels to settings panel
        morphSettingsPanel.add(morphLabelPanel);
        morphSettingsPanel.add(morphDurationPanel);
        morphSettingsPanel.add(morphDurationLabelPanel);
    }

    /**
     * Settings panel for the first image; part of imageSettingsPanel
     * @param listener The MorphController that serves as the ChangeListener
     */
    private void setupImage1SettingsPanels(MorphController listener) {
        JPanel titlePanel = new JPanel();
        JLabel title = new JLabel("Start Image");
        JPanel brightnessPanel = new JPanel();
        JPanel brightnessLabelPanel = new JPanel();
        JSlider brightnessSlider = new JSlider(JSlider.HORIZONTAL, -100, 100, 0);
        JLabel brightnessLabel = new JLabel("Brightness");

        titlePanel.setBackground(Color.BLACK);
        titlePanel.setOpaque(true);

        title.setBackground(Color.BLACK);
        title.setForeground(Color.WHITE);
        title.setFont(sectionLabelFont);

        titlePanel.add(title);

        brightnessPanel.setBackground(Color.BLACK);
        brightnessPanel.setOpaque(true);

        brightnessSlider.setSnapToTicks(false);
        brightnessSlider.setMajorTickSpacing(25);
        brightnessSlider.setPaintTicks(true);
        brightnessSlider.setPreferredSize(new Dimension(185, 50));
        brightnessSlider.setName("start");
        brightnessSlider.addChangeListener(listener);

        brightnessPanel.add(brightnessSlider);

        brightnessLabelPanel.setBackground(Color.BLACK);
        brightnessLabelPanel.setOpaque(true);

        brightnessLabel.setBackground(Color.BLACK);
        brightnessLabel.setForeground(Color.WHITE);
        brightnessLabel.setFont(subSectionLabelFont);

        brightnessLabelPanel.add(brightnessLabel);

        imageSettingsPanel.add(titlePanel);
        imageSettingsPanel.add(brightnessPanel);
        imageSettingsPanel.add(brightnessLabelPanel);
    }

    /**
     * Settings panel for the second image; part of imageSettingsPanel
     * @param listener The MorphController that serves as the ChangeListener
     */
    private void setupImage2SettingsPanels(MorphController listener) {
        JPanel titlePanel = new JPanel();
        JLabel title = new JLabel("End Image");
        JPanel brightnessPanel = new JPanel();
        JPanel brightnessLabelPanel = new JPanel();
        JSlider brightnessSlider = new JSlider(JSlider.HORIZONTAL, -100, 100, 0);
        JLabel brightnessLabel = new JLabel("Brightness");

        titlePanel.setBackground(Color.BLACK);
        titlePanel.setOpaque(true);

        title.setBackground(Color.BLACK);
        title.setForeground(Color.WHITE);
        title.setFont(sectionLabelFont);

        titlePanel.add(title);

        brightnessPanel.setBackground(Color.BLACK);
        brightnessPanel.setOpaque(true);

        brightnessSlider.setSnapToTicks(false);
        brightnessSlider.setMajorTickSpacing(25);
        brightnessSlider.setPaintTicks(true);
        brightnessSlider.setPreferredSize(new Dimension(185, 50));
        brightnessSlider.setName("end");
        brightnessSlider.addChangeListener(listener);

        brightnessPanel.add(brightnessSlider);

        brightnessLabelPanel.setBackground(Color.BLACK);
        brightnessLabelPanel.setOpaque(true);

        brightnessLabel.setBackground(Color.BLACK);
        brightnessLabel.setForeground(Color.WHITE);
        brightnessLabel.setFont(subSectionLabelFont);

        brightnessLabelPanel.add(brightnessLabel);

        imageSettingsPanel.add(titlePanel);
        imageSettingsPanel.add(brightnessPanel);
        imageSettingsPanel.add(brightnessLabelPanel);
    }
}
