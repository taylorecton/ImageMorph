/*
 * File:       ImagePanel.java
 * Author:     Taylor Ecton
 *
 * Purpose:    Provides the panel for holding the image loaded in by the user.
 *
 */

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel {
    // the path of the image in the panel
    private String imagePath;

    // the buffered image in the panel
    private BufferedImage image;

    // x and y coordinates for control points
    private int[][] xCoords, yCoords;

    // boolean indicating whether the control point at the given indices is highlighted
    private boolean[][] isHighlighted;

    // width and height of the panel
    private int width, height;

    // width offset and height offset for setting control points and lattice
    private double widthOffset, heightOffset;

    // the control points, the upper and lower triangles of the lattice
    private Polygon[][] controlPoints, upperTriangles, lowerTriangles;

    // the number of control points
    private int numberControlPoints;

    // booleans determining if control points should be shown or hidden
    private boolean showControlPoints, showLattice;

    // colors for lattice and control points
    private Color primaryCPColor, highlightedCPColor,
                  primaryLColor,  highlightedLColor;

    /**
     * Constructor for ImagePanel
     * @param width Width for the panel.
     * @param height Height for the panel.
     */
    public ImagePanel(int width, int height) {
        // set height and width of panel
        this.width = width;
        this.height = height;

        // initialize number of control points to 10
        this.numberControlPoints = 10;

        // initialize control points and lattice to visible
        showControlPoints = true;
        showLattice = true;

        // set initial colors for control points and lattice
        primaryCPColor = Color.WHITE;
        highlightedCPColor = Color.GREEN;
        primaryLColor = Color.RED;
        highlightedLColor = Color.GREEN;

        // set image path to none initially
        imagePath = "none";

        // initialize isHighlighted to false for all values
        isHighlighted = new boolean[numberControlPoints][numberControlPoints];
        for (int i = 0; i < numberControlPoints; i++) {
            for (int j = 0; j < numberControlPoints; j++) {
                isHighlighted[i][j] = false;
            }
        }

        // initialize triangle arrays
        upperTriangles = new Polygon[numberControlPoints+1][numberControlPoints+1];
        lowerTriangles = new Polygon[numberControlPoints+1][numberControlPoints+1];

        // initialize coordinates for control points
        initializeControlCoords();

        // set the initial background color for the panel and set the size
        this.setBackground(Color.DARK_GRAY);
        this.setOpaque(true);
        this.setPreferredSize(new Dimension(width, height));
    }

    /**
     * @return boolean value showControlPoints
     */
    public boolean getShowControlPoints() { return showControlPoints; }

    /**
     * @return boolean value showLattice
     */
    public boolean getShowLattice() { return showLattice; }

    /**
     * @return 2D int array xCoords
     */
    public int[][] getXCoords() { return xCoords; }

    /**
     * @return 2D int array yCoords
     */
    public int[][] getYCoords() { return yCoords; }

    /**
     * @return The path of the image in the panel
     */
    public String getImagePath() { return imagePath; }

    /**
     * @return int numberControlPoints
     */
    public int getNumberControlPoints() { return numberControlPoints; }

    /**
     * @return primary color for control points
     */
    public Color getPrimaryCPColor() { return primaryCPColor; }

    /**
     * @return highlight color for control points
     */
    public Color getHighlightedCPColor() { return highlightedCPColor; }

    /**
     * @return primary color for lattice
     */
    public Color getPrimaryLColor() { return primaryLColor; }

    /**
     * @return highlighted color for lattice
     */
    public Color getHighlightedLColor() { return highlightedLColor; }

    /**
     * @return upperTriangles, the 2D array of upper triangles in the image
     */
    public Polygon[][] getUpperTriangles() {
        return  upperTriangles;
    }

    /**
     * @return lowerTriangles, the 2D array of lower triangles in image
     */
    public Polygon[][] getLowerTriangles() {
        return lowerTriangles;
    }

    /**
     * @return Current width setting for panel.
     */
    public int getWidth() { return width; }

    /**
     * @return Current height setting for panel.
     */
    public int getHeight() { return height; }

    /**
     * @return widthOffset
     */
    public double getWidthOffset() {return widthOffset; }

    /**
     * @return heightOffset
     */
    public double getHeightOffset() { return heightOffset; }

    /**
     * @return The image for the panel.
     */
    public BufferedImage getImage() { return image; }

    /**
     * Sets the width of the panel
     * @param newWidth width of panel
     */
    public void setWidth(int newWidth) { width = newWidth; }

    /**
     * Sets the height of the panel
     * @param newHeight height of panel
     */
    public void setHeight(int newHeight) { height = newHeight; }

    /**
     * Sets number of control points.
     * @param numberControlPoints the new number of control points
     */
    public void setNumberControlPoints(int numberControlPoints) { this.numberControlPoints = numberControlPoints; }

    /**
     * Sets the image that will be displayed in the panel.
     * @param image BufferedImage that will be displayed.
     */
    public void setImage(BufferedImage image, boolean isLeftPanel) {
        if (image == null)
            return;

        int preScaledW = image.getWidth();
        int preScaledH = image.getHeight();

        // scales the image so that the dimensions do not exceed 600 * 400
        // keeps aspect ratio of image in tact
        // sets dimensions for left image and then uses left image dimensions for right image
        if ((preScaledW * preScaledH) > (600 * 400) && isLeftPanel) {
            BufferedImage afterScaling = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            double ratio = (double) preScaledW / (double) preScaledH;

            width = (int) Math.sqrt(ratio * 600 * 400);
            height = (600 * 400) / width;

            double scaleW = width / (preScaledW + 0.0);
            double scaleH = height / (preScaledH + 0.0);

            AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleW, scaleH);
            AffineTransformOp scaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BICUBIC);

            scaleOp.filter(image, afterScaling);
            this.image = afterScaling;
        } else if (!isLeftPanel && preScaledH != height && preScaledW != width) {
            BufferedImage afterScaling = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            double scaleW = width / (preScaledW + 0.0);
            double scaleH = height / (preScaledH + 0.0);

            AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleW, scaleH);
            AffineTransformOp scaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BICUBIC);

            scaleOp.filter(image, afterScaling);
            this.image = afterScaling;
        } else {
            width = preScaledW;
            height = preScaledH;

            this.image = image;
        }

        this.setPreferredSize(new Dimension(width, height));
    }

    /**
     * Sets the path of the image used in this panel.
     * Used for saving/loading configuration.
     * @param path The path of the image.
     */
    public void setImagePath(String path) { imagePath = path; }

    /**
     * Sets boolean value showControlPoints
     * @param value The value showControlPoints will be set to.
     */
    public void setShowControlPoints(boolean value) { showControlPoints = value; }

    /**
     * Sets boolean value showLattice
     * @param value The value showLattice will be set to
     */
    public void setShowLattice(boolean value) { showLattice = value; }

    /**
     * Sets xCoords.
     * @param newCoords 2D array of updated xCoords
     */
    public void setXCoords(int[][] newCoords) { xCoords = newCoords; }

    /**
     * Sets yCoords.
     * @param newCoords 2D array of updated yCoords
     */
    public void setYCoords(int[][] newCoords) { yCoords = newCoords; }

    /**
     * Sets boolean value for isHighlighted[i][j] for single index into the 2D array, isHighlighted
     * @param indices The values i (indices[0]) and j (indices[1])
     * @param value The boolean value isHighlighted[i][j] will be set to
     */
    public void setIsHighlighted(int[] indices, boolean value) {
        int i = indices[0];
        int j = indices[1];

        isHighlighted[i][j] = value;
    }

    /**
     * Sets color for control points and lattice when unselected
     * @param color The color they will be set to
     */
    public void setPrimaryCPColor(Color color) { primaryCPColor = color; }

    /**
     * Sets color for control points and lattice when they are highlighted
     * @param color The color they will be set to when highlighted
     */
    public void setHighlightedCPColor(Color color) { highlightedCPColor = color; }

    /**
     * Sets the primary color for the lattice
     * @param color The color the lattice lines will be when not selected
     */
    public void setPrimaryLColor(Color color) { primaryLColor = color; }

    /**
     * Sets the highlight color for the lattice lines.
     * @param color The color the lattice lines will be when selected.
     */
    public void setHighlightedLColor(Color color) { highlightedLColor = color; }

    /**
     * Used to get the width of the panel after adjusting for a new image; called before
     * actually setting the image so that the ImageMorphWindow can be resized before setting the image.
     * Setting the image and then resizing the ImageMorphWindow results in image being cut off.
     * @param image The image that will be set.
     * @return The width that will be set for the image.
     */
    public int getAdjustedWidth(BufferedImage image) {
        int preScaledW = image.getWidth();
        int preScaledH = image.getHeight();

        int newWidth;

        if ((preScaledW * preScaledH) > (600 * 400)) {
            double ratio = (double) preScaledW / (double) preScaledH;

            newWidth = (int) Math.sqrt(ratio * 600 * 400);
        } else {
            newWidth = preScaledW;
        }

        return newWidth;
    }

    /**
     * Used to get the height of the panel after adjusting for a new image; called before
     * actually setting the image so that the ImageMorphWindow can be resized before setting the image.
     * Setting the image and then resizing the ImageMorphWindow results in image being cut off.
     * @param image The image that will be set.
     * @return The height that will be set for the image.
     */
    public int getAdjustedHeight(BufferedImage image) {
        int preScaledW = image.getWidth();
        int preScaledH = image.getHeight();

        int newWidth;
        int newHeight;

        if ((preScaledW * preScaledH) > (600 * 400)) {
            double ratio = (double) preScaledW / (double) preScaledH;

            newWidth = (int) Math.sqrt(ratio * 600 * 400);
            newHeight = (600 * 400) / newWidth;
        } else {
            newHeight = preScaledH;
        }

        return newHeight;
    }

    /**
     * Gets the indices of a clicked controlPoint
     * @param click The location clicked
     * @return An int[2] consisting of the indices of the controlPoint clicked or null if outside a control point
     */
    public int[] getControlPoint(Point click) {
        for (int i = 0; i < numberControlPoints; i++) {
            for (int j = 0; j < numberControlPoints; j++) {
                if (controlPoints[i][j].contains(click))
                    return new int[]{i, j};
            }
        }
        return null;
    }

    /**
     * Initializes the lattice.
     */
    public void initializeLattice() {
        // initialize isHighlighted to false for all values
        isHighlighted = new boolean[numberControlPoints][numberControlPoints];
        for (int i = 0; i < numberControlPoints; i++) {
            for (int j = 0; j < numberControlPoints; j++) {
                isHighlighted[i][j] = false;
            }
        }

        // initialize triangle arrays
        upperTriangles = new Polygon[numberControlPoints+1][numberControlPoints+1];
        lowerTriangles = new Polygon[numberControlPoints+1][numberControlPoints+1];

        // initialize coordinates for control points
        initializeControlCoords();
    }

    /**
     * Checks if a point falls within the triangles surrounding the selected control point.
     * @param mouse The point to check.
     * @param indices The indices of the selected control point.
     * @return boolean indicating whether the mouse falls within one of the surrounding triangles.
     */
    public boolean inSurroundingTriangles(Point mouse, int[] indices) {
        int i = indices[0];
        int j = indices[1];

        // check all the surrounding triangles
        return (lowerTriangles[i][j].contains(mouse) || lowerTriangles[i][j+1].contains(mouse)
                || lowerTriangles[i+1][j].contains(mouse) || upperTriangles[i][j+1].contains(mouse)
                || upperTriangles[i+1][j+1].contains(mouse) || upperTriangles[i+1][j].contains(mouse));
    }

    /**
     * Overrides the paintComponent method to allow for adding image and drawing
     * control points and lattice.
     * @param g The graphics context
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // paint the image if there is one
        if (image != null) {
            g.drawImage(image, 0, 0, this);
        }

        // paint the lattice if it is visible
        drawLowerTriangles(g2);
        drawUpperTriangles(g2);

        // update the control points and paint them if they're visible
        updateControlPoints();
        if (showControlPoints)
            drawControlPoints(g2);
    }

    /**
     * Draws the control points
     * @param g2 The graphics context
     */
    private void drawControlPoints(Graphics2D g2) {
        for (int i = 0; i < numberControlPoints; i++) {
            for (int j = 0; j < numberControlPoints; j++) {

                if (!isHighlighted[i][j])
                    g2.setColor(primaryCPColor);
                else
                    g2.setColor(highlightedCPColor);

                g2.drawPolygon(controlPoints[i][j]);
                g2.fillPolygon(controlPoints[i][j]);
            }
        }
    }

    /**
     * Updates the control points positions (needed in case they aren't visible so they can be updated without drawing)
     */
    public void updateControlPoints() {
        for (int i = 0; i < numberControlPoints; i++) {
            for (int j = 0; j < numberControlPoints; j++) {
                int currX = xCoords[i][j];
                int currY = yCoords[i][j];

                int[] controlPointXCoords = {currX, currX+5, currX, currX-5};
                int[] controlPointYCoords = {currY-5, currY, currY+5, currY};

                controlPoints[i][j] = new Polygon(controlPointXCoords, controlPointYCoords, 4);
            }
        }
    }

    /**
     * Draws lower triangles in lattice. Need to have references to both upper and lower triangles to check positions.
     */
    private void drawLowerTriangles(Graphics2D g2) {
        for (int i = 0; i < numberControlPoints+1; i++) {
            for (int j = 0; j < numberControlPoints+1; j++) {
                // coordinates for the triangle
                int[] triangleXCoords;
                int[] triangleYCoords;

                // coordinates for each corner of the triangle
                int aX, aY, bX, bY, cX, cY;

                g2.setColor(primaryLColor);

                // set the coordinates of first corner
                if (i == 0 && j == numberControlPoints) {
                    aX = width;
                    aY = 0;
                } else if (i == 0) {
                    aX = (int) widthOffset * (j + 1);
                    aY = 0;
                } else if (j == numberControlPoints) {
                    aX = width;
                    aY = (int) heightOffset * i;
                } else {
                    aX = xCoords[i-1][j];
                    aY = yCoords[i-1][j];
                }

                // set coordinates of second corner
                if (i == numberControlPoints && j == numberControlPoints) {
                    bX = width;
                    bY = height;
                } else if (j == numberControlPoints) {
                    bX = width;
                    bY = (int) heightOffset * (i+1);
                } else if (i == numberControlPoints) {
                    bX = (int) widthOffset * (j+1);
                    bY = height;
                } else {
                    bX = xCoords[i][j];
                    bY = yCoords[i][j];
                }

                // set coordinates of third corner
                if (j == 0 && i == numberControlPoints) {
                    cX = 0;
                    cY = height;
                } else if (j == 0) {
                    cX = 0;
                    cY = (int) heightOffset * (i+1);
                } else if (i == numberControlPoints) {
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
                if (showLattice)
                    g2.drawPolygon(lowerTriangles[i][j]);
            }
        }
    }

    /**
     * Draws upper triangles in lattice
     */
    private void drawUpperTriangles(Graphics2D g2) {
        for (int i = 0; i < numberControlPoints+1; i++) {
            for (int j = 0; j < numberControlPoints+1; j++) {
                // coordinates for the triangle
                int[] triangleXCoords;
                int[] triangleYCoords;

                // coordinates for each corner of the triangle
                int aX, aY, bX, bY, cX, cY;

                g2.setColor(primaryLColor);

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

                    if (isHighlighted[i-1][j-1])
                        g2.setColor(highlightedLColor);
                }

                // get coordinates for second corner
                if (i == 0 && j == numberControlPoints) {
                    bX = width;
                    bY = 0;
                } else if (j == numberControlPoints) {
                    bX = width;
                    bY = (int) heightOffset * i;
                } else if (i == 0) {
                    bX = (int) widthOffset * (j+1);
                    bY = 0;
                } else {
                    bX = xCoords[i-1][j];
                    bY = yCoords[i-1][j];

                    if (isHighlighted[i-1][j])
                        g2.setColor(highlightedLColor);
                }

                // get coordinates for third corner
                if (j == 0 && i == numberControlPoints) {
                    cX = 0;
                    cY = height;
                } else if (j == 0) {
                    cX = 0;
                    cY = (int) heightOffset * (i+1);
                } else if (i == numberControlPoints) {
                    cX = (int) widthOffset * j;
                    cY = height;
                } else {
                    cX = xCoords[i][j-1];
                    cY = yCoords[i][j-1];

                    if (isHighlighted[i][j-1])
                        g2.setColor(highlightedLColor);
                }

                // initialize coordinate arrays for this triangle
                triangleXCoords = new int[]{aX, bX, cX};
                triangleYCoords = new int[]{aY, bY, cY};

                // create the triangle and draw it if it is visible
                upperTriangles[i][j] = new Polygon(triangleXCoords, triangleYCoords, 3);

                if (showLattice)
                    g2.drawPolygon(upperTriangles[i][j]);
            }
        }
    }

    /**
     * Sets initial control point coordinates based on size of panel and number of control points
     */
    private void initializeControlCoords() {
        // current x and y coordinates
        int currX;
        int currY;

        // the vertical and horizontal offsets for the points
        widthOffset = width / (numberControlPoints+1.0);
        heightOffset = height / (numberControlPoints+1.0);

        currY = (int) heightOffset;

        // initialize x and y coordinate arrays and control points array
        xCoords = new int[numberControlPoints][numberControlPoints];
        yCoords = new int[numberControlPoints][numberControlPoints];
        controlPoints = new Polygon[numberControlPoints][numberControlPoints];

        // set all the initial coordinates
        for (int i = 0; i < numberControlPoints; i++) {
            currX = (int) widthOffset;

            for (int j = 0; j < numberControlPoints; j++) {

                xCoords[i][j] = currX;
                yCoords[i][j] = currY;

                currX += widthOffset;
            }
            currY += heightOffset;
        }
    }
}
