package com.apps.mycontactsapp.model;

/**
 * Class representing the user's profile preferences.
 * Encapsulates settings for sorting, viewing, and other display options.
 */
public class ProfilePreferences {

    private ContactSortOrder defaultSortOrder;
    private ContactViewType viewType;
    private boolean notificationsEnabled;
    private int contactsPerPage;
    private boolean showContactPhotos;
    private String preferredLanguage;

    /**
     * Default constructor initializing preferences with default values.
     */
    public ProfilePreferences() {
        this.defaultSortOrder = ContactSortOrder.NAME_ASC;
        this.viewType = ContactViewType.LIST;
        this.notificationsEnabled = true;
        this.contactsPerPage = 10;
        this.showContactPhotos = true;
        this.preferredLanguage = "English";
    }

    /**
     * Parameterized constructor for custom preferences.
     *
     * @param defaultSortOrder     the default sort order
     * @param viewType             the view type (list/grid)
     * @param notificationsEnabled whether notifications are enabled
     * @param contactsPerPage      number of contacts per page
     * @param showContactPhotos    whether to show contact photos
     * @param preferredLanguage    the preferred language
     */
    public ProfilePreferences(ContactSortOrder defaultSortOrder, ContactViewType viewType, boolean notificationsEnabled,
            int contactsPerPage, boolean showContactPhotos, String preferredLanguage) {
        this.defaultSortOrder = defaultSortOrder;
        this.viewType = viewType;
        this.notificationsEnabled = notificationsEnabled;
        this.contactsPerPage = contactsPerPage;
        this.showContactPhotos = showContactPhotos;
        this.preferredLanguage = preferredLanguage;
    }

    // Getters and Setters

    /**
     * Gets the default sort order.
     * 
     * @return the sort order.
     */
    public ContactSortOrder getDefaultSortOrder() {
        return defaultSortOrder;
    }

    /**
     * Sets the default sort order.
     * 
     * @param defaultSortOrder the new sort order.
     */
    public void setDefaultSortOrder(ContactSortOrder defaultSortOrder) {
        this.defaultSortOrder = defaultSortOrder;
    }

    /**
     * Gets the view type.
     * 
     * @return the view type (LIST/GRID).
     */
    public ContactViewType getViewType() {
        return viewType;
    }

    /**
     * Sets the view type.
     * 
     * @param viewType the new view type.
     */
    public void setViewType(ContactViewType viewType) {
        this.viewType = viewType;
    }

    /**
     * Checks if notifications are enabled.
     * 
     * @return true if enabled, false otherwise.
     */
    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    /**
     * Sets the notification preference.
     * 
     * @param notificationsEnabled true to enable, false to disable.
     */
    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    /**
     * Gets the number of contacts to show per page.
     * 
     * @return the count.
     */
    public int getContactsPerPage() {
        return contactsPerPage;
    }

    /**
     * Sets the number of contacts per page.
     * 
     * @param contactsPerPage the count.
     */
    public void setContactsPerPage(int contactsPerPage) {
        this.contactsPerPage = contactsPerPage;
    }

    /**
     * Checks if contact photos should be shown.
     * 
     * @return true if shown, false otherwise.
     */
    public boolean isShowContactPhotos() {
        return showContactPhotos;
    }

    /**
     * Sets whether to show contact photos.
     * 
     * @param showContactPhotos true to show, false to hide.
     */
    public void setShowContactPhotos(boolean showContactPhotos) {
        this.showContactPhotos = showContactPhotos;
    }

    /**
     * Gets the preferred language.
     * 
     * @return the language code or name.
     */
    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    /**
     * Sets the preferred language.
     * 
     * @param preferredLanguage the language code or name.
     */
    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    /**
     * Returns a string representation of the preferences.
     * 
     * @return the string representation.
     */
    @Override
    public String toString() {
        return "ProfilePreferences{" +
                "defaultSortOrder=" + defaultSortOrder +
                ", viewType=" + viewType +
                ", notificationsEnabled=" + notificationsEnabled +
                ", contactsPerPage=" + contactsPerPage +
                ", showContactPhotos=" + showContactPhotos +
                ", preferredLanguage='" + preferredLanguage + '\'' +
                '}';
    }
}
