package com.ambroz.formula.gamemodel.enums;

import com.ambroz.formula.gamemodel.labels.PrepareGameLabels;

/**
 *
 * @author Jiri Ambroz
 */
public enum FormulaType {

    Player(PrepareGameLabels.PLAYER),
    ComputerEasy(PrepareGameLabels.COMPUTER_EASY),
    ComputerMedium(PrepareGameLabels.COMPUTER_MEDIUM),
    None(PrepareGameLabels.NONE);

    private final String type;

    FormulaType(String formulaType) {
        type = formulaType;
    }

    public static FormulaType getType(int formuleType) {
        FormulaType formula;
        if (formuleType == 1) {
            formula = Player;
        } else if (formuleType == 2) {
            formula = ComputerEasy;
        } else if (formuleType == 3) {
            formula = ComputerMedium;
        } else {
            formula = None;
        }
        return formula;
    }

    @Override
    public String toString() {
        return type;
    }

}
