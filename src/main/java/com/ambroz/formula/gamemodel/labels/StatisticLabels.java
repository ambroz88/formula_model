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
public class StatisticLabels {

    public final static String MOVES = "moves";
    public final static String DISTANCE = "distance";
    public final static String WAIT = "wait";

    private Properties properties;

    public StatisticLabels(String language) {
        String fileName = language + "/StatPanel.properties";
        try {
            this.properties = FileIO.loadProperties(fileName);
        } catch (IOException ex) {
            try {
                this.properties = FileIO.loadProperties(this.getClass().getClassLoader().getResource(fileName).getFile());
            } catch (IOException ex1) {
                Logger.getLogger(StatisticLabels.class.getName()).log(Level.SEVERE, null, ex1);
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
