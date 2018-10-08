package com.ambroz.formula.gamemodel.datamodel;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public class Paper extends PropertyChanger {

    public static final int DIMENSION = 300;

    private List<Segment> horizontal;
    private List<Segment> vertical;
    private int gridSize;
    private int width;
    private int height;

    /**
     * It calculates two polylines which compose square paper.
     */
    public Paper() {
        this.width = DIMENSION;
        this.height = DIMENSION;
        gridSize = 15;
        updateGrid();
    }

    /**
     * It calculates two polylines which compose square paper.
     */
    private void updateGrid() {
        vertical = new ArrayList<>();
        horizontal = new ArrayList<>();
        //create vertical lines of square paper
        for (int x = 0; x <= getWidth(); x++) {
            vertical.add(new Segment(new Point(x, 0), new Point(x, getHeight())));
        }
        //create horizontal lines of square paper
        for (int y = 0; y <= getHeight(); y++) {
            horizontal.add(new Segment(new Point(0, y), new Point(getWidth(), y)));
        }
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int paperWidth) {
        int old = getWidth();
        this.width = paperWidth;
        updateGrid();
        firePropertyChange(PropertyChanger.PAPER_WIDTH, old, getWidth()); //cought by Draw and Options
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int paperHeight) {
        int old = getHeight();
        this.height = paperHeight;
        updateGrid();
        firePropertyChange(PropertyChanger.PAPER_HEIGHT, old, height); //cought by Draw and Options
    }

    public int getGridSize() {
        return gridSize;
    }

    /**
     * Setter for size of square edge on the "paper".
     *
     * @param size is new grid size
     */
    public void setGridSize(int size) {
        int old = getGridSize();
        gridSize = size;
        firePropertyChange(PropertyChanger.PAPER_GRID, old, getGridSize()); //cought by Draw
    }

    /**
     * It checks whether the input point is inside this paper.
     *
     * @param click point with square coordinates
     * @return true if point is inside, false otherwise
     */
    public boolean isOutside(Point click) {
        return click.x > getWidth() || click.y > getHeight() || click.x < 0 || click.y < 0;
    }

    /**
     * It returns number of points, which are outside of the visible part of drawing window.
     *
     * @param data is set of points that will be tested
     * @return number of points outside of paper
     */
    public int outPaperNumber(List<Point> data) {
        int outBorder = 0;

        for (int i = 0; i < data.size(); i++) {
            if (isOutside(data.get(i))) {
                outBorder++;
            }
        }
        return outBorder;
    }

    public List<Segment> getHorizontalLines() {
        return horizontal;
    }

    public List<Segment> getVerticalLines() {
        return vertical;
    }

}
