package com.ambroz.formula.gamemodel.datamodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public class PropertyChanger {

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
