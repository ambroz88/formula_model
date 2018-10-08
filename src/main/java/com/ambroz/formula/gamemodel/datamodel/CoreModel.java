package com.ambroz.formula.gamemodel.datamodel;

import com.ambroz.formula.gamemodel.enums.Language;
import com.ambroz.formula.gamemodel.labels.HintLabels;
import com.ambroz.formula.gamemodel.track.Track;
import com.ambroz.formula.gamemodel.utils.Calc;

/**
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public class CoreModel extends PropertyChanger {

    protected HintLabels hintLabels;
    private final Paper paper;
    private Track track;
    private Language language;
    private int stage;

    public CoreModel(Paper gamePaper) {
        this.paper = gamePaper;
        track = new Track();
        language = Language.English;
        hintLabels = new HintLabels(language.toString());
    }

    public void repaintScene() {
        //cought by drawing components
        firePropertyChange(REPAINT, false, true);
    }

    public void fireHint(String hintLabelProperty) {
        //cought by HintComponent
        firePropertyChange(HINT, null, hintLabels.getValue(hintLabelProperty));
    }

    public void fireCoordinates(String coordinates) {
        //cought by CoordinatesPanel
        firePropertyChange(MOUSE_MOVING, null, coordinates);
    }

    public Language getLanguage() {
        return language;
    }

    public final void setLanguage(Language language) {
        String old = getLanguage().toString();
        this.language = language;
        hintLabels = new HintLabels(language.toString());
        firePropertyChange(LANGUAGE, old, language.toString());
    }

    public int getStage() {
        return stage;
    }

    public final void setStage(int stage) {
        this.stage = stage;
    }

    public Track getTrack() {
        return track;
    }

    public final void setTrack(Track track) {
        this.track = track;
        getPaper().setWidth(Calc.ceilingOnPlaceValue(track.getMaxWidth(), 10));
        getPaper().setHeight(Calc.ceilingOnPlaceValue(track.getMaxHeight(), 10));
    }

    public Paper getPaper() {
        return paper;
    }

    public HintLabels getHintLabels() {
        return hintLabels;
    }

}
