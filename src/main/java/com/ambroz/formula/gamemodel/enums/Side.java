package com.ambroz.formula.gamemodel.enums;

/**
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public enum Side {

    Left, Right;

    public int getValue() {
        int value;

        if (this.equals(Left)) {
            value = 1;
        } else {
            value = -1;
        }

        return value;
    }

    public Side getOppositeSide() {
        if (this.equals(Left)) {
            return Right;
        } else {
            return Left;
        }
    }
}
