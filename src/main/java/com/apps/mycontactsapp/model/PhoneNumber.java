package com.apps.mycontactsapp.model;

/**
 * Value object representing a phone number with a label (e.g., "Mobile",
 * "Work").
 */
public class PhoneNumber {
    private final String label;
    private final String number;

    public PhoneNumber(String label, String number) {
        this.label = label;
        this.number = number;
    }

    public String getLabel() {
        return label;
    }

    public String getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return label + ": " + number;
    }
}
