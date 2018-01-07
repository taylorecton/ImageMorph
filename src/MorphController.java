/*
 * File:       LatticeController.java
 * Author:     Taylor Ecton
 *
 * Purpose:    Implements various listeners used for controlling the lattice.
 *
 */

import Jama.Matrix;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;

public class MorphController implements ActionListener, ChangeListener, MouseListener, MouseMotionListener {
    // References to the left and right ImagePanel objects and the ImageMorphWindow
    private ImagePanel leftPanel, rightPanel;
    private ImageMorphWindow window;

    // Boolean value indicating if control point is being dragged
    private boolean isDragging, isMorphing, hasMorphed, exporting;

    // The indices of the currently dragged control point
    private int[] controlPointIndices;

    private int dimension;

    // x and y coordinates of points in panel that is being manipulated
    private int[][] xCoords, yCoords;

    // Coordinates of left panel before a morph preview takes place
    private int[][] startXCoords, startYCoords;
    private Polygon[][] startUpperTriangles, startLowerTriangles, endUpperTriangles, endLowerTriangles;

    private BufferedImage startImage, endImage;

    private boolean showingLattice, showingControlPoints;

    // Morph duration in seconds
    private int morphDuration;

    private int imageNumber;
    private String exportDirectory;

    // t is t in equation X(t) = ((1-t) * X_start) + (t * X_end)
    // deltaT is increase in t each time morphTimer goes off
    private double t, deltaT;

    // timer that controls the morph preview
    private Timer morphTimer;

    /**
     * Constructor for MorphController.
     * @param window The ImageMorphWindow.
     */
    public MorphController(ImageMorphWindow window) {
        this.window = window;

        isDragging = false;
        isMorphing = false;
        hasMorphed = false;
        exporting = false;

        morphDuration = 5;

        imageNumber = 1;
        exportDirectory = "./";
    }

    /**
     * @return int morphDuration; getter primarily used for saving projects
     */
    public int getMorphDuration() { return morphDuration; }

    /**
     * @return int[][] startXCoords; allows a project that has been morphed to be saved
     */
    public int[][] getStartXCoords() { return startXCoords; }

    /**
     * @return int[][] startYCoords; allows a project that has been morphed to be saved
     */
    public int[][] getStartYCoords() { return startYCoords; }

    /**
     * @return boolean isMorphing; used to prevent interaction with buttons outside lattice controller
     */
    public boolean getIsMorphing() { return isMorphing; }

    /**
     * @return boolean hasMorphed; Used to prevent an export while left panel has morphed image set
     */
    public boolean getHasMorphed() { return hasMorphed; }

    /**
     * @return boolean exporting
     */
    public boolean getExporting() { return exporting; }

    /**
     * Sets the duration of the morph.
     * @param duration The duration to be set.
     */
    public void setMorphDuration(int duration) { morphDuration = duration; }

    /**
     * Set whether the morph should be exporting.
     * @param exporting boolean indicating if the morph should export.
     */
    public void setExporting(boolean exporting) { this.exporting = exporting; }

    /**
     * Sets the directory where an exported morph will be saved.
     * @param directory
     */
    public void setExportDirectory(String directory) { exportDirectory = directory; }

    /**
     * Setter for leftPanel.
     * @param leftPanel The left ImagePanel.
     */
    public void setLeftPanel(ImagePanel leftPanel) {
        this.leftPanel = leftPanel;
    }

    /**
     * Setter for rightPanel.
     * @param rightPanel The right ImagePanel.
     */
    public void setRightPanel(ImagePanel rightPanel) {
        this.rightPanel = rightPanel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    /**
     * Overrides mousePressed event to handle control points being clicked.
     * @param e The mousePressed event.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        // Get the ImagePanel being clicked
        ImagePanel panel = (ImagePanel) e.getSource();

        // Get the panel's x and y coordinates
        xCoords = panel.getXCoords();
        yCoords = panel.getYCoords();

        // Check if any of the control points contain the click
        if ((controlPointIndices = panel.getControlPoint(e.getPoint())) != null && !hasMorphed) {
            // If click is in control point, set isDragging to true
            isDragging = true;

            // Set edits made to true
            window.setEditsMade(true);

            // Set the selected control point to highlighted in both panels
            leftPanel.setIsHighlighted(controlPointIndices, true);
            rightPanel.setIsHighlighted(controlPointIndices, true);

            // Repaint the panels
            repaintPanels();
        }
    }

    /**
     * Override the mouseReleased method to handle control point being released
     * @param e The mouseReleased event.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        // Check isDragging; controlPointIndices is null until isDragging has been set true at least once
        if (isDragging) {
            // Set isDragging to false
            isDragging = false;

            // Set selected control point to not highlighted in both panels
            leftPanel.setIsHighlighted(controlPointIndices, false);
            rightPanel.setIsHighlighted(controlPointIndices, false);

            // Repaint the panels
            repaintPanels();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    /**
     * Override the mouseDragged method to handle control points being dragged.
     * @param e The mouseDragged event.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        // Get the panel where the dragging is occurring.
        ImagePanel panel = (ImagePanel) e.getSource();

        if (isDragging) {
            // Get the indices of the control point
            int i = controlPointIndices[0];
            int j = controlPointIndices[1];

            if (!panel.inSurroundingTriangles(e.getPoint(), controlPointIndices))
                return;

            // Set the coordinates at the control point indices to the x and y of the
            // mouse position
            xCoords[i][j] = e.getX();
            yCoords[i][j] = e.getY();

            // Update the x and y coordinates in the panel
            panel.setXCoords(xCoords);
            panel.setYCoords(yCoords);

            // Repaint the panels
            panel.repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {}

    /**
     * Override the actionPerformed method to handle events in the SettingsPanel
     * @param e The ActionEvent -- Something in SettingsPanel was changed.
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        // Set edits made to true
        window.setEditsMade(true);

        // If the source is a JCheckBox
        if (e.getSource() instanceof JCheckBox) {
            // Get the source
            JCheckBox source = (JCheckBox) e.getSource();

            // Set showControlPoints or showLattice based on source
            if (source.getName().equals("showControlPoints")) {
                leftPanel.setShowControlPoints(source.isSelected());
                rightPanel.setShowControlPoints(source.isSelected());

                repaintPanels();
            } else if (source.getName().equals("showLattice")) {
                leftPanel.setShowLattice(source.isSelected());
                rightPanel.setShowLattice(source.isSelected());

                repaintPanels();
            }
        } else if (e.getSource() instanceof  JComboBox) {
            // If the source is a JComboBox, get the source
            JComboBox<String> source = (JComboBox<String>) e.getSource();

            // Set color of correct component based on source
            if (source.getName().equals("primaryCP")) {
                // Get the selected color in the source
                String color = (String) source.getSelectedItem();
                Color newColor = getColorFromString(color);

                leftPanel.setPrimaryCPColor(newColor);
                rightPanel.setPrimaryCPColor(newColor);

                repaintPanels();
            } else if (source.getName().equals("highlightCP")) {
                // Get the selected color in the source
                String color = (String) source.getSelectedItem();
                Color newColor = getColorFromString(color);

                leftPanel.setHighlightedCPColor(newColor);
                rightPanel.setHighlightedCPColor(newColor);

                repaintPanels();
            } else if (source.getName().equals("primaryL")) {
                // Get the selected color in the source
                String color = (String) source.getSelectedItem();
                Color newColor = getColorFromString(color);

                leftPanel.setPrimaryLColor(newColor);
                rightPanel.setPrimaryLColor(newColor);

                repaintPanels();
            } else if (source.getName().equals("highlightL")) {
                // Get the selected color in the source
                String color = (String) source.getSelectedItem();
                Color newColor = getColorFromString(color);

                leftPanel.setHighlightedLColor(newColor);
                rightPanel.setHighlightedLColor(newColor);

                repaintPanels();
            } else if (source.getName().equals("duration")) {
                String durationString = (String) source.getSelectedItem();
                String[] durationStringSplit = durationString.split(" ");

                try {
                    morphDuration = Integer.parseInt(durationStringSplit[0]);
                } catch (NumberFormatException ex) {
                    System.err.println(ex.getMessage());
                }
            } else if (source.getName().equals("latticeResolution")) {
                String resolutionString = (String) source.getSelectedItem();
                String[] resStringSplit = resolutionString.split("x");

                try {
                    int resolution = Integer.parseInt(resStringSplit[0]);

                    leftPanel.setNumberControlPoints(resolution);
                    rightPanel.setNumberControlPoints(resolution);

                    leftPanel.initializeLattice();
                    rightPanel.initializeLattice();

                    repaintPanels();
                } catch (NumberFormatException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        } else if (e.getSource() instanceof JButton) {
            // The source is a button -- either preview morph or reset lattice
            JButton source = (JButton) e.getSource();
            JLabel imageLabel = window.getStartImageLabel();

            if (source.getActionCommand().equals("preview") && !isMorphing) {

                if (leftPanel.getImage() == null || rightPanel.getImage() == null) {
                    String message = "You must set both a start and end image to perform a morph!";
                    String title = "Error Exporting";

                    JOptionPane.showConfirmDialog(
                            window, message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE
                    );

                    return;
                }

                // update button text and action
                source.setText("   Reset Morph   ");
                source.setActionCommand("reset");

                // relabel the Start Image label
                imageLabel.setText("Morphing... 0%");
                imageLabel.setForeground(Color.GREEN);

                // set hasMorphed to prevent interaction with lattice/control points
                hasMorphed = true;
                exporting = false;

                morph();
            } else if (source.getActionCommand().equals("reset") && !isMorphing) {
                // reset button text and action
                source.setText("   Preview Morph   ");
                source.setActionCommand("preview");

                leftPanel.setShowLattice(showingLattice);
                leftPanel.setShowControlPoints(showingControlPoints);


                // reset left image label
                imageLabel.setText("Start Image");
                imageLabel.setForeground(Color.WHITE);

                // reset coordinates to pre-morph coordinates
                leftPanel.setImage(startImage, true);
                leftPanel.setXCoords(startXCoords);
                leftPanel.setYCoords(startYCoords);

                // repaint the panel
                leftPanel.repaint();

                // re-allow interaction with lattice
                hasMorphed = false;
            }
        }
    }

    /**
     * Overrides stateChanged to use Sliders for adjusting image brightness.
     * May add more sliders to adjust more image settings at later time.
     * @param e The event that triggered this function call.
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        float scaleFactor;
        RescaleOp op;
        BufferedImage bim;

        // Scale factor used to brighten the image
        scaleFactor = (source.getValue() / 100.f) + 1.f;

        // operation for scaling image brightness
        op = new RescaleOp(scaleFactor, 0, null);

        // scale the appropriate image
        if (source.getName().equals("start")) {
            if (startImage == null && leftPanel.getImage() != null)
                startImage = leftPanel.getImage();
            else if (leftPanel.getImage() == null)
                return;

            bim = new BufferedImage(startImage.getWidth(), startImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            bim.getGraphics().drawImage(startImage, 0, 0, null);
            bim = op.filter(bim, bim);
            leftPanel.setImage(bim, true);
        } else if (source.getName().equals("end")) {
            if (endImage == null && rightPanel.getImage() != null)
                endImage = rightPanel.getImage();
            else if (rightPanel.getImage() == null)
                return;

            bim = new BufferedImage(endImage.getWidth(), endImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            bim.getGraphics().drawImage(endImage, 0, 0, null);
            bim = op.filter(bim, bim);
            rightPanel.setImage(bim, false);
        }
        repaintPanels();

    }

    /**
     * Runs morph, either as a preview or for an export.
     */
    public void morph() {
        String labelText;

        // coordinates the preview will have at the end of the animation
        int[][] endXCoords = rightPanel.getXCoords();
        int[][] endYCoords = rightPanel.getYCoords();

        // get the image label and the preview button to make updates
        JLabel imageLabel = window.getStartImageLabel();
        JButton previewButton = window.getPreviewButton();

        // set isMorphing to prevent interaction with certain buttons
        isMorphing = true;

        // text that will appear above the left image while morphing
        if (exporting) {
            labelText = "Exporting... ";
        } else {
            labelText = "Morphing... ";
        }

        // get value of showLattice and showControlPoints to reset them after morph
        showingLattice = leftPanel.getShowLattice();
        showingControlPoints = leftPanel.getShowControlPoints();

        // remove lattice and control points for morph
        leftPanel.setShowControlPoints(false);
        leftPanel.setShowLattice(false);

        // update all of the initial values that will be needed to calculate the morph
        dimension = leftPanel.getNumberControlPoints();
        startImage = leftPanel.getImage();
        endImage = rightPanel.getImage();
        startXCoords = leftPanel.getXCoords();
        startYCoords = leftPanel.getYCoords();
        startLowerTriangles = leftPanel.getLowerTriangles();
        startUpperTriangles = leftPanel.getUpperTriangles();
        endLowerTriangles = rightPanel.getLowerTriangles();
        endUpperTriangles = rightPanel.getUpperTriangles();

        // initialize t to zero
        t = 0;

        // deltaT is 1 divided by the total number of frames the animation should have
        deltaT = 1.0 / (30 * morphDuration);

        // delay for timer is set to 1000/30 which should give 30 frames per second
        morphTimer = new Timer(1000/30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // used for label of preview
                int percentComplete = (int) (t * 100);

                // update preview label
                imageLabel.setText(labelText + percentComplete + "%");

                // temporary x and y coordinates
                int[][] currentXCoords = new int[dimension][dimension];
                int[][] currentYCoords = new int[dimension][dimension];

                // Calculate the next positions to draw
                for (int i = 0; i < dimension; i++) {
                    for (int j = 0; j < dimension; j++) {
                        // If coordinates are already in right position, don't do calculation
                        // Prevents shakiness in fixed points that results from rounding
                        if ((currentXCoords[i][j] == endXCoords[i][j] && currentYCoords[i][j] == endYCoords[i][j]))
                            continue;

                        // Update current x and y coordinates
                        currentXCoords[i][j] = (int) ((1 - t)*startXCoords[i][j] + t*endXCoords[i][j]);
                        currentYCoords[i][j] = (int) ((1 - t)*startYCoords[i][j] + t*endYCoords[i][j]);
                    }
                }

                // calculate the values for the current lower and upper triangles
                // they are calculated in this class rather than the image panel;
                // If you try to set the triangles in the image panels and then use the getUpper/getLowerTriangles
                // methods of the ImagePanel class, they do not return the correct values; I suspect it has something
                // to do with the timer.
                Polygon[][] currentLowerTriangles = getLowerTriangles(currentXCoords, currentYCoords);
                Polygon[][] currentUpperTriangles = getUpperTriangles(currentXCoords, currentYCoords);

                // Image where warped start image will be placed
                BufferedImage destImage1 = new BufferedImage(startImage.getWidth(),
                        startImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

                // Image where warped endImage will be placed
                BufferedImage destImage2 = new BufferedImage(startImage.getWidth(),
                        startImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

                // Warp the images according the to triangles
                calculateAndApplyAffineTransforms(startImage, destImage1, startLowerTriangles, currentLowerTriangles);
                calculateAndApplyAffineTransforms(startImage, destImage1, startUpperTriangles, currentUpperTriangles);
                calculateAndApplyAffineTransforms(endImage, destImage2, endLowerTriangles, currentLowerTriangles);
                calculateAndApplyAffineTransforms(endImage, destImage2, endUpperTriangles, currentUpperTriangles);

                // set the alpha for the images
                float alpha;
                if (t <= 1.0)
                    alpha = (float) t;
                else
                    alpha = 1.f;

                // draw the two images together in a single image, setting alphas appropriately
                BufferedImage destImage = new BufferedImage(endImage.getWidth(), endImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = destImage.createGraphics();
                AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1 - alpha);
                g2.setComposite(ac);
                g2.drawImage(destImage1, 0, 0, null);

                ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
                g2.setComposite(ac);
                g2.drawImage(destImage2, 0, 0, null);
                g2.dispose();

                // update the image in the left panel
                leftPanel.setImage(destImage, true);
                leftPanel.repaint();

                // write the image if exporting
                if (exporting) {
                    writeImage(destImage, imageNumber);
                    imageNumber++;
                }

                // increment t by deltaT
                t += deltaT;

                // check if we have reached the end of the animation
                if (t >= 1.0) {
                    // stop the timer and update the label
                    morphTimer.stop();

                    imageLabel.setText(labelText + "100%");

                    // reset values if exporting
                    if (exporting) {
                        imageLabel.setText("Start Image");
                        leftPanel.setImage(startImage, true);
                        leftPanel.setShowControlPoints(showingControlPoints);
                        leftPanel.setShowLattice(showingLattice);
                        leftPanel.repaint();
                    }

                    isMorphing = false;
                    exporting = false;

                    // reset imageNumber
                    imageNumber = 1;

                    // reset the preview button colors so button does not stay highlighted after animation
                    previewButton.setBackground(Color.BLACK);
                    previewButton.setForeground(Color.WHITE);
                    previewButton.setBorder(new LineBorder(Color.WHITE));

                }
            }
        });


        morphTimer.start();
    }

    /**
     * Writes an image with name "image-${imageNumber}.jpg" in the specified export directory.
     * @param image The image to be written.
     * @param imageNumber The number to be attached to the image.
     */
    private void writeImage(BufferedImage image, int imageNumber) {
        BufferedImage typeConverted = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        String imageName = exportDirectory + "/image-" + imageNumber + ".jpg";
        typeConverted.getGraphics().drawImage(image, 0, 0, null);

        try {
            File output = new File(imageName);
            ImageIO.write(typeConverted, "jpg", output);
        } catch (IOException e1) {
            System.err.println(e1.getMessage());
        }
    }

    /**
     * Calculates and applies affine transforms to all of the source triangles based on destination triangles.
     * @param src Source image.
     * @param dest Destination image.
     * @param srcTriangles Source triangles.
     * @param destTriangles The destination triangles where the source triangles will be mapped.
     */
    private void calculateAndApplyAffineTransforms(BufferedImage src,
                                                   BufferedImage dest,
                                                   Polygon[][] srcTriangles,
                                                   Polygon[][] destTriangles) {

        /*
         * The following two matrix equations will be solved:
         *
         *     A * x1 = bX
         *     A * x2 = bY
         *
         * where A is a 3x3 matrix composed of the (x, y) coordinates of the source triangles (and a row of ones in the
         * third column), bX is a 3x1 matrix consisting of the x-coordinates of the destination triangles, and
         * bY is a 3x1 matrix consisting of the y-coordinates of the destination triangles.
         *
         * x1 and x2 are used to create the affine transform to be applied to create the destination image.
         */
        Matrix A;
        Matrix x1;
        Matrix x2;
        Matrix bX;
        Matrix bY;

        double[][] AVals;
        double[][] bXVals;
        double[][] bYVals;

        for (int i = 0; i < dimension+1; i++) {
            for (int j = 0; j < dimension+1; j++) {
                AVals = new double[][]{
                        {(double) srcTriangles[i][j].xpoints[0], (double) srcTriangles[i][j].ypoints[0], 1.d},
                        {(double) srcTriangles[i][j].xpoints[1], (double) srcTriangles[i][j].ypoints[1], 1.d},
                        {(double) srcTriangles[i][j].xpoints[2], (double) srcTriangles[i][j].ypoints[2], 1.d}
                };

                bXVals = new double[][] {
                        {(double) destTriangles[i][j].xpoints[0]},
                        {(double) destTriangles[i][j].xpoints[1]},
                        {(double) destTriangles[i][j].xpoints[2]}
                };

                bYVals = new double[][] {
                        {(double) destTriangles[i][j].ypoints[0]},
                        {(double) destTriangles[i][j].ypoints[1]},
                        {(double) destTriangles[i][j].ypoints[2]}
                };

                A = new Matrix(AVals);
                bX = new Matrix(bXVals);
                bY = new Matrix(bYVals);

                x1 = A.solve(bX);
                x2 = A.solve(bY);

                // everything below in this function is based on the code from Dr. Seales' slides on morphing
                AffineTransform affineTransform = new AffineTransform(x1.get(0, 0), x2.get(0,0),
                                                                      x1.get(1, 0), x2.get(1 , 0),
                                                                      x1.get(2, 0), x2.get(2, 0));

                GeneralPath destPath = new GeneralPath(GeneralPath.WIND_EVEN_ODD);

                destPath.moveTo((float) bX.get(0, 0), (float) bY.get(0, 0));
                destPath.lineTo((float) bX.get(1, 0), (float) bY.get(1, 0));
                destPath.lineTo((float) bX.get(2, 0), (float) bY.get(2, 0));
                destPath.lineTo((float) bX.get(0, 0), (float) bY.get(0, 0));

                Graphics2D g2 = dest.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.clip(destPath);
                g2.setTransform(affineTransform);
                g2.drawImage(src, 0, 0, null);
                g2.dispose();
            }
        }
    }

    /**
     * Calculates upperTriangles for in between images.
     * @param xCoords current x coordinates
     * @param yCoords current y coordinates
     * @return 2D polygon array consisting of upper triangles for in between images of morph
     */
    private Polygon[][] getUpperTriangles(int[][] xCoords, int[][] yCoords) {
        Polygon[][] upperTriangles = new Polygon[dimension+1][dimension+1];

        int width = leftPanel.getWidth();
        int height = leftPanel.getHeight();
        double widthOffset = leftPanel.getWidthOffset();
        double heightOffset = leftPanel.getHeightOffset();

        for (int i = 0; i < dimension+1; i++) {
            for (int j = 0; j < dimension+1; j++) {
                // coordinates for the triangle
                int[] triangleXCoords;
                int[] triangleYCoords;

                // coordinates for each corner of the triangle
                int aX, aY, bX, bY, cX, cY;

                // get coordinates for first corner
                if (i == 0 && j == 0) {
                    aX = 0;
                    aY = 0;
                } else if (i == 0) {
                    aX = (int) widthOffset * j;
                    aY = 0;
                } else if (j == 0) {
                    aX = 0;
                    aY =(int) heightOffset * i;
                } else {
                    aX = xCoords[i-1][j-1];
                    aY = yCoords[i-1][j-1];
                }

                // get coordinates for second corner
                if (i == 0 && j == dimension) {
                    bX = width;
                    bY = 0;
                } else if (j == dimension) {
                    bX = width;
                    bY = (int) heightOffset * i;
                } else if (i == 0) {
                    bX = (int) widthOffset * (j+1);
                    bY = 0;
                } else {
                    bX = xCoords[i-1][j];
                    bY = yCoords[i-1][j];
                }

                // get coordinates for third corner
                if (j == 0 && i == dimension) {
                    cX = 0;
                    cY = height;
                } else if (j == 0) {
                    cX = 0;
                    cY = (int) heightOffset * (i+1);
                } else if (i == dimension) {
                    cX = (int) widthOffset * j;
                    cY = height;
                } else {
                    cX = xCoords[i][j-1];
                    cY = yCoords[i][j-1];
                }

                // initialize coordinate arrays for this triangle
                triangleXCoords = new int[]{aX, bX, cX};
                triangleYCoords = new int[]{aY, bY, cY};

                // create the triangle and draw it if it is visible
                upperTriangles[i][j] = new Polygon(triangleXCoords, triangleYCoords, 3);
            }
        }

        return upperTriangles;
    }

    /**
     * Calculates lowerTriangles for in between images.
     * @param xCoords current x coordinates
     * @param yCoords current y coordinates
     * @return 2D polygon array consisting of lower triangles for in between images of morph
     */
    private Polygon[][] getLowerTriangles(int[][] xCoords, int[][] yCoords) {
        Polygon[][] lowerTriangles = new Polygon[dimension+1][dimension+1];

        int width = leftPanel.getWidth();
        int height = leftPanel.getHeight();
        double widthOffset = leftPanel.getWidthOffset();
        double heightOffset = leftPanel.getHeightOffset();

        for (int i = 0; i < dimension+1; i++) {
            for (int j = 0; j < dimension+1; j++) {
                // coordinates for the triangle
                int[] triangleXCoords;
                int[] triangleYCoords;

                // coordinates for each corner of the triangle
                int aX, aY, bX, bY, cX, cY;

                // set the coordinates of first corner
                if (i == 0 && j == dimension) {
                    aX = width;
                    aY = 0;
                } else if (i == 0) {
                    aX = (int) widthOffset * (j + 1);
                    aY = 0;
                } else if (j == dimension) {
                    aX = width;
                    aY = (int) heightOffset * i;
                } else {
                    aX = xCoords[i-1][j];
                    aY = yCoords[i-1][j];
                }

                // set coordinates of second corner
                if (i == dimension && j == dimension) {
                    bX = width;
                    bY = height;
                } else if (j == dimension) {
                    bX = width;
                    bY = (int) heightOffset * (i+1);
                } else if (i == dimension) {
                    bX = (int) widthOffset * (j+1);
                    bY = height;
                } else {
                    bX = xCoords[i][j];
                    bY = yCoords[i][j];
                }

                // set coordinates of third corner
                if (j == 0 && i == dimension) {
                    cX = 0;
                    cY = height;
                } else if (j == 0) {
                    cX = 0;
                    cY = (int) heightOffset * (i+1);
                } else if (i == dimension) {
                    cX = (int) widthOffset * j;
                    cY = height;
                } else {
                    cX = xCoords[i][j-1];
                    cY = yCoords[i][j-1];
                }

                // create coordinate arrays
                triangleXCoords = new int[]{aX, bX, cX};
                triangleYCoords = new int[]{aY, bY, cY};

                // create triangle and draw it if it's visible
                lowerTriangles[i][j] = new Polygon(triangleXCoords, triangleYCoords, 3);
            }
        }

        return lowerTriangles;
    }

    /**
     * Converts String color name to Color.
     * @param name The name of the color.
     * @return The Color that matches the String.
     */
    private Color getColorFromString(String name) {
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
            case "White":
                color = Color.WHITE;
                break;
            default:
                break;
        }

        return color;
    }

    /**
     * Helper function that repaints both panels.
     */
    private void repaintPanels() {
        leftPanel.repaint();
        rightPanel.repaint();
    }
}
