package com.ambroz.formula.gamemodel.enums;

/**
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public enum PointPosition {

    CollisionLeft("leftCollision"),
    CollisionRight("rightCollision"),
    FinishLine("finishLine"),
    Finish("finish"),
    Outside("outside"),
    Inside("inside"),
    Edge("insideoutsideLine"),
    Normal("normal");

    private final String position;

    private PointPosition(String position) {
        this.position = position;
    }

    public boolean equals(PointPosition comparedPosition) {
        return position.equals(comparedPosition.toString());
    }

    public boolean contains(PointPosition comparedPosition) {
        return position.contains(comparedPosition.toString());
    }

    @Override
    public String toString() {
        return position;
    }

}
