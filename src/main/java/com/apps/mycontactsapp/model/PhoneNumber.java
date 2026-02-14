package com.apps.mycontactsapp.model;

/**
 * Value object representing a phone number with a label (e.g., "Mobile",
 * "Work").
 */
public class PhoneNumber {
    private final String label;
    private final String number;

    /**
     * Constructs a new PhoneNumber.
     * 
     * @param label  the label (e.g. "Mobile").
     * @param number the phone number string.
     */
    public PhoneNumber(String label, String number) {
        this.label = label;
        this.number = number;
    }

    /**
     * Gets the label.
     * 
     * @return the label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Gets the phone number.
     * 
     * @return the phone number.
     */
    public String getNumber() {
        return number;
    }

    /**
     * Returns a string representation of the PhoneNumber.
     * 
     * @return the string representation (label: number).
     */
    @Override
    public String toString() {
        return label + ": " + number;
    }
}
