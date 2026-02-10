package com.apps.mycontactsapp.model;

import com.apps.mycontactsapp.exceptions.ValidationException;

/**
 * Represents an Organization contact.
 */
public class Organization extends Contact {
    private String website;
    private String department;

    private Organization(OrganizationBuilder builder) {
        super(builder);
        this.website = builder.website;
        this.department = builder.department;
    }

    // Copy Constructor
    private Organization(Organization source) {
        super(source);
        this.website = source.website;
        this.department = source.department;
    }

    @Override
    public Contact copy() {
        return new Organization(this);
    }

    @Override
    protected void updateStateFrom(Contact source) {
        super.updateStateFrom(source);
        if (source instanceof Organization) {
            Organization o = (Organization) source;
            this.website = o.website;
            this.department = o.department;
        }
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) throws ValidationException {
        // Optional validation logic here
        this.website = website;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) throws ValidationException {
        // Optional validation logic here
        this.department = department;
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
