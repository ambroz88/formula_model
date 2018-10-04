package com.ambroz.formula.gamemodel.enums;

/**
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public enum Language {

    Czech("CZ"),
    English("EN");

    private final String language;

    private Language(String languageCode) {
        this.language = languageCode;
    }

    @Override
    public String toString() {
        return language;
    }

}
