package com.ambroz.formula.gamemodel.datamodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public class PropertyChanger {

    public static final String HINT = "hint";
    public static final String REPAINT = "repaint";
    public static final String LANGUAGE = "language";
    public static final String MOUSE_MOVING = "mouseMoving";

    public static final String TRACK_SAVED = "newTrack";
    public static final String TRACK_READY = "trackReady";

    public static final String PAPER_GRID = "grid";
    public static final String PAPER_WIDTH = "paperWidth";
    public static final String PAPER_HEIGHT = "paperHeight";

    public static final String FORMULA_NAME = "name";
    public static final String FORMULA_COLOUR = "color";
    public static final String FORMULA_DISTANCE = "dist";
    public static final String FORMULA_MOVES = "move";
    public static final String FORMULA_WAIT = "stop";
    public static final String FORMULA_RESET = "reset";

    public static final String RACE_LOAD_TRACK = "loadTrack";
    public static final String RACE_CRASH = "crash";
    public static final String RACE_WINNER = "winner";
    public static final String RACE_NEW_GAME = "newGame";

    private final PropertyChangeSupport prop;

    public PropertyChanger() {
        prop = new PropertyChangeSupport(this);
    }

    public void firePropertyChange(String prop, Object oldValue, Object newValue) {
        this.prop.firePropertyChange(prop, oldValue, newValue);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        prop.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        prop.removePropertyChangeListener(listener);
    }

}
