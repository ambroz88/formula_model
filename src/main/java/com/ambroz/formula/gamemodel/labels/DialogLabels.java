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
public final class DialogLabels {

    public final static String ATTENTION = "attention";
    public final static String SAVE_LABEL = "saveLabel";
    public final static String CONDITION = "condition";
    public final static String EVENT = "action";
    public final static String YES = "yes";
    public final static String NO = "no";

    private Properties properties;

    public DialogLabels(String language) {
        String fileName = language + "/Dialog.properties";
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
