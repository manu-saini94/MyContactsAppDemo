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

    public ContactSortOrder getDefaultSortOrder() {
        return defaultSortOrder;
    }

    public void setDefaultSortOrder(ContactSortOrder defaultSortOrder) {
        this.defaultSortOrder = defaultSortOrder;
    }

    public ContactViewType getViewType() {
        return viewType;
    }

    public void setViewType(ContactViewType viewType) {
        this.viewType = viewType;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public int getContactsPerPage() {
        return contactsPerPage;
    }

    public void setContactsPerPage(int contactsPerPage) {
        this.contactsPerPage = contactsPerPage;
    }

    public boolean isShowContactPhotos() {
        return showContactPhotos;
    }

    public void setShowContactPhotos(boolean showContactPhotos) {
        this.showContactPhotos = showContactPhotos;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

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
