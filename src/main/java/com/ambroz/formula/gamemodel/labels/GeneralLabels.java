package com.ambroz.formula.gamemodel.labels;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ambroz.formula.gamemodel.utils.FileIO;

/**
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public class GeneralLabels {

    public final static String BUILD_TRACK = "build";
    public final static String PLAY_GAME = "play";
    public final static String OPTIONS = "options";
    public final static String TRACK_TITLE = "trackTitle";

    private Properties properties;

    public GeneralLabels(String language) {
        String fileName = language + "/General.properties";
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
