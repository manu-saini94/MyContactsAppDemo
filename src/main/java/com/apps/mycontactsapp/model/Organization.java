package com.apps.mycontactsapp.model;

import com.apps.mycontactsapp.exceptions.ValidationException;

/**
 * Represents an Organization contact.
 */
public class Organization extends Contact {
    private final String website;
    private final String department;

    private Organization(OrganizationBuilder builder) {
        super(builder);
        this.website = builder.website;
        this.department = builder.department;
    }

    public String getWebsite() {
        return website;
    }

    public String getDepartment() {
        return department;
    }

    @Override
    public String getDisplayName() {
        return getName() + (department != null ? " (" + department + ")" : "");
    }

    public static class OrganizationBuilder extends Contact.ContactBuilder<OrganizationBuilder, Organization> {
        private String website;
        private String department;

        public OrganizationBuilder website(String website) {
            this.website = website;
            return this;
        }

        public OrganizationBuilder department(String department) {
            this.department = department;
            return this;
        }

        @Override
        protected OrganizationBuilder self() {
            return this;
        }

        @Override
        public Organization build() throws ValidationException {
            validate(); // Base validation checks 'name'
            return new Organization(this);
        }
    }
}
