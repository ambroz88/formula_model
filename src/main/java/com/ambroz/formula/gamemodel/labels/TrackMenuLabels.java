package com.ambroz.formula.gamemodel.labels;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ambroz.formula.gamemodel.utils.FileIO;

/**
 *
 * @author Jiri Ambroz
 */
public class TrackMenuLabels {

    public final static String TITLE = "title";
    public final static String BUILD_LEFT = "buildLeft";
    public final static String BUILD_RIGHT = "buildRight";
    public final static String EDIT = "edit";
    public final static String DELETE_LAST = "deleteLast";
    public final static String REVERSE = "reverse";
    public final static String NEW_TRACK = "newTrack";
    public final static String SAVE = "save";

    private Properties properties;

    public TrackMenuLabels(String language) {
        String fileName = language + "/TrackMenu.properties";
        try {
            this.properties = FileIO.loadProperties(fileName);
        } catch (IOException ex) {
            try {
                this.properties = FileIO.loadProperties(this.getClass().getClassLoader().getResource(fileName).getFile());
            } catch (IOException ex1) {
                Logger.getLogger(DialogLabels.class.getName()).log(Level.SEVERE, null, ex);
                this.properties = new Properties();
            }
        }
    }

    public String getValue(String propertyName) {
        Object label = properties.get(propertyName);
        if (label != null) {
            return label.toString();
        } else {
            return "CORRECT LABEL NOT FOUND.";
        }
    }
}
